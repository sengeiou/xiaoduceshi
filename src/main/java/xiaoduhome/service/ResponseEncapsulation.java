package xiaoduhome.service;

import ai.qiwu.com.xiaoduhome.common.Constants;
import ai.qiwu.com.xiaoduhome.common.Constants.EndMsg;
import ai.qiwu.com.xiaoduhome.common.Constants.WelcomeWord;
import ai.qiwu.com.xiaoduhome.common.Constants.ErrorMsg;
import ai.qiwu.com.xiaoduhome.common.OkHttp3Utils;
import ai.qiwu.com.xiaoduhome.common.Utils;
import ai.qiwu.com.xiaoduhome.pojo.GetUserInfoByToken;
import ai.qiwu.com.xiaoduhome.pojo.QiWuResponse;
import com.alibaba.fastjson.JSON;
import com.baidu.dueros.bot.AudioPlayer;
import com.baidu.dueros.data.request.*;
import com.baidu.dueros.data.request.audioplayer.event.PlaybackFinishedEvent;
import com.baidu.dueros.data.request.audioplayer.event.PlaybackStoppedEvent;
import com.baidu.dueros.data.request.buy.event.BuyEvent;
import com.baidu.dueros.data.request.events.LinkAccountSucceededEvent;
import com.baidu.dueros.data.request.pay.event.ChargeEvent;
import com.baidu.dueros.data.request.pay.event.Payload;
import com.baidu.dueros.data.response.OutputSpeech;
import com.baidu.dueros.data.response.Reprompt;
import com.baidu.dueros.data.response.card.Card;
import com.baidu.dueros.data.response.card.LinkAccountCard;
import com.baidu.dueros.data.response.directive.audioplayer.AudioPlayerDirective;
import com.baidu.dueros.data.response.directive.audioplayer.Play;
import com.baidu.dueros.data.response.directive.display.Hint;
import com.baidu.dueros.data.response.directive.display.RenderTemplate;
import com.baidu.dueros.data.response.directive.display.templates.*;
import com.baidu.dueros.data.response.directive.display.templates.BodyTemplate1.PositionType;
import com.baidu.dueros.data.response.directive.pay.Buy;
import com.baidu.dueros.data.response.directive.pay.Charge;
import com.baidu.dueros.model.Response;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

/**
 * @author 苗权威
 * @dateTime 19-6-28 下午8:22
 */
@Slf4j
public class ResponseEncapsulation extends AudioPlayer {
    private static final Boolean USE_CHARGE_ORDER = false;

    public ResponseEncapsulation(HttpServletRequest request) throws IOException {
        super(request);
        String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBANMWIYduiQZt0B7s" +
                "IZ6PW15gpcO7Yn+9ZbaMSHwzJqNeDAZzh6HXF7r0ZpQcw3XEWKPAPWyuJ5eqZf46" +
                "W/NyJQkcddWrA/5OvcZw1uAmzTt8sWz1zokt2e6v6SucvvHoPxLGjfTKWh6Qr+2x" +
                "K/5wmdOOpikL/ruH2ZviYjto2cBPAgMBAAECgYBuQCLnD8618uk/HWo53PqGAsjR" +
                "wK+jtJLJk9/QLw9BSL/TJshyuVuFpF1ngtZ8Tj9V1/S9LQE08CUxcd3Q+49njgEq" +
                "R60FKDjwCe8G9El5jIYIU0RTnd84I8B0RKWBO8ShZ29qWEVXOExPZc1fMlq4UgBr" +
                "Xkfsh/E1cTfphYyS8QJBAOuX1c11XOSeDVo5KyxM26K2Kiekjm1cLIXJR54hetRN" +
                "diCHqd5WyolKJzxFuUMEsq+sAfgd7KsrorO5hwY42lUCQQDlXt9TiIWGMt3oVEIf" +
                "ulJQmURhkQ+Z9EC2wHnFrE3UhkCNyX3TOwTzV8bzC+Vr2hAobbhXTCr8tLGm0Yxk" +
                "f1wTAkEAqbQtplosF+Jh6+PSXY7fh02BAB1hGxWSXKyokhe7ysIhnT0b97S9IDfy" +
                "G1B+KvBvZmuY34luub4s7RlvUeQSIQJANaq7Cip5Q2sHbOK6Df5kYCNcUo/EXLs/" +
                "oQLr+wpTs5Qt6n7oh9HZWK6DCD8SUOfWu/7gENzrefE1V9jTxnfeLQJBANKf4QfA" +
                "CvwfHTyEgphcWrWDzB7bEqUDWiEjfO8sf+lwj9I0yU6fO/+aUtwU9xwkij1PscUF" +
                "9AWUhbas+LG9mds=";
        //privateKey为私钥内容,0代表你的Bot在DBP平台debug环境，1或者其他整数代表online环境,
        // botMonitor对象已经在bot-sdk里初始化，可以直接调用
        //this.botMonitor.setEnvironmentInfo(privateKey, 0);
        //this.botMonitor.setMonitorEnabled(true);
    }

