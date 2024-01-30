package com.example.entity;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class Result<T> implements Serializable {
    private static final long serialVersionUID = -4408341719434417427L;

    public static final String SUCCESS_CODE = "0";

    private String code;
    private String message;
    private T data;

    public boolean isSuccess() {
        return SUCCESS_CODE.equals(code);
    }
}
