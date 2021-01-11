package xiaoduhome.spirit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 苗权威
 * @dateTime 19-12-11 下午3:40
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Dialog {
    private String botAccount;
    private String npcName;
    private String person;
    private String text;
    private String tone;

    private String pitch;
    private String speed;
    private String volume;
}
