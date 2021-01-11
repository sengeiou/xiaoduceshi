package xiaoduhome.repository.secondary;

import ai.qiwu.com.xiaoduhome.entity.secondary.XiaoDuCollectTB;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface XiaoDuCollectRepository extends JpaRepository<XiaoDuCollectTB, Long> {

    List<XiaoDuCollectTB> getByXiaoduUserIdAndChannelId(String xiaoduUserId, String channelId);

    XiaoDuCollectTB getByXiaoduUserIdAndChannelIdAndWorkName(String xiaoduUserId, String channelId, String workName);
}
