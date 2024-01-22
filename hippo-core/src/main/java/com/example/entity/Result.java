package com.example.entity;

import java.io.Serial;
import java.io.Serializable;

public class Result<T> implements Serializable {
    private static final long serialVersionUID = -4408341719434417427L;
    private String code;
    private String message;
    private T data;
}
