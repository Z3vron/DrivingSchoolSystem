package com.drivingschool.bookingsystem.repository;

import com.drivingschool.bookingsystem.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select b from Booking b where b.instructorId = :instructorId and b.status <> 'CANCELLED' "
            + "and b.startTime < :endTime and b.endTime > :startTime")
    List<Booking> findOverlappingBookings(@Param("instructorId") Long instructorId,
                                          @Param("startTime") LocalDateTime startTime,
                                          @Param("endTime") LocalDateTime endTime);

    List<Booking> findByTraineeId(Long traineeId);
}
