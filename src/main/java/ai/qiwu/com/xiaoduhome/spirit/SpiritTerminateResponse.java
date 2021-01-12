package ai.qiwu.com.xiaoduhome.spirit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author 苗权威
 * @dateTime 19-12-11 下午4:07
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpiritTerminateResponse {
    private String aipioneerUsername;
    private List<Dialog> dialogs;
//    private String extraChatKey;
//    private String mainChatKey;
}
