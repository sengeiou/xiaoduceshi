package xiaoduhome.entity.secondary;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author 苗权威
 * @dateTime 19-8-22 下午3:45
 */
@Entity
@Table(name = "xiaodu_order_tb")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@DynamicUpdate
public class XiaoDuOrderTB {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "work_name")
    private String workName;

    @Column(name = "channel_id")
    private String channelId;

    @Column(name = "app_user_id")
    private String appUserId;

    @Column(name = "xiaodu_user_id")
    private String xiaoduUserId;

    @Column(name = "unit_price")
    private Integer unitPrice;

    @Column
    private Long number;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "time_stamp")
    private Long timeStamp;

    @Column(name = "is_buy")
    private Boolean isBuy;

    @Column(name = "ch_order_id")
    private String chOrderId;

    @Column(name = "creation_timestamp")
    private String creationTimestamp;

    @Column(name = "status")
    private Boolean status;

    @Column
    private Integer device;

    @CreatedDate
    @Column(name = "create_time", updatable = false, nullable = false)
    private Timestamp createTime;

    @LastModifiedDate
    @Column(name = "update_time", nullable = false)
    private Timestamp updateTime;
}
