package ai.qiwu.com.xiaoduhome.entity.secondary;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @author 苗权威
 * @dateTime 19-8-22 下午4:23
 */
@Entity
@Table(name = "xiaodu_order_tb")
@Getter
@Setter
public class XiaoDuOrderIdTB {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bot_account")
    private String botId;

    @Column(name = "xiaodu_user_id")
    private String xiaoduUserId;

    @Column(name = "unit_price")
    private Integer unitPrice;

    @Column
    private Long number;
}
