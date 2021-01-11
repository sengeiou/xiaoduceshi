package xiaoduhome.entity.primary;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author 苗权威
 * @dateTime 19-8-19 下午4:20
 */
@Entity
@Table(name = "app_user_tb")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class AppUserTB {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "info_id")
    private String infoId;

    @Column(name = "user_phone")
    private String phone;

    @CreatedDate
    @Column(name = "gmt_create", updatable = false, nullable = false)
    private Timestamp createTime;
}
