package com.restaurant.hnks24cntt1it210tranhuunhatproject.dto;

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
public class MentoringSessionRequestDTO {
    private Integer studentId;
    private Integer lecturerId;
    private LocalDate sessionDate;
    private LocalTime startTime;
    private LocalTime endTime;
}