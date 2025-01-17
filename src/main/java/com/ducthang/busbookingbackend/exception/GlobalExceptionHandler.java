package com.ducthang.busbookingbackend.exception;

import com.ducthang.busbookingbackend.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler
{
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<?>> handleAppException(AppException ex) {
        return createErrorResponse(
                ex.getErrorCode().getResponseCode(),
                ex.getErrorCode().getMessage(),
                null,
                ex.getErrorCode().getHttpStatusCode().value()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(MethodArgumentNotValidException ex) {
        ErrorCode errorCode = ErrorCode.INVALID_REQUEST;
        List<Object> errors = ex.getBindingResult().getAllErrors().stream()
                .map(error -> ((FieldError) error).getField() + " - " + error.getDefaultMessage())
                .collect(Collectors.toList());

        return createErrorResponse(
                errorCode.getResponseCode(),
                errorCode.getMessage(),
                errors,
                errorCode.getHttpStatusCode().value()
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        ErrorCode errorCode = ErrorCode.INVALID_REQUEST;
        return createErrorResponse(
                errorCode.getResponseCode(),
                errorCode.getMessage(),
                List.of(ex.getMessage()),
                errorCode.getHttpStatusCode().value()
        );
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ApiResponse<?>> handleMissingRequestPart(MissingServletRequestPartException ex) {
        ErrorCode errorCode = ErrorCode.INVALID_REQUEST;
        return createErrorResponse(
                errorCode.getResponseCode(),
                errorCode.getMessage(),
                List.of(ex.getMessage()),
                errorCode.getHttpStatusCode().value()
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDenied(AccessDeniedException ex) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        return createErrorResponse(
                errorCode.getResponseCode(),
                errorCode.getMessage(),
                null,
                errorCode.getHttpStatusCode().value()
        );
    }

    private ResponseEntity<ApiResponse<?>> createErrorResponse(int responseCode, String message, Object errors, int httpStatus) {
        ApiResponse<?> errorResponse = ApiResponse.error(responseCode, message, errors);
        return ResponseEntity.status(httpStatus).body(errorResponse);
    }

}
