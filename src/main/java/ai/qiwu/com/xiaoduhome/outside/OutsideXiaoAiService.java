package ai.qiwu.com.xiaoduhome.outside;

import ai.qiwu.com.xiaoduhome.common.Constants;
import ai.qiwu.com.xiaoduhome.outside.model.AudioInfo;
import ai.qiwu.com.xiaoduhome.outside.model.SingleBotResponse;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static ai.qiwu.com.xiaoduhome.xiaoai.common.XiaoAiConstants.ERROR_MSG.SORRY_REPEAT;
import static ai.qiwu.com.xiaoduhome.xiaoai.common.XiaoAiConstants.PREFIX_USERID;

/**
 * painter
 * 20-6-28 下午3:57
 */
@Service
@Slf4j
public class OutsideXiaoAiService {
    private final StringRedisTemplate stringRedisTemplate;

    @Autowired
    public OutsideXiaoAiService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 处理不同意图,type为0则说明是启动,为1则说明是会话进行中,为2则说明用户退出
     * @param request 封装请求信息
     * @return 响应结果
     */
    public XiaoAiResponse getResponse(XiaoAiRequest request, AudioInfo info, Boolean dev) {
        String userId = getUserId(request);
        int type = request.getRequest().getType();
        if (dev && Boolean.TRUE.equals(request.getRequest().getIs_monitor())) {
            return buildTextResponse("outside 判定为监控请求,过滤");
        }
        switch (type) {
            case 0 : return dealWithLaunchRequest(userId, info);
            case 1 : return dealWithIntentRequest(request, userId, info);
            case 2 : return dealWithEndRequest(userId, info.getLastWord());
            default: return buildTextResponse(SORRY_REPEAT);
        }
    }

