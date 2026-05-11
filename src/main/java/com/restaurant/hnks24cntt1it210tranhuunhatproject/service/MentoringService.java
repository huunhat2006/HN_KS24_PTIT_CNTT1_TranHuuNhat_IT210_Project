package com.restaurant.hnks24cntt1it210tranhuunhatproject.service;

import com.restaurant.hnks24cntt1it210tranhuunhatproject.dto.LecturerDTO;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.dto.MentoringSessionRequestDTO;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.dto.MentoringSessionResponseDTO;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.Department;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.MentoringSession;

import java.util.List;

public interface MentoringService {
    MentoringSessionResponseDTO bookSession(MentoringSessionRequestDTO requestDTO);
    MentoringSessionResponseDTO cancelSession(Integer sessionId, Integer studentId);
    List<Department> getAllDepartments();
    List<LecturerDTO> getAllLecturers();
    List<MentoringSession> getStudentSessions(Integer studentId, String sort);

    void approveSession(Integer sessionId, Integer equipmentId, Integer quantity, Integer approverId);
}