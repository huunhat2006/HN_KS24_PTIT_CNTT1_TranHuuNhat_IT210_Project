package com.restaurant.hnks24cntt1it210tranhuunhatproject.service.impl;

import com.restaurant.hnks24cntt1it210tranhuunhatproject.dto.AcademicRecordDTO;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.dto.BookSessionDTO;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.dto.LecturerDTO;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.dto.StudentDashboardDTO;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.Department;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.Lecturer;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.MentoringSession;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.User;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.enums.MentoringSessionStatus;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.repository.DepartmentRepository;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.repository.LecturerRepository;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.repository.MentoringSessionRepository;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.repository.UserRepository;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Time;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final MentoringSessionRepository mentoringSessionRepository;
    private final DepartmentRepository departmentRepository;
    private final LecturerRepository lecturerRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public StudentDashboardDTO getDashboard(Integer studentId) {
        long pending = mentoringSessionRepository.countStudentSessionsByStatus(studentId, MentoringSessionStatus.PENDING);
        long completed = mentoringSessionRepository.countStudentSessionsByStatus(studentId, MentoringSessionStatus.COMPLETED);
        long canceled = mentoringSessionRepository.countStudentSessionsByStatus(studentId, MentoringSessionStatus.CANCELED);
        long total = mentoringSessionRepository.countStudentSessions(studentId);
        return StudentDashboardDTO.builder()
                .pendingSessions(pending)
                .completedSessions(completed)
                .canceledSessions(canceled)
                .totalSessions(total)
                .chartLabels(List.of("Sắp diễn ra", "Đã hoàn thành", "Đã hủy"))
                .chartValues(List.of(pending, completed, canceled))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MentoringSession> getStudentSessions(Integer studentId, String sort) {
        return switch (sort == null ? "" : sort) {
            case "date_desc" -> mentoringSessionRepository.findStudentSessionsOrderByDateDesc(studentId);
            case "time_asc" -> mentoringSessionRepository.findStudentSessionsOrderByTimeAsc(studentId);
            case "time_desc" -> mentoringSessionRepository.findStudentSessionsOrderByTimeDesc(studentId);
            default -> mentoringSessionRepository.findStudentSessionsOrderByDateAsc(studentId);
        };
    }

    @Override
    @Transactional(readOnly = true)
    public List<AcademicRecordDTO> getAcademicRecords(Integer studentId) {
        List<Object[]> rows = mentoringSessionRepository.findStudentAcademicProfileRows(studentId);
        Map<Integer, AcademicRecordDTO> grouped = new LinkedHashMap<>();
        for (Object[] row : rows) {
            Integer sessionId = toInteger(row[0]);
            AcademicRecordDTO dto = grouped.get(sessionId);
            if (dto == null) {
                dto = AcademicRecordDTO.builder()
                        .sessionId(sessionId)
                        .lecturerName(row[1] != null ? row[1].toString() : "")
                        .sessionDate(toLocalDate(row[2]))
                        .startTime(toLocalTime(row[3]))
                        .endTime(toLocalTime(row[4]))
                        .evaluationNotes(row[5] != null ? row[5].toString() : "")
                        .performanceRating(row[6] != null ? ((Number) row[6]).intValue() : null)
                        .equipmentNames(new ArrayList<>())
                        .build();
                grouped.put(sessionId, dto);
            }
            String equipmentName = row[7] != null ? row[7].toString() : null;
            if (equipmentName != null && !equipmentName.isBlank() && !dto.getEquipmentNames().contains(equipmentName)) {
                dto.getEquipmentNames().add(equipmentName);
            }
        }
        return new ArrayList<>(grouped.values());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LecturerDTO> getLecturers(Integer departmentId) {
        return lecturerRepository.findAll().stream()
                .filter(lecturer -> departmentId == null || lecturer.getDepartment() != null && departmentId.equals(lecturer.getDepartment().getId()))
                .sorted(Comparator.comparing(this::lecturerFullName, Comparator.nullsLast(String::compareToIgnoreCase)))
                .map(this::toLecturerDTO)
                .toList();
    }

    @Override
    @Transactional
    public void bookSession(BookSessionDTO requestDTO) {
        if (requestDTO.getSessionDate() == null || requestDTO.getStartTime() == null || requestDTO.getEndTime() == null) {
            throw new RuntimeException("Ngày và giờ không được để trống");
        }
        if (!requestDTO.getEndTime().isAfter(requestDTO.getStartTime())) {
            throw new RuntimeException("Giờ kết thúc phải sau giờ bắt đầu");
        }
        long durationMinutes = Duration.between(requestDTO.getStartTime(), requestDTO.getEndTime()).toMinutes();
        if (durationMinutes > 180) {
            throw new RuntimeException("Thời lượng đặt lịch không được vượt quá 3 giờ");
        }
        LocalDateTime requestedDateTime = LocalDateTime.of(requestDTO.getSessionDate(), requestDTO.getStartTime());
        if (requestedDateTime.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Không thể đặt lịch trong quá khứ");
        }

        User student = userRepository.findById(requestDTO.getStudentId())
                .orElseThrow(() -> new RuntimeException("Sinh viên không tồn tại"));
        Lecturer lecturer = lecturerRepository.findById(requestDTO.getLecturerId())
                .orElseThrow(() -> new RuntimeException("Giảng viên không tồn tại"));

        if (requestDTO.getDepartmentId() != null && (lecturer.getDepartment() == null || !requestDTO.getDepartmentId().equals(lecturer.getDepartment().getId()))) {
            throw new RuntimeException("Giảng viên không thuộc khoa đã chọn");
        }

        List<MentoringSession> sameDateSessions = mentoringSessionRepository.findByLecturerIdAndSessionDate(lecturer.getUserId(), requestDTO.getSessionDate());
        boolean conflict = sameDateSessions.stream()
                .filter(session -> session.getStatus() != MentoringSessionStatus.CANCELED)
                .anyMatch(session -> overlaps(requestDTO.getStartTime(), requestDTO.getEndTime(), session.getStartTime(), session.getEndTime()));
        if (conflict) {
            throw new RuntimeException("Giảng viên đã có lịch trong khung giờ này");
        }

        MentoringSession session = MentoringSession.builder()
                .student(student)
                .lecturer(lecturer)
                .sessionDate(requestDTO.getSessionDate())
                .startTime(requestDTO.getStartTime())
                .endTime(requestDTO.getEndTime())
                .status(MentoringSessionStatus.PENDING)
                .build();
        mentoringSessionRepository.save(session);
    }

    @Override
    @Transactional
    public void cancelSession(Integer sessionId, Integer studentId) {
        MentoringSession session = mentoringSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Lịch cố vấn không tồn tại"));
        if (session.getStudent() == null || !session.getStudent().getId().equals(studentId)) {
            throw new RuntimeException("Lịch này không thuộc về bạn");
        }
        if (session.getStatus() != MentoringSessionStatus.PENDING) {
            throw new RuntimeException("Chỉ có thể hủy lịch đang chờ");
        }
        session.setStatus(MentoringSessionStatus.CANCELED);
        mentoringSessionRepository.save(session);
    }

    private LecturerDTO toLecturerDTO(Lecturer lecturer) {
        return LecturerDTO.builder()
                .userId(lecturer.getUserId())
                .fullName(lecturerFullName(lecturer))
                .specialization(lecturer.getSpecialization())
                .departmentName(lecturer.getDepartment() != null ? lecturer.getDepartment().getDepartmentName() : null)
                .departmentId(lecturer.getDepartment() != null ? lecturer.getDepartment().getId() : null)
                .build();
    }

    private String lecturerFullName(Lecturer lecturer) {
        if (lecturer.getUser() != null && lecturer.getUser().getUserProfile() != null) {
            return lecturer.getUser().getUserProfile().getFullName();
        }
        return "";
    }

    private Integer toInteger(Object value) {
        return value == null ? null : ((Number) value).intValue();
    }

    private LocalDate toLocalDate(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof LocalDate localDate) {
            return localDate;
        }
        if (value instanceof Date date) {
            return date.toLocalDate();
        }
        return LocalDate.parse(value.toString());
    }

    private LocalTime toLocalTime(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof LocalTime localTime) {
            return localTime;
        }
        if (value instanceof Time time) {
            return time.toLocalTime();
        }
        return LocalTime.parse(value.toString());
    }

    private boolean overlaps(LocalTime startA, LocalTime endA, LocalTime startB, LocalTime endB) {
        return startA.isBefore(endB) && endA.isAfter(startB);
    }
}

