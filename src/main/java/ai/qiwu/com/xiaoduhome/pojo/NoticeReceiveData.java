package ai.qiwu.com.xiaoduhome.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NoticeReceiveData {
    private Integer type;
    private String botAccount;
    private Long timeOrCount;
    private Boolean collect;
    private String jytUserId;
    private String xiaoduUserId;
    private String channel;
    private Boolean loaded;
}
