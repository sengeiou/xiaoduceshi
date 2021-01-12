package ai.qiwu.com.xiaoduhome.pojo.playAside;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author 苗权威
 * @dateTime 19-9-10 下午6:17
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AsideImage {
    private String authorName;
    private String worksBannerImgUrl;
    private Map<String,String> npcHeadImgUrl;
}
