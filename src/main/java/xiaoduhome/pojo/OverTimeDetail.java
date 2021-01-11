package xiaoduhome.pojo;

import com.baidu.dueros.data.response.directive.Directive;
import com.baidu.dueros.model.Response;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 苗权威
 * @dateTime 19-8-28 下午3:22
 */
@Getter
@Setter
@ToString
public class OverTimeDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<Directive> directives = new ArrayList<>();
    private final Map<String, String> attributes = new HashMap<>();
    private volatile Response response;
}
