package ai.qiwu.com.xiaoduhome.xiaoai.model.response.innerResponse.directive.AudioItem;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 苗权威
 * @dateTime 19-7-5 下午8:42
 */
@Getter
@Setter
@ToString
public class AudioStream {
    private String token;
    private String url;
    private Long offset_in_milliseconds;
}
