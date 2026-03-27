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

    private static <T> HttpRes<T> response(Integer code, String status, String message) {
        HttpRes<T> res = new HttpRes<>();
        res.setCode(code);
        res.setStatus(status);
        res.setMessage(message);
        return res;
    }
    public static <T> HttpRes<T> fail(HttpStatus httpStatus) {
        return response(httpStatus.value(), httpStatus.getReasonPhrase(), "");
    }

    public static <T> HttpRes<T> fail(HttpStatus httpStatus, String message) {
        return response(httpStatus.value(), httpStatus.getReasonPhrase(), message);
    }

}


