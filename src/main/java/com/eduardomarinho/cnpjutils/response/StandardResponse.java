package com.eduardomarinho.cnpjutils.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StandardResponse<T> {
    private T data;
    private String message;
    private boolean success;

    public StandardResponse(T data, String message, boolean success) {
        this.data = data;
        this.message = message;
        this.success = success;
    }

    public static <T> StandardResponse<T> success(T data, String message) {
        return new StandardResponse<>(data, message, true);
    }

    public static <T> StandardResponse<T> error(String message) {
        return new StandardResponse<>(null, message, false);
    }
}