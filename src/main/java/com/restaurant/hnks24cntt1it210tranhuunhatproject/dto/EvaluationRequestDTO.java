package com.restaurant.hnks24cntt1it210tranhuunhatproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationRequestDTO {
    private Integer sessionId;
    private Integer performanceRating;
    private String evaluationNotes;
}

