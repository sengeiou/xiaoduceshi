package xiaoduhome.outside.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * painter
 * 20-7-1 下午5:40
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AudioInfo {
    private Long infoId;
    private String firstWord;
    private String lastWord;
    private String xiaoaiSecret;
    private String xiaoaiKeyId;
    private String publicKey;
    private String botAccount;
    private String privateKey;
}
