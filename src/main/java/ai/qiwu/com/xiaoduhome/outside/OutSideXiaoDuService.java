package ai.qiwu.com.xiaoduhome.outside;

import ai.qiwu.com.xiaoduhome.common.Constants;
import ai.qiwu.com.xiaoduhome.outside.model.AudioInfo;
import ai.qiwu.com.xiaoduhome.outside.model.SingleBotResponse;
import ai.qiwu.com.xiaoduhome.service.DplbotServiceUtil;
import ai.qiwu.com.xiaoduhome.spirit.SpiritRedisService;
import ai.qiwu.com.xiaoduhome.xiaoai.common.RequestTerminal;
import ai.qiwu.com.xiaoduhome.xiaoai.common.XiaoAiConstants;
import ai.qiwu.com.xiaoduhome.baidu.dueros.bot.BaseBot;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.request.IntentRequest;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.request.LaunchRequest;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.request.SessionEndedRequest;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.request.videoplayer.event.PlaybackFinishedEvent;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.request.videoplayer.event.PlaybackNearlyFinishedEvent;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.request.videoplayer.event.PlaybackStartedEvent;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.request.videoplayer.event.PlaybackStoppedEvent;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.OutputSpeech;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.directive.Directive;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.directive.audioplayer.AudioPlayerDirective;
import ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.directive.audioplayer.Play;
import ai.qiwu.com.xiaoduhome.baidu.dueros.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static ai.qiwu.com.xiaoduhome.common.Constants.ErrorMsg.SORRY_UNCATCH;
import static ai.qiwu.com.xiaoduhome.common.Constants.PREFIX_USERID;

/**
 * painter
 * 20-7-2 下午4:28
 */
@Slf4j
public class OutSideXiaoDuService extends BaseBot {

    private final StringRedisTemplate stringRedisTemplate;
    private final AudioInfo audioInfo;

    OutSideXiaoDuService(HttpServletRequest request, StringRedisTemplate stringRedisTemplate, AudioInfo audioInfo) throws IOException {
        super(request);
        this.stringRedisTemplate = stringRedisTemplate;
        this.audioInfo = audioInfo;
        //privateKey为私钥内容,0代表你的Bot在DBP平台debug环境，1或者其他整数代表online环境,
        // botMonitor对象已经在bot-sdk里初始化，可以直接调用
        this.botMonitor.setEnvironmentInfo(audioInfo.getPrivateKey(), 1);
    }

