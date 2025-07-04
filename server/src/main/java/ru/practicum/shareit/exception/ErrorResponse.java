package ru.practicum.shareit.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private String error;
    private Map<String, String> errors;
}