    @Override
    protected Response onChargeEvent(ChargeEvent chargeEvent) {
        Payload payload = chargeEvent.getPayload();
        if (payload.getPurchaseResult().equals("SUCCESS")) {
            log.info("支付成功,"+payload.getMessage());
            log.info("百度扣款金额："+chargeEvent.getPayload().getAuthorizationDetails().getCapturedAmount().getAmount());
            log.info("本次交易百度生成的订单ID:"+chargeEvent.getPayload().getBaiduOrderReferenceId());
            log.info("对应支付的订单ID:"+chargeEvent.getPayload().getSellerOrderId());
            return buildErrorResponse("恭喜您支付成功，开始体验吧");
        }else {
            log.info("支付发生错误,"+payload.getMessage());
            return buildErrorResponse("哎呀，没有支付成功");
        }
    }

    @Override
    protected Response onBuyEvent(BuyEvent buyEvent) {
        com.baidu.dueros.data.request.buy.event.Payload payload = buyEvent.getPayload();
        String result = payload.getPurchaseResult().getPurchaseResult();
        log.info("此次支付结果:"+result+";msg: "+payload.getMessage());
        if (result.equals("SUCCESS")) {
            log.info("支付成功");
            return buildErrorResponse("恭喜您支付成功，开始体验吧");
        }
        return buildErrorResponse("哎呀，没有支付成功");
    }

    @Override
    protected Response onLinkAccountSucceededEvent(LinkAccountSucceededEvent linkAccountSucceededEvent) {
        log.info("登录成功返回信息:{}",linkAccountSucceededEvent);
        return buildErrorResponse("您已登录成功");
    }

