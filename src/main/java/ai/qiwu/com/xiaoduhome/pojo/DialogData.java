package ai.qiwu.com.xiaoduhome.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class DialogData {
    private String botAccount;
    private String npcName;
    private String person;
    private String pitch;
    private String speed;
    private String text;
    private String tone;
    private String volume;
}
