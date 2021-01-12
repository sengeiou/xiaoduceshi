package ai.qiwu.com.xiaoduhome.outside.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * painter
 * 20-7-2 下午3:42
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class SingleBotResponse {
    private Integer code;
    private String msg;
    private SingleBotData data;
}
