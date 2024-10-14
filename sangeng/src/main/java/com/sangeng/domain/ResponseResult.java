package com.sangeng.domain;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 后端统一返回结果
 * @param <T>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseResult<T> {

    private Integer code; //编码：1成功，0和其它数字为失败
    private String msg; //错误信息
    private T data; //数据设置为泛型，方便存储不同类型的数据

    public ResponseResult(Integer code, String msg){
        this.code = code;
        this.msg = msg;
    }
    public ResponseResult(Integer code, T data){
        this.code = code;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
    public ResponseResult(Integer code, String msg, T data){
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

}