    @Override
    protected Response onInent(IntentRequest intentRequest) {
        String userId = getCustomerId();
        String timestamp = this.getRequest().getRequest().getTimestamp();
        Query query = intentRequest.getQuery();
        String userWord = null;
        if (query != null) userWord = query.getOriginal();
        if (userWord == null){
            log.info("小度没有传来任何话语");
            return buildErrorResponse(ErrorMsg.SORRY_UNCATCH);
        }

        log.info("请求消息:"+this.getStrRequest());
        if (getIntent().getName().equals("buy")) {
            String productName = getSlot("sys.wildcard-slot");
            log.info("用户要购买的作品名："+productName);
            String accessToken;
            if ((accessToken = getAccessToken()) == null) {
                log.info("未登录");
                Card card = new LinkAccountCard();
//                    Card card = new TextCard("点击登录", "https://www.miaoshop.top/xiaodu/load?userId="+userId, "跳转链接");
                Response response = buildErrorResponse("请在屏幕或是小度App上完成登录");
                response.setCard(card);
                return response;
            }else {
                if (USE_CHARGE_ORDER) {
                    int hasUserAlreadyOwned;
                    try {
                        hasUserAlreadyOwned = hasUserBuyTheThing(accessToken);
                    } catch (Exception e) {
                        log.info("判断用户是否已购买操作出错:"+e.toString());
                        return new Response(new OutputSpeech(OutputSpeech.SpeechType.PlainText, "请在说一遍吧"));
                    }
                    switch (hasUserAlreadyOwned) {
                        case 1 : log.info("用户已经购买");// 已购买如何处理
                        case 2 : {
                            log.info("用户需要进行购买");
                            String sellerOrderId = "sgaggg"+new Random(47).nextInt(1000)+"aga"+new Random(47).nextInt(765355);
                            Charge chargeDirective = new Charge("0.01", sellerOrderId,
                                    productName, productName+",这是个非常精彩的故事哦");
                            addDirective(chargeDirective);
                            OutputSpeech outputSpeech = new OutputSpeech(OutputSpeech.SpeechType.PlainText, "请到小度APP上去支付");
                            return new Response(outputSpeech);
                        }
                        case 3 : return new Response(new OutputSpeech(OutputSpeech.SpeechType.PlainText, "请在说一遍吧"));
                        case 4 : {
                            // token失效
                            Card card = new LinkAccountCard();
                            Response response = buildErrorResponse("请在屏幕或是小度App上完成登录");
                            response.setCard(card);
                            return response;
                        }
                    }
                }else {
                    String productId = "190725134159854900";
                    Buy buy = new Buy(productId);
                    addDirective(buy);
                    OutputSpeech outputSpeech = new OutputSpeech(OutputSpeech.SpeechType.PlainText, "请到小度APP上去支付");
                    return new Response(outputSpeech);
                }
            }
        }else {
            QiWuResponse data = null;
            try {
                data = RequestTerminal.requestTerminate(userId, userWord, 1);
            } catch (Exception e) {
                log.error("请求终端接口出错:{}", e.toString());
            }
            if (data == null) {
                return buildErrorResponse(ErrorMsg.SORRY_UNCATCH);
            }
            String audioUrl = data.getAudio();
            String uri = "https://aliyun-hz1.chewrobot.com/xiaodu/image/5b2d0a11/lvzhou/1564033095964d39f.jpg";
            ImageStructure imageStructure = new ImageStructure(uri, 512, 512);
            Tag tag = new Tag("PAY", "支付标签");
            imageStructure.addTag(tag);
            Tpl1Content tpl1Content = new Tpl1Content(TextStructure.TextType.PlainText,data.getText());
            tpl1Content.setPosition(PositionType.CENTER);
            BodyTemplate1 template1 = new BodyTemplate1("互动小说", tpl1Content);
            template1.setBackgroundImage(imageStructure);
            addDirective(buildPlayDirective(audioUrl, AudioPlayerDirective.PlayBehaviorType.REPLACE_ALL));
            addDirective(new RenderTemplate(template1));
            String hint1 = "说退出就会退出当前作品";
            String hint2 = "开始时说推荐作品会向您推荐更多作品";
            String hint3 = "我们的作品库在不断扩充";
            String hint4 = "希望您玩的开心";
            ArrayList<String> hints = new ArrayList<>();
            hints.add(hint1);
            hints.add(hint2);
            hints.add(hint3);
            hints.add(hint4);
            addDirective(new Hint(hints));
        }
        return new Response();
    }

    @Override
    public Response onLaunch(LaunchRequest launchRequest) {
        log.info("请求消息:"+this.getStrRequest());
        //USER_TIMESTAMP.put(getCustomerId(), new Holder());
        OutputSpeech outputSpeech = new OutputSpeech(OutputSpeech.SpeechType.PlainText, WelcomeWord.WELCOME);
        Reprompt reprompt = new Reprompt(new OutputSpeech(OutputSpeech.SpeechType.PlainText, WelcomeWord.BEGIN_NO_RESPONSE));
        return new Response(outputSpeech, null, reprompt);
    }

    @Override
    public Response onSessionEnded(SessionEndedRequest sessionEndedRequest) {
        log.info("触发结束事件,"+this.getStrRequest());
        switch (sessionEndedRequest.getReason()) {
            case ERROR: dealWithError(sessionEndedRequest); break;
            case USER_INITIATED: log.info(EndMsg.USER_SAY_CANCEL); break;
            case EXCEEDED_MAX_REPROMPTS: log.info(EndMsg.CAN_NOT_UNDERSTAND);break;
        }
        // 向终端发送“退出”
//        OutputSpeech outputSpeech = new OutputSpeech(OutputSpeech.SpeechType.PlainText, EndMsg.BYE_BYE);
//        RequestTerminal.requestTerminate("退出", getCustomerId());
//        return new Response(outputSpeech);
        return new Response();
    }

