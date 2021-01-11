package com.baidu.dueros.data.response.directive.dpl.commands;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * painter
 * 20-8-29 上午10:28
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScrollToElementCommand extends BaseCommand {

    private Integer delay;

    private String dWhen;

    private Integer duration;

    private String targetComponentId;

    private ScrollToIndexCommand.AlignType align;

    public ScrollToElementCommand() {
        super("ScrollToElement");
    }

    public Integer getDelay() {
        return delay;
    }

    public ScrollToElementCommand setDelay(Integer delay) {
        this.delay = delay;
        return this;
    }

    public String getdWhen() {
        return dWhen;
    }

    public ScrollToElementCommand setdWhen(String dWhen) {
        this.dWhen = dWhen;
        return this;
    }

    public Integer getDuration() {
        return duration;
    }

    public ScrollToElementCommand setDuration(Integer duration) {
        this.duration = duration;
        return this;
    }

    public String getTargetComponentId() {
        return targetComponentId;
    }

    public ScrollToElementCommand setTargetComponentId(String targetComponentId) {
        this.targetComponentId = targetComponentId;
        return this;
    }

    public ScrollToIndexCommand.AlignType getAlign() {
        return align;
    }

    public ScrollToElementCommand setAlign(ScrollToIndexCommand.AlignType align) {
        this.align = align;
        return this;
    }
}
