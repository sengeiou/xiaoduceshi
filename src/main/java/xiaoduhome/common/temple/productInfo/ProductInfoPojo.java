package xiaoduhome.common.temple.productInfo;

import ai.qiwu.com.xiaoduhome.pojo.dpl.Event;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author 苗权威
 * @dateTime 19-9-23 下午5:24
 */
@Getter
@Setter
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ProductInfoPojo {
    private String type;
    private String position;
    private String top;
    private String bottom;
    private String left;
    private String src;
    private String height;
    private String direction;
    private String width;
    private String headerTitle;
    private Boolean hasBackIcon;
    private String componentId;
    private List<ProductInfoPojo> items;
    @JsonProperty("flex-direction")
    private String flexDirection;
    @JsonProperty("border-radius")
    private String borderRadius;
    @JsonProperty("scale-type")
    private String scaleType;
    @JsonProperty("margin-left")
    private String marginLeft;
    @JsonProperty("margin-top")
    private String marginTop;
    private List<Event> onClick;
    private List<Event> onEnd;
    @JsonProperty("font-size")
    private String fontSize;
    private String text;
    @JsonProperty("max-lines")
    private Integer maxLines;
    @JsonProperty("margin-right")
    private String marginRight;
    @JsonProperty("line-height")
    private String lineHeight;
    @JsonProperty("text-overflow")
    private String textOverflow;
    @JsonProperty("align-items")
    private String alignItems;
    private List<String> hints;
}
