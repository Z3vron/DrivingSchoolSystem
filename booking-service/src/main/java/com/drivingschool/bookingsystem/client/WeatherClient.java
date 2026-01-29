package com.drivingschool.bookingsystem.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;

@Component
public class WeatherClient {

    private final RestClient restClient;

    public WeatherClient(
            RestClient.Builder restClientBuilder,
            @Value("${weather.base-url:https://api.open-meteo.com/v1/forecast}") String baseUrl) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
    }

    public OpenMeteoResponse fetchForecast(double latitude, double longitude, LocalDate date) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("latitude", latitude)
                        .queryParam("longitude", longitude)
                        .queryParam("hourly", "temperature_2m,weathercode")
                        .queryParam("timezone", "UTC")
                        .queryParam("start_date", date)
                        .queryParam("end_date", date)
                        .build())
                .retrieve()
                .body(OpenMeteoResponse.class);
    }
}
