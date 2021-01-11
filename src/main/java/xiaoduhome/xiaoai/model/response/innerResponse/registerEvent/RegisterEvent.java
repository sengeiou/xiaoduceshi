package xiaoduhome.xiaoai.model.response.innerResponse.registerEvent;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 苗权威
 * @dateTime 19-7-5 下午8:49
 */
@Getter
@Setter
@ToString
public class RegisterEvent {
    public RegisterEvent(String event_name) {
        this.event_name = event_name;
    }

    private String event_name;
}
