package com.drivingschool.rewardssystem.repository;

import com.drivingschool.rewardssystem.model.RewardsAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RewardsAccountRepository extends JpaRepository<RewardsAccount, Long> {
    Optional<RewardsAccount> findByTraineeId(Long traineeId);
}
