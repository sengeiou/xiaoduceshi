package ai.qiwu.com.xiaoduhome.xiaoai.model.response;

import ai.qiwu.com.xiaoduhome.xiaoai.model.response.innerResponse.Response;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

/**
 * @author 苗权威
 * @dateTime 19-7-5 下午8:30
 */
@Getter
@Setter
@ToString
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class XiaoAiResponse {
    private String version;
    private Map<String, String> session_attributes;
    private Boolean is_session_end;
    private Response response;
}
