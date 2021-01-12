package ai.qiwu.com.xiaoduhome.pojo.dpl;

import ai.qiwu.com.xiaoduhome.common.temple.CustomMainTemplateInterface;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 苗权威
 * @dateTime 19-8-7 上午9:37
 */
@Getter
@Setter
@ToString
public class MyMainTemplate implements CustomMainTemplateInterface {
    private List<String> parameters = new ArrayList<>();
    private List<Item> items;

    public MyMainTemplate() {
        this.parameters.add("payload");
        this.items = new ArrayList<>();
    }
}
