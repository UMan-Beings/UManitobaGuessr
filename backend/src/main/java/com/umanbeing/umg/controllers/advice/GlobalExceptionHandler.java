package com.umanbeing.umg.controllers.advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.NoHandlerFoundException;
import jakarta.servlet.http.HttpServletRequest;

import com.umanbeing.umg.domain.HttpRes;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Enumerate all exceptions that we want to handle globally here, and return appropriate HTTP responses.

    // Authentication exceptions
    @ExceptionHandler(AccessDeniedException.class)
    public HttpRes<Void> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        logger.error("Access denied to {}: {}", requestUri, e.getMessage());
        return HttpRes.fail(HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public HttpRes<Void> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        logger.error("Method not allowed for {}: {}", requestUri, e.getMessage());
        return HttpRes.fail(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public HttpRes<Void> handleNoHandlerFoundException(NoHandlerFoundException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        logger.error("No handler found for {}: {}", requestUri, e.getMessage());
        return HttpRes.fail(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public HttpRes<Void> handleGenericException(Exception e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        logger.error("An error occurred while processing {}: {}", requestUri, e.getMessage());
        return HttpRes.fail(HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
