/* 
 * Copyright (c) 2017 Baidu, Inc. All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.baidu.dueros.bot;

import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import com.baidu.apm.monitor.BotMonitor;
import com.baidu.dueros.certificate.Certificate;
import com.baidu.dueros.data.request.IntentRequest;
import com.baidu.dueros.data.request.LaunchRequest;
import com.baidu.dueros.data.request.Query;
import com.baidu.dueros.data.request.RequestBody;
import com.baidu.dueros.data.request.SessionEndedRequest;
import com.baidu.dueros.data.request.SupportedInterfaces;
import com.baidu.dueros.data.request.audioplayer.event.AudioPlayerEvent;
import com.baidu.dueros.data.request.buy.event.BuyEvent;
import com.baidu.dueros.data.request.events.*;
import com.baidu.dueros.data.request.pay.event.ChargeEvent;
import com.baidu.dueros.data.request.permission.event.PermissionGrantFailedEvent;
import com.baidu.dueros.data.request.permission.event.PermissionGrantedEvent;
import com.baidu.dueros.data.request.permission.event.PermissionRejectedEvent;
import com.baidu.dueros.data.request.permission.event.PermissionRequiredEvent;
import com.baidu.dueros.data.request.videoplayer.event.PlaybackFinishedEvent;
import com.baidu.dueros.data.request.videoplayer.event.PlaybackNearlyFinishedEvent;
import com.baidu.dueros.data.request.videoplayer.event.PlaybackPausedEvent;
import com.baidu.dueros.data.request.videoplayer.event.PlaybackQueueClearedEvent;
import com.baidu.dueros.data.request.videoplayer.event.PlaybackResumedEvent;
import com.baidu.dueros.data.request.videoplayer.event.PlaybackStartedEvent;
import com.baidu.dueros.data.request.videoplayer.event.PlaybackStoppedEvent;
import com.baidu.dueros.data.request.videoplayer.event.PlaybackStutterFinishedEvent;
import com.baidu.dueros.data.request.videoplayer.event.PlaybackStutterStartedEvent;
import com.baidu.dueros.data.request.videoplayer.event.ProgressReportDelayElapsedEvent;
import com.baidu.dueros.data.request.videoplayer.event.ProgressReportIntervalElapsedEvent;
import com.baidu.dueros.data.request.videoplayer.event.VideoPlayerEvent;
import com.baidu.dueros.data.response.Context;
import com.baidu.dueros.data.response.ExpectResponse;
import com.baidu.dueros.data.response.ResponseBody;
import com.baidu.dueros.data.response.Session;
import com.baidu.dueros.data.response.ExpectResponse.ExpectResponseType;
import com.baidu.dueros.data.response.directive.ConfirmIntent;
import com.baidu.dueros.data.response.directive.ConfirmSlot;
import com.baidu.dueros.data.response.directive.Delegate;
import com.baidu.dueros.data.response.directive.Directive;
import com.baidu.dueros.data.response.directive.ElicitSlot;
import com.baidu.dueros.data.response.directive.dpl.ExecuteCommands;
import com.baidu.dueros.data.response.directive.dpl.event.UserEvent;
import com.baidu.dueros.model.Request;
import com.baidu.dueros.model.Response;
import com.baidu.dueros.model.ResponseEncapsulation;
import com.baidu.dueros.nlu.Intent;
import com.baidu.dueros.nlu.Slot;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * {@code BaseBot}是所有Bot的基类，使用Bot-SDK开发的Bot需要继承这个类
 * 
 * @author tianlonglong(tianlong02@baidu.com)
 * @version V1.1.1
 * @since v1.1.1
 */
public class BaseBot {

    // Base子类构造的Response
    protected Response response;
    // Bot收到的Request请求
    private final Request request;
    // 会话信息
    private Session session = new Session();
    // 是否需要结束本次会话
    private boolean shouldEndSession = false;
    // 如果DuerOS仍然会选用当前Bot结果，应该再次下发请求，并将request.determined字段设置为true
    private boolean needDetermine = false;
    // 麦克风开关是否打开
    private boolean expectSpeech = false;
    // 返回的指令
    private List<Directive> directives = new ArrayList<Directive>();
    // 通过NLU解析出来的Intent
    private Intent intent;

    // 是否打开参数验证，默认为false
    private boolean enableCertificate = false;
    // afterSearchScore
    private float afterSearchScore = 1.0f;
    private List<ExpectResponse> expectResponses = new ArrayList<>();
    // 认证签名
    private Certificate certificate;
    // 缓存认证相关信息
    private static ConcurrentHashMap<String, PublicKey> cache = new ConcurrentHashMap<>();
    // 数据统计信息
    public BotMonitor botMonitor;
    // 序列化字符串
    private String strRequest;

    /**
     * Base构造方法
     * 
     * @param request
     *            为封装后的RequestEncapsulation
     * @throws IOException
     *             抛出的异常
     */
    protected BaseBot(Request request) throws IOException {
        this.request = request;
        this.session.getAttributes().putAll(this.request.getSession().getAttributes());
        this.strRequest = request.toString();
    }

    /**
     * Base构造方法
     * 
     * @param request
     *            为Servlet的request
     * @throws IOException
     *             抛出异常
     * @throws JsonMappingException
     *             抛出异常
     */
    protected BaseBot(String request) throws IOException, JsonMappingException {
        ObjectMapper mapper = new ObjectMapper();
        this.request = mapper.readValue(request, Request.class);
        this.botMonitor = new BotMonitor(request);
        this.session.getAttributes().putAll(this.request.getSession().getAttributes());
        this.strRequest = request;
    }

