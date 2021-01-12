package ai.qiwu.com.xiaoduhome.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 苗权威
 * @dateTime 19-7-18 下午7:44
 */
@Getter
@Setter
@ToString
public class TokenDuerosResponse {
    private String access_token;
    private Integer expires_in;
    private String refresh_token;
}
