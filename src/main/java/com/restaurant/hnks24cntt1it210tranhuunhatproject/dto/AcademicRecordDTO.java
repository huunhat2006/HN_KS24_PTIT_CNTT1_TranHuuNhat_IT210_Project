package com.restaurant.hnks24cntt1it210tranhuunhatproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcademicRecordDTO {
    private Integer sessionId;
    private String lecturerName;
    private LocalDate sessionDate;
    private LocalTime startTime;
    private LocalTime endTime;
    @Builder.Default
    private List<String> equipmentNames = new ArrayList<>();
    private String evaluationNotes;
    private Integer performanceRating;
}
