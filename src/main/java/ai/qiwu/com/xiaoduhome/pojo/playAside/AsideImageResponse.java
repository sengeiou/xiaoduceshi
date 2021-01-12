package ai.qiwu.com.xiaoduhome.pojo.playAside;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * @author 苗权威
 * @dateTime 19-9-10 下午6:16
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AsideImageResponse {
    private Integer code;
    private String msg;
    private AsideImage data;
}