    /**
     * BaseBot构造方法
     * 
     * @param certificate
     *            认证对象
     * @throws IOException
     *             可能抛出的IOException异常
     */
    protected BaseBot(Certificate certificate) throws IOException {
        this(certificate.getMessage());
        this.strRequest = certificate.getMessage();
    }

    /**
     * Base构造方法
     * 
     * @param request
     *            为Servlet的request
     * @throws IOException
     *             抛出异常
     * @throws JsonMappingException
     *             抛出异常
     */
    protected BaseBot(HttpServletRequest request) throws IOException, JsonMappingException {
        certificate = new Certificate(request);
        String message = certificate.getMessage();
        ObjectMapper mapper = new ObjectMapper();
        this.request = mapper.readValue(message, Request.class);
        this.botMonitor = new BotMonitor(message);
        this.session.getAttributes().putAll(this.request.getSession().getAttributes());
        this.strRequest = certificate.getMessage();
    }

    /**
     * 获取Request信息
     * 
     * @return Request Request信息
     */
    public Request getRequest() {
        return this.request;
    }

    /**
     * 获取session属性信息中某个字段的值
     * 
     * @param key
     *            属性信息的key
     * @return String 属性信息的值value
     */
    protected String getSessionAttribute(final String key) {
        return request.getSession().getAttributes().get(key);
    }

    /**
     * 设置session属性信息中某个字段的值
     * 
     * @param key
     *            属性信息的key
     * @param value
     *            属性信息的值value
     */
    protected void setSessionAttribute(final String key, final String value) {
        session.getAttributes().put(key, value);
    }

    /**
     * 清空session的属性信息
     */
    protected void clearSessionAttribute() {
        session.getAttributes().clear();
    }

    /**
     * 获取userID
     * 
     * @return String userid
     */
    protected String getUserId() {
        return request.getContext().getSystem().getUser().getUserId();
    }

    /**
     * 获取序列化字符串信息
     * 
     * @return String strRequest
     */
    public String getStrRequest() {
        return strRequest;
    }

    /**
     * 获取百度uid
     * 
     * @return String uid
     */
    protected String getBaiduUid() {
        return request.getContext().getSystem().getUser().getUserInfo().getAccount().getBaidu().getBaiduUid();
    }

    /**
     * 获取deviceID
     * 
     * @return String deviceID
     */
    protected String getDeviceId() {
        return request.getContext().getSystem().getDevice().getDeviceId();
    }

    /**
     * 获取设备apiAccessToken
     * 
     * @return String apiAccessToken
     */
    protected String getApiAccessToken() {
        return request.getContext().getSystem().getApiAccessToken();
    }

    /**
     * 获取设备SupportedInterfaces
     * 
     * @return SupportedInterfaces SupportedInterfaces
     */
    protected SupportedInterfaces getSupportedInterfaces() {
        return request.getContext().getSystem().getDevice().getSupportedInterfaces();
    }

    /**
     * 获取apiEndPoint
     * 
     * @return String apiEndPoint
     */
    protected String getApiEndPoint() {
        return request.getContext().getSystem().getApiEndPoint();
    }

    /**
     * 获取来自端上报的原始设备Id
     * 
     * @return String 原始设备Id
     */
    protected String getOriginalDeviceId() {
        return request.getContext().getSystem().getDevice().getOriginalDeviceId();
    }

    /**
     * 设置slot信息
     * 
     * @param slot
     *            需要设置的Slot
     */
    protected void setSlot(final Slot slot) {
        intent = getIntent();
        intent.setSlot(slot);
    }

    /**
     * 根据NLU返回的结果，获取槽位名slot对应的槽位值，默认获取第一个意图的槽位
     * 
     * @param slot
     *            槽位名称
     * @return String 槽位值
     */
    protected String getSlot(final String slot) {
        // 默认获取第一个意图
        Intent intent = getIntent();
        if (intent == null) {
            return null;
        }
        Map<String, Slot> slots = intent.getSlots();
        if (slots == null) {
            return null;
        }
        if (slots.get(slot) == null) {
            return null;
        }
        return slots.get(slot).getValue();
    }

    /**
     * 获取第index个意图的某个槽位值
     * 
     * @param slot
     *            槽位名称
     * @param index
     *            第几个意图
     * @return String 槽位值
     */
    protected String getSlot(final String slot, int index) {
        Intent intent = getIntent(index);
        if (intent == null) {
            return null;
        }
        Map<String, Slot> slots = intent.getSlots();
        if (slots == null) {
            return null;
        }
        if (slots.get(slot) == null) {
            return null;
        }
        return slots.get(slot).getValue();
    }

    /**
     * 询问某些槽位，如果询问一些槽位，表明多轮进行中
     * 
     * @param slot
     *            槽位名称
     */
    protected void ask(final String slot) {
        ElicitSlot elicitSlot = new ElicitSlot();
        elicitSlot.setSlotToElicit(slot);
        elicitSlot.setUpdatedIntent(getIntent());
        addDirective(elicitSlot);
    }

    /**
     * 设置delegate某个槽位或确认意图
     */
    protected void setDelegate() {
        Delegate delegate = new Delegate();
        delegate.setUpdatedIntent(getIntent());
        addDirective(delegate);
    }

    /**
     * 设置对一个槽位的确认
     * 
     * @param slot
     *            槽位名称
     */
    protected void setConfirmSlot(final String slot) {
        ConfirmSlot confirmSlot = new ConfirmSlot();
        confirmSlot.setSlotToConfirm(slot);
        confirmSlot.setUpdatedIntent(getIntent());
        addDirective(confirmSlot);
    }

