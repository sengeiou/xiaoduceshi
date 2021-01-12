package ai.qiwu.com.xiaoduhome.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 苗权威
 * @dateTime 19-7-18 下午1:33
 */
@Getter
@Setter
@ToString
public class GetUserInfoByToken {
    private String message;
    private String payload;
    private Integer retcode;
    private Integer status;
}