    private void dealWithError(SessionEndedRequest request) {
        switch (request.getError().getType()) {
            case INTERNAL_ERROR: log.info(ErrorMsg.INTERNAL_ERROR); break;
            case INVALID_RESPONSE: log.info(ErrorMsg.INVALID_RESPONSE); break;
            case DEVICE_COMMUNICATION_ERROR: log.info(ErrorMsg.DEVICE_COMMUNICATION_ERROR); break;
        }
        log.info("错误信息 : "+ request.getError().getMessage());
    }

    private Response buildErrorResponse(String errorMsg) {
        OutputSpeech outputSpeech = new OutputSpeech(OutputSpeech.SpeechType.PlainText, errorMsg);
        return new Response(outputSpeech);
    }

    private Play buildPlayDirective(String audioUrl, AudioPlayerDirective.PlayBehaviorType type) {
        return new Play(type, audioUrl, 0);

    }

    /**
     * 音频播放结束，开麦
     *
     * expectSpeech
     * 在会话进行过程中，技能是否需要用户进行对话响应。该字段可选，并且仅在shouldEndSession为false时有效。
     *     true：表示技能需要用户进行对话响应，设备端上打开麦克风聆听用户的语音。
     *     false：表示技能不需要用户进行对话响应，设备端上关闭麦克风。
     *
     * shouldEndSession字段和expectSpeech字段同时为false，技能还在会话中，但是不需要用户进行回复，
     * 设备端会关闭麦克风，此时可以返回AudioPlayer.Play指令。
     * @param playbackFinishedEvent
     * @return
     */
    @Override
    protected Response onPlaybackFinishedEvent(PlaybackFinishedEvent playbackFinishedEvent) {
        log.info("音频播放完毕，开麦");
        this.waitAnswer();
        this.setExpectSpeech(true);
        return new Response();
    }

    @Override
    protected Response onPlaybackStoppedEvent(PlaybackStoppedEvent playbackStoppedEvent) {
        log.info("音频播放停止");
        this.waitAnswer();
        return new Response();
    }

    @Override
    protected Response onDefaultEvent() {
        log.info("未拦截的事件类型:"+getEventType(this.getStrRequest()));
        this.waitAnswer();
        return new Response();
    }

    private String getCustomerId() {
        String userId;
        try {
            userId = getUserId();
        } catch (NullPointerException e) {
            userId = Utils.useIPAsItsID();
        }
        return userId;
    }

    private String getEventType(String requestStr) {
        int typeIndex = requestStr.lastIndexOf("type");
        int comma = requestStr.indexOf(',', typeIndex);
        return requestStr.substring(typeIndex, comma);
    }

    private String getAsrRawResult(String requestStr) {
        int asrIndex = requestStr.lastIndexOf("asrRawResult");
        int asrWordBegin = requestStr.indexOf(':', asrIndex)+2;
        int asrWordEnd = requestStr.indexOf('"',asrWordBegin);
        return requestStr.substring(asrWordBegin, asrWordEnd);
    }

    private Integer hasUserBuyTheThing(String accessToken) throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put("App-Channel-Id", "JIAOYOUTIANXIA");
        headers.put("Authorization", Constants.XIAOWU_API.BEARER+accessToken);
        String responseStr = OkHttp3Utils.doGet(Constants.XIAOWU_API.GET_USER_INFO_BY_TOKEN, new HashMap<>(), headers);
        GetUserInfoByToken userInfo = JSON.parseObject(responseStr, GetUserInfoByToken.class);
        if (userInfo.getRetcode() == 0) {
            log.info("获取用户信息成功,"+userInfo);
            // 1: buy; 2: not buy
            return 1;
        }else {
            log.warn("获取用户信息失败:"+userInfo);
            // 若token失效刷新Token操作
            // 3: 404,获取用户信息失败，提醒用户再说一遍
            // 4: 401,403..让用户重新登录
            Integer status = userInfo.getStatus();
            if (status == 404) return 3;
            else return 4;
        }
    }
}
