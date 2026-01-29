package com.drivingschool.rewardssystem.controller.dto;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public record ErrorResponse(String message, Map<String, String> errors) {
    public ErrorResponse(String message, Map<String, String> errors) {
        this.message = message;
        this.errors = errors == null ? null : Collections.unmodifiableMap(new HashMap<>(errors));
    }

    @Override
    public Map<String, String> errors() {
        return errors == null ? null : new HashMap<>(errors);
    }
}
