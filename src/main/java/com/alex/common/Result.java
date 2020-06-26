package com.alex.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wsh
 * @date 2020-06-26
 */
@Data
public class Result implements Serializable {

    //0表示成功，-1表示失败
    private Integer status;
    private String msg;
    private Object data;
    private String action;

    public static Result success(String msg, Object data) {
        Result result = new Result();
        result.setStatus(0);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    public static Result success(Object data) {
        return success("操作成功", data);
    }

    public static Result success() {
        return success("操作成功", null);
    }

    public static Result fail(String msg) {
        Result result = new Result();
        result.setStatus(-1);
        result.setMsg(msg);
        result.setData(null);
        return result;
    }

    public Result action(String action){
        this.action = action;
        return this;
    }

}
