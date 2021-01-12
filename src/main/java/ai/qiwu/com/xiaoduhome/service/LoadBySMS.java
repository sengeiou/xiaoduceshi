package ai.qiwu.com.xiaoduhome.service;

import ai.qiwu.com.xiaoduhome.common.Constants.XIAOWU_API;
import ai.qiwu.com.xiaoduhome.common.OkHttp3Utils;
import ai.qiwu.com.xiaoduhome.common.TokenHolder;
import ai.qiwu.com.xiaoduhome.common.Utils;
import ai.qiwu.com.xiaoduhome.entity.primary.AppUserTB;
import ai.qiwu.com.xiaoduhome.pojo.CaptchaSmsResponse;
import ai.qiwu.com.xiaoduhome.pojo.TokenResponse;
import ai.qiwu.com.xiaoduhome.repository.primary.AppUserTBRepository;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * 处理验证码，令牌的请求
 * @author 苗权威
 * @dateTime 19-7-17 下午5:53
 */
@Service
@Slf4j
public class LoadBySMS {

//    private static final ConcurrentHashMap<String, TokenHolder> USER_TOKEN = new ConcurrentHashMap<>();

    private final AppUserTBRepository appUserTBRepository;

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 手机号到TOKEN信息之间的映射
     */
//    private static final ConcurrentHashMap<String, TokenHolder> CODE_TOKEN = new ConcurrentHashMap<>();

    @Autowired
    public LoadBySMS(AppUserTBRepository appUserTBRepository, StringRedisTemplate stringRedisTemplate) {
        this.appUserTBRepository = appUserTBRepository;
        this.stringRedisTemplate = stringRedisTemplate;
    }

//    /**
//     * 登录成功后每次请求都会携带AccessToken，这里存储AccessToken到手机号的映射，用于之后令牌过期取RefreshToken使用
//     */
//    private static final ConcurrentHashMap<String, String> ACCESSTOKEN_PHONENUM = new ConcurrentHashMap<>();


    /**
     * 通过电话号码获取短信验证码
     * @param phoneNumber
     * @return 是否成功获取
     * @throws IOException
     */
    public Boolean requireCaptcha(String phoneNumber) throws IOException {
        // 校验手机号
        phoneNumber = phoneNumber.trim();
        if (!Utils.verifyPhoneNumber(phoneNumber)) {
            return false;
        }
        String authorization = Utils.captchaAuthorization(phoneNumber);
        int retCode = getCaptchaRequest(authorization);
        if (retCode != 0) {
            log.info("获取验证码失败");
//            new Thread(new InsertUserInfoToAppUserTB(phoneNumber)).start();
        }
        return retCode == 0;
    }

    class InsertUserInfoToAppUserTB implements Runnable {
        String phone;

        InsertUserInfoToAppUserTB(String phone) {
            this.phone = phone;
        }

        @Override
        public void run() {
            AppUserTB userInfo = appUserTBRepository.getByPhone(phone);
            if (userInfo == null) {
                appUserTBRepository.insertUser(phone);
            }
        }
    }

    // 该方法用于TextCard触发登录以绕过小度的第三方登录模式
//    public Boolean requireToken(String phoneNumber, String captcha, String userId) throws IOException {
//        // 获取token
//        Request request = new Request.Builder().url(XIAOWU_API.SDK_TOKEN)
//                .addHeader("App-Channel-Id", "JIAOYOUTIANXIA")
//                .addHeader("Authorization", tokenAuthorization(phoneNumber, captcha))
//                .addHeader("Content-Type","application/json; charset=utf-8")
//                .get().build();
//        try (Response response = client.newCall(request).execute()){
//            log.info("获取Token返回");
//            TokenResponse tokenResponse = JSON.parseObject(response.body().string(), TokenResponse.class);
//            log.info("获取Token返回信息:{}", tokenResponse);
//            if (tokenResponse.getRetcode() == 0) {
//                updateUserToken(userId, tokenResponse);
//                return true;
//            }else {
//                return false;
//            }
//        }
//    }

    /**
     * Oauth2Controller类调用的方法,根据电话号码与短信验证码向郊游天下请求令牌
     * 我们将令牌信息与电话号码间构成map，用于之后Dueros请求Token时查找
     * @param phoneNumber
     * @param captcha 验证码
     * @return 是否成功从平台获取到令牌信息
     * @throws IOException
     */
    public boolean requireCodeToken(String phoneNumber, String captcha) throws IOException {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("App-Channel-Id", "xiaodu-speaker-api-test");
        headers.put("Content-Type", "application/json;charset=utf-8");
        headers.put("Authorization", Utils.tokenAuthorization(phoneNumber, captcha));
        String tokenStr = OkHttp3Utils.doGet(XIAOWU_API.SDK_TOKEN, new HashMap<>(), headers);
        log.info("获取Token返回:"+tokenStr);
        TokenResponse tokenResponse = JSON.parseObject(tokenStr, TokenResponse.class);
//        log.info("获取Token返回信息:{}", tokenResponse);
        if (tokenResponse.getRetcode() == 0) {
            updateCodeToken(phoneNumber, tokenResponse);
            return true;
        }else {
            return false;
        }
    }

