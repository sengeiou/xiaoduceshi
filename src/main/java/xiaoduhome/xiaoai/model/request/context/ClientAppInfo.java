package xiaoduhome.xiaoai.model.request.context;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 苗权威
 * @dateTime 19-7-5 下午8:21
 */
@Getter
@Setter
@ToString
public class ClientAppInfo {
    private String pkg_name;
    private String version_name;
}
