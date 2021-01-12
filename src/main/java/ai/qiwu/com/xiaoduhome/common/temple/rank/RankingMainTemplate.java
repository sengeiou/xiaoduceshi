package ai.qiwu.com.xiaoduhome.common.temple.rank;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 苗权威
 * @dateTime 19-8-27 上午10:21
 */
@Data
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class RankingMainTemplate {
    private List<String> parameters = new ArrayList<>();
    private List<RankingPojo> items;

    public RankingMainTemplate() {
        this.parameters.add("payload");
        this.items = new ArrayList<>();
    }
}
