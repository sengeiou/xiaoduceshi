package xiaoduhome.repository.secondary;

import ai.qiwu.com.xiaoduhome.entity.secondary.XiaoDuOrderIdTB;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author 苗权威
 * @dateTime 19-8-22 下午4:28
 */
public interface XiaoDuOrderTBIdRepository extends JpaRepository<XiaoDuOrderIdTB, Long> {
    @Override
    <S extends XiaoDuOrderIdTB> S save(S s);
}