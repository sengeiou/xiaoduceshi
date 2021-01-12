package ai.qiwu.com.xiaoduhome.common.temple.onLunch;

import ai.qiwu.com.xiaoduhome.common.temple.FrameTemple;
import ai.qiwu.com.xiaoduhome.pojo.dpl.Event;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author 苗权威
 * @dateTime 19-9-5 下午4:34
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class LunchPojo {
    private String type;
    private String position;
    private String height;
    private String width;
    private String left;
    private String top;
    private String bottom;
    private String right;
    private String src;
    private List<LunchPojo> items;
    @JsonProperty("padding-right")
    private String paddingRight;
    @JsonProperty("padding-top")
    private String paddingTop;
    @JsonProperty("padding-bottom")
    private String paddingBottom;
    private String componentId;
    private String headerTitle;
    private Boolean hasBackIcon;
    private String direction;
    @JsonProperty("margin-bottom")
    private String marginBottom;
    @JsonProperty("margin-left")
    private String marginLeft;
    @JsonProperty("margin-right")
    private String marginRight;
    @JsonProperty("margin-top")
    private String marginTop;
    @JsonProperty("border-radius")
    private String borderRadius;
    @JsonProperty("scale-type")
    private String scaleType;
    private List<Event> onClick;
    private List<Event> onEnd;
    @JsonProperty("background-color")
    private String backgroundColor;
    @JsonProperty("align-items")
    private String alignItems;
    private String text;
    @JsonProperty("font-size")
    private String fontSize;
    @JsonProperty("justify-content")
    private String justifyContent;
    private List<LunchBotData> data;
    private List<FrameTemple> firstItem;
    private String headerImage;
    private List<String> hints;
}
