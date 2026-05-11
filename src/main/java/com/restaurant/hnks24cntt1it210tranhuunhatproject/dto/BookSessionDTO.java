package com.restaurant.hnks24cntt1it210tranhuunhatproject.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookSessionDTO {
    @NotNull
    private Integer studentId;

    private Integer departmentId;

    @NotNull
    private Integer lecturerId;

    @NotNull
    @FutureOrPresent
    private LocalDate sessionDate;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;
}
