package xiaoduhome.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author 苗权威
 * @dateTime 19-8-19 上午10:55
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserCollecResult {
    private Integer code;
    private String msg;
    private List<CollecProduct> data;
}
