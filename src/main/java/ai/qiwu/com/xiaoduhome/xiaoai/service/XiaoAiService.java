package ai.qiwu.com.xiaoduhome.xiaoai.service;

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
import ai.qiwu.com.xiaoduhome.xiaoai.model.response.innerResponse.directive.AudioItem.AudioItem;
import ai.qiwu.com.xiaoduhome.xiaoai.model.response.innerResponse.directive.AudioItem.AudioStream;
import ai.qiwu.com.xiaoduhome.xiaoai.model.response.innerResponse.directive.Directive;
import ai.qiwu.com.xiaoduhome.xiaoai.model.response.innerResponse.toDisplay.ToDisplay;
import ai.qiwu.com.xiaoduhome.xiaoai.model.response.innerResponse.toSpeak.ToSpeak;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static ai.qiwu.com.xiaoduhome.common.Constants.*;
import static ai.qiwu.com.xiaoduhome.common.Constants.WelcomeWord.*;
import static ai.qiwu.com.xiaoduhome.controller.XiaoDuHomeController.*;
import static ai.qiwu.com.xiaoduhome.xiaoai.common.XiaoAiConstants.ERROR_MSG.SORRY_REPEAT;
import static ai.qiwu.com.xiaoduhome.xiaoai.common.XiaoAiConstants.PREFIX_USERID;
import static ai.qiwu.com.xiaoduhome.xiaoai.common.XiaoAiConstants.TTS.WAIT_WORD_3;
import static ai.qiwu.com.xiaoduhome.xiaoai.common.XiaoAiConstants.TTS.WELCOME_WORD_DEFAULT;

/**
 * @author 苗权威
 * @dateTime 19-7-5 下午8:52
 */
@Service
@Slf4j
public class XiaoAiService {
//    /**
//     * cpu个数，包括核
//     */
//    private static final int CORE = Runtime.getRuntime().availableProcessors();
    /**
     * 线程池，核心线程书为核数+1,最大线程数为1500,非核心线程空闲时间为2.5s
     */
//    private static final ThreadPoolExecutor POOL = new ThreadPoolExecutor(CORE+1, 1500,
//            2500, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(CORE), new ThreadPoolExecutor.CallerRunsPolicy());
    /**
     * 缓存上次超时的响应结果，下次返回给用户
     */
    private static final ConcurrentHashMap<String, XiaoAiResponse> LAST_OUTTIME_RESULT = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, Byte> LAST_OUTTIME_MARK = new ConcurrentHashMap<>();
//    private static final ConcurrentHashMap<String, String> LAST_AUDIO_PLAY = new ConcurrentHashMap<>();

    public static final Byte OUTTIME_TYPE_0  = 0;
    private static final Byte OUTTIME_TYPE_1  = 1;
    private static final Byte OUTTIME_TYPE_2  = 2;

//    public static final String deviceId = "XIAOAI_nbNGwoZIS8AmRGVqHog==";//XIAOAI_nbNGwoZIS8AmRGVqHog==,XIAOAI_oIOWfYEFbZGFHB5DzoMA==

