package xiaoduhome.common;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 苗权威
 * @dateTime 19-7-17 下午5:59
 */
@Getter
@Setter
@ToString
public class TokenHolder {
    private volatile String userId;
    private volatile String accessToken;
    private volatile String refreshToken;
    private volatile String timestamp;
//    private volatile String state;
    private volatile Integer expire;
}
