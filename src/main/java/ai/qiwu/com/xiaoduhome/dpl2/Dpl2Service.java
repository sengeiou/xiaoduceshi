package ai.qiwu.com.xiaoduhome.dpl2;

import ai.qiwu.com.xiaoduhome.common.BaseHolder;
import ai.qiwu.com.xiaoduhome.common.Constants;
import ai.qiwu.com.xiaoduhome.common.OkHttp3Utils;
import ai.qiwu.com.xiaoduhome.common.Utils;
import ai.qiwu.com.xiaoduhome.dpl2.model.*;
import ai.qiwu.com.xiaoduhome.entity.secondary.XiaoDuOrderTB;
import ai.qiwu.com.xiaoduhome.pojo.ActorWithWord;
import ai.qiwu.com.xiaoduhome.pojo.DialogData;
import ai.qiwu.com.xiaoduhome.pojo.UserBehaviorData;
import ai.qiwu.com.xiaoduhome.pojo.data.ProjectData;
import ai.qiwu.com.xiaoduhome.repository.secondary.XiaoDuOrderTbRepository;
import ai.qiwu.com.xiaoduhome.service.DplbotServiceUtil;
import ai.qiwu.com.xiaoduhome.service.RequestTerminal;
import ai.qiwu.com.xiaoduhome.service.ScheduleServiceNew;
import ai.qiwu.com.xiaoduhome.spirit.SpiritRedisService;
import ai.qiwu.com.xiaoduhome.xiaoai.common.XiaoAiConstants;
import ai.qiwu.com.xiaoduhome.baidu.dueros.bot.BaseBot;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.request.IntentRequest;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.request.LaunchRequest;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.request.Query;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.request.SessionEndedRequest;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.request.audioplayer.event.PlaybackFinishedEvent;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.request.audioplayer.event.PlaybackNearlyFinishedEvent;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.request.audioplayer.event.PlaybackStartedEvent;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.request.audioplayer.event.PlaybackStoppedEvent;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.request.events.LinkAccountSucceededEvent;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.request.events.NextRequired;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.request.pay.event.ChargeEvent;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.OutputSpeech;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.card.Card;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.card.LinkAccountCard;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.directive.SendPart;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.directive.dpl.Document;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.directive.dpl.ExecuteCommands;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.directive.dpl.RenderDocument;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.directive.dpl.commands.ScrollToElementCommand;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.directive.dpl.commands.ScrollToIndexCommand;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.directive.dpl.event.UserEvent;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.directive.pay.Charge;
import ai.qiwu.com.xiaoduhome.baidu.dueros.model.Response;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static ai.qiwu.com.xiaoduhome.common.Constants.*;
import static ai.qiwu.com.xiaoduhome.common.Constants.DPL_COMPONENT_ID.*;
import static ai.qiwu.com.xiaoduhome.common.Constants.ErrorMsg.SORRY_UNCATCH;
import static ai.qiwu.com.xiaoduhome.common.Constants.PRODUCT_INTRO.*;
import static ai.qiwu.com.xiaoduhome.service.DplbotServiceUtil.POOL;

/**
 * painter
 * 20-8-24 下午6:18
 */
@Slf4j
public class Dpl2Service extends BaseBot {

    private static Document FIRST_PAGE;
    private static Document categoryPage;

    private static JsonNode colHisPageData;
    private static JsonNode productListPageData;
    private static JsonNode homePageData;
    private static JsonNode playPageData;
    private static JsonNode recommendPageData;
    private static JsonNode productDetailPageData;

    static {
        try {
            FIRST_PAGE = ScheduleServiceNew.objectMapper.readValue(DplbotServiceUtil.getDPLTemple("static/dpl2/firstPage.json"), Document.class);
            categoryPage = ScheduleServiceNew.objectMapper.readValue(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("static/dpl2/category.json"), Document.class);

            colHisPageData = ScheduleServiceNew.objectMapper.readTree(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("static/dpl2/collectHistory.json"));
            productListPageData = ScheduleServiceNew.objectMapper.readTree(Thread.currentThread()
                    .getContextClassLoader().getResourceAsStream("static/dpl2/productList.json"));
            homePageData = ScheduleServiceNew.objectMapper.readTree(Thread.currentThread()
                    .getContextClassLoader().getResourceAsStream("static/dpl2/home.json"));
            playPageData = ScheduleServiceNew.objectMapper.readTree(Thread.currentThread()
                    .getContextClassLoader().getResourceAsStream("static/dpl2/play.json"));
            recommendPageData = ScheduleServiceNew.objectMapper.readTree(Thread.currentThread()
                    .getContextClassLoader().getResourceAsStream("static/dpl2/recommand.json"));
            productDetailPageData = ScheduleServiceNew.objectMapper.readTree(Thread.currentThread()
                    .getContextClassLoader().getResourceAsStream("static/dpl2/productDetail.json"));

        } catch (Exception e) {
            log.error("加载初始化界面失败");
        }
    }

    private final String channel;
    private final StringRedisTemplate stringRedisTemplate;

    private String customUserId;

    public Dpl2Service(HttpServletRequest request, StringRedisTemplate stringRedisTemplate) throws IOException {
        super(request);
        this.channel = "baidu-screen-jiaoyou-audio-test";
//        this.channel = "jiaoyou-audio-test";
        this.stringRedisTemplate = stringRedisTemplate;
        this.botMonitor.setEnvironmentInfo(PRIVATE_KEY, 1);
    }

    @Override
    protected Response onLaunch(LaunchRequest launchRequest) {
        RenderDocument renderDocument = new RenderDocument(FIRST_PAGE);
        this.addDirective(renderDocument);
        // 初次进入时创建用户的行为信息,包括历史记录,送花数,收藏记录
        // 历史记录,收藏记录,送花数,郊游天下id
        final String accessToken = getAccessToken();
        final String userId = customUserId();
        if (accessToken == null) {
            DplbotServiceUtil.onLunchFillUserBehaviorDataInMaster(userId, channel, null, false);
        } else {
            UserBehaviorData data = DplbotServiceUtil.getRedisTemplate().opsForValue().get(userId);
            String jytUserId = null;
            if (data != null) jytUserId = data.getJytUserId();
            if (jytUserId == null) jytUserId = DplbotServiceUtil.getUserIdByToken(accessToken, channel, userId);
            setSessionAttribute("userId", jytUserId);
            UserBehaviorData behaviorData = DplbotServiceUtil.onLunchFillUserBehaviorDataInMaster(userId, channel, jytUserId, true);
            log.info("onLunch data:"+behaviorData);
        }
        POOL.submit(() -> RequestTerminal.backRequestByChannel(userId, channel));
        setSessionAttribute(BACK_TRACE, null);
        return new Response();
    }