    /**
     * 处理启动意图，返回欢迎语
     * @return 响应信息
     */
    private XiaoAiResponse dealWithLaunchRequest(String userId, AudioInfo info) {
        stringRedisTemplate.delete(SpiritRedisService.PREFIX_REDIS+userId);
        String welcomeWord;
        if (StringUtils.isBlank((welcomeWord=info.getFirstWord()))) {
            String redisId = SpiritRedisService.PREFIX_REDIS+userId;
            try {
                Future<XiaoAiResponse> future = DplbotServiceUtil.getPool().submit(() -> task(userId, "开始", info));
                return future.get(2000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException | ExecutionException e) {
                log.error("outside 获取任务执行结果出错:{}", e.toString());
                return buildTextResponse(SORRY_REPEAT);
            } catch (TimeoutException e) {
                log.info("outside 超时,让用户再说一遍");
                stringRedisTemplate.opsForValue().setIfAbsent(redisId, "out", 15, TimeUnit.SECONDS);
                return buildTextResponse(XiaoAiConstants.ERROR_MSG.TIME_OUT);
            } catch (Exception all) {
                log.error("outside 任务执行发生了错误:{}", all.toString());
                return buildTextResponse(SORRY_REPEAT);
            }
        }
        return buildTextResponse(welcomeWord);
    }

    /**
     * 处理正常会话意图;
     * 将任务提交线程池，监控其执行时间，若1.8s内没有返回则判为超时，提示用户稍等，下一次再将结果返回给用户
     * @param request 请求信息
     * @param userId 用户在小爱上的id
     * @return 响应信息
     */
    private XiaoAiResponse dealWithIntentRequest(final XiaoAiRequest request, final String userId, AudioInfo info) {
        Boolean idle = request.getRequest().getNo_response();
        String redisId = SpiritRedisService.PREFIX_REDIS+userId;
        try {
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisId))) {
                String data = stringRedisTemplate.opsForValue().get(redisId);
                log.info("outside 超时redis获取信息："+data);
                if (StringUtils.isNotBlank(data)) {
                    String redisIdleId = SpiritRedisService.PREFIX_REDIS_IDLE+userId;
                    if (data.equals("out")) {
                        String idleNum = stringRedisTemplate.opsForValue().get(redisIdleId);
                        boolean firstIdle;
                        if ((firstIdle = StringUtils.isBlank(idleNum)) || idleNum.equals("1")) {
                            try {
                                Thread.sleep(1500);
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
                        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisIdleId))) stringRedisTemplate.delete(redisIdleId);
                        stringRedisTemplate.delete(redisId);
                        return buildAudioResponse(data);
                    }
                }
            }
        } catch (Exception e) {
            log.error("outside xiaoai redis error:"+e);
        }
        if (Boolean.TRUE.equals(idle)) {
            try {
                //log.info("开始等待");
                Thread.sleep(2000);
                //log.info("等待OVER");
            } catch (InterruptedException e) {
                log.warn("outside 等待中断");
            }
            return buildTextResponse((RandomUtils.nextInt() & 1) == 1 ? XiaoAiConstants.TTS.USER_IDLE :
                    XiaoAiConstants.TTS.ANOTHER_USER_IDLE);
        }
        try {
            Future<XiaoAiResponse> future = DplbotServiceUtil.getPool().submit(() -> task(userId, request.getQuery(), info));
            return future.get(2000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException e) {
            log.error("outside 获取任务执行结果出错:{}", e.toString());
            return buildTextResponse(SORRY_REPEAT);
        } catch (TimeoutException e) {
            log.info("outside 超时,让用户再说一遍");
            stringRedisTemplate.opsForValue().setIfAbsent(redisId, "out", 15, TimeUnit.SECONDS);
            return buildTextResponse(XiaoAiConstants.ERROR_MSG.TIME_OUT);
        } catch (Exception all) {
            log.error("outside 任务执行发生了错误:{}", all.toString());
            return buildTextResponse(SORRY_REPEAT);
        }
    }

    /**
     * 处理用户退出意图,先向中控发送“退出”，之后返回结束语
     * @param userId 用户id
     * @return 返回信息
     */
    private XiaoAiResponse dealWithEndRequest(String userId, String byeWord) {
        if (ScheduleServiceNew.SERVER_CHANGE) stringRedisTemplate.delete(Constants.CAN_NOT_MOVE_USER_KEY+userId);
        if (StringUtils.isBlank(byeWord)) byeWord = OutsideConstant.OUTSIDE_BYE_WORD;
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
            log.error("outside xiaoai userid encode error:{}, {}",e, userId);
            return PREFIX_USERID+ userId.replaceAll("/", "");
        }
    }

    private XiaoAiResponse task(String userId, String userWord, AudioInfo info) {
        if (userWord == null) {
            log.warn("outside 请求中没有带有用户的话");
            throw new RequestWordNullException("outside user word can not be null");
        }
        SingleBotResponse response;
        try {
            log.info("小爱,用户话:{}", userWord);
            response = RequestTerminal.outSideRequestTerminate(userId, userWord, info.getBotAccount());
            log.info("小爱,小悟返回:{}", response);
        } catch (Exception e) {
            log.error("向终端发出的请求信息失败:{}", e.toString());
            return buildTextResponse(SORRY_REPEAT);
        }
        if (OutsideConstant.CMDCODE_OUT.equals(response.getData().getCmdCode())) {
            String bye = info.getLastWord();
            if (StringUtils.isBlank(bye)) bye = "再见";
            return buildResponseBeforeLoadout(bye);
        }
        XiaoAiResponse xiaoAiResponse;
        try {
            String audioUrl;
            xiaoAiResponse = buildAudioResponse((audioUrl=response.getData().getAudioUrl()));
            if (Boolean.TRUE.equals(stringRedisTemplate.opsForValue().setIfPresent(SpiritRedisService.PREFIX_REDIS+userId,
                    audioUrl, 15, TimeUnit.SECONDS))) {
                log.info("outside xiaoai 超时redis存储成功");
            }
        } catch (Exception e) {
            log.error("outside 没有获取到TTS返回的路径path");
            xiaoAiResponse = buildTextResponse(SORRY_REPEAT);
        }
        return xiaoAiResponse;
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
}
