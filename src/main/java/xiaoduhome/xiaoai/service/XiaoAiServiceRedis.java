package xiaoduhome.xiaoai.service;

import ai.qiwu.com.xiaoduhome.common.Constants;
import ai.qiwu.com.xiaoduhome.common.OkHttp3Utils;
import ai.qiwu.com.xiaoduhome.service.DplbotServiceUtil;
import ai.qiwu.com.xiaoduhome.service.ScheduleServiceNew;
import ai.qiwu.com.xiaoduhome.spirit.SpiritRedisService;
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
import ai.qiwu.com.xiaoduhome.xiaoai.model.response.innerResponse.toSpeak.ToSpeak;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static ai.qiwu.com.xiaoduhome.common.Constants.WelcomeWord.*;
import static ai.qiwu.com.xiaoduhome.common.Constants.WelcomeWord.SKILL;
import static ai.qiwu.com.xiaoduhome.common.Constants.WelcomeWord.SKILL_NOVEL;
import static ai.qiwu.com.xiaoduhome.common.Constants.WelcomeWord.SKILL_SMART;
import static ai.qiwu.com.xiaoduhome.common.Constants.WelcomeWord.WELCOME;
import static ai.qiwu.com.xiaoduhome.controller.XiaoDuHomeController.*;
import static ai.qiwu.com.xiaoduhome.controller.XiaoDuHomeController.WELCOME_FORMAT;
import static ai.qiwu.com.xiaoduhome.controller.XiaoDuHomeController.WELCOME_NOVEL;
import static ai.qiwu.com.xiaoduhome.controller.XiaoDuHomeController.WELCOME_SMART;
import static ai.qiwu.com.xiaoduhome.xiaoai.common.XiaoAiConstants.ERROR_MSG.SORRY_REPEAT;
import static ai.qiwu.com.xiaoduhome.xiaoai.common.XiaoAiConstants.PREFIX_USERID;
import static ai.qiwu.com.xiaoduhome.xiaoai.common.XiaoAiConstants.TTS.WELCOME_WORD_DEFAULT;

/**
 * @author mqw
 * 20-1-9 上午10:43
 */
@Service
@Slf4j
public class XiaoAiServiceRedis {
    private final StringRedisTemplate stringRedisTemplate;

    @Autowired
    public XiaoAiServiceRedis(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 处理不同意图,type为0则说明是启动,为1则说明是会话进行中,为2则说明用户退出
     * @param request 封装请求信息
     * @return 响应结果
     */
    public XiaoAiResponse getResponse(XiaoAiRequest request) {
        String userId = getUserId(request);
//        String userId = "%2FScfTGmNbhn%2Fs8KXMJfZY4nQ%2F";
        int type = request.getRequest().getType();
        if (Boolean.TRUE.equals(request.getRequest().getIs_monitor())) {
//            log.info("小爱monitor请求:"+userId);
            return buildTextResponse("判定为监控请求,过滤");
        }
        switch (type) {
            case 0 : return dealWithLaunchRequest(request, userId);
            case 1 : return dealWithIntentRequest(request, userId);
            case 2 : return dealWithEndRequest(userId, request.getType());
            default: return buildTextResponse(SORRY_REPEAT);
        }
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
    private XiaoAiResponse dealWithLaunchRequest(XiaoAiRequest request, String userId) {
        String welcomeWord = null;
        stringRedisTemplate.delete(SpiritRedisService.PREFIX_REDIS+userId);
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
                    log.error("onLaunch welcomeWord type:{}, error:{}", type, e.toString());
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
        Boolean idle = request.getRequest().getNo_response();
        String redisId = SpiritRedisService.PREFIX_REDIS+userId;

        String baseHostUrl = ScheduleServiceNew.BASE_HOST_URL;
        String audioUrl = ScheduleServiceNew.BASE_AUDIO_URL;
        String cdnUrl = ScheduleServiceNew.CDN_HOST_URL;
        if (ScheduleServiceNew.SERVER_CHANGE) {
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(Constants.SERVER_CHANGE_KEY))) {
                if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(Constants.CAN_NOT_MOVE_USER_KEY+userId))) {
                    stringRedisTemplate.expire(Constants.CAN_NOT_MOVE_USER_KEY+userId, 4, TimeUnit.MINUTES);
                } else {
                    stringRedisTemplate.opsForValue().set(Constants.CAN_NOT_MOVE_USER_KEY+userId, "1", 4, TimeUnit.MINUTES);
                }
                baseHostUrl = ScheduleServiceNew.OLD_BASE_HOST_URL;
                audioUrl = ScheduleServiceNew.OLD_BASE_AUDIO_URL;
                cdnUrl = ScheduleServiceNew.OLD_CDN_HOST_URL;
            } else if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(Constants.CAN_NOT_MOVE_USER_KEY+userId))){
                stringRedisTemplate.expire(Constants.CAN_NOT_MOVE_USER_KEY+userId, 4, TimeUnit.MINUTES);
                baseHostUrl = ScheduleServiceNew.OLD_BASE_HOST_URL;
                audioUrl = ScheduleServiceNew.OLD_BASE_AUDIO_URL;
                cdnUrl = ScheduleServiceNew.OLD_CDN_HOST_URL;
            }
        }
        try {
//            long start = System.currentTimeMillis();
//            log.info("redis get cost:"+(System.currentTimeMillis()-start));
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisId))) {
                String data = stringRedisTemplate.opsForValue().get(redisId);
                log.info("超时redis获取信息："+data);
                if (StringUtils.isNotBlank(data)) {
                    String redisIdleId = SpiritRedisService.PREFIX_REDIS_IDLE+userId;
                    if ("out".equals(data)) {
                        String idleNum = stringRedisTemplate.opsForValue().get(redisIdleId);
                        boolean firstIdle;
                        if ((firstIdle = StringUtils.isBlank(idleNum)) || "1".equals(idleNum)) {
                            try {
                                Thread.sleep(1500);
                            } catch (Exception e){}
//                            data = stringRedisTemplate.opsForValue().get(redisId);
//                            if (!data.equals("out")) {
//                                stringRedisTemplate.delete(redisIdleId);
//                                stringRedisTemplate.delete(redisId);
//                                return buildAudioResponse(changeAudioUrl(data, userId));
//                            }
                            if (firstIdle) idleNum = "1";
                            else idleNum = "2";
                            stringRedisTemplate.opsForValue().set(redisIdleId, idleNum, 15, TimeUnit.SECONDS);
                            return buildTextResponse((RandomUtils.nextInt() & 1) == 1 ? XiaoAiConstants.TTS.WAIT_WORD_1 :
                                    XiaoAiConstants.TTS.WAIT_WORD_2);
                        } else {
                            stringRedisTemplate.delete(redisIdleId);
                            stringRedisTemplate.delete(redisId);
                        }
                    } else {
                        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisIdleId))) stringRedisTemplate.delete(redisIdleId);
                        stringRedisTemplate.delete(redisId);
                        return buildAudioResponse(data);
