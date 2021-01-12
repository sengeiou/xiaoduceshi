package ai.qiwu.com.xiaoduhome.common.temple.play;

import ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.directive.dpl.Document;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 苗权威
 * @dateTime 19-9-2 上午10:18
 */
@Getter
@Setter
@ToString
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PlayDocument extends Document {
    private PlayMainTemplate mainTemplate;
}