    @Override
    protected Response onInent(IntentRequest intentRequest) {
        String redisId = SpiritRedisService.PREFIX_REDIS+customUserId();
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisId))) {
            String trace = getSessionAttribute(BACK_TRACE);
            if (StringUtils.endsWith(trace, File.separator)) {
                String lastData;
                if ((lastData=stringRedisTemplate.opsForValue().get(redisId))!=null) {
                    String redisIdleId = SpiritRedisService.PREFIX_REDIS_IDLE+customUserId();
                    if ("out".equals(lastData)) {
                        String idleNum = stringRedisTemplate.opsForValue().get(redisIdleId);
                        boolean firstIdle;
                        if ((firstIdle = StringUtils.isBlank(idleNum)) || "1".equals(idleNum)) {
                            try {
                                Thread.sleep(2000);
                            } catch (Exception e){
                                // do nothing
                            }
                            lastData = stringRedisTemplate.opsForValue().get(redisId);
                            if (StringUtils.isNotBlank(lastData) && !"out".equals(lastData)) {
                                stringRedisTemplate.delete(redisIdleId);
                                stringRedisTemplate.delete(redisId);
//                                return dealScreenOutTimeResult(lastData, getUserId(), channel);
                            }
                            if (firstIdle) idleNum = "1";
                            else idleNum = "2";
                            stringRedisTemplate.opsForValue().set(redisIdleId, idleNum, 10, TimeUnit.SECONDS);
                            return buildTextResponse((RandomUtils.nextInt() & 1) == 1 ? XiaoAiConstants.TTS.WAIT_WORD_1 :
                                    XiaoAiConstants.TTS.WAIT_WORD_2);
                        } else {
                            stringRedisTemplate.delete(redisIdleId);
                            stringRedisTemplate.delete(redisId);
                        }
                    } else {
                        stringRedisTemplate.delete(redisIdleId);
                        stringRedisTemplate.delete(redisId);
                        return dealScreenOutTimeResult(lastData);
                    }
                } else {
                    log.info("outtime lastData is null");
                }
            } else {
                stringRedisTemplate.delete(redisId);
            }
        }
        try {
            Future<Response> future = DplbotServiceUtil.getPOOL().submit(() -> dealDefaultIntent(intentRequest, customUserId(), null));
            return future.get(2500, TimeUnit.MILLISECONDS);
        }  catch (InterruptedException | ExecutionException e) {
            log.warn("获取任务执行结果出错:{}", ExceptionUtils.getStackTrace(e));
            return buildTextResponse(SORRY_UNCATCH);
        } catch (TimeoutException e) {
            log.info("超时,让用户再说一遍:{}",e.toString());
            stringRedisTemplate.opsForValue().setIfAbsent(redisId, "out", 10, TimeUnit.SECONDS);
            addDirective(new SendPart("outTime"));
            return buildTextResponse("主人请稍等，晓悟正在加载数据");
        } catch (Exception all) {
            log.warn("任务执行发生了错误:{}", ExceptionUtils.getStackTrace(all));
            return buildTextResponse(SORRY_UNCATCH);
        }
    }

    @Override
    protected Response onUserEvent(UserEvent userEvent) {
        String componentId = userEvent.getPayload().getComponentId();
        log.info("componentId:"+componentId);
        try {
            switch (componentId) {
                case DPL2.TRIGGER_HOME_PAGE_UPDATE: {
                    this.setExpectSpeech(true);
                    return buildTextResponse("获取更多作品请对我说,刷新");
                }
                case DPL2.TRIGGER_HOME_PAGE_LOAD: {
                    openRandomHome();
                    return buildTextResponse(WelcomeWord.WELCOME);
                }
                case DPL2.AUDIO_END : return audioEvent();
                case DPL2.BACK_TRIGGER : {
                    setSessionAttribute(CUR_FIRST_ROW_INDEX, null);
                    return dealBackIntent();
                }
                default: {
                    String componentPrefix = componentId.substring(0, 5);
                    switch (componentPrefix) {
                        case Constants.PRE_PRODUCT_IMG : return dealOpenProductDetail(ScheduleServiceNew.getProjectByName(componentId.substring(5)));
                        case Constants.PRE_ENTER_PRODUCT : return directInProduct(componentId.substring(5));
                        case FLOWE : return dealSendFlowerIntent(customUserId(), "1");
                        case PRE_COLLE : return dealCollectOrUnCollectIntent("colleCo".equals(componentId), null, true);
                        case PRE_CATEGORY : return openCategoryProductList(componentId.substring(5), 1);
                    }
                }
            }
        } catch (Exception e) {
            log.error("onUserEvent error:"+ ExceptionUtils.getStackTrace(e));
        }
        return new Response();
    }

    private Response audioEvent() {
        if (getSessionAttribute("end") != null) {
            try {
                setSessionAttribute("end", null);
                String endData = stringRedisTemplate.opsForValue().get(SpiritRedisService.PREFIX_REDIS+customUserId()+"_end");
                String path = OkHttp3Utils.doPostJsonStr(ScheduleServiceNew.BASE_HOST_URL+"9663/api/audio", endData);
                if (StringUtils.isBlank(path) || "null".equals(path)) {
                    log.error("onNextRequiredEvent 小度没有获取到音频链接");
                    return new Response();
                }
                CentralResponseVO vo = ScheduleServiceNew.objectMapper.readValue(endData, CentralResponseVO.class);
                DialogData datum = vo.getDialogs().stream().filter(dialogData ->
                        dialogData.getBotAccount().equals(ScheduleServiceNew.getTheChannelBotAccount(channel))).findFirst().orElse(null);
                if (datum == null) {
                    log.error("onNextRequiredEvent 没有找到推荐bot的话语:"+endData);
                    return new Response();
                }
                return dealRecommend(null, datum.getText(), path);
            } catch (Exception e) {
                log.warn(ExceptionUtils.getStackTrace(e));
            }
        } else {
            setExpectSpeech(true);
        }
        return new Response();
    }

    @Override
    protected Response onSessionEnded(SessionEndedRequest sessionEndedRequest) {
        RequestTerminal.backRequestByChannel(customUserId(), channel);
        return super.onSessionEnded(sessionEndedRequest);
    }

    @Override
    protected Response onPlaybackStartedEvent(PlaybackStartedEvent playbackStartedEvent) {
        return super.onPlaybackStartedEvent(playbackStartedEvent);
    }

    @Override
    protected Response onPlaybackStoppedEvent(PlaybackStoppedEvent playbackStoppedEvent) {
        return super.onPlaybackStoppedEvent(playbackStoppedEvent);
    }

    @Override
    protected Response onPlaybackNearlyFinishedEvent(PlaybackNearlyFinishedEvent playbackNearlyFinishedEvent) {
        return super.onPlaybackNearlyFinishedEvent(playbackNearlyFinishedEvent);
    }

    @Override
    protected Response onPlaybackFinishedEvent(PlaybackFinishedEvent playbackFinishedEvent) {
        return super.onPlaybackFinishedEvent(playbackFinishedEvent);
    }

    @Override
    protected Response onLinkAccountSucceededEvent(LinkAccountSucceededEvent linkAccountSucceededEvent) {
//        log.info("登录成功返回信息:{}",linkAccountSucceededEvent);
        String accessToken = getAccessToken();
        String jytUserId;
        if (!isNoScreenRequest()) {
            log.info("检测到有屏登录");
            String userId = customUserId();
            jytUserId = DplbotServiceUtil.getUserIdByToken(accessToken, channel, userId);
            DplbotServiceUtil.onLunchFillUserBehaviorData(userId, channel, jytUserId, true);
//            DplbotServiceUtil.noticeOtherServer(5, null, null, false, userId, jytUserId, channel, false);
            if (jytUserId == null) {
                log.warn("登录后获取用户的app id 失败");
            }
        } else {
            jytUserId = DplbotServiceUtil.getUserIdByToken(accessToken, null, getUserId());
            if (jytUserId == null)
                log.warn("登录后获取用户的app id 失败");
        }
        setSessionAttribute("userId", jytUserId);
        return buildTextResponse("您已登录成功");
    }

    @Override
    protected Response onChargeEvent(ChargeEvent chargeEvent) {
        String token = chargeEvent.getToken();
        String[] tokens = token.split(File.separator);
//        log.info("onChargeEvent token:"+token);
        final String botAccount = tokens[0];
        String str = "恭喜您支付成功,开始体验吧";
        if (!isNoScreenRequest()) {
            String jytUserId = getSessionAttribute("userId");
            Boolean response = DplbotServiceUtil.dealChargeCallback(chargeEvent, customUserId(), getAccessToken(),
                    jytUserId, ScheduleServiceNew.getWorkNameByBotAccount(botAccount), channel);
//            log.info("onChargeEvent response:"+response);
            if (Boolean.TRUE.equals(response)) {
                long flowerNums = DplbotServiceUtil.updateUserFlower(customUserId(), Integer.parseInt(tokens[1]), channel,
                        jytUserId, getAccessToken(), botAccount);
                try {
                    if ("0".equals(tokens[2])) {
//                        log.info("onChargeEvent in project token:{}",token);
                        dealOpenProductDetail(ScheduleServiceNew.getProjectByBotAccount(botAccount), flowerNums, jytUserId, customUserId());
                        str = "恭喜您支付成功";
                    }
                } catch (Exception e) {
                    log.warn(ExceptionUtils.getStackTrace(e));
                }
            } else {
                str = "很抱歉,您没有支付成功";
            }
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
        return buildTextResponse(str);
    }

    @Override
    protected Response onNextRequiredEvent(NextRequired nextRequired) {
        try {
            switch (nextRequired.getToken()) {
                case "outTime": {
                    String lastData;
                    String redisId = SpiritRedisService.PREFIX_REDIS+customUserId();
                    if ((lastData=stringRedisTemplate.opsForValue().get(redisId))!=null) {
                        if ("out".equals(lastData)) {
                            stringRedisTemplate.delete(redisId);
                            setExpectSpeech(true);
                            return buildTextResponse("主人晓悟加载数据失败了，请您再重复下刚才的操作吧");
                        } else {
                            stringRedisTemplate.delete(redisId);
                            return dealScreenOutTimeResult(lastData);
                        }
                    } else {
                        log.info("send part out time lastData is null");
                        setExpectSpeech(true);
                        return new Response();
                    }
                }
                case "end": {
                    String endData = stringRedisTemplate.opsForValue().get(SpiritRedisService.PREFIX_REDIS+customUserId()+"_end");
                    String path = OkHttp3Utils.doPostJsonStr(ScheduleServiceNew.BASE_HOST_URL+"9663/api/audio", endData);
                    if (StringUtils.isBlank(path) || "null".equals(path)) {
                        log.error("onNextRequiredEvent 小度没有获取到音频链接");
                        return new Response();
                    }
                    CentralResponseVO vo = ScheduleServiceNew.objectMapper.readValue(endData, CentralResponseVO.class);
                    DialogData datum = vo.getDialogs().stream().filter(dialogData ->
                            dialogData.getBotAccount().equals(ScheduleServiceNew.getTheChannelBotAccount(channel))).findFirst().orElse(null);
                    if (datum == null) {
                        log.error("onNextRequiredEvent 没有找到推荐bot的话语:"+endData);
                        return new Response();
                    }
                    return dealRecommend(null, datum.getText(), path);
                }
                case "direct" :{
                    String data = stringRedisTemplate.opsForValue().get(SpiritRedisService.PREFIX_REDIS_DIRECT+customUserId());
                    String path;
                    CentralResponseVO centralResponse;
                    try {
                        path = OkHttp3Utils.doPostJsonStr(ScheduleServiceNew.BASE_AUDIO_URL+"/api/audio", data);
                        centralResponse = ScheduleServiceNew.objectMapper.readValue(data, CentralResponseVO.class);
                    } catch (Exception e) {
                        log.warn("onNextRequiredEvent 没有获取到TTS返回地址");
                        return buildTextResponse("主人晓悟加载数据失败了，您可以通过点击来进入游戏");
                    }
                    if (StringUtils.isBlank(path) || "null".equals(path)) {
                        log.error("onNextRequiredEvent 小度没有获取到音频链接");
                        return buildTextResponse("主人晓悟加载数据失败了，您可以通过点击来进入游戏");
                    }
                    String botAccount = centralResponse.getAipioneerUsername();
                    return openPlayPage(centralResponse.getPlotImgUrl(),ScheduleServiceNew.getWorkNameByBotAccount(botAccount),
                            dealAudioUrl(path, botAccount), DplbotServiceUtil.getTheActorsWithWords(centralResponse.getDialogs(), botAccount), botAccount);
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

    private Response dealScreenOutTimeResult(String data) {
        try {
            String[] arr = data.split("⧩");
            JsonNode object = ScheduleServiceNew.objectMapper.readValue(arr[0], JsonNode.class);
            String path = arr[1];
            if (Boolean.parseBoolean(arr[2])) {
                setSessionAttribute(DPL2.PRODUCT_END_MARK, "1");
            }
            String botAccount = object.get("aipioneerUsername").asText();
            ArrayNode arrayNode = (ArrayNode) object.get("dialogs");
            if (botAccount.equals(ScheduleServiceNew.getTheChannelBotAccount(channel))){
                return dealRecommend(data, arrayNode.get(0).get("text").asText(), path);
            }
            setExpectSpeech(true);
            return openPlayPage(object.get("plotImgUrl").asText(),ScheduleServiceNew.getWorkNameByBotAccount(botAccount), dealAudioUrl(path, botAccount),
                    DplbotServiceUtil.getTheActorsWithWords(arrayNode, botAccount), botAccount);
        }catch (Exception e) {
            log.error("dealScreenOutTimeResult error:"+e);
        }
        setExpectSpeech(true);
        return buildTextResponse("主人晓悟加载数据失败了，请再说一遍吧");
    }

    private Response dealDefaultIntent(IntentRequest intentRequest, String userId, String word) {
        if (word == null) {
            Query query = intentRequest.getQuery();
            if (query != null) word = query.getOriginal();
            if (word == null){
                log.info("小度没有传来任何话语");
                return buildTextResponse(SORRY_UNCATCH);
            }
        }
        String data = null;
        CentralResponseVO centralResponse;
        try {
            data = RequestTerminal.requestTerminateTemp(word, userId, channel);
            centralResponse = ScheduleServiceNew.objectMapper.readValue(data, CentralResponseVO.class);
        } catch (Exception e) {
            log.error("请求终端接口出错:{}, data:{}", e.toString(), data);
            return buildTextResponse(SORRY_UNCATCH);
        }
        log.info("小悟返回:"+data);
        if (StringUtils.isBlank(data)) {
            return buildTextResponse(SORRY_UNCATCH);
        }
        try {
            List<DialogData> commands = centralResponse.getCommands();
            if (commands != null && commands.size() == 1) {
                String txt = commands.get(0).getText();
                int i;
                if (StringUtils.isNotBlank(txt) && (i = txt.indexOf("☛")) != -1) {
                    int end = txt.indexOf("☚");
                    String command = txt.substring(i+1, end).trim();
                    if (StringUtils.isNotBlank(command)) {
                        if (command.contains("out")) {
                            log.info("小度，推荐bot退出：{},{}",word,userId);
                            return buildTextResponse("本技能暂不支持该功能，若您想要退技能，请说小度小度，退出");
                        }
                        String curFirstRowIndex = getSessionAttribute(CUR_FIRST_ROW_INDEX);
                        if (curFirstRowIndex != null) setSessionAttribute(CUR_FIRST_ROW_INDEX, null);
                        switch (command) {
                            case "refresh"  : return homePageRefresh();
                            case "recommend": return dealRecommend(data, centralResponse.getDialogs().get(0).getText(), null);
                            case "history"  : return dealHistoryIntent(1);
                            case "collect"  : return dealOpenCollectIntent(1);
                            case "next"     : return dealNextPageIntent();
                            case "before"   : return dealLastPageIntent();
                            case "back"     : return dealBackIntent();
                            case "load"     : return load();
                            case "start"    : return dealStartIntent(intentRequest);
                            case "openRank" : return dealRankIntent(1);
                            case "openNum"  : return dealOpenNumIntent(end+1 == txt.length() ? "1" : txt.substring(end+1));
                            case "jump"     : return dealJumpIntent(end+1 == txt.length() ? "1" : txt.substring(end+1));
                            case "store"    : return dealCollectOrUnCollectIntent( true, end+1 == txt.length() ? null : txt.substring(end+1), false);
                            case "unStore"  : return dealCollectOrUnCollectIntent( false, end+1 == txt.length() ? null : txt.substring(end+1), false);
                            case "flower"   : return dealSendFlowerIntent(userId, end+1 == txt.length() ? "1" : txt.substring(end+1));
                            case "category" : return openCategory();
                            case "up"       : return dealUpOrDownCommand(true, curFirstRowIndex);
                            case "down"     : return dealUpOrDownCommand(false, curFirstRowIndex);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("解析出错:"+e);
            return new Response();
        }
        boolean endMark = false;
        if ("exit".equalsIgnoreCase(centralResponse.getBotStatus())) {
            String trace = getSessionAttribute(BACK_TRACE);
            if (StringUtils.endsWith(trace, File.separator)) {
                String botAccount = trace.substring(2, trace.length()-1);
                if (!CollectionUtils.isEmpty(centralResponse.getDialogs())) {
                    final List<DialogData> recommendWord = new ArrayList<>(2);
                    List<DialogData> botDatas = centralResponse.getDialogs().stream()
                            .filter(dialogData -> {
                                if (botAccount.equals(dialogData.getBotAccount())) return true;
                                recommendWord.add(dialogData);
                                return false;
                            })
                            .collect(Collectors.toList());
                    if (botDatas.size() == 0) {
                        return dealBackIntent();
                    } else {
                        log.info("end recommend:"+recommendWord);
                        try {
                            CentralResponseVO vo = new CentralResponseVO();
                            vo.setDialogs(recommendWord);
                            vo.setAipioneerUsername(centralResponse.getAipioneerUsername());
                            stringRedisTemplate.opsForValue().set(SpiritRedisService.PREFIX_REDIS+userId+"_end",
                                    ScheduleServiceNew.objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(vo),
                                    3, TimeUnit.MINUTES);
                            endMark = true;
                        } catch (Exception e) {
                            log.error("end recommend json error:"+e);
                        }
                        centralResponse.setDialogs(botDatas);
                        centralResponse.setAipioneerUsername(botDatas.get(0).getBotAccount());
                    }

                } else {
                    return dealBackIntent();
                }
            } else {
                return dealBackIntent();
            }
        }

        String botAccount = centralResponse.getAipioneerUsername();
        if (StringUtils.isBlank(botAccount)) {
            log.warn("central response data botAccount is null:"+data);
            setExpectSpeech(true);
            return buildTextResponse("主人晓悟加载数据失败了，请再说一遍吧");
        }
        String path;
        try {
            if (endMark) {
                path = OkHttp3Utils.doPostJsonStr(ScheduleServiceNew.BASE_AUDIO_URL+"/api/audio",
                        ScheduleServiceNew.objectMapper.writeValueAsString(centralResponse));
            } else {
                path = OkHttp3Utils.doPostJsonStr(ScheduleServiceNew.BASE_AUDIO_URL+"/api/audio", data);
            }
//            log.info("================path:{}, BASE_AUDIO_URL:{}",path, ScheduleServiceNew.BASE_AUDIO_URL);
        } catch (Exception e) {
            log.warn("没有获取到TTS返回地址");
            return buildTextResponse(ErrorMsg.SORRY_UNCATCH);
        }
        if (StringUtils.isBlank(path) || "null".equals(path)) {
            log.error("小度没有获取到音频链接");
            return buildTextResponse(ErrorMsg.SORRY_UNCATCH);
        }
        if (!endMark && botAccount.equals(ScheduleServiceNew.getTheChannelBotAccount(channel))){
            return dealRecommend(data, centralResponse.getDialogs().get(0).getText(), dealAudioUrl(path, botAccount));
        }

        String beforeBotAccount = getSessionAttribute("botAcc");
        if (!botAccount.equals(beforeBotAccount)) {
            DplbotServiceUtil.updateTheUserHistory(userId, botAccount, getAccessToken(), channel, getSessionAttribute("userId"));
        }
        if (Boolean.TRUE.equals(stringRedisTemplate.opsForValue().setIfPresent(SpiritRedisService.PREFIX_REDIS+userId,
                new StringBuilder(data).append("⧩").append(path).append("⧩").append(endMark).toString(), 20, TimeUnit.SECONDS))) {
            return new Response();
        }
        if (endMark) {
            setSessionAttribute(DPL2.PRODUCT_END_MARK, "1");
        }
        return openPlayPage(centralResponse.getPlotImgUrl(),ScheduleServiceNew.getWorkNameByBotAccount(botAccount), dealAudioUrl(path, botAccount),
                DplbotServiceUtil.getTheActorsWithWords(centralResponse.getDialogs(), botAccount), botAccount);
    }

    // =================== up/down page =================

    private Response dealUpOrDownCommand(boolean up, String curFirstRowIndex) {
        int i;
        if (StringUtils.isBlank(curFirstRowIndex)) {
            i = 1;
        } else {
            i = Integer.parseInt(curFirstRowIndex);
        }
        String trace = getSessionAttribute(BACK_TRACE);
        if (trace == null || trace.length() < 2) {
            return dealPageUpDown(up, i, 4);
        } else if (StringUtils.endsWith(trace, File.separator)) {
            return dealPageUpDown(up, i, 5);
        } else if (trace.length() > 2){
            return buildTextResponse("主人，晓悟认为您现在所在的页面不支持滑动，晓悟错了吗?");
        }
        char type = trace.charAt(1);
        switch (type) {
            case '1' :
            case '2' : return dealPageUpDown(up, i, CO_HI_PAGE_SIZE);
            case '4' :
            case '5' : return dealPageUpDown(up, i, 3);
            case '7' : return dealPageUpDown(up, i, 2);
            default  : return buildTextResponse("主人，晓悟认为您现在所在的页面不支持滑动，晓悟错了吗?");
        }
    }

    private Response dealPageUpDown(boolean up, int curFirstRowIndex, int bound) {
        int nextIndex;
        if (up) {
            nextIndex = curFirstRowIndex+1;
            if (nextIndex > bound) nextIndex = bound;
        } else {
            nextIndex = curFirstRowIndex - 1;
            if (nextIndex < 1) nextIndex = 1;
        }
        ExecuteCommands commands = new ExecuteCommands();
        ScrollToElementCommand elementCommand = new ScrollToElementCommand();
        elementCommand.setAlign(ScrollToIndexCommand.AlignType.FIRST);
        elementCommand.setComponentId(SCROLL_ID);
        elementCommand.setTargetComponentId(String.valueOf(nextIndex));
        commands.addCommand(elementCommand);
        addDirective(commands);
        setSessionAttribute(CUR_FIRST_ROW_INDEX, String.valueOf(nextIndex));
        return new Response();
    }

    // =================== home page refresh ================
    private Response homePageRefresh() {
        String trace = getSessionAttribute(BACK_TRACE);
        if (trace == null || trace.length() < 2) {
            return openRandomHome();
        }
        return buildTextResponse("主人,该页面不支持刷新");
    }

    // ==================== flower ==================

    private Response dealSendFlowerIntent(String userId, String numStr) {
        if (getAccessToken() == null) {
            setExpectSpeech(true);
            return buildTextResponse("主人,该功能需要登录,请说登录");
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
                return buildTextResponse("不好意思主人,晓悟出了点问题,请再说一遍");
            }
            setSessionAttribute("userId", appUserId);
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
                return buildTextResponse("不好意思主人,晓悟出了点问题,请再说一遍");
            }
            XiaoDuOrderTbRepository repository = BaseHolder.getBean("xiaoDuOrderTbRepository");
            XiaoDuOrderTB response = repository.save(orderTB);
            if (response.getId() != null) {
                String desc = new StringBuilder("感谢您赏赐给").append(orderTB.getWorkName())
                        .append("的").append(num).append("朵鲜花").toString();
                Charge charge = new Charge(amount, orderId, "鲜花", desc);
                charge.setToken(botId + File.separator + num + File.separator + (inPlay ? 1 : 0));
                addDirective(charge);
                return buildTextResponse(desc+",请扫描屏幕上的二维码完成支付");
            } else {
                log.warn("订单没有插入到xioadu_order_tb");
                return buildTextResponse("不好意思主人,晓悟出了点问题,请再说一遍");
            }
        }else {
            log.warn("页面不支持送花意图,trace:"+trace);
            return buildTextResponse("不好意思主人，晓悟无法确定您的位置,不知到您要收藏什么作品");
        }
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

    // ====================================collect unCollect ======================

    private Response dealCollectOrUnCollectIntent(boolean collect, String workName, boolean userEvent) {
        if (getAccessToken() == null) {
            setExpectSpeech(true);
            return buildTextResponse("主人,该功能需要登录,请说登录");
        }
        boolean inProductInfo = false;
//        boolean inPlayInfo = false;
        String botAccount;
        String trace = getSessionAttribute(BACK_TRACE);
        if (trace == null || trace.length() <= 2) {
            if (workName != null) {
                workName = workName.replaceAll("[ \uD83C\uDD70\uD83C\uDD71]", "");
                if (StringUtils.isBlank(workName)) {
                    setExpectSpeech(true);
                    return buildTextResponse("主人，您需要明确说出您要操作的作品名称");
                }
                botAccount = ScheduleServiceNew.getBotAccountByWorkname(workName);
                if (StringUtils.isBlank(botAccount)) {
                    return buildTextResponse("不好意思主人，晓悟没有找到该作品");
                }
            } else {
                setExpectSpeech(true);
                return buildTextResponse("主人，您需要明确说出您要操作的作品名称");
            }
        } else {
            if (StringUtils.endsWith(trace, File.separator)) {
                botAccount = trace.substring(2, trace.length()-1);
//                inPlayInfo = true;
            }
            else {
                inProductInfo = true;
                botAccount = trace.substring(2);
            }
        }
        try {
            String appUserId = getSessionAttribute("userId");
            if (appUserId == null) appUserId = DplbotServiceUtil.getUsUserId(customUserId(),getAccessToken(), channel);
            boolean alreadyCollected = DplbotServiceUtil.doesUserCollectThisBot(botAccount, getAccessToken(), customUserId(),
                    channel, appUserId);
            if (appUserId != null) {
                if (alreadyCollected) {
                    if (collect) {
                        return buildTextResponse("主人，您已经收藏过该作品了");
                    } else {
                        boolean result = DplbotServiceUtil.userUnCollectTheBot(customUserId(), botAccount,
                                channel, appUserId, getAccessToken());
                        if (!result) {
                            return buildTextResponse("不好意思主人，晓悟出了点问题没有取消成功");
                        }
                        ProjectData projectData = ScheduleServiceNew.getProjectByBotAccount(botAccount);
                        if (!userEvent && inProductInfo) {
                            dealOpenProductDetail(projectData, false);
                        }
                        return buildTextResponse(String.format(UN_COLLECT_BOT_WORD_MG,projectData.getName()));
                    }
                } else {
                    if (collect) {
                        boolean result = DplbotServiceUtil.userCollectTheBot(customUserId(), botAccount, channel,
                                appUserId, getAccessToken());
                        if (!result) {
                            return buildTextResponse("不好意思主人，晓悟出了点问题没有收藏成功");
                        }
                        ProjectData projectData = ScheduleServiceNew.getProjectByBotAccount(botAccount);
                        if (!userEvent && inProductInfo) {
                            dealOpenProductDetail(projectData, true);
                        }
                        return buildTextResponse(String.format(COLLECT_BOT_WORD_MG,projectData.getName()));
                    }else {
                        return buildTextResponse("主人，您还未收藏过该作品");
                    }
                }
            }else {
                return buildTextResponse("不好意思主人,晓悟走神了，没能理解您的意思");
            }
        } catch (Exception e) {
            log.error("收藏/取消收藏出错:{}",e.toString());
            return buildTextResponse("不好意思主人,晓悟走神了，没能理解您的意思");
        }
    }

    // ========================= jump =======================

    private Response dealJumpIntent(String numStr) {
        String trace = getSessionAttribute("trace");
        if (trace == null || trace.length() < 2) {
            return buildTextResponse("主人，当前的页面不能跳页");
        }
        int index = DplbotServiceUtil.extractNum(numStr);
        if (index <= 0) index = 1;
        String nextPageIndex = getSessionAttribute(NEXT_PAGE_INDEX);
        if (StringUtils.isNotBlank(nextPageIndex) && String.valueOf(index+1).equals(nextPageIndex)) {
            return buildTextResponse("主人，晓悟认为您现在就在第"+index+"页");
        }
        char pageSign = trace.charAt(1);
        Response response;
        switch (pageSign) {
            case '1' : response = dealOpenCollectIntent(index);break;
            case '2' : response = dealHistoryIntent(index);break;
            case '4' : response = dealRankIntent(index);break;
            case '5' : response = openCategoryProductList(getSessionAttribute(CATEGORY_NAME), index);break;
            default  : response = buildTextResponse("主人，当前的页面不能跳页");
        }
        return response;
    }

    // =========================next/pre page=======================

    private Response dealNextPageIntent() {
        String signPage = getSessionAttribute("trace");
        if (signPage == null || signPage.length() < 2) {
            return buildTextResponse("主人,小悟滑不动了,需要休息会");
        }
        char sign = signPage.charAt(1);
        String nextPageIndex = getSessionAttribute(NEXT_PAGE_INDEX);
        if (nextPageIndex == null) {
            nextPageIndex = "2";
        }
        switch (sign) {
            // collect
            case '1': {
                dealOpenCollectIntent(Integer.parseInt(nextPageIndex));
                break;
            }
            // history
            case '2': {
                dealHistoryIntent(Integer.parseInt(nextPageIndex));
                break;
            }
            // rank
            case '4': {
                dealRankIntent(Integer.parseInt(nextPageIndex));
                break;
            }
            // category product list page
            case '5':{
                String category = getSessionAttribute(CATEGORY_NAME);
                if (category == null) category = "古风";
                openCategoryProductList(category, Integer.parseInt(nextPageIndex));
                break;
            }
            default:{
                return buildTextResponse("主人,当前页面没有下一页");
            }
        }
        return new Response();
    }

    private Response dealLastPageIntent() {
        String signPage = getSessionAttribute("trace");
        if (signPage == null || signPage.length() < 2) {
            return buildTextResponse("主人,小悟滑不动了,需要休息会");
        }
        char sign = signPage.charAt(1);
        String nextPageIndex = getSessionAttribute(NEXT_PAGE_INDEX);
        if (nextPageIndex == null || "2".equals(nextPageIndex)) {
            return buildTextResponse("主人,小悟没法再往前翻了");
        }
        switch (sign) {
            // collect
            case '1': {
                dealOpenCollectIntent(Integer.parseInt(nextPageIndex)-2);
                break;
            }
            // history
            case '2': {
                dealHistoryIntent(Integer.parseInt(nextPageIndex)-2);
                break;
            }
            // rank
            case '4': {
                dealRankIntent(Integer.parseInt(nextPageIndex)-2);
                break;
            }
            // category product list page
            case '5':{
                String category = getSessionAttribute(CATEGORY_NAME);
                if (category == null) category = "古风";
                openCategoryProductList(category, Integer.parseInt(nextPageIndex)-2);
                break;
            }
            default:{
                return buildTextResponse("主人,当前页面没有上一页");
            }
        }
        return new Response();
    }

    // ============ back intent===================================

    private Response dealBackIntent() {
        String trace = getSessionAttribute(BACK_TRACE);
        char second;
        String query;
        if (trace == null || ((query=getQuery()) != null && query.contains("首"))) {
            // home page
            openRandomHome();
            if (StringUtils.endsWith(trace,File.separator)) {
                RequestTerminal.backRequestByChannel(customUserId(), channel);
            }
            return buildTextResponse("主人,已为您返回到首页");
        }
        else if (trace.length() <= 2) {
            if (trace.charAt(1) == '5') {
                return openCategory();
            } else {
                openRandomHome();
                return buildTextResponse("主人,已为您返回到首页");
            }
        }
        else if (StringUtils.endsWith(trace,File.separator)){
            String botAccount = trace.substring(2, trace.length()-1);
            dealOpenProductDetail(ScheduleServiceNew.getProjectByBotAccount(botAccount));
            RequestTerminal.backRequestByChannel(customUserId(), channel);
            return buildTextResponse("主人,已为您返回到作品介绍页");
        } else {
            second = trace.charAt(1);
            switch (second){
                // home page
                case '0':{
                    openRandomHome();
                    return buildTextResponse("主人,已为您返回到首页");
                }
                case '1': {
                    //collect
                    String nextPageIndex = getSessionAttribute(NEXT_PAGE_INDEX);
                    if (nextPageIndex == null) {
                        nextPageIndex = "2";
                    }
                    dealOpenCollectIntent(Integer.parseInt(nextPageIndex)-1);
                    return buildTextResponse("主人,已为您返回到收藏页");
                }
                case '2': {
                    //history
                    String nextPageIndex = getSessionAttribute(NEXT_PAGE_INDEX);
                    if (nextPageIndex == null) {
                        nextPageIndex = "2";
                    }
                    dealHistoryIntent(Integer.parseInt(nextPageIndex)-1);
                    return buildTextResponse("主人,已为您返回到历史记录页");
                }
                case '4': {
                    //rank
                    String nextPageIndex = getSessionAttribute(NEXT_PAGE_INDEX);
                    if (nextPageIndex == null) {
                        nextPageIndex = "2";
                    }
                    dealRankIntent(Integer.parseInt(nextPageIndex)-1);
                    return buildTextResponse("主人,已为您返回到排行榜");
                }
                case '5':{
                    String category = getSessionAttribute(CATEGORY_NAME);
                    if (category == null) {
                        openRandomHome();
                        return buildTextResponse("主人,已为您返回到首页");
                    } else {
                        String nextPageIndex = getSessionAttribute(NEXT_PAGE_INDEX);
                        if (nextPageIndex == null) {
                            nextPageIndex = "2";
                        }
                        openCategoryProductList(category, Integer.parseInt(nextPageIndex)-1);

                        return buildTextResponse("已为您返回到" + category + "类作品列表页");
                    }
                }
            }
        }
        return new Response();
    }

    // ======================== open num intent =================

    private Response dealOpenNumIntent(String numStr) {
        String trace = getSessionAttribute("trace");
        int num = DplbotServiceUtil.extractNum(numStr);
        if (trace == null || trace.charAt(1) == '0') {
            return dealOpenNumInHome(num);
        } else if (trace.length() > 2) {
            return buildTextResponse("主人，小悟认为该页面不支持打开功能,您可以直接点击屏幕");
        } else if (num <= 0) {
            num = 1;
        }
        char page = trace.charAt(1);
        Response response;
        switch (page){
            case '1': response = dealOpenNumCoHi(num, true); break;
            case '2': response = dealOpenNumCoHi(num, false); break;
            case '4': response = dealOpenNumRank(num); break;
            case '6': response = dealOpenNumRecommend(num); break;
            case '5': response = dealOpenCategoryProduct(num);break;
            case '7': response = dealOpenCategory(num);break;
            default:{
                response = buildTextResponse("主人，对不起小悟没能理解,您可以直接点击屏幕上的按钮");
            }
        }
        return response;
    }

    private Response dealOpenNumInHome(int num) {
        if (num > 12) {
            return buildTextResponse("主人，小悟没有找到该作品");
        }
        String actualIndex = getSessionAttribute(START_ACTUAL_INDEX);
        if (StringUtils.isBlank(actualIndex)) {
            actualIndex = "0";
        }
        List<ProjectData> dataList = ScheduleServiceNew.getTheChannelAllProjectData(channel);
        ProjectData data = dataList.get((Integer.parseInt(actualIndex)+num-1)%dataList.size());
        if (!DplbotServiceUtil.doesUserPlayed(data.getBotAccount(), getUserId(),
                getAccessToken(), getAccessToken() != null)) {
            return dealOpenProductDetail(data);
        } else {
            return directInProduct(data.getName());
        }
    }

    private Response directInProduct(final String productName) {
        addDirective(new SendPart("direct"));
        POOL.submit(() -> {
            String enterProductWord = Constants.PRE_PRODUCT_PLAY + productName;
            String data = null;
            try {
                data = RequestTerminal.requestTerminateTemp(enterProductWord, getUserId(), channel);
            } catch (IOException e) {
                log.error("请求终端接口出错:", e.toString());
            }
            if (data == null) {
                return;
            }
            stringRedisTemplate.opsForValue().set(SpiritRedisService.PREFIX_REDIS_DIRECT+customUserId(),
                    data, 15, TimeUnit.SECONDS);
        });
        return buildTextResponse("主人，正在为您打开"+productName);
    }

    private Response dealOpenNumCoHi(int num, boolean collect) {
        UserBehaviorData data = DplbotServiceUtil.getRedisTemplate().opsForValue().get(customUserId());
        if (data == null) {
            data = DplbotServiceUtil.onLunchFillUserBehaviorDataInMaster(customUserId(), channel, getSessionAttribute("userId"), getAccessToken() != null);
            if (data == null) return new Response();
        }
        Set<Map.Entry<String, Long>> entry;
        if (collect) {
            entry = data.getUserBotId2Collected().entrySet();
        } else {
            entry = data.getUserBotId2History().entrySet();
        }
        if (num > entry.size()) {
            return buildTextResponse("主人，小悟没有找到您的第"+num+"个记录");
        }
        TreeMap<Long, String> order = new TreeMap<>((o1, o2) -> (o1 < o2) ? 1 : ((o1.equals(o2)) ? 0 : -1));
        for (Map.Entry<String, Long> item: entry) {
            order.put(item.getValue(), item.getKey());
        }
        String productName = ScheduleServiceNew.getProjectByBotAccount(order.values()
                .toArray(new String[]{})[num-1]).getName();
        return directInProduct(productName);
    }

    private Response dealOpenNumRank(int num) {
        List<ProjectData> botData = ScheduleServiceNew.getTheChannelProjectsByWatch(channel);
        if (num > botData.size()) {
            return buildTextResponse("主人，小悟没有找到排名"+num+"的作品");
        }
        ProjectData data = botData.get(num-1);
        if (!DplbotServiceUtil.doesUserPlayed(data.getBotAccount(), getUserId(),
                getAccessToken(), getAccessToken() != null)) {
            return dealOpenProductDetail(data);
        } else {
            return directInProduct(data.getName());
        }
    }

    private Response dealOpenNumRecommend(int num) {
        String nameList = getSessionAttribute(PRE_COLLE);
        if (StringUtils.isBlank(nameList)) {
            return buildTextResponse("主人，小悟不知道您要打开哪个作品，只能麻烦你亲自动手了");
        }
        String[] names = nameList.split(",");
        if (num > names.length) {
            return buildTextResponse("主人，这个数字超出了范围");
        }
        ProjectData data = ScheduleServiceNew.getProjectByName(names[num-1]);
        if (!DplbotServiceUtil.doesUserPlayed(data.getBotAccount(), getUserId(),
                getAccessToken(), getAccessToken() != null)) {
            return dealOpenProductDetail(data);
        } else {
            return directInProduct(data.getName());
        }
    }

    private Response dealOpenCategory(int num) {
        if (num > ScheduleServiceNew.CATEGORY.size()) {
            return buildTextResponse("主人，这个数字超出了范围");
        }
        return openCategoryProductList(ScheduleServiceNew.CATEGORY.get(num-1), 1);
    }

    private Response dealOpenCategoryProduct(int num) {
        String categoryName = getSessionAttribute(CATEGORY_NAME);
        if (StringUtils.isBlank(categoryName)) {
            return buildTextResponse("主人，晓悟不知道您现在的位置，只能麻烦你亲自动手了");
        }
        List<ProjectData> dataList = ScheduleServiceNew.category2Bots.get(categoryName);
        if (num > dataList.size()) {
            return buildTextResponse("主人，这个数字超出了范围");
        }
        ProjectData data = dataList.get(num-1);
        if (!DplbotServiceUtil.doesUserPlayed(data.getBotAccount(), getUserId(),
                getAccessToken(), getAccessToken() != null)) {
            return dealOpenProductDetail(data);
        } else {
            return directInProduct(data.getName());
        }
    }

    ///===============================页面==============================

//    private Response  dealOpenHome() {
//        try {
//            JsonNode jsonNode = homePageData.deepCopy();
//            ObjectNode dataList = (ObjectNode) jsonNode.get("dataSource");
//            List<ProjectData> botData = ScheduleServiceNew.getTheChannelAllProjectData(channel);
//
//            ArrayNode arrayNode = ScheduleServiceNew.objectMapper.createArrayNode();
//            arrayNode.addAll(rowColumn4(botData, 0, 4).parallelStream().map(homePageData ->
//                    ScheduleServiceNew.objectMapper.convertValue(homePageData, JsonNode.class))
//                    .collect(Collectors.toList()));
//            dataList.set("contentList", arrayNode);
//            RenderDocument renderDocument = new RenderDocument(ScheduleServiceNew.objectMapper
//                    .readValue(jsonNode.toPrettyString(), Document.class));
//            addDirective(renderDocument);
//            setSessionAttribute(PRE_COLLE,);
//            OutputSpeech outputSpeech = new OutputSpeech(OutputSpeech.SpeechType.PlainText, Constants.WelcomeWord.WELCOME);
//            return new Response(outputSpeech);
//        } catch (Exception e) {
//            log.error("dealOpenHome error:"+e);
//        }
//        return new Response();
//    }

    private Response openPlayPage(String backgroundImg, String title, String audio, List<ActorWithWord> aws, String botAccount) {
        try {
            JsonNode jsonNode = playPageData.deepCopy();
            ObjectNode dataList = (ObjectNode) jsonNode.get("dataSource");
            dataList.set("contentList", ScheduleServiceNew.objectMapper.convertValue(aws, ArrayNode.class));
            dataList.put("audio", audio);
            if (StringUtils.isNotBlank(backgroundImg)) {
                dataList.put("backgroundImg", backgroundImg);
            }
            if (title != null) {
                dataList.put("headerTitle", title);
            }
            RenderDocument renderDocument = new RenderDocument(ScheduleServiceNew.objectMapper
                    .readValue(jsonNode.toPrettyString(), Document.class));
            addDirective(renderDocument);

            String trace = getSessionAttribute(BACK_TRACE);
            if (trace == null || trace.length() != 2) trace = "00";
            trace += botAccount + File.separator;
            setSessionAttribute(BACK_TRACE, trace);
            setSessionAttribute("botAcc", botAccount);
        } catch (Exception e) {
            log.error("openPlayPage error:"+e);
        }
        return new Response();
    }

    private Response openRandomHome() {
        try {
            JsonNode jsonNode = homePageData.deepCopy();
            ObjectNode dataList = (ObjectNode) jsonNode.get("dataSource");
            List<ProjectData> botData = ScheduleServiceNew.getTheChannelAllProjectData(channel);
            ArrayNode arrayNode = ScheduleServiceNew.objectMapper.createArrayNode();
            int start;
            arrayNode.addAll(rowColumn4Recur(botData, (start=RandomUtils.nextInt(0, botData.size()))).stream().map(homePageData ->
                    ScheduleServiceNew.objectMapper.convertValue(homePageData, JsonNode.class))
                    .collect(Collectors.toList()));
            dataList.set("contentList", arrayNode);
            RenderDocument renderDocument = new RenderDocument(ScheduleServiceNew.objectMapper
                    .readValue(jsonNode.toPrettyString(), Document.class));
            addDirective(renderDocument);
            setSessionAttribute(BACK_TRACE, null);
            setSessionAttribute(START_ACTUAL_INDEX, String.valueOf(start));
            return new Response();
        } catch (Exception e) {
            log.error("dealOpenHome error:"+e);
        }
        return new Response();
    }

    private Response dealRankIntent(int pageIndex) {
        try {
            JsonNode jsonNode = productListPageData.deepCopy();
            ObjectNode dataList = (ObjectNode) jsonNode.get("dataSource");
            List<ProjectData> botData = ScheduleServiceNew.getTheChannelProjectsByWatch(channel);
            int start = (pageIndex-1)*RE_RA_CA_PAGE_SIZE;
            if (start >= botData.size()) {
                return buildTextResponse("主人,没有更多了");
            }
            ArrayNode arrayNode = ScheduleServiceNew.objectMapper.createArrayNode();
            arrayNode.addAll(rowColumn4(botData, start, 3).stream().map(homePageData ->
                    ScheduleServiceNew.objectMapper.convertValue(homePageData, JsonNode.class))
                    .collect(Collectors.toList()));
            dataList.set("contentList", arrayNode);
            dataList.put("headerTitle", "排行榜");
            dataList.put("sideText", pageIndex + "/" +
                    (botData.size()/RE_RA_CA_PAGE_SIZE + (botData.size()%RE_RA_CA_PAGE_SIZE==0?0:1)));
            dataList.put("rank", true);
            RenderDocument renderDocument = new RenderDocument(ScheduleServiceNew.objectMapper
                    .readValue(jsonNode.toPrettyString(), Document.class));
            addDirective(renderDocument);
            setSessionAttribute(NEXT_PAGE_INDEX, String.valueOf(pageIndex+1));
            setSessionAttribute(BACK_TRACE, "04");
            return buildTextResponse("主人,正在为您打开排行榜,请稍等");
        } catch (Exception e) {
            log.error("dealRankIntent:"+e);
        }
        return new Response();
    }

    private Response openCategory() {
        try {
            RenderDocument render = new RenderDocument(categoryPage);
            addDirective(render);
            setSessionAttribute(BACK_TRACE,"07");
            return buildTextResponse("主人,正在为您打开分类页,请稍等");
        } catch (Exception e) {
            log.error("dealOpenCategory:"+e);
        }
        return new Response();
    }

    private Response openCategoryProductList(String categoryName, int pageIndex) {
        try {
            if (StringUtils.isBlank(categoryName)) {
                return buildTextResponse("主人,晓悟无法确定您现在的位置,只能麻烦您亲自动手了");
            }
            JsonNode jsonNode = productListPageData.deepCopy();
            ObjectNode dataList = (ObjectNode) jsonNode.get("dataSource");
            List<ProjectData> botData = ScheduleServiceNew.category2Bots.get(categoryName);
            int start = (pageIndex-1)*RE_RA_CA_PAGE_SIZE;
            if (start >= botData.size()) {
                return buildTextResponse("主人,没有更多了");
            }

            ArrayNode arrayNode = ScheduleServiceNew.objectMapper.createArrayNode();
            arrayNode.addAll(rowColumn4(botData, start, 3).stream().map(homePageData ->
                    ScheduleServiceNew.objectMapper.convertValue(homePageData, JsonNode.class))
                    .collect(Collectors.toList()));
            dataList.set("contentList", arrayNode);
            dataList.put("headerTitle", categoryName);
            dataList.put("sideText", pageIndex + "/" +
                    (botData.size()/RE_RA_CA_PAGE_SIZE + (botData.size()%RE_RA_CA_PAGE_SIZE==0?0:1)));
            RenderDocument renderDocument = new RenderDocument(ScheduleServiceNew.objectMapper
                    .readValue(jsonNode.toPrettyString(), Document.class));
            addDirective(renderDocument);
            setSessionAttribute(NEXT_PAGE_INDEX, String.valueOf(pageIndex+1));
            setSessionAttribute(BACK_TRACE, "75");
            setSessionAttribute(CATEGORY_NAME, categoryName);
            return buildTextResponse("主人,为您展示如下"+categoryName+"类型作品");
        } catch (Exception e) {
            log.error("dealRankIntent:"+e);
        }
        return new Response();
    }

    private Response dealRecommend(String data, String text, String path) {
        try {
            if (StringUtils.isBlank(path)) {
                path = OkHttp3Utils.doPostJsonStr(ScheduleServiceNew.BASE_AUDIO_URL+"/api/audio", data);
                if (StringUtils.isBlank(path) || "null".equals(path)) {
                    log.error("dealRecommend小度没有获取到音频链接");
                    return buildTextResponse(ErrorMsg.SORRY_UNCATCH);
                }
                path = dealAudioUrl(path, ScheduleServiceNew.getTheChannelBotAccount(channel));
            }
            List<String> botNames = Utils.getRecommandBotNames(text);
            AtomicInteger num = new AtomicInteger(1);
            List<ProjectClickData> botData = botNames.stream()
                    .map(s -> {
                        ProjectData bot = ScheduleServiceNew.getProjectByName(s);
                        if (bot == null) return null;
                        ProjectClickData clickData = new ProjectClickData();
                        clickData.setNum1(num.getAndIncrement());
                        clickData.setImageId1(Constants.PRE_PRODUCT_IMG + bot.getName());
                        clickData.setImageName1(bot.getName());
                        clickData.setImg1(bot.getBannerImgUrl());
                        return clickData;
                    })
                    .filter(Objects::nonNull).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(botData)) return new Response();
            JsonNode jsonNode = recommendPageData.deepCopy();
            ObjectNode dataList = (ObjectNode) jsonNode.get("dataSource");
            ArrayNode arrayNode = ScheduleServiceNew.objectMapper.createArrayNode();
            arrayNode.addAll(botData.stream().map(homePageData ->
                    ScheduleServiceNew.objectMapper.convertValue(homePageData, JsonNode.class))
                    .collect(Collectors.toList()));
            dataList.set("contentList", arrayNode);
            dataList.put("audio", path);
            addDirective(new RenderDocument(ScheduleServiceNew.objectMapper
                    .readValue(jsonNode.toPrettyString(), Document.class)));
            setSessionAttribute(PRE_COLLE, String.join(",",botNames));
            setSessionAttribute(BACK_TRACE, "06");
        } catch (Exception e) {
            log.warn("dealRecommend:"+e);
        }
        return new Response();
    }

    private Response dealHistoryIntent(int indexPage) {
        try {
            CoHiPageData data = getDpl2History(customUserId(), indexPage);
            if (data == null) {
                return buildTextResponse("主人,没有找到您的游戏记录");
            } else if (StringUtils.isBlank(data.getHeaderTitle())) {
                return buildTextResponse("主人,没有更多了");
            }
            JsonNode jsonNode = colHisPageData.deepCopy();
            ObjectNode root = (ObjectNode) jsonNode;
            root.set("dataSource", ScheduleServiceNew.objectMapper.convertValue(data, JsonNode.class));

            addDirective(new RenderDocument(ScheduleServiceNew.objectMapper
                    .readValue(jsonNode.toPrettyString(), Document.class)));
            setSessionAttribute(BACK_TRACE, "02");
            setSessionAttribute(NEXT_PAGE_INDEX, String.valueOf(indexPage+1));
            return buildTextResponse("主人,正在为您打开历史记录,请稍等");
        } catch (Exception e) {
            log.error("dealHistoryIntent error:"+e);
        }
        return new Response();
    }

    private Response dealOpenCollectIntent(int indexPage) {
        if (getAccessToken() == null) {
            return buildTextResponse("主人,该功能需要登录,请说小度小度,登录");
        } else {
            try {
                CoHiPageData data = getDpl2Collect(customUserId(), indexPage);
                if (data == null) {
                    return new Response();
                } else if (data.getHeaderTitle() == null) {
                    return buildTextResponse("主人,没有找到您的收藏记录");
                }
                JsonNode jsonNode = colHisPageData.deepCopy();
                ObjectNode root = (ObjectNode) jsonNode;
                root.set("dataSource", ScheduleServiceNew.objectMapper.convertValue(data, JsonNode.class));

                addDirective(new RenderDocument(ScheduleServiceNew.objectMapper
                        .readValue(jsonNode.toPrettyString(), Document.class)));
                setSessionAttribute(NEXT_PAGE_INDEX, String.valueOf(indexPage+1));
                setSessionAttribute(BACK_TRACE, "01");
                return buildTextResponse("主人,正在为您打开收藏列表,请稍等");
            } catch (Exception e) {
                log.error("dealOpenCollectIntent error:"+e);
            }
            return new Response();
        }
    }

    private Response dealOpenProductDetail(ProjectData productDetail, boolean collect) {
        try {
            String userId = customUserId();
            String productName = productDetail.getName();

            ProjectDetailData data = new ProjectDetailData();

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String dateString = formatter.format(productDetail.getCreateTime());
            data.setImgUrl(productDetail.getBannerImgUrl());
            data.setWorkName(productDetail.getName());
            data.setTime(PRODUCT_INTRO.CREATE_TIME + dateString);
            data.setAuthor(PRODUCT_INTRO.AUTHOR + productDetail.getAuthorName());
            data.setLabel(PRODUCT_INTRO.LABEL+String.join(PRODUCT_INTRO.SPACE, productDetail.getLabels()));
            data.setIntro(productDetail.getIntro());
            data.setId(Constants.PRE_ENTER_PRODUCT + productName);

            Long flowerCount = DplbotServiceUtil.getUserSendFlowerNum(productDetail.getBotAccount(), userId,
                    channel,getAccessToken(), getSessionAttribute("userId"));
            data.setFlowText(USER_SEND_FLOWER+flowerCount);
            data.setLogin(true);
            data.setFlowerId(FLOWE + productDetail.getBotAccount());
            data.setCollect(collect);

            JsonNode jsonNode = productDetailPageData.deepCopy();
            ObjectNode root = (ObjectNode) jsonNode;
            root.set("dataSource", ScheduleServiceNew.objectMapper.convertValue(data, JsonNode.class));

            addDirective(new RenderDocument(ScheduleServiceNew.objectMapper
                    .readValue(jsonNode.toPrettyString(), Document.class)));

            String trace = getSessionAttribute(BACK_TRACE);
            StringBuilder builder = new StringBuilder();
            if (trace == null || trace.length() < 2) {
                builder.append("00");
            } else if (trace.length()>2) {
                builder.append(trace, 0, 2);
            } else {
                builder.append(trace);
            }

            trace = builder.append(productDetail.getBotAccount()).toString();
            setSessionAttribute(BACK_TRACE, trace);
        } catch (Exception e) {
            log.error("dealOpenProductDetail:"+e);
        }
        return new Response();
    }

    private Response dealOpenProductDetail(ProjectData productDetail, long flowerNum, String jytUserId, String userId) {
        try {
            ProjectDetailData data = new ProjectDetailData();

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String dateString = formatter.format(productDetail.getCreateTime());
            data.setImgUrl(productDetail.getBannerImgUrl());
            data.setWorkName(productDetail.getName());
            data.setTime(PRODUCT_INTRO.CREATE_TIME + dateString);
            data.setAuthor(PRODUCT_INTRO.AUTHOR + productDetail.getAuthorName());
            data.setLabel(PRODUCT_INTRO.LABEL+String.join(PRODUCT_INTRO.SPACE, productDetail.getLabels()));
            data.setIntro(productDetail.getIntro());
            data.setId(Constants.PRE_ENTER_PRODUCT + productDetail.getName());

            data.setFlowText(USER_SEND_FLOWER+flowerNum);
            data.setLogin(true);
            data.setFlowerId(FLOWE + productDetail.getBotAccount());
            data.setCollect(DplbotServiceUtil.didUserCollecThisProduct(productDetail.getBotAccount(), userId, jytUserId, channel));

            JsonNode jsonNode = productDetailPageData.deepCopy();
            ObjectNode root = (ObjectNode) jsonNode;
            root.set("dataSource", ScheduleServiceNew.objectMapper.convertValue(data, JsonNode.class));

            addDirective(new RenderDocument(ScheduleServiceNew.objectMapper
                    .readValue(jsonNode.toPrettyString(), Document.class)));

            String trace = getSessionAttribute(BACK_TRACE);
            StringBuilder builder = new StringBuilder();
            if (trace == null || trace.length() < 2) {
                builder.append("00");
            } else if (trace.length()>2) {
                builder.append(trace, 0, 2);
            } else {
                builder.append(trace);
            }

            trace = builder.append(productDetail.getBotAccount()).toString();
            setSessionAttribute(BACK_TRACE, trace);
        } catch (Exception e) {
            log.error("dealOpenProductDetail:"+e);
        }
        return new Response();
    }

    private Response dealOpenProductDetail(ProjectData productDetail) {
        try {
            if (productDetail == null) {
                log.warn("dealOpenProductDetail没有找到作品");
                return new Response();
            }
            String accessToken = getAccessToken();
            String userId = customUserId();
            String productName = productDetail.getName();

            ProjectDetailData data = new ProjectDetailData();

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String dateString = formatter.format(productDetail.getCreateTime());
            data.setImgUrl(productDetail.getBannerImgUrl());
            data.setWorkName(productDetail.getName());
            data.setTime(PRODUCT_INTRO.CREATE_TIME + dateString);
            data.setAuthor(PRODUCT_INTRO.AUTHOR + productDetail.getAuthorName());
            data.setLabel(PRODUCT_INTRO.LABEL+String.join(PRODUCT_INTRO.SPACE, productDetail.getLabels()));
            data.setIntro(productDetail.getIntro());
            data.setId(Constants.PRE_ENTER_PRODUCT + productName);

            // "start product" container button
            // collectText, collectUrl, collectComId
            String flowers;
            boolean collect = false;
            if (accessToken != null) {
                Long flowerCount = DplbotServiceUtil.getUserSendFlowerNum(productDetail.getBotAccount(), userId,
                        channel,accessToken, getSessionAttribute("userId"));
                flowers = USER_SEND_FLOWER+flowerCount;

                // collection container button
                collect = DplbotServiceUtil.didUserCollecThisProduct(productDetail.getBotAccount(), userId,
                        getSessionAttribute("userId"), channel);
                data.setLogin(true);
//                if (collec) {
//                    collectText = COLLECED;
//                    collectUrl = COLLECTED_IMG_URL;
//                    collectComId = UN_COLLEC_ID + productDetail.getBotAccount();
//                } else {
//                    collectText = UN_COLLEC;
//                    collectUrl = UN_COLLECTED_IMG_URL;
//                    collectComId = COLLEC_ID + productDetail.getBotAccount();
//                }
            } else {
                flowers = FLOWER;
                data.setLogin(false);

//                collectText = UN_COLLEC;
//                collectUrl = UN_COLLECTED_IMG_URL;
//                collectComId = FLOWER_LOAD;
            }
            data.setFlowerId(FLOWE + productDetail.getBotAccount());
            data.setFlowText(flowers);
            data.setCollect(collect);
//            data.setCollectImg(collectUrl);
//            data.setCollectId(collectComId);
//            data.setCollectText(collectText);

            JsonNode jsonNode = productDetailPageData.deepCopy();
            ObjectNode root = (ObjectNode) jsonNode;
            root.set("dataSource", ScheduleServiceNew.objectMapper.convertValue(data, JsonNode.class));

            addDirective(new RenderDocument(ScheduleServiceNew.objectMapper
                    .readValue(jsonNode.toPrettyString(), Document.class)));

            String trace = getSessionAttribute(BACK_TRACE);
            StringBuilder builder = new StringBuilder();
            if (trace == null || trace.length() < 2) {
                builder.append("00");
            } else if (trace.length()>2) {
                builder.append(trace, 0, 2);
            } else {
                builder.append(trace);
            }

            trace = builder.append(productDetail.getBotAccount()).toString();
            setSessionAttribute(BACK_TRACE, trace);
        } catch (Exception e) {
            log.error("dealOpenProductDetail:"+e);
        }
        return new Response();
    }

    // ===================== load ====================

    private Response load() {
        Card card = new LinkAccountCard();
        Response response = buildTextResponse(LOAD_INTRO);
        response.setCard(card);
        return response;
    }

    //===========================tools==================

    private Response dealStartIntent(IntentRequest intentRequest) {
        String trace = getSessionAttribute("trace");
        if (trace != null && trace.length() > 2 && !StringUtils.endsWith(trace,File.separator)) {
            String botId = trace.substring(2);
            String enterName = "打开" + ScheduleServiceNew.getWorkNameByBotAccount(botId);
            return dealDefaultIntent(intentRequest, customUserId(), enterName);
        }
        return new Response();
    }

    private CoHiPageData getDpl2Collect(String userId, int indexPage) {
        UserBehaviorData behaviorData = DplbotServiceUtil.getRedisTemplate().opsForValue().get(userId);
        if (behaviorData == null) {
            return null;
        }
        Map<String, Long> userBotId2Collected = behaviorData.getUserBotId2Collected();
        int bound = userBotId2Collected.size();
        int start = (indexPage-1)*CO_HI_PAGE_SIZE;
        if (start >= bound) {
            if (bound == 0) {
                return new CoHiPageData();
            }
            return null;
        }
        CoHiPageData pageData = new CoHiPageData();
        pageData.setSideText(indexPage + "/" + (bound/CO_HI_PAGE_SIZE + (bound%CO_HI_PAGE_SIZE == 0 ? 0 : 1)));
        pageData.setHeaderTitle("收藏夹");

        TreeMap<Long, String> order = new TreeMap<>((o1, o2) -> (o1 < o2) ? 1 : ((o1.equals(o2)) ? 0 : -1));
        for (Map.Entry<String, Long> entry: userBotId2Collected.entrySet()) {
            order.put(entry.getValue(), entry.getKey());
        }
        int j = 0, end = start+CO_HI_PAGE_SIZE;
        List<CoHiOnePageData> dataList = new ArrayList<>(CO_HI_PAGE_SIZE);
        for (Map.Entry<Long, String> entry: order.entrySet()) {
            if (j == end) break;
            if (j++ >= start) {
                ProjectData productDetail = ScheduleServiceNew.getProjectByBotAccount(entry.getValue());
                if (productDetail == null) continue;
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
                String lastTime = sdf.format(new java.sql.Date(entry.getKey()));
                CoHiOnePageData onePageData = new CoHiOnePageData();
                onePageData.setTime(lastTime);
                onePageData.setId(Constants.PRE_ENTER_PRODUCT+productDetail.getName());
                onePageData.setImageUrl(productDetail.getBannerImgUrl());
                onePageData.setNum(j);
                onePageData.setWorkIntro(StringUtils.isBlank(productDetail.getIntro()) ?
                        "作者太懒没写介绍" : productDetail.getIntro());
                onePageData.setWorkName(productDetail.getName());
                onePageData.setItemSeqId(j-start);
                dataList.add(onePageData);
            }
        }
        pageData.setContentList(dataList);
        return pageData;
    }

    private CoHiPageData getDpl2History(String userId, int indexPage) {
        UserBehaviorData behaviorData = DplbotServiceUtil.getRedisTemplate().opsForValue().get(userId);
        if (behaviorData == null) {
            return null;
        } else {
            try {
                int bound = behaviorData.getUserBotId2History().size();
                int start = (indexPage-1)*CO_HI_PAGE_SIZE;
                if (start >= bound) {
                    if (bound == 0) {
                        return new CoHiPageData();
                    }
                    return null;
                }
                CoHiPageData pageData = new CoHiPageData();
                pageData.setSideText(indexPage + "/" + (bound/CO_HI_PAGE_SIZE + (bound%CO_HI_PAGE_SIZE == 0 ? 0 : 1)));
                pageData.setHeaderTitle("历史记录");

                TreeMap<Long, String> order = new TreeMap<>((o1, o2) -> (o1 < o2) ? 1 : ((o1.equals(o2)) ? 0 : -1));
                for (Map.Entry<String, Long> entry: behaviorData.getUserBotId2History().entrySet()) {
                    order.put(entry.getValue(), entry.getKey());
                }
                int j = 0, end = start+CO_HI_PAGE_SIZE;
                List<CoHiOnePageData> dataList = new ArrayList<>(CO_HI_PAGE_SIZE);
                for (Map.Entry<Long, String> entry: order.entrySet()) {
                    if (j == end) break;
                    if (j++ >= start) {
                        ProjectData productDetail = ScheduleServiceNew.getProjectByBotAccount(entry.getValue());
                        if (productDetail == null) continue;
                        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
                        String lastTime = sdf.format(new java.sql.Date(entry.getKey()));
                        CoHiOnePageData onePageData = new CoHiOnePageData();
                        onePageData.setTime(lastTime);
                        onePageData.setId(Constants.PRE_ENTER_PRODUCT+productDetail.getName());
                        onePageData.setImageUrl(productDetail.getBannerImgUrl());
                        onePageData.setNum(j);
                        onePageData.setWorkIntro(StringUtils.isBlank(productDetail.getIntro()) ?
                                "作者太懒没写介绍" : productDetail.getIntro());
                        onePageData.setWorkName(productDetail.getName());
                        onePageData.setItemSeqId(j-start);
                        dataList.add(onePageData);
                    }
                }
                pageData.setContentList(dataList);
                return pageData;
            } catch (Exception e) {
                log.error("getDpl2History:"+e);
            }
        }
        return null;
    }

    private boolean isNoScreenRequest() {
        return getSupportedInterfaces().getDisplay() == null;
    }

    private String dealAudioUrl(String path, String botAccount) {
        if (path.startsWith("a")) {
            return new StringBuilder(ScheduleServiceNew.CDN_HOST_URL).append("/api/audio/cache/").append(botAccount).append("/?filename=").append(path.substring(1)).append("&exist=1").toString();
        }
        return new StringBuilder(ScheduleServiceNew.CDN_HOST_URL).append("/api/audio/cache/").append(botAccount).append("/?filename=").append(path).toString();
    }

    private String customUserId() {
        if (customUserId == null) {
            try {
                customUserId = PREFIX_USERID+ URLEncoder.encode(getUserId(), "UTF-8");
            } catch (Exception e) {
                log.error("xiaodu userid encode error:{}, {}",e, getUserId());
                customUserId = PREFIX_USERID+ getUserId().replaceAll("/", "");
            }
        }
        return customUserId;
    }

    private List<HomePageData> rowColumn4Recur(List<ProjectData> botData, int startInc) {
        HomePageData pageData;
        ProjectData item;
        int endExc = startInc+16;
        List<HomePageData> data = new ArrayList<>(4);
        int row = 0;
        while (startInc < endExc) {
            pageData = new HomePageData();
            for (int j = 1; j < 5; startInc++,j++) {
                item = botData.get(startInc%botData.size());
                if (j == 1) {
                    pageData.setImageId1(Constants.PRE_PRODUCT_IMG + item.getName());
                    pageData.setImageName1(item.getName());
                    pageData.setImg1(item.getBannerImgUrl());
                    pageData.setNum1(j+row*4);
                } else if (j == 2) {
                    pageData.setImageId2(Constants.PRE_PRODUCT_IMG + item.getName());
                    pageData.setImageName2(item.getName());
                    pageData.setImg2(item.getBannerImgUrl());
                    pageData.setNum2(j+row*4);
                } else if (j == 3) {
                    pageData.setImageId3(Constants.PRE_PRODUCT_IMG + item.getName());
                    pageData.setImageName3(item.getName());
                    pageData.setImg3(item.getBannerImgUrl());
                    pageData.setNum3(j+row*4);
                } else {
                    pageData.setImageId4(Constants.PRE_PRODUCT_IMG + item.getName());
                    pageData.setImageName4(item.getName());
                    pageData.setImg4(item.getBannerImgUrl());
                    pageData.setNum4(j+row*4);
                }
            }
            pageData.setItemSeqId(++row);
            data.add(pageData);
        }
        return data;
    }

    // 从botData列表startInc开始到endExc结束，一行有4个，一共row行
    private List<HomePageData> rowColumn4(List<ProjectData> botData, int startInc, int row) {
        int onceEndExc;
        HomePageData pageData;
        ProjectData item;
        int endExc = startInc+row*4 >= botData.size() ? botData.size() : startInc+row*4;
        List<HomePageData> data = new ArrayList<>(row);
        int curItemIndex = 0;
        while (startInc < endExc) {
            onceEndExc = startInc + 4 >= endExc ? endExc : startInc + 4;
            pageData = new HomePageData();
            for (int j = 1; startInc < onceEndExc; startInc++,j++) {
                item = botData.get(startInc);
                if (j == 1) {
                    pageData.setImageId1(Constants.PRE_PRODUCT_IMG + item.getName());
                    pageData.setImageName1(item.getName());
                    pageData.setImg1(item.getBannerImgUrl());
                    pageData.setNum1(startInc+1);
                } else if (j == 2) {
                    pageData.setImageId2(Constants.PRE_PRODUCT_IMG + item.getName());
                    pageData.setImageName2(item.getName());
                    pageData.setImg2(item.getBannerImgUrl());
                    pageData.setNum2(startInc+1);
                } else if (j == 3) {
                    pageData.setImageId3(Constants.PRE_PRODUCT_IMG + item.getName());
                    pageData.setImageName3(item.getName());
                    pageData.setImg3(item.getBannerImgUrl());
                    pageData.setNum3(startInc+1);
                } else {
                    pageData.setImageId4(Constants.PRE_PRODUCT_IMG + item.getName());
                    pageData.setImageName4(item.getName());
                    pageData.setImg4(item.getBannerImgUrl());
                    pageData.setNum4(startInc+1);
                }
            }
            pageData.setItemSeqId(++curItemIndex);
            data.add(pageData);
        }
        return data;
    }

    private Response buildTextResponse(String text) {
        OutputSpeech outputSpeech = new OutputSpeech(OutputSpeech.SpeechType.PlainText, text);
        return new Response(outputSpeech);
    }
}
