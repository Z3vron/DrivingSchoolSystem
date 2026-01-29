package com.drivingschool.bookingsystem.controller.dto;

import java.time.LocalDateTime;

public record WeatherResponse(
        LocalDateTime startTime,
        double temperatureC,
        int weatherCode,
        String description) {
}
