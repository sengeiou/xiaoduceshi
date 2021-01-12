package ai.qiwu.com.xiaoduhome.controller;

import ai.qiwu.com.xiaoduhome.common.TokenHolder;
import ai.qiwu.com.xiaoduhome.common.Utils;
import ai.qiwu.com.xiaoduhome.pojo.TokenDuerosResponse;
import ai.qiwu.com.xiaoduhome.pojo.TokenResponse;
import ai.qiwu.com.xiaoduhome.pojo.User;
import ai.qiwu.com.xiaoduhome.service.LoadBySMS;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 百度的第三方登录基于OAuth2.0的授权码模式，由于郊游天下平台还没有实现该种授权模式给第三方,
 * 所以这里便根据OAuth2协议的内容来实现，从而满足Dueros的要求
 * 切忌：验证方法enableVerify()一定要开启,否则回调地址会拒绝你
 * @author 苗权威
 * @dateTime 19-7-18 下午5:55
 */
@Controller
@RequestMapping(value = "/xiaoduceshi/security")
@Slf4j
public class Oauth2Controller {
    private final LoadBySMS loadBySMS;

    private final StringRedisTemplate stringRedisTemplate;

    @Autowired
    public Oauth2Controller(LoadBySMS loadBySMS, StringRedisTemplate stringRedisTemplate) {
        this.loadBySMS = loadBySMS;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * /xiaodu/security/load:这是小度授权信息配置里的“授权地址”,也就是登录卡片点击后跳转到的登录页面地址
     * @param state 状态，例如在测试时传来的值是“debug”,在回调时该值若有需要原样返回
     * @param scope 用户权限范围
     * @param response_type  code
     * @param redirect_uri 回调地址
     * @param client_id 我们设置的Client_id
     * @param modelMap 用于传值
     * @param request 测试时使用来查看请求头信息的
     * @return 返回结果页
     */
    @GetMapping("/load")
    public String load(@RequestParam(required = false) String state, @RequestParam(required = false) String scope,
                       @RequestParam String response_type, @RequestParam String redirect_uri,
                       @RequestParam String client_id, ModelMap modelMap, HttpServletRequest request) {
        //Dueros发来的登录请求信息
        //HttpRequestUtils.getLoadPageInfo(request);
        boolean verifyResult = Utils.verifyLoadRequest(client_id, redirect_uri);
        if (!verifyResult) {
            log.error("该登录请求不合规:client_id:{},redirect_uri:{}",client_id, redirect_uri);
            return "LoadFailed";
        }
        User user = new User();
        user.setState(state);
        modelMap.put("userInfo", user);
        return "Oauth2_load_temp2";
    }

    @GetMapping("/home")
    public String homeLoad(@RequestParam(required = false) String state, @RequestParam(required = false) String scope,
                       @RequestParam String response_type, @RequestParam String redirect_uri,
                       @RequestParam String client_id, ModelMap modelMap, HttpServletRequest request) {
        //Dueros发来的登录请求信息
        //HttpRequestUtils.getLoadPageInfo(request);
        boolean verifyResult = Utils.verifyLoadRequest(client_id, redirect_uri);
        if (!verifyResult) {
            log.error("该登录请求不合规:client_id:{},redirect_uri:{}",client_id, redirect_uri);
            return "LoadFailed";
        }
        User user = new User();
        user.setState(state);
        modelMap.put("userInfo", user);
        return "home_load";
    }

    /**
     * 用户点击获取短信验证码
     * @param phoneNumber 电话号码
     */
    @RequestMapping("/captcha")
    public @ResponseBody
    void getCaptcha(@RequestParam String phoneNumber) {
        // 校验手机号
//        log.info("电话号码:"+phoneNumber);
        try {
            loadBySMS.requireCaptcha(phoneNumber);
        } catch (Exception e) {
            log.error("获取验证码出错:{}", e.toString());
        }
    }

    /**
     * 用户点击登录后调用该方法,利用电话号码与短信验证码从郊游天下平台获取AccessToken与RefreshToken,
     * 之后Oauth2_load页会重定向到回调地址,回调地址在收到后会请求我们设置的Token地址,
     * 即/xiaodu/security/accessToken
     * @param phoneNumber 电话号码
     * @param captcha 短信验证码
     * @return 1代表从郊游天下平台获取验证码成功
     */
    @RequestMapping("/token")
    public @ResponseBody String getAccessToken(@RequestParam String phoneNumber, @RequestParam String captcha) {
//        log.info("电话号码:"+phoneNumber+",captcha:"+captcha);
        if (StringUtils.isNotEmpty(captcha)) {
            // 获取token
            boolean result;
            try {
                result = loadBySMS.requireCodeToken(phoneNumber, captcha);
            } catch (IOException e) {
                log.error("获取Token出错:{}", e.toString());
                result = false;
            }
//            log.info("请求郊游天下Token返回结果:"+result);
            if (result) {
                return "1";
            }else {
                log.warn("请求郊游天下Token失败");
                return "0";
            }
        }
        else {
            return "0";
        }
    }

    /**
     * 回调地址会主动请求该地址,我们返回给他Token
     * @param grant_type authorization_code
     * @param code 授权码
     * @param client_id 我们设置的Client_id
     * @param client_secret 我们设置的Client_secret
     * @param redirect_uri 回调地址
     * @return 按规定我们需要返回的信息格式如下：
     * {
     *     "access_token": "ACCESS_TOKEN",
     *     "expires_in": 1234,
     *     "refresh_token":"REFRESH_TOKEN"
     * }
     */
    @RequestMapping("/accessToken")
    public @ResponseBody TokenDuerosResponse returnTokenToDueros(@RequestParam String grant_type,
                                            @RequestParam(required = false) String code, @RequestParam String client_id,
                                            @RequestParam String client_secret, @RequestParam String redirect_uri,
                                                                 @RequestParam(required = false) String refresh_token) {
        log.info("小度请求Token,client_id:{};client_secret:{};redirect_uri:{};code:{};refreshToken:{}",
                client_id, client_secret, redirect_uri, code, refresh_token);
        // 请求会出现发来的消息每个值重复一遍的情况，待解决？
        client_id = client_id.substring(client_id.lastIndexOf(",")+1);
        client_secret = client_secret.substring(client_secret.lastIndexOf(",")+1);
        redirect_uri = redirect_uri.substring(redirect_uri.lastIndexOf(",")+1);
        boolean verifyResult = Utils.verifyTokenRequest(client_id, client_secret, redirect_uri);
        if (!verifyResult) {
            log.error("不合规的Token请求");
            return null;
        }
        if (code == null && refresh_token != null) {
            refresh_token = refresh_token.substring(refresh_token.lastIndexOf(",")+1);
            TokenResponse tokenResponse = LoadBySMS.getTokenByRefreshToken(refresh_token);
//            log.warn("refreshToken:"+tokenResponse);
            if (tokenResponse == null) {
                return new TokenDuerosResponse();
            }
            TokenDuerosResponse response = new TokenDuerosResponse();
            response.setAccess_token(tokenResponse.getAccessToken());
            response.setRefresh_token(tokenResponse.getRefreshToken());
            response.setExpires_in(tokenResponse.getExpire());
            return response;
        }
        if (code == null) {
            log.error("returnTokenToDueros: code is null");
            return null;
        }
        code = code.substring(code.lastIndexOf(",")+1);
        String holderStr = stringRedisTemplate.opsForValue().get(code);
        stringRedisTemplate.delete(code);
        if (holderStr == null) {
            log.error("returnTokenToDueros: redis hsa no data");
            return null;
        }
        TokenHolder holder = JSON.parseObject(holderStr, TokenHolder.class);
        String accessToken;
        if (holder == null || (accessToken =holder.getAccessToken())==null) {
            log.error("没有对应的令牌:"+holder);
            return null;
        }
        TokenDuerosResponse response = new TokenDuerosResponse();
        response.setAccess_token(accessToken);
        response.setRefresh_token(holder.getRefreshToken());
        response.setExpires_in(holder.getExpire());
        return response;
    }
}