    /**
     * 发起验证码获取请求
     * @param authorization
     * @throws IOException
     */
    private Integer getCaptchaRequest(String authorization) throws IOException {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("App-Channel-Id", "xiaodu-speaker-api-test");
        headers.put("Content-Type", "application/json;charset=utf-8");
        headers.put("Authorization", authorization);
        String smsStr = OkHttp3Utils.doGet(XIAOWU_API.CAPTCHA_SMS, new HashMap<>(), headers);
//      log.info("发送验证码成功");
        CaptchaSmsResponse captchaResponse = JSON.parseObject(smsStr, CaptchaSmsResponse.class);
        log.info("获取验证码返回信息:{}", captchaResponse);
        return captchaResponse.getRetcode();
    }

    //该方法是更新USER_TOKEN的
//    private void updateUserToken(String userId, TokenResponse tokenResponse) {
//        TokenHolder tokenHolder = USER_TOKEN.get(userId);
//        if (tokenHolder == null) {
//            tokenHolder = new TokenHolder();
//        }
//        tokenHolder.setUserId(userId);
//        tokenHolder.setAccessToken(tokenResponse.getAccessToken());
//        tokenHolder.setRefreshToken(tokenResponse.getRefreshToken());
//    }

    /**
     * 更新token缓存信息
     * @param phoneNumber
     * @param tokenResponse
     */
    private void updateCodeToken(String phoneNumber, TokenResponse tokenResponse) {
        String holderStr = stringRedisTemplate.opsForValue().get(phoneNumber);
        TokenHolder tokenHolder;
        if (StringUtils.isBlank(holderStr)) {
            tokenHolder = new TokenHolder();
        } else {
            tokenHolder = JSON.parseObject(holderStr, TokenHolder.class);

        }
        tokenHolder.setExpire(tokenResponse.getExpire());
        tokenHolder.setAccessToken(tokenResponse.getAccessToken());
        tokenHolder.setRefreshToken(tokenResponse.getRefreshToken());
        stringRedisTemplate.opsForValue().set(phoneNumber,JSON.toJSONString(tokenHolder), 30, TimeUnit.SECONDS);
    }

    public static TokenResponse getTokenByRefreshToken(String refreshToken) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("App-Channel-Id", "xiaodu-speaker-api-test");
        headers.put("Content-Type", "application/json;charset=utf-8");
        headers.put("Authorization", XIAOWU_API.BEARER+refreshToken);
        String smsStr = null;
        try {
            smsStr = OkHttp3Utils.doPostJsonStr(XIAOWU_API.REFRESH_TOKEN, "", headers);
        } catch (Exception e) {
            log.error("通过RefreshToken请求Token出错:{}",e.toString());
        }
        if (smsStr == null) return null;
        return JSON.parseObject(smsStr, TokenResponse.class);
    }

//    /**
//     * 获取accesstoken-phonenumber的缓存map
//     * @return
//     */
//    public static ConcurrentHashMap<String, String> getAccesstokenPhonenum() {
//        return ACCESSTOKEN_PHONENUM;
//    }

    public static void main(String[] args) throws IOException {
//        HashMap<String, String> headers = new HashMap<>();
//        headers.put("App-Channel-Id", "JIAOYOUTIANXIA");
//        headers.put("Content-Type", "application/json;charset=utf-8");
//        headers.put("Authorization", Utils.tokenAuthorization("13922841032", "7210"));
//        String tokenStr = OkHttp3Utils.doGet(XIAOWU_API.SDK_TOKEN, new HashMap<>(), headers);
////        log.info("获取Token返回");
//        System.out.println(tokenStr);

        HashMap<String, String> headers = new HashMap<>();
        headers.put("App-Channel-Id", "xiaodu-speaker-api-test");
        headers.put("Content-Type", "application/json;charset=utf-8");
        headers.put("Authorization", XIAOWU_API.BEARER+"dba29f53-41b1-4144-906d-a19f15d25dc8");
        String smsStr = null;
        try {
            smsStr = OkHttp3Utils.doPostJsonStr("https://account-center-test.chewrobot.com/api/sdk/token", "", headers);
            System.out.println(smsStr);
        } catch (Exception e) {
            log.error("通过RefreshToken请求Token出错:{}",e.toString());
        }
    }
}
