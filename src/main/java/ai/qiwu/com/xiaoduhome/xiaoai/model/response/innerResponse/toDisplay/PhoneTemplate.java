package ai.qiwu.com.xiaoduhome.xiaoai.model.response.innerResponse.toDisplay;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

/**
 * @author 苗权威
 * @dateTime 20-1-7 下午8:34
 */
@Getter
@Setter
@ToString
public class PhoneTemplate {
    private String template_name;
    private Map<String, String> params;
}
