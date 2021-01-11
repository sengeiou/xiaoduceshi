package xiaoduhome.xiaoai.model.request;

import ai.qiwu.com.xiaoduhome.xiaoai.model.request.context.Context;
import ai.qiwu.com.xiaoduhome.xiaoai.model.request.innerRequest.Request;
import ai.qiwu.com.xiaoduhome.xiaoai.model.request.session.Session;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 苗权威
 * @dateTime 19-7-5 下午8:10
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class XiaoAiRequest {
    private String version;
    private String query;
    private Session session;
    private Context context;
    private Request request;

    private Integer type; // 1 为jiaoyou-audio-child-test
}
