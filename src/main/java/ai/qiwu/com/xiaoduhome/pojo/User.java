package ai.qiwu.com.xiaoduhome.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 苗权威
 * @dateTime 19-7-12 下午8:47
 */
@Getter
@Setter
@ToString
public class User {
    private String userId;
    private String userName;
    private String password;
    private String phoneNumber;
    private String captcha;

    private String state;
}
