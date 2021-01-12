package ai.qiwu.com.xiaoduhome.xiaoai.model.response.innerResponse.toDisplay.UITemplate.Item;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author 苗权威
 * @dateTime 19-7-5 下午8:36
 */
@Getter
@Setter
@ToString
public class Item {
    private String image_style;
    private List<Object> images;
    private String intent;
    private String title;
    private Object body;
    private String background_image;
}
