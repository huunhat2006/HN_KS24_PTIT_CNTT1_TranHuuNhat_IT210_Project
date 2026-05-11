package com.restaurant.hnks24cntt1it210tranhuunhatproject.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "borrowing_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowingDetail {

    @EmbeddedId
    private BorrowingDetailId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("borrowingRecordId")
    @JoinColumn(name = "borrowing_record_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private BorrowingRecord borrowingRecord;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("equipmentId")
    @JoinColumn(name = "equipment_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Equipment equipment;

    @Column(nullable = false)
    @Builder.Default
    private Integer quantity = 1;
}