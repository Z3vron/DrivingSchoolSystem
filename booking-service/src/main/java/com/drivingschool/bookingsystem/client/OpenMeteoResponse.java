package com.drivingschool.bookingsystem.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record OpenMeteoResponse(Hourly hourly) {

    public record Hourly(List<String> time, List<Double> temperature_2m, List<Integer> weathercode) {
        public Hourly(List<String> time, List<Double> temperature_2m, List<Integer> weathercode) {
            this.time = time == null ? null : Collections.unmodifiableList(new ArrayList<>(time));
            this.temperature_2m = temperature_2m == null ? null : Collections.unmodifiableList(new ArrayList<>(temperature_2m));
            this.weathercode = weathercode == null ? null : Collections.unmodifiableList(new ArrayList<>(weathercode));
        }

        @Override
        public List<String> time() {
            return time == null ? null : new ArrayList<>(time);
        }

        @Override
        public List<Double> temperature_2m() {
            return temperature_2m == null ? null : new ArrayList<>(temperature_2m);
        }

        @Override
        public List<Integer> weathercode() {
            return weathercode == null ? null : new ArrayList<>(weathercode);
        }
    }
}
