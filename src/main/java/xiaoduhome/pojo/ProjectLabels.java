package xiaoduhome.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author 苗权威
 * @dateTime 19-8-16 下午8:15
 */
@Getter
@Setter
@ToString
public class ProjectLabels {
    private String projectName;
    private List<String> labels;

    public ProjectLabels(String projectName, List<String> labels) {
        this.projectName = projectName;
        this.labels = labels;
    }
}
