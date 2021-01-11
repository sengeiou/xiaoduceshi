package xiaoduhome.xiaoai.service;

import ai.qiwu.com.xiaoduhome.common.Constants;
import ai.qiwu.com.xiaoduhome.common.OkHttp3Utils;
import ai.qiwu.com.xiaoduhome.service.DplbotServiceUtil;
import ai.qiwu.com.xiaoduhome.service.ScheduleServiceNew;
import ai.qiwu.com.xiaoduhome.xiaoai.common.RequestTerminal;
import ai.qiwu.com.xiaoduhome.xiaoai.common.RequestWordNullException;
import ai.qiwu.com.xiaoduhome.xiaoai.common.Utils;
import ai.qiwu.com.xiaoduhome.xiaoai.common.XiaoAiConstants;
import ai.qiwu.com.xiaoduhome.xiaoai.model.request.XiaoAiRequest;
import ai.qiwu.com.xiaoduhome.xiaoai.model.response.XiaoAiResponse;
import ai.qiwu.com.xiaoduhome.xiaoai.model.response.innerResponse.Response;
import ai.qiwu.com.xiaoduhome.xiaoai.model.response.innerResponse.actionProperty.ActionProperty;
import ai.qiwu.com.xiaoduhome.xiaoai.model.response.innerResponse.actionProperty.AppIntentInfo;
import ai.qiwu.com.xiaoduhome.xiaoai.model.response.innerResponse.directive.AudioItem.AudioItem;
import ai.qiwu.com.xiaoduhome.xiaoai.model.response.innerResponse.directive.AudioItem.AudioStream;
import ai.qiwu.com.xiaoduhome.xiaoai.model.response.innerResponse.directive.Directive;
import ai.qiwu.com.xiaoduhome.xiaoai.model.response.innerResponse.toDisplay.PhoneTemplate;
import ai.qiwu.com.xiaoduhome.xiaoai.model.response.innerResponse.toDisplay.ToDisplay;
import ai.qiwu.com.xiaoduhome.xiaoai.model.response.innerResponse.toSpeak.ToSpeak;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static ai.qiwu.com.xiaoduhome.common.Constants.TTS_DIDI3_GET_XIAOAI;
import static ai.qiwu.com.xiaoduhome.common.Constants.TTS_DIDI4_GET_XIAOAI;
import static ai.qiwu.com.xiaoduhome.common.Constants.WelcomeWord.*;
import static ai.qiwu.com.xiaoduhome.common.Constants.WelcomeWord.SKILL;
import static ai.qiwu.com.xiaoduhome.common.Constants.WelcomeWord.SKILL_NOVEL;
import static ai.qiwu.com.xiaoduhome.common.Constants.WelcomeWord.SKILL_SMART;
import static ai.qiwu.com.xiaoduhome.xiaoai.common.XiaoAiConstants.ERROR_MSG.SORRY_REPEAT;
import static ai.qiwu.com.xiaoduhome.xiaoai.common.XiaoAiConstants.PREFIX_USERID;
import static ai.qiwu.com.xiaoduhome.xiaoai.common.XiaoAiConstants.TTS.WAIT_WORD_3;

/**
 * @author 苗权威
 * @dateTime 20-1-7 下午8:15
 */
@Service
@Slf4j
public class XiaoAiTestService {
    private static final ConcurrentHashMap<String, XiaoAiResponse> LAST_OUTTIME_RESULT = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, Byte> LAST_OUTTIME_MARK = new ConcurrentHashMap<>();
//    private static final ConcurrentHashMap<String, String> LAST_AUDIO_PLAY = new ConcurrentHashMap<>();

    public static final Byte OUTTIME_TYPE_0  = 0;
    private static final Byte OUTTIME_TYPE_1  = 1;
    private static final Byte OUTTIME_TYPE_2  = 2;

    public static final String deviceId = "XIAOAI_nbNGwoZIS8AmRGVqHog==";//XIAOAI_nbNGwoZIS8AmRGVqHog==,XIAOAI_oIOWfYEFbZGFHB5DzoMA==

