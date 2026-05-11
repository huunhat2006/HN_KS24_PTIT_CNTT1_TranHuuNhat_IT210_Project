package com.restaurant.hnks24cntt1it210tranhuunhatproject.service;

import com.restaurant.hnks24cntt1it210tranhuunhatproject.dto.ApproveSessionRequestDTO;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.dto.EquipmentDTO;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.dto.EvaluationRequestDTO;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.dto.LecturerDashboardDTO;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.MentoringSession;

import java.util.List;

public interface LecturerService {
    LecturerDashboardDTO getDashboard(Integer lecturerId);
    List<MentoringSession> getPendingSessions(Integer lecturerId, String sort);
    List<MentoringSession> getCompletedSessions(Integer lecturerId, String sort);
    List<MentoringSession> getOverdueSessions(Integer lecturerId, String sort);
    List<EquipmentDTO> getAvailableEquipments();
    void approveSession(Integer lecturerId, ApproveSessionRequestDTO requestDTO);
    void evaluateSession(Integer lecturerId, EvaluationRequestDTO requestDTO);
}

