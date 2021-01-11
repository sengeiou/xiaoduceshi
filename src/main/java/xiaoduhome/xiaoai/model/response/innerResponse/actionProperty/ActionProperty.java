package xiaoduhome.xiaoai.model.response.innerResponse.actionProperty;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author 苗权威
 * @dateTime 19-7-5 下午8:47
 */
@Getter
@Setter
@ToString
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ActionProperty {
    private List<Long> file_id_list;

    private AppIntentInfo app_intent_info;
    private String app_h5_url;
    private String quick_app_path;
}
