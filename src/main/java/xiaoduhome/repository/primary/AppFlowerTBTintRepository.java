package xiaoduhome.repository.primary;

import ai.qiwu.com.xiaoduhome.entity.primary.AppFlowerTBTiny;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 苗权威
 * @dateTime 19-8-22 下午5:18
 */
@Repository
public interface AppFlowerTBTintRepository extends JpaRepository<AppFlowerTBTiny, Long> {
    List<AppFlowerTBTiny> getByUserIdAndBotId(String userId, String botId);

    @Query(value = "select number from app_flower_tb where user_id=?1 and bot_username=?2", nativeQuery = true)
    List<Long> getFlowerNumByUserIdAndBotAccount(String userId, String botAccount);
}
