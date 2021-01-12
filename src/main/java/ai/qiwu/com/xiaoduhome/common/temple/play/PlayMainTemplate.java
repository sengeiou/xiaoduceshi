package ai.qiwu.com.xiaoduhome.common.temple.play;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 苗权威
 * @dateTime 19-9-2 上午10:17
 */
@Data
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PlayMainTemplate {
    private List<String> parameters = new ArrayList<>();
    private List<PlayPojo> items;

    public PlayMainTemplate() {
        this.parameters.add("payload");
        this.items = new ArrayList<>();
    }
}
