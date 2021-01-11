package xiaoduhome.common.temple.onLunch;

import lombok.Getter;
import lombok.Setter;

/**
 * @author 苗权威
 * @dateTime 19-9-5 下午5:05
 */
@Getter
@Setter
public class LunchBotData {
    private String imageSrc;
    private String imageId;
    private String imageName;
    private Integer num;

    public LunchBotData(String imageSrc, String imageId, String imageName) {
        this.imageSrc = imageSrc;
        this.imageId = imageId;
        this.imageName = imageName;
    }
}
