package xiaoduhome.service.chatsLog;

import lombok.Getter;
import lombok.Setter;

/**
 * @author 苗权威
 * @dateTime 19-12-30 下午3:15
 */
@Getter
@Setter
public class LoginData {
    private String username;
    private String passwd;
    private Long time;
    private String token;
}
