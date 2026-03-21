package com.umanbeing.umg.domain;

import org.springframework.http.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HttpRes<T> {

    private Integer code;

    private String status;

    private String message;

    private T data;

    private static <T> HttpRes<T> response(Integer code, String status, String message, T data) {
        HttpRes<T> res = new HttpRes<>();
        res.setCode(code);
        res.setStatus(status);
        res.setMessage(message);
        res.setData(data);
        return res;
    }

    private static <T> HttpRes<T> response(Integer code, String status, String message) {
        HttpRes<T> res = new HttpRes<>();
        res.setCode(code);
        res.setStatus(status);
        res.setMessage(message);
        return res;
    }

    // TODO: Discuss if we need to customize HTTP status.
    public static <T> HttpRes<T> success() {
        return response(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), null);
    }

    public static <T> HttpRes<T> success(HttpStatus httpStatus) {
        return response(httpStatus.value(), httpStatus.getReasonPhrase(), null);
    }

    public static <T> HttpRes<T> success(String message, T data) {
        return response(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), message, data);
    }

    public static <T> HttpRes<T> fail() {
        return response(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), null);
    }

    public static <T> HttpRes<T> fail(HttpStatus httpStatus) {
        return response(httpStatus.value(), httpStatus.getReasonPhrase(), null);
    }

    public static <T> HttpRes<T> fail(String message) {
        return response(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), message);
    }

    // TODO: Discuss if we want to modify specific error codes for different types of errors (e.g. 400 for bad request, 401 for unauthorized, etc.)
    public static <T> HttpRes<T> fail(HttpStatus httpStatus, String message, T data) {
        return response(httpStatus.value(), httpStatus.getReasonPhrase(), message, data);
    }




}


