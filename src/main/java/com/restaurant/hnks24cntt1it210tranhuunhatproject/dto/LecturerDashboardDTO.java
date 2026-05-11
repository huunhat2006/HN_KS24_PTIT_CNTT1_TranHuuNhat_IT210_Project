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
public class LecturerDashboardDTO {
	private long pendingCount;
	private long completedCount;
	private long overdueCount;
	private long totalCount;
	private List<String> labels;
	private List<Long> values;
}

