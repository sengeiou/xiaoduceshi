package ai.qiwu.com.xiaoduhome.xiaoai.model.response.innerResponse.actionProperty;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 苗权威
 * @dateTime 20-1-10 下午2:33
 */
@Getter
@Setter
@ToString
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class AppIntentInfo {
    private String intent_type;
    private String uri;
}
