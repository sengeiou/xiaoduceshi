package xiaoduhome.pojo.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author 苗权威
 * @dateTime 20-1-15 下午8:30
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ProjectData {
    private String name;
    private String showedWorkName;
    private String intro;
    private String authorName;
    private Boolean free;
    private String botAccount;
    private String bannerImgUrl;
    private Timestamp createTime;
    private List<String> labels;
}
