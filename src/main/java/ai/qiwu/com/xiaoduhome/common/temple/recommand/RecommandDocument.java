package ai.qiwu.com.xiaoduhome.common.temple.recommand;

import ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.directive.dpl.Document;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 苗权威
 * @dateTime 19-8-27 上午10:50
 */
@Getter
@Setter
@ToString
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class RecommandDocument extends Document {
    private RecommandMainTemplate mainTemplate;

    @JsonIgnore
    private String recommendIdList;

    @JsonIgnore
    private String count;
}
