package xiaoduhome.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 苗权威
 * @dateTime 20-1-17 下午2:40
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActorWithWord {
    private String actor;
    private String asideImg;
    private String text;
    private Integer itemSeqId;
}
