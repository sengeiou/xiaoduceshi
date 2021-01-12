package ai.qiwu.com.xiaoduhome.pojo;

import ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.directive.dpl.commands.BaseCommand;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 苗权威
 * @dateTime 19-8-10 下午6:24
 */
@JsonIgnoreProperties(
        ignoreUnknown = true
)
@Getter
@Setter
public class MyParallelCommand extends BaseCommand {
    private long delayInMilliseconds;
    private List<BaseCommand> commands = new ArrayList<>();

    public MyParallelCommand() {
        super("Parallel");
    }

    public MyParallelCommand addCommand(BaseCommand command) {
        this.commands.add(command);
        return this;
    }
}
