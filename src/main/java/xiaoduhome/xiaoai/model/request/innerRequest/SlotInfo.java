package xiaoduhome.xiaoai.model.request.innerRequest;

import ai.qiwu.com.xiaoduhome.xiaoai.model.request.innerRequest.slot.Slot;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author 苗权威
 * @dateTime 19-7-5 下午8:26
 */
@Getter
@Setter
@ToString
public class SlotInfo {
    private String intent_name;
    private boolean is_confirmed;
    private List<Slot> slots;
}
