package ai.qiwu.com.xiaoduhome.spirit;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 苗权威
 * @dateTime 19-12-11 下午4:06
 */
@Getter
@Setter
@ToString
public class SpiritTerminateRequest {
    private String uid;
    private String msg;
    private Integer resptype = 2;
}
