package com.drivingschool.rewardssystem.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CoinFlipRequest {

    @NotNull
    private Long traineeId;

    @Min(1)
    private int wagerPoints;

    public Long getTraineeId() {
        return traineeId;
    }

    public void setTraineeId(Long traineeId) {
        this.traineeId = traineeId;
    }

    public int getWagerPoints() {
        return wagerPoints;
    }

    public void setWagerPoints(int wagerPoints) {
        this.wagerPoints = wagerPoints;
    }
}
