package ai.qiwu.com.xiaoduhome.repository.primary;

import ai.qiwu.com.xiaoduhome.entity.primary.AppUserTB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 苗权威
 * @dateTime 19-8-19 下午7:29
 */
@Repository
public interface AppUserTBRepository extends JpaRepository<AppUserTB, String> {
    AppUserTB getByPhone(String phone);

    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "INSERT INTO app_user_tb (user_phone,gmt_create) VALUES (?1, now())", nativeQuery = true)
    void insertUser(String phone);
}
