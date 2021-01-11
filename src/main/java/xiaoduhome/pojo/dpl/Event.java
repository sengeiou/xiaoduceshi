package xiaoduhome.pojo.dpl;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 苗权威
 * @dateTime 19-8-6 下午9:26
 */
@Getter
@Setter
@ToString
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Event {
    private String type;
    private String componentId;
    private Integer durationInMillisecond;
    private String distance;
    private Integer index;
    private String align;
    private String from;
    private String to;
    private String easing;
    private String attribute;
    private Long duration;
    private String repeatCount;
    private String position;
    private String value;
    private String repeatMode;

    public Event() {
    }

    public Event(String type, String componentId) {
        this.type = type;
        this.componentId = componentId;
    }
}
