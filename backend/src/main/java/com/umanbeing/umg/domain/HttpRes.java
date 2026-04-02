package com.umanbeing.umg.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Generic HTTP response class for encapsulating response data and status.
 * It is used to standardize the response format for HTTP requests and responses.
 * <p>It provides the following fields:
 * <li>code - the HTTP status code</li>
 * <li>status - the HTTP status description</li>
 * <li>message - a custom message for the response</li>
 * <li>data - optional data of any type</li>
 * </p>
 */
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

    /**
     * Creates a failed HTTP response with the given HTTP status.
     * @param httpStatus the desired HTTP status for the response
     * @return the failed HTTP response
     */
    public static <T> HttpRes<T> fail(HttpStatus httpStatus) {
        return response(httpStatus.value(), httpStatus.getReasonPhrase(), "");
    }

    /**
     * Creates a failed HTTP response with the given HTTP status and message.
     * @param httpStatus the desired HTTP status for the response
     * @param message a custom message for the response
     * @return the failed HTTP response
     */
    public static <T> HttpRes<T> fail(HttpStatus httpStatus, String message) {
        return response(httpStatus.value(), httpStatus.getReasonPhrase(), message);
    }

}


