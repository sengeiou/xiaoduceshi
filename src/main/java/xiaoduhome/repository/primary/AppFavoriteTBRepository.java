package xiaoduhome.repository.primary;

import ai.qiwu.com.xiaoduhome.entity.primary.AppFavoriteTB;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 苗权威
 * @dateTime 19-8-21 下午4:04
 */
@Repository
public interface AppFavoriteTBRepository extends JpaRepository<AppFavoriteTB, Long> {

    boolean existsByBotIdAndUserId(String botId, String userId);

    @Transactional
    void deleteByBotIdAndUserId(String botId, String userId);

    List<AppFavoriteTB> getByUserId(String userId);

    @Query(value = "select bot_username from app_favorite_tb where user_id=?1 order by gmt_create desc limit ?2,10", nativeQuery = true)
    List<String> getFavoriteLimit(String userId, Integer start);

    @Override
    <S extends AppFavoriteTB> long count(Example<S> example);
}
