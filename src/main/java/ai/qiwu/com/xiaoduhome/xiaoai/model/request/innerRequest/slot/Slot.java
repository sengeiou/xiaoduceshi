package ai.qiwu.com.xiaoduhome.xiaoai.model.request.innerRequest.slot;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 苗权威
 * @dateTime 19-7-5 下午8:27
 */
@Getter
@Setter
@ToString
public class Slot {
    private String name;
    private String value;
    private boolean is_inquire_failed;
}
