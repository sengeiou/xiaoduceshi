package xiaoduhome.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 苗权威
 * @dateTime 19-9-10 下午4:46
 */
@Getter
@Setter
@ToString
public class UserBehaviorData {
    // 用户浏览历史
    private volatile Map<String, Long> userBotId2History = new HashMap<>();
    // 用户对作品所送花的数目
    private volatile Map<String, Long> userBotId2Flowers = new HashMap<>();
    // 用户是否收藏该作品
    private volatile Map<String, Long> userBotId2Collected = new HashMap<>();
    // 标记清除,每半小时标记一次
//    private volatile Integer mark = 1;

    private volatile String jytUserId;

//    private volatile String channel;
}