//                        String botAccount = getBotAccountFromPath(data);
//                        if (StringUtils.isNotBlank(botAccount)) return buildAudioResponse(changeAudioUrlTemp(data, botAccount, baseHostUrl));
                    }
                }
            }
        } catch (Exception e) {
            log.error("xiaoai redis error:"+e);
        }
        if (Boolean.TRUE.equals(idle)) {
            try {
                //log.info("开始等待");
                Thread.sleep(2000);
                //log.info("等待OVER");
            } catch (InterruptedException e) {
                log.warn("等待中断");
            }
            return buildTextResponse((RandomUtils.nextInt() & 1) == 1 ? XiaoAiConstants.TTS.USER_IDLE :
                    XiaoAiConstants.TTS.ANOTHER_USER_IDLE);
        }
        try {
            final String finalBaseHost = baseHostUrl;
            final String finalAudioUrl = audioUrl;
            final String finalCdnUrl = cdnUrl;
//            log.info("XIAOAI_BASE_HOST_URL:"+finalBaseHost);
            Future<XiaoAiResponse> future = DplbotServiceUtil.getPool().submit(() ->
                    task(userId, request.getQuery(), request.getType(), finalBaseHost, finalAudioUrl, finalCdnUrl));
            return future.get(2000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException e) {
            log.error("获取任务执行结果出错:{}", e.toString());
            return buildTextResponse(SORRY_REPEAT);
        } catch (TimeoutException e) {
            log.info("超时,让用户再说一遍");
            stringRedisTemplate.opsForValue().setIfAbsent(redisId, "out", 15, TimeUnit.SECONDS);
            return buildTextResponse(XiaoAiConstants.ERROR_MSG.TIME_OUT);
        } catch (Exception all) {
            log.error("任务执行发生了错误:{}", all.toString());
            return buildTextResponse(SORRY_REPEAT);
        }
    }

    private String getBotAccountFromPath(String path) {
        try {
            int i = path.indexOf("audio");
            if (path.charAt(i+5) == '/') {
                return path.substring(i+6, path.indexOf('/',i+6));
            } else {
                return path.substring(i+8, path.indexOf('%',i+8));
            }
        } catch (Exception e) {
            log.warn("getBotAccountFromPath fail:path={},{}",path,e);
        }
        return null;
    }

    /**
     * 处理用户退出意图,先向中控发送“退出”，之后返回结束语
     * @param userId 用户id
     * @return 返回信息
     */
    private XiaoAiResponse dealWithEndRequest(String userId, Integer type) {
//        if (!monitor) RequestTerminal.backBotRequest(userId, type);
//        else {
//            log.info("小爱monitor退出请求:"+userId);
//        }
        RequestTerminal.backBotRequest(userId, type);
        if (ScheduleServiceNew.SERVER_CHANGE) stringRedisTemplate.delete(Constants.CAN_NOT_MOVE_USER_KEY+userId);
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
//        log.info("小爱,userId:{}", userId);
        if (userId == null) {
            userId = request.getContext().getDevice_id();
            if (userId != null) userId = Utils.modifyDeviceId(userId);
            else userId = Utils.useIPAsItsID();
        }
        try {
            return PREFIX_USERID+ URLEncoder.encode(userId, "UTF-8");
        } catch (Exception e) {
            log.error("xiaoai userid encode error:{}, {}",e, userId);
            return PREFIX_USERID+ userId.replaceAll("/", "");
        }
    }

    private XiaoAiResponse task(String userId, String userWord, Integer type, String baseUrl, String audioUrl, String cdnUrl) {
        XiaoAiResponse xiaoAiResponse;
        if (userWord == null) {
            log.warn("请求中没有带有用户的话");
            throw new RequestWordNullException("user word can not be null");
        }
        String response;
        try {
//            log.info("==================\n小爱,用户话:{}, userId:{}", userWord, userId);
            response = RequestTerminal.requestTerminate(userId, userWord, type, baseUrl);
//            log.info("小爱,小悟返回:{}", response);
        } catch (Exception e) {
            log.error("向终端发出的请求信息失败:{}", e.toString());
            return buildTextResponse(SORRY_REPEAT);
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
                                return buildResponseBeforeLoadout(content);
                            } else {
                                JSONArray dialogs = object.getJSONArray("dialogs");
                                if (dialogs != null && dialogs.size() != 0) {
                                    String str = ((JSONObject)dialogs.get(0)).getString("text");
                                    if (StringUtils.isNotBlank(str)) {
                                        str = str.replaceAll("\\{/.*?/}", "")+skillName;
                                        return buildResponseBeforeLoadout(str);
                                    }
                                }
                                return buildResponseBeforeLoadout(outWord);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("推荐bot退出解析出错:"+e);
            }

            String path;
            try {
                path = OkHttp3Utils.doPostJsonStr(audioUrl+"/api/audio", response);
                if (StringUtils.isBlank(path) || "null".equals(path)) {
                    log.error("没有获取到TTS返回的路径path");
                    return buildTextResponse(SORRY_REPEAT);
                }
                String botAccount = object.getString("aipioneerUsername");
                if (StringUtils.isBlank(botAccount)) {
                    log.warn("没有获取到aipioneerUsername");
                    botAccount = "empty";
                }
                xiaoAiResponse = buildAudioResponse((path = changeAudioUrlTemp(path, botAccount, cdnUrl)));
//                log.info("音频地址:"+path);
                if (Boolean.TRUE.equals(stringRedisTemplate.opsForValue()
                        .setIfPresent(SpiritRedisService.PREFIX_REDIS+userId, path, 15, TimeUnit.SECONDS))) {
                    log.info("xiaoai 超时redis存储成功");
                }
//                LAST_AUDIO_PLAY.put(userId, path);
            } catch (Exception e) {
                log.error("没有获取到TTS返回的路径path");
                xiaoAiResponse = buildTextResponse(SORRY_REPEAT);
            }
        }
        return xiaoAiResponse;
    }

    private String changeAudioUrl(String path, String userId) {
//        path = path.replaceAll("%2F", "/");
//        return new StringBuilder(TTS_DIDI3_GET_XIAOAI).append(userId).append("&path=").append(path).toString();
        return new StringBuilder(ScheduleServiceNew.BASE_HOST_URL).append("9663/api/audio/get?type=").append(userId).append("&path=").append(path).toString();
    }

    private String changeAudioUrlTemp(String path, String botAccount, String cdnHost) {
        if (path.startsWith("a")) {
            return new StringBuilder(cdnHost).append("/api/audio/cache/").append(botAccount).append("/?filename=").append(path.substring(1)).append("&exist=1").toString();
        } else {
            return new StringBuilder(cdnHost).append("/api/audio/cache/").append(botAccount).append("/?filename=").append(path).toString();
        }
    }

    public void theAudioAlreadyPlay(String userId) {
        try {
            userId = URLEncoder.encode(userId, "UTF-8");
        } catch (Exception e){
            log.warn("theAudioAlreadyPlay encode userId failed");
            userId = URLEncoder.encode(userId);
        }
        Boolean result = stringRedisTemplate.delete(SpiritRedisService.PREFIX_REDIS+userId);
        log.info("回调删除:"+result);
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        System.out.println(URLEncoder.encode("XIAOAI_ScfTGmNbhns8KXMJfZY4nQ==", "UTF-8"));
    }
}
