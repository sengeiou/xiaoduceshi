package xiaoduhome.common.temple;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 苗权威
 * @dateTime 19-8-24 下午5:42
 */
@Getter
@Setter
@ToString
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class CollectHistoryMainTemplate implements CustomMainTemplateInterface{
    private List<String> parameters = new ArrayList<>();
    private List<CollectHistoryPagePojo> items;

    public CollectHistoryMainTemplate() {
        this.parameters.add("payload");
        this.items = new ArrayList<>();
    }
}
