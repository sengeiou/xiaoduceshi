package xiaoduhome.common.temple.playUpdate;

import ai.qiwu.com.xiaoduhome.common.temple.recommand.RecommandBotInfo;
import ai.qiwu.com.xiaoduhome.common.temple.recommand.RecommandPojo;
import ai.qiwu.com.xiaoduhome.pojo.dpl.Event;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.util.List;

/**
 * @author 苗权威
 * @dateTime 19-8-28 下午6:31
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PlayUpdatePojo {
    private String type;
    private String height;
    private String width;
    private String componentId;
    private List<PlayUpdatePojo> items;
    private String padding;
    private String position;
    private String bottom;
    private String text;
    @JsonProperty("font-size")
    private String fontSize;
    @JsonProperty("font-style")
    private String fontStyle;
    private String looping;
    private String autoplay;
    private String src;
    private List<Event> onEnd;
}
