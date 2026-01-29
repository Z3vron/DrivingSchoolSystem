package com.drivingschool.bookingsystem.service;

import com.drivingschool.bookingsystem.client.OpenMeteoResponse;
import com.drivingschool.bookingsystem.client.WeatherClient;
import com.drivingschool.bookingsystem.controller.dto.WeatherResponse;
import com.drivingschool.bookingsystem.exception.ExternalServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Service
public class WeatherService {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    private static final Map<Integer, String> CODE_DESCRIPTIONS = Map.ofEntries(
            Map.entry(0, "Clear sky"),
            Map.entry(1, "Mainly clear"),
            Map.entry(2, "Partly cloudy"),
            Map.entry(3, "Overcast"),
            Map.entry(45, "Fog"),
            Map.entry(48, "Depositing rime fog"),
            Map.entry(51, "Light drizzle"),
            Map.entry(53, "Moderate drizzle"),
            Map.entry(55, "Dense drizzle"),
            Map.entry(61, "Slight rain"),
            Map.entry(63, "Moderate rain"),
            Map.entry(65, "Heavy rain"),
            Map.entry(71, "Slight snow fall"),
            Map.entry(73, "Moderate snow fall"),
            Map.entry(75, "Heavy snow fall"),
            Map.entry(80, "Slight rain showers"),
            Map.entry(81, "Moderate rain showers"),
            Map.entry(82, "Violent rain showers")
    );

    private final WeatherClient weatherClient;
    private final double latitude;
    private final double longitude;

    public WeatherService(
            WeatherClient weatherClient,
            @Value("${weather.latitude}") double latitude,
            @Value("${weather.longitude}") double longitude) {
        this.weatherClient = weatherClient;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public WeatherResponse getWeatherFor(LocalDateTime startTime) {
        LocalDateTime normalized = startTime.truncatedTo(ChronoUnit.HOURS);
        OpenMeteoResponse response = weatherClient.fetchForecast(
                latitude,
                longitude,
                normalized.toLocalDate());

        if (response == null
                || response.hourly() == null
                || response.hourly().time() == null
                || response.hourly().temperature_2m() == null
                || response.hourly().weathercode() == null) {
            throw new ExternalServiceException("Weather data unavailable");
        }

        String targetTime = normalized.format(TIME_FORMATTER);
        int index = response.hourly().time().indexOf(targetTime);
        if (index < 0) {
            throw new ExternalServiceException("Weather data unavailable for requested time");
        }

        if (index >= response.hourly().temperature_2m().size()
                || index >= response.hourly().weathercode().size()) {
            throw new ExternalServiceException("Weather data unavailable for requested time");
        }

        Double temperature = response.hourly().temperature_2m().get(index);
        Integer weatherCode = response.hourly().weathercode().get(index);
        String description = CODE_DESCRIPTIONS.getOrDefault(weatherCode, "Unknown");

        return new WeatherResponse(normalized, temperature, weatherCode, description);
    }
}
