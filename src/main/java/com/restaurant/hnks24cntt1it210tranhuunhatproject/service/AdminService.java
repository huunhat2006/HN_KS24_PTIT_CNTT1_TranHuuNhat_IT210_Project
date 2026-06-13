package com.restaurant.hnks24cntt1it210tranhuunhatproject.service;

import com.restaurant.hnks24cntt1it210tranhuunhatproject.dto.AdminDashboardDTO;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.dto.PendingBorrowRequestDTO;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.MentoringSession;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.User;

import java.util.List;

public interface AdminService {
    AdminDashboardDTO getDashboardStats();
    List<Object[]> getTop5Lecturers();
    List<User> getUsers(String sort);
    List<MentoringSession> getMentoringSessions(String sort);
    List<PendingBorrowRequestDTO> getPendingBorrowRequests(String sort);
    void confirmBorrowingIssuance(Integer borrowingRecordId);
}