    @Override
    protected Response onLaunch(LaunchRequest launchRequest) {
        String redisId;
        String userId = customUserId();
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey((redisId= SpiritRedisService.PREFIX_REDIS+userId)))) {
            stringRedisTemplate.delete(redisId);
        }
        String welcomeWord = audioInfo.getFirstWord();
        if (StringUtils.isBlank(welcomeWord)) {
            try {
                Future<Response> future = DplbotServiceUtil.getPOOL().submit(() -> dealNoScreenIntentTemp("开始", userId));
                return future.get(2500, TimeUnit.MILLISECONDS);
            } catch (InterruptedException | ExecutionException e) {
                log.error("outside xiaodu 获取任务执行结果出错:{}", ExceptionUtils.getStackTrace(e));
                return buildTextResponse(SORRY_UNCATCH);
            } catch (TimeoutException e) {
                log.warn("outside xiaodu 超时,让用户再说一遍:{}");
                stringRedisTemplate.opsForValue().setIfAbsent(redisId, "out", 15, TimeUnit.SECONDS);
                return buildTextResponse(SORRY_UNCATCH);
            } catch (Exception all) {
                log.error("outside xiaodu 任务执行发生了错误:{}", ExceptionUtils.getStackTrace(all));
                return buildTextResponse(SORRY_UNCATCH);
            }
        }
        OutputSpeech outputSpeech = new OutputSpeech(OutputSpeech.SpeechType.PlainText, welcomeWord);
        return new Response(outputSpeech);
    }

    @Override
    protected Response onInent(IntentRequest intentRequest) {
        final String userId = customUserId();
        String redisId = SpiritRedisService.PREFIX_REDIS+userId;
        try {
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisId))) {
                String data = stringRedisTemplate.opsForValue().get(redisId);
                log.warn("超时redis获取信息："+data);
                if (StringUtils.isNotBlank(data)) {
                    String redisIdleId = SpiritRedisService.PREFIX_REDIS_IDLE+userId;
                    if (data.equals("out")) {
                        String idleNum = stringRedisTemplate.opsForValue().get(redisIdleId);
                        boolean firstIdle;
                        if ((firstIdle = StringUtils.isBlank(idleNum)) || idleNum.equals("1")) {
                            try {
                                Thread.sleep(2000);
                            } catch (Exception e){}
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
                        stringRedisTemplate.delete(redisId);
                        stringRedisTemplate.delete(redisIdleId);
                        Directive play = buildNoScreenPlay(data, AudioPlayerDirective.PlayBehaviorType.REPLACE_ALL);
                        addDirective(play);
                        return new Response();
                    }
                } else {
                    stringRedisTemplate.delete(redisId);
                }
            }
        } catch (Exception e) {
            log.error("xiaodu redis error:"+e);
        }

        try {

            Future<Response> future = DplbotServiceUtil.getPOOL().submit(() -> dealNoScreenIntentTemp(intentRequest.getQuery().getOriginal(), userId));
            return future.get(2500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException e) {
            log.error("outside xiaodu 获取任务执行结果出错:{}", ExceptionUtils.getStackTrace(e));
            return buildTextResponse(SORRY_UNCATCH);
        } catch (TimeoutException e) {
            log.warn("outside xiaodu 超时,让用户再说一遍:{}");
            stringRedisTemplate.opsForValue().setIfAbsent(redisId, "out", 15, TimeUnit.SECONDS);
            return buildTextResponse(SORRY_UNCATCH);
        } catch (Exception all) {
            log.error("outside xiaodu 任务执行发生了错误:{}", ExceptionUtils.getStackTrace(all));
            return buildTextResponse(SORRY_UNCATCH);
        }
    }

    @Override
    protected Response onSessionEnded(SessionEndedRequest sessionEndedRequest) {
        switch (sessionEndedRequest.getReason()) {
            case ERROR: dealWithError(sessionEndedRequest); break;
            case USER_INITIATED: log.info(Constants.EndMsg.USER_SAY_CANCEL); break;
            case EXCEEDED_MAX_REPROMPTS: log.info(Constants.EndMsg.CAN_NOT_UNDERSTAND);break;
        }
        String byeWord = audioInfo.getLastWord();
        if (StringUtils.isBlank(byeWord)) byeWord = OutsideConstant.OUTSIDE_BYE_WORD;
        OutputSpeech outputSpeech = new OutputSpeech(OutputSpeech.SpeechType.PlainText, byeWord);
        return new Response(outputSpeech);
    }

    @Override
    protected Response onPlaybackStartedEvent(PlaybackStartedEvent playbackStartedEvent) {
        waitAnswer();
        return new Response();
    }

    @Override
    protected Response onPlaybackStoppedEvent(PlaybackStoppedEvent playbackStoppedEvent) {
        waitAnswer();
        return new Response();
    }

    @Override
    protected Response onPlaybackNearlyFinishedEvent(PlaybackNearlyFinishedEvent playbackNearlyFinishedEvent) {
        waitAnswer();
        return new Response();
    }

    @Override
    protected Response onPlaybackFinishedEvent(PlaybackFinishedEvent playbackFinishedEvent) {
        this.setExpectSpeech(true);
        return new Response();
    }

    private String customUserId() {
        try {
            return PREFIX_USERID+ URLEncoder.encode(getUserId(), "UTF-8");
        } catch (Exception e) {
            log.error("outside xiaodu userid encode error:{}, {}",e, getUserId());
            return PREFIX_USERID+ getUserId().replaceAll("/", "");
        }
    }

    private Response buildTextResponse(String errorMsg) {
        OutputSpeech outputSpeech = new OutputSpeech(OutputSpeech.SpeechType.PlainText, errorMsg);
        return new Response(outputSpeech);
    }

    private Play buildNoScreenPlay(String audioUrl, AudioPlayerDirective.PlayBehaviorType type) {
        return new Play(type, audioUrl, 0);
    }

    private Response dealNoScreenIntentTemp(String userWord, String userId) {
        try {
            if (userWord == null){
                log.error("小度没有传来任何话语");
                return buildTextResponse(Constants.ErrorMsg.SORRY_UNCATCH);
            }
            log.info("outside 小度.用户说的话：{}",userWord);
            SingleBotResponse response = RequestTerminal.xiaoduOutSideRequestTerminate(userId, userWord, audioInfo.getBotAccount());
            log.info("outside 小度,小悟返回：{},",response);

            if (OutsideConstant.CMDCODE_OUT.equals(response.getData().getCmdCode())) {
                String bye = audioInfo.getLastWord();
                if (StringUtils.isBlank(bye)) bye = "再见";
                return buildTextResponse(bye);
            }
            String path;
            Directive play = buildNoScreenPlay((path=response.getData().getAudioUrl()),  AudioPlayerDirective.PlayBehaviorType.REPLACE_ALL);
            addDirective(play);
            if (Boolean.TRUE.equals(stringRedisTemplate.opsForValue()
                    .setIfPresent(SpiritRedisService.PREFIX_REDIS+userId, path, 15, TimeUnit.SECONDS))) {
                log.warn("outside xiaodu 超时redis存储成功");
            }
            return new Response();
        } catch (Exception e) {
            log.warn("outside xiaodu 获取中控结果出错:"+e);
            return buildTextResponse(Constants.ErrorMsg.SORRY_UNCATCH);
        }
    }

    private void dealWithError(SessionEndedRequest request) {
        switch (request.getError().getType()) {
            case INTERNAL_ERROR: log.info(Constants.ErrorMsg.INTERNAL_ERROR); break;
            case INVALID_RESPONSE: log.info(Constants.ErrorMsg.INVALID_RESPONSE); break;
            case DEVICE_COMMUNICATION_ERROR: log.info(Constants.ErrorMsg.DEVICE_COMMUNICATION_ERROR); break;
        }
        log.info("错误信息 : "+ request.getError().getMessage());
    }
}
