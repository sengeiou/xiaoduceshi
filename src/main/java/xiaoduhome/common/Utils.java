package xiaoduhome.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static ai.qiwu.com.xiaoduhome.service.ScheduleServiceNew.LOAD_AUTH_CALLBACK_URL;

/**
 * @author 苗权威
 * @dateTime 19-6-29 下午5:11
 */
@Slf4j
public class Utils {

    /**
     * 校验电话号码
     */
    private static final String REGEX_MOBILE = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0,5-9]))\\d{8}$";

    private static final String REGEX_SEPERATE = "【(.+?)】";

    private static final String[] s1 = { "零", "一", "二", "三", "四", "五", "六", "七", "八", "九" };
//    private static final String[] s2 = { "十", "百", "千"};

    public static HttpServletRequest getHttpServletRequest() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder
                .getRequestAttributes())).getRequest();
    }
    public static String getRegexSeperate() {
        return REGEX_SEPERATE;
    }

    public static String useIPAsItsID() {
        HttpServletRequest request = getHttpServletRequest();
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 验证小度请求登录时发来的信息
     * @param clientId
     * @param redirectUri
     * @return
     */
    public static boolean verifyLoadRequest(String clientId, String redirectUri) {
        return Constants.SKILL_SECURITY.CLIENT_ID.equals(clientId) && LOAD_AUTH_CALLBACK_URL.contains(redirectUri);
//                (Constants.SKILL_SECURITY.REDIRECT_URI_TEST.equals(redirectUri)||
//                        Constants.SKILL_SECURITY.REDIRECT_URI.equals(redirectUri));
    }

    /**
     * 验证小度请求Token时发来的信息
     * @param clientId
     * @param clientSecret
     * @param redirectUri
     * @return
     */
    public static boolean verifyTokenRequest(String clientId, String clientSecret, String redirectUri) {
//        log.info("clientID:{},clientSecret:{},redirectUri:{}", clientId, clientSecret, redirectUri);
        return Constants.SKILL_SECURITY.CLIENT_ID.equals(clientId) && LOAD_AUTH_CALLBACK_URL.contains(redirectUri) &&
                Constants.SKILL_SECURITY.CLIENT_SECRET.equals(clientSecret);
    }

    /**
     * 校验电话号码
     * @param phoneNumber
     * @return
     */
    public static boolean verifyPhoneNumber(String phoneNumber) {
        return phoneNumber.matches(REGEX_MOBILE);
    }

    /**
     * Authorization:Basic+空格+base64加密后的电话号码
     * @param phoneNumber
     * @return
     */
    public static String captchaAuthorization(String phoneNumber) {
        return Constants.XIAOWU_API.BASIC_PREFIX + Base64.encodeBase64String(phoneNumber.getBytes());
    }

    /**
     * Authorization:Basic+空格+base64加密后的(电话号码:验证码)
     * @param phoneNumber
     * @param captcha
     * @return
     */
    public static String tokenAuthorization(String phoneNumber, String captcha) {
        String str = phoneNumber + ":" + captcha;
        str = Base64.encodeBase64String(str.getBytes(StandardCharsets.UTF_8));
        return Constants.XIAOWU_API.BASIC_PREFIX + str;
    }

    public static List<String> getRecommandBotNames(String word) {
        int start = word.indexOf("《");
        int end = word.lastIndexOf("》");
        word = word.substring(start, end+1);
        String[] names = word.split("、");
        List<String> botNames = new ArrayList<>();
        for (String name : names) {
            int i = name.indexOf(":");
            if (i == -1) i = name.length()-1;
            botNames.add(name.substring(1, i));
        }
        return botNames;
    }

    public static Timestamp getCurTime() {
        return new Timestamp(new Date().getTime());
    }

    public static Timestamp getCurTime(long curTimeMills) {
        return new Timestamp(new Date(curTimeMills).getTime());
    }

    /**
     * 获取真实ip
     *
     * @return 真实ip
     */
    public static String getRemoteIpByServletRequest(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (StringUtils.isNotBlank(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
//            log.info("x:"+ip);
            if (ip.contains(",")) {
                ip = ip.split(",")[0];
            }
        }
        if (isIpValid(ip)) {
            return ip;
        }
        ip = request.getHeader("Proxy-Client-IP");
        if (isIpValid(ip)) {
//            log.info("pro:"+ip);
            return ip;
        }
        ip = request.getHeader("WL-Proxy-Client-IP");
        if (isIpValid(ip)) {
//            log.info("wl:"+ip);
            return ip;
        }
        ip = request.getHeader("HTTP_CLIENT_IP");
        if (isIpValid(ip)) {
            return ip;
        }
        ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        if (isIpValid(ip)) {
//            log.info("HTTP:"+ip);
            return ip;
        }
        ip = request.getHeader("X-Real-IP");
        if (isIpValid(ip)) {
//            log.info("x-R:"+ip);
            return ip;
        }
        ip = request.getRemoteAddr();
//        log.info("remote:"+ip);
        return ip;
    }

    /**
     * 判断是否有效
     * @param ip ip
     * @param acceptInnerIp 是否接受内网ip
     * @return
     */
    private static boolean isIpValid(String ip, boolean acceptInnerIp) {
        return acceptInnerIp ? isIpValid(ip) : isIpValidAndNotPrivate(ip);
    }

    /**
     * 仅仅判断ip是否有效
     * @param ip
     * @return
     */
    private static boolean isIpValid(String ip) {
        if (StringUtils.isBlank(ip)) {
            return false;
        }
        String[] split = ip.split("\\.");
        if (split.length != 4) {
            return false;
        }
        try {
            long first = Long.valueOf(split[0]);
            long second = Long.valueOf(split[1]);
            long third = Long.valueOf(split[2]);
            long fourth = Long.valueOf(split[3]);
            return first < 256 && first > 0
                    && second < 256 && second >= 0
                    && third < 256 && third >= 0
                    && fourth < 256 && fourth >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 判断ip是否有效，并且不是内网ip
     * @param ip
     * @return
     */
    private static boolean isIpValidAndNotPrivate(String ip) {
        if (StringUtils.isBlank(ip)) {
            return false;
        }
        String[] split = ip.split("\\.");
        try {
            long first = Long.valueOf(split[0]);
            long second = Long.valueOf(split[1]);
            long third = Long.valueOf(split[2]);
            long fourth = Long.valueOf(split[3]);
            if (first < 256 && first > 0
                    && second < 256 && second >= 0
                    && third < 256 && third >= 0
                    && fourth < 256 && fourth >= 0) {
                if (first == 10) {
                    return false;
                }
                if (first == 172 && (second >= 16 && second <= 31)) {
                    return false;
                }
                if (first == 192 && second == 168) {
                    return false;
                }
                return true;
            }
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static int changeHanToNum(String numStr) {
        List<String> list = Arrays.asList(s1);
        StringBuilder num = new StringBuilder();
        boolean first = true;
        String[] arr = numStr.split("");
        for (String str: arr) {
            int i;
            if (first) {
                first = false;
                if ("十".equals(str)) {
                    if (arr.length == 1) return 10;
                    num.append(1);
                }
                else if ((i = list.indexOf(str)) != -1) num.append(i);
            }
            else if ((i = list.indexOf(str)) != -1) num.append(i);
        }
        return Integer.parseInt(num.toString());
    }

    public static void main(String[] args) throws ParseException {
        String s = "[{\"type\": \"SendEvent\",\"componentId\":\"%s\"}]";
        //System.out.println(String.format(s, "gsagh"));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = format.format(System.currentTimeMillis());
        Timestamp timestamp = Timestamp.valueOf(time);


    }
}
