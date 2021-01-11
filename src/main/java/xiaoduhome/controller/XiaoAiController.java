package xiaoduhome.controller;

import ai.qiwu.com.xiaoduhome.common.OkHttp3Utils;
import ai.qiwu.com.xiaoduhome.pojo.data.XiaoAiPhoneMark;
import ai.qiwu.com.xiaoduhome.spirit.SpiritTerminateRequest;
import ai.qiwu.com.xiaoduhome.xiaoai.common.RequestTerminal;
import ai.qiwu.com.xiaoduhome.xiaoai.common.Utils;
import ai.qiwu.com.xiaoduhome.xiaoai.model.request.XiaoAiRequest;
import ai.qiwu.com.xiaoduhome.xiaoai.model.response.XiaoAiResponse;
import ai.qiwu.com.xiaoduhome.xiaoai.model.response.innerResponse.Response;
import ai.qiwu.com.xiaoduhome.xiaoai.model.response.innerResponse.actionProperty.ActionProperty;
import ai.qiwu.com.xiaoduhome.xiaoai.model.response.innerResponse.actionProperty.AppIntentInfo;
import ai.qiwu.com.xiaoduhome.xiaoai.model.response.innerResponse.toSpeak.ToSpeak;
import ai.qiwu.com.xiaoduhome.xiaoai.service.XiaoAiService;
import ai.qiwu.com.xiaoduhome.xiaoai.service.XiaoAiServiceRedis;
import ai.qiwu.com.xiaoduhome.xiaoai.service.XiaoAiTestService;
import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;

import static ai.qiwu.com.xiaoduhome.xiaoai.common.XiaoAiConstants.ERROR_MSG.SORRY_REPEAT;

/**
 * @author 苗权威
 * @dateTime 19-12-6 上午9:54
 */
@RestController
@RequestMapping("/xiaoai")
@Slf4j
@RequiredArgsConstructor(onConstructor_={@Autowired})
public class XiaoAiController {

//    private final XiaoAiService xiaoAiService;
//    private final XiaoAiTestService xiaoAiTestService;
    private final XiaoAiServiceRedis xiaoAiServiceRedis;

    private final static XiaoAiResponse PHONE_RESPONSE;

    static {
        XiaoAiResponse response = new XiaoAiResponse();
        response.setVersion("1.0");
        response.setIs_session_end(true);

        Response responseInfo = new Response();
        // 这个字段很重要，他会在播放音频或是tts结束后，开麦收音，因为默认麦是关闭的，
        // 若不主动打开则小爱在一次会话后退出技能。
        responseInfo.setOpen_mic(false);
        responseInfo.setAction("App.LaunchIntent");

        ActionProperty actionProperty = new ActionProperty();
        AppIntentInfo appIntentInfo = new AppIntentInfo();
        appIntentInfo.setIntent_type("activity");
        appIntentInfo.setUri("intent://com.qiwu.life.ui.activity.LaunchActivity");
        actionProperty.setApp_intent_info(appIntentInfo);
        responseInfo.setAction_property(actionProperty);

        ToSpeak toSpeak = new ToSpeak();
        toSpeak.setType(0);
        toSpeak.setText("请下载app后体验");
        responseInfo.setTo_speak(toSpeak);

        response.setResponse(responseInfo);

        PHONE_RESPONSE = response;
    }

    /**
     * Controller控制器
     * @param request
     * @return
     */
    @PostMapping("/")
    public XiaoAiResponse xiaoai(@RequestBody XiaoAiRequest request,HttpServletRequest servletRequest) { // 交游天下
//        log.info("请求："+request);
        //        log.info("响应："+response);
        try {
            if (!Utils.encry(5, servletRequest)) {
                log.error("验证没通过:5");
                return null;
            }
            request.setType(5);
            return xiaoAiServiceRedis.getResponse(request);
        } catch (Exception e) {
            log.error("controller xiaoai:"+e);
        }
        return buildTextResponse(SORRY_REPEAT);
//        return xiaoAiService.getResponse(request);
    }

    @PostMapping("/adult")
    public XiaoAiResponse xiaoaiAdult(@RequestBody XiaoAiRequest request, HttpServletRequest servletRequest) { // 智能小说
        //log.info("请求："+request);
//        log.info("================================");
        try {
            if (!Utils.encry(2, servletRequest)) {
                log.error("验证没通过:2");
                return null;
            }
            request.setType(2);
            //        log.info("响应："+response);
            return xiaoAiServiceRedis.getResponse(request);
        } catch (Exception e) {
            log.error("controller xiaoaiAdult:"+e);
        }
        return buildTextResponse(SORRY_REPEAT);
//        return xiaoAiService.getResponse(request);
    }

    @PostMapping("/child")
    public XiaoAiResponse xiaoaiChild(@RequestBody XiaoAiRequest request,HttpServletRequest servletRequest) { // 智能故事
        //log.info("请求："+request);
        try {
            if (!Utils.encry(1, servletRequest)) {
                log.error("验证没通过:1");
                return null;
            }
            request.setType(1);
            //        log.info("响应："+response);
            return xiaoAiServiceRedis.getResponse(request);
        } catch (Exception e) {
            log.error("controller xiaoaiChild:"+e);
        }
        return buildTextResponse(SORRY_REPEAT);
//        return xiaoAiService.getResponse(request);
    }

