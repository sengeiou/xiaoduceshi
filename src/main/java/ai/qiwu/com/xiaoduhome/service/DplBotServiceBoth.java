package ai.qiwu.com.xiaoduhome.service;

import ai.qiwu.com.xiaoduhome.baidu.dueros.data.request.audioplayer.event.PlaybackFinishedEvent;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.card.ListCard;
import ai.qiwu.com.xiaoduhome.common.BaseHolder;
import ai.qiwu.com.xiaoduhome.common.Constants;
import ai.qiwu.com.xiaoduhome.common.OkHttp3Utils;
import ai.qiwu.com.xiaoduhome.common.Utils;
import ai.qiwu.com.xiaoduhome.common.temple.CollectHistoryDocument;
import ai.qiwu.com.xiaoduhome.common.temple.CollectHistoryUtils;
import ai.qiwu.com.xiaoduhome.common.temple.TempleUtils;
import ai.qiwu.com.xiaoduhome.common.temple.onLunch.LunchDocument;
import ai.qiwu.com.xiaoduhome.common.temple.onLunch.LunchUtils;
import ai.qiwu.com.xiaoduhome.common.temple.play.PlayDocument;
import ai.qiwu.com.xiaoduhome.common.temple.play.PlayUtils;
import ai.qiwu.com.xiaoduhome.common.temple.productInfo.ProductInfoDocument;
import ai.qiwu.com.xiaoduhome.common.temple.productInfo.ProductInfoUtils;
import ai.qiwu.com.xiaoduhome.common.temple.recommand.RecommandDocument;
import ai.qiwu.com.xiaoduhome.common.temple.recommand.RecommendUtil;
import ai.qiwu.com.xiaoduhome.entity.secondary.XiaoDuOrderTB;
import ai.qiwu.com.xiaoduhome.pojo.DialogData;
import ai.qiwu.com.xiaoduhome.pojo.ExitData;
import ai.qiwu.com.xiaoduhome.pojo.OverTimeDetail;
import ai.qiwu.com.xiaoduhome.pojo.UserBehaviorData;
import ai.qiwu.com.xiaoduhome.pojo.data.ProjectData;
import ai.qiwu.com.xiaoduhome.pojo.dpl.MyDocument;
import ai.qiwu.com.xiaoduhome.repository.secondary.XiaoDuOrderTbRepository;
import ai.qiwu.com.xiaoduhome.spirit.SpiritRedisService;
import ai.qiwu.com.xiaoduhome.xiaoai.common.XiaoAiConstants;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import ai.qiwu.com.xiaoduhome.baidu.dueros.bot.BaseBot;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.request.events.LinkAccountSucceededEvent;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.request.events.NextRequired;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.request.pay.event.ChargeEvent;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.OutputSpeech;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.Reprompt;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.directive.Directive;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.directive.SendPart;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.directive.audioplayer.AudioPlayerDirective;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.directive.audioplayer.Play;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.directive.dpl.ExecuteCommands;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.directive.dpl.RenderDocument;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.directive.dpl.commands.ScrollCommand;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.directive.dpl.commands.ScrollToIndexCommand;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.directive.dpl.commands.SetStateCommand;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.directive.dpl.commands.UpdateComponentCommand;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.directive.dpl.event.UserEvent;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.directive.pay.Charge;
import ai.qiwu.com.xiaoduhome.baidu.dueros.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.lang.System;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ai.qiwu.com.xiaoduhome.common.Constants.*;
import static ai.qiwu.com.xiaoduhome.common.Constants.DPL_COMPONENT_ID.*;
import static ai.qiwu.com.xiaoduhome.common.Constants.ErrorMsg.SORRY_UNCATCH;
import static ai.qiwu.com.xiaoduhome.common.Constants.PRODUCT_INTRO.*;
import static ai.qiwu.com.xiaoduhome.common.Constants.WelcomeWord.*;
import static ai.qiwu.com.xiaoduhome.controller.XiaoDuHomeController.*;
import static ai.qiwu.com.xiaoduhome.service.DplbotServiceUtil.POOL;
import static ai.qiwu.com.xiaoduhome.service.DplbotServiceUtil.USER_HISTORY;

/**
 * @author mqw
 */
@Slf4j
public class DplBotServiceBoth extends BaseBot {
    private static final MyDocument FIRST_PAGE;

//    private static final ConcurrentHashMap<String, OverTimeDetail> LAST_OUTTIME_RESULT = new ConcurrentHashMap<>();

    static {
        FIRST_PAGE = JSON.parseObject(DplbotServiceUtil.getDPLTemple("static/json/firstPage.json"), MyDocument.class);
    }

    private static final String FIRST_IMAGE = "https://didi-gz4.jiaoyou365.com/duai/image/Loading.png";

//    private static final String DU_DEVICE_ID = "XIAODU_2bf4f1ade0565b09bef2c529b03e163b";

    private final int type;

    private final static Reprompt WAIT_WORD = new Reprompt(new OutputSpeech(OutputSpeech.SpeechType.PlainText, "晓悟正在等你的回复"));

//    private final RedisTemplate<String, OverTimeDetail> redisTemplate;

//    private final RedisTemplate<String, XiaoDuNoScreenStore> redisTemplate;

    private final StringRedisTemplate stringRedisTemplate;

    private final String channel;

    public DplBotServiceBoth(HttpServletRequest request, Integer type,StringRedisTemplate stringRedisTemplate) throws IOException {
        super(request);
        this.type = type;
        switch (type) {
            case 1:  channel = "jiaoyou-audio-child-test";break;
            case 2:  channel = "jiaoyou-audio-adult-test";break;
            case 5:  channel = "jiaoyou-audio-test";break;
            default: channel = "baidu-screen-jiaoyou-audio-test";
        }
        this.stringRedisTemplate = stringRedisTemplate;
        //privateKey为私钥内容,0代表你的Bot在DBP平台debug环境，1或者其他整数代表online环境,
        // botMonitor对象已经在bot-sdk里初始化，可以直接调用
        this.botMonitor.setEnvironmentInfo(PRIVATE_KEY, 1);
        this.botMonitor.setMonitorEnabled(true);
    }

