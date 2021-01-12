package ai.qiwu.com.xiaoduhome.repository.secondary;

import ai.qiwu.com.xiaoduhome.entity.secondary.XiaoDuHistoryBotIdTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author 苗权威
 * @dateTime 19-8-27 下午5:30
 */
public interface XiaoDuHistoryBotTimeRepository extends JpaRepository<XiaoDuHistoryBotIdTime, Long> {
    List<XiaoDuHistoryBotIdTime> getByXiaoduUserId(String xiaoduUserId);

    @Query(value = "select bot_id,last_time from xiaodu_history_tb where xiaodu_user_id=?1 order by last_time desc limit ?2,10",nativeQuery = true)
    List<XiaoDuHistoryBotIdTime> getByXiaoduUserIdLimit(String xiaoduUserId, Integer start);
}
