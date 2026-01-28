package com.drivingschool.rewardssystem.controller.dto;

import com.drivingschool.rewardssystem.model.RewardsAccount;

public class RewardsAccountResponse {

    private Long traineeId;
    private int points;
    private int goldenSlotsWon;

    public static RewardsAccountResponse from(RewardsAccount account) {
        RewardsAccountResponse response = new RewardsAccountResponse();
        response.traineeId = account.getTraineeId();
        response.points = account.getPoints();
        response.goldenSlotsWon = account.getGoldenSlotsWon();
        return response;
    }

    public Long getTraineeId() {
        return traineeId;
    }

    public int getPoints() {
        return points;
    }

    public int getGoldenSlotsWon() {
        return goldenSlotsWon;
    }
}
