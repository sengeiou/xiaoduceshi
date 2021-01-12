package ai.qiwu.com.xiaoduhome.dpl2.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

/**
 * painter
 * 20-8-27 下午8:31
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectDetailData {
    private String imgUrl;
    private String workName;
    private String time;
    private String label;
    private String author;
    private String intro;
    private String id;
    private String flowerId;
//    private String collectId;
//    private String collectImg;
//    private String collectText;
    private Boolean collect;
    private Boolean login;
    private String flowText;
}
