package xiaoduhome.pojo.pageData;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author 苗权威
 * @dateTime 19-8-27 下午6:02
 */
@Getter
@Setter
@AllArgsConstructor
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class HistoryData {
    private String botAccountId;
    private Integer collectPageLeftNum;
    private String bannerImgUrl;
    private String name;
    private String intro;
    private String lastTime;
    private String authorName;

    public HistoryData() {
    }

    public HistoryData(String botAccountId, Integer collectPageLeftNum,
                       String bannerImgUrl, String name, String intro) {
        this.botAccountId = botAccountId;
        this.collectPageLeftNum = collectPageLeftNum;
        this.bannerImgUrl = bannerImgUrl;
        this.name = name;
        this.intro = intro;
    }
}
