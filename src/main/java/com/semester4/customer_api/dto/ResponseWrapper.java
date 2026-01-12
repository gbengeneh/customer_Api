package com.semester4.customer_api.dto;

import java.io.Serializable;

public class ResponseWrapper<T> implements Serializable {
    private T data;
    private String message;

    public ResponseWrapper(T data){
        this.data = data;
    }

    public ResponseWrapper(String message){
        this.message = message;
    }

    public ResponseWrapper(T data, String message){
        this.data = data;
        this.message = message;
    }
}
