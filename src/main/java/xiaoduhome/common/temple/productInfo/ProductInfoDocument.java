package xiaoduhome.common.temple.productInfo;

import com.baidu.dueros.data.response.directive.dpl.Document;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

/**
 * @author 苗权威
 * @dateTime 19-9-23 下午5:22
 */
@Getter
@Setter
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ProductInfoDocument extends Document {
    private ProductInfoMainTemplate mainTemplate;
}
