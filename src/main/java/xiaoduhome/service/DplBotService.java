package xiaoduhome.service;

//import ai.qiwu.com.xiaoduhome.common.*;
//import ai.qiwu.com.xiaoduhome.common.temple.CollectHistoryDocument;
//import ai.qiwu.com.xiaoduhome.common.temple.TempleUtils;
//import ai.qiwu.com.xiaoduhome.common.temple.onLunch.LunchUtils;
//import ai.qiwu.com.xiaoduhome.common.temple.play.PlayDocument;
//import ai.qiwu.com.xiaoduhome.common.temple.play.PlayUtils;
//import ai.qiwu.com.xiaoduhome.common.temple.productInfo.ProductInfoDocument;
//import ai.qiwu.com.xiaoduhome.common.temple.productInfo.ProductInfoUtils;
//import ai.qiwu.com.xiaoduhome.common.temple.recommand.RecommandDocument;
//import ai.qiwu.com.xiaoduhome.common.temple.recommand.RecommendUtil;
//import ai.qiwu.com.xiaoduhome.entity.primary.*;
//import ai.qiwu.com.xiaoduhome.entity.secondary.XiaoDuOrderTB;
//import ai.qiwu.com.xiaoduhome.pojo.*;
//import ai.qiwu.com.xiaoduhome.pojo.dpl.MyDocument;
//import ai.qiwu.com.xiaoduhome.pojo.playAside.PlayPageData;
//import ai.qiwu.com.xiaoduhome.repository.secondary.XiaoDuOrderTbRepository;
//import ai.qiwu.com.xiaoduhome.spirit.SpiritTerminateRequest;
//import ai.qiwu.com.xiaoduhome.xiaoai.common.XiaoAiConstants;
//import ai.qiwu.com.xiaoduhome.xiaoai.service.XiaoAiService;
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
import com.baidu.dueros.bot.BaseBot;
//import com.baidu.dueros.data.request.IntentRequest;
//import com.baidu.dueros.data.request.LaunchRequest;
//import com.baidu.dueros.data.request.Query;
//import com.baidu.dueros.data.request.SessionEndedRequest;
//import com.baidu.dueros.data.request.audioplayer.event.PlaybackFinishedEvent;
//import com.baidu.dueros.data.request.audioplayer.event.PlaybackNearlyFinishedEvent;
//import com.baidu.dueros.data.request.audioplayer.event.PlaybackStartedEvent;
//import com.baidu.dueros.data.request.audioplayer.event.PlaybackStoppedEvent;
//import com.baidu.dueros.data.request.events.LinkAccountSucceededEvent;
//import com.baidu.dueros.data.request.pay.event.ChargeEvent;
//import com.baidu.dueros.data.response.OutputSpeech;
//import com.baidu.dueros.data.response.Reprompt;
//import com.baidu.dueros.data.response.card.*;
//import com.baidu.dueros.data.response.directive.Directive;
//import com.baidu.dueros.data.response.directive.audioplayer.AudioPlayerDirective;
//import com.baidu.dueros.data.response.directive.audioplayer.Play;
//import com.baidu.dueros.data.response.directive.display.PushStack;
//import com.baidu.dueros.data.response.directive.dpl.ExecuteCommands;
//import com.baidu.dueros.data.response.directive.dpl.RenderDocument;
//import com.baidu.dueros.data.response.directive.dpl.commands.*;
//import com.baidu.dueros.data.response.directive.dpl.event.UserEvent;
//import com.baidu.dueros.data.response.directive.pay.Buy;
//import com.baidu.dueros.data.response.directive.pay.Charge;
//import com.baidu.dueros.model.Response;
//import com.baidu.dueros.nlu.Intent;
import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.RandomStringUtils;
//import org.apache.commons.lang3.RandomUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.commons.lang3.exception.ExceptionUtils;
//import org.apache.commons.lang3.math.NumberUtils;
//
import javax.servlet.http.HttpServletRequest;
import java.io.*;
//import java.net.URLEncoder;
//import java.text.SimpleDateFormat;
//import java.util.*;
//import java.util.concurrent.*;
//import java.util.regex.Pattern;
//
//import static ai.qiwu.com.xiaoduhome.common.Constants.*;
//import static ai.qiwu.com.xiaoduhome.common.Constants.DPL_COMPONENT_ID.*;
//import static ai.qiwu.com.xiaoduhome.common.Constants.ErrorMsg.*;
//import static ai.qiwu.com.xiaoduhome.common.Constants.PRODUCT_INTRO.*;
//import static ai.qiwu.com.xiaoduhome.common.Constants.WelcomeWord.*;
//import static ai.qiwu.com.xiaoduhome.common.Constants.XIAOWU_API.*;
//import static ai.qiwu.com.xiaoduhome.service.ScheduleService.*;
//import static ai.qiwu.com.xiaoduhome.xiaoai.service.XiaoAiService.LAST_OUTTIME_MARK;
//import static ai.qiwu.com.xiaoduhome.xiaoai.service.XiaoAiService.OUTTIME_TYPE_0;
//

/**
 * @author 苗权威
 * @dateTime 19-8-6 下午8:06
 */
