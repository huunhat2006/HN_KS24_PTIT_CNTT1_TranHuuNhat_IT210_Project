package com.restaurant.hnks24cntt1it210tranhuunhatproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentDTO {
    private Integer id;
    private String equipmentName;
    private Integer labTypeId;
    private String labTypeName;
    private String description;
    private Integer totalQuantity;
    private Integer availableQuantity;
}