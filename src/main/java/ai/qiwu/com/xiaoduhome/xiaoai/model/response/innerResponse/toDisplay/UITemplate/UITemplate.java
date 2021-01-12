package ai.qiwu.com.xiaoduhome.xiaoai.model.response.innerResponse.toDisplay.UITemplate;

import ai.qiwu.com.xiaoduhome.xiaoai.model.response.innerResponse.toDisplay.UITemplate.Item.Item;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author 苗权威
 * @dateTime 19-7-5 下午8:35
 */
@Getter
@Setter
@ToString
public class UITemplate {
    private int type;
    private List<Item> items;
    private String logo;
    private Item item;
}
