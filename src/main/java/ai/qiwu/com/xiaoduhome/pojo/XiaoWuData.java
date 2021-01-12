package ai.qiwu.com.xiaoduhome.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 苗权威
 * @dateTime 19-8-17 下午5:07
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class XiaoWuData {
    private String worksName;
    private String authorName;
    private String aipioneerUsername;
    private Integer count;
}
