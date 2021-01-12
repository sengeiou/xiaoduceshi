package ai.qiwu.com.xiaoduhome.entity.primary;

import lombok.Data;

import javax.persistence.*;

/**
 * @author 苗权威
 * @dateTime 19-8-22 下午5:12
 */
@Entity
@Table(name = "app_flower_tb")
@Data
public class AppFlowerTBTiny {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "unit_price")
    private Double unitPrice;

    @Column(name = "bot_username")
    private String botId;

    @Column
    private Long number;
}
