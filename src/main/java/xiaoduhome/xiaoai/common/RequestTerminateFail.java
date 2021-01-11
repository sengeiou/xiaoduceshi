package xiaoduhome.xiaoai.common;

/**
 * 自定义异常类，代表请求终端出错
 * @author 苗权威
 * @dateTime 19-8-10 下午3:11
 */
public class RequestTerminateFail extends RuntimeException {
    /**
     * 错误码
     */
    private int errorCode;
    /**
     * 错误信息
     */
    private String errorMsg;

    public RequestTerminateFail(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public RequestTerminateFail(String message) {
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
