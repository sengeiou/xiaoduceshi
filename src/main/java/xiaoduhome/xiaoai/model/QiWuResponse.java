package xiaoduhome.xiaoai.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class QiWuResponse {
    private String audio;
    private String text;
    private String extraChatKey;
    private String mainChatKey;
}
