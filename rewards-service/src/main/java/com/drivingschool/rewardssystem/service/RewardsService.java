package com.drivingschool.rewardssystem.service;

import com.drivingschool.rewardssystem.exception.BusinessRuleException;
import com.drivingschool.rewardssystem.exception.ResourceNotFoundException;
import com.drivingschool.rewardssystem.model.RewardsAccount;
import com.drivingschool.rewardssystem.repository.RewardsAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class RewardsService {

    private static final Logger logger = LoggerFactory.getLogger(RewardsService.class);
    private static final int LOTTERY_COST = 10;

    private final RewardsAccountRepository repository;
    private final SecureRandom random = new SecureRandom();

    public RewardsService(RewardsAccountRepository repository) {
        this.repository = repository;
    }

    public RewardsAccount getAccount(Long traineeId) {
        return repository.findByTraineeId(traineeId)
                .orElseThrow(() -> new ResourceNotFoundException("Rewards account not found"));
    }

    public RewardsAccount earnPoints(Long traineeId, int points) {
        if (points <= 0) {
            throw new BusinessRuleException("Points must be positive");
        }
        RewardsAccount account = repository.findByTraineeId(traineeId)
                .orElseGet(() -> new RewardsAccount(null, traineeId, 0, 0));
        account.setPoints(account.getPoints() + points);
        RewardsAccount saved = repository.save(account);
        logger.info("Points earned traineeId={} points={} total={}", traineeId, points, saved.getPoints());
        return saved;
    }

    public RewardsAccount lotteryDraw(Long traineeId) {
        RewardsAccount account = getAccount(traineeId);
        if (account.getPoints() < LOTTERY_COST) {
            throw new BusinessRuleException("Not enough points for lottery draw");
        }
        account.setPoints(account.getPoints() - LOTTERY_COST);
        boolean win = random.nextInt(10) == 0;
        if (win) {
            account.setGoldenSlotsWon(account.getGoldenSlotsWon() + 1);
        }
        RewardsAccount saved = repository.save(account);
        logger.info("Lottery draw traineeId={} win={} points={}", traineeId, win, saved.getPoints());
        return saved;
    }

    public RewardsAccount coinFlip(Long traineeId, int wagerPoints) {
        RewardsAccount account = getAccount(traineeId);
        if (wagerPoints <= 0) {
            throw new BusinessRuleException("Wager must be positive");
        }
        if (account.getPoints() < wagerPoints) {
            throw new BusinessRuleException("Insufficient points for wager");
        }
        boolean win = random.nextBoolean();
        if (win) {
            account.setPoints(account.getPoints() + wagerPoints);
        } else {
            account.setPoints(account.getPoints() - wagerPoints);
        }
        RewardsAccount saved = repository.save(account);
        logger.info("Coin flip traineeId={} win={} wager={} total={}",
                traineeId, win, wagerPoints, saved.getPoints());
        return saved;
    }
}
