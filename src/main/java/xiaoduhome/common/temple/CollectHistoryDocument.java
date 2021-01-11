package xiaoduhome.common.temple;

import com.baidu.dueros.data.response.directive.dpl.Document;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 苗权威
 * @dateTime 19-8-24 下午5:38
 */
@Getter
@Setter
@ToString
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class CollectHistoryDocument extends Document{
    private CollectHistoryMainTemplate mainTemplate;
    @JsonIgnore
    private String idToken;
}
