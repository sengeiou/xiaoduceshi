package xiaoduhome.pojo.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

/**
 * @author 苗权威
 * @dateTime 20-1-18 上午11:16
 */
@Getter
@Setter
@ToString
public class ResultData {
    private List<ProjectData> bots;
    private Map<String, Map<String, Long>> channelAcc;
    private Map<String, Map<String, String>> headImg;
}