    @PostMapping("/phone") // 手机技能
    // @RequestBody XiaoAiRequest request,
    public XiaoAiResponse xiaoaiPhoneStory(@RequestBody XiaoAiRequest request) {
//        long start = System.currentTimeMillis();
//        if (!Utils.encry(3, servletRequest)) return null;
//        log.info("encry:"+(System.currentTimeMillis()-start));

//        try (InputStream stream = servletRequest.getInputStream()){
//            StringBuilder sb = new StringBuilder();
//            byte[] b = new byte[4096];
//            for (int n; (n = stream.read(b)) != -1;)
//            {
//                sb.append(new String(b, 0, n));
//            }
//            String str = sb.toString();
//            log.info("请求："+str);
//            XiaoAiRequest xiaoAiRequest = JSON.parseObject(str, XiaoAiRequest.class);
//            request.setType(5);
//            XiaoAiResponse response = xiaoAiTestService.getResponse(request);
//            log.info("响应："+JSON.toJSONString(response));
//            return response;
//        } catch (Exception e) {
//
//        }

        try {
//            log.info("请求信息："+request);
            //            log.info("响应信息："+response);
            return PHONE_RESPONSE;
        } catch (Exception e) {
            log.error("controller xiaoai phone:"+e);
        }
        return buildTextResponse(SORRY_REPEAT);
    }

    @GetMapping("/app/uri")
    public void changeAppUri(String uri) {
        XiaoAiTestService.appUri = uri;
        log.info("app uri:"+uri);
    }

    @PostMapping("/mark")
    public void setMark(@RequestBody XiaoAiPhoneMark data) {
        String secret = data.getSecret();
        if (StringUtils.startsWith(secret, "mqw") && StringUtils.endsWith(secret, "#")
                && secret.length() == 8) {
            XiaoAiTestService.mark = data.getMark();
            XiaoAiTestService.open = data.getOpen();
        }
        log.info("xiaoai mark:"+XiaoAiTestService.mark);
    }

    @PostMapping("/story") //互动故事
    // @RequestBody XiaoAiRequest request,
    public XiaoAiResponse xiaoaiChildStory(@RequestBody XiaoAiRequest request) {
//        long start = System.currentTimeMillis();
//        if (!Utils.encry(3, servletRequest)) return null;
//        log.info("encry:"+(System.currentTimeMillis()-start));

//        try (InputStream stream = servletRequest.getInputStream()){
//            StringBuilder sb = new StringBuilder();
//            byte[] b = new byte[4096];
//            for (int n; (n = stream.read(b)) != -1;)
//            {
//                sb.append(new String(b, 0, n));
//            }
//            String str = sb.toString();
//            log.info("请求："+str);
//            XiaoAiRequest request = JSON.parseObject(str, XiaoAiRequest.class);
//            request.setType(5);
//            XiaoAiResponse response = xiaoAiServiceRedis.getResponse(request);
//            log.info("响应："+response);
//            return response;
//        } catch (Exception e) {
//
//        }
//        return null;

        log.info("=============================");
        request.setType(5);
        //        log.info("响应："+response);
        return xiaoAiServiceRedis.getResponse(request);
    }


////    @PostMapping("/literature") // 互动文艺
//    public XiaoAiResponse xiaoaiAdultLiterature(@RequestBody XiaoAiRequest request) {
//        //log.info("请求："+request);
//        request.setType(4);
//        XiaoAiResponse response = xiaoAiService.getResponse(request);
////        log.info("响应："+response);
//        return response;
////        return xiaoAiService.getResponse(request);
//    }

//    @GetMapping("/audio/play")
//    public void xiaoaiPlayed(String userId) {
//        if (StringUtils.isNotBlank(userId)) {
//            log.info("小爱确认音频播放:"+userId);
//            xiaoAiServiceRedis.theAudioAlreadyPlay(userId);
//        }
//        else log.error("/xiaoai/audio/play userId is null");
//    }

    private XiaoAiResponse buildResponse() {
        XiaoAiResponse response = new XiaoAiResponse();
        response.setVersion("1.0");
        response.setIs_session_end(false);

        Response responseInfo = new Response();
        // 这个字段很重要，他会在播放音频或是tts结束后，开麦收音，因为默认麦是关闭的，
        // 若不主动打开则小爱在一次会话后退出技能。
        responseInfo.setOpen_mic(true);
        response.setResponse(responseInfo);
        return response;
    }

    private XiaoAiResponse buildTextResponse(String text) {
        XiaoAiResponse response = buildResponse();
        Response responseInfo = response.getResponse();

        ToSpeak toSpeak = new ToSpeak();
        toSpeak.setType(0);
        toSpeak.setText(text);
        responseInfo.setTo_speak(toSpeak);

        return response;
    }

    public static void main(String[] args) throws IOException {
        SpiritTerminateRequest requestPojo = new SpiritTerminateRequest();
        requestPojo.setUid("wetewt23424");
        requestPojo.setMsg("推荐作品");
        requestPojo.setResptype(null);
        HashMap<String, String> ALL_HEADERS = new HashMap<>();
        ALL_HEADERS.put("Content-Type", "application/json;charset=utf-8");
        ALL_HEADERS.put("App-Channel-Id", "xiaoai-jiaoyou-audio-test");
        String resStr = OkHttp3Utils.doPostJsonStr("http://hw-gz19.heyqiwu.cn:8888/centralControl/api/chat2", JSON.toJSONString(requestPojo), ALL_HEADERS);
        System.out.println(resStr);
    }
}
