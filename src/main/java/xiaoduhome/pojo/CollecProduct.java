package xiaoduhome.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 苗权威
 * @dateTime 19-8-19 上午10:56
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class CollecProduct {
    private String worksName; // 作品名称
    private String authorName;
    private String aipioneerUsername; //作品用户名
}
