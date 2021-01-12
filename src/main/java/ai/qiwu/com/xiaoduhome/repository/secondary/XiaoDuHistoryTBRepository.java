package ai.qiwu.com.xiaoduhome.repository.secondary;

import ai.qiwu.com.xiaoduhome.entity.secondary.XiaoDuHistoryTB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 苗权威
 * @dateTime 19-8-26 下午2:21
 */
public interface XiaoDuHistoryTBRepository extends JpaRepository<XiaoDuHistoryTB, Long> {
    @Query(value = "SELECT DISTINCT xiaodu_user_id FROM xiaodu_history_tb", nativeQuery = true)
    List<String> getAllUser();

//    @Modifying
//    @Transactional(rollbackFor = Exception.class)
//    @Query(value = "delete from xiaodu_history_tb where xiaodu_user_id=?1 and last_time < (select last_time from (select last_time from xiaodu_history_tb where xiaodu_user_id=?1 order by last_time limit 15,1) t1)", nativeQuery = true)
//    void deleteOverTime(String userId);

    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "delete from xiaodu_history_tb where last_time <?1", nativeQuery = true)
    void deletePasted(Long boundTime);

    @Override
    <S extends XiaoDuHistoryTB> S save(S s);

    XiaoDuHistoryTB getByXiaoduUserIdAndChannelIdAndWorkName(String xiaoduUserId, String channelId, String workName);

//    @Query(value = "select bot_id from xiaodu_history_tb where xiaodu_user_id=?1",nativeQuery = true)
//    List<String> getUserHostory(String userId);

    List<XiaoDuHistoryTB> getByXiaoduUserId(String userId);

    List<XiaoDuHistoryTB> getByXiaoduUserIdAndChannelId(String xiaoduUserId, String channelId);
}
