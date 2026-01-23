package com.furnicraft.common.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private Integer status;

    private String message;

    private T data;

    private String correlationId;

    private Instant timestamp;

    private Object errors;

}
