package ai.qiwu.com.xiaoduhome.common.temple.rank;

import lombok.Data;

/**
 * @author 苗权威
 * @dateTime 19-8-27 上午10:35
 */
@Data
public class RankBotInfo {
    private String imageSrc;
    private String imageId;
    private String imageName;
    private Integer num;

    public RankBotInfo() {
    }

    public RankBotInfo(String imageSrc, String imageId, String imageName) {
        this.imageSrc = imageSrc;
        this.imageId = imageId;
        this.imageName = imageName;
    }
}