    public static volatile int mark = 2;
    public static volatile int open = 1;

    public static volatile String appUri = "intent://com.qiwu.life.ui.activity.LaunchActivity";
    /**
     * 处理不同意图,type为0则说明是启动,为1则说明是会话进行中,为2则说明用户退出
     * @param request 封装请求信息
     * @return 响应结果
     */
    public XiaoAiResponse getResponse(XiaoAiRequest request) {
        String userId = getUserId(request);
//        String userId = "%2FScfTGmNbhn%2Fs8KXMJfZY4nQ%2F";
        int type = request.getRequest().getType();
        if (mark == 1) {
            if (open == 1) log.info("11111111111");
            switch (type) {
//            case 0 : return dealWithLaunchRequest(request);
                case 0 : return dealWithLaunchRequestPhone();
                case 1 : return dealWithLaunchRequestPhone();
//            case 1 : return dealWithIntentRequest(request, userId);
                case 2 : return dealWithLaunchRequestPhone();
                default: return dealWithLaunchRequestPhone();
            }
        } else if (mark == 2) {
            if (open == 1) log.info("2222222222222");
            switch (type) {
//            case 0 : return dealWithLaunchRequest(request);
                case 0 : return dealWithLaunchRequestPhone2();
                case 1 : return dealWithLaunchRequestPhone2();
//            case 1 : return dealWithIntentRequest(request, userId);
                case 2 : return dealWithLaunchRequestPhone2();
                default: return dealWithLaunchRequestPhone2();
            }
        } else if (mark == 3) {
            if (open == 1) log.info("3333333333333");
            switch (type) {
//            case 0 : return dealWithLaunchRequest(request);
                case 0 : return dealWithLaunchRequestPhone3();
                case 1 : return dealWithLaunchRequestPhone3();
//            case 1 : return dealWithIntentRequest(request, userId);
                case 2 : return dealWithLaunchRequestPhone3();
                default: return dealWithLaunchRequestPhone3();
            }
        }
        return null;
//        switch (type) {
////            case 0 : return dealWithLaunchRequest(request);
//            case 0 : return dealWithLaunchRequestPhone();
//            case 1 : return dealWithLaunchRequestPhone();
////            case 1 : return dealWithIntentRequest(request, userId);
//            case 2 : return dealWithEndRequest(userId, request.getType());
//            default: return buildTextResponse(SORRY_REPEAT);
//        }
    }

    /**
     * 构建基础返回信息,注意is_session_end字段置为false,
     * open_mic这个字段很重要,他会在播放音频或是tts结束后,开麦收音,因为默认麦是关闭的,若不主动打开则小爱在一次会话后退出技能。
     * @return 小爱返回信息
     */
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

    private XiaoAiResponse buildLunchResponse(String text) {
        XiaoAiResponse response = buildResponse();
        Response responseInfo = response.getResponse();

        ToDisplay toDisplay = new ToDisplay();
//        toDisplay.setType(3);
        toDisplay.setUi_type("phone");

        PhoneTemplate phoneTemplate = new PhoneTemplate();
        phoneTemplate.setTemplate_name("smartNovelLunch");
        Map<String, String> params = new HashMap<>();
        params.put("box0_title", "智能小说");
        params.put("box0_subtitle","几十部精彩作品在等着你");
        params.put("box0_text", text);
        params.put("box0_image", "https://didi-gz4.jiaoyou365.com/duai/image/Loading.png");
        phoneTemplate.setParams(params);
        toDisplay.setPhone_template(phoneTemplate);
        responseInfo.setTo_display(toDisplay);

        ToSpeak toSpeak = new ToSpeak();
        toSpeak.setType(0);
        toSpeak.setText(text);
        responseInfo.setTo_speak(toSpeak);

        return response;
    }

