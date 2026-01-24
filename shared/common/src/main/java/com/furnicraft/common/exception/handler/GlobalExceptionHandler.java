package com.furnicraft.common.exception.handler;

import com.furnicraft.common.api.ApiResponse;
import com.furnicraft.common.exception.BaseException;
import com.furnicraft.common.exception.ErrorCode;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static com.furnicraft.common.filter.CorrelationIdFilter.CORRELATION_ID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<?>> handleBaseException(BaseException ex) {

        ErrorCode code = ex.getErrorCode();

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("errorCode", code.name());

        ApiResponse<?> response = ApiResponse.builder()
                .status(code.getStatus().value())
                .message(ex.getMessage())
                .timestamp(Instant.now())
                .correlationId(MDC.get(CORRELATION_ID))
                .errors(errorDetails)
                .build();

        return new ResponseEntity<>(response, code.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidation(MethodArgumentNotValidException ex) {


        Map<String, String> validationErrors = new HashMap<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(error -> validationErrors.put(error.getField(), error.getDefaultMessage()));

        ErrorCode code = ErrorCode.VALIDATION_FAILED;

        ApiResponse<?> response = ApiResponse.builder()
                .status(code.getStatus().value())
                .message(code.getDefaultMessage())
                .timestamp(Instant.now())
                .correlationId(MDC.get(CORRELATION_ID))
                .errors(validationErrors)
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGenericException(Exception ex) {

        ErrorCode code = ErrorCode.INTERNAL_ERROR;

        ApiResponse<?> response = ApiResponse.builder()
                .status(code.getStatus().value())
                .message(code.getDefaultMessage())
                .timestamp(Instant.now())
                .correlationId(MDC.get(CORRELATION_ID))
                .errors(ex.getMessage())
                .build();

        return ResponseEntity.internalServerError().body(response);
    }
}
