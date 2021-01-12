package ai.qiwu.com.xiaoduhome.xiaoai.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static ai.qiwu.com.xiaoduhome.service.ScheduleServiceNew.XIAO_AI_ENCRY_HOST;

/**
 * 工具类
 * @author 苗权威
 * @dateTime 19-7-6 上午11:16
 */
@Slf4j
public class Utils {

    /**
     * 代表空格,斜杠,单引号,双引号,tab键
     */
    private static final String STRIP = " /'\"\\t+";

//    public static final String NUM_OR_ALPHA = "[^0-9a-zA-Z]";

    /**
     * 获取HttpServletRequest对象
     * @return HttpServletRequest
     */
    private static HttpServletRequest getHttpServletRequest() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder
                .getRequestAttributes())).getRequest();
    }

    /**
     * 当获取不到用户的id时，采用其ip地址作为替代
     * @return id
     */
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
     * 该方法是用来作为公司测试使用，以区分统一用户id下的不通设备
     * @param deviceId
     * @return
     */
    public static String modifyDeviceId(String deviceId) {
        char[] arr = deviceId.toCharArray();
        StringBuilder builder = new StringBuilder();
        for (char c : arr) {
            if (STRIP.contains(c+"")) continue;
            builder.append(c);
        }
        return builder.toString();
    }

    public static boolean encry(Integer type, HttpServletRequest request) {
//        String host = "didi-gz4.jiaoyou365.com";
        String host = XIAO_AI_ENCRY_HOST;
        String xiaomiDate = request.getHeader("x-xiaomi-date");
        String md5 = request.getHeader("content-md5");
        if (StringUtils.isBlank(md5)) md5 = "";
        String contentType = request.getHeader("content-type");
        String authorization = request.getHeader("authorization");
        if (StringUtils.isAnyBlank(xiaomiDate, contentType, authorization)) return false;
        String secret, urlPath, keyId;
        switch (type) {
            case 1: {
                secret = XiaoAiConstants.SECURITY.SMART_STORY_SECRET;
                keyId = XiaoAiConstants.SECURITY.SMART_STORY_KEYID;
                urlPath = "/xiaoai/child";
                break;
            }
            case 2:{
                secret = XiaoAiConstants.SECURITY.SMART_NOVEL_SECRET;
                keyId = XiaoAiConstants.SECURITY.SMART_NOVEL_KEYID;
                urlPath = "/xiaoai/adult";
                break;
            }
            case 3:{
                secret = "Q+TOpok4Dy/CMCXgHE6HFNrmdImYoJmRQlAGx7LkI54=";
                keyId = "zOQuywFuKjlhbTMr4dHwFA==";
                urlPath = "/xiaoaiTest/story";
                break;
            }
            case 4:{
                secret = "AaBr41l9jrPFir3c0tVdjl+nnwtMC6xcM4b/ua/MspA=";
                keyId = "8eQZnL/YfA783qd/LOY0pw==";
                urlPath = "/xiaoaiTest/story";
                break;
            }
            default:{
                secret = XiaoAiConstants.SECURITY.SMART_JIAOYOU_SECRET;
                keyId = XiaoAiConstants.SECURITY.SMART_JIAOYOU_KEYID;
                urlPath = "/xiaoai/";
                break;
            }
        }
        String algorithmForMac = "HmacSHA256";

        String method = "POST";
        String param = "";
        String source = new StringBuilder().append(method).append('\n').append(urlPath).append('\n').append(param).append('\n')
                .append(xiaomiDate).append('\n').append(host).append('\n')
                .append(contentType).append('\n').append(md5).append('\n').toString();
        try {
            Mac mac = Mac.getInstance(algorithmForMac);
            SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.decodeBase64(secret), algorithmForMac);
            mac.init(secretKeySpec);
//            String signature = Hex.encodeHexString(mac.doFinal(source.getBytes(StandardCharsets.UTF_8)));
            String signature = new StringBuilder("MIAI-HmacSHA256-V1 ").append(keyId).append("::")
                    .append(Hex.encodeHexString(mac.doFinal(source.getBytes(StandardCharsets.UTF_8)))).toString();
            return signature.equals(authorization);
//            boolean result = signature.equals(authorization);
//            log.info("signature:{}, compare:{}",signature, result);
//            return result;
        } catch (Exception e) {
            log.error("encry:"+e);
        }
        return false;
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
//        SimpleDateFormat sdf3 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
//        sdf3.setTimeZone(TimeZone.getTimeZone("GMT"));
//        String rfc1123_3 = sdf3.format(new Date(System.currentTimeMillis()-100000));
//        System.out.println("rfc1123_3 = "+rfc1123_3);
//        System.out.println(new Date(System.currentTimeMillis()));
        String s = URLEncoder.encode("+Ls69sFTjXN/6//831N1V==Vdfw", "UTF-8");
//        System.out.println(s);
        System.out.println("+Ls69sFTjXN/683//1N1V==Vdfw".replaceAll("/", ""));
        System.out.println(URLEncoder.encode("sgagsgdg", "UTF-8"));
        System.out.println(URLEncoder.encode("123467845131", "UTF-8"));
    }
}
