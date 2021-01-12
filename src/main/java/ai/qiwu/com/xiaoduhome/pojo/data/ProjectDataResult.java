package ai.qiwu.com.xiaoduhome.pojo.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 苗权威
 * @dateTime 20-1-18 上午11:15
 */
@Getter
@Setter
@ToString
public class ProjectDataResult {
    private Integer code;
    private String msg;
    private ResultData data;
}
