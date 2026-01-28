package com.drivingschool.rewardssystem.controller.dto;

import jakarta.validation.constraints.NotNull;

public class LotteryRequest {

    @NotNull
    private Long traineeId;

    public Long getTraineeId() {
        return traineeId;
    }

    public void setTraineeId(Long traineeId) {
        this.traineeId = traineeId;
    }
}
