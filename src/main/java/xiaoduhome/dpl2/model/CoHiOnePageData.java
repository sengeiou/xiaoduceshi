package xiaoduhome.dpl2.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * painter
 * 20-8-27 下午5:21
 */
@Getter
@Setter
@NoArgsConstructor
public class CoHiOnePageData {
    private String id;
    private Integer num;
    private String imageUrl;
    private String workName;
    private String workIntro;
    private String time;
    private Integer itemSeqId;
}
