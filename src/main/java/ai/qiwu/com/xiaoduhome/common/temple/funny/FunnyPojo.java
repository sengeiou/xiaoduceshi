package ai.qiwu.com.xiaoduhome.common.temple.funny;

import ai.qiwu.com.xiaoduhome.pojo.dpl.Event;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author 苗权威
 * @dateTime 19-8-30 上午11:18
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class FunnyPojo {
    private String type;
    private String height;
    private String width;
    private String position;
    private String left;
    private String top;
    private String src;
    private List<FunnyPojo> items;
    private String headerTitle;
    private Boolean hasBackIcon;
    @JsonProperty("flex-direction")
    private String flexDirection;
    @JsonProperty("margin-top")
    private String marginTop;
    @JsonProperty("margin-left")
    private String marginLeft;
    @JsonProperty("font-size")
    private String fontSize;
    @JsonProperty("line-height")
    private String lineHeight;
    @JsonProperty("scale-type")
    private String scaleType;
    @JsonProperty("border-radius")
    private String borderRadius;
    private List<FunnyPageItemData> data;
    private String text;
    private String bottom;
    private List<String> hints;
    private String direction;
    private String componentId;
    private String looping;
    private String autoplay;
    private List<Event> onLoaded;
    private List<Event> onClick;
    @JsonProperty("border-width")
    private String borderWidth;
    @JsonProperty("padding-vertical")
    private String paddingVertical;
    @JsonProperty("padding-horizontal")
    private String paddingHorizontal;
    @JsonProperty("border-color")
    private String borderColor;
    private String color;
}
