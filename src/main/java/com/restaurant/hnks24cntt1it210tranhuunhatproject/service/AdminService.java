package com.restaurant.hnks24cntt1it210tranhuunhatproject.service;

import com.restaurant.hnks24cntt1it210tranhuunhatproject.dto.AdminDashboardDTO;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.dto.PendingBorrowRequestDTO;

import java.util.List;

public interface AdminService {
    AdminDashboardDTO getDashboardStats();
    List<Object[]> getTop5Lecturers();
    List<PendingBorrowRequestDTO> getPendingBorrowRequests(String sort);
    void confirmBorrowingIssuance(Integer borrowingRecordId);
}

