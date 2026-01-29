package com.drivingschool.bookingsystem.controller;

import com.drivingschool.bookingsystem.controller.dto.BookingRequest;
import com.drivingschool.bookingsystem.controller.dto.BookingResponse;
import com.drivingschool.bookingsystem.controller.dto.ErrorResponse;
import com.drivingschool.bookingsystem.controller.dto.WeatherResponse;
import com.drivingschool.bookingsystem.model.Booking;
import com.drivingschool.bookingsystem.service.BookingService;
import com.drivingschool.bookingsystem.service.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final WeatherService weatherService;

    public BookingController(BookingService bookingService, WeatherService weatherService) {
        this.bookingService = bookingService;
        this.weatherService = weatherService;
    }

    @PostMapping
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking created"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "422",
                    description = "Business rule violation",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "502",
                    description = "User service unavailable",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
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
            @ApiResponse(
                    responseCode = "404",
                    description = "Booking not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "422",
                    description = "Business rule violation",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public BookingResponse confirmBooking(@PathVariable("id") Long id) {
        return BookingResponse.from(bookingService.confirmBooking(id));
    }

    @PostMapping("/{id}/cancel")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking cancelled"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Booking not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public BookingResponse cancelBooking(@PathVariable("id") Long id) {
        return BookingResponse.from(bookingService.cancelBooking(id));
    }

    @DeleteMapping("/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Booking deleted"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Booking not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "422",
                    description = "Business rule violation",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteBooking(@PathVariable("id") Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking found"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Booking not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
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

    @GetMapping("/weather")
    @Operation(summary = "Weather forecast for booking time", description = "Returns forecast for the provided startTime (UTC, hourly).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Weather forecast for booking time"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid date-time format",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "502",
                    description = "Weather service unavailable",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public WeatherResponse getWeatherForBooking(
            @Parameter(
                    description = "Start time in ISO-8601 format (UTC), e.g. 2026-01-29T10:00:00",
                    example = "2026-01-29T10:00:00")
            @RequestParam("startTime")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime) {
        return weatherService.getWeatherFor(startTime);
    }
}
