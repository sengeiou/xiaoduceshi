package xiaoduhome.pojo.dpl;

import com.baidu.dueros.data.response.directive.dpl.Document;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 苗权威
 * @dateTime 19-8-7 上午9:50
 */
@Getter
@Setter
@ToString
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class MyDocument extends Document {
    private MyMainTemplate mainTemplate;
}
