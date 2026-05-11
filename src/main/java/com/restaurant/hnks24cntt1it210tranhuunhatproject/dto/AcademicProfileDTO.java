package com.restaurant.hnks24cntt1it210tranhuunhatproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcademicProfileDTO {
    private Integer sessionId;
    private String lecturerName;
    private LocalDate sessionDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private List<String> equipmentNames;
    private String evaluationNotes;
    private Integer performanceRating;
}