    /**
     * 设置对一个意图的确认
     */
    protected void setConfirmIntent() {
        ConfirmIntent confirmIntent = new ConfirmIntent();
        confirmIntent.setUpdatedIntent(getIntent());
        addDirective(confirmIntent);
    }

    /**
     * 获取当前的一个intent名称，默认获取第一个Intent
     * 
     * @return Intent
     */
    protected Intent getIntent() {
        return getIntent(0);
    }

    /**
     * 获取第index个Intent
     * 
     * @param index
     *            Intent坐标
     * @return Intent
     */
    protected Intent getIntent(int index) {
        RequestBody requestBody = request.getRequest();
        IntentRequest intentRequest = null;
        Intent intent = null;
        if (requestBody instanceof IntentRequest) {
            intentRequest = (IntentRequest) requestBody;
            // 获取第index个Intent
            intent = intentRequest.getIntents().get(index);
        }
        return intent;
    }

    /**
     * 获取accessToken
     * 
     * @return String accessToken
     */
    protected String getAccessToken() {
        return request.getContext().getSystem().getUser().getAccessToken();
    }

    /**
     * 告诉DuerOS，在多轮对话中，等待用户的回答
     */
    protected void waitAnswer() {
        shouldEndSession = false;
    }

    /**
     * 告诉DuerOS，需要结束会话
     */
    protected void endDialog() {
        shouldEndSession = true;
    }

    /**
     * 获取needDetermine的getter方法
     *
     * @return needDetermine 是否会发生副作用
     */
    public boolean effectConfirmed() {
        return needDetermine;
    }

    /**
     * 设置needDetermine的setter方法
     */
    public void declareEffect() {
        this.needDetermine = true;
    }

    /**
     * 通过控制expectSpeech来控制麦克风开关
     * 
     * @param expectSpeech
     *            麦克风是否开启
     */
    public void setExpectSpeech(boolean expectSpeech) {
        this.expectSpeech = expectSpeech;
    }

    /**
     * 获取原始Query信息
     * 
     * @return String 原始Query信息
     */
    protected String getQuery() {
        // 只有IntentRequest才有Query信息
        if (isIntentRequest() == true) {
            IntentRequest intentRequest = (IntentRequest) request.getRequest();
            Query query = intentRequest.getQuery();
            return query.getOriginal();
        }
        return null;
    }

    /**
     * 设置afterSearchScore
     *
     * @param afterSearchScore
     *            afterSearchScore
     */
    protected void setAfterSearchScore(final float afterSearchScore) {
        this.afterSearchScore = afterSearchScore;
    }

    /**
     * 新增text类型的ExpectResponse
     *
     * @param text
     *            text
     */
    protected void addExpectTextResponse(String text) {
        expectResponses.add(new ExpectResponse(ExpectResponseType.Text, text));
    }

    /**
     * 新增slot类型的ExpectResponse
     *
     * @param slot
     *            slot
     */
    protected void addExpectSlotResponse(String slot) {
        expectResponses.add(new ExpectResponse(ExpectResponseType.Slot, slot));
    }

    /**
     * 判断是否为IntentRequest
     * 
     * @return boolean 判断是否为IntentRequest
     */
    private boolean isIntentRequest() {
        RequestBody requestBody = request.getRequest();
        return (requestBody instanceof IntentRequest);
    }

    /**
     * 生成一个token
     * 
     * @return String 生成一个Token
     */
    protected String genToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * 向Response中添加一个Directive指令
     * 
     * @param directive
     *            需要用户构造的指令，为Directive的子类
     */
    protected void addDirective(Directive directive) {
        if (directive instanceof ExecuteCommands) {
            ((ExecuteCommands) directive).setToken(request.getContext().getScreen().getToken());
        }
        directives.add(directive);
    }

    /**
     * 开启验证请求参数
     * 
     */
    public void enableVerify() {
        enableCertificate = true;
    }

    /**
     * 关闭验证请求参数
     * 
     */
    public void disableVerify() {
        enableCertificate = false;
    }

    /**
     * 设置Certificate对象
     * 
     * @param certificate
     *            认证签名
     */
    public void setCertificate(Certificate certificate) {
        this.certificate = certificate;
    }

    /**
     * 签名验证
     * 
     * @return boolean 签名验证是否正确
     */
    private boolean verify() {
        // 首先检查是否打开请求参数验证
        if (enableCertificate == false) {
            return true;
        }
        String signaturecerturl = certificate.getSignaturecerturl();
        if (signaturecerturl == null) {
            return false;
        }
        if (!cache.containsKey(signaturecerturl)) {
            PublicKey publicKey = Certificate.getPublicKeyFromUrl(signaturecerturl);
            if (publicKey == null) {
                return false;
            }
            cache.put(signaturecerturl, publicKey);
        }
        return Certificate.verify(cache, certificate);
    }

    /**
     * 若请求不合法，则返回该Response
     * 
     * @return String 不合法返回的JSON数据
     */
    private String illegalRequest() {
        return "{\"status\":1,\"msg\":\"invalid request\"}";
    }

