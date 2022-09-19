package com.shanlu.common;

public class ResponseBody<T> {
    private String code;
    private String message;
    private T data;
    private Object extra;

    public ResponseBody() {
    }

    public boolean isSuccess() {
        return this.code.equals("SUCCESS");
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Object getExtra() {
        return extra;
    }

    public void setExtra(Object extra) {
        this.extra = extra;
    }

}
