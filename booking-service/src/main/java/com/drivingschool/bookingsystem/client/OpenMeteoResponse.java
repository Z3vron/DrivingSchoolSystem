package com.drivingschool.bookingsystem.client;

import java.util.List;

public record OpenMeteoResponse(Hourly hourly) {

    public record Hourly(List<String> time, List<Double> temperature_2m, List<Integer> weathercode) {
    }
}
