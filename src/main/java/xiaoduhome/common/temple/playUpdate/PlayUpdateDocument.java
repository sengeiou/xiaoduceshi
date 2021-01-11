package xiaoduhome.common.temple.playUpdate;

import com.baidu.dueros.data.response.directive.dpl.Document;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

/**
 * @author 苗权威
 * @dateTime 19-8-28 下午6:39
 */
@Data
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PlayUpdateDocument extends Document {
    private PlayUpdateMainTemplate mainTemplate;
}
