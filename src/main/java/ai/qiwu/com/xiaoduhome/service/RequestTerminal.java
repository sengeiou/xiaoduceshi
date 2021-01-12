package ai.qiwu.com.xiaoduhome.service;

import ai.qiwu.com.xiaoduhome.common.Constants;
import ai.qiwu.com.xiaoduhome.common.OkHttp3Utils;
import ai.qiwu.com.xiaoduhome.pojo.QiWuRequest;
import ai.qiwu.com.xiaoduhome.pojo.QiWuResponse;
import ai.qiwu.com.xiaoduhome.spirit.SpiritTerminateRequest;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author 苗权威
 * @dateTime 19-6-29 下午3:49
 */
@Slf4j
public class RequestTerminal {

    static String requestTerminateUrl(String userWord, String userId, String channel, String baseHostUrl) throws IOException {
        SpiritTerminateRequest requestPojo = new SpiritTerminateRequest();
        requestPojo.setUid(userId);
        requestPojo.setMsg(userWord);
        String requestJsonStr = JSON.toJSONString(requestPojo);
//        log.info("向小悟发送的请求信息:{}", requestJsonStr);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json;charset=utf-8");
        headers.put("App-Channel-Id", channel);
//        return OkHttp3Utils.doPostJsonStr(baseHostUrl, requestJsonStr, headers);
        return OkHttp3Utils.doPostJsonStrForCentralWithUidParam(baseHostUrl, requestJsonStr, headers, userId);
    }

    public static String requestTerminateTemp(String userWord, String userId, String channel) throws IOException {
        SpiritTerminateRequest requestPojo = new SpiritTerminateRequest();
        requestPojo.setUid(userId);
        requestPojo.setMsg(userWord);
        String requestJsonStr = JSON.toJSONString(requestPojo);
//        log.info("向小悟发送的请求信息:{}", requestJsonStr);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json;charset=utf-8");
        headers.put("App-Channel-Id", channel);
//        String ip = Utils.getRemoteIpByServletRequest(request);
//        if (StringUtils.isNotBlank(ip)) {
//            headers.put("ip", ip);
//            if (test++ < 5) {
//                try {
//                    Enumeration enumeration = request.getHeaderNames();
//                    while (enumeration.hasMoreElements()) {
//                        String name = (String) enumeration.nextElement();
//                        log.info(name+":"+request.getHeader(name));
//                    }
//                } catch (Exception e) {
//                    log.warn("failed:"+e);
//                }
//            }
//        } else {
//            log.warn("ip is null");
//        }
//        switch (type) {
//            case 1 :
//            case 3 : headers.put("App-Channel-Id", "jiaoyou-audio-child-test");break;
//            case 2 :
//            case 4 : headers.put("App-Channel-Id", "jiaoyou-audio-adult-test");break;
//            default: headers.put("App-Channel-Id", "jiaoyou-audio-test");
//        }
//        return OkHttp3Utils.doPostJsonStr(Constants.QI_WU_INTERFACE, requestJsonStr, headers);
        return OkHttp3Utils.doPostJsonStr(ScheduleServiceNew.BASE_HOST_URL, requestJsonStr, headers);
    }

    static QiWuResponse requestTerminate(String userWord, String userId, int type) throws IOException {
        QiWuRequest requestPojo = new QiWuRequest();
        requestPojo.setUid(userId);
//        requestPojo.setAccess(Constants.SERVICE_NAME);
        requestPojo.setMsg(userWord);
        String requestJsonStr = JSON.toJSONString(requestPojo);
//        log.info("向小悟发送的请求信息:{}", requestJsonStr);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json;charset=utf-8");
        switch (type) {
            case 1 :
            case 3 : headers.put("App-Channel-Id", "jiaoyou-audio-child-test");break;
            case 2 :
            case 4 : headers.put("App-Channel-Id", "jiaoyou-audio-adult-test");break;
            default: headers.put("App-Channel-Id", "jiaoyou-audio-test");
        }
        String resStr = OkHttp3Utils.doPostJsonStr(Constants.QI_WU_INTERFACE, requestJsonStr, headers);
        if (resStr == null) return null;
//        log.info("返回信息:{}", resStr);
        return JSON.parseObject(resStr, QiWuResponse.class);
    }

    static QiWuResponse requestTerminateDU2(String userWord, String userId, int type) throws IOException {
        QiWuRequest requestPojo = new QiWuRequest();
        requestPojo.setUid(userId);
//        requestPojo.setAccess(Constants.SERVICE_NAME);
        requestPojo.setMsg(userWord);
        String requestJsonStr = JSON.toJSONString(requestPojo);
//        log.info("向小悟发送的请求信息:{}", requestJsonStr);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json;charset=utf-8");
        switch (type) {
            case 1 :
            case 3 : headers.put("App-Channel-Id", "jiaoyou-audio-child-test");break;
            case 2 :
            case 4 : headers.put("App-Channel-Id", "jiaoyou-audio-adult-test");break;
            default: headers.put("App-Channel-Id", "jiaoyou-audio-test");
        }
        String resStr = OkHttp3Utils.doPostJsonStr(Constants.INTERFACE_DI, requestJsonStr, headers);
        if (resStr == null) return null;
//        log.info("返回信息:{}", resStr);
        return JSON.parseObject(resStr, QiWuResponse.class);
    }

    static void backRequest(String userId, int type) {
        SpiritTerminateRequest requestPojo = new SpiritTerminateRequest();
        requestPojo.setUid(userId);
        requestPojo.setMsg("退出");
        String requestJsonStr = JSON.toJSONString(requestPojo);
//        OkHttp3Utils.backPostAsync(ScheduleServiceNew.BASE_HOST_URL, requestJsonStr, type);
        OkHttp3Utils.backPostAsync(ScheduleServiceNew.BASE_HOST_URL+"?uid="+userId, requestJsonStr, type);
//        OkHttp3Utils.backPostAsync(Constants.QI_WU_INTERFACE, requestJsonStr, type);
    }

    static void backRequestDU2(String userId, int type) {
        QiWuRequest requestPojo = new QiWuRequest();
        requestPojo.setUid(userId);
//        requestPojo.setAccess(Constants.SERVICE_NAME);
        requestPojo.setMsg("退出当前作品");
        String requestJsonStr = JSON.toJSONString(requestPojo);
        OkHttp3Utils.backPostAsync(Constants.INTERFACE_DI, requestJsonStr, type);
    }

    public static void backRequestByChannel(String userId, String channel) {
        SpiritTerminateRequest requestPojo = new SpiritTerminateRequest();
        requestPojo.setUid(userId);
        requestPojo.setMsg("退出");
        String requestJsonStr = JSON.toJSONString(requestPojo);
        OkHttp3Utils.backPostAsyncByChannel(ScheduleServiceNew.BASE_HOST_URL+"?uid="+userId, requestJsonStr, channel);
    }
}
