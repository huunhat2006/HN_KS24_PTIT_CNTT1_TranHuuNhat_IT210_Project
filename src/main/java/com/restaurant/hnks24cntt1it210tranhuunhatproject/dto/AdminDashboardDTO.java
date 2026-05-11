package com.restaurant.hnks24cntt1it210tranhuunhatproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardDTO {
    private long totalEquipments;
    private long totalUsers;
    private long totalMentoringSessions;
    private List<String> topLecturerNames;
    private List<Long> topLecturerCounts;
    private List<String> overviewLabels;
    private List<Long> overviewValues;
}