    private XiaoAiResponse buildResponseBeforeLoadout(String text) {
        XiaoAiResponse response = new XiaoAiResponse();
        response.setVersion("1.0");
        response.setIs_session_end(true);

        Response responseInfo = new Response();
        ToSpeak toSpeak = new ToSpeak();
        toSpeak.setType(0);
        toSpeak.setText(text);
        responseInfo.setTo_speak(toSpeak);

        response.setResponse(responseInfo);
        return response;
    }

    /**
     * 构建语音返回信息
     * @param audioUrl 音频文件地址
     * @return 小爱返回信息
     */
    private XiaoAiResponse buildAudioResponse(String audioUrl, JSONObject object) {
        XiaoAiResponse response = buildResponse();
        Response responseInfo = response.getResponse();

        ToDisplay toDisplay = new ToDisplay();
//        toDisplay.setType(3);
        toDisplay.setUi_type("phone");

        PhoneTemplate phoneTemplate = new PhoneTemplate();
        JSONArray array = object.getJSONArray("dialogs");
        JSONObject job;
        String actorUrl, text, actor, prefix, suffix;
        int i = 0;
        Map<String, String> params = new HashMap<>();
        for (Object ob: array) {
            if (i == 4) break;
            prefix = "box"+i;
            job = (JSONObject)ob;
            actor = job.getString("npcName");
            actorUrl = ScheduleServiceNew.getAsideImgUrl(job.getString("botAccount"), actor);
            text = job.getString("text");
            if (StringUtils.isNotBlank(text)) {
                text = text.replaceAll("\\{/.*?/}|\uD83C\uDD70|\uD83C\uDD71", "");
            }
            params.put(prefix+"_actor", actor);
            params.put(prefix+"_text", text);
            params.put(prefix+"_image", actorUrl);
            i++;
        }
        switch (i) {
            case 1: suffix = "One";break;
            case 2: suffix = "Two";break;
            case 3: suffix = "Thr";break;
            case 4: suffix = "Four";break;
            default: suffix = "One";
        }
        phoneTemplate.setParams(params);
        phoneTemplate.setTemplate_name("smartNovelInt"+suffix);
        toDisplay.setPhone_template(phoneTemplate);
        responseInfo.setTo_display(toDisplay);

        Directive directive = new Directive();
        directive.setType("audio");
        AudioItem audioItem = new AudioItem();
        AudioStream audioStream = new AudioStream();
        audioStream.setUrl(audioUrl);
        audioItem.setStream(audioStream);
        directive.setAudio_item(audioItem);
        List<Directive> directives = new ArrayList<>();
        directives.add(directive);
        responseInfo.setDirectives(directives);

        return response;
    }

    /**
     * 构建文字返回信息
     * @param text 文字
     * @return 小爱返回信息
     */
    private XiaoAiResponse buildTextResponse(String text) {
        XiaoAiResponse response = buildResponse();
        Response responseInfo = response.getResponse();

        ToSpeak toSpeak = new ToSpeak();
        toSpeak.setType(0);
        toSpeak.setText(text);
        responseInfo.setTo_speak(toSpeak);


        return response;
    }

    private XiaoAiResponse buildCancelTextResponse(String text) {
        XiaoAiResponse response = new XiaoAiResponse();
        response.setVersion("1.0");
        response.setIs_session_end(true);

        Response responseInfo = new Response();
        // 这个字段很重要，他会在播放音频或是tts结束后，开麦收音，因为默认麦是关闭的，
        // 若不主动打开则小爱在一次会话后退出技能。
        responseInfo.setOpen_mic(false);
        ToSpeak toSpeak = new ToSpeak();
        toSpeak.setType(0);
        toSpeak.setText(text);
        responseInfo.setTo_speak(toSpeak);
        response.setResponse(responseInfo);

        return response;
    }

