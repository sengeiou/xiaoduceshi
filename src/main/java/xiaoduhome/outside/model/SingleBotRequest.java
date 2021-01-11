package xiaoduhome.outside.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

/**
 * painter
 * 20-7-2 下午3:27
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SingleBotRequest {
    private String uid;
    private String msg;
    private String botAccount;
}
