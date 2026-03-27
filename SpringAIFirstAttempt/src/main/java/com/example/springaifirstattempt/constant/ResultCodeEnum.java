package com.example.springaifirstattempt.constant;

import lombok.Getter;

/**
 * 响应状态码枚举
 */
@Getter
public enum ResultCodeEnum {
    SUCCESS(200, "操作成功"),
    ERROR(500, "操作失败");

    private final Integer code;
    private final String message;

    ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
