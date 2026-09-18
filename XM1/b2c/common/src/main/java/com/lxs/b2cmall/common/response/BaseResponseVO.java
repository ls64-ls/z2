package com.lxs.b2cmall.common.response;

import java.io.Serializable;

public class BaseResponseVO<T> implements Serializable {

    private Integer status;
    private String message;
    private T data;

    public BaseResponseVO() {
    }

    public BaseResponseVO(Integer status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public static <T> BaseResponseVO<T> success(T data) {
        BaseResponseVO<T> vo = new BaseResponseVO<>();
        vo.setStatus(200);
        vo.setMessage("success");
        vo.setData(data);
        return vo;
    }

    public static <T> BaseResponseVO<T> success(String message, T data) {
        BaseResponseVO<T> vo = new BaseResponseVO<>();
        vo.setStatus(200);
        vo.setMessage(message);
        vo.setData(data);
        return vo;
    }

    public static <T> BaseResponseVO<T> success() {
        BaseResponseVO<T> vo = new BaseResponseVO<>();
        vo.setStatus(200);
        vo.setMessage("success");
        return vo;
    }

    public static <T> BaseResponseVO<T> fail(Integer status, String message) {
        BaseResponseVO<T> vo = new BaseResponseVO<>();
        vo.setStatus(status);
        vo.setMessage(message);
        return vo;
    }

    public static <T> BaseResponseVO<T> fail(String message) {
        BaseResponseVO<T> vo = new BaseResponseVO<>();
        vo.setStatus(500);
        vo.setMessage(message);
        return vo;
    }
}
