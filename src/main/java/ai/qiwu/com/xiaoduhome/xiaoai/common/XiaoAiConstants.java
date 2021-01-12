package ai.qiwu.com.xiaoduhome.xiaoai.common;

/**
 * 常量值
 * @author 苗权威
 * @dateTime 19-7-5 下午8:56
 */
public interface XiaoAiConstants {
    String QI_WU_TERMINATE_DIDI = "http://didi-gz3.jiaoyou365.com:8070/api/chat2";
    String QI_WU_TERMINATE_DIDI2 = "http://didi-gz2.jiaoyou365.com:8070/api/chat2";
    String QI_WU_TERMINATE_nx2 = "http://didi-gz4.jiaoyou365.com:8070/api/chat2";
    String TTS_DIDI3 = "http://didi-gz3.jiaoyou365.com:9663/api/audio";
    String TTS_DIDI2 = "http://didi-gz2.jiaoyou365.com:9663/api/audio";
    String TTS_DIDI4 = "http://didi-gz4.jiaoyou365.com:9663/api/audio";
    String DEVICE_NAME = "XIAOAI";
    String CONTENT_TYPE = "application/json;charset=utf-8";
    String PLAYBACK_NEARLY_FINISHED = "mediaplayer.playbacknearlyfinished";
    String PREFIX_USERID="XIAOAI_";


    interface ERROR_MSG {
        String SORRY_REPEAT = "不好意思，请再说一遍吧";
        String TIME_OUT = "不好意思我没听清，请再说一遍吧";
        String TERMINATE_IO_EXCEPTION = "请求终端接口时发生IO错误";
        String HTTP_CONNECTION_CLOSE_EXCEPTION = "关闭与终端接口链接时出错";
    }

    interface TTS {
        String WELCOME_WORD = "欢迎来到交游天下,海量的作品在这等您,你可以说推荐作品给我";
        String WELCOME_WORD_CHILD = "小朋友你好，欢迎来到智能故事,你可以说推荐作品给我";
        String WELCOME_WORD_CHILD_STORY = "小朋友你好，欢迎来到互动故事,你可以说推荐作品给我";
        String WELCOME_WORD_Adult = "你好，欢迎来到智能小说,你可以说推荐作品给我";
        String WELCOME_WORD_Adult_literature = "你好，欢迎来到互动文艺,你可以说推荐作品给我";
        String WELCOME_WORD_DEFAULT = "欢迎来到我们的技能,与剧情人物自由对话，带您体验不同的人生和情怀。向您推荐新上线的求生记，您可以说打开求生记。或者说，推荐其它作品。";
        String END_BYE = "我们的作品库在不断扩充,欢迎再来玩";
        String END_BYE_CHILD = "再见啦小朋友，记得下次再来玩哦";
        String END_BYE_Adult = "再见啦,记得下次再来玩哦";
        String END_WORD = "退出";
        String END_BOT_WORD = "退出当前作品";
        String USER_IDLE = "小悟在等你的回复";
        String ANOTHER_USER_IDLE = "再不回复我,那我可退出喽";

        String WAIT_WORD_1 = "请稍微等下，小悟正在努力的获取您刚才的返回结果,";
        String WAIT_WORD_2 = "由于网络的原因，您可能需要稍稍等等";
        String WAIT_WORD_3 = "由于网络原因小悟正在加倍努力获取结果，不过您现在得快些对我说些什么，要不然您就退出我们的技能了";
    }

    interface SECURITY {
        String SMART_STORY_SECRET = "J03wDvFuUtr0lgBAm628aSNZiC0haluC/yXe90//Z7M=";
        String SMART_STORY_KEYID = "5hlvswxeP9J1Ths0+fL9vQ==";
        String SMART_NOVEL_SECRET = "IeVTzSD1wHDSoboQhqQFTHJgB28Wr/HtxkgkL5HLmmY=";
        String SMART_NOVEL_KEYID = "RrNYDL7DeNgKZGAW/KC6Iw==";
        String SMART_JIAOYOU_SECRET = "kkluiypsxYwGzLej+ZAHoo/ec0gnoOsBdr8uzmBn1+M=";
        String SMART_JIAOYOU_KEYID = "BDLeaCyRlF7wI8Wvb/RjwA==";
    }
}
