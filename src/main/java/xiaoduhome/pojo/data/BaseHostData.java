package xiaoduhome.pojo.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BaseHostData {
    private String secretKey;
    private String host;
    private String audio;
    private String cdn;
}
