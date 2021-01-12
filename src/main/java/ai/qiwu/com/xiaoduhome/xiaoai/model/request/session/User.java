package ai.qiwu.com.xiaoduhome.xiaoai.model.request.session;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 苗权威
 * @dateTime 19-7-5 下午8:16
 */
@Getter
@Setter
@ToString
public class User {
    private String user_id;
    private String access_token;
    private Boolean is_user_login;
    private String gender;
}