    private XiaoAiResponse dealWithLaunchRequestPhone() {
        XiaoAiResponse response = new XiaoAiResponse();
        response.setVersion("1.0");
        response.setIs_session_end(true);

        Response responseInfo = new Response();
        // 这个字段很重要，他会在播放音频或是tts结束后，开麦收音，因为默认麦是关闭的，
        // 若不主动打开则小爱在一次会话后退出技能。
        responseInfo.setOpen_mic(false);
        responseInfo.setAction("App.LaunchWithAppQuickAppH5");

        ActionProperty actionProperty = new ActionProperty();
        AppIntentInfo appIntentInfo = new AppIntentInfo();
        appIntentInfo.setIntent_type("activity");
        appIntentInfo.setUri("com.qiwu.life.ui.activity.LaunchActivity");
        actionProperty.setApp_intent_info(appIntentInfo);
        responseInfo.setAction_property(actionProperty);

        ToSpeak toSpeak = new ToSpeak();
        toSpeak.setType(0);
        toSpeak.setText("请下载app后体验");
        responseInfo.setTo_speak(toSpeak);

        response.setResponse(responseInfo);

        return response;
    }

    private XiaoAiResponse dealWithLaunchRequestPhone2() {
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
        appIntentInfo.setUri(appUri);
//        appIntentInfo.setUri("com.qiwu.life.ui.activity.LaunchActivity");
//        appIntentInfo.setUri("intent://com.qiwu.life.ui.activity.LaunchActivity");
        actionProperty.setApp_intent_info(appIntentInfo);
        responseInfo.setAction_property(actionProperty);

        ToSpeak toSpeak = new ToSpeak();
        toSpeak.setType(0);
        toSpeak.setText("请下载app后体验");
        responseInfo.setTo_speak(toSpeak);

        response.setResponse(responseInfo);

        return response;
    }

    private XiaoAiResponse dealWithLaunchRequestPhone3() {
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
        appIntentInfo.setUri("com.qiwu.life.ui.activity.LaunchActivity");
        actionProperty.setApp_intent_info(appIntentInfo);
        responseInfo.setAction_property(actionProperty);

        ToSpeak toSpeak = new ToSpeak();
        toSpeak.setType(0);
        toSpeak.setText("请下载app后体验");
        responseInfo.setTo_speak(toSpeak);

        response.setResponse(responseInfo);

        return response;
    }