    /**
     * 处理不同意图,type为0则说明是启动,为1则说明是会话进行中,为2则说明用户退出
     * @param request 封装请求信息
     * @return 响应结果
     */
    public XiaoAiResponse getResponse(XiaoAiRequest request) {
        String userId = getUserId(request);
//        String userId = "%2FScfTGmNbhn%2Fs8KXMJfZY4nQ%2F";
        int type = request.getRequest().getType();
        switch (type) {
            case 0 : return dealWithLaunchRequest(request);
            case 1 : return dealWithIntentRequest(request, userId);
            case 2 : return dealWithEndRequest(userId, request.getType(), Boolean.TRUE.equals(request.getRequest().getIs_monitor()));
            default: return buildTextResponse(SORRY_REPEAT);
        }
    }

//    private XiaoAiResponse postToQiWuInterface(String userId, String userWord) {
//        String audioUrl = null;
//        String textContent = null;
//        CloseableHttpClient httpClient = null;
//        CloseableHttpResponse httpResponse = null;
//        try {
//            httpClient = HttpClientBuilder.create().build();
//            HttpPost post = new HttpPost(XiaoAiConstants.QI_WU_TERMINATE);
//            HashMap<String, String> params = new HashMap<>();
//            params.put("user_id", userId);
//            params.put("text", userWord);
//            params.put("access",XiaoAiConstants.DEVICE_NAME);
//            String paramsJsonFormat = JSON.toJSONString(params);
//            StringEntity body = new StringEntity(paramsJsonFormat, StandardCharsets.UTF_8);
//            post.setHeader("Content-Type", XiaoAiConstants.CONTENT_TYPE);
//            post.setEntity(body);
//
//            httpResponse = httpClient.execute(post);
//            HttpEntity responseBody = httpResponse.getEntity();
//            if (responseBody != null) {
//                String responseContent = EntityUtils.toString(responseBody);
//                //log.info("齐悟接口返回: "+ responseContent);
//                JSONObject responseJsonFormat = JSONObject.parseObject(responseContent);
//                audioUrl = (String) responseJsonFormat.get("audio");
//                textContent = (String) responseJsonFormat.get("text");
//            }
//        } catch (IOException e) {
//            log.info(ERROR_MSG.TERMINATE_IO_EXCEPTION);
//            return buildTextResponse(ERROR_MSG.SORRY_REPEAT);
//        } finally {
//            try {
//                if (httpClient != null) httpClient.close();
//                if (httpResponse != null) httpResponse.close();
//            } catch (IOException e) {
//                log.info(ERROR_MSG.HTTP_CONNECTION_CLOSE_EXCEPTION);
//            }
//        }
//        return buildAudioResponse(audioUrl);
//    }

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
    private XiaoAiResponse buildAudioResponse(String audioUrl) {
        XiaoAiResponse response = buildResponse();
        Response responseInfo = response.getResponse();

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

    private XiaoAiResponse buildAudioTextResponse(String audioUrl, String text) {
        XiaoAiResponse response = buildResponse();
        Response responseInfo = response.getResponse();

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

        ToDisplay display = new ToDisplay();
        display.setType(0);
        display.setText(text);
        responseInfo.setTo_display(display);

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

    /**
     * 处理启动意图，返回欢迎语
     * @param request 请求信息
     * @return 响应信息
     */
    private XiaoAiResponse dealWithLaunchRequest(XiaoAiRequest request) {
//        if (deviceId == null) {
//            deviceId = getUserId(request);
//            log.info("didi-gz3 deviceId:"+deviceId);
//        }
        LAST_OUTTIME_RESULT.remove(getUserId(request));
        LAST_OUTTIME_MARK.remove(getUserId(request));
//        LAST_AUDIO_PLAY.remove(getUserId(request));

        String welcomeWord = null;
        String num = RandomStringUtils.randomNumeric(3);
        Integer type = request.getType();
        switch (type) {
            case 1 : {
//                welcomeWord = WELCOME_SMART_FORMAT_AI;
//                try {
//                    List<String> botList = channel2Bots.get("xiaoai-jiaoyou-audio-child-test");
//                    String name = botList.get(Integer.parseInt(num)%botList.size());
//                    welcomeWord = String.format(welcomeWord, name,name);
//                } catch (Exception e) {
//                    log.error("onLaunch welcomeWord type:{}, error:{}", type, e);
//                    welcomeWord = WELCOME_SMART;
//                }
                welcomeWord = WELCOME_SMART;
                break;
            }
            case 2 : {
//                welcomeWord = WELCOME_NOVEL_FORMAT;
//                try {
//                    List<String> botList = channel2Bots.get("xiaoai-jiaoyou-audio-adult-test");
//                    String name = botList.get(Integer.parseInt(num)%botList.size());
//                    welcomeWord = String.format(welcomeWord, name,name);
//                } catch (Exception e) {
//                    log.error("onLaunch welcomeWord type:{}, error:{}", type, e);
//                    welcomeWord = WELCOME_NOVEL;
//                }
                welcomeWord = WELCOME_NOVEL;
                break;
            }
            case 5 : {
                welcomeWord = WELCOME_FORMAT;
                try {
                    List<String> botList = ScheduleServiceNew.jiaoyouChannel2Bots.get("xiaoai-jiaoyou-audio-adult-test");
                    String name = botList.get(Integer.parseInt(num)%botList.size());
                    welcomeWord = String.format(welcomeWord, name,name);
                } catch (Exception e) {
                    log.error("onLaunch welcomeWord type:{}, error:{}", type, e);
                    welcomeWord = WELCOME;
                }
                break;
            }
        }
        if (welcomeWord == null) welcomeWord = WELCOME_WORD_DEFAULT;
        return buildTextResponse(welcomeWord);
    }

    /**
     * 处理正常会话意图;
     * 将任务提交线程池，监控其执行时间，若1.8s内没有返回则判为超时，提示用户稍等，下一次再将结果返回给用户
     * @param request 请求信息
     * @param userId 用户在小爱上的id
     * @return 响应信息
     */
    private XiaoAiResponse dealWithIntentRequest(final XiaoAiRequest request, final String userId) {
//        if (Boolean.TRUE.equals(request.getRequest().getIs_monitor())) return buildTextResponse("测试返回");
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
    private XiaoAiResponse dealWithEndRequest(String userId, Integer type, Boolean monitor) {
//        LAST_OUTTIME_MARK.remove(userId);
//        LAST_OUTTIME_RESULT.remove(userId);
//        LAST_AUDIO_PLAY.remove(userId);
        if (!monitor) RequestTerminal.backBotRequest(userId, type);
        else {
            log.info("小爱monitor退出请求:"+userId);
        }
        String byeWord;
        switch (type) {
            case 1: byeWord = EndMsg.END_BYE_STORY;break;
            case 2: byeWord = EndMsg.END_BYE_NOVEL;break;
            case 5: byeWord = EndMsg.END_BYE_JIAOYOU;break;
            default: byeWord = EndMsg.END_BYE_DEFAULT;break;
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
            try {
//                log.info(response);
                JSONObject object = JSON.parseObject(response);
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
                                    outWord = EndMsg.END_BYE_STORY;
                                    break;
                                }
                                case 2: {
                                    skillName = SKILL_NOVEL;
                                    outWord = EndMsg.END_BYE_NOVEL;
                                    break;
                                }
                                default: {
                                    skillName = SKILL;
                                    outWord = EndMsg.END_BYE_JIAOYOU;
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
                xiaoAiResponse = buildAudioResponse(changeAudioUrl(path, userId));
//                LAST_AUDIO_PLAY.put(userId, path);
            } catch (Exception e) {
                log.error("没有获取到TTS返回的路径path");
                xiaoAiResponse = buildTextResponse(SORRY_REPEAT);
            }
        }
        LAST_OUTTIME_RESULT.put(userId, xiaoAiResponse);
    }

//    /**
//     * 任务封装类
//     */
//    class Task implements Runnable {
//        private final String userId;
//        private final String userWord;
//        private final Integer type;
//
//        Task(String userId, String userWord, Integer type) {
//            this.userId = userId;
//            this.userWord = userWord;
//            this.type = type;
//        }
//
//        @Override
//        public void run() {
//            XiaoAiResponse xiaoAiResponse;
//            if (userWord == null) {
//                log.info("请求中没有带有用户的话");
//                throw new RequestWordNullException("user word can not be null");
//            }
//            log.info("小爱,用户说的话:{}", userWord);
//            String response;
//            try {
//                response = RequestTerminal.requestTerminate(userId, userWord, type);
//                log.info("小爱,小悟返回:{}", response);
//            } catch (Exception e) {
//                log.error("向终端发出的请求信息失败:{}", e.toString());
//                xiaoAiResponse = buildTextResponse(SORRY_REPEAT);
//                LAST_OUTTIME_RESULT.put(userId, xiaoAiResponse);
//                return;
//            }
//            if (StringUtils.isBlank(response)) {
//                log.error("中控返回结果为空");
//                xiaoAiResponse = buildTextResponse(SORRY_REPEAT);
//            } else {
//                String path;
//                try {
//                    boolean one;
//                    if ((one=userId.equals(deviceId))) {
////                        path = OkHttp3Utils.doPostJsonStr(XiaoAiConstants.TTS_DIDI4, response);
//                        path = OkHttp3Utils.doPostJsonStr(XiaoAiConstants.TTS_DIDI3, response);
//                    } else {
//                        path = OkHttp3Utils.doPostJsonStr(XiaoAiConstants.TTS_DIDI3, response);
//                    }
//                    if (StringUtils.isBlank(path) || path.equals("null")) {
//                        log.error("没有获取到TTS返回的路径path");
//                        LAST_OUTTIME_RESULT.put(userId, buildTextResponse(SORRY_REPEAT));
//                        return;
//                    }
//                    if (one) {
//                        xiaoAiResponse = buildAudioResponse(changeAudioUrl(path, userId));
//                    } else {
//                        xiaoAiResponse = buildAudioResponse(changeAudioUrl(path, userId));
//                    }
//                    LAST_AUDIO_PLAY.put(userId, path);
//                    if (userId.equals(deviceId)) {
//                        DplbotServiceUtil.getPool().submit(() -> {
//                            JSONObject ob = JSONObject.parseObject(response);
//                            JSONArray array = ob.getJSONArray("dialogs");
//                            StringBuilder builder = new StringBuilder(userWord).append(File.separator);
//                            for (Object item: array) {
//                                String str = ((JSONObject) item).getString("text");
//                                builder.append(str.replaceAll("\\{/.*?/}", "")).append(";");
//                            }
//                            WebSocketLog.sendInfo(builder.toString(), "ai1");
//                        });
//                    } else {
//                        DplbotServiceUtil.getPool().submit(() -> {
//                            JSONObject ob = JSONObject.parseObject(response);
//                            JSONArray array = ob.getJSONArray("dialogs");
//                            StringBuilder builder = new StringBuilder(userWord).append(File.separator);
//                            for (Object item: array) {
//                                String str = ((JSONObject) item).getString("text");
//                                builder.append(str.replaceAll("\\{/.*?/}", "")).append(";");
//                            }
//                            WebSocketLog.sendInfo(builder.toString(), "ai2");
//                        });
//                    }
//                } catch (Exception e) {
//                    log.error("没有获取到TTS返回的路径path");
//                    xiaoAiResponse = buildTextResponse(SORRY_REPEAT);
//                }
////                final String word = userWord;
////                DplbotServiceUtil.getPOOL().submit(() -> {
////                    if (deviceId.equals(userId)) {
////                        WebSocketLog.sendInfo(new StringBuilder(word).append(File.separator)
////                                .append(response.getText()).toString(), "ai1");
////                    } else {
////                        WebSocketLog.sendInfo(new StringBuilder(word).append(File.separator)
////                                .append(response.getText()).toString(), "ai2");
////                    }
////                });
//            }
//            LAST_OUTTIME_RESULT.put(userId, xiaoAiResponse);
//        }
//    }

    private String changeAudioUrl(String path, String userId) {
        path = path.replaceAll("%2F", "/");
        return new StringBuilder(TTS_DIDI3_GET_XIAOAI).append(userId).append("&path=").append(path).toString();
    }

    private String changeAudioUrlTemp(String path, String userId) {
        path = path.replaceAll("%2F", "/");
//        return new StringBuilder(TTS_DIDI4_GET_XIAOAI_STATIC).append(path.substring(path.indexOf("audio")+6)).toString();
        return new StringBuilder(TTS_DIDI4_GET_XIAOAI).append(userId).append("&path=").append(path).toString();
    }

//    public void changeAudioPlayMark(String userId) {
//        LAST_AUDIO_PLAY.remove(userId);
//    }

    public static void main(String[] args) {
        System.out.println(Utils.modifyDeviceId("XIAOAI_Ls69sFTjXN/6831N1VVdfw"));
        System.out.println(Utils.modifyDeviceId("XIAOAI_+nzkUO6JnLrmiWSlBQaZ9g"));
        System.out.println(Utils.modifyDeviceId("XIAOAI_Ls69sFTjXN/6831N1VVdfw"));
    }
}
