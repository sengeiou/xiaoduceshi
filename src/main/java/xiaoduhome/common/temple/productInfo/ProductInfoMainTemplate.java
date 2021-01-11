package xiaoduhome.common.temple.productInfo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 苗权威
 * @dateTime 19-9-23 下午5:23
 */
@Getter
@Setter
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ProductInfoMainTemplate {
    private List<String> parameters = new ArrayList<>();
    private List<ProductInfoPojo> items;

    public ProductInfoMainTemplate() {
        this.parameters.add("payload");
        this.items = new ArrayList<>();
    }
}