    /**
     * 处理正常会话意图;
     * 将任务提交线程池，监控其执行时间，若1.8s内没有返回则判为超时，提示用户稍等，下一次再将结果返回给用户
     * @param request 请求信息
     * @param userId 用户在小爱上的id
     * @return 响应信息
     */
    private XiaoAiResponse dealWithIntentRequest(final XiaoAiRequest request, final String userId) {
        Boolean idle = request.getRequest().getNo_response();
        Byte waitType;
        if ((waitType=LAST_OUTTIME_MARK.remove(userId)) != null) {
            boolean firstTime = waitType == 0;
            if (LAST_OUTTIME_RESULT.get(userId) == null) {
                if (Boolean.TRUE.equals(idle)) {
                    if (firstTime) {
                        LAST_OUTTIME_MARK.put(userId, OUTTIME_TYPE_1);
                        return buildTextResponse((RandomUtils.nextInt() & 1) == 1 ? XiaoAiConstants.TTS.WAIT_WORD_1 :
                                XiaoAiConstants.TTS.WAIT_WORD_2);
                    } else {
                        LAST_OUTTIME_MARK.put(userId, OUTTIME_TYPE_2);
                        return buildTextResponse(WAIT_WORD_3);
                    }
                }
                LAST_OUTTIME_MARK.put(userId, OUTTIME_TYPE_0);
                return buildTextResponse((RandomUtils.nextInt() & 1) == 1 ? XiaoAiConstants.TTS.WAIT_WORD_1 :
                        XiaoAiConstants.TTS.WAIT_WORD_2);
            }
            return LAST_OUTTIME_RESULT.remove(userId);
        }
//        String path;
//        if ((path = LAST_AUDIO_PLAY.get(userId)) != null) {
//            log.info(userId+": audio no play");
//            return buildAudioResponse(changeAudioUrl(path, userId));
//        }
        if (Boolean.TRUE.equals(idle)) {
            try {
                //log.info("开始等待");
                Thread.sleep(1700);
                //log.info("等待OVER");
            } catch (InterruptedException e) {
                log.warn("等待中断");
            }
//            XiaoAiResponse response = getLastResult(userId);
//            if (response != null) return response;
            //log.info("用户没说话");
            //return buildTextResponse(".");
            return buildTextResponse((RandomUtils.nextInt() & 1) == 1 ? XiaoAiConstants.TTS.USER_IDLE :
                    XiaoAiConstants.TTS.ANOTHER_USER_IDLE);
        }
//        XiaoAiResponse response = getLastResult(userId);
//        if (response != null) return response;
        try {
            Future future = DplbotServiceUtil.getPool().submit(() -> task(userId, request.getQuery(), request.getType()));
            future.get(2200, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException e) {
            log.error("获取任务执行结果出错:{}", e.toString());
            return buildTextResponse(SORRY_REPEAT);
        } catch (TimeoutException e) {
            log.info("超时,让用户再说一遍:{}",e.toString());
            LAST_OUTTIME_MARK.put(userId, OUTTIME_TYPE_0);
            return buildTextResponse(XiaoAiConstants.ERROR_MSG.TIME_OUT);
        } catch (Exception all) {
            log.error("任务执行发生了错误:{}", all.toString());
            return buildTextResponse(SORRY_REPEAT);
        }
        return LAST_OUTTIME_RESULT.remove(userId);
    }

    /**
     * 处理用户退出意图,先向中控发送“退出”，之后返回结束语
     * @param userId 用户id
     * @return 返回信息
     */
    private XiaoAiResponse dealWithEndRequest(String userId, Integer type) {
//        LAST_OUTTIME_MARK.remove(userId);
//        LAST_OUTTIME_RESULT.remove(userId);
//        LAST_AUDIO_PLAY.remove(userId);
        RequestTerminal.backBotRequest(userId, type);
        String byeWord;
        switch (type) {
            case 1: byeWord = Constants.EndMsg.END_BYE_STORY;break;
            case 2: byeWord = Constants.EndMsg.END_BYE_NOVEL;break;
            case 5: byeWord = Constants.EndMsg.END_BYE_JIAOYOU;break;
            default: byeWord = Constants.EndMsg.END_BYE_DEFAULT;break;
        }
        return buildCancelTextResponse(byeWord);
    }

    /**
     * 获取用户id
     * @param request 请求信息
     * @return 用户id
     */
    private String getUserId(XiaoAiRequest request) {
        String userId = request.getSession().getUser().getUser_id();
        if (userId == null) {
            userId = request.getContext().getDevice_id();
            if (userId != null) userId = Utils.modifyDeviceId(userId);
        }
        if (userId == null) {
            userId = Utils.useIPAsItsID();
        }
        try {
            return PREFIX_USERID+ URLEncoder.encode(userId, "UTF-8");
        } catch (Exception e) {
            return PREFIX_USERID+ userId.replaceAll("/", "");
        }
//        return PREFIX_USERID+userId.replaceAll(NUM_OR_ALPHA, "");
//        return Utils.modifyDeviceId(userId);
    }

    private XiaoAiResponse getLastResult(String userId) {
        return LAST_OUTTIME_RESULT.remove(userId);
    }

    private void task(String userId, String userWord, Integer type) {
        XiaoAiResponse xiaoAiResponse;
        if (userWord == null) {
            log.warn("请求中没有带有用户的话");
            throw new RequestWordNullException("user word can not be null");
        }
        String response;
        try {
            response = RequestTerminal.requestTerminate(userId, userWord, type, null);
//            log.info("小爱,小悟返回:{}", response);
        } catch (Exception e) {
            log.error("向终端发出的请求信息失败:{}", e.toString());
            xiaoAiResponse = buildTextResponse(SORRY_REPEAT);
            LAST_OUTTIME_RESULT.put(userId, xiaoAiResponse);
            return;
        }
        if (StringUtils.isBlank(response)) {
            log.error("中控返回结果为空");
            xiaoAiResponse = buildTextResponse(SORRY_REPEAT);
        } else {
            JSONObject object = JSON.parseObject(response);
            try {
//                log.info(response);
                JSONArray commands = object.getJSONArray("commands");
                if (commands != null && commands.size() != 0) {
                    String txt;
                    for (Object ob: commands) {
                        txt = ((JSONObject) ob).getString("text");
                        int i;
                        if (StringUtils.isNotBlank(txt) && (i=txt.indexOf("out")) != -1) {
                            log.info("小爱,推荐bot退出：{},userId:{}", userWord, userId);
                            String content = txt.substring(i+3, txt.indexOf("☚")).trim();
                            String skillName, outWord;
                            switch (type) {
                                case 1: {
                                    skillName = SKILL_SMART;
                                    outWord = Constants.EndMsg.END_BYE_STORY;
                                    break;
                                }
                                case 2: {
                                    skillName = SKILL_NOVEL;
                                    outWord = Constants.EndMsg.END_BYE_NOVEL;
                                    break;
                                }
                                default: {
                                    skillName = SKILL;
                                    outWord = Constants.EndMsg.END_BYE_JIAOYOU;
                                }
                            }
                            RequestTerminal.backBotRequest(userId, type);
                            if (StringUtils.isNotBlank(content)) {
                                content = content.replaceAll("\\{/.*?/}", "")+skillName;
                                LAST_OUTTIME_RESULT.put(userId, buildResponseBeforeLoadout(content));
                                return;
                            } else {
                                JSONArray dialogs = object.getJSONArray("dialogs");
                                if (dialogs != null && dialogs.size() != 0) {
                                    String str = ((JSONObject)dialogs.get(0)).getString("text");
                                    if (StringUtils.isNotBlank(str)) {
                                        str = str.replaceAll("\\{/.*?/}", "")+skillName;
                                        LAST_OUTTIME_RESULT.put(userId, buildResponseBeforeLoadout(str));
                                        return;
                                    }
                                }
                                LAST_OUTTIME_RESULT.put(userId, buildResponseBeforeLoadout(outWord));
                                return;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("推荐bot退出解析出错:"+e);
            }

            String path;
            try {
                path = OkHttp3Utils.doPostJsonStr(XiaoAiConstants.TTS_DIDI3, response);
                if (StringUtils.isBlank(path) || path.equals("null")) {
                    log.error("没有获取到TTS返回的路径path");
                    LAST_OUTTIME_RESULT.put(userId, buildTextResponse(SORRY_REPEAT));
                    return;
                }
                xiaoAiResponse = buildAudioResponse(changeAudioUrl(path, userId), object);
//                LAST_AUDIO_PLAY.put(userId, path);
            } catch (Exception e) {
                log.error("没有获取到TTS返回的路径path");
                xiaoAiResponse = buildTextResponse(SORRY_REPEAT);
            }
        }
        LAST_OUTTIME_RESULT.put(userId, xiaoAiResponse);
    }

    private String changeAudioUrl(String path, String userId) {
        path = path.replaceAll("%2F", "/");
        return new StringBuilder(TTS_DIDI3_GET_XIAOAI).append(userId).append("&path=").append(path).toString();
    }

    private String changeAudioUrlTemp(String path, String userId) {
        path = path.replaceAll("%2F", "/");
//        return new StringBuilder(TTS_DIDI4_GET_XIAOAI_STATIC).append(path.substring(path.indexOf("audio")+6)).toString();
        return new StringBuilder(TTS_DIDI4_GET_XIAOAI).append(userId).append("&path=").append(path).toString();
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        XiaoAiTestService service = new XiaoAiTestService();
        System.out.println(JSON.toJSONString(service.dealWithLaunchRequestPhone()));
    }
}
