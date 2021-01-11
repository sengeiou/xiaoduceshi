package xiaoduhome.entity.secondary;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @author 苗权威
 * @dateTime 19-8-27 下午5:28
 */
@Entity
@Table(name = "xiaodu_history_tb")
@Getter
@Setter
public class XiaoDuHistoryBotIdTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bot_account")
    private String botId;

    @Column(name = "last_time")
    private Long lastTime;

    @Column(name = "xiaodu_user_id")
    private String xiaoduUserId;
}
