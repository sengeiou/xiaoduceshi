package ai.qiwu.com.xiaoduhome.dpl2.model;

import ai.qiwu.com.xiaoduhome.pojo.DialogData;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * painter
 * 20-8-29 下午3:17
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class CentralResponseVO {
    private String botStatus;
    private String aipioneerUsername;
    private String plotImgUrl;
    private List<DialogData> dialogs;
    private List<DialogData> commands;
}
