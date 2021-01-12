package ai.qiwu.com.xiaoduhome.common.temple.recommand;

import lombok.Getter;
import lombok.Setter;

/**
 * @author 苗权威
 * @dateTime 19-8-27 上午10:52
 */
@Getter
@Setter
public class RecommandBotInfo {
    private String imageSrc;
    private String imageId;
    private String imageName;
    private Integer num;

    public RecommandBotInfo(String imageSrc, String imageId, String imageName) {
        this.imageSrc = imageSrc;
        this.imageId = imageId;
        this.imageName = imageName;
    }
}
