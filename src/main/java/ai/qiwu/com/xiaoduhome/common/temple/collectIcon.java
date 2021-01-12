package ai.qiwu.com.xiaoduhome.common.temple;

import ai.qiwu.com.xiaoduhome.pojo.dpl.Event;
import ai.qiwu.com.xiaoduhome.pojo.dpl.Item;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author 苗权威
 * @dateTime 19-8-24 下午1:57
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class collectIcon {
    private String type;
    private String width;
    private String height;
    private String src;
    @JsonProperty("scale-type")
    private String scaleType;
    private String text;
    @JsonProperty("margin-left")
    private String marginLeft;
    @JsonProperty("align-items")
    private String alignItems;
    @JsonProperty("justify-content")
    private String justify;
    private List<Event> onClick;
    private List<Item> items;
    @JsonProperty("margin-bottom")
    private String marginBo;
    private String componentId;
    @JsonProperty("margin-top")
    private String marginTop;
    @JsonProperty("font-size")
    private String fontSize;
    @JsonProperty("letter-spacing")
    private String LetterSpace;
}
