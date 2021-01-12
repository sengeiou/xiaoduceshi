package ai.qiwu.com.xiaoduhome.common.temple;

import ai.qiwu.com.xiaoduhome.pojo.dpl.Event;
import ai.qiwu.com.xiaoduhome.pojo.pageData.HistoryData;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author 苗权威
 * @dateTime 19-8-24 下午4:57
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class CollectHistoryPagePojo {
    private String type;
    private String position;
    private String left;
    private String right;
    private String width;
    private String bottom;
    private String height;
    private String src;
    private String top;
    private Boolean hasBackIcon;
    private String margin;
    private String componentId;
    private String headerTitle;
    @JsonProperty("margin-left")
    private String marginLeft;
    @JsonProperty("margin-right")
    private String marginRight;
    @JsonProperty("margin-top")
    private String marginTop;
    @JsonProperty("margin-bottom")
    private String marginBottom;
    private String padding;
    private List<CollectHistoryPagePojo> items;
    private String direction;
    @JsonProperty("font-size")
    private String fontSize;
    @JsonProperty("flex-direction")
    private String flexDirection;
    private String text;
    @JsonProperty("border-radius")
    private String borderRadius;
    private List<HistoryData> data;
    @JsonProperty("text-overflow")
    private String textOverflow;
    @JsonProperty("max-lines")
    private Integer maxLines;
    private List<Event> onClick;
    private List<Event> onEnd;
    @JsonProperty("border-color")
    private String borderColor;
    @JsonProperty("border-width")
    private String borderWidth;
    private List<String> hints;
    @JsonProperty("padding-bottom")
    private String paddingBottom;
    @JsonProperty("padding-right")
    private String paddingRight;
    @JsonProperty("padding-left")
    private String paddingLeft;
    @JsonProperty("scale-type")
    private String scaleType;
    @JsonProperty("background-color")
    private String backgroundColor;
    @JsonProperty("align-items")
    private String alignItems;
    @JsonProperty("justify-content")
    private String justifyContent;
}