    /**
     * 在Bot没有返回的情况下，默认返回
     * 
     * @return String 默认返回
     * @throws JsonProcessingException
     */
    public String defaultResponse() throws JsonProcessingException {

        ResponseBody responseBody = new ResponseBody();
        // 默认shouldEndSessions设置为false
        responseBody.setShouldEndSession(false);

        ResponseEncapsulation response = new ResponseEncapsulation(responseBody);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Include.NON_NULL);
        String str = objectMapper.writeValueAsString(response);
        return str;
    }

    /**
     * 根据Bot返回的Response转换为ResponseEncapsulation，然后序列化
     * 
     * @param response
     *            Bot返回的Response
     * @throws JsonProcessingException
     *             抛出异常
     * @return String 封装后的ResponseEncapsulation的序列化内容
     */
    protected String build(Response response) throws JsonProcessingException {
        if (response == null) {
            return defaultResponse();
        }
        Context context = new Context();
        if (isIntentRequest() == true) {
            context.setIntent(getIntent());
            context.setAfterSearchScore(afterSearchScore);
            context.setExpectResponses(expectResponses);
        }

        ResponseBody responseBody = new ResponseBody();
        responseBody.setDirectives(directives);
        if (response.getCard() != null) {
            responseBody.setCard(response.getCard());
        }
        if (response.getOutputSpeech() != null) {
            responseBody.setOutputSpeech(response.getOutputSpeech());
        }
        if (response.getReprompt() != null) {
            responseBody.setReprompt(response.getReprompt());
        }
        responseBody.setShouldEndSession(shouldEndSession);
        responseBody.setNeedDetermine(needDetermine);
        responseBody.setExpectSpeech(expectSpeech);
        if (response.getResource() != null) {
            responseBody.setResource(response.getResource());
        }

        ResponseEncapsulation responseEncapsulation = new ResponseEncapsulation(context, session, responseBody);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(Include.NON_NULL);
        return mapper.writeValueAsString(responseEncapsulation);
    }

    /**
     * 事件调度
     * 
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     * 
     * @throws Exception
     *             抛出异常
     */
    private void dispatch() throws JsonParseException, JsonMappingException, IOException {
        RequestBody requestBody = request.getRequest();
        if (requestBody instanceof LaunchRequest) {
            LaunchRequest launchRequest = (LaunchRequest) requestBody;
            response = onLaunch(launchRequest);
        } else if (requestBody instanceof IntentRequest) {
            IntentRequest intentRequest = (IntentRequest) requestBody;
            response = onInent(intentRequest);
        } else if (requestBody instanceof SessionEndedRequest) {
            SessionEndedRequest sessionEndedRequest = (SessionEndedRequest) requestBody;
            response = onSessionEnded(sessionEndedRequest);
        } else if (requestBody instanceof NextRequired) {
            NextRequired nextRequired = (NextRequired) requestBody;
            response = onNextRequiredEvent(nextRequired);
        } else if (requestBody instanceof AudioPlayerEvent) {
            // AudioPlayer audioPlayer = (AudioPlayer) this;
            if (requestBody instanceof com.baidu.dueros.data.request.audioplayer.event.PlaybackNearlyFinishedEvent) {
                com.baidu.dueros.data.request.audioplayer.event.PlaybackNearlyFinishedEvent playbackNearlyFinishedEvent;
                playbackNearlyFinishedEvent = (com.baidu.dueros.data.request.audioplayer.event.PlaybackNearlyFinishedEvent) requestBody;
                response = this.onPlaybackNearlyFinishedEvent(playbackNearlyFinishedEvent);
            } else if (requestBody instanceof com.baidu.dueros.data.request.audioplayer.event.PlaybackStartedEvent) {
                com.baidu.dueros.data.request.audioplayer.event.PlaybackStartedEvent playbackStartedEvent;
                playbackStartedEvent = (com.baidu.dueros.data.request.audioplayer.event.PlaybackStartedEvent) requestBody;
                response = this.onPlaybackStartedEvent(playbackStartedEvent);
            } else if (requestBody instanceof com.baidu.dueros.data.request.audioplayer.event.PlaybackStoppedEvent) {
                com.baidu.dueros.data.request.audioplayer.event.PlaybackStoppedEvent playbackStoppedEvent;
                playbackStoppedEvent = (com.baidu.dueros.data.request.audioplayer.event.PlaybackStoppedEvent) requestBody;
                response = this.onPlaybackStoppedEvent(playbackStoppedEvent);
            } else if (requestBody instanceof com.baidu.dueros.data.request.audioplayer.event.PlaybackFinishedEvent) {
                com.baidu.dueros.data.request.audioplayer.event.PlaybackFinishedEvent playbackFinishedEvent;
                playbackFinishedEvent = (com.baidu.dueros.data.request.audioplayer.event.PlaybackFinishedEvent) requestBody;
                response = this.onPlaybackFinishedEvent(playbackFinishedEvent);
            } else if (requestBody instanceof com.baidu.dueros.data.request.audioplayer.event.PlaybackPausedEvent) {
                com.baidu.dueros.data.request.audioplayer.event.PlaybackPausedEvent playbackPausedEvent;
                playbackPausedEvent = (com.baidu.dueros.data.request.audioplayer.event.PlaybackPausedEvent) requestBody;
                response = this.onPlaybackPausedEvent(playbackPausedEvent);
            } else if (requestBody instanceof com.baidu.dueros.data.request.audioplayer.event.PlaybackResumedEvent) {
                com.baidu.dueros.data.request.audioplayer.event.PlaybackResumedEvent playbackResumedEvent;
                playbackResumedEvent = (com.baidu.dueros.data.request.audioplayer.event.PlaybackResumedEvent) requestBody;
                response = this.onPlaybackResumedEvent(playbackResumedEvent);
            } else if (requestBody instanceof com.baidu.dueros.data.request.audioplayer.event.PlaybackStutterFinishedEvent) {
                com.baidu.dueros.data.request.audioplayer.event.PlaybackStutterFinishedEvent playbackStutterFinishedEvent;
                playbackStutterFinishedEvent = (com.baidu.dueros.data.request.audioplayer.event.PlaybackStutterFinishedEvent) requestBody;
                response = this.onPlaybackStutterFinishedEvent(playbackStutterFinishedEvent);
            } else if (requestBody instanceof com.baidu.dueros.data.request.audioplayer.event.PlaybackStutterStartedEvent) {
                com.baidu.dueros.data.request.audioplayer.event.PlaybackStutterStartedEvent playbackStutterStartedEvent;
                playbackStutterStartedEvent = (com.baidu.dueros.data.request.audioplayer.event.PlaybackStutterStartedEvent) requestBody;
                response = this.onPlaybackStutterStartedEvent(playbackStutterStartedEvent);
            } else if (requestBody instanceof com.baidu.dueros.data.request.audioplayer.event.ProgressReportDelayElapsedEvent) {
                com.baidu.dueros.data.request.audioplayer.event.ProgressReportDelayElapsedEvent progressReportDelayElapsedEvent;
                progressReportDelayElapsedEvent = (com.baidu.dueros.data.request.audioplayer.event.ProgressReportDelayElapsedEvent) requestBody;
                response = this.onProgressReportDelayElapsedEvent(progressReportDelayElapsedEvent);
            } else if (requestBody instanceof com.baidu.dueros.data.request.audioplayer.event.ProgressReportIntervalElapsedEvent) {
                com.baidu.dueros.data.request.audioplayer.event.ProgressReportIntervalElapsedEvent progressReportIntervalElapsedEvent;
                progressReportIntervalElapsedEvent = (com.baidu.dueros.data.request.audioplayer.event.ProgressReportIntervalElapsedEvent) requestBody;
                response = this.onProgressReportIntervalElapsedEvent(progressReportIntervalElapsedEvent);
            }
        } else if (requestBody instanceof VideoPlayerEvent) {
            // VideoPlayer videoPlayer = (VideoPlayer) this;
            if (requestBody instanceof PlaybackStartedEvent) {
                PlaybackStartedEvent playbackStartedEvent = (PlaybackStartedEvent) requestBody;
                response = this.onPlaybackStartedEvent(playbackStartedEvent);
            } else if (requestBody instanceof PlaybackStoppedEvent) {
                PlaybackStoppedEvent playbackStoppedEvent = (PlaybackStoppedEvent) requestBody;
                response = this.onPlaybackStoppedEvent(playbackStoppedEvent);
            } else if (requestBody instanceof PlaybackFinishedEvent) {
                PlaybackFinishedEvent playbackFinishedEvent = (PlaybackFinishedEvent) requestBody;
                response = this.onPlaybackFinishedEvent(playbackFinishedEvent);
            } else if (requestBody instanceof PlaybackNearlyFinishedEvent) {
                PlaybackNearlyFinishedEvent playbackNearlyFinishedEvent = (PlaybackNearlyFinishedEvent) requestBody;
                response = this.onPlaybackNearlyFinishedEvent(playbackNearlyFinishedEvent);
            } else if (requestBody instanceof ProgressReportIntervalElapsedEvent) {
                ProgressReportIntervalElapsedEvent progressReportIntervalElapsedEvent = (ProgressReportIntervalElapsedEvent) requestBody;
                response = this.onProgressReportIntervalElapsedEvent(progressReportIntervalElapsedEvent);
            } else if (requestBody instanceof ProgressReportDelayElapsedEvent) {
                ProgressReportDelayElapsedEvent progressReportDelayElapsedEvent = (ProgressReportDelayElapsedEvent) requestBody;
                response = this.onProgressReportDelayElapsedEvent(progressReportDelayElapsedEvent);
            } else if (requestBody instanceof PlaybackStutterStartedEvent) {
                PlaybackStutterStartedEvent playbackStutterStartedEvent = (PlaybackStutterStartedEvent) requestBody;
                response = this.onPlaybackStutterStartedEvent(playbackStutterStartedEvent);
            } else if (requestBody instanceof PlaybackStutterFinishedEvent) {
                PlaybackStutterFinishedEvent playbackStutterFinishedEvent = (PlaybackStutterFinishedEvent) requestBody;
                response = this.onPlaybackStutterFinishedEvent(playbackStutterFinishedEvent);
            } else if (requestBody instanceof PlaybackPausedEvent) {
                PlaybackPausedEvent playbackPausedEvent = (PlaybackPausedEvent) requestBody;
                response = this.onPlaybackPausedEvent(playbackPausedEvent);
            } else if (requestBody instanceof PlaybackResumedEvent) {
                PlaybackResumedEvent playbackResumedEvent = (PlaybackResumedEvent) requestBody;
                response = this.onPlaybackResumedEvent(playbackResumedEvent);
            } else if (requestBody instanceof PlaybackQueueClearedEvent) {
                PlaybackQueueClearedEvent playbackQueueClearedEvent = (PlaybackQueueClearedEvent) requestBody;
                response = this.onPlaybackQueueClearedEvent(playbackQueueClearedEvent);
            }
        } else if (requestBody instanceof ConnectionsResponseEvent) {
            ConnectionsResponseEvent connectionsResponseEvent = (ConnectionsResponseEvent) requestBody;
            if ("Charge".equals(connectionsResponseEvent.getName())) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode tree = objectMapper.readTree(strRequest);
                List<JsonNode> code = tree.findValues("request");
                if (code.size() == 1 && code.get(0) != null) {
                    String strRequest = code.get(0).toString();
                    ChargeEvent chargeEvent = objectMapper.readValue(strRequest, ChargeEvent.class);
                    response = this.onChargeEvent(chargeEvent);
                }
            } else if ("LinkAccountSucceeded".equals(connectionsResponseEvent.getName())) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode tree = objectMapper.readTree(strRequest);
                List<JsonNode> code = tree.findValues("request");
                if (code.size() == 1 && code.get(0) != null) {
                    String strRequest = code.get(0).toString();
                    LinkAccountSucceededEvent linkAccountSucceededEvent = objectMapper.readValue(strRequest,
                            LinkAccountSucceededEvent.class);
                    response = this.onLinkAccountSucceededEvent(linkAccountSucceededEvent);
                }
            }
        } else if (requestBody instanceof CommonEvent) {
            if (requestBody instanceof ElementSelectedEvent) {
                ElementSelectedEvent elementSelectedEvent = (ElementSelectedEvent) requestBody;
                response = this.onElementSelectedEvent(elementSelectedEvent);
            } else if (requestBody instanceof LinkClickedEvent) {
                LinkClickedEvent linkClickedEvent = (LinkClickedEvent) requestBody;
                response = this.onLinkClickedEvent(linkClickedEvent);
            } else if (requestBody instanceof PermissionGrantFailedEvent) {
                PermissionGrantFailedEvent permissionGrantFailedEvent = (PermissionGrantFailedEvent) requestBody;
                response = this.onPermissionGrantFailedEvent(permissionGrantFailedEvent);
            } else if (requestBody instanceof PermissionRejectedEvent) {
                PermissionRejectedEvent permissionRejectedEvent = (PermissionRejectedEvent) requestBody;
                response = this.onPermissionRejectedEvent(permissionRejectedEvent);
            } else if (requestBody instanceof PermissionGrantedEvent) {
                PermissionGrantedEvent permissionGrantedEvent = (PermissionGrantedEvent) requestBody;
                response = this.onPermissionGrantedEvent(permissionGrantedEvent);
            } else if (requestBody instanceof PermissionRequiredEvent) {
                PermissionRequiredEvent permissionRequiredEvent = (PermissionRequiredEvent) requestBody;
                response = this.onPermissionRequiredEvent(permissionRequiredEvent);
            } else if (requestBody instanceof ButtonClickedEvent) {
                ButtonClickedEvent buttonClickedEvent = (ButtonClickedEvent) requestBody;
                response = this.onButtonClickedEvent(buttonClickedEvent);
            } else if (requestBody instanceof RadioButtonClickedEvent) {
                RadioButtonClickedEvent radioButtonClickedEvent = (RadioButtonClickedEvent) requestBody;
                response = this.onRadioButtonClicked(radioButtonClickedEvent);
            } else if (requestBody instanceof UserEvent) {
                UserEvent userEvent = (UserEvent) requestBody;
                response = this.onUserEvent(userEvent);
            } else if (requestBody instanceof BuyEvent) {
                BuyEvent buyEvent = (BuyEvent) requestBody;
                response = this.onBuyEvent(buyEvent);
            }
        }
        if (response == null) {
            response = this.onDefaultEvent();
        }
    }

    /**
     * 处理IntentRequest请求
     * 
     * @param intentRequest
     *            收到的IntentRequest请求
     * @return Response 返回Response
     */
    protected Response onInent(final IntentRequest intentRequest) {
        return response;
    }

    /**
     * 处理LaunchRequest请求
     * 
     * @param launchRequest
     *            收到的LaunchRequest请求
     * @return Response 返回Response
     */
    protected Response onLaunch(final LaunchRequest launchRequest) {
        return response;
    }

    /**
     * 处理SessionEndedRequest请求
     * 
     * @param sessionEndedRequest
     *            收到的SessionEndedRequest请求
     * @return Response 返回Response
     */
    protected Response onSessionEnded(final SessionEndedRequest sessionEndedRequest) {
        return response;
    }

    /**
     * 处理列表模版项点击事件 对应的事件是：Display.ElementSelected 事件
     * 
     * @param elementSelectedEvent
     *            模板列表项点击事件
     * @return Response 返回的Response
     */
    protected Response onElementSelectedEvent(final ElementSelectedEvent elementSelectedEvent) {
        return response;
    }

    /**
     * 处理卡片列表项点击事件 对应的事件是：Screen.LinkClicked
     * 
     * @param linkClickedEvent
     *            卡片列表项点击事件
     * @return Response 返回的Response
     */
    protected Response onLinkClickedEvent(final LinkClickedEvent linkClickedEvent) {
        return response;
    }

    /**
     * 用户点击Button后，技能会收到Form.ButtonClicked事
     * 
     * @param buttonClickedEvent
     *            用户点击Button事件
     * @return Response 返回的Response
     */
    protected Response onButtonClickedEvent(final ButtonClickedEvent buttonClickedEvent) {
        return response;
    }

    /**
     * 用户点击控制组件的button后，技能会收到Form.radioButtonClickedEvent事
     * 
     * @param radioButtonClickedEvent
     *            用户点击Button事件
     * @return Response 返回的Response
     */
    protected Response onRadioButtonClicked(final RadioButtonClickedEvent radioButtonClickedEvent) {
        return response;
    }

    /**
     * userEvent事件
     *
     * @param userEvent
     *            userEvent
     * @return Response 返回的Response
     */
    protected Response onUserEvent(final UserEvent userEvent) {
        return response;
    }

    /**
     * buyEvent事件
     *
     * @param buyEvent
     *            buyEvent
     * @return Response 返回的Response
     */
    protected Response onBuyEvent(final BuyEvent buyEvent) {
        return response;
    }

    /**
     * 处理表示用户拒绝授权事件 对应的事件是：Permission.GrantFailed
     *
     * @param permissionGrantFailedEvent
     *            表示用户拒绝授权
     * @return Response 返回的Response
     */
    protected Response onPermissionGrantFailedEvent(final PermissionGrantFailedEvent permissionGrantFailedEvent) {
        return response;
    }

    /**
     * 处理表表示用户同意授权，但是由于其他原因导致授权失败事件 对应的事件是：Permission.Rejected
     *
     * @param permissionRejectedEvent
     *            表示用户同意授权，但是由于其他原因导致授权失败
     * @return Response 返回的Response
     */
    protected Response onPermissionRejectedEvent(final PermissionRejectedEvent permissionRejectedEvent) {
        return response;
    }

    /**
     * 处理表示用户同意授权事件 对应的事件是：Permission.Granted
     *
     * @param permissionGrantedEvent
     *            表示用户同意授权
     * @return Response 返回的Response
     */
    protected Response onPermissionGrantedEvent(final PermissionGrantedEvent permissionGrantedEvent) {
        return response;
    }

    /**
     * 当技能返回了录音等需要权限才可以使用的指令时，但是没有录音等权限时会收到该事件，需要技能返回Permission.
     * AskForPermissionsConsent指令申请用户录音等权限。
     *
     * @param permissionRequiredEvent
     *            表示需要用户授权
     * @return Response 返回的Response
     */
    protected Response onPermissionRequiredEvent(final PermissionRequiredEvent permissionRequiredEvent) {
        return response;
    }

    /**
     * 当用户授权完成之后，技能会收到此事件
     *
     * @param linkAccountSucceededEvent
     *            授权成功事件
     * @return Response 返回的Response
     */
    protected Response onLinkAccountSucceededEvent(final LinkAccountSucceededEvent linkAccountSucceededEvent) {
        return response;
    }

    /**
     * 处理支付事件
     *
     * @param chargeEvent
     *            支付事件
     * @return Response 返回的Response
     */
    protected Response onChargeEvent(final ChargeEvent chargeEvent) {
        return response;
    }

    /**
     * 处理PlaybackStartedEvent事件
     *
     * @param playbackStartedEvent
     *            PlaybackStartedEvent事件
     * @return Response 返回的Response
     */
    protected Response onPlaybackStartedEvent(
            final com.baidu.dueros.data.request.audioplayer.event.PlaybackStartedEvent playbackStartedEvent) {
        return response;
    }

    /**
     * 处理PlaybackStoppedEvent事件
     *
     * @param playbackStoppedEvent
     *            PlaybackStoppedEvent事件
     * @return Response 返回的Response
     */
    protected Response onPlaybackStoppedEvent(
            final com.baidu.dueros.data.request.audioplayer.event.PlaybackStoppedEvent playbackStoppedEvent) {
        return response;
    }

    /**
     * 处理onPlaybackNearlyFinishedEvent事件
     *
     * @param playbackNearlyFinishedEvent
     *            PlaybackNearlyFinishedEvent事件
     * @return Response 返回的Response
     */
    protected Response onPlaybackNearlyFinishedEvent(
            final com.baidu.dueros.data.request.audioplayer.event.PlaybackNearlyFinishedEvent playbackNearlyFinishedEvent) {
        return response;
    }

    /**
     * 处理PlaybackFinishedEvent事件
     *
     * @param playbackFinishedEvent
     *            PlaybackFinishedEvent事件
     * @return Response 返回的Response
     */
    protected Response onPlaybackFinishedEvent(
            final com.baidu.dueros.data.request.audioplayer.event.PlaybackFinishedEvent playbackFinishedEvent) {
        return response;
    }

    /**
     * 处理ProgressReportIntervalElapsedEvent事件
     *
     * @param progressReportIntervalElapsedEvent
     *            ProgressReportIntervalElapsedEvent事件
     * @return Response 返回的Response
     */
    protected Response onProgressReportIntervalElapsedEvent(
            final com.baidu.dueros.data.request.audioplayer.event.ProgressReportIntervalElapsedEvent progressReportIntervalElapsedEvent) {
        return response;
    }

    /**
     * 处理ProgressReportDelayElapsedEvent事件
     *
     * @param progressReportDelayElapsedEvent
     *            ProgressReportDelayElapsedEvent事件
     * @return Response 返回的Response
     */
    protected Response onProgressReportDelayElapsedEvent(
            final com.baidu.dueros.data.request.audioplayer.event.ProgressReportDelayElapsedEvent progressReportDelayElapsedEvent) {
        return response;
    }

    /**
     * 处理PlaybackStutterStartedEvent事件
     *
     * @param playbackStutterStartedEvent
     *            PlaybackStutterStartedEvent事件
     * @return Response 返回的Response
     */
    protected Response onPlaybackStutterStartedEvent(
            final com.baidu.dueros.data.request.audioplayer.event.PlaybackStutterStartedEvent playbackStutterStartedEvent) {
        return response;
    }

    /**
     * 处理PlaybackStutterFinishedEvent事件
     *
     * @param playbackStutterFinishedEvent
     *            PlaybackStutterFinishedEvent事件
     * @return Response 返回的Response
     */
    protected Response onPlaybackStutterFinishedEvent(
            final com.baidu.dueros.data.request.audioplayer.event.PlaybackStutterFinishedEvent playbackStutterFinishedEvent) {
        return response;
    }

    /**
     * 处理PlaybackPausedEvent事件
     *
     * @param playbackPausedEvent
     *            PlaybackPausedEvent事件
     * @return Response 返回的Response
     */
    protected Response onPlaybackPausedEvent(
            final com.baidu.dueros.data.request.audioplayer.event.PlaybackPausedEvent playbackPausedEvent) {
        return response;
    }

    /**
     * 处理PlaybackResumedEvent事件
     *
     * @param playbackResumedEvent
     *            PlaybackResumedEvent事件
     * @return Response 返回的Response
     */
    protected Response onPlaybackResumedEvent(
            final com.baidu.dueros.data.request.audioplayer.event.PlaybackResumedEvent playbackResumedEvent) {
        return response;
    }

    /**
     * 处理PlaybackQueueClearedEvent事件
     *
     * @param playbackQueueClearedEvent
     *            PlaybackQueueClearedEvent事件
     * @return Response 返回的Response
     */
    protected Response onPlaybackQueueClearedEvent(final PlaybackQueueClearedEvent playbackQueueClearedEvent) {
        return response;
    }

    /**
     * 处理PlaybackStartedEvent事件
     *
     * @param playbackStartedEvent
     *            PlaybackStartedEvent事件
     * @return Response 返回的Response
     */
    protected Response onPlaybackStartedEvent(final PlaybackStartedEvent playbackStartedEvent) {
        return response;
    }

    /**
     * 处理PlaybackStoppedEvent事件
     *
     * @param playbackStoppedEvent
     *            PlaybackStoppedEvent事件
     * @return Response 返回的Response
     */
    protected Response onPlaybackStoppedEvent(final PlaybackStoppedEvent playbackStoppedEvent) {
        return response;
    }

    /**
     * 处理PlaybackNearlyFinishedEvent事件
     *
     * @param playbackNearlyFinishedEvent
     *            PlaybackNearlyFinishedEvent事件
     * @return Response 返回的Response
     */
    protected Response onPlaybackNearlyFinishedEvent(final PlaybackNearlyFinishedEvent playbackNearlyFinishedEvent) {
        return response;
    }

    /**
     * 处理PlaybackFinishedEvent事件
     *
     * @param playbackFinishedEvent
     *            PlaybackFinishedEvent事件
     * @return Response 返回的Response
     */
    protected Response onPlaybackFinishedEvent(final PlaybackFinishedEvent playbackFinishedEvent) {
        return response;
    }

    /**
     * 处理PlaybackPausedEvent事件
     *
     * @param playbackPausedEvent
     *            PlaybackPausedEvent事件
     * @return Response 返回的Response
     */
    protected Response onPlaybackPausedEvent(final PlaybackPausedEvent playbackPausedEvent) {
        return response;
    }

    /**
     * 处理PlaybackResumedEvent事件
     *
     * @param playbackResumedEvent
     *            PlaybackResumedEvent事件
     * @return Response 返回的Response
     */
    protected Response onPlaybackResumedEvent(final PlaybackResumedEvent playbackResumedEvent) {
        return response;
    }

    /**
     * 处理PlaybackStutterFinishedEvent事件
     *
     * @param playbackStutterFinishedEvent
     *            PlaybackStutterFinishedEvent事件
     * @return Response 返回的Response
     */
    protected Response onPlaybackStutterFinishedEvent(final PlaybackStutterFinishedEvent playbackStutterFinishedEvent) {
        return response;
    }

    /**
     * 处理onPlaybackStutterStartedEvent事件
     *
     * @param playbackStutterStartedEvent
     *            playbackStutterStartedEvent事件
     * @return Response 返回的Response
     */
    protected Response onPlaybackStutterStartedEvent(final PlaybackStutterStartedEvent playbackStutterStartedEvent) {
        return response;
    }

    /**
     * 处理ProgressReportDelayElapsedEvent事件
     *
     * @param progressReportDelayElapsedEvent
     *            progressReportDelayElapsedEvent事件
     * @return Response 返回的Response
     */
    protected Response onProgressReportDelayElapsedEvent(
            final ProgressReportDelayElapsedEvent progressReportDelayElapsedEvent) {
        return response;
    }

    /**
     * 处理ProgressReportIntervalElapsedEvent事件
     *
     * @param progressReportIntervalElapsedEvent
     *            ProgressReportIntervalElapsedEvent事件
     * @return Response 返回的Response
     */
    protected Response onProgressReportIntervalElapsedEvent(
            final ProgressReportIntervalElapsedEvent progressReportIntervalElapsedEvent) {
        return response;
    }

    /**
     * 默认事件处理
     *
     * @since V1.1.1
     * @return Response 返回的Response
     */
    protected Response onDefaultEvent() {
        return response;
    }

    protected Response onNextRequiredEvent(NextRequired nextRequired) {
        return response;
    }

    /**
     * Bot子类调用
     * 
     * @return String 返回的response信息
     * @throws Exception
     *             抛出的异常
     */
    public String run() throws Exception {
        // 请求参数不合法
        if (verify() == false) {
            return this.illegalRequest();
        }

        this.dispatch();
        String responseStr = this.build(response);
        this.botMonitor.setResponse(responseStr);
        this.botMonitor.uploadData();
        return responseStr;
    }
}