    @Override
    public Response onLaunch(ai.qiwu.com.xiaoduhome.baidu.dueros.data.request.LaunchRequest launchRequest) {
//        getRequest().getContext().getScreen() == null
        if (isNoScreenRequest()) {
//            String userId = customUserId();
            String redisId;
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey((redisId=SpiritRedisService.PREFIX_REDIS+customUserId())))) {
                stringRedisTemplate.delete(redisId);
            }
            String welcomeWord;
            String skillName;
            String num = RandomStringUtils.randomNumeric(3);
            switch (type) {
                case 1 : {
//                    welcomeWord = WELCOME_SMART_FORMAT;
//                    try {
//                        List<String> botList = channel2Bots.get("jiaoyou-audio-child-test");
//                        String name = botList.get(Integer.parseInt(num)%botList.size());
//                        welcomeWord = String.format(welcomeWord, name,name);
//                    } catch (Exception e) {
//                        log.error("onLaunch welcomeWord type:{}, error:{}", type, e);
//                        welcomeWord = WELCOME_SMART;
//                    }
                    welcomeWord = WELCOME_SMART;
                    skillName = SKILL_SMART;
                    break;
                }
                case 2 : {
//                    welcomeWord = WELCOME_NOVEL_FORMAT;
//                    try {
//                        List<String> botList = channel2Bots.get("jiaoyou-audio-adult-test");
//                        String name = botList.get(Integer.parseInt(num)%botList.size());
//                        welcomeWord = String.format(welcomeWord, name,name);
//                    } catch (Exception e) {
//                        log.error("onLaunch welcomeWord type:{}, error:{}", type, e);
//                        welcomeWord = WELCOME_NOVEL;
//                    }
                    welcomeWord = WELCOME_NOVEL;
                    skillName = SKILL_NOVEL;
                    break;
                }
                case 3 : welcomeWord = WelcomeWord.WELCOME_STORY; skillName = SKILL_STORY; break;
                case 4 : welcomeWord = WelcomeWord.WELCOME_LITERATURE; skillName = SKILL_LITERATURE; break;
                default: {
                    welcomeWord = WELCOME_FORMAT;
                    try {
                        List<String> botList = ScheduleServiceNew.jiaoyouChannel2Bots.get("jiaoyou-audio-adult-test");
                        String name = botList.get(Integer.parseInt(num)%botList.size());
                        welcomeWord = String.format(welcomeWord, name,name);
                    } catch (Exception e) {
                        log.error("onLaunch welcomeWord type:{}, error:{}", type, e);
                        welcomeWord = WELCOME;
                    }
                    skillName = SKILL;
                    break;
                }
            }
//            StandardCard standardCard = new StandardCard(skillName, welcomeWord);
//            standardCard.setImage(FIRST_IMAGE);
            OutputSpeech outputSpeech = new OutputSpeech(OutputSpeech.SpeechType.PlainText, welcomeWord);
            return new Response(outputSpeech);
        } else {
            RenderDocument renderDocument = new RenderDocument(FIRST_PAGE);
            this.addDirective(renderDocument);
            OutputSpeech outputSpeech = new OutputSpeech(OutputSpeech.SpeechType.PlainText, WelcomeWord.WELCOME);
            // 初次进入时创建用户的行为信息,包括历史记录,送花数,收藏记录
            // 历史记录,收藏记录,送花数,郊游天下id
            final String accessToken = getAccessToken();
            final String userId = getUserId();
//            UserBehaviorData data;
            DplbotServiceUtil.POOL.submit(() -> {
                if (accessToken == null) {
                    DplbotServiceUtil.onLunchFillUserBehaviorDataInMaster(userId, channel, null, false);
                    DplbotServiceUtil.noticeOtherServer(5, null, null, null, userId, null, channel, false);
                } else {
                    UserBehaviorData data = USER_HISTORY.get(userId);
                    String jytUserId = null;
                    if (data != null) jytUserId = data.getJytUserId();
                    if (jytUserId == null) jytUserId = DplbotServiceUtil.getUserIdByToken(accessToken, channel, userId);
                    UserBehaviorData behaviorData = DplbotServiceUtil.onLunchFillUserBehaviorDataInMaster(userId, channel, jytUserId, true);
                    log.info("onLunch data:"+behaviorData);
                    log.info("onLunch history:"+USER_HISTORY);
                    DplbotServiceUtil.noticeOtherServer(5, null, null, null, userId, jytUserId, channel, true);
                }
            });
//            if ((data= USER_HISTORY.get(userId)) == null) {
//                if (accessToken == null) {
//                    DplbotServiceUtil.onLunchFillUserBehaviorData(userId, channel, null);
//                } else {
//                    String jytUserId = DplbotServiceUtil.getUserIdByToken(accessToken, channel, userId);
//                    if (StringUtils.isNotBlank(jytUserId)) {
//                        DplbotServiceUtil.onLunchFillUserBehaviorData(userId, channel, jytUserId);
//                    }
//                }
//            } else if (accessToken != null && data.getJytUserId() == null){
//                userId=DplbotServiceUtil.getUserIdByToken(accessToken, channel, userId);
//                if (StringUtils.isNotBlank(userId)) data.setJytUserId(userId);
//            }
            setSessionAttribute(BACK_TRACE, null);
            return new Response(outputSpeech);
        }
    }

    @Override
    protected Response onInent(ai.qiwu.com.xiaoduhome.baidu.dueros.data.request.IntentRequest intentRequest) {
        if (isNoScreenRequest()) {
            final String userId = customUserId();

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

            String redisId = SpiritRedisService.PREFIX_REDIS+userId;
//            final String deviceUserId = PREFIX_USERID + getDeviceId();

            try {
                if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisId))) {
                    String data = stringRedisTemplate.opsForValue().get(redisId);
                    log.warn("超时redis获取信息："+data);
                    if (StringUtils.isNotBlank(data)) {
                        String redisIdleId = SpiritRedisService.PREFIX_REDIS_IDLE+userId;
                        if (data.equals("out")) {
                            String idleNum = stringRedisTemplate.opsForValue().get(redisIdleId);
                            boolean firstIdle;
                            if ((firstIdle = StringUtils.isBlank(idleNum)) || "1".equals(idleNum)) {
                                try {
                                    Thread.sleep(2000);
                                } catch (Exception e){}
//                                data = stringRedisTemplate.opsForValue().get(redisId);
//                                if (StringUtils.isNotBlank(data) && !data.equals("out")) {
//                                    stringRedisTemplate.delete(redisIdleId);
//                                    stringRedisTemplate.delete(redisId);
//                                    Directive play = buildNoScreenPlayDirec(dealAudioUrl(data),  AudioPlayerDirective.PlayBehaviorType.REPLACE_ALL);
//                                    addDirective(play);
//                                    return new Response();
//                                }
                                if (firstIdle) idleNum = "1";
                                else idleNum = "2";
                                stringRedisTemplate.opsForValue().set(redisIdleId, idleNum, 15, TimeUnit.SECONDS);
                                return buildErrorResponse((RandomUtils.nextInt() & 1) == 1 ? XiaoAiConstants.TTS.WAIT_WORD_1 :
                                        XiaoAiConstants.TTS.WAIT_WORD_2);
                            } else {
                                stringRedisTemplate.delete(redisIdleId);
                                stringRedisTemplate.delete(redisId);
                            }
                        } else {
                            stringRedisTemplate.delete(redisId);
                            stringRedisTemplate.delete(redisIdleId);
                            Directive play = buildNoScreenPlayDirec(data,  AudioPlayerDirective.PlayBehaviorType.REPLACE_ALL);
                            addDirective(play);
                            return new Response();
//                            String botAccount = getBotAccountFromPath(data);
//                            if (StringUtils.isNotBlank(botAccount)) {
//                                Directive play = buildNoScreenPlayDirec(dealAudioUrlTemp(data, getBotAccountFromPath(data), cdnUrl),  AudioPlayerDirective.PlayBehaviorType.REPLACE_ALL);
//                                Directive play = buildNoScreenPlayDirec(data,  AudioPlayerDirective.PlayBehaviorType.REPLACE_ALL);
//                                addDirective(play);
//                                return new Response();
//                            }
                        }
                    } else {
                        stringRedisTemplate.delete(redisId);
                    }
                }
            } catch (Exception e) {
                log.error("xiaodu redis error:"+e);
            }

            try {
                final String finalBaseHost = baseHostUrl;
                final String finalAudioUrl = audioUrl;
                final String finalCdnUrl = cdnUrl;
//                log.info("BASE_HOST_URL:"+finalBaseHost);
                Future<Response> future = DplbotServiceUtil.getPOOL().submit(() ->
                        dealNoScreenIntentTemp(intentRequest, userId, finalBaseHost, finalAudioUrl, finalCdnUrl));
                return future.get(2500, TimeUnit.MILLISECONDS);
            } catch (InterruptedException | ExecutionException e) {
                log.error("获取任务执行结果出错:{}", ExceptionUtils.getStackTrace(e));
                return buildErrorResponse(SORRY_UNCATCH);
            } catch (TimeoutException e) {
                log.warn("超时,让用户再说一遍:{}");
                stringRedisTemplate.opsForValue().setIfAbsent(redisId, "out", 15, TimeUnit.SECONDS);
                return buildErrorResponse(SORRY_UNCATCH);
            } catch (Exception all) {
                log.error("任务执行发生了错误:{}", ExceptionUtils.getStackTrace(all));
                return buildErrorResponse(SORRY_UNCATCH);
            }
        } else {
            String userId = getUserId();
            String redisId = SpiritRedisService.PREFIX_REDIS+userId;
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisId))) {
                String trace = getSessionAttribute(BACK_TRACE);
                if (StringUtils.endsWith(trace, File.separator)) {
                    String lastData;
                    if ((lastData=stringRedisTemplate.opsForValue().get(redisId))!=null) {
                        if ("out".equals(lastData)) {
                            String redisIdleId = SpiritRedisService.PREFIX_REDIS_IDLE+userId;
                            String idleNum = stringRedisTemplate.opsForValue().get(redisIdleId);
                            boolean firstIdle;
                            if ((firstIdle = StringUtils.isBlank(idleNum)) || idleNum.equals("1")) {
                                try {
                                    Thread.sleep(2000);
                                } catch (Exception e){}
                                lastData = stringRedisTemplate.opsForValue().get(redisId);
                                if (StringUtils.isNotBlank(lastData) && !lastData.equals("out")) {
                                    stringRedisTemplate.delete(redisIdleId);
                                    stringRedisTemplate.delete(redisId);
                                    return dealScreenOutTimeResult(lastData, getUserId(), channel);
                                }
                                if (firstIdle) idleNum = "1";
                                else idleNum = "2";
                                stringRedisTemplate.opsForValue().set(redisIdleId, idleNum, 10, TimeUnit.SECONDS);
                                return buildErrorResponse((RandomUtils.nextInt() & 1) == 1 ? XiaoAiConstants.TTS.WAIT_WORD_1 :
                                        XiaoAiConstants.TTS.WAIT_WORD_2);
                            } else {
                                stringRedisTemplate.delete(redisIdleId);
                                stringRedisTemplate.delete(redisId);
                            }
                        } else {
                            return dealScreenOutTimeResult(lastData, getUserId(), channel);
                        }
                    } else {
                        log.info("outtime lastData is null");
                    }
                } else {
                    stringRedisTemplate.delete(redisId);
                }
            }
            try {
                Future<Response> future = DplbotServiceUtil.getPOOL().submit(() -> dealDefaultIntent(intentRequest, userId,
                        null, channel));
                return future.get(2700, TimeUnit.MILLISECONDS);
            }  catch (InterruptedException | ExecutionException e) {
                log.warn("获取任务执行结果出错:{}", ExceptionUtils.getStackTrace(e));
                return buildErrorResponse(SORRY_UNCATCH);
            } catch (TimeoutException e) {
                log.info("超时,让用户再说一遍:{}",e.toString());
                stringRedisTemplate.opsForValue().setIfAbsent(redisId, "out", 10, TimeUnit.SECONDS);
                return buildErrorResponse(SORRY_UNCATCH);
            } catch (Exception all) {
                log.warn("任务执行发生了错误:{}", ExceptionUtils.getStackTrace(all));
                return buildErrorResponse(SORRY_UNCATCH);
            }
        }
    }

    private Response dealScreenOutTimeResult(String data, String userId, String channelId) {
        String[] arr = data.split("⧩");
        log.info("outTime:"+Arrays.toString(arr));
        JSONObject object = JSON.parseObject(arr[0]);
        String path = arr[1];
        String botAccount = object.getString("aipioneerUsername");
        String beforeBotAccount = getSessionAttribute("botAcc");
        if (!botAccount.equals(beforeBotAccount)) {
            DplbotServiceUtil.updateTheUserBehaviorDataMark(userId, botAccount, getAccessToken(), getSessionAttribute("userId"), channelId);
            String productName = ScheduleServiceNew.getWorkNameByBotAccount(botAccount);
            if (StringUtils.isBlank(productName)) productName = "交游天下";
            PlayDocument document = PlayUtils.getPlayDocument(productName, DplbotServiceUtil.getTheActorsWithWords(object.getJSONArray("dialogs"), botAccount),
                    dealAudioUrl(path), botAccount);

            RenderDocument render = new RenderDocument(document);
            addDirective(render);
            DplbotServiceUtil.updateTheUserHistory(userId, botAccount, getAccessToken(), channelId, null);
            String trace = getSessionAttribute(BACK_TRACE);
            if (trace == null || trace.length() != 2) trace = "00";
            trace = new StringBuilder(trace).append(botAccount).append(File.separator).toString();
            setSessionAttribute(BACK_TRACE, trace);
            setSessionAttribute("botAcc", botAccount);
        } else {
            String productName = ScheduleServiceNew.getWorkNameByBotAccount(botAccount);
            if (StringUtils.isBlank(productName)) productName = "交游天下";
            PlayDocument document = PlayUtils.getPlayDocument(productName,
                    DplbotServiceUtil.getTheActorsWithWords(object.getJSONArray("dialogs"), botAccount), dealAudioUrl(path), botAccount);

            RenderDocument render = new RenderDocument(document);
            addDirective(render);
        }
        return new Response();
    }

    private Response dealNoScreenIntentTemp(ai.qiwu.com.xiaoduhome.baidu.dueros.data.request.IntentRequest intentRequest, String userId, String baseUrl,
                                            String audioUrl, String cdnUrl) {
//        String intentName = getIntent().getName();
//        if ("sendFlower".equals(intentName)) {
//            return dealNoScreenSendFlowerIntent(userId, null);
//        } else if ("load".equals(intentName)) {
//            Card card = new LinkAccountCard();
//            Response response = buildErrorResponse("请在小度App上完成登录");
//            response.setCard(card);
//            return response;
//        }

        try {
            final ai.qiwu.com.xiaoduhome.baidu.dueros.data.request.Query query = intentRequest.getQuery();
            final String userWord = query.getOriginal();
//            if (query != null) userWord = query.getOriginal();
            if (userWord == null){
                log.error("小度没有传来任何话语");
                return buildErrorResponse(ErrorMsg.SORRY_UNCATCH);
            }
//            log.info("小度.用户说的话：{}",userWord);

            String channel;
            switch (type) {
                case 1 :
                case 3 : channel = "jiaoyou-audio-child-test";break;
                case 2 :
                case 4 : channel = "jiaoyou-audio-adult-test";break;
                default: channel = "jiaoyou-audio-test";break;
            }
            String response = RequestTerminal.requestTerminateUrl(userWord, userId, channel, baseUrl);
//            log.info("小度,小悟返回：{},",response);
            if (StringUtils.isBlank(response)) {
                return buildErrorResponse(ErrorMsg.SORRY_UNCATCH);
            }
//            String mark = RandomStringUtils.randomAlphabetic(3);
//            AudioBoxData pathData = new AudioBoxData();
//            OkHttp3Utils.asyncPost(XiaoAiConstants.TTS_DIDI3, response, pathData);
            JSONObject object = JSON.parseObject(response);
            try {
                JSONArray commands = object.getJSONArray("commands");
                if (commands != null && commands.size() != 0) {
                    String txt;
                    for (Object ob: commands) {
                        txt = ((JSONObject) ob).getString("text");
                        int i;
                        if (StringUtils.isNotBlank(txt) && (i=txt.indexOf("out")) != -1) {
                            log.warn("小度，推荐bot退出：{},{}",userWord,userId);
                            String content = txt.substring(i+3, txt.indexOf("☚")).trim();
                            String byeWord = null;
                            String skillName = null;
                            switch (type) {
                                case 1: {
                                    byeWord = EndMsg.END_BYE_STORY;
                                    skillName = SKILL_SMART;
                                    break;
                                }
                                case 2: {
                                    byeWord = EndMsg.END_BYE_NOVEL;
                                    skillName = SKILL_NOVEL;
                                    break;
                                }
                                case 5: {
                                    byeWord = EndMsg.END_BYE_JIAOYOU;
                                    skillName = SKILL;
                                    break;
                                }
                            }
                            if (byeWord == null) byeWord = EndMsg.END_BYE_Adult;
                            if (skillName == null) skillName = SKILL_DEFAULT;
                            if (StringUtils.isBlank(content)) {
                                JSONArray dialogs = object.getJSONArray("dialogs");
                                if (dialogs != null && dialogs.size() != 0) {
                                    String str = ((JSONObject)dialogs.get(0)).getString("text");
                                    if (StringUtils.isNotBlank(str)) {
                                        str = str.replaceAll("\\{/.*?/}", "")+skillName;
                                        RequestTerminal.backRequest(userId, type);
                                        return buildLogOutResponse(str);
                                    }
                                }
                                RequestTerminal.backRequest(userId, type);
                                return buildLogOutResponse(byeWord);
                            } else {
                                content = content.replaceAll("\\{/.*?/}", "")+skillName;
                                RequestTerminal.backRequest(userId, type);
                                return buildLogOutResponse(content);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("推荐bot退出解析出错:"+e);
            }
            String path = OkHttp3Utils.doPostJsonStr(audioUrl+"/api/audio", response);
//            String path = OkHttp3Utils.doPostJsonStr("http://didi-gz5.jiaoyou365.com:8190/api/audio", response);
            if (StringUtils.isBlank(path) || "null".equals(path)) {
                log.error("小度没有获取到音频链接");
                return buildErrorResponse(ErrorMsg.SORRY_UNCATCH);
            }
//            final JSONObject ob = JSONObject.parseObject(response);
//            String botAcc = ob.getString("aipioneerUsername");
//            String beforeBotAccount = getSessionAttribute("botAcc");
//            if (StringUtils.isBlank(beforeBotAccount) || !beforeBotAccount.equals(botAcc)) {
//                setSessionAttribute("botAcc", botAcc);
//                data.getAttributes().put("botAcc",botAcc);
//            }

            String botAccount = object.getString("aipioneerUsername");
            if (StringUtils.isBlank(botAccount)) {
                log.warn("没有获取到aipioneerUsername");
                botAccount = "empty";
            }
            Directive play = buildNoScreenPlayDirec((path=dealAudioUrlTemp(path, botAccount, cdnUrl)),  AudioPlayerDirective.PlayBehaviorType.REPLACE_ALL);
//            log.info("小度,语音："+path);
//            Directive play = buildNoScreenPlayDirec(dealAudioUrl(path),  AudioPlayerDirective.PlayBehaviorType.REPLACE_ALL);
            addDirective(play);
//            if (userId.equals(DU_DEVICE_ID)) {
//                DplbotServiceUtil.getPOOL().submit(() -> {
//                    JSONArray array = ob.getJSONArray("dialogs");
//                    StringBuilder builder = new StringBuilder(userWord).append(File.separator);
//                    for (Object item: array) {
//                        String str = ((JSONObject) item).getString("text");
//                        builder.append(str.replaceAll("\\{/.*?/}", "")).append(";");
//                    }
//                    WebSocketLog.sendInfo(builder.toString(), "du1");
//                });
//            } else {
//                DplbotServiceUtil.getPOOL().submit(() -> {
//                    JSONArray array = ob.getJSONArray("dialogs");
//                    StringBuilder builder = new StringBuilder(userWord).append(File.separator);
//                    for (Object item: array) {
//                        String str = ((JSONObject) item).getString("text");
//                        builder.append(str.replaceAll("\\{/.*?/}", "")).append(";");
//                    }
//                    WebSocketLog.sendInfo(builder.toString(), "du2");
//                });
//            }
            if (Boolean.TRUE.equals(stringRedisTemplate.opsForValue()
                    .setIfPresent(SpiritRedisService.PREFIX_REDIS+userId, path, 15, TimeUnit.SECONDS))) {
                log.warn("xiaodu 超时redis存储成功");
            }
            return new Response();
        } catch (Exception e) {
            log.warn("中控返回结果为空:"+e);
            return buildErrorResponse(ErrorMsg.SORRY_UNCATCH);
        }
    }

    private String dealAudioUrl(String path) {
        return ScheduleServiceNew.BASE_AUDIO_URL+"/api/audio/get?path="+path;
    }

    private String dealAudioUrlTemp(String path, String botAccount, String cdnUrl) {
//        return ScheduleServiceNew.BASE_HOST_URL+"9663/api/audio/get?path="+path;
        if (path.startsWith("a")) {
            return new StringBuilder(cdnUrl).append("/api/audio/cache/").append(botAccount).append("/?filename=").append(path.substring(1)).append("&exist=1").toString();
        }
        return new StringBuilder(cdnUrl).append("/api/audio/cache/").append(botAccount).append("/?filename=").append(path).toString();
    }

//    private Response dealNoScreenSendFlowerIntent(String userId, String channelId) {
//        if (getAccessToken() == null) {
//            return buildErrorResponse("该功能需要登录");
//        }
//        String num = getSlot("sys.number");
//        OutputSpeech outputSpeech;
//        String confirmationStatus = getIntent().getConfirmationStatus().name();
//        log.info("槽位确认状态:{}",confirmationStatus);
//        if ("NONE".equals(confirmationStatus)) {
//            if (num == null) {
//                log.info("确认送花个数");
//                ask("sys.number");
//                outputSpeech = new OutputSpeech(OutputSpeech.SpeechType.PlainText, "请问您要送几朵花");
//                setExpectSpeech(true);
//            } else {
//                log.info("确认最终送花意图");
//                setConfirmIntent();
//                outputSpeech = new OutputSpeech(OutputSpeech.SpeechType.PlainText,
//                        new StringBuilder("您要送").append(num).append("朵花是吗").toString());
//                setExpectSpeech(true);
//            }
//            return new Response(outputSpeech);
//        } else if ("CONFIRMED".equals(confirmationStatus)) {
//            log.info("确认");
//            int unitPrice = Integer.parseInt(ScheduleServiceNew.FLOWER_UNIT_PRICE);
//            String amount = String.valueOf(unitPrice*Integer.parseInt(num)/100.0);
//            String orderId = userId + System.currentTimeMillis();
//            String appUserId = getSessionAttribute("userId");
//            if (appUserId == null) {
//                appUserId = DplbotServiceUtil.getUsUserId(userId,getAccessToken(), channelId);
//                if (appUserId == null) {
//                    log.error("送花时获取用户app_user_id失败");
//                    return buildErrorResponse("不好意思出了点问题,请再说一遍吧");
//                }
//            }
//            String botId = getSessionAttribute("botAcc");
//            if (StringUtils.isNotBlank(botId)) {
//                XiaoDuOrderTbRepository repository = BaseHolder.getBean("xiaoDuOrderTbRepository");
//                XiaoDuOrderTB tb= buildNoScreenBeforePayPojo(botId, appUserId, userId, unitPrice, Integer.parseInt(num), orderId);
//                if (tb == null) {
//                    log.warn("buildNoScreenBeforePayPojo return null");
//                    return new Response();
//                }
//                XiaoDuOrderTB response = repository.save(tb);
//
//                if (response.getId() != null) {
//                    String desc = new StringBuilder("感谢您赏赐给该作品的").append(num).append("朵鲜花").toString();
//                    String name = ScheduleService.PRODUCT_ID_DETAIL.get(botId).getName();
//                    if (name != null) desc = new StringBuilder("感谢您赏赐给").append(name)
//                            .append("的").append(num).append("朵鲜花").toString();
//                    Charge charge = new Charge(amount, orderId, num+"朵鲜花", desc);
//                    charge.setToken(new StringBuilder(botId).append(File.separator).append(num).toString());
//                    addDirective(charge);
//                    return buildErrorResponse("请到小度APP上完成支付");
//                } else {
//                    log.error("订单没有插入到xioadu_order_tb");
//                    return buildErrorResponse(ERROR_SEND_FLOWER);
//                }
//            }else {
//                log.error("无法获取到botID");
//                return buildErrorResponse("只能在作品里才可以送花");
//            }
//        } else {
//            log.info("否定");
//            return  buildErrorResponse("好的,已为您终止");
//        }
//    }

    private Play buildNoScreenPlayDirec(String audioUrl, AudioPlayerDirective.PlayBehaviorType type) {
        return new Play(type, audioUrl, 0);
    }

    /**
     *
     * @param idsToken 当前页面作品的id集合
     * @return
     */
    private Response dealOpenNum(String idsToken, int collectNum) {
        String[] tokenList = new String(Base64.getDecoder().decode(idsToken)).split(File.separator);
        int curIndex = 0;
        String nextPageIndex = getSessionAttribute(NEXT_PAGE_INDEX);
        if (StringUtils.isNotBlank(nextPageIndex)) {
            curIndex = Integer.parseInt(nextPageIndex)-1;
            curIndex = curIndex < 0 ? 0 : curIndex;
        }
        collectNum = collectNum-curIndex*CO_HI_PAGE_SIZE;
        if (collectNum <= 0 || collectNum > tokenList.length) return buildErrorResponse("请说出您要打开的作品在当前页面所显示的数字");
        String botID = tokenList[collectNum-1];
        ProjectData detail = ScheduleServiceNew.getProjectByBotAccount(botID);
        String productName = correctName(detail.getName());
        String enterProductWord = new StringBuilder(Constants.PRE_PRODUCT_PLAY).append(productName).toString();
        log.info("进入作品语句:{}", enterProductWord);
        String data = null;
        try {
            data = RequestTerminal.requestTerminateTemp(enterProductWord, getUserId(), channel);
        } catch (IOException e) {
            log.error("请求终端接口出错:", e.toString());
        }
        if (data == null) {
            return buildErrorResponse(SORRY_UNCATCH);
        }


        String path = null;
        try {
            path = OkHttp3Utils.doPostJsonStr(ScheduleServiceNew.BASE_HOST_URL+"9663/api/audio", data);
        } catch (Exception e) {
            log.warn("没有获取到TTS返回地址");
        }
        if (StringUtils.isBlank(path) || "null".equals(path)) {
            log.error("小度没有获取到音频链接");
            return buildErrorResponse(ErrorMsg.SORRY_UNCATCH);
        }
        PlayDocument document = PlayUtils.getPlayDocument(productName,
                DplbotServiceUtil.getTheActorsWithWords(JSON.parseObject(data).getJSONArray("dialogs"), botID),
                dealAudioUrl(path), botID);
        RenderDocument render = new RenderDocument(document);
        this.setSessionAttribute(PRE_COLLE, null);
        String trace = getSessionAttribute(BACK_TRACE);
        if (trace == null || trace.length() != 2) trace = "00";
        String backTrace = new StringBuilder(trace).append(detail.getBotAccount()).append(File.separator).toString();
        this.setSessionAttribute(BACK_TRACE, backTrace);
        this.addDirective(render);
        return new Response();
    }

    @Override
    protected Response onUserEvent(UserEvent userEvent) {
        try {
            //log.info("onUserEvent事件触发,token:{}, componentId:{}",userEvent.getToken(),userEvent.getPayload().getComponentId());
            if (getAccessToken() != null && getSessionAttribute("userId") == null) {
                String userId = DplbotServiceUtil.getUsUserId(getUserId(),getAccessToken(), channel);
                setSessionAttribute("userId", userId);
            }
            String componentId = userEvent.getPayload().getComponentId();
            String componentPrefix;
            if ("audio".equals(componentId)){
                if (getSessionAttribute("end") != null) {
                    try {
                        setSessionAttribute("end", null);
                        String endId = SpiritRedisService.PREFIX_REDIS+getUserId()+"_end";
                        String recommand = stringRedisTemplate.opsForValue().get(endId);
                        log.info("end play ended: recommand->"+recommand);
                        if (StringUtils.isNotBlank(recommand)) {
                            stringRedisTemplate.delete(endId);
                            String path = OkHttp3Utils.doPostJsonStr(ScheduleServiceNew.BASE_HOST_URL+"9663/api/audio", recommand);
                            if (StringUtils.isBlank(path) || "null".equals(path)) {
                                log.error("小度没有获取到音频链接");
                                return buildErrorResponse(ErrorMsg.SORRY_UNCATCH);
                            }
                            ExitData exitData = JSON.parseObject(recommand, ExitData.class);
                            String text = null;
                            for (DialogData data: exitData.getDialogs()) {
                                if (data.getBotAccount().equals(exitData.getAipioneerUsername())) {
                                    text = data.getText();
                                    break;
                                }
                            }
                            if (StringUtils.isBlank(text)) return new Response();
                            RecommandDocument document = displayFunnyRecommandPage(text, dealAudioUrl(path));
                            if (document != null) {
                                RenderDocument render = new RenderDocument(document);
                                addDirective(render);
                                setSessionAttribute(PRE_COLLE, document.getRecommendIdList());
                                setSessionAttribute(BACK_TRACE, "06");
                            }
                            return new Response();
                        }
                    } catch (Exception e) {
                        this.setExpectSpeech(true);
                        log.warn(ExceptionUtils.getStackTrace(e));
                    }
                } else {
                    setExpectSpeech(true);
                }
            }
            else if (Constants.PRE_PRODUCT_IMG.equals((componentPrefix = componentId.substring(0, 5)))) {
                // 作品图片点击事件
                String accessToken = getAccessToken();
                String userId = getUserId();
                String productName = componentId.substring(5);

                ProjectData productDetail = ScheduleServiceNew.getProjectByName(productName);
                if (productDetail == null) {
                    return buildErrorResponse("目前不支持该作品");
                }

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                String dateString = formatter.format(productDetail.getCreateTime());
                String time = new StringBuilder(PRODUCT_INTRO.CREATE_TIME).append(dateString).toString();
                String author = new StringBuilder(PRODUCT_INTRO.AUTHOR).append(productDetail.getAuthorName()).toString();
                StringBuilder labels = new StringBuilder(PRODUCT_INTRO.LABEL);
                for (String label: productDetail.getLabels()) {
                    labels.append(PRODUCT_INTRO.SPACE).append(label);
                }
                String intro = productDetail.getIntro();
                String enterProName = new StringBuilder(Constants.PRE_ENTER_PRODUCT).append(productName).toString();

                // "start product" container button
                String flowers, flowerReward,collectText, collectUrl, collectComId;
                if (accessToken != null) {
                    // flower container button
                    Long flowerCount = DplbotServiceUtil.getUserSendFlowerNum(productDetail.getBotAccount(), userId,
                            channel,accessToken, getSessionAttribute("userId"));
                    flowers = USER_SEND_FLOWER+flowerCount;
                    flowerReward = new StringBuilder(FLOWE).append(productDetail.getBotAccount()).toString();

                    // collection container button
                    boolean collec = DplbotServiceUtil.didUserCollecThisProduct(productDetail.getBotAccount(), userId,
                            accessToken, getSessionAttribute("userId"));
                    if (collec) {
                        collectText = COLLECED;
                        collectUrl = COLLECTED_IMG_URL;
                        collectComId = new StringBuilder(UN_COLLEC_ID).append(productDetail.getBotAccount()).toString();
                    } else {
                        collectText = UN_COLLEC;
                        collectUrl = UN_COLLECTED_IMG_URL;
                        collectComId = new StringBuilder(COLLEC_ID).append(productDetail.getBotAccount()).toString();
                    }
                } else {
                    flowers = FLOWER;
                    flowerReward = FLOWER_LOAD;

                    collectText = UN_COLLEC;
                    collectUrl = UN_COLLECTED_IMG_URL;
                    collectComId = FLOWER_LOAD;
                }

                ProductInfoDocument document = ProductInfoUtils.getProductINfoDocument(productDetail.getBannerImgUrl(), correctName(productName), time, author, labels.toString(),
                        intro, enterProName, flowerReward, flowers, collectComId, collectUrl, collectText);

                RenderDocument renderDocument = new RenderDocument(document);
                this.addDirective(renderDocument);
                //this.addDirective(new PushStack());
                String trace = getSessionAttribute(BACK_TRACE);
                log.info("before trace------------:{}",trace);
                StringBuilder builder = new StringBuilder();
                if (trace == null || trace.length() < 2) {
                    builder.append("00");
                } else if (trace.length()>2) {
                    builder.append(trace, 0, 2);
                } else {
                    builder.append(trace);
                }

                trace = builder.append(productDetail.getBotAccount()).toString();
                log.info("after trace------------:{}",trace);
                this.setSessionAttribute(BACK_TRACE, trace);
                return new Response();
            } else if (Constants.PRE_ENTER_PRODUCT.equals(componentPrefix)) {
                String productName = componentId.substring(5);
                String enterProductWord = new StringBuilder(Constants.PRE_PRODUCT_PLAY).append(productName).toString();
                log.info("进入作品语句:{}", enterProductWord);
                String data = null;
                try {
                    data = RequestTerminal.requestTerminateTemp(enterProductWord, getUserId(), channel);
                } catch (IOException e) {
                    log.error("请求终端接口出错:", e.toString());
                }
                if (data == null) {
                    return buildErrorResponse(SORRY_UNCATCH);
                }

                String path = null;
                try {
                    path = OkHttp3Utils.doPostJsonStr(ScheduleServiceNew.BASE_HOST_URL+"9663/api/audio", data);
                } catch (Exception e) {
                    log.warn("没有获取到TTS返回地址");
                }
                if (StringUtils.isBlank(path) || "null".equals(path)) {
                    log.error("小度没有获取到音频链接");
                    return buildErrorResponse(ErrorMsg.SORRY_UNCATCH);
                }
                String botID = ScheduleServiceNew.getBotAccountByWorkname(productName);
                PlayDocument document = PlayUtils.getPlayDocument(productName,
                        DplbotServiceUtil.getTheActorsWithWords(JSON.parseObject(data).getJSONArray("dialogs"), botID),
                        dealAudioUrl(path), botID);

                RenderDocument render = new RenderDocument(document);
                this.setSessionAttribute(PRE_COLLE, null);
                String trace = getSessionAttribute(BACK_TRACE);
                if (trace == null || trace.length()<2) trace = "00";
                else if (trace.length()>2) trace = trace.substring(0,2);
                this.setSessionAttribute(BACK_TRACE, new StringBuilder(trace).append(botID).append(File.separator).toString());
                this.addDirective(render);
            } else if (FLOWE.equals(componentPrefix)) {
                if (FLOWER_LOAD.equals(componentId)) {
                    return load();
                } else {
//                Buy buy = new Buy(FLOWER_PRODUCT_ID);
//                String token = new String(Base64.getEncoder().encode(componentId.substring(5).getBytes()), StandardCharsets.UTF_8);
//                buy.setToken(token);
//                addDirective(buy);
//                OutputSpeech outputSpeech = new OutputSpeech(OutputSpeech.SpeechType.PlainText, REWARD_FLOWER);
//                return new Response(outputSpeech);
                    Charge charge = DplbotServiceUtil.sendOneFlowerByCharge(getUserId(), getSessionAttribute("userId"),
                            getAccessToken(), getSessionAttribute("trace"),channel);
                    if (charge != null) {
                        addDirective(charge);
                        return buildErrorResponse("扫描屏幕上的二维码完成支付");
                    }
                    return new Response();
                }
            } else if ("loadingPage".equals(componentId)) {
                RenderDocument renderDocument = new RenderDocument(LunchUtils.getLunchDocument());
                addDirective(renderDocument);
                return new Response();

//                this.addDirective(new PushStack());
//                addDirective(new SendPart("lunch"));
            } else if (PRE_COLLE.equals(componentPrefix)) {
                boolean isUnCollect = "Un".equals(componentId.substring(5,7));
                final String botId = componentId.substring(7);
                if (isUnCollect) {
                    //取消收藏
                    if (DplbotServiceUtil.userUnCollectTheBot(getUserId(), botId, channel, getSessionAttribute("userId"), getAccessToken())) {
                        changeIconIdTextAfterCollect(false, botId);
                    }
                } else {
                    //收藏
                    if (DplbotServiceUtil.userCollectTheBot(getUserId(), botId, channel, getSessionAttribute("userId"),getAccessToken())) {
                        changeIconIdTextAfterCollect(true, botId);
                    }
                }
            } else if (PRE_CATEGORY.equals(componentPrefix)) {
                String category = componentId.substring(5);
                // 分类推荐页面相同
                RecommandDocument document = RecommendUtil.getCategory(0, category);
                if (document == null) return buildErrorResponse("不好意思该分类还没有作品");
                RenderDocument render = new RenderDocument(document);
                this.addDirective(render);
                this.setSessionAttribute("category",category);
                this.setSessionAttribute("trace","75");
            } else if ("playButton".equals(componentId)) {
                return buildErrorResponse("您可以直接对我说我要送花或者我要收藏或取消收藏");
            } else if ("lunchCollect".equals(componentId)) {
                return dealOpenCollectClick();
            } else if ("lunchHistory".equals(componentId)) {
                return dealHistoryClick();
            }
//        else if ("funnyChange".equals(componentId)) {
//            ExecuteCommands executeCommands = new ExecuteCommands();
//            AutoPageCommand page = new AutoPageCommand();
//            page.setComponentId("funnyPage");
//            page.setDurationInMillisecond(2000);
//            executeCommands.addCommand(page);
//            this.addDirective(executeCommands);
//        }
        } catch (Exception e) {
            log.error("onUserEvent error:{}", ExceptionUtils.getStackTrace(e));
        }
        return new Response();
    }

    @Override
    protected Response onSessionEnded(ai.qiwu.com.xiaoduhome.baidu.dueros.data.request.SessionEndedRequest sessionEndedRequest) {
        switch (sessionEndedRequest.getReason()) {
            case ERROR: dealWithError(sessionEndedRequest); break;
            case USER_INITIATED: log.info(EndMsg.USER_SAY_CANCEL); break;
            case EXCEEDED_MAX_REPROMPTS: log.info(EndMsg.CAN_NOT_UNDERSTAND);break;
        }
        if (isNoScreenRequest()) {
            String userId = customUserId();
            String byeWord = EndMsg.END_BYE_Adult;
            switch (type) {
                case 2: byeWord = EndMsg.END_BYE_NOVEL;break;
                case 1: byeWord = EndMsg.END_BYE_STORY;break;
                case 3: byeWord = EndMsg.END_BYE_CHILD;break;
                case 5: byeWord = EndMsg.END_BYE_JIAOYOU;break;
            }
            OutputSpeech outputSpeech = new OutputSpeech(OutputSpeech.SpeechType.PlainText, byeWord);
            RequestTerminal.backRequest(userId, type);
            if (ScheduleServiceNew.SERVER_CHANGE) stringRedisTemplate.delete(Constants.CAN_NOT_MOVE_USER_KEY+userId);
//            try {
//                if (DU_DEVICE_ID.equals(deviceId)) {
//                    RequestTerminal.backRequest(DU_DEVICE_ID, type);
//                } else {
//                    RequestTerminal.backRequestDU2(deviceId, type);
//                }
//            } catch (Exception e) {
//                log.warn("用户退出向中控发出退出指令出错");
//            }
            return new Response(outputSpeech);
        } else {
            String byeWord = EndMsg.END_BYE_JIAOYOU;
            OutputSpeech outputSpeech = new OutputSpeech(OutputSpeech.SpeechType.PlainText, byeWord);
            RequestTerminal.backRequest(getUserId(), type);
            return new Response(outputSpeech);
        }
    }

    private void dealWithError(ai.qiwu.com.xiaoduhome.baidu.dueros.data.request.SessionEndedRequest request) {
        switch (request.getError().getType()) {
            case INTERNAL_ERROR: log.info(ErrorMsg.INTERNAL_ERROR); break;
            case INVALID_RESPONSE: log.info(ErrorMsg.INVALID_RESPONSE); break;
            case DEVICE_COMMUNICATION_ERROR: log.info(ErrorMsg.DEVICE_COMMUNICATION_ERROR); break;
        }
        log.info("错误信息 : "+ request.getError().getMessage());
    }

    private Response dealHistoryClick() {
        CollectHistoryDocument document = DplbotServiceUtil.getHistory(getUserId(), getAccessToken(),
                getSessionAttribute("userId"), 0, channel);
        if (document == null) return buildErrorResponse("不好意思，您太久没玩记录被清空了");
        RenderDocument render = new RenderDocument(document);
        addDirective(render);
        setSessionAttribute(PRE_COLLE,document.getIdToken());
        setSessionAttribute("recommand", null);
        setSessionAttribute(NEXT_PAGE_INDEX, null);
        setSessionAttribute(BACK_TRACE, "02");
        OutputSpeech outputSpeech = new OutputSpeech(OutputSpeech.SpeechType.PlainText, "正在为您打开历史记录");
        return new Response(outputSpeech);
    }

    private Response dealOpenCategory(String word) {
        LunchDocument document = LunchUtils.getCategoryDocument();
        if (document == null) return new Response();
        RenderDocument render = new RenderDocument(document);
        this.addDirective(render);
        this.setSessionAttribute("trace","07");
        return buildErrorResponse(word);
    }

    private Response dealOpenCollectClick() {
        if (getAccessToken() == null) {
            OutputSpeech outputSpeech = new OutputSpeech(OutputSpeech.SpeechType.PlainText, "该功能需要登录,请说小度小度,登录");
            return new Response(outputSpeech);
        } else {
            String appUserId = getSessionAttribute("userId");
            if (appUserId == null) {
                appUserId = DplbotServiceUtil.getUsUserId(getUserId(), getAccessToken(), channel);
                if (appUserId == null) {
                    return new Response();
                }
            }
            CollectHistoryDocument document = DplbotServiceUtil.getCollectPage(getUserId(), getAccessToken(),
                    appUserId, 0, channel);
            if (document != null) {
                RenderDocument render = new RenderDocument(document);
                addDirective(render);
                setSessionAttribute(NEXT_PAGE_INDEX, null);
                setSessionAttribute(PRE_COLLE,document.getIdToken());
                setSessionAttribute(BACK_TRACE, "01");
                OutputSpeech outputSpeech = new OutputSpeech(OutputSpeech.SpeechType.PlainText, "正在为您打开收藏夹");
                return new Response(outputSpeech);
            }
        }
        return new Response();
    }

//    @Override
//    protected Response onBuyEvent(BuyEvent buyEvent) {
//        com.baidu.dueros.data.request.buy.event.JYTUserResponse payload = buyEvent.getPayload();
//        String result = payload.getPurchaseResult().getPurchaseResult();
//        log.info("此次支付结果:"+result+";msg: "+payload.getMessage());
//        if (result.equals("SUCCESS")) {
//            log.info("支付成功");
//            POOL.submit(new SendFlowerThread(buyEvent));
//            POOL.submit(new SendFlowerBuyToXiaoduTB(buyEvent));
//            return buildErrorResponse("感谢大人赏的花");
//        }
//        return buildErrorResponse("哎呀，没有支付成功");
//    }


    private ExecuteCommands changeIconIdTextAfterCollect(boolean collect, String botId) {
        ExecuteCommands executeCommands = new ExecuteCommands();
        UpdateComponentCommand update = new UpdateComponentCommand();
        String srcValue;
        String textValue;
        String idValue;
        String previousClickId;
        String unCollectId = new StringBuilder(UN_COLLEC_ID).append(botId).toString();
        String collectId = new StringBuilder(COLLEC_ID).append(botId).toString();
        if (collect) {
            srcValue = COLLECTED_IMG_URL;
            textValue = COLLECED;
            idValue = unCollectId;
            previousClickId = collectId;
        } else {
            srcValue = UN_COLLECTED_IMG_URL;
            textValue = UN_COLLEC;
            idValue = collectId;
            previousClickId = unCollectId;
        }

        update.setComponentId(previousClickId);
        update.setDocument(TempleUtils.getCollectIcon(idValue, srcValue, textValue));
        executeCommands.addCommand(update);
        this.addDirective(executeCommands);
        return executeCommands;
    }

    private ExecuteCommands changeCollectIconInPlay(boolean collect) {
        ExecuteCommands executeCommands = new ExecuteCommands();
        SetStateCommand setState = new SetStateCommand();
        setState.setComponentId("playCollect");
        setState.setState("src");
        if (collect) {
            setState.setValue(COLLECTED_IMG_URL);
        }else {
            setState.setValue(UN_COLLECTED_IMG_URL);
        }
        executeCommands.addCommand(setState);
        return executeCommands;
    }

    private Response load() {
        ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.card.Card card = new ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.card.LinkAccountCard();
        Response response = buildErrorResponse(LOAD_INTRO);
        response.setCard(card);
        return response;
    }

//    private Integer getUserSendFlowerNum(String productId, String accessToken) {
//        HashMap<String, String> params = new HashMap<>();
//        params.put("module", "works");
//        params.put("aipioneerUsername", productId);
//        HashMap<String, String> headers = new HashMap<>();
//        headers.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
//        headers.put("Authorization", new StringBuilder(BEARER).append(accessToken).toString());
//        CodeMsgData response;
//        try {
//            String flowerRes = OkHttp3Utils.doGet(FLOWER_INTERFACE, params, headers);
//            response = JSON.parseObject(flowerRes, CodeMsgData.class);
//        } catch (IOException e) {
//            log.error("请求用户送花数出错");
//            return null;
//        }
//        log.info("调用获取用户送花数接口结果:{}", response);
//        if (response == null || response.getCode() != 1) return null;
//        return response.getData().getCount();
//    }


//    private List<CollecProduct> getUserProductList(String accessToken) {
//        HashMap<String, String> params = new HashMap<>();
//        params.put("module", "works");
//        HashMap<String, String> headers = new HashMap<>();
//        headers.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
//        headers.put("Authorization", new StringBuilder(BEARER).append(accessToken).toString());
//        UserCollecResult result;
//        try {
//            String resStr = OkHttp3Utils.doGet(XIAOWU_API.USER_CLOOEC_LIST, params, headers);
//            result = JSON.parseObject(resStr, UserCollecResult.class);
//        } catch (Exception e) {
//            log.error("调用获取用户收藏作品列表接口出错:{}", e.toString());
//            return null;
//        }
//        if (result.getCode() == 1) return result.getData();
//        else {
//            log.error("获取用户收藏作品列表出错,错误码:{}, 错误信息:{}", result.getCode(), result.getMsg());
//            return null;
//        }
//    }

    private static String correctName(String name) {
        int brace = name.indexOf("{");
        if (brace != -1) name = name.substring(0, brace);
        return name;
    }

    @Override
    protected Response onLinkAccountSucceededEvent(LinkAccountSucceededEvent linkAccountSucceededEvent) {
        log.info("登录成功返回信息:{}",linkAccountSucceededEvent);
        String accessToken = getAccessToken();
        String jytUserId;
        if (!isNoScreenRequest()) {
            log.info("检测到有屏登录");
            String userId = getUserId();
            jytUserId = DplbotServiceUtil.getUserIdByToken(accessToken, channel, userId);
            DplbotServiceUtil.onLunchFillUserBehaviorData(userId, channel, jytUserId, true);
            DplbotServiceUtil.noticeOtherServer(5, null, null, false, userId, jytUserId, channel, false);
            if (jytUserId == null)
                log.warn("登录后获取用户的app id 失败");
        } else {
            jytUserId = DplbotServiceUtil.getUserIdByToken(accessToken, null, getUserId());
            if (jytUserId == null)
                log.warn("登录后获取用户的app id 失败");
        }
        setSessionAttribute("userId", jytUserId);
        return buildErrorResponse("您已登录成功");
    }

    private String getEventType(String requestStr) {
        int typeIndex = requestStr.lastIndexOf("type");
        int comma = requestStr.indexOf(',', typeIndex);
        return requestStr.substring(typeIndex, comma);
    }

    private Response buildErrorResponse(String errorMsg) {
        OutputSpeech outputSpeech = new OutputSpeech(OutputSpeech.SpeechType.PlainText, errorMsg);
        return new Response(outputSpeech);
    }

    private Response buildLogOutResponse(String errorMsg) {
        OutputSpeech outputSpeech = new OutputSpeech(OutputSpeech.SpeechType.PlainText, errorMsg);
        endDialog();
        return new Response(outputSpeech);
    }

    private Response buildNoScreenListCardResponse(String content, String botAccount) {
        Pattern pattern = Pattern.compile(Utils.getRegexSeperate());
        List<String> actors = DplbotServiceUtil.seperateActor(content, pattern);
        String[] actorWord = DplbotServiceUtil.seperateActorWord(content, pattern);
        int i = 0;
        ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.card.ListCard listCard = new ListCard();
        for (String actor: actors) {
            ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.card.StandardCardInfo cardInfo = new ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.card.StandardCardInfo(actor, actorWord[i++].replaceAll("<br>","\n"));
            cardInfo.setImage(ScheduleServiceNew.getAsideImgUrl(botAccount, actor));
            listCard.getList().add(cardInfo);
        }
//        content = buildText(content);
        return new Response(null, listCard, WAIT_WORD);
    }

    private Response buildNoScreenTextCardREsponse(String content) {
        content = buildText(content);
        ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.card.TextCard textCard = new ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.card.TextCard(content);
        return new Response(null, textCard, WAIT_WORD);
    }

    private String buildText(String text) {
        Pattern pattern = Pattern.compile(Utils.getRegexSeperate());
        StringBuilder builder = new StringBuilder();
        List<String> actors = DplbotServiceUtil.seperateActor(text, pattern);
        String[] word = DplbotServiceUtil.seperateActorWord(text, pattern);
        int i = 0;
        for (String actor: actors) {
            builder.append(actor).append(":").append(word[i].replaceAll("<br>", "\n")).append("\n");
            i++;
        }
        if (builder.length() == 0) builder.append(text);
        return builder.toString();
    }

    private void backToBotIntro(String botAccount, String channelId) {
        ProjectData data = ScheduleServiceNew.getProjectByBotAccount(botAccount);

        // product name
        String productName = correctName(data.getName());

        // product create time
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(data.getCreateTime());
        String createTime = PRODUCT_INTRO.CREATE_TIME + dateString;

        // product author
        String author = PRODUCT_INTRO.AUTHOR + data.getAuthorName();

        // product labels
        StringBuilder labels = new StringBuilder(PRODUCT_INTRO.LABEL);
        for (String label: data.getLabels()) {
            labels.append(PRODUCT_INTRO.SPACE).append(label);
        }

        // product introduction
        String intro = data.getIntro();

        // "start product" container button
        String enterProName = Constants.PRE_ENTER_PRODUCT + productName;

        String accessToken = getAccessToken();
        String flowers, flowerReward,collectText, collectUrl, collectComId;
        if (accessToken != null) {
            // flower container button
            Long count = DplbotServiceUtil.getUserSendFlowerNum(botAccount, getUserId(), channelId, accessToken,
                    getSessionAttribute("userId"));
            flowers = USER_SEND_FLOWER+count;
            flowerReward = FLOWE + data.getBotAccount();

            // collection container button
            boolean collec = DplbotServiceUtil.doesUserCollectThisBot(data.getBotAccount(), accessToken, getUserId(),
                    channelId, getSessionAttribute("userId"));
            if (collec) {
                collectText = COLLECED;
                collectUrl = COLLECTED_IMG_URL;
                collectComId = UN_COLLEC_ID + data.getBotAccount();
            } else {
                collectText = UN_COLLEC;
                collectUrl = UN_COLLECTED_IMG_URL;
                collectComId = COLLEC_ID + data.getBotAccount();
            }
        } else {
            flowers = FLOWER;
            flowerReward = FLOWER_LOAD;

            collectText = UN_COLLEC;
            collectUrl = UN_COLLECTED_IMG_URL;
            collectComId = FLOWER_LOAD; // 提示登录
        }

        ProductInfoDocument document = ProductInfoUtils.getProductINfoDocument(data.getBannerImgUrl(), correctName(productName), createTime, author, labels.toString(),
                intro, enterProName, flowerReward, flowers, collectComId, collectUrl, collectText);
        RenderDocument renderDocument = new RenderDocument(document);
        this.addDirective(renderDocument);
        String trace = getSessionAttribute(BACK_TRACE);
        trace = trace.substring(0, trace.length()-1);
        this.setSessionAttribute(BACK_TRACE, trace);
    }

    // 异步发送“退出”给终端
    private void sendCancellToTerminate(String userId){
        RequestTerminal.backRequest(userId, type);
    }

    private Response dealJumpIntent(String userId, String numStr, String channelId) {
        String trace = getSessionAttribute("trace");
        if (trace == null || trace.length() < 2) {
            return buildErrorResponse("小悟跳不动了");
        }
        int index = DplbotServiceUtil.extractNum(numStr);
        char pageSign = trace.charAt(1);
        Response response;
        switch (pageSign) {
            case '1' : response = dealCollectHistoryJump(index, true, userId, channelId);break;
            case '2' : response = dealCollectHistoryJump(index, false, userId, channelId);break;
            case '4' : response = dealRankJump(index, channelId);break;
            case '5' : response = dealCategoryJump(index);break;
            default  : response = buildErrorResponse("该页面不支持跳转");
        }
        return response;
    }

    private Response dealRankJump(int index, String channelId) {
        index = index == 0 ? index : index-1;
        RecommandDocument document;
        if (index == 0) document = RecommendUtil.getRankDocumentFirstPage(channelId);
        else document = RecommendUtil.getRankOtherPageDocument(index, channelId);
        if (document == null) {
            return buildErrorResponse("超出了范围,无法跳转");
        }
        RenderDocument render = new RenderDocument(document);
        addDirective(render);
        String cmt = String.valueOf(index+1);
        setSessionAttribute(NEXT_PAGE_INDEX,cmt);
        setSessionAttribute(PRE_COLLE, document.getRecommendIdList());
        return new Response();
    }

    private Response dealCategoryJump(int index) {
        index = index == 0 ? index : index-1;
        String category = getSessionAttribute("category");
        if (category == null) {
            return buildErrorResponse("无法跳转,请说返回然后重新进入当前分类");
        }
        RecommandDocument document = RecommendUtil.getCategory(index, category);
        if (document == null) {
            return buildErrorResponse("超出了范围,无法跳转");
        }
        RenderDocument render = new RenderDocument(document);
        addDirective(render);
        String cmt = String.valueOf(index+1);
        setSessionAttribute(NEXT_PAGE_INDEX,cmt);
        return new Response();
    }

    private Response dealCollectHistoryJump(int index, boolean collect, String userId, String channelId) {
        if (index < 0) return buildErrorResponse("这个数字超出了范围");
        String nextPageIndex = getSessionAttribute(NEXT_PAGE_INDEX);
        if (StringUtils.isBlank(nextPageIndex) && index <= 1) return buildErrorResponse(
                "您已经在第一页了，请看右上角当前页和总页数的提示");
        else if (String.valueOf(index).equals(nextPageIndex)) return buildErrorResponse("您已经在第"+index+"页了，请看右上角当前页和总页数的提示");
        index = index <= 1 ? 0 : index-1;
        int start = index*CO_HI_PAGE_SIZE;
        CollectHistoryDocument document;
        if (collect) document = DplbotServiceUtil.getCollectPage(userId, getAccessToken(),
                getSessionAttribute("userId"), start, channelId);
        else document = DplbotServiceUtil.getHistory(userId, getAccessToken(), getSessionAttribute("userId"),start,channelId);
        if (document != null) {
            if ("1".equals(document.getIdToken())) {
                log.info("dealCollectHistoryJump:-->"+1);
                return buildErrorResponse("您要打开的页数超出了范围");
            }
            RenderDocument render = new RenderDocument(document);
            addDirective(render);
            String cmt = String.valueOf(index+1);
            setSessionAttribute(NEXT_PAGE_INDEX,cmt);
            setSessionAttribute(PRE_COLLE, document.getIdToken());
            return new Response();
        } else {
            log.info("dealCollectHistoryJump:-->document is null");
            return buildErrorResponse("超出了范围无法跳到那一页");
        }
    }

    private Response dealStartIntent(String userId, ai.qiwu.com.xiaoduhome.baidu.dueros.data.request.IntentRequest intentRequest, String channelId) {
        String trace = getSessionAttribute("trace");
        if (trace != null && trace.length() > 2 && !StringUtils.endsWith(trace,File.separator)) {
            String botId = trace.substring(2);
            String enterName = "打开" +
                    ScheduleServiceNew.getBotAccountByWorkname(botId);
            return dealDefaultIntent(intentRequest, userId, enterName, channelId);
        }else {
            backToHome();
        }
        return new Response();
    }

    private Response dealSendFlowerIntent(String userId, String numStr) {
        if (getAccessToken() == null) {
            return buildErrorResponse("该功能需要登录,请说小度小度,登录");
        }
        int num = DplbotServiceUtil.extractNum(numStr);
        int unitPrice = Integer.parseInt(ScheduleServiceNew.FLOWER_UNIT_PRICE);
        String amount = String.valueOf(unitPrice*num/100.0);
        String orderId = userId +"_"+ System.currentTimeMillis();
        String appUserId = getSessionAttribute("userId");
        if (appUserId == null) {
            appUserId = DplbotServiceUtil.getUsUserId(userId,getAccessToken(), channel);
            if (appUserId == null) {
                log.error("送花时获取用户app_user_id失败");
                return buildErrorResponse("不好意思出了点问题,请再说一遍吧");
            }
        }
        String botId;
        String trace = getSessionAttribute(BACK_TRACE);
        if (trace != null && trace.length() > 2) {
            boolean inPlay;
            if ((inPlay=StringUtils.endsWith(trace, File.separator))) {
                botId = trace.substring(2, trace.length()-1);
            }else {
                botId = trace.substring(2);
            }

            XiaoDuOrderTB orderTB = buildBeforePayPojo(botId, appUserId, userId, unitPrice, num, orderId);
            if (orderTB == null) {
                return buildErrorResponse(ERROR_SEND_FLOWER);
            }
            XiaoDuOrderTbRepository repository = BaseHolder.getBean("xiaoDuOrderTbRepository");
            XiaoDuOrderTB response = repository.save(orderTB);
            if (response.getId() != null) {
                String desc = new StringBuilder("感谢您赏赐给该作品的").append(num).append("朵鲜花").toString();
                String name = ScheduleServiceNew.getWorkNameByBotAccount(botId);
                if (name != null) desc = new StringBuilder("感谢您赏赐给").append(name)
                        .append("的").append(num).append("朵鲜花").toString();
                Charge charge = new Charge(amount, orderId, "鲜花", desc);
                charge.setToken(new StringBuilder(botId).append(File.separator).append(num).append(File.separator).append(inPlay?1:0).toString());
                addDirective(charge);
                return buildErrorResponse("请扫描二维码完成支付");
            } else {
                log.warn("订单没有插入到xioadu_order_tb");
                return buildErrorResponse(ERROR_SEND_FLOWER);
            }
        }else {
            log.warn("无法获取到botID");
            return buildErrorResponse(ERROR_SEND_FLOWER);
        }
    }

    private Response dealCollectOrUnCollectIntent(String userId, String intentName, String workName) {
        if (getAccessToken() == null) {
            return buildErrorResponse("该功能需要登录,请说小度小度,登录");
        }
        boolean collect = "collect".equals(intentName);
        boolean inProductInfo = false;
        boolean inPlayInfo = false;
        String botAccount;
        String trace = getSessionAttribute("trace");
        if (trace == null || trace.length() <= 2) {
            if (workName != null) {
                workName = extractWorkName(workName);
                if (StringUtils.isBlank(workName)) return buildErrorResponse("您需要指明要收藏的作品名称");
                botAccount = ScheduleServiceNew.getBotAccountByWorkname(workName);
                if (StringUtils.isBlank(botAccount)) return new Response();
            } else {
                return buildErrorResponse("您需要指明要收藏的作品名称");
            }
        } else {
            if (StringUtils.endsWith(trace, File.separator)) {
                botAccount = trace.substring(2, trace.length()-1);
                inPlayInfo = true;
            }
            else {
                inProductInfo = true;
                botAccount = trace.substring(2);
            }
        }
        try {
            String appUserId = getSessionAttribute("userId");
            if (appUserId == null) appUserId = DplbotServiceUtil.getUsUserId(userId,getAccessToken(), channel);
            boolean alreadyCollected = DplbotServiceUtil.doesUserCollectThisBot(botAccount, getAccessToken(), userId,
                    channel, appUserId);
            if (appUserId != null) {
                if (alreadyCollected) {
                    if (collect) return buildErrorResponse("您之前就已经收藏过该作品啦,看来你真的很喜欢它");
                    else {
                        boolean result = DplbotServiceUtil.userUnCollectTheBot(userId, botAccount,  channel, appUserId, getAccessToken());
                        if (!result) return new Response();
                        String botName = ScheduleServiceNew.getWorkNameByBotAccount(botAccount);
                        if (inProductInfo) {
                            ExecuteCommands commands = changeIconIdTextAfterCollect(false, botAccount);
                            addDirective(commands);
                        } else if (inPlayInfo) {
                            changeCollectIconInPlay(false);
                        } else {
                            return buildErrorResponse("已将"+botName+"移出您的收藏列表");
                        }
                        if (botName == null) return buildErrorResponse(UN_COLLECT_BOT_WORD);
                        else return buildErrorResponse(String.format(UN_COLLECT_BOT_WORD_MG,botName));
                    }
                } else {
                    if (collect) {
                        boolean result = DplbotServiceUtil.userCollectTheBot(userId, botAccount, channel, appUserId, getAccessToken());
                        if (!result) return new Response();
                        String word = ScheduleServiceNew.getWorkNameByBotAccount(botAccount);
                        if (inProductInfo) {
                            ExecuteCommands commands = changeIconIdTextAfterCollect(true, botAccount);
                            addDirective(commands);
                        } else if (inPlayInfo) {
                            changeCollectIconInPlay(true);
                        } else {
                            return buildErrorResponse("已将"+word+"加入您的收藏列表");
                        }
                        if (word == null) return buildErrorResponse(COLLECT_BOT_WORD);
                        else return buildErrorResponse(String.format(COLLECT_BOT_WORD_MG, correctName(word)));
                    }else {
                        return buildErrorResponse("您还未收藏过该作品,它不在您的收藏列表内");
                    }
                }
            }else {
                return buildErrorResponse(SORRY_UNCATCH);
            }
        } catch (Exception e) {
            log.error("收藏/取消收藏出错:{}",e.toString());
            return buildErrorResponse(SORRY_UNCATCH);
        }
    }

    private Response dealRankIntent(String channelId) {
        RecommandDocument document = RecommendUtil.getRankDocumentFirstPage(channelId);
        RenderDocument render = new RenderDocument();
        render.setDocument(document);
        addDirective(render);
        String token = document.getRecommendIdList();
        setSessionAttribute(PRE_COLLE, token);
        setSessionAttribute(NEXT_PAGE_INDEX, null);
        setSessionAttribute(BACK_TRACE, "04");
        return buildErrorResponse("正在为您打开排行榜");
    }

    private void dealRecommandIntent(OverTimeDetail detail, String userId) {
        RecommandDocument recommandDocument = RecommendUtil.getRecommendDocument(0, channel);
        RenderDocument render = new RenderDocument();
        render.setDocument(recommandDocument);
        addDirective(render);
        detail.getDirectives().add(render);
        String token = recommandDocument.getRecommendIdList();
        if (token != null) {
            setSessionAttribute("recommand", token);
            setSessionAttribute(PRE_COLLE, null);
            setSessionAttribute(NEXT_PAGE_INDEX, null);

            detail.getAttributes().put(NEXT_PAGE_INDEX, null);
            detail.getAttributes().put("recommand", token);
            detail.getAttributes().put(PRE_COLLE, null);
        }
        setSessionAttribute(BACK_TRACE, "03");
        detail.getAttributes().put(BACK_TRACE,"03");
//        LAST_OUTTIME_RESULT.put(userId, detail);
    }

    private Response dealHistoryIntent(String channelId) {
        CollectHistoryDocument document = null;
        try {
            document = DplbotServiceUtil.getHistory(getUserId(), getAccessToken(),
                    getSessionAttribute("userId"),0, channelId);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
        if (document == null) {
            return buildErrorResponse("没有更多了");
        } else if ("1".equals(document.getIdToken())) {
            return buildErrorResponse("没有找到您的游戏记录");
        }
        RenderDocument render = new RenderDocument(document);
        addDirective(render);
        //dplBotService.addDirective(new PushStack());
        setSessionAttribute(PRE_COLLE,document.getIdToken());
//        setSessionAttribute("recommand", null);
//        setSessionAttribute(NEXT_PAGE_INDEX, null);
        setSessionAttribute(BACK_TRACE, "02");
        return buildErrorResponse("正在为您打开历史记录");
    }

    private Response dealNextPageIntent(String userId, String channelId) {
        String signPage = getSessionAttribute("trace");
        if (signPage == null || signPage.length() < 2) {
            return buildErrorResponse("哎呀,小悟累啦,滑不动了");
        }
        char sign = signPage.charAt(1);
        switch (sign) {
            case '1': {
                String recommendNextCount = getSessionAttribute(NEXT_PAGE_INDEX);
                if (recommendNextCount == null) {
                    recommendNextCount = "1";
                }
                int count = Integer.parseInt(recommendNextCount);
                int start = count*CO_HI_PAGE_SIZE;
                String jytUserId = getSessionAttribute("userId");
                if (StringUtils.isBlank(jytUserId)) {
                    jytUserId = DplbotServiceUtil.getUsUserId(userId, getAccessToken(), channelId);
                    setSessionAttribute("userId", jytUserId);
                }
                CollectHistoryDocument document = DplbotServiceUtil.getCollectPage(userId,getAccessToken(),
                        jytUserId, start, channelId);
                if (document != null) {
                    RenderDocument render = new RenderDocument(document);
                    addDirective(render);
                    String cmt = String.valueOf(count+1);
                    setSessionAttribute(NEXT_PAGE_INDEX,cmt);
                    setSessionAttribute(PRE_COLLE, document.getIdToken());
                }
                return new Response();
            }
            case '2': {
                String recommendNextCount = getSessionAttribute(NEXT_PAGE_INDEX);
                if (recommendNextCount == null) {
                    recommendNextCount = "1";
                }
                int count = Integer.parseInt(recommendNextCount);
                int start = count*CO_HI_PAGE_SIZE;
                CollectHistoryDocument document = DplbotServiceUtil.getHistory(userId, getAccessToken(),
                        getSessionAttribute("userId"),start,channelId);
                if (document != null) {
                    RenderDocument render = new RenderDocument(document);
                    addDirective(render);
                    String cmt = String.valueOf(count+1);
                    setSessionAttribute(NEXT_PAGE_INDEX,cmt);
                    setSessionAttribute(PRE_COLLE, document.getIdToken());
                }
                return new Response();
            }
            case '3': {
                String recommendNextCount = getSessionAttribute(NEXT_PAGE_INDEX);
                int count = 1;
                if (recommendNextCount != null) count = Integer.parseInt(recommendNextCount);
                RecommandDocument document = RecommendUtil.getRecommendDocument(count,channelId);
                if (document == null) {
                    return buildErrorResponse("客观你已经翻到底了,没有更多了");
                }
                RenderDocument render = new RenderDocument(document);
                addDirective(render);
                String token = document.getRecommendIdList();
                setSessionAttribute(PRE_COLLE, token);
                setSessionAttribute(NEXT_PAGE_INDEX,String.valueOf(count+1));
                return new Response();
            }
            case '4': {
                String recommendNextCount = getSessionAttribute(NEXT_PAGE_INDEX);
                int count = 1;
                if (recommendNextCount != null) count = Integer.parseInt(recommendNextCount);
                RecommandDocument document = RecommendUtil.getRankOtherPageDocument(count, channelId);
                if (document == null) {
                    return buildErrorResponse("客观你已经翻到底了,没有更多了");
                }
                RenderDocument render = new RenderDocument(document);
                addDirective(render);
                String token = document.getRecommendIdList();
                setSessionAttribute(PRE_COLLE, token);
                setSessionAttribute(NEXT_PAGE_INDEX,String.valueOf(count+1));
                return new Response();
            }
            case '5':{
                String category = getSessionAttribute("category");
                if (category == null) category = "古风";
                String categoryNextCount = getSessionAttribute(NEXT_PAGE_INDEX);
                int count = 1;
                if (categoryNextCount != null) count = Integer.parseInt(categoryNextCount);
                RecommandDocument document = RecommendUtil.getCategory(count,category);
                if (document == null) {
                    return buildErrorResponse("客观你已经翻到底了,没有更多了");
                }
                RenderDocument render = new RenderDocument(document);
                addDirective(render);
                String countStr = String.valueOf(count+1);
                setSessionAttribute(NEXT_PAGE_INDEX,countStr);
                return new Response();
            }
            default:{
                return buildErrorResponse("可以跟我说返回或者回到首页");
            }
        }
    }

    private Response dealLastPageIntent(String userId, String channelId) {
        String signPage = getSessionAttribute("trace");
        if (signPage == null || signPage.length() < 2) {
            return buildErrorResponse("不好意思,小悟累啦,滑不动了");
        }
        char sign = signPage.charAt(1);
        switch (sign) {
            case '1':{
                String recommendNextCount = getSessionAttribute(NEXT_PAGE_INDEX);
                if (recommendNextCount == null || "1".equals(recommendNextCount)) {
                    return buildErrorResponse("客观,前面没啦,没法往前翻了");
                }
                int count = Integer.parseInt(recommendNextCount);
                int start = (count-2)*CO_HI_PAGE_SIZE;
                String jytUserId = getSessionAttribute("userId");
                if (StringUtils.isBlank(jytUserId)) {
                    jytUserId = DplbotServiceUtil.getUsUserId(userId, getAccessToken(), channelId);
                    setSessionAttribute("userId", jytUserId);
                }
                CollectHistoryDocument document = DplbotServiceUtil.getCollectPage(userId,getAccessToken(),
                        jytUserId, start, channelId);
                if (document != null) {
                    RenderDocument render = new RenderDocument(document);
                    addDirective(render);
                    String cmt = String.valueOf(count-1);
                    setSessionAttribute(NEXT_PAGE_INDEX,cmt);
                    setSessionAttribute(PRE_COLLE, document.getIdToken());
                }
                break;
            }
            case '2':{
                String recommendNextCount = getSessionAttribute(NEXT_PAGE_INDEX);
                if (recommendNextCount == null || "1".equals(recommendNextCount)) {
                    return buildErrorResponse("客观,前面没啦,没法往前翻了");
                }
                int count = Integer.parseInt(recommendNextCount);
                int start = (count-2)*CO_HI_PAGE_SIZE;
                CollectHistoryDocument document = DplbotServiceUtil.getHistory(userId, getAccessToken(),
                        getSessionAttribute("userId"), start, channelId);
                if (document != null) {
                    RenderDocument render = new RenderDocument(document);
                    addDirective(render);
                    String cmt = String.valueOf(count-1);
                    setSessionAttribute(NEXT_PAGE_INDEX,cmt);
                    setSessionAttribute(PRE_COLLE, document.getIdToken());
                }
                break;
            }
            case '3': {
                String recommendNextCount = getSessionAttribute(NEXT_PAGE_INDEX);
                if (recommendNextCount == null || "1".equals(recommendNextCount)) {
                    return buildErrorResponse("客观,前面没啦,没法往前翻了");
                }
                int count = Integer.parseInt(recommendNextCount);
                RecommandDocument document = RecommendUtil.getRecommendDocument(count-2, channelId);
                if (document != null) {
                    RenderDocument render = new RenderDocument(document);
                    addDirective(render);
                    String token = document.getRecommendIdList();
                    setSessionAttribute(PRE_COLLE, token);
                    setSessionAttribute(NEXT_PAGE_INDEX,String.valueOf(count-1));
                }
                break;
            }
            case '4': {
                String recommendNextCount = getSessionAttribute(NEXT_PAGE_INDEX);
                if (recommendNextCount == null || "1".equals(recommendNextCount)) {
                    return buildErrorResponse("客观,前面没啦,没法往前翻了");
                }
                int count = Integer.parseInt(recommendNextCount);
                RecommandDocument document;
                if (count == 2) document = RecommendUtil.getRankDocumentFirstPage(channelId);
                else document = RecommendUtil.getRankOtherPageDocument(count-2, channelId);
                if (document != null) {
                    RenderDocument render = new RenderDocument(document);
                    addDirective(render);
                    String token = document.getRecommendIdList();
                    setSessionAttribute(PRE_COLLE, token);
                    setSessionAttribute(NEXT_PAGE_INDEX,String.valueOf(count-1));
                }
                break;
            }
            case '5':{
                String category = getSessionAttribute("category");
                if (category == null) category = "古风";
                String recommendNextCount = getSessionAttribute(NEXT_PAGE_INDEX);
                if (recommendNextCount == null || "1".equals(recommendNextCount)) {
                    return buildErrorResponse("客观,前面没啦,没法往前翻了");
                }
                int count = Integer.parseInt(recommendNextCount);
                RecommandDocument document = RecommendUtil.getCategory(count-2,category);
                RenderDocument render = new RenderDocument(document);
                addDirective(render);
                setSessionAttribute(NEXT_PAGE_INDEX,String.valueOf(count-1));
                break;
            }
            default:{
                return buildErrorResponse("可以跟我说返回或者回到首页");
            }
        }
        return new Response();
    }

    private Response dealOpenNumIntent(String numStr, String channelId) {
        String trace = getSessionAttribute("trace");
        int num = DplbotServiceUtil.extractNum(numStr);
        log.info("dealOpenNumIntent num:"+num);
        if (trace == null || trace.charAt(1) == '0') {
            return dealOpenNumInHome(num);
        } else if (trace.length() > 2) {
            return buildErrorResponse("客官,您在这个页面想打开啥");
        }
        char page = trace.charAt(1);
        Response response;
        switch (page){
            case '1':
            case '2': response = dealOpenNum(getSessionAttribute(PRE_COLLE), num); break;
            case '3':
            case '4': response = dealOpenNumInRecommend(getSessionAttribute(PRE_COLLE), num, channelId, true); break;
            case '6': response = dealOpenNumInRecommend(getSessionAttribute(PRE_COLLE), num, channelId, false); break;
            case '5': response = dealOpenNumInCategoryBots(channelId, num);break;
            case '7': response = dealOpenNumCategory(num);break;
            default:{
                response = buildErrorResponse("屏幕上的图标都是可以点击的哦");
            }
        }
        return response;
    }

    private String extractWorkName(String str) {
//        str = str.replaceAll("[ \uD83C\uDD70\uD83C\uDD71]", "");
        return str.replaceAll("[ \uD83C\uDD70\uD83C\uDD71]", "");
    }

    private Response dealOpenCollectIntent(String channelId) {
        if (getAccessToken() == null) {
            return buildErrorResponse("该功能需要登录,请说小度小度,登录");
        } else {
            String jytId = getSessionAttribute("userId");
            if (StringUtils.isBlank(jytId)) {
                jytId = DplbotServiceUtil.getUsUserId(getUserId(), getAccessToken(), channelId);
                if (StringUtils.isBlank(jytId)) return buildErrorResponse("不好意思没有获取到您的收藏记录");
                setSessionAttribute("userId", jytId);
            }
//            String userId = getUserId();
//            UserBehaviorData data = USER_HISTORY.get(userId);
//            if (data.getUserBotId2Collected().size() == 0 && data.getUserBotId2Flowers().size() == 0) {
//                DplbotServiceUtil.onLunchFillUserBehaviorData(userId, channelId, jytId);
//            }
            CollectHistoryDocument document = DplbotServiceUtil.getCollectPage(getUserId(),getAccessToken(), jytId, 0, channelId);
            if (document == null) {
                backToHome();
                return buildErrorResponse("不好意思没有获取到您的收藏记录");
            } else if ("1".equals(document.getIdToken())){
                return buildErrorResponse("您还未收藏过任何作品");
            }
            RenderDocument render = new RenderDocument(document);
            addDirective(render);
            setSessionAttribute(PRE_COLLE,document.getIdToken());
            setSessionAttribute(BACK_TRACE, "01");
            return buildErrorResponse("正在为您打开收藏列表");
        }
    }

    private Response dealBackIntent(String userId, String channelId) {
        String trace = getSessionAttribute(BACK_TRACE);
        char second;
        if (trace == null || getQuery().contains("首")) {
            // home page
            backToHome();
            if (StringUtils.endsWith(trace,File.separator)) sendCancellToTerminate(userId);
            return buildErrorResponse("已为您返回到首页");
        }
        else if (trace.length() <= 2) {
            if (trace.charAt(1) == '5') {
                return dealOpenCategory("已为您返回到分类页");
            } else {
                backToHome();
                return buildErrorResponse("已为您返回到首页");
            }
        }
        else if (StringUtils.endsWith(trace,File.separator)){
            String botAccount = trace.substring(2, trace.length()-1);
            log.info("返回作品的id:{}", botAccount);
            backToBotIntro(botAccount, channelId);
            sendCancellToTerminate(userId);
            return buildErrorResponse("已为您返回到作品介绍页");
        } else {
            second = trace.charAt(1);
            switch (second){
                // home page
                case '0':{
                    backToHome();
                    return buildErrorResponse("已为您返回到首页");
                }
                case '1': {
                    //collect
                    String appUserId = getSessionAttribute("userId");
                    if (StringUtils.isBlank(appUserId)) {
                        UserBehaviorData data = USER_HISTORY.get(userId);
                        if (data != null) appUserId = data.getJytUserId();
                        if (StringUtils.isBlank(appUserId)) {
                            appUserId = DplbotServiceUtil.getUsUserId(userId, getAccessToken(), channelId);
                        }
                    }
                    String recommendNextCount = getSessionAttribute(NEXT_PAGE_INDEX);
                    int count = 1;
                    if (recommendNextCount != null) count = Integer.parseInt(recommendNextCount);
                    CollectHistoryDocument document = DplbotServiceUtil.getCollectPage(userId, getAccessToken(),appUserId,
                            (count-1)*CO_HI_PAGE_SIZE, channelId);
                    if (document == null) {
                        backToHome();
                        return buildErrorResponse("您还未收藏过任何作品,快去收藏吧");
                    }
                    RenderDocument render = new RenderDocument(document);
                    addDirective(render);
                    setSessionAttribute(BACK_TRACE, "01");
                    setSessionAttribute(PRE_COLLE,document.getIdToken());
                    return buildErrorResponse("已为您返回到收藏页");
                }
                case '2': {
                    //history
                    String recommendNextCount = getSessionAttribute(NEXT_PAGE_INDEX);
                    int count = 1;
                    if (recommendNextCount != null) count = Integer.parseInt(recommendNextCount);
                    CollectHistoryDocument document = DplbotServiceUtil.getHistory(userId, getAccessToken(),getSessionAttribute("userId"),
                            (count-1)*CO_HI_PAGE_SIZE, channelId);
                    if (document == null) {
                        backToHome();
                        return buildErrorResponse("哎呀,可能是您太久没玩了,历史记录已被清空");
                    }
                    RenderDocument render = new RenderDocument(document);
                    addDirective(render);
                    setSessionAttribute(PRE_COLLE,document.getIdToken());
                    setSessionAttribute(BACK_TRACE, "02");
                    return buildErrorResponse("已为您返回到历史记录页");
                }
                case '3': {
                    //recommend
                    String recommendNextCount = getSessionAttribute(NEXT_PAGE_INDEX);
                    int count = 1;
                    if (recommendNextCount != null) count = Integer.parseInt(recommendNextCount);
                    RecommandDocument recommandDocument = RecommendUtil.getRecommendDocument(count-1, channelId);
                    if (recommandDocument == null) {
                        RenderDocument render = new RenderDocument();
                        render.setDocument(LunchUtils.getLunchDocument_part());
                        addDirective(render);
                        addDirective(new SendPart("lunch"));
                        setSessionAttribute(BACK_TRACE, null);
                        break;
                    }
                    RenderDocument render = new RenderDocument();
                    render.setDocument(recommandDocument);
                    addDirective(render);
                    setSessionAttribute(PRE_COLLE, recommandDocument.getRecommendIdList());
                    setSessionAttribute(BACK_TRACE, "03");
                    return buildErrorResponse("已为您返回到推荐页");
                }
                case '4': {
                    //rank
                    String recommendNextCount = getSessionAttribute(NEXT_PAGE_INDEX);
                    int count = 1;
                    if (recommendNextCount != null) count = Integer.parseInt(recommendNextCount);
                    RecommandDocument recommandDocument;
                    if (count == 1) recommandDocument = RecommendUtil.getRankDocumentFirstPage(channelId);
                    else recommandDocument = RecommendUtil.getRankOtherPageDocument(count-1, channelId);
                    if (recommandDocument == null) {
                        RenderDocument render = new RenderDocument();
                        render.setDocument(LunchUtils.getLunchDocument_part());
                        addDirective(render);
                        addDirective(new SendPart("lunch"));
                        setSessionAttribute(BACK_TRACE, null);
                        break;
                    }
                    RenderDocument render = new RenderDocument();
                    render.setDocument(recommandDocument);
                    addDirective(render);
                    setSessionAttribute(PRE_COLLE, recommandDocument.getRecommendIdList());
                    setSessionAttribute(BACK_TRACE, "04");
                    return buildErrorResponse("已为您返回到排行榜");
                }
                case '5':{
                    String category = getSessionAttribute("category");
                    if (category == null) {
                        backToHome();
                    } else {
                        String count = getSessionAttribute(NEXT_PAGE_INDEX);
                        int pageIndex = 0;
                        if (count != null) pageIndex = Integer.parseInt(count)-1;
                        RecommandDocument document = RecommendUtil.getCategory(pageIndex, category);
                        RenderDocument render = new RenderDocument(document);
                        addDirective(render);
                        return buildErrorResponse(new StringBuilder("已为您返回到")
                                .append(category).append("分类第").append(pageIndex+1).append("页").toString());
                    }
                }
            }
        }
        return new Response();
    }

    private void backToHome() {
        RenderDocument renderDocument = new RenderDocument(LunchUtils.getLunchDocument_part());
        addDirective(renderDocument);
        addDirective(new SendPart("lunch"));
        setSessionAttribute(BACK_TRACE,null);
    }

    private Response dealDefaultIntent(ai.qiwu.com.xiaoduhome.baidu.dueros.data.request.IntentRequest intentRequest, String userId, String word, String channelId) {
        if (word == null) {
            ai.qiwu.com.xiaoduhome.baidu.dueros.data.request.Query query = intentRequest.getQuery();
            if (query != null) word = query.getOriginal();
            if (word == null){
                log.info("小度没有传来任何话语");
                return buildErrorResponse(SORRY_UNCATCH);
            }
        }
        String data = null;
        try {
            data = RequestTerminal.requestTerminateTemp(word, userId, channelId);
        } catch (Exception e) {
            log.error("请求终端接口出错:", e.toString());
        }
        if (StringUtils.isBlank(data)) {
            return buildErrorResponse(SORRY_UNCATCH);
        }
        log.info("小悟返回:"+data);
        JSONObject object = JSON.parseObject(data);
        try {
            JSONArray commands = object.getJSONArray("commands");
            if (commands != null && commands.size() == 1) {
                String txt = ((JSONObject)commands.get(0)).getString("text");
                int i;
                if (StringUtils.isNotBlank(txt) && (i = txt.indexOf("☛")) != -1) {
                    int end = txt.indexOf("☚");
                    String command = txt.substring(i+1, end).trim();
                    if (StringUtils.isNotBlank(command)) {
                        if ((i=command.indexOf("out")) != -1) {
                            log.info("小度，推荐bot退出：{},{}",word,userId);
                            return buildErrorResponse("本技能暂不支持该功能，若您想要退技能，请说小度小度，退出");
//                            String content = command.substring(i+3).trim();
//                            String byeWord = null;
//                            String skillName = null;
//                            switch (type) {
//                                case 1: {
//                                    byeWord = EndMsg.END_BYE_STORY;
//                                    skillName = SKILL_SMART;
//                                    break;
//                                }
//                                case 2: {
//                                    byeWord = EndMsg.END_BYE_NOVEL;
//                                    skillName = SKILL_NOVEL;
//                                    break;
//                                }
//                                case 5: {
//                                    byeWord = EndMsg.END_BYE_JIAOYOU;
//                                    skillName = SKILL;
//                                    break;
//                                }
//                            }
//                            if (byeWord == null) byeWord = EndMsg.END_BYE_Adult;
//                            if (skillName == null) skillName = SKILL_DEFAULT;
//                            if (StringUtils.isBlank(content)) {
//                                JSONArray dialogs = object.getJSONArray("dialogs");
//                                if (dialogs != null && dialogs.size() != 0) {
//                                    String str = ((JSONObject)dialogs.get(0)).getString("text");
//                                    if (StringUtils.isNotBlank(str)) {
//                                        str = str.replaceAll("\\{/.*?/}", "")+skillName;
//                                        RequestTerminal.backRequest(userId, type);
//                                        return buildLogOutResponse(str);
//                                    }
//                                }
//                                RequestTerminal.backRequest(userId, type);
//                                return buildLogOutResponse(byeWord);
//                            } else {
//                                content = content.replaceAll("\\{/.*?/}", "")+skillName;
//                                RequestTerminal.backRequest(userId, type);
//                                return buildLogOutResponse(content);
//                            }
                        }
                        String locate = getSessionAttribute("locate");
                        if (locate != null) setSessionAttribute("locate", null);
                        switch (command) {
                            case "recommend": return dealRecommend(data, object, true);
                            case "history"  : return dealHistoryIntent(channelId);
                            case "collect"  : return dealOpenCollectIntent(channelId);
                            case "next"     : return dealNextPageIntent(userId,channelId);
                            case "before"   : return dealLastPageIntent(userId, channelId);
                            case "back"     : return dealBackIntent(userId, channelId);
                            case "load"     : return load();
                            case "start"    : return dealStartIntent(userId, intentRequest, channelId);
                            case "openRank" : return dealRankIntent(channelId);
                            case "openNum"  : return dealOpenNumIntent(end+1 == txt.length() ? "1" : txt.substring(end+1), channelId);
                            case "jump"     : return dealJumpIntent(userId, end+1 == txt.length() ? "1" : txt.substring(end+1), channelId);
                            case "store"    : return dealCollectOrUnCollectIntent(userId, "collect", end+1 == txt.length() ? null : txt.substring(end+1));
                            case "unStore"  : return dealCollectOrUnCollectIntent(userId, "unCollect", end+1 == txt.length() ? null : txt.substring(end+1));
                            case "flower"   : return dealSendFlowerIntent(userId, end+1 == txt.length() ? "1" : txt.substring(end+1));
                            case "category" : return dealOpenCategory("正在为您打开分类页");
                            case "up"       : {
                                setSessionAttribute("locate", locate);
                                return dealUpOrDownCommand(true);
                            }
                            case "down"     : {
                                setSessionAttribute("locate", locate);
                                return dealUpOrDownCommand(false);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("解析出错:"+e);
            return new Response();
        }
        boolean endMark = false;
        if ("exit".equalsIgnoreCase(object.getString("botStatus"))) {
            String trace = getSessionAttribute(BACK_TRACE);
            String botAccount;
            if (StringUtils.endsWith(trace, File.separator) && data.contains((botAccount=trace.substring(2, trace.length()-1)))) {
                ExitData exitData = JSON.parseObject(data, ExitData.class);
                if (exitData.getDialogs() != null) {
                    final List<DialogData> outDatas = new ArrayList<>();
                    List<DialogData> botDatas = exitData.getDialogs().stream()
                            .filter(dialogData -> {
                                if (botAccount.equals(dialogData.getBotAccount())) return true;
                                outDatas.add(dialogData);
                                return false;
                            })
                            .collect(Collectors.toList());
                    if (botDatas == null || botDatas.size() == 0) {
                        return dealBackIntent(userId, channelId);
                    }
                    if (outDatas.size() != 0) {
                        exitData.setDialogs(outDatas);
                        log.info("end recommend:"+exitData);
                        stringRedisTemplate.opsForValue().set(SpiritRedisService.PREFIX_REDIS+userId+"_end",
                                JSON.toJSONString(exitData), 30, TimeUnit.SECONDS);
                        endMark = true;
                    }
                    ExitData botEnd = new ExitData();
                    botEnd.setAipioneerUsername(botAccount);
                    botEnd.setDialogs(botDatas);
                    data = JSON.toJSONString(botEnd);
                    object = JSON.parseObject(data);
                } else {
                    return dealBackIntent(userId, channelId);
                }
            } else {
                return dealBackIntent(userId, channelId);
            }
        }

        String botAccount = object.getString("aipioneerUsername");
        if (StringUtils.isBlank(botAccount)) return new Response();
        if (botAccount.equals(ScheduleServiceNew.getTheChannelBotAccount(channelId))){
            return dealRecommend(data, object, false);
        }
        String path = null;
        try {
            path = OkHttp3Utils.doPostJsonStr(ScheduleServiceNew.BASE_HOST_URL+"9663/api/audio", data);
        } catch (Exception e) {
            log.warn("没有获取到TTS返回地址");
        }
        if (StringUtils.isBlank(path) || "null".equals(path)) {
            log.error("小度没有获取到音频链接");
            return buildErrorResponse(ErrorMsg.SORRY_UNCATCH);
        }

        String beforeBotAccount = getSessionAttribute("botAcc");
        if (!botAccount.equals(beforeBotAccount)) {
            DplbotServiceUtil.updateTheUserBehaviorDataMark(userId, botAccount, getAccessToken(), getSessionAttribute("userId"), channelId);
            String productName = ScheduleServiceNew.getWorkNameByBotAccount(botAccount);
            if (StringUtils.isBlank(productName)) productName = "交游天下";
            PlayDocument document = PlayUtils.getPlayDocument(productName, DplbotServiceUtil.getTheActorsWithWords(object.getJSONArray("dialogs"), botAccount),
                     dealAudioUrl(path), botAccount);

            RenderDocument render = new RenderDocument(document);
            addDirective(render);
            DplbotServiceUtil.updateTheUserHistory(userId, botAccount, getAccessToken(), channelId, null);
            String trace = getSessionAttribute(BACK_TRACE);
            if (trace == null || trace.length() != 2) trace = "00";
            trace = new StringBuilder(trace).append(botAccount).append(File.separator).toString();
            setSessionAttribute(BACK_TRACE, trace);
            setSessionAttribute("botAcc", botAccount);
            setSessionAttribute("locate", null);
        } else {
//            ExecuteCommands executeCommands = new ExecuteCommands();
//            SetStateCommand audio = new SetStateCommand();
//            SetStateCommand textState = new SetStateCommand();
//            audio.setComponentId("audioCom");
//            audio.setState("src");
//            audio.setValue(data.getAudio());
//            textState.setComponentId("text");
//            textState.setState("text");
//            textState.setValue(text);
//
//            SetStateCommand asideImg = new SetStateCommand();
//            asideImg.setComponentId("asideImg");
//            asideImg.setState("src");
//            asideImg.setValue(ScheduleService.getAsideImgUrl(botAccount, aside));
//
//            SetStateCommand asideText = new SetStateCommand();
//            asideText.setComponentId("asideText");
//            asideText.setState("text");
//            asideText.setValue(aside);
//
//            executeCommands.addCommand(audio);
//            executeCommands.addCommand(textState);
//            executeCommands.addCommand(asideImg);
//            executeCommands.addCommand(asideText);

//                UpdateComponentCommand update = new UpdateComponentCommand();
//                update.setComponentId("whole");
//                update.setDocument(TempleUtils.getPlayUpdate(data.getAudio(), text));
//                executeCommands.addCommand(update);

//            addDirective(executeCommands);
//            detail.getDirectives().add(executeCommands);

            String productName = ScheduleServiceNew.getWorkNameByBotAccount(botAccount);
            if (StringUtils.isBlank(productName)) productName = "交游天下";
//            PlayDocument document = PlayUtils.getPlayDocument(correctName(productName), aside,
//                    ScheduleService.getAsideImgUrl(botAccount, aside),
//                    data.getAudio(), text);
            PlayDocument document = PlayUtils.getPlayDocument(productName,
                    DplbotServiceUtil.getTheActorsWithWords(object.getJSONArray("dialogs"), botAccount), dealAudioUrl(path), botAccount);

            RenderDocument render = new RenderDocument(document);
            addDirective(render);
        }
        if (endMark) setSessionAttribute("end", "1");
        stringRedisTemplate.opsForValue().setIfPresent(SpiritRedisService.PREFIX_REDIS+userId,
                new StringBuilder(data).append("⧩").append(path).toString(), 10, TimeUnit.SECONDS);
        return new Response();
    }

    private Response dealUpOrDownCommand(boolean up) {
        String trace = getSessionAttribute(BACK_TRACE);
        if (trace == null || trace.length() < 2 || trace.charAt(1) == '0') {
            return dealRankUpOrDown(up, true);
        } else if (trace.length() > 2) {
            return new Response();
        }
        char type = trace.charAt(1);
        switch (type) {
            case '1' :
            case '2' : return dealCollectOrHistoryUpOrDown(up);
            case '4' :
            case '5' : return dealRankUpOrDown(up, false);
            case '7' : return dealCategoryUpOrDown(up);
            default  : return new Response();
        }
    }

    private Response dealCategoryUpOrDown(boolean up) {
        int locate = 0;
        String distance;
        String currentIndex = getSessionAttribute("locate");
        if (up) {
            if (StringUtils.isNotBlank(currentIndex))  locate = Integer.parseInt(currentIndex);
            locate += 1;
            if (locate >= 3) return buildErrorResponse("已经滑到底了");
            distance = "240dp";
        } else {
            if (StringUtils.isBlank(currentIndex))  return buildErrorResponse("已滑到最顶部");
            locate = Integer.parseInt(currentIndex);
            if (locate == 0) return buildErrorResponse("已滑到最顶部");
            locate -= 1;
            distance = "-240dp";
        }
        ExecuteCommands commands = new ExecuteCommands();
        ScrollCommand command = new ScrollCommand();
        command.setComponentId("udScroll");
        command.setDistance(distance);
        commands.addCommand(command);
        addDirective(commands);
        setSessionAttribute("locate", String.valueOf(locate));
        return new Response();
    }

    private Response dealRankUpOrDown(boolean up, boolean home) {
        int locate = 0;
        String distance;
        String currentIndex = getSessionAttribute("locate");
        if (up) {
            if (StringUtils.isNotBlank(currentIndex))  locate = Integer.parseInt(currentIndex);
            locate += 1;
            int bound = 3;
            if (home) {
                bound = ScheduleServiceNew.getTheChannelAllProjectData(channel).size();
                bound /= 4;
                if (bound % 4 != 0) bound += 1;
            }
            if (locate >= bound) return buildErrorResponse("已经滑到底了");
            distance = "300dp";
        } else {
            if (StringUtils.isBlank(currentIndex)) return buildErrorResponse("已滑到最顶部");
            locate = Integer.parseInt(currentIndex);
            if (locate == 0) return buildErrorResponse("已滑到最顶部");
            locate -= 1;
            distance = "-300dp";
        }
        ExecuteCommands commands = new ExecuteCommands();
        ScrollCommand command = new ScrollCommand();
        command.setComponentId("udScroll");
        command.setDistance(distance);
        commands.addCommand(command);
        addDirective(commands);
        setSessionAttribute("locate", String.valueOf(locate));
        return new Response();
    }

    private Response dealCollectOrHistoryUpOrDown(boolean up) {
        int locate = 0;
        String currentIndex = getSessionAttribute("locate");
        if (up) {
            if (StringUtils.isNotBlank(currentIndex))  locate = Integer.parseInt(currentIndex);
            locate += 3;
            if (locate >= CO_HI_PAGE_SIZE) return buildErrorResponse("已经滑到底了");
        } else {
            if (StringUtils.isBlank(currentIndex)) return buildErrorResponse("已滑到最顶部");
            locate = Integer.parseInt(currentIndex);
            locate -= 3;
            if (locate < 0) return buildErrorResponse("已滑到最顶部");
            locate = locate <= 3 ? 0 : locate-3;
        }
        ExecuteCommands commands = new ExecuteCommands();
        ScrollToIndexCommand command = new ScrollToIndexCommand();
        command.setIndex(locate);
        command.setAlign(ScrollToIndexCommand.AlignType.FIRST);
        command.setComponentId("udScroll");
        commands.addCommand(command);
        addDirective(commands);
        setSessionAttribute("locate", String.valueOf(locate));
        return new Response();
    }

    private Response dealRecommend(String data, JSONObject object, boolean hasOrder) {
        try {
            String path = OkHttp3Utils.doPostJsonStr(ScheduleServiceNew.BASE_HOST_URL+"9663/api/audio", data);
            if (StringUtils.isBlank(path) || "null".equals(path)) {
                log.error("小度没有获取到音频链接");
                return buildErrorResponse(ErrorMsg.SORRY_UNCATCH);
            }
            String text = ((JSONObject) object.getJSONArray("dialogs").get(0)).getString("text");
            if (!hasOrder && !text.contains("推荐如下")) {
                String trace = getSessionAttribute(BACK_TRACE);
                char type='0';
                int ln;
                if (trace == null || (ln=trace.length()) < 2 || (ln==2 && (type=trace.charAt(1)) == '0')) {
                    return dealHomeBottom(dealAudioUrl(path), true);
                } else if (ln > 2) {
                    if (StringUtils.endsWith(trace, File.separator)) return new Response();
                    return dealIntroBottom(dealAudioUrl(path), trace);
                }
                switch (type) {
                    case '1': return dealHistoryOrCollectBottom(getUserId(), channel, dealAudioUrl(path), true);
                    case '2': return dealHistoryOrCollectBottom(getUserId(), channel, dealAudioUrl(path), false);
                    case '4': return dealRankBottom(channel, dealAudioUrl(path));
                    case '5': return dealCategoryBotBottom(dealAudioUrl(path));
                    case '6': return dealHomeBottom(dealAudioUrl(path), false);
                    case '7': return dealCategoryBottom(dealAudioUrl(path));
                    default : return dealHomeBottom(dealAudioUrl(path), false);
                }
            }
            RecommandDocument document = displayFunnyRecommandPage(text, dealAudioUrl(path));
            if (document != null) {
                RenderDocument render = new RenderDocument(document);
                addDirective(render);
                setSessionAttribute(PRE_COLLE, document.getRecommendIdList());
                setSessionAttribute(BACK_TRACE, "06");
            }
        } catch (Exception e) {
            log.warn("dealRecommend:"+e);
        }
        return new Response();
    }

    private Response dealIntroBottom(String src, String trace) {
        String accessToken = getAccessToken();
        String userId = getUserId();
        String botAccount = trace.substring(2);

        ProjectData productDetail = ScheduleServiceNew.getProjectByBotAccount(botAccount);
        if (productDetail == null) {
            return buildErrorResponse("目前不支持该作品");
        }
        String productName = productDetail.getName();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(productDetail.getCreateTime());
        String time = new StringBuilder(PRODUCT_INTRO.CREATE_TIME).append(dateString).toString();
        String author = new StringBuilder(PRODUCT_INTRO.AUTHOR).append(productDetail.getAuthorName()).toString();
        StringBuilder labels = new StringBuilder(PRODUCT_INTRO.LABEL);
        for (String label: productDetail.getLabels()) {
            labels.append(PRODUCT_INTRO.SPACE).append(label);
        }
        String intro = productDetail.getIntro();
        String enterProName = new StringBuilder(Constants.PRE_ENTER_PRODUCT).append(productName).toString();

        // "start product" container button
        String flowers, flowerReward,collectText, collectUrl, collectComId;
        if (accessToken != null) {
            // flower container button
            Long flowerCount = DplbotServiceUtil.getUserSendFlowerNum(productDetail.getBotAccount(), userId,
                    channel,accessToken, getSessionAttribute("userId"));
            flowers = USER_SEND_FLOWER+flowerCount;
            flowerReward = new StringBuilder(FLOWE).append(productDetail.getBotAccount()).toString();

            // collection container button
            boolean collec = DplbotServiceUtil.didUserCollecThisProduct(productDetail.getBotAccount(), userId,
                    accessToken, getSessionAttribute("userId"));
            if (collec) {
                collectText = COLLECED;
                collectUrl = COLLECTED_IMG_URL;
                collectComId = new StringBuilder(UN_COLLEC_ID).append(productDetail.getBotAccount()).toString();
            } else {
                collectText = UN_COLLEC;
                collectUrl = UN_COLLECTED_IMG_URL;
                collectComId = new StringBuilder(COLLEC_ID).append(productDetail.getBotAccount()).toString();
            }
        } else {
            flowers = FLOWER;
            flowerReward = FLOWER_LOAD;

            collectText = UN_COLLEC;
            collectUrl = UN_COLLECTED_IMG_URL;
            collectComId = FLOWER_LOAD;
        }

        ProductInfoDocument document = ProductInfoUtils.getProductINfoDocument(productDetail.getBannerImgUrl(), correctName(productName), time, author, labels.toString(),
                intro, enterProName, flowerReward, flowers, collectComId, collectUrl, collectText);
        document.getMainTemplate().getItems().add(ProductInfoUtils.buildAudioComponent(src));
        RenderDocument renderDocument = new RenderDocument(document);
        this.addDirective(renderDocument);
        return new Response();
    }

    private Response dealHomeBottom(String src, boolean alreadyInHome) {
        LunchDocument document = LunchUtils.getLunchDocument_part();
        RenderDocument renderDocument = new RenderDocument(document);
        addDirective(renderDocument);
        if (!alreadyInHome) setSessionAttribute(BACK_TRACE,null);
        setSessionAttribute("lunchBottom", src);
        addDirective(new SendPart("lunchBottom"));
        return new Response();
    }

    private Response dealCategoryBottom(String src) {
        LunchDocument document = LunchUtils.getCategoryDocument();
        if (document == null) return new Response();
        document.getMainTemplate().getItems().add(LunchUtils.buildAudioComponent(src));
        RenderDocument render = new RenderDocument(document);
        this.addDirective(render);
        return new Response();
    }

    private Response dealCategoryBotBottom(String src) {
        String category = getSessionAttribute("category");
        if (category == null) category = "古风";
        String categoryNextCount = getSessionAttribute(NEXT_PAGE_INDEX);
        int count = 0;
        if (categoryNextCount != null) {
            count = Integer.parseInt(categoryNextCount);
            count = count == 0 ? 0:count-1;
        }
        RecommandDocument document = RecommendUtil.getCategory(count,category);
        if (document == null) {
            return buildErrorResponse("客观你已经翻到底了,没有更多了");
        }
        document.getMainTemplate().getItems().add(RecommendUtil.buildAudioComponent(src));
        RenderDocument render = new RenderDocument(document);
        addDirective(render);
        return new Response();
    }

    private Response dealRankBottom(String channelId, String src) {
        String recommendNextCount = getSessionAttribute(NEXT_PAGE_INDEX);
        int count = 1;
        if (recommendNextCount != null) count = Integer.parseInt(recommendNextCount);
        RecommandDocument document = RecommendUtil.getRankOtherPageDocument(count, channelId);
        if (document == null) {
            return buildErrorResponse("客观你已经翻到底了,没有更多了");
        }
        document.getMainTemplate().getItems().add(RecommendUtil.buildAudioComponent(src));
        RenderDocument render = new RenderDocument(document);
        addDirective(render);
        return new Response();
    }

    private Response dealHistoryOrCollectBottom(String userId, String channelId, String src, boolean collect) {
        String recommendNextCount = getSessionAttribute(NEXT_PAGE_INDEX);
        int count;
        if (recommendNextCount == null) {
            count = 0;
        } else {
            count = Integer.parseInt(recommendNextCount);
            count = count == 0 ? 0 : count-1;
        }
        int start = count*CO_HI_PAGE_SIZE;
        CollectHistoryDocument document;
        if (collect) {
            String jytUserId = getSessionAttribute("userId");
            if (StringUtils.isBlank(jytUserId)) {
                jytUserId = DplbotServiceUtil.getUsUserId(userId, getAccessToken(), channelId);
                setSessionAttribute("userId", jytUserId);
            }
            document = DplbotServiceUtil.getCollectPage(userId,getAccessToken(),
                    jytUserId, start, channelId);
        } else {
            document = DplbotServiceUtil.getHistory(userId, getAccessToken(),
                    getSessionAttribute("userId"),start,channelId);
        }
        if (document != null) {
            document.getMainTemplate().getItems().add(CollectHistoryUtils.buildAudioComponent(src));
            RenderDocument render = new RenderDocument(document);
            addDirective(render);
        }
        return new Response();
    }

    private Response dealOpenNumInRecommend(String idsToken, int collectNum, String channelId, boolean rank) {
        if (idsToken == null) {
            return buildErrorResponse("请重新进入当前页面");
        }
        String[] tokenList = new String(Base64.getDecoder().decode(idsToken)).split(File.separator);
        if (rank) {
            int index;
            String currentNextPage = getSessionAttribute(NEXT_PAGE_INDEX);
            if (StringUtils.isNotBlank(currentNextPage)) {
                index = Integer.parseInt(currentNextPage)-1;
                index = index < 0 ? 0 : index;
            } else {
                index = 0;
            }
            collectNum = collectNum-index*RE_RA_CA_PAGE_SIZE;
        }
        if (collectNum > tokenList.length || collectNum <= 0) return buildErrorResponse("这个数字超出了范围,请说出您要打开的作品在当前页面所显示的数字");
        ProjectData detail = ScheduleServiceNew.getProjectByBotAccount(tokenList[collectNum-1]);
        String accessToken = getAccessToken();
        if (DplbotServiceUtil.doesUserPlayed(detail.getBotAccount(), getUserId(), accessToken, accessToken != null)) {
            String productName = correctName(detail.getName());
            return directInProduct(productName, detail.getBotAccount());
        } else {
            return enterProductInfo(detail.getName(), channelId);
        }
    }

    private Response dealOpenNumCategory(int num) {
        if (num > ScheduleServiceNew.CATEGORY.size()) return buildErrorResponse(num+"超出了范围");
        RecommandDocument document = RecommendUtil.getCategory(0, ScheduleServiceNew.CATEGORY.get(num-1));
        if (document == null) return new Response();
        RenderDocument render = new RenderDocument(document);
        addDirective(render);
        setSessionAttribute(BACK_TRACE, "75");
        setSessionAttribute("category", ScheduleServiceNew.CATEGORY.get(num-1));
        return new Response();
    }

    private Response dealOpenNumInCategoryBots(String channelId, int num) {
        String count = getSessionAttribute(NEXT_PAGE_INDEX);
        String category = getSessionAttribute("category");
        if (StringUtils.isBlank(category)) return buildErrorResponse("请直接点击进入");
        int pageIndex = 0;
        if (count != null) pageIndex = Integer.parseInt(count)-1;
        num = pageIndex*RE_RA_CA_PAGE_SIZE+num-1;
        List<ProjectData> categoryBots = ScheduleServiceNew.category2Bots.get(category);
        if (num < 0 || num >= categoryBots.size()) {
            return buildErrorResponse("这个数字超出了范围,作品的序号已显示在图片左上角");
        }
        ProjectData bot = categoryBots.get(num);
        Response response;
        String accessToken = getAccessToken();
        if (DplbotServiceUtil.doesUserPlayed(bot.getBotAccount(), getUserId(), accessToken, accessToken != null)) {
            response = directInProduct(bot.getName(), bot.getBotAccount());
        } else response = enterProductInfo(bot.getName(), channelId);
        setSessionAttribute("category", category);
        setSessionAttribute(NEXT_PAGE_INDEX, count);
        return response;
    }

    private Response dealOpenNumInHome(int num) {
        List<ProjectData> dataList = ScheduleServiceNew.getTheChannelAllProjectData(channel);
        if (num <= dataList.size()) {
            ProjectData bot = dataList.get(num-1);
            String accessToken = getAccessToken();
            if (!DplbotServiceUtil.doesUserPlayed(bot.getBotAccount(), getUserId(), accessToken, accessToken != null)) return enterProductInfo(bot.getName(), channel);
            else return directInProduct(bot.getName(), bot.getBotAccount());
        }
//        if (num > 0 && num <= 10) {
//            GameProjectTb bot = ScheduleService.getRecommendList().get(num-1);
//            log.info("历史：作品id:{}",bot.getBotAccount());
//            if (!DplbotServiceUtil.doesUserPlayed(bot.getBotAccount(), getUserId(), getAccessToken())) return enterProductInfo(bot.getName(), channel);
//            else return directInProduct(bot.getName(), bot.getBotAccount());
//        } else if (num > 10 && num <= 20) {
//            GameProjectTb bot = ScheduleService.getRankingList().get(num-11);
//            log.info("历史：作品id:{}",bot.getBotAccount());
//            if (!DplbotServiceUtil.doesUserPlayed(bot.getBotAccount(), getUserId(), getAccessToken())) return enterProductInfo(bot.getName(), channel);
//            else return directInProduct(bot.getName(), bot.getBotAccount());
//        } else if (num > 20 && num <= 30) {
//            String categoryName = ScheduleService.CATEGORY[num-21];
//            RecommandDocument document = RecommendUtil.getCategory(0, categoryName);
//            if (document == null) return buildErrorResponse("不好意思该分类还没有作品");
//            RenderDocument render = new RenderDocument(document);
//            this.addDirective(render);
//            this.setSessionAttribute("category",categoryName);
//            this.setSessionAttribute("trace","05");
//            return buildErrorResponse(new StringBuilder("为您打开").append(categoryName).append("类作品").toString());
//        }
        else {
            return buildErrorResponse("这数字超出我的范围了");
        }
    }

    private Response directInProduct(final String name,final String botId) {
        addDirective(new SendPart("direct"));
        POOL.submit(() -> {
            String productName = correctName(name);
            String enterProductWord = Constants.PRE_PRODUCT_PLAY + productName;
            log.info("进入作品语句:{}", enterProductWord);
            String data = null;
            try {
                data = RequestTerminal.requestTerminateTemp(enterProductWord, getUserId(), channel);
            } catch (IOException e) {
                log.error("请求终端接口出错:", e.toString());
            }
            if (data == null) {
                return;
            }
            stringRedisTemplate.opsForValue().set(SpiritRedisService.PREFIX_REDIS_DIRECT+getUserId(),
                    data, 15, TimeUnit.SECONDS);
        });
        return buildErrorResponse("晓悟正在为您打开"+name+",请您稍等");
    }

    private Response enterProductInfo(String productName, String channelId) {
        ProjectData productDetail = ScheduleServiceNew.getProjectByName(productName);

        // product create time
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(productDetail.getCreateTime());
        String createTime = new StringBuilder(PRODUCT_INTRO.CREATE_TIME).append(dateString).toString();

        // product author
        String author = new StringBuilder(PRODUCT_INTRO.AUTHOR).append(productDetail.getAuthorName()).toString();

        // product labels
        StringBuilder labels = new StringBuilder(PRODUCT_INTRO.LABEL);
        for (String label: productDetail.getLabels()) {
            labels.append(PRODUCT_INTRO.SPACE).append(label);
        }

        // product introduction
        String intro = productDetail.getIntro();

        // "start product" container button
        String enterProName = Constants.PRE_ENTER_PRODUCT + productName;

        String accessToken = getAccessToken();
        String flowers, flowerReward,collectText, collectUrl, collectComId;
        if (accessToken != null) {
            // flower container button
            Long count = DplbotServiceUtil.getUserSendFlowerNum(productDetail.getBotAccount(), getUserId(), channelId,
                    accessToken, getSessionAttribute("userId"));
            flowers = USER_SEND_FLOWER+count;
            flowerReward = FLOWE + productDetail.getBotAccount();

            // collection container button
            boolean collec = DplbotServiceUtil.doesUserCollectThisBot(productDetail.getBotAccount(), accessToken,
                    getUserId(), channelId, getSessionAttribute("userId"));
            if (collec) {
                collectText = COLLECED;
                collectUrl = COLLECTED_IMG_URL;
                collectComId = new StringBuilder(UN_COLLEC_ID).append(productDetail.getBotAccount()).toString();
            } else {
                collectText = UN_COLLEC;
                collectUrl = UN_COLLECTED_IMG_URL;
                collectComId = new StringBuilder(COLLEC_ID).append(productDetail.getBotAccount()).toString();
            }
        } else {
            flowers = FLOWER;
            flowerReward = FLOWER_LOAD;

            collectText = UN_COLLEC;
            collectUrl = UN_COLLECTED_IMG_URL;
            collectComId = FLOWER_LOAD; // 提示登录
        }

        ProductInfoDocument document = ProductInfoUtils.getProductINfoDocument(productDetail.getBannerImgUrl(), correctName(productName), createTime, author, labels.toString(),
                intro, enterProName, flowerReward, flowers, collectComId, collectUrl, collectText);

        RenderDocument renderDocument = new RenderDocument(document);
        this.addDirective(renderDocument);
        String trace = getSessionAttribute(BACK_TRACE);
        log.info("before trace------------:{}",trace);
        StringBuilder builder = new StringBuilder();
        if (trace == null || trace.length() < 2) {
            builder.append("00");
        } else if (trace.length()>2) {
            builder.append(trace, 0, 2);
        } else {
            builder.append(trace);
        }

        trace = builder.append(productDetail.getBotAccount()).toString();
        log.info("after trace------------:{}",trace);
        this.setSessionAttribute(BACK_TRACE, trace);
        return new Response();
    }

    private RecommandDocument displayFunnyRecommandPage(String word, String audio) {
        try {
            List<String> botNames = Utils.getRecommandBotNames(word);
            log.info("botNames:{}",botNames);
            List<ProjectData> bots = new ArrayList<>();
            for (String botName : botNames) {
                ProjectData detail = ScheduleServiceNew.getProjectByName(botName);
                if (detail == null) continue;
                bots.add(detail);
            }
            if (bots.size() != 0) return RecommendUtil.getFunnyDocument(bots, audio);
        } catch (Exception e) {
            log.error("获取funny页出错:{}",e.toString());
        }
        return null;
    }

    private XiaoDuOrderTB buildBeforePayPojo(String botAccount, String appUserId, String userId, int unitPrice,
                                             long number, String orderId) {
        XiaoDuOrderTB xiaoDuOrderTB = new XiaoDuOrderTB();
        buildPayPojoCommon(xiaoDuOrderTB, botAccount, appUserId, userId, unitPrice, number, orderId);
        if (xiaoDuOrderTB.getWorkName() == null) {
            log.warn("没有通过botAccount获取到作品名");
            return null;
        }
        xiaoDuOrderTB.setDevice(1);
        return xiaoDuOrderTB;
    }

    private XiaoDuOrderTB buildNoScreenBeforePayPojo(String botAccount, String appUserId, String userId, int unitPrice,
                                                     long number, String orderId) {
        XiaoDuOrderTB xiaoDuOrderTB = new XiaoDuOrderTB();
        buildPayPojoCommon(xiaoDuOrderTB, botAccount, appUserId, userId, unitPrice, number, orderId);
        if (xiaoDuOrderTB.getWorkName() == null) return null;
        xiaoDuOrderTB.setDevice(2);
        return xiaoDuOrderTB;
    }

    private void buildPayPojoCommon(XiaoDuOrderTB xiaoDuOrderTB, String botAccount, String appUserId, String userId, int unitPrice,
                                    long number, String orderId) {
        String workName = ScheduleServiceNew.getWorkNameByBotAccount(botAccount);
        if (StringUtils.isBlank(workName)) return;
        xiaoDuOrderTB.setWorkName(workName);
        xiaoDuOrderTB.setAppUserId(appUserId);
        xiaoDuOrderTB.setXiaoduUserId(userId);
        xiaoDuOrderTB.setUnitPrice(unitPrice);
        xiaoDuOrderTB.setNumber(number);
        xiaoDuOrderTB.setIsBuy(false);
        xiaoDuOrderTB.setStatus(false);
        xiaoDuOrderTB.setChOrderId(orderId);
        xiaoDuOrderTB.setTimeStamp(System.currentTimeMillis());
        xiaoDuOrderTB.setChannelId(channel);
    }

    @Override
    protected Response onChargeEvent(ChargeEvent chargeEvent) {
        String token = chargeEvent.getToken();
        String[] tokens = token.split(File.separator);
        log.info("onChargeEvent token:"+token);
        final String botAccount = tokens[0];
        String str = "恭喜您支付成功,开始体验吧";
        if (!isNoScreenRequest()) {
            String userId = getUserId();
            String jytUserId = getSessionAttribute("userId");
            Boolean response = DplbotServiceUtil.dealChargeCallback(chargeEvent, userId, getAccessToken(),
                    jytUserId, ScheduleServiceNew.getWorkNameByBotAccount(botAccount), channel);
            log.info("onChargeEvent response:"+response);
            if (Boolean.TRUE.equals(response)) {
                Map<String, Long> flowerRecords = USER_HISTORY.get(userId).getUserBotId2Flowers();
                Long before = flowerRecords.get(botAccount);
                if (before == null) before = 0L;
                flowerRecords.put(botAccount, ++before);
                DplbotServiceUtil.noticeOtherServer(2, botAccount, before, false, userId, jytUserId, channel, true);
                try {
                    if ("0".equals(tokens[2])) {
                        log.info("onChargeEvent in project info:{}, token:{}",before,token);
                        enterProductInfo(ScheduleServiceNew.getWorkNameByBotAccount(botAccount), channel);
//                        addDirective(new SendPart("payed"));
                        str = "恭喜您支付成功";
                    }
                } catch (Exception e) {
                    log.warn(ExceptionUtils.getStackTrace(e));
                }
            } else {
                str = "很抱歉,您没有支付成功";
            }
//            String trace = getSessionAttribute(BACK_TRACE);
//            if (trace.length()>2 && !StringUtils.endsWith(trace, File.separator)) {
//                backToBotIntro(trace.substring(2, trace.length()-1), channel);
//            }
        } else {
            String userId = customUserId();
            String jytUserId = getSessionAttribute("userId");
            if (StringUtils.isBlank(jytUserId)) jytUserId = DplbotServiceUtil.getUserIdByToken(getAccessToken(), channel, getUserId());
            if (StringUtils.isBlank(jytUserId)) {
                jytUserId = "null";
            }
            boolean response = DplbotServiceUtil.dealChargeCallback(chargeEvent, userId, getAccessToken(),
                    jytUserId, ScheduleServiceNew.getWorkNameByBotAccount(botAccount), channel);
            if (!response) {
                str = "很抱歉,您没有支付成功";
            }
        }
        return buildErrorResponse(str);
    }

    @Override
    protected Response onPlaybackFinishedEvent(PlaybackFinishedEvent playbackFinishedEvent) {
//        log.info("音频播放完毕，开麦");
//        setSessionAttribute("botAcc",getSessionAttribute("botAcc"));
//        log.error("FinishedEvent");
        this.setExpectSpeech(true);
        return new Response();
    }

    @Override
    protected Response onPlaybackStoppedEvent(ai.qiwu.com.xiaoduhome.baidu.dueros.data.request.audioplayer.event.PlaybackStoppedEvent playbackStoppedEvent) {
//        setSessionAttribute("botAcc",getSessionAttribute("botAcc"));
        waitAnswer();
        return new Response();
    }

    @Override
    protected Response onPlaybackStartedEvent(ai.qiwu.com.xiaoduhome.baidu.dueros.data.request.audioplayer.event.PlaybackStartedEvent playbackStartedEvent) {
//        setSessionAttribute("botAcc",getSessionAttribute("botAcc"));
//        if (isNoScreenRequest()) {
//            Boolean result = stringRedisTemplate.delete(SpiritRedisService.PREFIX_REDIS+customUserId());
//            log.info("音频播放事件删除:"+result);
//        }
        waitAnswer();
        return new Response();
    }

    @Override
    protected Response onPlaybackNearlyFinishedEvent(ai.qiwu.com.xiaoduhome.baidu.dueros.data.request.audioplayer.event.PlaybackNearlyFinishedEvent playbackNearlyFinishedEvent) {
//        setSessionAttribute("botAcc",getSessionAttribute("botAcc"));
        waitAnswer();
        return new Response();
    }

    @Override
    protected Response onNextRequiredEvent(NextRequired nextRequired) {
        try {
            switch (nextRequired.getToken()) {
                case "lunch" : {
                    RenderDocument renderDocument = new RenderDocument(LunchUtils.getLunchDocument());
                    addDirective(renderDocument);
                    break;
                }
                case "lunchBottom": {
                    String src = getSessionAttribute("lunchBottom");
                    if (StringUtils.isNotBlank(src)) {
                        LunchDocument document = LunchUtils.getLunchDocument();
                        document.getMainTemplate().getItems().add(LunchUtils.buildAudioComponent(src));
                        RenderDocument renderDocument = new RenderDocument(document);
                        addDirective(renderDocument);
                        setSessionAttribute("lunchBottom", null);
                        return new Response();
                    }
                    break;
                }
                case "direct" :{
                    String userId = SpiritRedisService.PREFIX_REDIS_DIRECT+getUserId();
                    String data = stringRedisTemplate.opsForValue().get(userId);
                    log.info("send part direct:"+data);
                    if (StringUtils.isNotBlank(data)) {
                        stringRedisTemplate.delete(userId);
                        String path = null;
                        try {
                            path = OkHttp3Utils.doPostJsonStr(ScheduleServiceNew.BASE_HOST_URL+"9663/api/audio", data);
                        } catch (Exception e) {
                            log.warn("没有获取到TTS返回地址");
                        }
                        if (StringUtils.isBlank(path) || "null".equals(path)) {
                            log.error("小度没有获取到音频链接");
                            return buildErrorResponse("由于网络原因，小悟没有获取到结果，您可以通过点击来进入游戏");
                        }
                        JSONObject object = JSON.parseObject(data);
                        String botId = object.getString("aipioneerUsername");
                        PlayDocument document = PlayUtils.getPlayDocument(ScheduleServiceNew.getWorkNameByBotAccount(botId),
                                DplbotServiceUtil.getTheActorsWithWords(object.getJSONArray("dialogs"), botId), dealAudioUrl(path), botId);
                        RenderDocument render = new RenderDocument(document);
                        this.setSessionAttribute(PRE_COLLE, null);
                        String trace = getSessionAttribute(BACK_TRACE);
                        if (trace == null || trace.length() != 2) trace = "00";
                        String backTrace = new StringBuilder(trace).append(botId).append(File.separator).toString();
                        this.setSessionAttribute(BACK_TRACE, backTrace);
                        this.addDirective(render);
                    } else {
                        log.warn("direct can not get data,"+userId);
                        return buildErrorResponse("不好意思好像出了什么问题，请再说一边吧");
                    }
                }
//                case "payed" : {
//                    log.info("success pay part");
//                    String trace = getSessionAttribute(BACK_TRACE);
//                    if (StringUtils.isNotBlank(trace)) {
//                        String word = USER_SEND_FLOWER+USER_HISTORY.get(getUserId()).getUserBotId2Flowers().get(trace.substring(2));
//                        log.info("flower num update:"+word);
//                        ExecuteCommands executeCommands = new ExecuteCommands();
//                        SetStateCommand stateCommand = new SetStateCommand();
//                        stateCommand.setComponentId("pft");
//                        stateCommand.setState("text");
//                        stateCommand.setValue(word);
//                        executeCommands.addCommand(stateCommand);
//                        addDirective(executeCommands);
//                    }
//                }
            }
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return new Response();
    }

    @Override
    protected Response onDefaultEvent() {
        ai.qiwu.com.xiaoduhome.baidu.dueros.data.request.RequestBody requestBody = getRequest().getRequest();
        if (requestBody instanceof ai.qiwu.com.xiaoduhome.baidu.dueros.data.request.audioplayer.event.AudioPlayerEvent) {
            this.setExpectSpeech(true);
            return new Response();
        }
        return new Response();
    }

    private String customUserId() {
        try {
            return PREFIX_USERID+ URLEncoder.encode(getUserId(), "UTF-8");
        } catch (Exception e) {
            log.error("xiaodu userid encode error:{}, {}",e, getUserId());
            return PREFIX_USERID+ getUserId().replaceAll("/", "");
        }
//        return PREFIX_USERID + ai.qiwu.com.xiaoduhome.xiaoai.common.Utils.modifyDeviceId(getUserId());
//        return PREFIX_USERID + getUserId().replaceAll(NUM_OR_ALPHA, "");
    }

    private boolean isNoScreenRequest() {
//        return getSupportedInterfaces().getDPL() == null;
        return getSupportedInterfaces().getDisplay() == null;
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

    public static void main(String[] args) throws IOException {
        String result = RequestTerminal.requestTerminateTemp("退出吧", "234325626", "jiaoyou-audio-test");
        System.out.println(result);
    }
}
