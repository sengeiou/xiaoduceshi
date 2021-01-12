package ai.qiwu.com.xiaoduhome.repository.secondary;

import ai.qiwu.com.xiaoduhome.entity.secondary.XiaoDuOrderTB;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 苗权威
 * @dateTime 19-8-22 下午3:59
 */
public interface XiaoDuOrderTbRepository extends JpaRepository<XiaoDuOrderTB, Long> {
    @Override
    <S extends XiaoDuOrderTB> boolean exists(@NotNull Example<S> example);

    @NotNull
    @Override
    <S extends XiaoDuOrderTB> S save(@NotNull S s);

    @NotNull
    @Override
    <S extends XiaoDuOrderTB> S saveAndFlush(@NotNull S s);

    XiaoDuOrderTB getByChOrderId (String chOrderId);

    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "UPDATE xiaodu_order_tb SET order_id=?1,status=1,creation_timestamp=?3,update_time=now() WHERE ch_order_id=?2",nativeQuery = true)
    void updateAterSeccessPayed(String orderId,String chOrderId, String createTimestamp);

    @Modifying
    @Transactional(rollbackFor = Exception.class)
    void deleteByWorkNameAndChOrderId(String workName, String chOrderId);

    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "DELETE FROM xiaodu_order_tb WHERE status=0 AND time_stamp<?1",nativeQuery = true)
    void deleteOverTimeOrder(Long bound);

    List<XiaoDuOrderTB> getByXiaoduUserIdAndChannelIdAndStatus(String xiaoduUserId, String channelId, Boolean status);
}
