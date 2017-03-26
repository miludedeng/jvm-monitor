package cc.cafetime.entity;

/**
 * Created by liujing on 2017/2/18.
 */
public class ResponseData {

    private String status; //状态
    private Object data; //数据
    private String message; // 信息

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
