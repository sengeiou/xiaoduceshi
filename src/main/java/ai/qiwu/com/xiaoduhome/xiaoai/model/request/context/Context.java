package ai.qiwu.com.xiaoduhome.xiaoai.model.request.context;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author 苗权威
 * @dateTime 19-7-5 下午8:20
 */
@Getter
@Setter
@ToString
public class Context {
    private String device_id;
    private String passport;
    private List<ClientAppInfo> app_info;
}
