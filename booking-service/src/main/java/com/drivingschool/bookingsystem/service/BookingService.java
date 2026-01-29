package com.drivingschool.bookingsystem.service;

import com.drivingschool.bookingsystem.client.UserClient;
import com.drivingschool.bookingsystem.client.UserSummary;
import com.drivingschool.bookingsystem.exception.BusinessRuleException;
import com.drivingschool.bookingsystem.exception.ResourceNotFoundException;
import com.drivingschool.bookingsystem.model.Booking;
import com.drivingschool.bookingsystem.repository.BookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);
    private static final Duration MAX_DURATION = Duration.ofHours(2);
    private static final Duration MIN_DURATION = Duration.ofMinutes(60);

    private final BookingRepository bookingRepository;
    private final UserClient userClient;

    public BookingService(BookingRepository bookingRepository, UserClient userClient) {
        this.bookingRepository = bookingRepository;
        this.userClient = userClient;
    }

    public Booking createBooking(Long traineeId, Long instructorId, LocalDateTime start, LocalDateTime end) {
        validateTimes(start, end);
        validateRoles(traineeId, instructorId);
        ensureInstructorAvailability(instructorId, start, end);

        Booking booking = new Booking(null, traineeId, instructorId, start, end, Booking.Status.PENDING);
        Booking saved = bookingRepository.save(booking);
        logger.info("Booking created id={} traineeId={} instructorId={}",
                saved.getId(), saved.getTraineeId(), saved.getInstructorId());
        return saved;
    }

    public Booking confirmBooking(Long id) {
        Booking booking = getBooking(id);
        if (booking.getStatus() == Booking.Status.CANCELLED) {
            throw new BusinessRuleException("Cannot confirm cancelled booking");
        }
        booking.setStatus(Booking.Status.CONFIRMED);
        Booking saved = bookingRepository.save(booking);
        logger.info("Booking confirmed id={}", id);
        return saved;
    }

    public Booking cancelBooking(Long id) {
        Booking booking = getBooking(id);
        booking.setStatus(Booking.Status.CANCELLED);
        Booking saved = bookingRepository.save(booking);
        logger.info("Booking cancelled id={}", id);
        return saved;
    }

    public void deleteBooking(Long id) {
        Booking booking = getBooking(id);
        if (booking.getStatus() != Booking.Status.CANCELLED) {
            throw new BusinessRuleException("Only cancelled bookings can be deleted");
        }
        bookingRepository.delete(booking);
        logger.info("Booking deleted id={}", id);
    }

    public List<Booking> getTraineeBookings(Long traineeId) {
        return bookingRepository.findByTraineeId(traineeId);
    }

    public Booking getBooking(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
    }

    private void validateTimes(LocalDateTime start, LocalDateTime end) {
        if (start.isBefore(LocalDateTime.now())) {
            throw new BusinessRuleException("Start time cannot be in the past");
        }
        if (!end.isAfter(start)) {
            throw new BusinessRuleException("End time must be after start time");
        }
        Duration duration = Duration.between(start, end);
        if (duration.compareTo(MIN_DURATION) < 0 || duration.compareTo(MAX_DURATION) > 0) {
            throw new BusinessRuleException("Lesson duration must be between 60 and 120 minutes");
        }
    }

    private void validateRoles(Long traineeId, Long instructorId) {
        UserSummary trainee = userClient.getUser(traineeId);
        UserSummary instructor = userClient.getUser(instructorId);
        if (trainee == null || instructor == null) {
            throw new BusinessRuleException("User data unavailable");
        }
        if (!"TRAINEE".equalsIgnoreCase(trainee.getRole())) {
            throw new BusinessRuleException("Trainee must have role TRAINEE");
        }
        if (!"INSTRUCTOR".equalsIgnoreCase(instructor.getRole())) {
            throw new BusinessRuleException("Instructor must have role INSTRUCTOR");
        }
    }

    private void ensureInstructorAvailability(Long instructorId, LocalDateTime start, LocalDateTime end) {
        if (!bookingRepository.findOverlappingBookings(instructorId, start, end).isEmpty()) {
            throw new BusinessRuleException("Instructor has overlapping booking");
        }
    }
}
