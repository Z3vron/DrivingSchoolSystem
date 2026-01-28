package com.drivingschool.rewardssystem.service;

import com.drivingschool.rewardssystem.exception.BusinessRuleException;
import com.drivingschool.rewardssystem.model.RewardsAccount;
import com.drivingschool.rewardssystem.repository.RewardsAccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RewardsServiceTest {

    @Mock
    private RewardsAccountRepository repository;

    @InjectMocks
    private RewardsService rewardsService;

    @Test
    void earnPointsRejectsNonPositive() {
        assertThatThrownBy(() -> rewardsService.earnPoints(1L, 0))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void lotteryRejectsInsufficientPoints() {
        RewardsAccount account = new RewardsAccount(1L, 1L, 5, 0);
        when(repository.findByTraineeId(1L)).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> rewardsService.lotteryDraw(1L))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void coinFlipRejectsTooHighWager() {
        RewardsAccount account = new RewardsAccount(1L, 1L, 3, 0);
        when(repository.findByTraineeId(1L)).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> rewardsService.coinFlip(1L, 5))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void earnPointsCreatesNewAccount() {
        when(repository.findByTraineeId(2L)).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        RewardsAccount saved = rewardsService.earnPoints(2L, 15);
        assertThat(saved.getPoints()).isEqualTo(15);
        assertThat(saved.getTraineeId()).isEqualTo(2L);
    }
}
