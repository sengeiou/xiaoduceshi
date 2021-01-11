package xiaoduhome.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 苗权威
 * @dateTime 19-7-17 下午7:34
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenResponse {
    private String accessToken;
    private String message;
    private String payload;
    private String refreshToken;
    private String tokenType;
    private Integer expire;
    private Integer retcode;
    private Integer status;
    private Long timestamp;
}
