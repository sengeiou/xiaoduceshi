package ai.qiwu.com.xiaoduhome.common.temple.funny;

import ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.directive.dpl.Document;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 苗权威
 * @dateTime 19-8-30 上午11:28
 */
@Getter
@Setter
@ToString
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class FunnyDocument extends Document {
    private FunnyMainTemplate mainTemplate;
}
