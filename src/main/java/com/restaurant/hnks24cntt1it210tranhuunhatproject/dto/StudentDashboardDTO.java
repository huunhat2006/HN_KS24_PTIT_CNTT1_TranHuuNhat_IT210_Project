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
public class StudentDashboardDTO {
	private long pendingSessions;
	private long completedSessions;
	private long canceledSessions;
	private long totalSessions;
	private List<String> chartLabels;
	private List<Long> chartValues;
}

