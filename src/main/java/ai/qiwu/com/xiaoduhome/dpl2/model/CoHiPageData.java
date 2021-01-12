package ai.qiwu.com.xiaoduhome.dpl2.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * painter
 * 20-8-27 下午5:20
 */
@Getter
@Setter
@NoArgsConstructor
public class CoHiPageData {
    private String sideText;
    private String headerTitle;
    private List<CoHiOnePageData> contentList;
}
