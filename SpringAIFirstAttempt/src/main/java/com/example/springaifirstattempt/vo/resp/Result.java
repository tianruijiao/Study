package com.example.springaifirstattempt.vo.resp;

import com.example.springaifirstattempt.constant.ResultCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 提示信息
     */
    private String msg;

    /**
     * 业务数据
     */
    private T data;

    /**
     * 成功响应（无数据）
     */
    public static <T> Result<T> success() {
        return new Result<>(ResultCodeEnum.SUCCESS.getCode(),
                ResultCodeEnum.SUCCESS.getMessage(),
                null);
    }

    /**
     * 成功响应（带数据）
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCodeEnum.SUCCESS.getCode(),
                ResultCodeEnum.SUCCESS.getMessage(),
                data);
    }

    /**
     * 成功响应（自定义消息，带数据）
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(ResultCodeEnum.SUCCESS.getCode(), message, data);
    }

    /**
     * 失败响应
     */
    public static <T> Result<T> error() {
        return new Result<>(ResultCodeEnum.ERROR.getCode(),
                ResultCodeEnum.ERROR.getMessage(),
                null);
    }

    /**
     * 失败响应（自定义消息）
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(ResultCodeEnum.ERROR.getCode(), message, null);
    }

    /**
     * 失败响应（自定义状态码和消息）
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    /**
     * 失败响应（使用ResultCodeEnum枚举）
     */
    public static <T> Result<T> error(ResultCodeEnum resultCode) {
        return new Result<>(resultCode.getCode(),
                resultCode.getMessage(),
                null);
    }

    /**
     * 失败响应（自定义状态码、消息和数据）
     */
    public static <T> Result<T> error(Integer code, String message, T data) {
        return new Result<>(code, message, data);
    }
}
