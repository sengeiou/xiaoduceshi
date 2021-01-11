package xiaoduhome.xiaoai.model.request.innerRequest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 苗权威
 * @dateTime 19-7-5 下午8:23
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Request {
    private Integer type;
    private String request_id;
    private Long timestamp;
    private Boolean no_response;
    private String event_type;
    private EventProperty event_property;
    private String Locale;
    private SlotInfo slot_info;

    private Boolean is_monitor;
}
