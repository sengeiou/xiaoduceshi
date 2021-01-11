package xiaoduhome.common.temple.onLunch;

import ai.qiwu.com.xiaoduhome.common.temple.funny.FunnyPojo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 苗权威
 * @dateTime 19-9-5 下午4:33
 */
@Getter
@Setter
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class LunchMainTemplate {
    private List<String> parameters = new ArrayList<>();
    private List<LunchPojo> items;

    public LunchMainTemplate() {
        this.parameters.add("payload");
        this.items = new ArrayList<>();
    }
}
