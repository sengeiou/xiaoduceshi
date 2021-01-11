package xiaoduhome.common.temple.rank;

import com.baidu.dueros.data.response.directive.dpl.Document;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 苗权威
 * @dateTime 19-8-27 上午10:23
 */
@Getter
@Setter
@ToString
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class RankingDocument extends Document {
    private RankingMainTemplate mainTemplate;

    @JsonIgnore
    private String rankBotIds;

    @JsonIgnore
    private String count = "15";
}
