package com.restaurant.hnks24cntt1it210tranhuunhatproject.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowingDetailId implements Serializable {

    @Column(name = "borrowing_record_id")
    private Integer borrowingRecordId;

    @Column(name = "equipment_id")
    private Integer equipmentId;
}