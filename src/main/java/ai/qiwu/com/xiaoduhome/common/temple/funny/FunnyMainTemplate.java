package ai.qiwu.com.xiaoduhome.common.temple.funny;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 苗权威
 * @dateTime 19-8-30 上午11:28
 */
@Getter
@Setter
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class FunnyMainTemplate {
    private List<String> parameters = new ArrayList<>();
    private List<FunnyPojo> items;

    public FunnyMainTemplate() {
        this.parameters.add("payload");
        this.items = new ArrayList<>();
    }
}
