package ai.qiwu.com.xiaoduhome.pojo.dpl;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 苗权威
 * @dateTime 19-8-15 下午6:20
 */
@Getter
@Setter
@ToString
public class Data {
    private String imageSrc;
    private String imageName;
    private String imageId;
    private Integer num;

    public Data() {
    }

    public Data(String imageSrc, String imageName, String imageId) {
        this.imageSrc = imageSrc;
        this.imageName = imageName;
        this.imageId = imageId;
    }
}
