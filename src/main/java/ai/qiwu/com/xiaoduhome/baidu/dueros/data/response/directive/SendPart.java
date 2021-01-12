package ai.qiwu.com.xiaoduhome.baidu.dueros.data.response.directive;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author 苗权威
 * @dateTime 20-1-20 下午6:21
 */
@JsonTypeName("StreamResponse.SendPart")
public class SendPart extends Directive {
    private String token;

    public SendPart(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
