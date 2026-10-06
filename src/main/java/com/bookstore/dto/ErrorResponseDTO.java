package com.bookstore.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standardized structured error response envelope")
public class ErrorResponseDTO {

    @Schema(description = "UTC timestamp when the error occurred", example = "2026-10-06T14:40:00Z")
    private Instant timestamp;

    @Schema(description = "HTTP status code integer", example = "422")
    private int status;

    @Schema(description = "HTTP status phrase", example = "Unprocessable Entity")
    private String error;

    @Schema(description = "Machine-readable application error code", example = "INSUFFICIENT_STOCK")
    private String code;

    @Schema(description = "Human-readable error description", example = "Requested quantity exceeds available stock.")
    private String message;

    @Schema(description = "Specific payload field that failed validation (if applicable)", example = "items[0].quantity")
    private String field;

    @Schema(description = "Additional contextual error metadata")
    private Map<String, Object> details;

    @Schema(description = "URI path where the error was triggered", example = "/v1/orders")
    private String path;
}
