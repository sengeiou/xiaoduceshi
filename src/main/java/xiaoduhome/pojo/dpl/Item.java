package xiaoduhome.pojo.dpl;

import ai.qiwu.com.xiaoduhome.common.temple.Agent;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author 苗权威
 * @dateTime 19-8-6 下午9:14
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Item implements Agent {
    private Integer duration;
    private String type;
    private String width;
    private String height;
    private String src;
    @JsonProperty("scale-type")
    private String scaleType;
    @JsonProperty("border-bottom-right-radius")
    private String borderBottomRightRadius;
    private Double opacity;
    private String text;
    @JsonProperty("line-height")
    private String lineHeight;
    @JsonProperty("letter-spacing")
    private String LetterSpacing;
    private String color;
    @JsonProperty("padding-left")
    private String paddingLeft;
    @JsonProperty("padding-right")
    private String paddingRight;
    private String padding;
    @JsonProperty("margin-left")
    private String marginLeft;
    @JsonProperty("margin-right")
    private String marginRight;
    @JsonProperty("margin-top")
    private String marginTop;
    @JsonProperty("margin-bottom")
    private String marginBottom;
    private Boolean looping;
    private Boolean autoPlay;
    private String audioType;

    private List<Event> onClick;
    private List<Event> onEnd;
    private List<Event> onPrepared;
    private List<Event> onLoaded;
    private List<Event> onPause;
    private List<Event> onPlay;
    private List<Event> onRelease;
    private String command;

    private List<Item> items;
    private String position;
    private String top;
    private String left;
    private String bottom;
    private String right;
    private String headerImage;
    @JsonProperty("flex-direction")
    private String flexDirection;
    @JsonProperty("font-size")
    private String fontSize;
    @JsonProperty("font-style")
    private String fontStyle;
    private List<String> hints;
    private String componentId;

    private String direction;
    @JsonProperty("border-radius")
    private String borderRadius;

    private List<Data> data;
    private String headerTitle;
    private Boolean hasBackIcon;

    private String initialPage;
    @JsonProperty("max-lines")
    private Integer maxLines;

    @JsonProperty("align-items")
    private String alignItems;
    @JsonProperty("justify-content")
    private String justifyContent;
    @JsonProperty("text-overflow")
    private String textOverflow;

    private List<Agent> firstItem;
    @JsonProperty("background-color")
    private String backgroundColor;
}
