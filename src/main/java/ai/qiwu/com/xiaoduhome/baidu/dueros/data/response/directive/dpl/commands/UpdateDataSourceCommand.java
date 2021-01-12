package ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.directive.dpl.commands;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * painter
 * 20-8-26 上午10:46
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateDataSourceCommand extends BaseCommand {

    // 指令在端上的延迟执行时间，单位毫秒，默认为 0
    private Integer delay = 0;

    // 指令的条件执行表达式
    private String dWhen = "";

    // 与 dataSource 保持相同结构的需要更新的数据内容，该指令被执行时，data 内容会被 merge 更新到原 dataSource 中
    private Object data;

    public UpdateDataSourceCommand() {
        super("UpdateDataSource");
    }

    public Integer getDelay() {
        return delay;
    }

    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    public String getdWhen() {
        return dWhen;
    }

    public void setdWhen(String dWhen) {
        this.dWhen = dWhen;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
