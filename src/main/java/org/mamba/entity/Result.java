package org.mamba.entity;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Result() {
    }

    public static <E> Result<E> success(E data) {
        return new Result<>(200, "successfully", data);
    }

    public static Result success() {
        return new Result<>(200, "successfully", null);
    }

    public static Result error(String message) {
        return new Result(500, message, null);
    }
}
