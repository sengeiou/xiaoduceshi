package xiaoduhome.pojo.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

/**
 * @author 苗权威
 * @dateTime 20-1-19 下午1:41
 */
@Getter
@Setter
@ToString
public class UpdateUserCollectData {
    private String userId;
    private Map<String, Long> collectInfo;
    private String channel;
}
