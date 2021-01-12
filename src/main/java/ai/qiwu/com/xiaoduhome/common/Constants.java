package ai.qiwu.com.xiaoduhome.common;

/**
 * @author 苗权威
 * @dateTime 19-6-29 下午3:06
 */
public interface Constants {

    String SERVER_CHANGE_KEY = "audio_box_change_server";
    String CAN_NOT_MOVE_USER_KEY = "audio_box_stay_";

    String QI_WU_INTERFACE = "http://didi-gz3.jiaoyou365.com:8070/api/chat2";
    String INTERFACE_DI = "http://didi-gz3.jiaoyou365.com:8070/api/chat2";
    String TTS_DIDI3_GET_XIAOAI = "http://didi-gz3.jiaoyou365.com:9663/api/audio/get?type=";
    String TTS_DIDI4_GET_XIAOAI = "http://didi-gz4.jiaoyou365.com:9663/api/audio/get?type=";
    String PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBANMWIYduiQZt0B7s" +
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
    String IMG_CUSTOM_PREFIX = "https://didi-gz5.jiaoyou365.com/duai/image/";
    String PRE_PRODUCT_IMG = "prod:";
    String PRE_ENTER_PRODUCT = "enter";
    String PRE_PRODUCT_PLAY = "打开";

    String LOAD_INTRO = "扫描二维码输入手机号获取验证码登录";
    String REWARD_FLOWER = "请扫码支付";
    String UNIT_PRICE = "1";
    String PREFIX_USERID = "XIAODU_";

    Integer CO_HI_PAGE_SIZE = 4;
    Integer RE_RA_CA_PAGE_SIZE = 12;

    interface DPL_COMPONENT_ID {
        String CUR_FIRST_ROW_INDEX = "curFirstRowIndex";
        String SCROLL_ID = "udScroll";
        String CATEGORY_NAME = "categoryName";
        String START_ACTUAL_INDEX = "actualIndex";
        String FLOWE = "flowe";
        String FLOWER_LOAD = "flowerLoad";
        String PRE_COLLE = "colle";
        String COLLEC_ID = "colleAL";
        String UN_COLLEC_ID = "colleUn";
        String BACK_TRACE = "trace";
        String NEXT_PAGE_INDEX = "comCnt";

        String PRE_CATEGORY = "categ";
        String PRE_FUNNY_IMG = "funny";

        String COLLECTED_IMG_URL = "https://hw-gz25.heyqiwu.cn/duai/image/collec.png";
        String UN_COLLECTED_IMG_URL = "https://hw-gz25.heyqiwu.cn/duai/image/uncollec.png";
        String DEFAULT_ASIDE_IMG_URL = "http://hw-gz25.heyqiwu.cn/Editor/upload/img/npc/2019/02/19174004213.png";
    }

    interface PRODUCT_INTRO {
        String CREATE_TIME = "发表时间： ";
        String AUTHOR = "作者： ";
        String LABEL = "相关标签: ";
        String SPACE = "  ";
        String USER_SEND_FLOWER = "已赏朵数:";
        String FLOWER = "打赏鲜花";
        String COLLECED = "已收藏";
        String UN_COLLEC = "收藏";
        String COLLECT_BOT_WORD = "已为您收藏该作品";
        String COLLECT_BOT_WORD_MG = "已将%s加入您的收藏";
        String UN_COLLECT_BOT_WORD = "已将该作品移出您的收藏列表";
        String UN_COLLECT_BOT_WORD_MG = "已将%s移出您的收藏列表";
    }

    interface SKILL_SECURITY {
        String REDIRECT_URI = "https://xiaodu.baidu.com/saiya/auth/3a0610f8af67a3c92d803a80a1c8976c";
//        String REDIRECT_URI = "https://xiaodu.baidu.com/saiya/auth/4fbc7b9112e3a43dc7dbf43fd556929e";
        String REDIRECT_URI_TEST = "https://xiaodu-dbp.baidu.com/saiya/auth/3a0610f8af67a3c92d803a80a1c8976c";
//        String REDIRECT_URI_TEST = "https://xiaodu-dbp.baidu.com/saiya/auth/4fbc7b9112e3a43dc7dbf43fd556929e";
        String CLIENT_ID = "aiqiwucom";
        String CLIENT_SECRET = "miaoquanwei";
    }

    interface WelcomeWord {
        String WELCOME = "欢迎来到交游天下。与剧情人物自由对话，带您体验不同的人生和情怀。向您推荐新上线的求生记，您可以说打开求生记。或者说，推荐其它作品。";
//        String WELCOME_FORMAT = "欢迎来到交游天下。与剧情人物自由对话，带您体验不同的人生和情怀。向您推荐新上线的%s，您可以说打开%s。或者说，推荐其它作品。";
        String SKILL = "交游天下";
        String SKILL_DEFAULT = "我们的技能";
        // 欢迎来到晓悟智能故事。与可爱的角色们自由对话，畅游不同的故事世界。我们新上线了遇见伽利略，你可以说打开遇见伽利略。或者说，推荐其它作品。
//        String WELCOME_SMART = "欢迎来到晓悟智能故事。我们新上线了遇见伽利略，你可以说打开遇见伽利略。或者说，推荐其它作品。";
        String WELCOME_SMART_FORMAT = "欢迎来到晓悟智能故事。与可爱的角色们自由对话，畅游不同的故事世界。我们新上线了%s，你可以说打开%s。或者说，推荐其它作品。";
        String WELCOME_SMART_FORMAT_AI = "欢迎来到晓悟智能故事。推荐我们新上线的%s，你可以说打开%s。或者说，推荐其它作品。";
        String SKILL_SMART = "智能故事";
        String XIAOWU_SKILL_SMART = "晓悟智能故事";
        // 与剧情人物自由对话，带您体验不同的人生和情怀。
//        String WELCOME_NOVEL = "欢迎来到晓悟智能小说。向您推荐新上线的野蛮女友，您可以说打开野蛮女友。或者说，推荐其它作品。";
        String WELCOME_NOVEL_FORMAT = "欢迎来到晓悟智能小说。与剧情人物自由对话，带您体验不同的人生和情怀。向您推荐新上线的%s，您可以说打开%s。或者说，推荐其它作品。";
        String SKILL_NOVEL = "智能小说";
        String XIAOWU_SKILL_NOVEL = "晓悟智能小说";
        String WELCOME_LITERATURE = "欢迎来到互动文艺,你可以说推荐作品给我";
        String SKILL_LITERATURE = "互动文艺";
        String WELCOME_STORY = "小朋友你好,欢迎来到交互故事,你可以对我说有什么好玩的吗";
        String SKILL_STORY = "交互故事";
        String BEGIN_NO_RESPONSE = "咋还没开始就停住了，我们这里可有很多好玩的作品哦";
        String GUIDE_WORD = "请点击或者直接说出您想要玩的作品名";
        String FAIL_COLLECT = "好像出了什么问题,请再说一遍吧";
        String ERROR_SEND_FLOWER = "好像出了什么问题,我们终止了该订单";
    }

