package ai.qiwu.com.xiaoduhome.common.temple;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author 苗权威
 * @dateTime 19-8-24 下午2:54
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class FrameTemple implements Agent {
    private String type;
    @JsonProperty("flex-direction")
    private String flexDirection;
    private List<FrameTemple> items;
    private String text;
    @JsonProperty("font-size")
    private String fontSize;
    @JsonProperty("margin-left")
    private String marginLeft;
    @JsonProperty("border-top-left-radius")
    private String borderTopLeftRadius;
    @JsonProperty("border-top-right-radius")
    private String borderTopRightRadius;
    @JsonProperty("border-bottom-right-radius")
    private String borderBottomRightRadius;
    @JsonProperty("padding-vertical")
    private String paddingVertical;
    @JsonProperty("padding-horizontal")
    private String paddingHorizontal;
    @JsonProperty("background-color")
    private String backgroundColor;
    private String color;
    private String height;
    @JsonProperty("font-style")
    private String fontStyle;
    @JsonProperty("margin-top")
    private String marginTop;
}
