package com.umanbeing.umg.controllers.advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import com.umanbeing.umg.domain.HttpRes;
import org.springframework.http.ResponseEntity;

@RestControllerAdvice
public class GlobalExceptionHandler implements HandlerExceptionResolver {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<HttpRes<Void>> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        logger.error("Access denied to {}: {}", requestUri, e.getMessage());
        HttpRes<Void> response = HttpRes.fail(HttpStatus.FORBIDDEN);
        return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(response);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<HttpRes<Void>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        logger.error("Method not allowed for {}: {}", requestUri, e.getMessage());
        HttpRes<Void> response = HttpRes.fail(HttpStatus.METHOD_NOT_ALLOWED);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED.value()).body(response);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<HttpRes<Void>> handleNoHandlerFoundException(NoHandlerFoundException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        logger.error("No handler found for {}: {}", requestUri, e.getMessage());
        HttpRes<Void> response = HttpRes.fail(HttpStatus.NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(response);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<HttpRes<Void>> handleNoResourceFoundException(NoResourceFoundException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        logger.error("No resource found for {}: {}", requestUri, e.getMessage());
        HttpRes<Void> response = HttpRes.fail(HttpStatus.NOT_FOUND, e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(response);
    }


    // MethodArgumentNotValidException is thrown when @Valid validation on request body fails
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<HttpRes<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        logger.error("Validation failed for {}: {}", requestUri, e.getMessage());
        HttpRes<Void> response = HttpRes.fail(HttpStatus.BAD_REQUEST, "Argument validation failed");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<HttpRes<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        logger.error("Malformed JSON request for {}: {}", requestUri, e.getMessage());
        HttpRes<Void> response = HttpRes.fail(HttpStatus.BAD_REQUEST, "Malformed JSON request");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(response);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<HttpRes<Void>> handleResponseStatusException(ResponseStatusException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        logger.error("Response status exception for {}: {}", requestUri, e.getMessage());
        int statusCodeValue = e.getStatusCode().value();
        HttpStatus httpStatus = HttpStatus.resolve(statusCodeValue);
        if (httpStatus == null) {
            logger.warn("Received non-standard HTTP status code {} for {}: {}", statusCodeValue, requestUri, e.getMessage());
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        HttpRes<Void> response = HttpRes.fail(httpStatus, e.getReason());
        return ResponseEntity.status(statusCodeValue).body(response);
    }

    // Spring security defaults to throw BadCredentialsException for authentication failures to prevent user enumeration,
    // so we won't handle UsernameNotFoundException separately unless we want to change this behavior
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<HttpRes<Void>> handleBadCredentialsException(BadCredentialsException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        logger.error("Authentication failed for {}: {}", requestUri, e.getMessage());
        HttpRes<Void> response = HttpRes.fail(HttpStatus.UNAUTHORIZED, "Error while trying to login");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<HttpRes<Void>> handleTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        logger.error("Type mismatch for {}: {}", requestUri, e.getMessage());
        HttpRes<Void> response = HttpRes.fail(HttpStatus.BAD_REQUEST, "Wrong data type provided in request");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<HttpRes<Void>> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        logger.error("Illegal argument for {}: {}", requestUri, e.getMessage());
        HttpRes<Void> response = HttpRes.fail(HttpStatus.BAD_REQUEST, e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(response);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<HttpRes<Void>> handleExpiredJwtException(TokenExpiredException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        logger.error("JWT token expired for {}: {}", requestUri, e.getMessage());
        HttpRes<Void> response = HttpRes.fail(HttpStatus.UNAUTHORIZED, "JWT token has expired");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(response);
    }

    @ExceptionHandler(SignatureVerificationException.class)
    public ResponseEntity<HttpRes<Void>> handleSignatureVerificationException(SignatureVerificationException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        logger.error("JWT signature verification failed for {}: {}", requestUri, e.getMessage());
        HttpRes<Void> response = HttpRes.fail(HttpStatus.UNAUTHORIZED, "JWT token signature verification failed, token may be invalid");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(response);
    }


    // We are using email for username during login
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<HttpRes<Void>> handleUsernameNotFoundException(UsernameNotFoundException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        logger.error("Email not found for {}: {}", requestUri, e.getMessage());
        HttpRes<Void> response = HttpRes.fail(HttpStatus.UNAUTHORIZED, "Email not found for JWT token");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpRes<Void>> handleGenericException(Exception e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        logger.error("An error occurred while processing {}: {}, {}", requestUri, e.getClass().getSimpleName(), e.getMessage());
        HttpRes<Void> response = HttpRes.fail(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(response);
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, Object handler, Exception ex) {
        if (ex instanceof TokenExpiredException) {
            handleExpiredJwtException((TokenExpiredException) ex, request);
        } else if (ex instanceof SignatureVerificationException) {
            handleSignatureVerificationException((SignatureVerificationException) ex, request);
        } else if (ex instanceof UsernameNotFoundException) {
            handleUsernameNotFoundException((UsernameNotFoundException) ex, request);
        } else if (ex instanceof AccessDeniedException) {
            handleAccessDeniedException((AccessDeniedException) ex, request);
        } else if (ex instanceof HttpRequestMethodNotSupportedException) {
            handleHttpRequestMethodNotSupportedException((HttpRequestMethodNotSupportedException) ex, request);
        } else if (ex instanceof NoHandlerFoundException) {
            handleNoHandlerFoundException((NoHandlerFoundException) ex, request);
        } else if (ex instanceof NoResourceFoundException) {
            handleNoResourceFoundException((NoResourceFoundException) ex, request);
        } else if (ex instanceof MethodArgumentNotValidException) {
            handleMethodArgumentNotValidException((MethodArgumentNotValidException) ex, request);
        } else if (ex instanceof HttpMessageNotReadableException) {
            handleHttpMessageNotReadableException((HttpMessageNotReadableException) ex, request);
        } else if (ex instanceof ResponseStatusException) {
            handleResponseStatusException((ResponseStatusException) ex, request);
        } else if (ex instanceof BadCredentialsException) {
            handleBadCredentialsException((BadCredentialsException) ex, request);
        } else if (ex instanceof MethodArgumentTypeMismatchException) {
            handleTypeMismatchException((MethodArgumentTypeMismatchException) ex, request);
        } else if (ex instanceof IllegalArgumentException) {
            handleIllegalArgumentException((IllegalArgumentException) ex, request);
        } else {
            handleGenericException(ex, request);
        }

        ModelAndView mav = new ModelAndView("errorView");
        mav.addObject("message", ex.getMessage());
        return mav;
    }
}
