package xiaoduhome.common.temple.play;

import ai.qiwu.com.xiaoduhome.pojo.dpl.Event;
import ai.qiwu.com.xiaoduhome.pojo.playAside.PlayPageData;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.util.List;

/**
 * @author 苗权威
 * @dateTime 19-9-2 上午10:10
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PlayPojo {
    private String type;
    private String position;
    private String left;
    private String top;
    private String bottom;
    private String src;
    private String height;
    private String width;
    private String headerTitle;
    private Boolean hasBackIcon;
    private String right;
    private List<PlayPojo> items;
    @JsonProperty("padding-left")
    private String paddingLeft;
    @JsonProperty("padding-top")
    private String paddingTop;
    @JsonProperty("padding-right")
    private String paddingRight;
    @JsonProperty("padding-vertical")
    private String paddingVertical;
    @JsonProperty("border-radius")
    private String borderRadius;
    @JsonProperty("background-color")
    private String backgroundColor;
    private String text;
    @JsonProperty("scale-type")
    private String scaleType;
    private String componentId;
    @JsonProperty("font-size")
    private String fontSize;
    @JsonProperty("font-style")
    private String fontStyle;
    private String padding;
    private String looping;
    private String autoplay;
    private List<Event> onEnd;
    @JsonProperty("margin-left")
    private String marginLeft;
    @JsonProperty("margin-top")
    private String marginTop;
    @JsonProperty("margin-bottom")
    private String marginBottom;
    private List<String> hints;
    private List<Event> onClick;
    @JsonProperty("line-height")
    private String lineHeight;
    @JsonProperty("padding-bottom")
    private String paddingBottom;
    @JsonProperty("align-items")
    private String alignItems;
    @JsonProperty("justify-content")
    private String justifyContent;
    private String direction;
    @JsonProperty("flex-direction")
    private String flexDirection;
    @JsonProperty("onLoaded")
    private List<Event> onLoaded;
    private List<PlayPageData> data;
}