    interface EndMsg {
        String BYE_BYE = "我们的作品库在不断的扩充，欢迎下次再来玩";
        String END_BYE_CHILD = "再见啦小朋友，记得下次再来玩哦";
        String END_BYE_NOVEL = "后续有更多作品上线，欢迎常来访问智能小说。"; // smart novel
        String XIAOWU_END_BYE_NOVEL = "后续有更多作品上线，欢迎常来访问晓悟智能小说。"; // smart novel
        String END_BYE_STORY = "智能故事后续会有更多作品上线，欢迎常来"; // smart story
        String XIAOWU_END_BYE_STORY = "晓悟智能故事后续会有更多作品上线，欢迎常来"; // smart story
        String END_BYE_JIAOYOU = "交游天下后续会有更多作品上线，欢迎常来";
        String END_BYE_DEFAULT = "后续会有更多作品上线，欢迎常来我们的技能";
        String END_BYE_Adult = "再见啦,记得下次再来玩哦";
        String CAN_NOT_UNDERSTAND = "用户无输入或多次输入无法理解访问";
        String USER_SAY_CANCEL = "用户直接说退出";

        String BEFORE_LOAD_OUT = "如果您想退出我们的技能请对我说退出";
    }

    interface ErrorMsg {
        String SORRY_UNCATCH = "不好意思我没听清，请再说一遍吧";
        String TERMINATE_RETURN_EMPTY = "中控返回了空的链接";
        String INTERNAL_ERROR = "其他DuerOS系统错误";
        String INVALID_RESPONSE = "技能返回了无效的响应,可能技能响应数据量超过24KB,或是技能在request的dialogState为COMPLETED时仍然发送Dialog.Delegate指令等";
        String DEVICE_COMMUNICATION_ERROR = "DuerOS与端通信异常";

        String COLLECT_FAIL = "您可以点击屏幕上的按钮收藏该作品";
        String UN_COLLECT_FAIL = "您可以点击屏幕上的按钮进行取消操作";

        String INTERFACE_ERROR = "终端接口请求出错";
        String HTTP_CONNECTION_CLOSE_FAILED = "与终端的链接关闭错误";
    }

    interface XIAOWU_API {
        String BASIC_PREFIX = "Basic ";
        String BEARER = "Bearer ";
        //https://account-center-test.chewrobot.com/api/captcha/sms
        String CAPTCHA_SMS = "https://account-center-test.chewrobot.com/api/sdk/captcha/sms";
        String REFRESH_TOKEN = "https://account-center-test.chewrobot.com/api/sdk/token";
        String SDK_TOKEN = "https://account-center-test.chewrobot.com/api/sdk/token";
        String GET_USER_INFO_BY_TOKEN ="https://account-center-test.chewrobot.com/api/sdk/user/info";

//        String FLOWER_INTERFACE = "http://aws-nx2.chewrobot.com:18082/api/flower/count";
        String USER_ID_BY_TOKEN = "https://account-center-test.chewrobot.com/api/sdk/user/info";
    }

    interface BAI_DU {
        String RETURN_BAIDU_TOKEN = "https://openapi.baidu.com/oauth/2.0/token";
        String BAIDU_TOKEN = "https://openapi.baidu.com/oauth/2.0/token?grant_type=authorization_code&client_id=dSG525LMr887uLlfAyK1C1ag&client_secret=1azuXoKHMSWT5MVNNkZ4C5mED23qySZh&redirect_uri=https%3A%2F%2Fwww.miaoshop.top%2Fxiaodu%2Fbaidu%2Fcode&code=";
        String BAIDU_AUTHORIZE = "https://openapi.baidu.com/oauth/2.0/authorize?response_type=code&client_id=dSG525LMr887uLlfAyK1C1ag&redirect_uri=https%3A%2F%2Fwww.miaoshop.top%2Fxiaodu%2Fbaidu%2Fcode&confirm_login=1&state=mqw";
    }

    interface DPL2 {
        String TRIGGER_HOME_PAGE_LOAD = "loadingPage";
        String TRIGGER_HOME_PAGE_UPDATE = "homePageUpdate";
        String ATTRIBUTE_PAGE_UPDATE_COUNT = "updatePageCount";
        String AUDIO_END = "audio";
        String PRODUCT_END_MARK = "end";
        String COLLECT_BUTTON_CLICK = "collectClick";
        String BACK_TRIGGER = "backTrigger";
    }
}
