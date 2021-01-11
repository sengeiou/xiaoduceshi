package xiaoduhome.common.temple.recommand;

import ai.qiwu.com.xiaoduhome.common.temple.rank.RankBotInfo;
import ai.qiwu.com.xiaoduhome.common.temple.rank.RankingPojo;
import ai.qiwu.com.xiaoduhome.pojo.dpl.Event;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.util.List;

/**
 * @author 苗权威
 * @dateTime 19-8-27 上午10:48
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class RecommandPojo {
    private String type;
    private String height;
    private String width;
    private List<RecommandPojo> items;
    private String position;
    private String top;
    private String left;
    private String right;
    private String src;
    private String headerTitle;
    private Boolean hasBackIcon;
    private String componentId;
    @JsonProperty("margin-top")
    private String marginTop;
    @JsonProperty("margin-bottom")
    private String marginBottom;
    private String direction;
    @JsonProperty("align-items")
    private String alignItems;
    @JsonProperty("margin-left")
    private String marginLeft;
    @JsonProperty("scale-type")
    private String scaleType;
    @JsonProperty("border-radius")
    private String borderRadius;
    private List<Event> onClick;
    private List<Event> onEnd;
    @JsonProperty("font-size")
    private String fontSize;
    private String text;
    private String bottom;
    private List<String> hints;
    private List<RecommandBotInfo> data;
    @JsonProperty("background-color")
    private String backgroundColor;
    @JsonProperty("flex-direction")
    private String flexDirection;
    @JsonProperty("justify-content")
    private String justifyContent;
    private String looping;
    private String autoplay;
}
