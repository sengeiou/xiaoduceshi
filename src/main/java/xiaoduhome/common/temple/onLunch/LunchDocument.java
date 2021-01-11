package xiaoduhome.common.temple.onLunch;

import com.baidu.dueros.data.response.directive.dpl.Document;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 苗权威
 * @dateTime 19-9-5 下午4:32
 */
@Getter
@Setter
@ToString
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class LunchDocument extends Document {
    private LunchMainTemplate mainTemplate;
}
