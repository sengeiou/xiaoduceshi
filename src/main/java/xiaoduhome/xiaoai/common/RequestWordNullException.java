package xiaoduhome.xiaoai.common;

/**
 * 自定义异常类，代表无法获取用户的话
 * @author 苗权威
 * @dateTime 19-8-10 下午3:15
 */
public class RequestWordNullException extends RuntimeException {
    /**
     * 错误码
     */
    private int errorCode;
    /**
     * 错误信息
     */
    private String errorMsg;

    public RequestWordNullException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public RequestWordNullException(String message) {
        super(message);
        this.errorMsg = errorMsg;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