@Slf4j
public class DplBotService extends BaseBot {
//    private static final MyDocument FIRST_PAGE;
//
//    private static final ConcurrentHashMap<String, OverTimeDetail> LAST_OUTTIME_RESULT = new ConcurrentHashMap<>();
//
//    private static volatile RecommandDocument FUNNY_DEFAULT_DOCUMENT;
//
//    // 用来处理短时间内的多次请求
//    private static final ConcurrentHashMap<String, List<IntentRequest>> CACHE_XIAODU_REQUEST = new ConcurrentHashMap<>();
//
//    private static final ConcurrentHashMap<String, String> USERID_AUDIO_PATH = new ConcurrentHashMap<>();
//
//    static {
//        FIRST_PAGE = JSON.parseObject(DplbotServiceUtil.getDPLTemple("static/json/firstPage.json"), MyDocument.class);
//    }
//
//    private static final String FIRST_IMAGE = "https://didi-gz4.jiaoyou365.com/duai/image/Loading.png";
//
////    private static final String DU_DEVICE_ID = "XIAODU_2bf4f1ade0565b09bef2c529b03e163b";
//
//    private final int type;
//
//    private final static Reprompt WAIT_WORD = new Reprompt(new OutputSpeech(OutputSpeech.SpeechType.PlainText, "晓悟正在等你的回复"));
//
    public DplBotService(HttpServletRequest request, Integer type) throws IOException {
        super(request);
//        this.type = type;
        //privateKey为私钥内容,0代表你的Bot在DBP平台debug环境，1或者其他整数代表online环境,
        // botMonitor对象已经在bot-sdk里初始化，可以直接调用
//        this.botMonitor.setEnvironmentInfo(PRIVATE_KEY, 1);
//        this.botMonitor.setMonitorEnabled(true);
    }
//
//    @Override
//    public Response onLaunch(LaunchRequest launchRequest) {
//        if (getRequest().getContext().getScreen() == null) {
//            String userId = customUserId();
//            LAST_OUTTIME_MARK.remove(userId);
//            LAST_OUTTIME_RESULT.remove(userId);
//            USERID_AUDIO_PATH.remove(userId);
//            String welcomeWord;
//            String skillName;
//            String num = RandomStringUtils.randomNumeric(3);
//            switch (type) {
//                case 1 : {
////                    welcomeWord = WELCOME_SMART_FORMAT;
////                    try {
////                        List<String> botList = channel2Bots.get("jiaoyou-audio-child-test");
////                        String name = botList.get(Integer.parseInt(num)%botList.size());
////                        welcomeWord = String.format(welcomeWord, name,name);
////                    } catch (Exception e) {
////                        log.error("onLaunch welcomeWord type:{}, error:{}", type, e);
////                        welcomeWord = WELCOME_SMART;
////                    }
//                    welcomeWord = WELCOME_SMART;
//                    skillName = SKILL_SMART;
//                    break;
//                }
//                case 2 : {
////                    welcomeWord = WELCOME_NOVEL_FORMAT;
////                    try {
////                        List<String> botList = channel2Bots.get("jiaoyou-audio-adult-test");
////                        String name = botList.get(Integer.parseInt(num)%botList.size());
////                        welcomeWord = String.format(welcomeWord, name,name);
////                    } catch (Exception e) {
////                        log.error("onLaunch welcomeWord type:{}, error:{}", type, e);
////                        welcomeWord = WELCOME_NOVEL;
////                    }
//                    welcomeWord = WELCOME_NOVEL;
//                    skillName = SKILL_NOVEL;
//                    break;
//                }
//                case 3 : welcomeWord = WelcomeWord.WELCOME_STORY; skillName = SKILL_STORY; break;
//                case 4 : welcomeWord = WelcomeWord.WELCOME_LITERATURE; skillName = SKILL_LITERATURE; break;
//                default: {
//                    welcomeWord = WELCOME_FORMAT;
//                    try {
//                        List<String> botList = jiaoyouChannel2Bots.get("jiaoyou-audio-adult-test");
//                        String name = botList.get(Integer.parseInt(num)%botList.size());
//                        welcomeWord = String.format(welcomeWord, name,name);
//                    } catch (Exception e) {
//                        log.error("onLaunch welcomeWord type:{}, error:{}", type, e);
//                        welcomeWord = WELCOME;
//                    }
//                    skillName = SKILL;
//                    break;
//                }
//            }
//            StandardCard standardCard = new StandardCard(skillName, welcomeWord);
//            standardCard.setImage(FIRST_IMAGE);
//            OutputSpeech outputSpeech = new OutputSpeech(OutputSpeech.SpeechType.PlainText, welcomeWord);
//            return new Response(outputSpeech, standardCard);
//        } else {
//            return new Response();
//        }
////        RenderDocument renderDocument = new RenderDocument(FIRST_PAGE);
////        this.addDirective(renderDocument);
////        OutputSpeech outputSpeech = new OutputSpeech(OutputSpeech.SpeechType.PlainText, Constants.WelcomeWord.WELCOME);
////        // 初次进入时创建用户的行为信息,包括历史记录,送花数,收藏记录
////        // 历史记录,收藏记录,送花数,郊游天下id
////        if (DplbotServiceUtil.USER_HISTORY.get(getUserId()) == null) {
////            String accessToken = getAccessToken();
////            if (accessToken != null) setSessionAttribute("userId", DplbotServiceUtil.getUserIdByToken(accessToken));
////            DplbotServiceUtil.onLunchFillUserBehaviorData(accessToken, getUserId());
////        }
////        return new Response(outputSpeech);
//    }
//
//    @Override
//    protected Response onInent(IntentRequest intentRequest) {
//        final String userId = customUserId();
//        if (getRequest().getContext().getScreen() == null) {
////            final String deviceUserId = PREFIX_USERID + getDeviceId();
//            Byte overTimeType;
//            String path;
//            if ((overTimeType = LAST_OUTTIME_MARK.remove(userId)) != null) {
//                OverTimeDetail lastData = LAST_OUTTIME_RESULT.get(userId);
//                if (lastData != null) {
//                    for (Directive directive : lastData.getDirectives()) {
//                        //log.info("上次超时保存的Directive:{}",directive.getClass().getName());
//                        this.addDirective(directive);
//                    }
//                    for (Map.Entry<String, String> entry : lastData.getAttributes().entrySet()) {
//                        //log.info("上次超时保存的key:{}",key);
//                        this.setSessionAttribute(entry.getKey(), entry.getValue());
//                    }
//                    LAST_OUTTIME_MARK.remove(userId);
//                    LAST_OUTTIME_RESULT.remove(userId);
//                    return lastData.getResponse();
//                }
//                if (overTimeType != 3) {
//                    LAST_OUTTIME_MARK.put(userId, (byte) (overTimeType+1));
//                    return buildErrorResponse((RandomUtils.nextInt() & 1) == 1 ? XiaoAiConstants.TTS.WAIT_WORD_1 :
//                            XiaoAiConstants.TTS.WAIT_WORD_2);
//                }
//                LAST_OUTTIME_MARK.remove(userId);
//            } else if ((path = USERID_AUDIO_PATH.get(userId)) != null) {
//                Directive play = buildNoScreenPlayDirec(dealAudioUrl(path),  AudioPlayerDirective.PlayBehaviorType.REPLACE_ALL);
//                addDirective(play);
////                log.error("小度音频播放开始事件未被触发:"+path);
////                dealWithFinishedNoCallEvent(userId);
//                return new Response();
//            }
//            try {
//                Future future = DplbotServiceUtil.getPOOL().submit(() -> dealNoScreenIntentTemp(intentRequest, userId));
//                future.get(2700, TimeUnit.MILLISECONDS);
//            } catch (InterruptedException | ExecutionException e) {
//                log.error("获取任务执行结果出错:{}", ExceptionUtils.getStackTrace(e));
//                return buildErrorResponse(SORRY_UNCATCH);
//            } catch (TimeoutException e) {
//                log.info("超时,让用户再说一遍:{}",e.toString());
//                LAST_OUTTIME_MARK.put(userId, OUTTIME_TYPE_0);
//                return buildErrorResponse(SORRY_UNCATCH);
//            } catch (Exception all) {
//                log.error("任务执行发生了错误:{}", ExceptionUtils.getStackTrace(all));
//                return buildErrorResponse(SORRY_UNCATCH);
//            }
//            OverTimeDetail detail;
//            if ((detail = LAST_OUTTIME_RESULT.remove(userId)) != null) return detail.getResponse();
//            return new Response();
//        } else {
//            return new Response();
//        }
////        OverTimeDetail detail;
////        if ((detail = LAST_OUTTIME_RESULT.remove(userId)) != null) {
////            for (Directive directive : detail.getDirectives()) {
////                //log.info("上次超时保存的Directive:{}",directive.getClass().getName());
////                this.addDirective(directive);
////            }
////            for (String key : detail.getAttributes().keySet()) {
////                //log.info("上次超时保存的key:{}",key);
////                this.setSessionAttribute(key, detail.getAttributes().get(key));
////            }
////            return detail.getResponse();
////        }
////        try {
////            Future future = DplbotServiceUtil.getPOOL().submit(new Task(intentRequest));
////            future.get(2200, TimeUnit.MILLISECONDS);
////        }  catch (InterruptedException | ExecutionException e) {
////            log.error("获取任务执行结果出错:{}", ExceptionUtils.getStackTrace(e));
////            return buildErrorResponse(SORRY_UNCATCH);
////        } catch (TimeoutException e) {
////            log.info("超时,让用户再说一遍:{}",e.toString());
////            if ("funny".equals(getIntent().getName()) && FUNNY_DEFAULT_DOCUMENT != null) {
////                RenderDocument document = new RenderDocument(FUNNY_DEFAULT_DOCUMENT);
////                this.addDirective(document);
////                return new Response();
////            }else {
////                return buildErrorResponse(SORRY_UNCATCH);
////            }
////        } catch (Exception all) {
////            log.error("任务执行发生了错误:{}", ExceptionUtils.getStackTrace(all));
////            return buildErrorResponse(SORRY_UNCATCH);
////        }
////        OverTimeDetail data = LAST_OUTTIME_RESULT.remove(getUserId());
////        if (data == null) return new Response();
////        return data.getResponse();
//    }
//
////    private void dealWithFinishedNoCallEvent(String userId) {
////        if (userId.equals(DU_DEVICE_ID)) {
////            DplbotServiceUtil.getPOOL().submit(() -> {
////                WebSocketLog.sendInfo("警告/小度音频播放开始事件未被触发，请将这一情况告知", "du1");
////            });
////        } else {
////            DplbotServiceUtil.getPOOL().submit(() -> {
////                WebSocketLog.sendInfo("警告/小度音频播放开始事件未被触发，请将这一情况告知", "du2");
////            });
////        }
////    }
//
//    private void dealNoScreenIntentTemp(IntentRequest intentRequest, String userId) {
//        String intentName = getIntent().getName();
//        OverTimeDetail data = new OverTimeDetail();
//        LAST_OUTTIME_RESULT.put(userId, data);
//        if ("sendFlower".equals(intentName)) {
//            dealNoScreenSendFlowerIntent(userId, data);
//            return;
//        } else if ("load".equals(intentName)) {
//            Card card = new LinkAccountCard();
//            Response response = buildErrorResponse("请在小度App上完成登录");
//            response.setCard(card);
//            data.setResponse(response);
//            return;
//        }
//
//        List<IntentRequest> requestList = CACHE_XIAODU_REQUEST.get(userId);
//        try {
//            if (requestList == null) {
//                requestList = new ArrayList<>();
//                requestList.add(intentRequest);
//                CACHE_XIAODU_REQUEST.put(userId, requestList);
//                Thread.sleep(100);
//                int i = 1;
//                int j;
//                int count = 0;
//                while (i != (j = CACHE_XIAODU_REQUEST.get(userId).size()) && count < 5) {
//                    i = j;
//                    Thread.sleep(100);
//                    ++count;
//                }
//                intentRequest = requestList.get(i-1);
//            } else {
//                requestList.add(intentRequest);
////                log.warn("小度重复现象出现:{},requestId:{}",intentRequest.getQuery().getOriginal(), intentRequest.getRequestId());
//                log.warn("小度重复现象出现:{},",getStrRequest());
//                synchronized (requestList) {
//                    requestList.wait(10000);
//                    log.info("等待线程苏醒");
//                }
//                return;
//            }
//            final Query query = intentRequest.getQuery();
//            final String userWord = query.getOriginal();
////            if (query != null) userWord = query.getOriginal();
//            if (userWord == null){
//                log.error("小度没有传来任何话语");
//                data.setResponse(buildErrorResponse(ErrorMsg.SORRY_UNCATCH));
//                return;
//            }
////            log.info("小度.用户说的话：{}, requestID:{}",userWord, intentRequest.getRequestId());
//
//            String channel;
//            switch (type) {
//                case 1 :
//                case 3 : channel = "jiaoyou-audio-child-test";break;
//                case 2 :
//                case 4 : channel = "jiaoyou-audio-adult-test";break;
//                default: channel = "jiaoyou-audio-test";break;
//            }
//            String response = RequestTerminal.requestTerminateTemp(userWord, userId, channel);
////            log.info("小度,小悟返回：{},",response);
//            if (StringUtils.isBlank(response)) {
//                data.setResponse(buildErrorResponse(ErrorMsg.SORRY_UNCATCH));
//                return;
//            }
////            String mark = RandomStringUtils.randomAlphabetic(3);
////            AudioBoxData pathData = new AudioBoxData();
////            OkHttp3Utils.asyncPost(XiaoAiConstants.TTS_DIDI3, response, pathData);
//            try {
//                JSONObject object = JSON.parseObject(response);
//                JSONArray commands = object.getJSONArray("commands");
//                if (commands != null && commands.size() != 0) {
//                    String txt;
//                    for (Object ob: commands) {
//                        txt = ((JSONObject) ob).getString("text");
//                        int i;
//                        if (StringUtils.isNotBlank(txt) && (i=txt.indexOf("out")) != -1) {
//                            log.info("小度，推荐bot退出：{},{}",userWord,userId);
//                            String content = txt.substring(i+3, txt.indexOf("☚")).trim();
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
//                                        data.setResponse(buildLogOutResponse(str));
//                                        RequestTerminal.backRequest(userId, type);
//                                        return;
//                                    }
//                                }
//                                RequestTerminal.backRequest(userId, type);
//                                data.setResponse(buildLogOutResponse(byeWord));
//                                return;
//                            } else {
//                                content = content.replaceAll("\\{/.*?/}", "")+skillName;
//                                data.setResponse(buildLogOutResponse(content));
//                                RequestTerminal.backRequest(userId, type);
//                                return;
//                            }
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                log.error("推荐bot退出解析出错:"+e);
//            }
//            String path = OkHttp3Utils.doPostJsonStr(XiaoAiConstants.TTS_DIDI3, response);
////            String path = OkHttp3Utils.doPostJsonStr("http://didi-gz5.jiaoyou365.com:9663/api/audio", response);
//            if (StringUtils.isBlank(path) || "null".equals(path)) {
//                log.error("小度没有获取到音频链接");
//                data.setResponse(buildErrorResponse(ErrorMsg.SORRY_UNCATCH));
//                return;
//            }
////            final JSONObject ob = JSONObject.parseObject(response);
////            String botAcc = ob.getString("aipioneerUsername");
////            String beforeBotAccount = getSessionAttribute("botAcc");
////            if (StringUtils.isBlank(beforeBotAccount) || !beforeBotAccount.equals(botAcc)) {
////                setSessionAttribute("botAcc", botAcc);
////                data.getAttributes().put("botAcc",botAcc);
////            }
//
//            Directive play = buildNoScreenPlayDirec(dealAudioUrl(path),  AudioPlayerDirective.PlayBehaviorType.REPLACE_ALL);
//            addDirective(play);
//            data.getDirectives().add(play);
//            USERID_AUDIO_PATH.put(userId, path);
////            if (userId.equals(DU_DEVICE_ID)) {
////                DplbotServiceUtil.getPOOL().submit(() -> {
////                    JSONArray array = ob.getJSONArray("dialogs");
////                    StringBuilder builder = new StringBuilder(userWord).append(File.separator);
////                    for (Object item: array) {
////                        String str = ((JSONObject) item).getString("text");
////                        builder.append(str.replaceAll("\\{/.*?/}", "")).append(";");
////                    }
////                    WebSocketLog.sendInfo(builder.toString(), "du1");
////                });
////            } else {
////                DplbotServiceUtil.getPOOL().submit(() -> {
////                    JSONArray array = ob.getJSONArray("dialogs");
////                    StringBuilder builder = new StringBuilder(userWord).append(File.separator);
////                    for (Object item: array) {
////                        String str = ((JSONObject) item).getString("text");
////                        builder.append(str.replaceAll("\\{/.*?/}", "")).append(";");
////                    }
////                    WebSocketLog.sendInfo(builder.toString(), "du2");
////                });
////            }
//        } catch (Exception e) {
//            log.warn("中控返回结果为空:"+e);
//            data.setResponse(buildErrorResponse(ErrorMsg.SORRY_UNCATCH));
//        }
//        finally {
//            if (requestList != null && requestList.size() > 1) {
//                synchronized (requestList){
//                    requestList.notifyAll();
//                }
//            }
//            CACHE_XIAODU_REQUEST.remove(userId);
//        }
//    }
//
//    private String dealAudioUrl(String path) {
//        return Constants.TTS_DIDI3_GET+path;
////        return "http://didi-gz5.jiaoyou365.com:9663/api/audio/get?path="+path;
//    }
//
//    private void dealNoScreenSendFlowerIntent(String userId, OverTimeDetail data) {
//        if (getAccessToken() == null) {
//            data.setResponse(buildErrorResponse("该功能需要登录"));
//            return ;
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
//            data.setResponse(new Response(outputSpeech));
//        } else if ("CONFIRMED".equals(confirmationStatus)) {
//            log.info("确认");
//            int unitPrice = Integer.parseInt(ScheduleService.FLOWER_UNIT_PRICE);
//            String amount = String.valueOf(unitPrice*Integer.parseInt(num)/100.0);
//            String orderId = new StringBuilder(userId).append(System.currentTimeMillis()).toString();
//            String appUserId = getSessionAttribute("userId");
//            if (appUserId == null) {
//                appUserId = DplbotServiceUtil.getUsUserId(userId,getAccessToken());
//                if (appUserId == null) {
//                    log.error("送花时获取用户app_user_id失败");
//                    data.setResponse(buildErrorResponse("不好意思出了点问题,请再说一遍吧"));
//                    return;
//                }
//            }
//            String botId = getSessionAttribute("botAcc");
//            if (StringUtils.isNotBlank(botId)) {
//                XiaoDuOrderTbRepository repository = BaseHolder.getBean("xiaoDuOrderTbRepository");
//                XiaoDuOrderTB response = repository.save(buildNoScreenBeforePayPojo(botId, appUserId, userId, unitPrice,
//                        Integer.parseInt(num), orderId));
//
//                if (response != null && response.getId() != null) {
//                    String desc = new StringBuilder("感谢您赏赐给该作品的").append(num).append("朵鲜花").toString();
//                    String name = ScheduleService.PRODUCT_ID_DETAIL.get(botId).getName();
//                    if (name != null) desc = new StringBuilder("感谢您赏赐给").append(name)
//                            .append("的").append(num).append("朵鲜花").toString();
//                    Charge charge = new Charge(amount, orderId, num+"朵鲜花", desc);
//                    charge.setToken(new StringBuilder(botId).append(File.separator).append(num).toString());
//                    addDirective(charge);
//                    data.setResponse(buildErrorResponse("请到小度APP上完成支付"));
//                } else {
//                    log.error("订单没有插入到xioadu_order_tb");
//                    data.setResponse(buildErrorResponse(ERROR_SEND_FLOWER));
//                }
//            }else {
//                log.error("无法获取到botID");
//                data.setResponse(buildErrorResponse("只能在作品里才可以送花"));
//            }
//        } else {
//            log.info("否定");
//            data.setResponse(buildErrorResponse("好的,已为您终止"));
//        }
//    }
//
//    private Play buildNoScreenPlayDirec(String audioUrl, AudioPlayerDirective.PlayBehaviorType type) {
//        return new Play(type, audioUrl, 0);
//    }
//
//    /**
//     *
//     * @param idsToken 当前页面作品的id集合
//     * @param overDetail
//     * @return
//     */
//    private Response dealOpenNum(String idsToken, OverTimeDetail overDetail) {
//        int collectNum = Integer.parseInt(getSlot("sys.number"));
//        String[] tokenList = new String(Base64.getDecoder().decode(idsToken)).split(File.separator);
//        String botID = tokenList[collectNum-1];
//        OnProductDetail detail = ScheduleService.PRODUCT_ID_DETAIL.get(botID);
//        String productName = correctName(detail.getName());
//        String enterProductWord = new StringBuilder(Constants.PRE_PRODUCT_PLAY).append(productName).toString();
//        log.info("进入作品语句:{}", enterProductWord);
//        QiWuResponse data = null;
//        try {
//            data = RequestTerminal.requestTerminate(enterProductWord, getUserId(), type);
//        } catch (IOException e) {
//            log.error("请求终端接口出错:", e.toString());
//        }
//        if (data == null) {
//            return buildErrorResponse(SORRY_UNCATCH);
//        }
//        String text = data.getText();
////        String aside = DplbotServiceUtil.getAsideText(text);
////        text = text.substring(text.indexOf("】")+1);
////        log.info("旁白名字:{}",aside);
////        PlayDocument document = PlayUtils.getPlayDocument(productName, aside, ScheduleService.getAsideImgUrl(botID, aside),
////                data.getAudio(), text);
//        Pattern pattern = Pattern.compile(Utils.getRegexSeperate());
//        PlayDocument document = PlayUtils.getPlayDocument(productName, DplbotServiceUtil.seperateActor(text, pattern), DplbotServiceUtil.seperateActorWord(text, pattern), data.getAudio(), botID);
//
//        RenderDocument render = new RenderDocument(document);
//        this.setSessionAttribute(PRE_COLLE, null);
//        overDetail.getAttributes().put(PRE_COLLE, null);
//        String trace = getSessionAttribute(BACK_TRACE);
//        if (trace == null || trace.length() != 2) trace = "00";
//        String backTrace = new StringBuilder(trace).append(detail.getBotAccount()).append(File.separator).toString();
//        this.setSessionAttribute(BACK_TRACE, backTrace);
//        overDetail.getAttributes().put(BACK_TRACE,backTrace);
//        this.addDirective(render);
//        overDetail.getDirectives().add(render);
//        return new Response();
//    }
//
//    @Override
//    protected Response onUserEvent(UserEvent userEvent) {
//        try {
//            //log.info("onUserEvent事件触发,token:{}, componentId:{}",userEvent.getToken(),userEvent.getPayload().getComponentId());
//            if (getAccessToken() != null && getSessionAttribute("userId") == null) {
//                String userId = DplbotServiceUtil.getUsUserId(getUserId(),getAccessToken());
//                setSessionAttribute("userId", userId);
//            }
//            String componentId = userEvent.getPayload().getComponentId();
//            String componentPrefix;
//            if ("audio".equals(componentId)){
//                setExpectSpeech(true);
//            }
//            else if (Constants.PRE_PRODUCT_IMG.equals((componentPrefix = componentId.substring(0, 5)))) {
//                // 作品图片点击事件
//                log.info("图片点击触发");
//                String accessToken = getAccessToken();
//                String userId = getUserId();
//                String productName = componentId.substring(5);
//
//                OnProductDetail productDetail = ScheduleService.PRODUCT_NAME_DETAIL.get(productName);
//                if (productDetail == null && !productName.equals("清新传{/zhuan4/}")) {
//                    return buildErrorResponse("目前不支持该作品");
//                }
//                String imgUrl = new StringBuilder(Constants.IMG_PREFIX).append(productDetail.getBannerImgUrl()).toString();
//
//                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//                String dateString = formatter.format(productDetail.getCreateTime());
//                String time = new StringBuilder(Constants.PRODUCT_INTRO.CREATE_TIME).append(dateString).toString();
//                String author = new StringBuilder(Constants.PRODUCT_INTRO.AUTHOR).append(productDetail.getAuthorName()).toString();
//                StringBuilder labels = new StringBuilder(Constants.PRODUCT_INTRO.LABEL);
//                for (String label: productDetail.getLabels()) {
//                    labels.append(Constants.PRODUCT_INTRO.SPACE).append(label);
//                }
//                String intro = productDetail.getIntro();
//                String enterProName = new StringBuilder(Constants.PRE_ENTER_PRODUCT).append(productName).toString();
//
//                // "start product" container button
//                String flowers, flowerReward,collectText, collectUrl, collectComId;
//                if (accessToken != null) {
//                    // flower container button
//                    Long flowerCount = DplbotServiceUtil.getUserSendFlowerNum(productDetail.getBotAccount(), userId, accessToken);
//                    flowers = USER_SEND_FLOWER+flowerCount;
//                    flowerReward = new StringBuilder(FLOWE).append(productDetail.getBotAccount()).toString();
//
//                    // collection container button
//                    boolean collec = DplbotServiceUtil.didUserCollecThisProduct(productDetail.getBotAccount(), userId,
//                            accessToken, getSessionAttribute("userId"));
//                    if (collec) {
//                        collectText = COLLECED;
//                        collectUrl = COLLECTED_IMG_URL;
//                        collectComId = new StringBuilder(UN_COLLEC_ID).append(productDetail.getBotAccount()).toString();
//                    } else {
//                        collectText = UN_COLLEC;
//                        collectUrl = UN_COLLECTED_IMG_URL;
//                        collectComId = new StringBuilder(COLLEC_ID).append(productDetail.getBotAccount()).toString();
//                    }
//                } else {
//                    flowers = FLOWE;
//                    flowerReward = FLOWER_LOAD;
//
//                    collectText = UN_COLLEC;
//                    collectUrl = UN_COLLECTED_IMG_URL;
//                    collectComId = FLOWER_LOAD;
//                }
//
//                ProductInfoDocument document = ProductInfoUtils.getProductINfoDocument(imgUrl, correctName(productName), time, author, labels.toString(),
//                        intro, enterProName, flowerReward, flowers, collectComId, collectUrl, collectText);
//
//                RenderDocument renderDocument = new RenderDocument(document);
//                this.addDirective(renderDocument);
//                //this.addDirective(new PushStack());
//                String trace = getSessionAttribute(BACK_TRACE);
//                log.info("before trace------------:{}",trace);
//                StringBuilder builder = new StringBuilder();
//                if (trace == null || trace.length() < 2) {
//                    builder.append("00");
//                } else if (trace.length()>2) {
//                    builder.append(trace, 0, 2);
//                } else {
//                    builder.append(trace);
//                }
//
//                trace = builder.append(productDetail.getBotAccount()).toString();
//                log.info("after trace------------:{}",trace);
//                this.setSessionAttribute(BACK_TRACE, trace);
//                return new Response();
//            } else if (Constants.PRE_ENTER_PRODUCT.equals(componentPrefix)) {
//                String productName = componentId.substring(5);
//                String enterProductWord = new StringBuilder(Constants.PRE_PRODUCT_PLAY).append(productName).toString();
//                log.info("进入作品语句:{}", enterProductWord);
//                QiWuResponse data = null;
//                try {
//                    data = RequestTerminal.requestTerminate(enterProductWord, getUserId(), type);
//                } catch (IOException e) {
//                    log.error("请求终端接口出错:", e.toString());
//                }
//                if (data == null) {
//                    return buildErrorResponse(SORRY_UNCATCH);
//                }
//                String text = data.getText();
////            String aside = DplbotServiceUtil.getAsideText(text);
////            text = text.substring(text.indexOf("】")+1);
////            log.info("旁白名字:{}",aside);
////
//                String keng = productName;
//                if (keng.equals("清新传")) keng = "清新传{/zhuan4/}";
//                String botID = ScheduleService.getProductNameDetail().get(keng).getBotAccount();
////            PlayDocument document = PlayUtils.getPlayDocument(productName, aside, ScheduleService.getAsideImgUrl(botID, aside),
////                    data.getAudio(), text);
//                Pattern pattern = Pattern.compile(Utils.getRegexSeperate());
//                PlayDocument document = PlayUtils.getPlayDocument(productName, DplbotServiceUtil.seperateActor(text, pattern), DplbotServiceUtil.seperateActorWord(text, pattern), data.getAudio(), botID);
//
//                RenderDocument render = new RenderDocument(document);
//                this.setSessionAttribute(PRE_COLLE, null);
//                String trace = getSessionAttribute(BACK_TRACE);
//                if (trace == null || trace.length()<2) trace = "00";
//                else if (trace.length()>2) trace = trace.substring(0,2);
//                if (productName.equals("清新传")) productName = "清新传{/zhuan4/}";
//                String botAccount = ScheduleService.PRODUCT_NAME_DETAIL.get(productName).getBotAccount();
//                this.setSessionAttribute(BACK_TRACE, new StringBuilder(trace).append(botAccount).append(File.separator).toString());
//                this.addDirective(render);
//            } else if (FLOWE.equals(componentPrefix)) {
//                if (FLOWER_LOAD.equals(componentId)) {
//                    return load();
//                } else {
////                Buy buy = new Buy(FLOWER_PRODUCT_ID);
////                String token = new String(Base64.getEncoder().encode(componentId.substring(5).getBytes()), StandardCharsets.UTF_8);
////                buy.setToken(token);
////                addDirective(buy);
////                OutputSpeech outputSpeech = new OutputSpeech(OutputSpeech.SpeechType.PlainText, REWARD_FLOWER);
////                return new Response(outputSpeech);
//                    Charge charge = DplbotServiceUtil.sendOneFlowerByCharge(getUserId(), getSessionAttribute("userId"),
//                            getAccessToken(), getSessionAttribute("trace"));
//                    if (charge != null) {
//                        addDirective(charge);
//                        return buildErrorResponse("扫描屏幕上的二维码完成支付");
//                    }
//                    return new Response();
//                }
//            } else if ("loadingPage".equals(componentId)) {
//                RenderDocument renderDocument = new RenderDocument(LunchUtils.getLunchDocument());
//                this.addDirective(new PushStack());
//                this.addDirective(renderDocument);
//                return new Response();
//            } else if (PRE_COLLE.equals(componentPrefix)) {
//                boolean isUnCollect = "Un".equals(componentId.substring(5,7));
//                final String botId = componentId.substring(7);
//                if (isUnCollect) {
//                    //取消收藏
//                    DplbotServiceUtil.userUnCollectTheBotInPool(getUserId(), botId, getAccessToken());
//                    changeIconIdTextAfterCollect(false, botId);
//                } else {
//                    //收藏
//                    DplbotServiceUtil.userCollectTheBotInPool(getUserId(), botId, getAccessToken());
//                    changeIconIdTextAfterCollect(true, botId);
//                }
//            } else if (PRE_CATEGORY.equals(componentPrefix)) {
//                String category = componentId.substring(5);
//                // 分类推荐页面相同
//                RecommandDocument document = RecommendUtil.getCategory(0, category);
//                if (document == null) return buildErrorResponse("不好意思该分类还没有作品");
//                RenderDocument render = new RenderDocument(document);
//                this.addDirective(render);
//                this.setSessionAttribute("category",category);
//                this.setSessionAttribute("trace","05");
//            } else if ("playButton".equals(componentId)) {
//                return buildErrorResponse("您可以直接对我说我要送花或者我要收藏或取消收藏");
//            } else if ("lunchCollect".equals(componentId)) {
//                return dealOpenCollectClick();
//            } else if ("lunchHistory".equals(componentId)) {
//                return dealHistoryClick();
//            }
////        else if ("funnyChange".equals(componentId)) {
////            ExecuteCommands executeCommands = new ExecuteCommands();
////            AutoPageCommand page = new AutoPageCommand();
////            page.setComponentId("funnyPage");
////            page.setDurationInMillisecond(2000);
////            executeCommands.addCommand(page);
////            this.addDirective(executeCommands);
////        }
//        } catch (Exception e) {
//            log.error("onUserEvent error:{}", ExceptionUtils.getStackTrace(e));
//        }
//        return new Response();
//    }
//
//    @Override
//    protected Response onSessionEnded(SessionEndedRequest sessionEndedRequest) {
//        switch (sessionEndedRequest.getReason()) {
//            case ERROR: dealWithError(sessionEndedRequest); break;
//            case USER_INITIATED: log.info(EndMsg.USER_SAY_CANCEL); break;
//            case EXCEEDED_MAX_REPROMPTS: log.info(EndMsg.CAN_NOT_UNDERSTAND);break;
//        }
//        if (getRequest().getContext().getScreen() == null) {
//            String userId = customUserId();
////            LAST_OUTTIME_MARK.remove(userId);
////            LAST_OUTTIME_RESULT.remove(userId);
////            USERID_AUDIO_PATH.remove(userId);
//            String byeWord = EndMsg.END_BYE_Adult;
//            switch (type) {
//                case 2: byeWord = EndMsg.END_BYE_NOVEL;break;
//                case 1: byeWord = EndMsg.END_BYE_STORY;break;
//                case 3: byeWord = EndMsg.END_BYE_CHILD;break;
//                case 5: byeWord = EndMsg.END_BYE_JIAOYOU;break;
//            }
//            OutputSpeech outputSpeech = new OutputSpeech(OutputSpeech.SpeechType.PlainText, byeWord);
//            RequestTerminal.backRequest(userId, type);
////            try {
////                if (DU_DEVICE_ID.equals(deviceId)) {
////                    RequestTerminal.backRequest(DU_DEVICE_ID, type);
////                } else {
////                    RequestTerminal.backRequestDU2(deviceId, type);
////                }
////            } catch (Exception e) {
////                log.warn("用户退出向中控发出退出指令出错");
////            }
//            return new Response(outputSpeech);
//        }
//        DplbotServiceUtil.writeTheUserHistoryInData(getUserId());
//        LAST_OUTTIME_RESULT.remove(getUserId());
//        return new Response();
//    }
//
//    private void dealWithError(SessionEndedRequest request) {
//        switch (request.getError().getType()) {
//            case INTERNAL_ERROR: log.info(ErrorMsg.INTERNAL_ERROR); break;
//            case INVALID_RESPONSE: log.info(ErrorMsg.INVALID_RESPONSE); break;
//            case DEVICE_COMMUNICATION_ERROR: log.info(ErrorMsg.DEVICE_COMMUNICATION_ERROR); break;
//        }
//        log.info("错误信息 : "+ request.getError().getMessage());
//    }
//
//    private Response dealHistoryClick() {
//        CollectHistoryDocument document = DplbotServiceUtil.getHistory(getUserId(), getAccessToken(), 0);
//        if (document == null) return buildErrorResponse("不好意思，您太久没玩记录被清空了");
//        RenderDocument render = new RenderDocument(document);
//        addDirective(render);
//        setSessionAttribute(PRE_COLLE,document.getIdToken());
//        setSessionAttribute("recommand", null);
//        setSessionAttribute("comCnt", null);
//        setSessionAttribute(BACK_TRACE, "02");
//        OutputSpeech outputSpeech = new OutputSpeech(OutputSpeech.SpeechType.PlainText, "正在为您打开历史记录");
//        return new Response(outputSpeech);
//    }
//
//    private Response dealOpenCollectClick() {
//        if (getAccessToken() == null) {
//            OutputSpeech outputSpeech = new OutputSpeech(OutputSpeech.SpeechType.PlainText, "该功能需要登录,请说小度小度,登录");
//            return new Response(outputSpeech);
//        } else {
//            String appUserId = getSessionAttribute("userId");
//            if (appUserId == null) {
//                appUserId = DplbotServiceUtil.getUsUserId(getUserId(), getAccessToken());
//                if (appUserId == null) {
//                    return new Response();
//                }
//            }
//            CollectHistoryDocument document = DplbotServiceUtil.getCollectPage(getUserId(), getAccessToken(), appUserId, 0);
//            if (document != null) {
//                RenderDocument render = new RenderDocument(document);
//                addDirective(render);
//                setSessionAttribute("comCnt", null);
//                setSessionAttribute(PRE_COLLE,document.getIdToken());
//                setSessionAttribute(BACK_TRACE, "01");
//                OutputSpeech outputSpeech = new OutputSpeech(OutputSpeech.SpeechType.PlainText, "正在为您打开收藏夹");
//                return new Response(outputSpeech);
//            }
//        }
//        return new Response();
//    }
//
////    @Override
////    protected Response onBuyEvent(BuyEvent buyEvent) {
////        com.baidu.dueros.data.request.buy.event.JYTUserResponse payload = buyEvent.getPayload();
////        String result = payload.getPurchaseResult().getPurchaseResult();
////        log.info("此次支付结果:"+result+";msg: "+payload.getMessage());
////        if (result.equals("SUCCESS")) {
////            log.info("支付成功");
////            POOL.submit(new SendFlowerThread(buyEvent));
////            POOL.submit(new SendFlowerBuyToXiaoduTB(buyEvent));
////            return buildErrorResponse("感谢大人赏的花");
////        }
////        return buildErrorResponse("哎呀，没有支付成功");
////    }
//
//
//    private ExecuteCommands changeIconIdTextAfterCollect(boolean collect, String botId) {
//        ExecuteCommands executeCommands = new ExecuteCommands();
//        UpdateComponentCommand update = new UpdateComponentCommand();
//        String srcValue;
//        String textValue;
//        String idValue;
//        String previousClickId;
//        String unCollectId = new StringBuilder(UN_COLLEC_ID).append(botId).toString();
//        String collectId = new StringBuilder(COLLEC_ID).append(botId).toString();
//        if (collect) {
//            srcValue = COLLECTED_IMG_URL;
//            textValue = COLLECED;
//            idValue = unCollectId;
//            previousClickId = collectId;
//        } else {
//            srcValue = UN_COLLECTED_IMG_URL;
//            textValue = UN_COLLEC;
//            idValue = collectId;
//            previousClickId = unCollectId;
//        }
//
//        update.setComponentId(previousClickId);
//        update.setDocument(TempleUtils.getCollectIcon(idValue, srcValue, textValue));
//        executeCommands.addCommand(update);
//        this.addDirective(executeCommands);
//        return executeCommands;
//    }
//
//    private ExecuteCommands changeCollectIconInPlay(boolean collect) {
//        ExecuteCommands executeCommands = new ExecuteCommands();
//        SetStateCommand setState = new SetStateCommand();
//        setState.setComponentId("playCollect");
//        setState.setState("src");
//        if (collect) {
//            setState.setValue(COLLECTED_IMG_URL);
//        }else {
//            setState.setValue(UN_COLLECTED_IMG_URL);
//        }
//        executeCommands.addCommand(setState);
//        return executeCommands;
//    }
//
//    private Response load() {
//        Card card = new LinkAccountCard();
//        Response response = buildErrorResponse(LOAD_INTRO);
//        response.setCard(card);
//        return response;
//    }
//
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
//
//
//    private List<CollecProduct> getUserProductList(String accessToken) {
//        HashMap<String, String> params = new HashMap<>();
//        params.put("module", "works");
//        HashMap<String, String> headers = new HashMap<>();
//        headers.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
//        headers.put("Authorization", new StringBuilder(BEARER).append(accessToken).toString());
//        UserCollecResult result;
//        try {
//            String resStr = OkHttp3Utils.doGet(Constants.XIAOWU_API.USER_CLOOEC_LIST, params, headers);
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
//
//    private static String correctName(String name) {
//        int brace = name.indexOf("{");
//        if (brace != -1) name = name.substring(0, brace);
//        return name;
//    }
//
//    @Override
//    protected Response onLinkAccountSucceededEvent(LinkAccountSucceededEvent linkAccountSucceededEvent) {
//        log.info("登录成功返回信息:{}",linkAccountSucceededEvent);
//        String accessToken = getAccessToken();
//        String jytUserId;
//        if (getRequest().getContext().getScreen() != null) {
//            log.info("检测到有屏登录");
//            String userId = getUserId();
//            DplbotServiceUtil.onLunchFillUserBehaviorData(accessToken, userId);
//            jytUserId = DplbotServiceUtil.getUsUserId(userId, accessToken);
//            if (jytUserId != null)
//                log.warn("登录后获取用户的app id 失败");
//        } else {
//            jytUserId = DplbotServiceUtil.getUserIdByToken(accessToken);
//            if (jytUserId != null)
//                log.warn("登录后获取用户的app id 失败");
//        }
//        setSessionAttribute("userId", jytUserId);
//        return buildErrorResponse("您已登录成功");
//    }
//
////    @Override
////    protected Response onDefaultEvent() {
////        log.info("未拦截的事件类型:"+getEventType(this.getStrRequest()));
////        this.waitAnswer();
////        return new Response();
////    }
//
//    private String getEventType(String requestStr) {
//        int typeIndex = requestStr.lastIndexOf("type");
//        int comma = requestStr.indexOf(',', typeIndex);
//        return requestStr.substring(typeIndex, comma);
//    }
//
//    private Response buildErrorResponse(String errorMsg) {
//        OutputSpeech outputSpeech = new OutputSpeech(OutputSpeech.SpeechType.PlainText, errorMsg);
//        return new Response(outputSpeech);
//    }
//
//    private Response buildLogOutResponse(String errorMsg) {
//        OutputSpeech outputSpeech = new OutputSpeech(OutputSpeech.SpeechType.PlainText, errorMsg);
//        endDialog();
//        return new Response(outputSpeech);
//    }
//
//    private Response buildNoScreenListCardResponse(String content, String botAccount) {
//        Pattern pattern = Pattern.compile(Utils.getRegexSeperate());
//        List<String> actors = DplbotServiceUtil.seperateActor(content, pattern);
//        String[] actorWord = DplbotServiceUtil.seperateActorWord(content, pattern);
//        int i = 0;
//        ListCard listCard = new ListCard();
//        for (String actor: actors) {
//            StandardCardInfo cardInfo = new StandardCardInfo(actor, actorWord[i++].replaceAll("<br>","\n"));
//            cardInfo.setImage(ScheduleService.getAsideImgUrl(botAccount, actor));
//            listCard.getList().add(cardInfo);
//        }
////        content = buildText(content);
//        return new Response(null, listCard, WAIT_WORD);
//    }
//
//    private Response buildNoScreenTextCardREsponse(String content) {
//        content = buildText(content);
//        TextCard textCard = new TextCard(content);
//        return new Response(null, textCard, WAIT_WORD);
//    }
//
//    private String buildText(String text) {
//        Pattern pattern = Pattern.compile(Utils.getRegexSeperate());
//        StringBuilder builder = new StringBuilder();
//        List<String> actors = DplbotServiceUtil.seperateActor(text, pattern);
//        String[] word = DplbotServiceUtil.seperateActorWord(text, pattern);
//        int i = 0;
//        for (String actor: actors) {
//            builder.append(actor).append(":").append(word[i].replaceAll("<br>", "\n")).append("\n");
//            i++;
//        }
//        if (builder.length() == 0) builder.append(text);
//        return builder.toString();
//    }
//
//    private void backToBotIntro(String botAccount, OverTimeDetail detail) {
//        OnProductDetail productDetail = ScheduleService.PRODUCT_ID_DETAIL.get(botAccount);
//        // product picture
//        String imgUrl = new StringBuilder(Constants.IMG_PREFIX).append(productDetail.getBannerImgUrl()).toString();
//
//        // product name
//        String productName = correctName(productDetail.getName());
//
//        // product create time
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//        String dateString = formatter.format(productDetail.getCreateTime());
//        String createTime = new StringBuilder(Constants.PRODUCT_INTRO.CREATE_TIME).append(dateString).toString();
//
//        // product author
//        String author = new StringBuilder(Constants.PRODUCT_INTRO.AUTHOR).append(productDetail.getAuthorName()).toString();
//
//        // product labels
//        StringBuilder labels = new StringBuilder(Constants.PRODUCT_INTRO.LABEL);
//        for (String label: productDetail.getLabels()) {
//            labels.append(Constants.PRODUCT_INTRO.SPACE).append(label);
//        }
//
//        // product introduction
//        String intro = productDetail.getIntro();
//
//        // "start product" container button
//        String enterProName = new StringBuilder(Constants.PRE_ENTER_PRODUCT).append(productName).toString();
//
//        String accessToken = getAccessToken();
//        String flowers, flowerReward,collectText, collectUrl, collectComId;
//        if (accessToken != null) {
//            // flower container button
//            Long count = DplbotServiceUtil.getUserSendFlowerNum(botAccount, getUserId(), accessToken);
//            flowers = USER_SEND_FLOWER+count;
//            flowerReward = new StringBuilder(FLOWE).append(productDetail.getBotAccount()).toString();
//
//            // collection container button
//            boolean collec = DplbotServiceUtil.doesUserCollectThisBot(productDetail.getBotAccount(), accessToken, getUserId());
//            if (collec) {
//                collectText = COLLECED;
//                collectUrl = COLLECTED_IMG_URL;
//                collectComId = new StringBuilder(UN_COLLEC_ID).append(productDetail.getBotAccount()).toString();
//            } else {
//                collectText = UN_COLLEC;
//                collectUrl = UN_COLLECTED_IMG_URL;
//                collectComId = new StringBuilder(COLLEC_ID).append(productDetail.getBotAccount()).toString();
//            }
//        } else {
//            flowers = FLOWER;
//            flowerReward = FLOWER_LOAD;
//
//            collectText = UN_COLLEC;
//            collectUrl = UN_COLLECTED_IMG_URL;
//            collectComId = FLOWER_LOAD; // 提示登录
//        }
//
//        ProductInfoDocument document = ProductInfoUtils.getProductINfoDocument(imgUrl, correctName(productName), createTime, author, labels.toString(),
//                intro, enterProName, flowerReward, flowers, collectComId, collectUrl, collectText);
//        RenderDocument renderDocument = new RenderDocument(document);
//        this.addDirective(renderDocument);
//        detail.getDirectives().add(renderDocument);
//        String trace = getSessionAttribute(BACK_TRACE);
//        trace = trace.substring(0, trace.length()-1);
//        this.setSessionAttribute(BACK_TRACE, trace);
//        detail.getAttributes().put(BACK_TRACE,trace);
//    }
//
//    // 异步发送“退出”给终端
//    private void sendCancellToTerminate(String userId){
//        RequestTerminal.backRequest(userId, type);
//    }
//
//    class Task implements Runnable{
//        IntentRequest intentRequest;
//
//        Task(IntentRequest intentRequest) {
//            this.intentRequest = intentRequest;
//        }
//
//        @Override
//        public void run() {
//            final String userId = getUserId();
//            String intentName = getIntent().getName();
//            OverTimeDetail detail = new OverTimeDetail();
//            log.info("Intent name:{}",intentName);
//            if ("ai.dueros.common.default_intent".equals(intentName) || "funny".equals(intentName)) {
//                dealDefaultIntent(intentRequest, detail, userId, intentName, null);
//                return;
//            }
//            switch (intentName) {
//                case "load"        :  dealLoadIntent(detail, userId);break;
//                case "back"        :  dealBackIntent(detail, userId);break;
//                case "openCollect" :  dealOpenCollectIntent(detail, userId);break;
//                case "openNum"     :  dealOpenNumIntent(detail, userId);break;
//                case "lastPage"    :  dealLastPageIntent(detail, userId, intentRequest);break;
//                case "nextPage"    :  dealNextPageIntent(detail, userId, intentRequest);break;
//                case "history"     :  dealHistoryIntent(detail, userId);break;
//                case "recommand"   :  dealRecommandIntent(detail, userId);break;
//                case "ranking"     :  dealRankIntent(detail, userId);break;
//                case "collect"     :
//                case "unCollect"   :  dealCollectOrUnCollectIntent(detail, userId, intentName);break;
//                case "sendFlower"  :  dealSendFlowerIntent(detail, userId);break;
//                case "jump"        :  dealJumpIntent(detail, userId);break;
//                case "start"       :  dealStartIntent(detail, userId, intentRequest, intentName);break;
//            }
//        }
//    }
//
//    private void dealJumpIntent(OverTimeDetail detail, String userId) {
//        String trace = getSessionAttribute("trace");
//        if (trace == null || trace.length() < 2) {
//            detail.setResponse(buildErrorResponse("小悟跳不动了"));
//            LAST_OUTTIME_RESULT.put(userId,detail);
//            return;
//        }
//        String num = getSlot("sys.number");
//        if (num == null) {
//            detail.setResponse(buildErrorResponse("我没听清您要跳转的页面,请再说一遍"));
//            LAST_OUTTIME_RESULT.put(userId,detail);
//            return;
//        }
//        int index = Integer.parseInt(num);
//        char pageSign = trace.charAt(1);
//        switch (pageSign) {
//            case '1' : {
//                dealCollectHistoryJump(index, true, detail, userId);
//                break;
//            }
//            case '2' : {
//                dealCollectHistoryJump(index, false, detail, userId);
//                break;
//            }
//            case '4' : {
//                dealRankJump(index, detail);
//                break;
//            }
//            case '5' : {
//                dealCategoryJump(index, detail);
//                break;
//            }
//            default  : {
//                detail.setResponse(buildErrorResponse("该页面不支持跳转"));
//            }
//        }
//        LAST_OUTTIME_RESULT.put(userId,detail);
//    }
//
//    private void dealRankJump(int index, OverTimeDetail detail) {
//        index = index == 0 ? index : index-1;
//        RecommandDocument document;
//        if (index == 0) document = RecommendUtil.getRankDocumentFirstPage();
//        else document = RecommendUtil.getRankOtherPageDocument(index);
//        if (document == null) {
//            detail.setResponse(buildErrorResponse("超出了范围,无法跳转"));
//            return;
//        }
//        RenderDocument render = new RenderDocument(document);
//        addDirective(render);
//        detail.getDirectives().add(render);
//        String cmt = String.valueOf(index+1);
//        setSessionAttribute("comCnt",cmt);
//        setSessionAttribute("recommand", document.getRecommendIdList());
//        detail.getAttributes().put("recommand", document.getRecommendIdList());
//        detail.getAttributes().put("comCnt", cmt);
//    }
//
//    private void dealCategoryJump(int index, OverTimeDetail detail) {
//        index = index == 0 ? index : index-1;
//        String category = getSessionAttribute("category");
//        if (category == null) {
//            detail.setResponse(buildErrorResponse("无法跳转,请说返回然后重新进入当前分类"));
//            return;
//        }
//        RecommandDocument document = RecommendUtil.getCategory(index, category);
//        if (document == null) {
//            detail.setResponse(buildErrorResponse("超出了范围,无法跳转"));
//            return;
//        }
//        RenderDocument render = new RenderDocument(document);
//        addDirective(render);
//        detail.getDirectives().add(render);
//        String cmt = String.valueOf(index+1);
//        setSessionAttribute("comCnt",cmt);
//        detail.getAttributes().put("comCnt", cmt);
//    }
//
//    private void dealCollectHistoryJump(int index, boolean collect, OverTimeDetail detail, String userId) {
//        index = index == 0 ? index : index-1;
//        int start = index*CO_HI_PAGE_SIZE;
//        CollectHistoryDocument document;
//        if (collect) document = DplbotServiceUtil.getCollectPage(userId, getAccessToken(), getSessionAttribute("userId"), start);
//        else document = DplbotServiceUtil.getHistory(userId, getAccessToken(), start);
//        if (document != null) {
//            RenderDocument render = new RenderDocument(document);
//            addDirective(render);
//            detail.getDirectives().add(render);
//            String cmt = String.valueOf(index+1);
//            setSessionAttribute("comCnt",cmt);
//            setSessionAttribute(PRE_COLLE, document.getIdToken());
//            detail.getAttributes().put(PRE_COLLE, document.getIdToken());
//            detail.getAttributes().put("comCnt", cmt);
//        } else {
//            detail.setResponse(buildErrorResponse("超出了范围无法跳到那一页"));
//        }
//    }
//
//    private void dealStartIntent(OverTimeDetail detail, String userId, IntentRequest intentRequest, String intentName) {
//        String trace = getSessionAttribute("trace");
//        if (trace != null && trace.length() > 2 && !StringUtils.endsWith(trace,File.separator)) {
//            String botId = trace.substring(2);
//            String enterName = new StringBuilder("我要玩")
//                    .append(ScheduleService.getProductIdDetail().get(botId).getName()).toString();
//            dealDefaultIntent(intentRequest, detail, userId, intentName, enterName);
//        }else {
//            dealDefaultIntent(intentRequest, detail, userId, intentName, null);
//        }
//    }
//
//    private void dealSendFlowerIntent(OverTimeDetail detail, String userId) {
//        if (getAccessToken() == null) {
//            OutputSpeech outputSpeech = new OutputSpeech(OutputSpeech.SpeechType.PlainText, "该功能需要登录,请说小度小度,登录");
//            detail.setResponse(new Response(outputSpeech));
//            LAST_OUTTIME_RESULT.put(userId, detail);
//            return;
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
//            detail.getResponse().setOutputSpeech(outputSpeech);
//            LAST_OUTTIME_RESULT.put(userId, detail);
//        } else if ("CONFIRMED".equals(confirmationStatus)) {
//            log.info("确认");
//            int unitPrice = Integer.parseInt(ScheduleService.FLOWER_UNIT_PRICE);
//            String amount = String.valueOf(unitPrice*Integer.parseInt(num)/100.0);
//            String orderId = new StringBuilder(userId).append(System.currentTimeMillis()).toString();
//            String appUserId = getSessionAttribute("userId");
//            if (appUserId == null) {
//                appUserId = DplbotServiceUtil.getUsUserId(userId,getAccessToken());
//                if (appUserId == null) {
//                    log.error("送花时获取用户app_user_id失败");
//                    detail.setResponse(buildErrorResponse("不好意思出了点问题,请再说一遍吧"));
//                    LAST_OUTTIME_RESULT.put(userId, detail);
//                    return;
//                }
//            }
//            String botId;
//            String trace = getSessionAttribute(BACK_TRACE);
//            if (trace != null && trace.length() > 2) {
//                if (StringUtils.endsWith(trace, File.separator)) {
//                    botId = trace.substring(2, trace.length()-1);
//                }else {
//                    botId = trace.substring(2);
//                }
//                XiaoDuOrderTbRepository repository = BaseHolder.getBean("xiaoDuOrderTbRepository");
//                XiaoDuOrderTB response = repository.save(buildBeforePayPojo(botId, appUserId, userId, unitPrice,
//                        Integer.parseInt(num), orderId));
//
//                if (response != null && response.getId() != null) {
//                    String desc = new StringBuilder("感谢您赏赐给该作品的").append(num).append("朵鲜花").toString();
//                    String name = ScheduleService.PRODUCT_ID_DETAIL.get(botId).getName();
//                    if (name != null) desc = new StringBuilder("感谢您赏赐给").append(name)
//                            .append("的").append(num).append("朵鲜花").toString();
//                    Charge charge = new Charge(amount, orderId, "鲜花", desc);
//                    charge.setToken(new StringBuilder(botId).append(File.separator).append(num).toString());
//                    addDirective(charge);
//                    detail.getDirectives().add(charge);
//                    detail.setResponse(buildErrorResponse("请扫描二维码完成支付"));
//                } else {
//                    log.error("订单没有插入到xioadu_order_tb");
//                    detail.setResponse(buildErrorResponse(ERROR_SEND_FLOWER));
//                }
//            }else {
//                log.error("无法获取到botID");
//                detail.setResponse(buildErrorResponse(ERROR_SEND_FLOWER));
//            }
//            LAST_OUTTIME_RESULT.put(userId, detail);
//        } else {
//            log.info("否定");
//            detail.setResponse(buildErrorResponse("好的,已为您终止"));
//            LAST_OUTTIME_RESULT.put(userId, detail);
//        }
//    }
//
//    private void dealCollectOrUnCollectIntent(OverTimeDetail detail, String userId, String intentName) {
//        if (getAccessToken() == null) {
//            OutputSpeech outputSpeech = new OutputSpeech(OutputSpeech.SpeechType.PlainText, "该功能需要登录,请说小度小度,登录");
//            detail.setResponse(new Response(outputSpeech));
//            LAST_OUTTIME_RESULT.put(userId, detail);
//            return;
//        }
//        boolean collect = "collect".equals(intentName);
//        boolean inProductInfo = false;
//        boolean inPlayInfo = false;
//        String botAccount;
//        String trace = getSessionAttribute("trace");
//        if (trace == null || trace.length() <= 2) {
//            botAccount = getSessionAttribute("botAcc");
//            if(botAccount == null) {
//                String word = COLLECT_FAIL;
//                if (!collect) word = UN_COLLECT_FAIL;
//                detail.setResponse(buildErrorResponse(word));
//                LAST_OUTTIME_RESULT.put(userId, detail);
//                return;
//            }
//        } else {
//            if (StringUtils.endsWith(trace, File.separator)) {
//                botAccount = trace.substring(2, trace.length()-1);
//                inPlayInfo = true;
//            }
//            else {
//                inProductInfo = true;
//                botAccount = trace.substring(2);
//            }
//        }
//        try {
//            boolean alreadyCollected = DplbotServiceUtil.doesUserCollectThisBot(botAccount, getAccessToken(), userId);
//            String appUserId = getSessionAttribute("userId");
//            if (appUserId == null) appUserId = DplbotServiceUtil.getUsUserId(userId,getAccessToken());
//            if (appUserId != null) {
//                if (alreadyCollected) {
//                    if (collect) detail.setResponse(buildErrorResponse("您之前就已经收藏过该作品啦,看来你真的很喜欢它"));
//                    else {
//                        DplbotServiceUtil.userUnCollectTheBotInPool(userId, botAccount, getAccessToken());
//                        String botName = ScheduleService.PRODUCT_ID_DETAIL.get(botAccount).getName();
//                        if (inProductInfo) {
//                            ExecuteCommands commands = changeIconIdTextAfterCollect(false, botAccount);
//                            addDirective(commands);
//                            detail.getDirectives().add(commands);
//                        } else if (inPlayInfo) {
//                            changeCollectIconInPlay(false);
//                        }
//                        if (botName == null) detail.setResponse(buildErrorResponse(UN_COLLECT_BOT_WORD));
//                        else detail.setResponse(buildErrorResponse(String.format(UN_COLLECT_BOT_WORD_MG,botName)));
//                    }
//                } else {
//                    if (collect) {
//                        DplbotServiceUtil.userCollectTheBotInPool(userId, botAccount, getAccessToken());
//                        String word = ScheduleService.PRODUCT_ID_DETAIL.get(botAccount).getName();
//                        if (inProductInfo) {
//                            ExecuteCommands commands = changeIconIdTextAfterCollect(true, botAccount);
//                            addDirective(commands);
//                            detail.getDirectives().add(commands);
//                        } else if (inPlayInfo) {
//                            changeCollectIconInPlay(true);
//                        }
//                        if (word == null) detail.setResponse(buildErrorResponse(COLLECT_BOT_WORD));
//                        else detail.setResponse(buildErrorResponse(String.format(COLLECT_BOT_WORD_MG, correctName(word))));
//                    }else {
//                        detail.setResponse(buildErrorResponse("您还未收藏过该作品,它不在您的收藏列表内"));
//                    }
//                }
//            }else {
//                detail.setResponse(buildErrorResponse(SORRY_UNCATCH));
//            }
//        } catch (Exception e) {
//            log.error("收藏/取消收藏出错:{}",e.toString());
//            detail.setResponse(buildErrorResponse(SORRY_UNCATCH));
//        }
//        LAST_OUTTIME_RESULT.put(userId, detail);
//    }
//
//    private void dealRankIntent(OverTimeDetail detail, String userId) {
//        RecommandDocument document = RecommendUtil.getRankDocumentFirstPage();
//        RenderDocument render = new RenderDocument();
//        render.setDocument(document);
//        addDirective(render);
//        detail.getDirectives().add(render);
//        String token = document.getRecommendIdList();
//        if (token != null) {
//            setSessionAttribute("recommand", token);
//            setSessionAttribute(PRE_COLLE, null);
//            setSessionAttribute("comCnt", null);
//
//            detail.getAttributes().put("recommand", token);
//            detail.getAttributes().put(PRE_COLLE, null);
//            detail.getAttributes().put("comCnt", null);
//        }
//        setSessionAttribute(BACK_TRACE, "04");
//        detail.getAttributes().put(BACK_TRACE, "04");
//        LAST_OUTTIME_RESULT.put(userId, detail);
//    }
//
//    private void dealRecommandIntent(OverTimeDetail detail, String userId) {
//        RecommandDocument recommandDocument = RecommendUtil.getRecommendDocument(0);
//        RenderDocument render = new RenderDocument();
//        render.setDocument(recommandDocument);
//        addDirective(render);
//        detail.getDirectives().add(render);
//        String token = recommandDocument.getRecommendIdList();
//        if (token != null) {
//            setSessionAttribute("recommand", token);
//            setSessionAttribute(PRE_COLLE, null);
//            setSessionAttribute("comCnt", null);
//
//            detail.getAttributes().put("comCnt", null);
//            detail.getAttributes().put("recommand", token);
//            detail.getAttributes().put(PRE_COLLE, null);
//        }
//        setSessionAttribute(BACK_TRACE, "03");
//        detail.getAttributes().put(BACK_TRACE,"03");
//        LAST_OUTTIME_RESULT.put(userId, detail);
//    }
//
//    private void dealHistoryIntent(OverTimeDetail detail, String userId) {
//        CollectHistoryDocument document = DplbotServiceUtil.getHistory(getUserId(), getAccessToken(), 0);
//        if (document == null) {
//            detail.setResponse(buildErrorResponse("没有更多了"));
//            LAST_OUTTIME_RESULT.put(userId, detail);
//            return;
//        }
//        RenderDocument render = new RenderDocument(document);
//        addDirective(render);
//        //dplBotService.addDirective(new PushStack());
//        setSessionAttribute(PRE_COLLE,document.getIdToken());
//        setSessionAttribute("recommand", null);
//        setSessionAttribute("comCnt", null);
//        setSessionAttribute(BACK_TRACE, "02");
//
//        detail.getDirectives().add(render);
//        detail.getAttributes().put("comCnt", null);
//        detail.getAttributes().put("recommand", null);
//        detail.getAttributes().put(PRE_COLLE,document.getIdToken());
//        detail.getAttributes().put(BACK_TRACE, "02");
//
//        LAST_OUTTIME_RESULT.put(userId, detail);
//    }
//
//    private void dealNextPageIntent(OverTimeDetail detail, String userId, IntentRequest intentRequest) {
//        String signPage = getSessionAttribute("trace");
//        if (signPage == null || signPage.length() < 2) {
//            detail.setResponse(buildErrorResponse("哎呀,小悟累啦,滑不动了"));
//            LAST_OUTTIME_RESULT.put(userId,detail);
//            return;
//        }
//        char sign = signPage.charAt(1);
//        switch (sign) {
//            case '1': {
//                String recommendNextCount = getSessionAttribute("comCnt");
//                if (recommendNextCount == null) {
//                    recommendNextCount = "1";
//                }
//                int count = Integer.parseInt(recommendNextCount);
//                int start = count*CO_HI_PAGE_SIZE;
//                CollectHistoryDocument document = DplbotServiceUtil.getCollectPage(userId, getAccessToken(),
//                        getSessionAttribute("userId"), start);
//                if (document != null) {
//                    RenderDocument render = new RenderDocument(document);
//                    addDirective(render);
//                    detail.getDirectives().add(render);
//                    String cmt = String.valueOf(count+1);
//                    setSessionAttribute("comCnt",cmt);
//                    setSessionAttribute(PRE_COLLE, document.getIdToken());
//                    detail.getAttributes().put(PRE_COLLE,document.getIdToken());
//                    detail.getAttributes().put("comCnt", cmt);
//                } else {
//                    detail.setResponse(buildErrorResponse("已经到最后一页了"));
//                }
//                break;
//            }
//            case '2': {
//                String recommendNextCount = getSessionAttribute("comCnt");
//                if (recommendNextCount == null) {
//                    recommendNextCount = "1";
//                }
//                int count = Integer.parseInt(recommendNextCount);
//                int start = count*CO_HI_PAGE_SIZE;
//                CollectHistoryDocument document = DplbotServiceUtil.getHistory(userId, getAccessToken(), start);
//                if (document != null) {
//                    RenderDocument render = new RenderDocument(document);
//                    addDirective(render);
//                    detail.getDirectives().add(render);
//                    String cmt = String.valueOf(count+1);
//                    setSessionAttribute("comCnt",cmt);
//                    setSessionAttribute(PRE_COLLE, document.getIdToken());
//                    detail.getAttributes().put(PRE_COLLE,document.getIdToken());
//                    detail.getAttributes().put("comCnt", cmt);
//                } else {
//                    detail.setResponse(buildErrorResponse("已经到最后一页了"));
//                }
//                break;
//            }
//            case '3': {
//                String recommendNextCount = getSessionAttribute("comCnt");
//                int count = 1;
//                if (recommendNextCount != null) count = Integer.parseInt(recommendNextCount);
//                RecommandDocument document = RecommendUtil.getRecommendDocument(count);
//                if (document == null) {
//                    detail.setResponse(buildErrorResponse("客观你已经翻到底了,没有更多了"));
//                    break;
//                }
//                RenderDocument render = new RenderDocument(document);
//                addDirective(render);
//                detail.getDirectives().add(render);
//                String token = document.getRecommendIdList();
//                setSessionAttribute("recommand", token);
//                detail.getAttributes().put("recommand",token);
//                setSessionAttribute("comCnt",String.valueOf(count+1));
//                detail.getAttributes().put("comCnt",String.valueOf(count+1));
//                break;
//            }
//            case '4': {
//                String recommendNextCount = getSessionAttribute("comCnt");
//                int count = 1;
//                if (recommendNextCount != null) count = Integer.parseInt(recommendNextCount);
//                RecommandDocument document = RecommendUtil.getRankOtherPageDocument(count);
//                if (document == null) {
//                    detail.setResponse(buildErrorResponse("客观你已经翻到底了,没有更多了"));
//                    break;
//                }
//                RenderDocument render = new RenderDocument(document);
//                addDirective(render);
//                detail.getDirectives().add(render);
//                String token = document.getRecommendIdList();
//                setSessionAttribute("recommand", token);
//                detail.getAttributes().put("recommand",token);
//                setSessionAttribute("comCnt",String.valueOf(count+1));
//                detail.getAttributes().put("comCnt",String.valueOf(count+1));
//                break;
//            }
//            case '5':{
//                String category = getSessionAttribute("category");
//                if (category == null) category = "古风";
//                String categoryNextCount = getSessionAttribute("comCnt");
//                int count = 1;
//                if (categoryNextCount != null) count = Integer.parseInt(categoryNextCount);
//                RecommandDocument document = RecommendUtil.getCategory(count,category);
//                if (document == null) {
//                    detail.setResponse(buildErrorResponse("客观你已经翻到底了,没有更多了"));
//                    break;
//                }
//                RenderDocument render = new RenderDocument(document);
//                addDirective(render);
//                detail.getDirectives().add(render);
//                String countStr = String.valueOf(count+1);
//                setSessionAttribute("comCnt",countStr);
//                detail.getAttributes().put("comCnt",countStr);
//                break;
//            }
//            default:{
//                detail.setResponse(buildErrorResponse("该功能还没实现"));
//            }
//        }
//        LAST_OUTTIME_RESULT.put(userId,detail);
//    }
//
//    private void dealLastPageIntent(OverTimeDetail detail, String userId, IntentRequest intentRequest) {
//        String signPage = getSessionAttribute("trace");
//        if (signPage == null || signPage.length() < 2) {
//            detail.setResponse(buildErrorResponse("不好意思,小悟累啦,滑不动了"));
//            LAST_OUTTIME_RESULT.put(userId,detail);
//            return;
//        }
//        char sign = signPage.charAt(1);
//        switch (sign) {
//            case '1':{
//                String recommendNextCount = getSessionAttribute("comCnt");
//                if (recommendNextCount == null || "1".equals(recommendNextCount)) {
//                    detail.setResponse(buildErrorResponse("客观,前面没啦,没法往前翻了"));
//                    break;
//                }
//                int count = Integer.parseInt(recommendNextCount);
//                int start = (count-2)*CO_HI_PAGE_SIZE;
//                CollectHistoryDocument document = DplbotServiceUtil.getCollectPage(userId, getAccessToken(),
//                        getSessionAttribute("userId"), start);
//                if (document != null) {
//                    RenderDocument render = new RenderDocument(document);
//                    addDirective(render);
//                    detail.getDirectives().add(render);
//                    String cmt = String.valueOf(count-1);
//                    setSessionAttribute("comCnt",cmt);
//                    setSessionAttribute(PRE_COLLE, document.getIdToken());
//                    detail.getAttributes().put(PRE_COLLE, document.getIdToken());
//                    detail.getAttributes().put("comCnt", cmt);
//                }
//                break;
//            }
//            case '2':{
//                String recommendNextCount = getSessionAttribute("comCnt");
//                if (recommendNextCount == null || "1".equals(recommendNextCount)) {
//                    detail.setResponse(buildErrorResponse("客观,前面没啦,没法往前翻了"));
//                    break;
//                }
//                int count = Integer.parseInt(recommendNextCount);
//                int start = (count-2)*CO_HI_PAGE_SIZE;
//                CollectHistoryDocument document = DplbotServiceUtil.getHistory(userId, getAccessToken(), start);
//                if (document != null) {
//                    RenderDocument render = new RenderDocument(document);
//                    addDirective(render);
//                    detail.getDirectives().add(render);
//                    String cmt = String.valueOf(count-1);
//                    setSessionAttribute("comCnt",cmt);
//                    setSessionAttribute(PRE_COLLE, document.getIdToken());
//                    detail.getAttributes().put(PRE_COLLE, document.getIdToken());
//                    detail.getAttributes().put("comCnt", cmt);
//                }
//                break;
//            }
//            case '3': {
//                String recommendNextCount = getSessionAttribute("comCnt");
//                if (recommendNextCount == null || "1".equals(recommendNextCount)) {
//                    detail.setResponse(buildErrorResponse("客观,前面没啦,没法往前翻了"));
//                    break;
//                }
//                int count = Integer.parseInt(recommendNextCount);
//                RecommandDocument document = RecommendUtil.getRecommendDocument(count-2);
//                RenderDocument render = new RenderDocument(document);
//                addDirective(render);
//                detail.getDirectives().add(render);
//                String token = document.getRecommendIdList();
//                setSessionAttribute("recommand", token);
//                detail.getAttributes().put("recommand",token);
//                setSessionAttribute("comCnt",String.valueOf(count-1));
//                detail.getAttributes().put("comCnt",String.valueOf(count-1));
//                break;
//            }
//            case '4': {
//                String recommendNextCount = getSessionAttribute("comCnt");
//                if (recommendNextCount == null || "1".equals(recommendNextCount)) {
//                    detail.setResponse(buildErrorResponse("客观,前面没啦,没法往前翻了"));
//                    break;
//                }
//                int count = Integer.parseInt(recommendNextCount);
//                RecommandDocument document;
//                if (count == 2) document = RecommendUtil.getRankDocumentFirstPage();
//                else document = RecommendUtil.getRankOtherPageDocument(count-2);
//                RenderDocument render = new RenderDocument(document);
//                addDirective(render);
//                detail.getDirectives().add(render);
//                String token = document.getRecommendIdList();
//                setSessionAttribute("recommand", token);
//                detail.getAttributes().put("recommand",token);
//                setSessionAttribute("comCnt",String.valueOf(count-1));
//                detail.getAttributes().put("comCnt",String.valueOf(count-1));
//                break;
//            }
//            case '5':{
//                String category = getSessionAttribute("category");
//                if (category == null) category = "古风";
//                String recommendNextCount = getSessionAttribute("comCnt");
//                if (recommendNextCount == null || "1".equals(recommendNextCount)) {
//                    detail.setResponse(buildErrorResponse("客观,前面没啦,没法往前翻了"));
//                    break;
//                }
//                int count = Integer.parseInt(recommendNextCount);
//                RecommandDocument document = RecommendUtil.getCategory(count-2,category);
//                RenderDocument render = new RenderDocument(document);
//                addDirective(render);
//                detail.getDirectives().add(render);
//                setSessionAttribute("comCnt",String.valueOf(count-1));
//                detail.getAttributes().put("comCnt",String.valueOf(count-1));
//                break;
//            }
//            default:{
//                detail.setResponse(buildErrorResponse("该功能还没实现"));
//            }
//        }
//        LAST_OUTTIME_RESULT.put(userId,detail);
//    }
//
//    private void dealOpenNumIntent(OverTimeDetail detail, String userId) {
//        String trace = getSessionAttribute("trace");
//        if (trace == null || trace.charAt(1) == '0') {
//            String numStr = getSlot("sys.number");
//            int num = 1;
//            if (numStr != null) num = Integer.parseInt(numStr);
//            detail.setResponse(dealOpenNumInHome(num, detail));
//            LAST_OUTTIME_RESULT.put(userId, detail);
//            return;
//        } else if (trace.length() > 2) {
//            detail.setResponse(buildErrorResponse("客官,您在这个页面想打开啥"));
//            LAST_OUTTIME_RESULT.put(userId, detail);
//            return;
//        }
//        char page = trace.charAt(1);
//        switch (page){
//            case '1':
//            case '2': detail.setResponse(dealOpenNum(getSessionAttribute(PRE_COLLE), detail)); break;
//            case '3': detail.setResponse(dealOpenNumInRecommend(getSessionAttribute("recommand"),detail)); break;
//            case '4': detail.setResponse(dealOpenNumInRecommend(getSessionAttribute("recommand"),detail)); break;
//            case '5': detail.setResponse(dealOpenNumInCategory(userId, detail));break;
//            case '6': detail.setResponse(dealOpenNumInRecommend(getSessionAttribute("recommand"), detail));break;
//            default:{
//                OutputSpeech outputSpeech = new OutputSpeech(OutputSpeech.SpeechType.PlainText, "屏幕上的图标都是可以点击的哦");
//                detail.getResponse().setOutputSpeech(outputSpeech);
//            }
//        }
//        LAST_OUTTIME_RESULT.put(userId, detail);
//    }
//
//    private void dealOpenCollectIntent(OverTimeDetail detail, String userId) {
//        if (getAccessToken() == null) {
//            OutputSpeech outputSpeech = new OutputSpeech(OutputSpeech.SpeechType.PlainText, "该功能需要登录,请说小度小度,登录");
//            detail.setResponse(new Response(outputSpeech));
//            LAST_OUTTIME_RESULT.put(userId, detail);
//        } else {
//            String appUserId = getSessionAttribute("userId");
//            if (appUserId == null) {
//                appUserId = DplbotServiceUtil.getUsUserId(userId, getAccessToken());
//                if (appUserId == null) {
//                    OutputSpeech outputSpeech = new OutputSpeech(OutputSpeech.SpeechType.PlainText, SORRY_UNCATCH);
//                    detail.setResponse(new Response(outputSpeech));
//                    LAST_OUTTIME_RESULT.put(userId, detail);
//                    return;
//                }
//            }
//            CollectHistoryDocument document = DplbotServiceUtil.getCollectPage(userId, getAccessToken(), appUserId, 0);
//            if (document == null) {
//                backToHome(detail);
//                detail.setResponse(buildErrorResponse("您还没有收藏任何作品"));
//                return;
//            }
//            RenderDocument render = new RenderDocument(document);
//            addDirective(render);
//            setSessionAttribute(PRE_COLLE,document.getIdToken());
//            setSessionAttribute("recommand", null);
//            setSessionAttribute(BACK_TRACE, "01");
//            setSessionAttribute("comCnt", null);
//            detail.getAttributes().put("comCnt", null);
//            detail.getAttributes().put("recommand", null);
//            detail.getAttributes().put(PRE_COLLE, document.getIdToken());
//            detail.getAttributes().put(BACK_TRACE,"01");
//            detail.getDirectives().add(render);
//            LAST_OUTTIME_RESULT.put(userId, detail);
//        }
//    }
//
//    private void dealLoadIntent(OverTimeDetail detail, String userId) {
//        detail.setResponse(load());
//        LAST_OUTTIME_RESULT.put(userId, detail);
//    }
//
//    private void dealBackIntent(OverTimeDetail detail, String userId) {
//        String trace = getSessionAttribute(BACK_TRACE);
//        char second;
//        if (trace == null || trace.length() <= 2 || getQuery().contains("首")) {
//            // home page
//            backToHome(detail);
//            if (trace != null && StringUtils.endsWith(trace,File.separator)) sendCancellToTerminate(userId);
//        } else if (StringUtils.endsWith(trace,File.separator)){
//            String botAccount = trace.substring(2, trace.length()-1);
//            log.info("返回作品的id:{}", botAccount);
//            backToBotIntro(botAccount, detail);
//            sendCancellToTerminate(userId);
//        } else {
//            second = trace.charAt(1);
//            switch (second){
//                // home page
//                case '0':{
//                    backToHome(detail);
//                    break;
//                }
//                case '1': {
//                    //collect
//                    String appUserId = getSessionAttribute("userId");
//                    if (appUserId == null) {
//                        appUserId = DplbotServiceUtil.getUsUserId(userId, getAccessToken());
//                        if (appUserId == null) {
//                            backToHome(detail);
//                            break;
//                        }
//                    }
//                    String recommendNextCount = getSessionAttribute("comCnt");
//                    int count = 1;
//                    if (recommendNextCount != null) count = Integer.parseInt(recommendNextCount);
//                    CollectHistoryDocument document = DplbotServiceUtil.getCollectPage(userId, getAccessToken(),
//                            appUserId, (count-1)*CO_HI_PAGE_SIZE);
//                    if (document == null) {
//                        backToHome(detail);
//                        detail.setResponse(buildErrorResponse("您还未收藏过任何作品,快去收藏吧"));
//                        break;
//                    }
//                    RenderDocument render = new RenderDocument(document);
//                    addDirective(render);
//                    setSessionAttribute(BACK_TRACE, "01");
//                    setSessionAttribute(PRE_COLLE,document.getIdToken());
//                    detail.getAttributes().put(BACK_TRACE,"01");
//                    detail.getAttributes().put(PRE_COLLE,document.getIdToken());
//                    detail.getDirectives().add(render);
//                    break;
//                }
//                case '2': {
//                    //history
//                    String recommendNextCount = getSessionAttribute("comCnt");
//                    int count = 1;
//                    if (recommendNextCount != null) count = Integer.parseInt(recommendNextCount);
//                    CollectHistoryDocument document = DplbotServiceUtil.getHistory(getUserId(), getAccessToken(), (count-1)*CO_HI_PAGE_SIZE);
//                    if (document == null) {
//                        backToHome(detail);
//                        detail.setResponse(buildErrorResponse("哎呀,可能是您太久没玩了,历史记录已被清空"));
//                        break;
//                    }
//                    RenderDocument render = new RenderDocument(document);
//                    addDirective(render);
//                    setSessionAttribute(PRE_COLLE,document.getIdToken());
//                    setSessionAttribute(BACK_TRACE, "02");
//                    detail.getAttributes().put(BACK_TRACE,"02");
//                    detail.getAttributes().put(PRE_COLLE,document.getIdToken());
//                    detail.getDirectives().add(render);
//                    break;
//                }
//                case '3': {
//                    //recommend
//                    String recommendNextCount = getSessionAttribute("comCnt");
//                    int count = 1;
//                    if (recommendNextCount != null) count = Integer.parseInt(recommendNextCount);
//                    RecommandDocument recommandDocument = RecommendUtil.getRecommendDocument(count-1);
//                    if (recommandDocument == null) {
//                        RenderDocument render = new RenderDocument();
//                        render.setDocument(LunchUtils.getLunchDocument());
//                        addDirective(render);
//                        detail.getDirectives().add(render);
//                        setSessionAttribute(BACK_TRACE, null);
//                        detail.getAttributes().put(BACK_TRACE, null);
//                        break;
//                    }
//                    RenderDocument render = new RenderDocument();
//                    render.setDocument(recommandDocument);
//                    addDirective(render);
//                    String token = recommandDocument.getRecommendIdList();
//                    if (token != null) {
//                        setSessionAttribute("recommand", token);
//                        detail.getAttributes().put("recommand",token);
//                    }
//                    setSessionAttribute(BACK_TRACE, "03");
//                    detail.getAttributes().put(BACK_TRACE, "03");
//                    detail.getDirectives().add(render);
//                    detail.setResponse(buildErrorResponse("已为您返回到推荐页"));
//                    break;
//                }
//                case '4': {
//                    //rank
//                    String recommendNextCount = getSessionAttribute("comCnt");
//                    int count = 1;
//                    if (recommendNextCount != null) count = Integer.parseInt(recommendNextCount);
//                    RecommandDocument recommandDocument;
//                    if (count == 1) recommandDocument = RecommendUtil.getRankDocumentFirstPage();
//                    else recommandDocument = RecommendUtil.getRankOtherPageDocument(count-1);
//                    if (recommandDocument == null) {
//                        RenderDocument render = new RenderDocument();
//                        render.setDocument(LunchUtils.getLunchDocument());
//                        addDirective(render);
//                        detail.getDirectives().add(render);
//                        setSessionAttribute(BACK_TRACE, null);
//                        detail.getAttributes().put(BACK_TRACE, null);
//                        break;
//                    }
//                    RenderDocument render = new RenderDocument();
//                    render.setDocument(recommandDocument);
//                    addDirective(render);
//                    String token = recommandDocument.getRecommendIdList();
//                    if (token != null) {
//                        setSessionAttribute("recommand", token);
//                        detail.getAttributes().put("recommand",token);
//                    }
//                    setSessionAttribute(BACK_TRACE, "04");
//                    detail.getAttributes().put(BACK_TRACE, "04");
//                    detail.getDirectives().add(render);
//                    detail.setResponse(buildErrorResponse("已为您返回到排行榜"));
//                    break;
//                }
//                case '5':{
//                    String category = getSessionAttribute("category");
//                    if (category == null) {
//                        backToHome(detail);
//                    } else {
//                        String count = getSessionAttribute("comCnt");
//                        int pageIndex = 0;
//                        if (count != null) pageIndex = Integer.parseInt(count)-1;
//                        RecommandDocument document = RecommendUtil.getCategory(pageIndex, category);
//                        RenderDocument render = new RenderDocument(document);
//                        addDirective(render);
//                        detail.getDirectives().add(render);
//                        detail.setResponse(buildErrorResponse(new StringBuilder("已为您返回到")
//                                .append(category).append("分类第").append(pageIndex+1).append("页").toString()));
//                    }
//                    break;
//                }
//            }
//        }
//        LAST_OUTTIME_RESULT.put(userId, detail);
//    }
//
//    private void backToHome(OverTimeDetail detail) {
//        RenderDocument renderDocument = new RenderDocument(LunchUtils.getLunchDocument());
//        addDirective(renderDocument);
//        setSessionAttribute(BACK_TRACE,null);
//        detail.getAttributes().put(BACK_TRACE,null);
//        detail.getDirectives().add(renderDocument);
//    }
//
//    private void dealDefaultIntent(IntentRequest intentRequest, OverTimeDetail detail, String userId,
//                                   String intentName,String word) {
//        if (word == null) {
//            Query query = intentRequest.getQuery();
//            if (query != null) word = query.getOriginal();
//            if (word == null){
//                log.info("小度没有传来任何话语");
//                detail.setResponse(buildErrorResponse(SORRY_UNCATCH));
//                LAST_OUTTIME_RESULT.put(userId, detail);
//                return;
//            }
//        }
//        QiWuResponse data = null;
//        try {
//            data = RequestTerminal.requestTerminate(word, userId, type);
//        } catch (Exception e) {
//            log.error("请求终端接口出错:", e.toString());
//        }
//        if (data == null) {
//            detail.setResponse(buildErrorResponse(SORRY_UNCATCH));
//            LAST_OUTTIME_RESULT.put(userId, detail);
//            return;
//        }
//        String text = data.getText();
//        if ("funny".equals(intentName) || data.getAipioneerUsername().equals("4151097686")) {
//            RecommandDocument document = displayFunnyRecommandPage(text, data.getAudio());
//            if (document != null) {
//                document.getMainTemplate().getItems().get(2).setSrc(data.getAudio());
//                FUNNY_DEFAULT_DOCUMENT = document;
//                RenderDocument render = new RenderDocument(document);
//                addDirective(render);
//                detail.getDirectives().add(render);
//                setSessionAttribute("recommand", document.getRecommendIdList());
//                setSessionAttribute(BACK_TRACE, "06");
//                detail.getAttributes().put(BACK_TRACE, "06");
//                detail.getAttributes().put("recommand", document.getRecommendIdList());
//                LAST_OUTTIME_RESULT.put(userId, detail);
//                return;
//            }
//            log.info("funny has som situation出现问题");
//        }
////        String aside = DplbotServiceUtil.getAsideText(text);
////        //log.info("旁白名字:{}",aside);
////        //log.info("小悟返回:{}", text);
////        text = text.substring(text.indexOf("】")+1);
//
//        if (ScheduleService.getExceptBotAccount().contains(data.getAipioneerUsername())) {
//            OutputSpeech outputSpeech = new OutputSpeech(OutputSpeech.SpeechType.PlainText, text.substring(text.indexOf("】")+1));
//            detail.getResponse().setOutputSpeech(outputSpeech);
//            LAST_OUTTIME_RESULT.put(userId, detail);
//            return;
//        }
//        final String botAccount = data.getAipioneerUsername();
//        if (botAccount == null) {
//            detail.setResponse(buildErrorResponse("不支持该作品换一个吧"));
//            LAST_OUTTIME_RESULT.put(userId, detail);
//            return;
//        }
//        String beforeBotAccount = getSessionAttribute("botAcc");
//        if (!botAccount.equals(beforeBotAccount)) {
//            DplbotServiceUtil.updateTheUserBehaviorDataMark(userId, botAccount, getAccessToken());
//            String productName = ScheduleService.PRODUCT_ID_DETAIL.get(botAccount).getName();
//
////            PlayDocument document = PlayUtils.getPlayDocument(correctName(productName), aside,
////                    ScheduleService.getAsideImgUrl(botAccount, aside),
////                    data.getAudio(), text);
//            Pattern pattern = Pattern.compile(Utils.getRegexSeperate());
//            PlayDocument document = PlayUtils.getPlayDocument(productName, DplbotServiceUtil.seperateActor(text, pattern),
//                    DplbotServiceUtil.seperateActorWord(text, pattern), data.getAudio(), botAccount);
//
//            RenderDocument render = new RenderDocument(document);
//            addDirective(render);
//            detail.getDirectives().add(render);
//            DplbotServiceUtil.updateTheUserHistory(userId, botAccount, getAccessToken());
//            String trace = getSessionAttribute(BACK_TRACE);
//            if (trace == null || trace.length() != 2) trace = "00";
//            trace = new StringBuilder(trace).append(botAccount).append(File.separator).toString();
//            setSessionAttribute(BACK_TRACE, trace);
//            setSessionAttribute("botAcc", botAccount);
//            detail.getAttributes().put("botAcc", botAccount);
//            detail.getAttributes().put(BACK_TRACE,trace);
//            LAST_OUTTIME_RESULT.put(userId, detail);
//        } else {
////            ExecuteCommands executeCommands = new ExecuteCommands();
////            SetStateCommand audio = new SetStateCommand();
////            SetStateCommand textState = new SetStateCommand();
////            audio.setComponentId("audioCom");
////            audio.setState("src");
////            audio.setValue(data.getAudio());
////            textState.setComponentId("text");
////            textState.setState("text");
////            textState.setValue(text);
////
////            SetStateCommand asideImg = new SetStateCommand();
////            asideImg.setComponentId("asideImg");
////            asideImg.setState("src");
////            asideImg.setValue(ScheduleService.getAsideImgUrl(botAccount, aside));
////
////            SetStateCommand asideText = new SetStateCommand();
////            asideText.setComponentId("asideText");
////            asideText.setState("text");
////            asideText.setValue(aside);
////
////            executeCommands.addCommand(audio);
////            executeCommands.addCommand(textState);
////            executeCommands.addCommand(asideImg);
////            executeCommands.addCommand(asideText);
//
////                UpdateComponentCommand update = new UpdateComponentCommand();
////                update.setComponentId("whole");
////                update.setDocument(TempleUtils.getPlayUpdate(data.getAudio(), text));
////                executeCommands.addCommand(update);
//
////            addDirective(executeCommands);
////            detail.getDirectives().add(executeCommands);
//
//            String productName = ScheduleService.PRODUCT_ID_DETAIL.get(botAccount).getName();
////            PlayDocument document = PlayUtils.getPlayDocument(correctName(productName), aside,
////                    ScheduleService.getAsideImgUrl(botAccount, aside),
////                    data.getAudio(), text);
//            Pattern pattern = Pattern.compile(Utils.getRegexSeperate());
//            PlayDocument document = PlayUtils.getPlayDocument(productName, DplbotServiceUtil.seperateActor(text, pattern),
//                    DplbotServiceUtil.seperateActorWord(text, pattern), data.getAudio(), botAccount);
//
//            RenderDocument render = new RenderDocument(document);
//            addDirective(render);
//            detail.getDirectives().add(render);
//            LAST_OUTTIME_RESULT.put(userId, detail);
//        }
//    }
//
//    private Response dealOpenNumInRecommend(String idsToken, OverTimeDetail overDetail) {
//        if (idsToken == null) {
//            return buildErrorResponse("请重新进入当前页面");
//        }
//        int collectNum = Integer.parseInt(getSlot("sys.number"));
//        String[] tokenList = new String(Base64.getDecoder().decode(idsToken)).split(File.separator);
//        if (collectNum > tokenList.length || collectNum <= 0) return buildErrorResponse("这个数字超出了范围");
//        OnProductDetail detail = ScheduleService.PRODUCT_ID_DETAIL.get(tokenList[collectNum-1]);
//        if (DplbotServiceUtil.doesUserPlayed(detail.getBotAccount(), getUserId(), getAccessToken())) {
//            String productName = correctName(detail.getName());
//            return directInProduct(productName, detail.getBotAccount(), overDetail);
//        } else {
//            return enterProductInfo(detail.getName(), overDetail);
//        }
//    }
//
//    private Response dealOpenNumInCategory(String userId, OverTimeDetail detail) {
//        String count = getSessionAttribute("comCnt");
//        int pageIndex = 0;
//        if (count != null) pageIndex = Integer.parseInt(count)-1;
//        String numStr = getSlot("sys.number");
//        int num = 1;
//        if (numStr != null) num = Integer.parseInt(numStr);
//        List<String> categoryBots = ScheduleService.getCategoryProductIdList().get(getSessionAttribute("category"));
//        num = pageIndex*RE_RA_CA_PAGE_SIZE+num-1;
//        if (num < 0 || num >= categoryBots.size()) {
//            Response response = buildErrorResponse("这个数字超出了范围,作品的序号已显示在图片左上角");
//            detail.setResponse(response);
//            LAST_OUTTIME_RESULT.put(userId, detail);
//            return response;
//        }
//        String botId = categoryBots.get(num);
//        String name = ScheduleService.getProductIdDetail().get(botId).getName();
//        Response response;
//        if (DplbotServiceUtil.doesUserPlayed(botId, getUserId(), getAccessToken())) response = directInProduct(correctName(name), botId, detail);
//        else response = enterProductInfo(correctName(name), detail);
//        detail.setResponse(response);
//        return response;
//    }
//
//    private Response dealOpenNumInHome(int num, OverTimeDetail detail) {
//        if (num > 0 && num <= 10) {
//            GameProjectTb bot = ScheduleService.getRecommendList().get(num-1);
//            log.info("历史：作品id:{}",bot.getBotAccount());
//            if (!DplbotServiceUtil.doesUserPlayed(bot.getBotAccount(), getUserId(), getAccessToken())) return enterProductInfo(bot.getName(), detail);
//            else return directInProduct(bot.getName(), bot.getBotAccount(), detail);
//        } else if (num > 10 && num <= 20) {
//            GameProjectTb bot = ScheduleService.getRankingList().get(num-11);
//            log.info("历史：作品id:{}",bot.getBotAccount());
//            if (!DplbotServiceUtil.doesUserPlayed(bot.getBotAccount(), getUserId(), getAccessToken())) return enterProductInfo(bot.getName(), detail);
//            else return directInProduct(bot.getName(), bot.getBotAccount(), detail);
//        } else if (num > 20 && num <= 30) {
//            String categoryName = ScheduleService.CATEGORY[num-21];
//            RecommandDocument document = RecommendUtil.getCategory(0, categoryName);
//            if (document == null) return buildErrorResponse("不好意思该分类还没有作品");
//            RenderDocument render = new RenderDocument(document);
//            this.addDirective(render);
//            detail.getDirectives().add(render);
//            this.setSessionAttribute("category",categoryName);
//            detail.getAttributes().put("category",categoryName);
//            this.setSessionAttribute("trace","05");
//            detail.getAttributes().put("trace","05");
//            return buildErrorResponse(new StringBuilder("为您打开").append(categoryName).append("类作品").toString());
//        }
//        else {
//            return buildErrorResponse("这数字超出我的范围了");
//        }
//    }
//
//    private Response directInProduct(String name,String botId,OverTimeDetail detail) {
//        String productName = correctName(name);
//        String enterProductWord = new StringBuilder(Constants.PRE_PRODUCT_PLAY).append(productName).toString();
//        log.info("进入作品语句:{}", enterProductWord);
//        QiWuResponse data = null;
//        try {
//            data = RequestTerminal.requestTerminate(enterProductWord, getUserId(), type);
//        } catch (IOException e) {
//            log.error("请求终端接口出错:", e.toString());
//        }
//        if (data == null) {
//            return buildErrorResponse(SORRY_UNCATCH);
//        }
//        String text = data.getText();
////        String aside = DplbotServiceUtil.getAsideText(text);
////        text = text.substring(text.indexOf("】")+1);
////        PlayDocument document = PlayUtils.getPlayDocument(productName, aside,ScheduleService.getAsideImgUrl(botId, aside),
////                data.getAudio(), text);
//        Pattern pattern = Pattern.compile(Utils.getRegexSeperate());
//        PlayDocument document = PlayUtils.getPlayDocument(productName, DplbotServiceUtil.seperateActor(text, pattern),
//                DplbotServiceUtil.seperateActorWord(text, pattern), data.getAudio(), botId);
//
//        RenderDocument render = new RenderDocument(document);
//        this.setSessionAttribute(PRE_COLLE, null);
//        detail.getAttributes().put(PRE_COLLE, null);
//        String trace = getSessionAttribute(BACK_TRACE);
//        if (trace == null || trace.length() != 2) trace = "00";
//        String backTrace = new StringBuilder(trace).append(botId).append(File.separator).toString();
//        this.setSessionAttribute(BACK_TRACE, backTrace);
//        detail.getAttributes().put(BACK_TRACE,backTrace);
//        this.addDirective(render);
//        detail.getDirectives().add(render);
//        return new Response();
//    }
//
//    private Response enterProductInfo(String productName, OverTimeDetail detail) {
//        OnProductDetail productDetail = ScheduleService.PRODUCT_NAME_DETAIL.get(productName);
//        // product picture
//        String imgUrl = new StringBuilder(Constants.IMG_PREFIX).append(productDetail.getBannerImgUrl()).toString();
//
//        // product create time
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//        String dateString = formatter.format(productDetail.getCreateTime());
//        String createTime = new StringBuilder(Constants.PRODUCT_INTRO.CREATE_TIME).append(dateString).toString();
//
//        // product author
//        String author = new StringBuilder(Constants.PRODUCT_INTRO.AUTHOR).append(productDetail.getAuthorName()).toString();
//
//        // product labels
//        StringBuilder labels = new StringBuilder(Constants.PRODUCT_INTRO.LABEL);
//        for (String label: productDetail.getLabels()) {
//            labels.append(Constants.PRODUCT_INTRO.SPACE).append(label);
//        }
//
//        // product introduction
//        String intro = productDetail.getIntro();
//
//        // "start product" container button
//        String enterProName = new StringBuilder(Constants.PRE_ENTER_PRODUCT).append(productName).toString();
//
//        String accessToken = getAccessToken();
//        String flowers, flowerReward,collectText, collectUrl, collectComId;
//        if (accessToken != null) {
//            // flower container button
//            Long count = DplbotServiceUtil.getUserSendFlowerNum(productDetail.getBotAccount(), getUserId(), accessToken);
//            flowers = USER_SEND_FLOWER+count;
//            flowerReward = new StringBuilder(FLOWE).append(productDetail.getBotAccount()).toString();
//
//            // collection container button
//            boolean collec = DplbotServiceUtil.doesUserCollectThisBot(productDetail.getBotAccount(), accessToken, getUserId());
//            if (collec) {
//                collectText = COLLECED;
//                collectUrl = COLLECTED_IMG_URL;
//                collectComId = new StringBuilder(UN_COLLEC_ID).append(productDetail.getBotAccount()).toString();
//            } else {
//                collectText = UN_COLLEC;
//                collectUrl = UN_COLLECTED_IMG_URL;
//                collectComId = new StringBuilder(COLLEC_ID).append(productDetail.getBotAccount()).toString();
//            }
//        } else {
//            flowers = FLOWER;
//            flowerReward = FLOWER_LOAD;
//
//            collectText = UN_COLLEC;
//            collectUrl = UN_COLLECTED_IMG_URL;
//            collectComId = FLOWER_LOAD; // 提示登录
//        }
//
//        ProductInfoDocument document = ProductInfoUtils.getProductINfoDocument(imgUrl, correctName(productName), createTime, author, labels.toString(),
//                intro, enterProName, flowerReward, flowers, collectComId, collectUrl, collectText);
//
//        RenderDocument renderDocument = new RenderDocument(document);
//        this.addDirective(renderDocument);
//        detail.getDirectives().add(renderDocument);
//        String trace = getSessionAttribute(BACK_TRACE);
//        log.info("before trace------------:{}",trace);
//        StringBuilder builder = new StringBuilder();
//        if (trace == null || trace.length() < 2) {
//            builder.append("00");
//        } else if (trace.length()>2) {
//            builder.append(trace, 0, 2);
//        } else {
//            builder.append(trace);
//        }
//
//        trace = builder.append(productDetail.getBotAccount()).toString();
//        log.info("after trace------------:{}",trace);
//        this.setSessionAttribute(BACK_TRACE, trace);
//        detail.getAttributes().put(BACK_TRACE, trace);
//        return new Response();
//    }
//
//    private RecommandDocument displayFunnyRecommandPage(String word, String audio) {
//        try {
//            List<String> botNames = Utils.getRecommandBotNames(word);
//            log.info("botNames:{}",botNames);
//            List<OnProductDetail> bots = new ArrayList<>();
//            for (String botName : botNames) {
//                if (botName.equals("清新传")) botName = "清新传{/zhuan4/}";
//                OnProductDetail detail = ScheduleService.PRODUCT_NAME_DETAIL.get(botName);
//                if (detail == null) continue;
//                bots.add(detail);
//            }
//            if (bots.size() != 0) return RecommendUtil.getFunnyDocument(bots, audio);
//        } catch (Exception e) {
//            log.error("获取funny页出错:{}",e.toString());
//        }
//        return null;
//    }
//
//    private XiaoDuOrderTB buildBeforePayPojo(String botAccount, String appUserId, String userId, int unitPrice,
//                       long number, String orderId) {
//        XiaoDuOrderTB xiaoDuOrderTB = new XiaoDuOrderTB();
//        buildPayPojoCommon(xiaoDuOrderTB, botAccount, appUserId, userId, unitPrice, number, orderId);
//        xiaoDuOrderTB.setDevice(1);
//        return xiaoDuOrderTB;
//    }
//
//    private XiaoDuOrderTB buildNoScreenBeforePayPojo(String botAccount, String appUserId, String userId, int unitPrice,
//                                             long number, String orderId) {
//        XiaoDuOrderTB xiaoDuOrderTB = new XiaoDuOrderTB();
//        buildPayPojoCommon(xiaoDuOrderTB, botAccount, appUserId, userId, unitPrice, number, orderId);
//        xiaoDuOrderTB.setDevice(2);
//        return xiaoDuOrderTB;
//    }
//
//    private void buildPayPojoCommon(XiaoDuOrderTB xiaoDuOrderTB, String botAccount, String appUserId, String userId, int unitPrice,
//                                    long number, String orderId) {
//        xiaoDuOrderTB.setBotId(botAccount);
//        xiaoDuOrderTB.setAppUserId(appUserId);
//        xiaoDuOrderTB.setXiaoduUserId(userId);
//        xiaoDuOrderTB.setUnitPrice(unitPrice);
//        xiaoDuOrderTB.setNumber(number);
//        xiaoDuOrderTB.setIsBuy(false);
//        xiaoDuOrderTB.setStatus(false);
//        xiaoDuOrderTB.setChOrderId(orderId);
//        xiaoDuOrderTB.setTimeStamp(curTime);
//    }
//
//    @Override
//    protected Response onChargeEvent(ChargeEvent chargeEvent) {
//        String token = chargeEvent.getToken();
//        int sepIndex = token.indexOf(File.separator);
//        final String botAccount = token.substring(0, sepIndex);
//        String str = "恭喜您支付成功,开始体验吧";
//        if (getRequest().getContext().getScreen() != null) {
//            boolean response = DplbotServiceUtil.dealChargeCallback(chargeEvent, customUserId(), getAccessToken(),
//                    getSessionAttribute("userId"), PRODUCT_ID_DETAIL.get(botAccount).getName(), "xiaodu_screen");
//            if (response) {
//                HashMap<String, Long> flowerRecords = DplbotServiceUtil.USER_HISTORY.get(getUserId()).getUserBotId2Flowers();
//                Long before = flowerRecords.get(botAccount);
//                if (before == null) before = 0L;
//                flowerRecords.put(botAccount, before+1);
//                ExecuteCommands executeCommands = new ExecuteCommands();
//                SetStateCommand stateCommand = new SetStateCommand();
//                stateCommand.setComponentId("pft");
//                stateCommand.setState("text");
//                stateCommand.setValue(USER_SEND_FLOWER+before);
//                executeCommands.addCommand(stateCommand);
//                try {
//                    addDirective(executeCommands);
//                } catch (Exception e) {
//                    log.info(ExceptionUtils.getStackTrace(e));
//                }
//            } else {
//                str = "很抱歉,您没有支付成功";
//            }
//        } else {
//            String userId = customUserId();
//            String jytUserId = getSessionAttribute("userId");
//            if (StringUtils.isBlank(jytUserId)) jytUserId = DplbotServiceUtil.getUserIdByToken(getAccessToken());
//            if (StringUtils.isBlank(jytUserId)) {
//                jytUserId = "null";
//            }
//            boolean response = DplbotServiceUtil.dealChargeCallback(chargeEvent, userId, getAccessToken(),
//                    jytUserId, PRODUCT_ID_DETAIL.get(botAccount).getName(), "xiaodu_no_screen");
//            if (!response) {
//                str = "很抱歉,您没有支付成功";
//            }
//        }
//        return buildErrorResponse(str);
//    }
//
//    @Override
//    protected Response onPlaybackFinishedEvent(PlaybackFinishedEvent playbackFinishedEvent) {
////        log.info("音频播放完毕，开麦");
////        setSessionAttribute("botAcc",getSessionAttribute("botAcc"));
//        this.setExpectSpeech(true);
////        log.error("FinishedEvent");
//        return new Response();
//    }
//
//    @Override
//    protected Response onPlaybackStoppedEvent(PlaybackStoppedEvent playbackStoppedEvent) {
////        setSessionAttribute("botAcc",getSessionAttribute("botAcc"));
//        waitAnswer();
//        return new Response();
//    }
//
//    @Override
//    protected Response onPlaybackStartedEvent(PlaybackStartedEvent playbackStartedEvent) {
////        setSessionAttribute("botAcc",getSessionAttribute("botAcc"));
//        USERID_AUDIO_PATH.remove(customUserId());
//        waitAnswer();
//        return new Response();
//    }
//
//    @Override
//    protected Response onPlaybackNearlyFinishedEvent(PlaybackNearlyFinishedEvent playbackNearlyFinishedEvent) {
////        setSessionAttribute("botAcc",getSessionAttribute("botAcc"));
//        waitAnswer();
//        return new Response();
//    }
//
//    private String customUserId() {
//        try {
//            return PREFIX_USERID+ URLEncoder.encode(getUserId(), "UTF-8");
//        } catch (Exception e) {
//            return PREFIX_USERID+ getUserId().replaceAll("/", "");
//        }
////        return PREFIX_USERID + ai.qiwu.com.xiaoduhome.xiaoai.common.Utils.modifyDeviceId(getUserId());
////        return PREFIX_USERID + getUserId().replaceAll(NUM_OR_ALPHA, "");
//    }
}
