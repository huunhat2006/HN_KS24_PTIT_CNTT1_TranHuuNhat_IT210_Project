package com.restaurant.hnks24cntt1it210tranhuunhatproject.dto;

import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.enums.MentoringSessionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MentoringSessionResponseDTO {
    private Integer id;
    private Integer studentId;
    private String studentName;
    private String studentEmail;
    private Integer lecturerId;
    private String lecturerName;
    private String lecturerDepartmentName;
    private String lecturerSpecialization;
    private LocalDate sessionDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private MentoringSessionStatus status;
    private LocalDateTime createdAt;
}