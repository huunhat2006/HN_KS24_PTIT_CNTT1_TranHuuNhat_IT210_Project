package com.restaurant.hnks24cntt1it210tranhuunhatproject.service;

import com.restaurant.hnks24cntt1it210tranhuunhatproject.dto.AcademicRecordDTO;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.dto.BookSessionDTO;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.dto.LecturerDTO;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.dto.StudentDashboardDTO;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.Department;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.MentoringSession;

import java.util.List;

public interface StudentService {
    StudentDashboardDTO getDashboard(Integer studentId);
    List<MentoringSession> getStudentSessions(Integer studentId, String sort);
    List<AcademicRecordDTO> getAcademicRecords(Integer studentId);
    List<Department> getAllDepartments();
    List<LecturerDTO> getLecturers(Integer departmentId);
    void bookSession(BookSessionDTO requestDTO);
    void cancelSession(Integer sessionId, Integer studentId);
}
