package com.drivingschool.bookingsystem.controller;

import com.drivingschool.bookingsystem.controller.dto.BookingRequest;
import com.drivingschool.bookingsystem.controller.dto.BookingResponse;
import com.drivingschool.bookingsystem.model.Booking;
import com.drivingschool.bookingsystem.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingResponse createBooking(@Valid @RequestBody BookingRequest request) {
        Booking booking = bookingService.createBooking(
                request.getTraineeId(),
                request.getInstructorId(),
                request.getStartTime(),
                request.getEndTime());
        return BookingResponse.from(booking);
    }

    @PostMapping("/{id}/confirm")
    public BookingResponse confirmBooking(@PathVariable("id") Long id) {
        return BookingResponse.from(bookingService.confirmBooking(id));
    }

    @PostMapping("/{id}/cancel")
    public BookingResponse cancelBooking(@PathVariable("id") Long id) {
        return BookingResponse.from(bookingService.cancelBooking(id));
    }

    @GetMapping("/{id}")
    public BookingResponse getBooking(@PathVariable("id") Long id) {
        return BookingResponse.from(bookingService.getBooking(id));
    }

    @GetMapping("/trainees/{traineeId}")
    public List<BookingResponse> getTraineeBookings(@PathVariable("traineeId") Long traineeId) {
        return bookingService.getTraineeBookings(traineeId).stream()
                .map(BookingResponse::from)
                .collect(Collectors.toList());
    }
}
