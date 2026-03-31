package com.furnicraft.common.exception.handler;

import com.furnicraft.common.api.ApiResponse;
import com.furnicraft.common.exception.BaseException;
import com.furnicraft.common.exception.ErrorCode;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.furnicraft.common.filter.CorrelationIdFilter.CORRELATION_ID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<?>> handleBaseException(BaseException ex) {
        ErrorCode code = ex.getErrorCode();

        Map<String, Object> errorDetails = new LinkedHashMap<>();
        errorDetails.put("errorCode", code.name());

        return buildResponse(code.getStatus(), ex.getMessage(), errorDetails);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> validationErrors = new LinkedHashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> validationErrors.put(error.getField(), error.getDefaultMessage()));

        ex.getBindingResult()
                .getGlobalErrors()
                .forEach(error -> validationErrors.put(error.getObjectName(), error.getDefaultMessage()));

        return buildResponse(
                ErrorCode.VALIDATION_FAILED.getStatus(),
                ErrorCode.VALIDATION_FAILED.getDefaultMessage(),
                validationErrors
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<?>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = new LinkedHashMap<>();

        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.put(violation.getPropertyPath().toString(), violation.getMessage());
        }

        return buildResponse(
                ErrorCode.VALIDATION_FAILED.getStatus(),
                ErrorCode.VALIDATION_FAILED.getDefaultMessage(),
                errors
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<?>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Map<String, Object> errors = new LinkedHashMap<>();
        errors.put("parameter", ex.getName());
        errors.put("value", ex.getValue());
        errors.put("expectedType", ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : null);

        return buildResponse(
                ErrorCode.INVALID_PARAMETER_TYPE.getStatus(),
                ErrorCode.INVALID_PARAMETER_TYPE.getDefaultMessage(),
                errors
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<?>> handleMissingParameter(MissingServletRequestParameterException ex) {
        Map<String, Object> errors = new LinkedHashMap<>();
        errors.put("parameter", ex.getParameterName());
        errors.put("expectedType", ex.getParameterType());

        return buildResponse(
                ErrorCode.MISSING_REQUEST_PARAMETER.getStatus(),
                ErrorCode.MISSING_REQUEST_PARAMETER.getDefaultMessage(),
                errors
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleUnreadableBody(HttpMessageNotReadableException ex) {
        return buildResponse(
                ErrorCode.INVALID_REQUEST_BODY.getStatus(),
                ErrorCode.INVALID_REQUEST_BODY.getDefaultMessage(),
                ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage()
        );
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse<?>> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex) {
        Map<String, Object> errors = new LinkedHashMap<>();
        errors.put("contentType", ex.getContentType() != null ? ex.getContentType().toString() : null);
        errors.put("supportedMediaTypes", ex.getSupportedMediaTypes());

        return buildResponse(
                ErrorCode.UNSUPPORTED_MEDIA_TYPE.getStatus(),
                ErrorCode.UNSUPPORTED_MEDIA_TYPE.getDefaultMessage(),
                errors
        );
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<?>> handleMaxUploadSize(MaxUploadSizeExceededException ex) {
        Map<String, Object> errors = new LinkedHashMap<>();
        errors.put("message", ex.getMessage());

        return buildResponse(
                ErrorCode.FILE_TOO_LARGE.getStatus(),
                ErrorCode.FILE_TOO_LARGE.getDefaultMessage(),
                errors
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGenericException(Exception ex) {
        return buildResponse(
                ErrorCode.INTERNAL_SERVER_ERROR.getStatus(),
                ErrorCode.INTERNAL_SERVER_ERROR.getDefaultMessage(),
                ex.getMessage()
        );
    }

    private ResponseEntity<ApiResponse<?>> buildResponse(HttpStatus status, String message, Object errors) {
        ApiResponse<?> response = ApiResponse.builder()
                .status(status.value())
                .message(message)
                .timestamp(Instant.now())
                .correlationId(MDC.get(CORRELATION_ID))
                .errors(errors)
                .build();

        return ResponseEntity.status(status).body(response);
    }
}