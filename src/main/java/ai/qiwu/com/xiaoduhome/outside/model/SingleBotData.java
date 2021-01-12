package ai.qiwu.com.xiaoduhome.outside.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * painter
 * 20-7-2 下午3:43
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class SingleBotData {
    private String audioUrl;
    private String text;
    private Integer cmdCode;
}
