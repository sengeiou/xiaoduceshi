package ai.qiwu.com.xiaoduhome.common.temple.rank;

import ai.qiwu.com.xiaoduhome.pojo.dpl.Event;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.util.List;

/**
 * @author 苗权威
 * @dateTime 19-8-27 上午10:13
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class RankingPojo {
    private String type;
    private String height;
    private String width;
    private List<RankingPojo> items;
    private String position;
    private String top;
    private String left;
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
    @JsonProperty("font-size")
    private String fontSize;
    private String text;
    private String bottom;
    private List<String> hints;
    private List<RankBotInfo> data;
    @JsonProperty("flex-direction")
    private String flexDirection;
    @JsonProperty("max-lines")
    private Integer maxLines;
    @JsonProperty("margin-right")
    private String marginRight;
    @JsonProperty("text-overflow")
    private String textOverflow;
    @JsonProperty("background-color")
    private String backgroundColor;

}
