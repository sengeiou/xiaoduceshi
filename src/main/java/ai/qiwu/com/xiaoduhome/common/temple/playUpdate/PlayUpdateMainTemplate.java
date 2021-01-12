package ai.qiwu.com.xiaoduhome.common.temple.playUpdate;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 苗权威
 * @dateTime 19-8-28 下午6:38
 */
@Data
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PlayUpdateMainTemplate {
    private List<String> parameters = new ArrayList<>();
    private List<PlayUpdatePojo> items;

    public PlayUpdateMainTemplate() {
        this.parameters.add("payload");
        this.items = new ArrayList<>();
    }
}
