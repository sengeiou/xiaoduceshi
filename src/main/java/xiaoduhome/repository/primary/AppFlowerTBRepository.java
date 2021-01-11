package xiaoduhome.repository.primary;

import ai.qiwu.com.xiaoduhome.entity.primary.AppFlowerTB;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author 苗权威
 * @dateTime 19-8-19 下午8:36
 */
@Repository
public interface AppFlowerTBRepository extends JpaRepository<AppFlowerTB, Long> {
    @Override
    <S extends AppFlowerTB> S save(S s);

    @Override
    <S extends AppFlowerTB> boolean exists(Example<S> example);


}
