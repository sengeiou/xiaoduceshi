package xiaoduhome.xiaoai.model.request.session;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

/**
 * @author 苗权威
 * @dateTime 19-7-5 下午8:15
 */
@Getter
@Setter
@ToString
public class Session {
    private String session_id;
    private Application application;
    private Map<String, String> attributes;
    private User user;
}
