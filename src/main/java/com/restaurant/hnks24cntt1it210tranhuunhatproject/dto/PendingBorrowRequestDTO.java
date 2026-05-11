package com.restaurant.hnks24cntt1it210tranhuunhatproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingBorrowRequestDTO {
    private Integer borrowingRecordId;
    private Integer sessionId;
    private String studentName;
    private String lecturerName;
    private String equipmentName;
    private String labTypeName;
    private Integer quantity;
}

