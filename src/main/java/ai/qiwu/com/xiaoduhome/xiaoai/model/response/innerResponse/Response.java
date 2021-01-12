package ai.qiwu.com.xiaoduhome.xiaoai.model.response.innerResponse;

import ai.qiwu.com.xiaoduhome.xiaoai.model.response.innerResponse.actionProperty.ActionProperty;
import ai.qiwu.com.xiaoduhome.xiaoai.model.response.innerResponse.directive.Directive;
import ai.qiwu.com.xiaoduhome.xiaoai.model.response.innerResponse.registerEvent.RegisterEvent;
import ai.qiwu.com.xiaoduhome.xiaoai.model.response.innerResponse.toDisplay.ToDisplay;
import ai.qiwu.com.xiaoduhome.xiaoai.model.response.innerResponse.toSpeak.ToSpeak;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author 苗权威
 * @dateTime 19-7-5 下午8:31
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {
    private ToSpeak to_speak;
    private ToDisplay to_display;
    private List<Directive> directives;
    private Boolean open_mic;
    private Boolean not_understand;
    private String action;
    private ActionProperty action_property;
    private List<RegisterEvent> register_events;
}
