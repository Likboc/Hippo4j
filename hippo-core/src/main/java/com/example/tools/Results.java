package com.example.tools;

import com.example.entity.Result;
import com.example.exception.AbstractException;

import java.util.Optional;

public final class Results {

    public static Result<Void> success() {
        return new Result<Void>()
                .setCode(Result.SUCCESS_CODE);
    }

    public static <T> Result<T> success(T data) {
        return new Result<T>()
                .setCode(Result.SUCCESS_CODE)
                .setData(data);
    }

    public static Result<Void> failure() {
        return failure(ErrorCodeEnum.SERVICE_ERROR.getCode(), ErrorCodeEnum.SERVICE_ERROR.getMessage());
    }

    public static Result<Void> failure(AbstractException abstractException) {
        String errorCode = Optional.ofNullable(abstractException.getErrorCode())
                .map(ErrorCode::getCode)
                .orElse(ErrorCodeEnum.SERVICE_ERROR.getCode());

        return new Result<Void>().setCode(errorCode)
                .setMessage(abstractException.getMessage());
    }

    public static Result<Void> failure(Throwable throwable) {
        return new Result<Void>().setCode(ErrorCodeEnum.SERVICE_ERROR.getCode())
                .setMessage(throwable.getMessage());
    }

    public static Result<Void> failure(ErrorCode errorCode) {
        return failure(errorCode.getCode(), errorCode.getMessage());
    }

    public static Result<Void> failure(String code, String message) {
        return new Result<Void>()
                .setCode(code)
                .setMessage(message);
    }
}
