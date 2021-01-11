package xiaoduhome.xiaoai.model.response.innerResponse.toDisplay;

import ai.qiwu.com.xiaoduhome.xiaoai.model.response.innerResponse.toDisplay.UITemplate.UITemplate;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 苗权威
 * @dateTime 19-7-5 下午8:34
 */
@Getter
@Setter
@ToString
public class ToDisplay {
    private int type;
    private String url;
    private String text;
    private UITemplate ui_template;

    private String ui_type;
    private PhoneTemplate phone_template;
}
