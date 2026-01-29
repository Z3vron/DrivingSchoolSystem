package com.drivingschool.rewardssystem.controller;

import com.drivingschool.rewardssystem.controller.dto.CoinFlipRequest;
import com.drivingschool.rewardssystem.controller.dto.EarnPointsRequest;
import com.drivingschool.rewardssystem.controller.dto.ErrorResponse;
import com.drivingschool.rewardssystem.controller.dto.LotteryRequest;
import com.drivingschool.rewardssystem.controller.dto.RewardsAccountResponse;
import com.drivingschool.rewardssystem.model.RewardsAccount;
import com.drivingschool.rewardssystem.service.RewardsService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rewards")
public class RewardsController {

    private final RewardsService rewardsService;

    public RewardsController(RewardsService rewardsService) {
        this.rewardsService = rewardsService;
    }

    @GetMapping("/{traineeId}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account found"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public RewardsAccountResponse getAccount(@PathVariable("traineeId") Long traineeId) {
        return RewardsAccountResponse.from(rewardsService.getAccount(traineeId));
    }

    @PostMapping("/earn")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Points earned"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "422",
                    description = "Business rule violation",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public RewardsAccountResponse earnPoints(@Valid @RequestBody EarnPointsRequest request) {
        RewardsAccount account = rewardsService.earnPoints(request.getTraineeId(), request.getPoints());
        return RewardsAccountResponse.from(account);
    }

    @PostMapping("/lottery")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lottery draw result"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "422",
                    description = "Business rule violation",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public RewardsAccountResponse lotteryDraw(@Valid @RequestBody LotteryRequest request) {
        RewardsAccount account = rewardsService.lotteryDraw(request.getTraineeId());
        return RewardsAccountResponse.from(account);
    }

    @PostMapping("/coin-flip")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Coin flip result"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "422",
                    description = "Business rule violation",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public RewardsAccountResponse coinFlip(@Valid @RequestBody CoinFlipRequest request) {
        RewardsAccount account = rewardsService.coinFlip(request.getTraineeId(), request.getWagerPoints());
        return RewardsAccountResponse.from(account);
    }
}
