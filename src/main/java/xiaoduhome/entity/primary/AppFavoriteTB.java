package xiaoduhome.entity.primary;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author 苗权威
 * @dateTime 19-8-21 下午4:00
 */
@Entity
@Table(name = "app_favorite_tb")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class AppFavoriteTB {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "work_name")
    private String botId;

    @Column(name = "user_id")
    private String userId;

    @Column
    private Integer module;

    @CreatedDate
    @Column(name = "gmt_create", updatable = false, nullable = false)
    private Timestamp createTime;

    @LastModifiedDate
    @Column(name = "gmt_modified", nullable = false)
    private Timestamp updateTime;
}
