package com.drivingschool.bookingsystem.controller;

import com.drivingschool.bookingsystem.controller.dto.BookingRequest;
import com.drivingschool.bookingsystem.controller.dto.BookingResponse;
import com.drivingschool.bookingsystem.model.Booking;
import com.drivingschool.bookingsystem.service.BookingService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking created"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "422", description = "Business rule violation"),
            @ApiResponse(responseCode = "502", description = "User service unavailable")
    })
    public BookingResponse createBooking(@Valid @RequestBody BookingRequest request) {
        Booking booking = bookingService.createBooking(
                request.getTraineeId(),
                request.getInstructorId(),
                request.getStartTime(),
                request.getEndTime());
        return BookingResponse.from(booking);
    }

    @PostMapping("/{id}/confirm")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking confirmed"),
            @ApiResponse(responseCode = "404", description = "Booking not found"),
            @ApiResponse(responseCode = "422", description = "Business rule violation")
    })
    public BookingResponse confirmBooking(@PathVariable("id") Long id) {
        return BookingResponse.from(bookingService.confirmBooking(id));
    }

    @PostMapping("/{id}/cancel")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking cancelled"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    public BookingResponse cancelBooking(@PathVariable("id") Long id) {
        return BookingResponse.from(bookingService.cancelBooking(id));
    }

    @GetMapping("/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking found"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    public BookingResponse getBooking(@PathVariable("id") Long id) {
        return BookingResponse.from(bookingService.getBooking(id));
    }

    @GetMapping("/trainees/{traineeId}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking list for trainee")
    })
    public List<BookingResponse> getTraineeBookings(@PathVariable("traineeId") Long traineeId) {
        return bookingService.getTraineeBookings(traineeId).stream()
                .map(BookingResponse::from)
                .collect(Collectors.toList());
    }
}
