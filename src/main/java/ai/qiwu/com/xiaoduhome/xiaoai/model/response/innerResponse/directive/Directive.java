package ai.qiwu.com.xiaoduhome.xiaoai.model.response.innerResponse.directive;

import ai.qiwu.com.xiaoduhome.xiaoai.model.response.innerResponse.directive.AudioItem.AudioItem;
import ai.qiwu.com.xiaoduhome.xiaoai.model.response.innerResponse.directive.ttsItem.TTSItem;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 苗权威
 * @dateTime 19-7-5 下午8:41
 */
@Getter
@Setter
@ToString
public class Directive {
    private String type;
    private AudioItem audio_item;
    private TTSItem tts_item;
}
