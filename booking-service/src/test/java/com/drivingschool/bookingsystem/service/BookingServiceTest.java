package com.drivingschool.bookingsystem.service;

import com.drivingschool.bookingsystem.client.UserClient;
import com.drivingschool.bookingsystem.client.UserSummary;
import com.drivingschool.bookingsystem.exception.BusinessRuleException;
import com.drivingschool.bookingsystem.model.Booking;
import com.drivingschool.bookingsystem.repository.BookingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private BookingService bookingService;

    @Test
    void createBookingRejectsPastStart() {
        assertThatThrownBy(() -> bookingService.createBooking(1L, 2L,
                LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1)))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void createBookingRejectsOverlaps() {
        stubUserRoles("TRAINEE", "INSTRUCTOR");
        when(bookingRepository.findOverlappingBookings(eq(2L), any(), any()))
                .thenReturn(List.of(new Booking()));

        assertThatThrownBy(() -> bookingService.createBooking(1L, 2L,
                LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3)))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void createBookingRejectsWrongRole() {
        stubUserRoles("INSTRUCTOR", "TRAINEE");

        assertThatThrownBy(() -> bookingService.createBooking(1L, 2L,
                LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3)))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void confirmBookingRejectedWhenCancelled() {
        Booking booking = new Booking(10L, 1L, 2L,
                LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3),
                Booking.Status.CANCELLED);
        when(bookingRepository.findById(10L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.confirmBooking(10L))
                .isInstanceOf(BusinessRuleException.class);
    }

    private void stubUserRoles(String traineeRole, String instructorRole) {
        UserSummary trainee = new UserSummary();
        trainee.setId(1L);
        trainee.setRole(traineeRole);
        UserSummary instructor = new UserSummary();
        instructor.setId(2L);
        instructor.setRole(instructorRole);
        when(userClient.getUser(1L)).thenReturn(trainee);
        when(userClient.getUser(2L)).thenReturn(instructor);
    }
}
