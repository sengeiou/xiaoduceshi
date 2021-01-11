package xiaoduhome.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 苗权威
 * @dateTime 20-1-17 下午7:45
 */
@Getter
@Setter
@ToString
public class SendFlowerVO {
    private String botAccount;
    private Long num;
    private Double unitPrice;
    private String userId;
    private String name;
    private String channelId;
    private Long orderId;
}
