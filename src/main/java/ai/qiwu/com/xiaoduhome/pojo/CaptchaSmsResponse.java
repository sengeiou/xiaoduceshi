package ai.qiwu.com.xiaoduhome.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 苗权威
 * @dateTime 19-7-17 下午6:33
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class CaptchaSmsResponse {
    private String message;
    private String payload;
    private Integer retcode;
    private Integer status;
    private Long timestamp;
}
