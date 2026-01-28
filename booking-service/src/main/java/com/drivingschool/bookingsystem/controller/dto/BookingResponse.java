package com.drivingschool.bookingsystem.controller.dto;

import com.drivingschool.bookingsystem.model.Booking;

import java.time.LocalDateTime;

public class BookingResponse {

    private Long id;
    private Long traineeId;
    private Long instructorId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Booking.Status status;

    public static BookingResponse from(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.id = booking.getId();
        response.traineeId = booking.getTraineeId();
        response.instructorId = booking.getInstructorId();
        response.startTime = booking.getStartTime();
        response.endTime = booking.getEndTime();
        response.status = booking.getStatus();
        return response;
    }

    public Long getId() {
        return id;
    }

    public Long getTraineeId() {
        return traineeId;
    }

    public Long getInstructorId() {
        return instructorId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public Booking.Status getStatus() {
        return status;
    }
}
