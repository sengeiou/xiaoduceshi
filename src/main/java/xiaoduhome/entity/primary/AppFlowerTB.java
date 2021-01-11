package xiaoduhome.entity.primary;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author 苗权威
 * @dateTime 19-8-19 下午8:27
 */
@Entity
@Table(name = "app_flower_tb")
@EntityListeners(AuditingEntityListener.class)
@Data
public class AppFlowerTB {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Integer module;

    @Column(name = "bot_username")
    private String botName;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "unit_price")
    private Double unitPrice;

    @Column
    private Long number;

    @Column(name = "order_id")
    private Long orderId;

    @Column
    private Integer status;

    @Column(name = "channel_id")
    private String channelId;

    @Column(name = "workname")
    private String workName;

    @CreatedDate
    @Column(name = "gmt_create", updatable = false, nullable = false)
    private Timestamp createTime;

    @LastModifiedDate
    @Column(name = "gmt_modified", nullable = false)
    private Timestamp updateTime;
}
