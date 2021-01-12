package ai.qiwu.com.xiaoduhome.baidu.dueros.data.request.events;

import ai.qiwu.com.xiaoduhome.baidu.dueros.data.request.RequestBody;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author 苗权威
 * @dateTime 20-1-20 下午6:14
 */
@JsonTypeName("StreamResponse.NextRequired")
@JsonIgnoreProperties(ignoreUnknown = true)
public class NextRequired extends RequestBody {

    private String requestId;
    private String timestamp;
    private String token;

    protected NextRequired(@JsonProperty("requestId") final String requestId,
                           @JsonProperty("timestamp") final String timestamp,
                           @JsonProperty("dialogRequestId") final String dialogRequestId) {
        super(requestId, timestamp, dialogRequestId);
    }

    @Override
    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    @Override
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
