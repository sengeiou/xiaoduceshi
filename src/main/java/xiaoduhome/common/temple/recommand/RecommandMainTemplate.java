package xiaoduhome.common.temple.recommand;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 苗权威
 * @dateTime 19-8-27 上午10:49
 */
@Data
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class RecommandMainTemplate {
    private List<String> parameters = new ArrayList<>();
    private List<RecommandPojo> items;

    public RecommandMainTemplate() {
        this.parameters.add("payload");
        this.items = new ArrayList<>();
    }
}
