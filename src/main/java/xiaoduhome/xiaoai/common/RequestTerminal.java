package xiaoduhome.xiaoai.common;

import ai.qiwu.com.xiaoduhome.common.OkHttp3Utils;
import ai.qiwu.com.xiaoduhome.outside.OutSideCache;
import ai.qiwu.com.xiaoduhome.outside.model.SingleBotRequest;
import ai.qiwu.com.xiaoduhome.outside.model.SingleBotResponse;
import ai.qiwu.com.xiaoduhome.service.ScheduleServiceNew;
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

    private static final HashMap<String, String> ALL_HEADERS;
    private static final HashMap<String, String> CHILD_HEADERS;
    private static final HashMap<String, String> ADULT_HEADERS;

    private static final HashMap<String, String> OUTSIDE_XIAOAI_HEADER;
    private static final HashMap<String, String> OUTSIDE_XIAODU_HEADER;

    static {
        ALL_HEADERS = new HashMap<>(2);
        ALL_HEADERS.put("Content-Type", "application/json;charset=utf-8");
        ALL_HEADERS.put("App-Channel-Id", "xiaoai-jiaoyou-audio-test");
        CHILD_HEADERS = new HashMap<>(2);
        CHILD_HEADERS.put("Content-Type", "application/json;charset=utf-8");
        CHILD_HEADERS.put("App-Channel-Id", "xiaoai-jiaoyou-audio-child-test");
        ADULT_HEADERS = new HashMap<>(2);
        ADULT_HEADERS.put("Content-Type", "application/json;charset=utf-8");
        ADULT_HEADERS.put("App-Channel-Id", "xiaoai-jiaoyou-audio-adult-test");

        OUTSIDE_XIAOAI_HEADER = new HashMap<>(2);
        OUTSIDE_XIAOAI_HEADER.put("Content-Type", "application/json;charset=utf-8");
        OUTSIDE_XIAOAI_HEADER.put("App-Channel-Id", "xiaoai-single-jiaoyou-pro");

        OUTSIDE_XIAODU_HEADER = new HashMap<>(2);
        OUTSIDE_XIAODU_HEADER.put("Content-Type", "application/json;charset=utf-8");
        OUTSIDE_XIAODU_HEADER.put("App-Channel-Id", "xiaodu-single-jiaoyou-pro");
    }

    /**
     * 向终端发出请求
     * @param userId 用户id
     * @param userWord 用户说的话
     * @return mp3链接及文字描述
     * @throws IOException
     */
    public static String requestTerminate(String userId, String userWord, Integer type, String baseHostUrl) throws IOException {
        SpiritTerminateRequest requestPojo = new SpiritTerminateRequest();
        requestPojo.setUid(userId);
        requestPojo.setMsg(userWord);
        String requestJsonStr = JSON.toJSONString(requestPojo);
        //log.info("向终端发出请求的参数信息:{}", requestJsonStr);
        HashMap<String, String> headers;
        switch (type) {
            case 1: headers = CHILD_HEADERS; break;
            case 2: headers = ADULT_HEADERS; break;
            default: headers = ALL_HEADERS; break;
        }
//        return OkHttp3Utils.doPostJsonStr(baseHostUrl, requestJsonStr, headers);
        return OkHttp3Utils.doPostJsonStrForCentralWithUidParam(baseHostUrl, requestJsonStr, headers, userId);
    }

    public static SingleBotResponse outSideRequestTerminate(String userId, String userWord, String botAccount) throws IOException {
        SingleBotRequest requestPojo = new SingleBotRequest();
        requestPojo.setUid(userId);
        requestPojo.setMsg(userWord);
        requestPojo.setBotAccount(botAccount);
        String requestJsonStr = JSON.toJSONString(requestPojo);
        //log.info("向终端发出请求的参数信息:{}", requestJsonStr);

        String response = OkHttp3Utils.doPostJsonStr(OutSideCache.SINGLE_BOT_INTERFACE, requestJsonStr, OUTSIDE_XIAOAI_HEADER);
        return JSON.parseObject(response, SingleBotResponse.class);
    }

    public static SingleBotResponse xiaoduOutSideRequestTerminate(String userId, String userWord, String botAccount) throws IOException {
        SingleBotRequest requestPojo = new SingleBotRequest();
        requestPojo.setUid(userId);
        requestPojo.setMsg(userWord);
        requestPojo.setBotAccount(botAccount);
        String requestJsonStr = JSON.toJSONString(requestPojo);
        //log.info("向终端发出请求的参数信息:{}", requestJsonStr);

        String response = OkHttp3Utils.doPostJsonStr(OutSideCache.SINGLE_BOT_INTERFACE, requestJsonStr, OUTSIDE_XIAODU_HEADER);
        return JSON.parseObject(response, SingleBotResponse.class);
    }

    public static void backBotRequest(String userId, Integer type) {
        SpiritTerminateRequest requestPojo = new SpiritTerminateRequest();
        requestPojo.setUid(userId);
        requestPojo.setMsg(XiaoAiConstants.TTS.END_WORD);
        String requestJsonStr = JSON.toJSONString(requestPojo);
        OkHttp3Utils.xiaoaiBackPostAsync(ScheduleServiceNew.BASE_HOST_URL+"?uid="+userId, requestJsonStr, type);
//        if (userId.equals(XiaoAiService.deviceId)) {
//            OkHttp3Utils.backPostAsync(XiaoAiConstants.QI_WU_TERMINATE_DIDI, requestJsonStr, type);
//        }else {
//            OkHttp3Utils.backPostAsync(XiaoAiConstants.QI_WU_TERMINATE_DIDI, requestJsonStr, type);
//        }
    }

    public static void tmallLunchCancelRequest(String userId, Integer type) {
        SpiritTerminateRequest requestPojo = new SpiritTerminateRequest();
        requestPojo.setUid(userId);
        requestPojo.setMsg(XiaoAiConstants.TTS.END_WORD);
        String requestJsonStr = JSON.toJSONString(requestPojo);
        OkHttp3Utils.tmallBackPostAsync(ScheduleServiceNew.BASE_HOST_URL+"?uid="+userId, requestJsonStr, type);
//        if (userId.equals(XiaoAiService.deviceId)) {
//            OkHttp3Utils.backPostAsync(XiaoAiConstants.QI_WU_TERMINATE_DIDI, requestJsonStr, type);
//        }else {
//            OkHttp3Utils.backPostAsync(XiaoAiConstants.QI_WU_TERMINATE_DIDI, requestJsonStr, type);
//        }
    }

    public static void tmallBotCancelRequest(String userId, Integer type) {
        SpiritTerminateRequest requestPojo = new SpiritTerminateRequest();
        requestPojo.setUid(userId);
        requestPojo.setMsg(XiaoAiConstants.TTS.END_BOT_WORD);
        String requestJsonStr = JSON.toJSONString(requestPojo);
        OkHttp3Utils.tmallBackPostAsync(ScheduleServiceNew.BASE_HOST_URL, requestJsonStr, type);
    }

    public static void main(String[] args) throws IOException {
        String s = requestTerminate("12342134dfg", "推荐作品", 2, "http://hw-gz15.heyqiwu.cn:");
        System.out.println(s);
    }
}
