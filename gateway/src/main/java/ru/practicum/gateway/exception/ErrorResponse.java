package ru.practicum.gateway.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private String error;
    private Map<String, String> errors;
}
