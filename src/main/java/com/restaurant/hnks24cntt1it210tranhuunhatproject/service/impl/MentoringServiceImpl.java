package com.restaurant.hnks24cntt1it210tranhuunhatproject.service.impl;

import com.restaurant.hnks24cntt1it210tranhuunhatproject.dto.LecturerDTO;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.dto.MentoringSessionRequestDTO;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.dto.MentoringSessionResponseDTO;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.BorrowingDetail;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.BorrowingDetailId;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.BorrowingRecord;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.Department;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.Equipment;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.Lecturer;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.MentoringSession;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.User;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.enums.BorrowingStatus;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.enums.MentoringSessionStatus;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.repository.BorrowingDetailRepository;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.repository.BorrowingRecordRepository;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.repository.DepartmentRepository;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.repository.EquipmentRepository;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.repository.LecturerRepository;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.repository.MentoringSessionRepository;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.repository.UserRepository;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.service.MentoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MentoringServiceImpl implements MentoringService {

    private final MentoringSessionRepository mentoringSessionRepository;
    private final UserRepository userRepository;
    private final LecturerRepository lecturerRepository;
    private final DepartmentRepository departmentRepository;
    private final BorrowingRecordRepository borrowingRecordRepository;
    private final EquipmentRepository equipmentRepository;
    private final BorrowingDetailRepository borrowingDetailRepository;

    @Override
    @Transactional
    public MentoringSessionResponseDTO bookSession(MentoringSessionRequestDTO requestDTO) {
        if (requestDTO.getSessionDate() == null || requestDTO.getStartTime() == null || requestDTO.getEndTime() == null) {
            throw new RuntimeException("Ngày và giờ cố vấn không được để trống");
        }

        if (!requestDTO.getEndTime().isAfter(requestDTO.getStartTime())) {
            throw new RuntimeException("Giờ kết thúc phải sau giờ bắt đầu");
        }

        LocalDateTime requestedDateTime = LocalDateTime.of(requestDTO.getSessionDate(), requestDTO.getStartTime());
        if (requestedDateTime.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Không thể đặt lịch ở thời gian trong quá khứ");
        }

        User student = findUserById(requestDTO.getStudentId())
                .orElseThrow(() -> new RuntimeException("Sinh viên không tìm thấy"));

        Lecturer lecturer = findLecturerById(requestDTO.getLecturerId())
                .orElseThrow(() -> new RuntimeException("Giảng viên không tìm thấy"));

        boolean duplicated = mentoringSessionRepository.findAll()
                .stream()
                .anyMatch(session ->
                        session.getLecturer() != null
                                && session.getLecturer().getUserId().equals(requestDTO.getLecturerId())
                                && session.getSessionDate().equals(requestDTO.getSessionDate())
                                && session.getStartTime().equals(requestDTO.getStartTime())
                                && (session.getStatus() == MentoringSessionStatus.PENDING || session.getStatus() == MentoringSessionStatus.COMPLETED)
                );

        if (duplicated) {
            throw new RuntimeException("Giảng viên đã có lịch cố vấn vào thời gian này");
        }

        MentoringSession session = MentoringSession.builder()
                .student(student)
                .lecturer(lecturer)
                .sessionDate(requestDTO.getSessionDate())
                .startTime(requestDTO.getStartTime())
                .endTime(requestDTO.getEndTime())
                .status(MentoringSessionStatus.PENDING)
                .build();

        MentoringSession savedSession = mentoringSessionRepository.save(session);
        return toResponseDTO(savedSession);
    }

    @Override
    @Transactional
    public MentoringSessionResponseDTO cancelSession(Integer sessionId, Integer studentId) {
        MentoringSession session = findSessionById(sessionId)
                .orElseThrow(() -> new RuntimeException("Lịch cố vấn không tìm thấy"));

        if (session.getStudent() == null || !session.getStudent().getId().equals(studentId)) {
            throw new RuntimeException("Lịch cố vấn này không thuộc về bạn");
        }

        if (session.getStatus() == MentoringSessionStatus.PENDING) {
            session.setStatus(MentoringSessionStatus.CANCELED);
            session = mentoringSessionRepository.save(session);
        }

        return toResponseDTO(session);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LecturerDTO> getAllLecturers() {
        return lecturerRepository.findAll()
                .stream()
                .map(this::toLecturerDTO)
                .collect(Collectors.toList());
    }

    private Optional<User> findUserById(Integer userId) {
        return userRepository.findById(userId);
    }

    private Optional<Lecturer> findLecturerById(Integer lecturerId) {
        return lecturerRepository.findById(lecturerId);
    }

    private Optional<MentoringSession> findSessionById(Integer sessionId) {
        return mentoringSessionRepository.findById(sessionId);
    }

    private MentoringSessionResponseDTO toResponseDTO(MentoringSession session) {
        String studentName = null;
        String studentEmail = null;
        if (session.getStudent() != null && session.getStudent().getUserProfile() != null) {
            studentName = session.getStudent().getUserProfile().getFullName();
            studentEmail = session.getStudent().getUserProfile().getEmail();
        }

        String lecturerName = null;
        String lecturerDepartmentName = null;
        String lecturerSpecialization = null;
        if (session.getLecturer() != null) {
            lecturerSpecialization = session.getLecturer().getSpecialization();
            if (session.getLecturer().getUser() != null && session.getLecturer().getUser().getUserProfile() != null) {
                lecturerName = session.getLecturer().getUser().getUserProfile().getFullName();
            }
            if (session.getLecturer().getDepartment() != null) {
                lecturerDepartmentName = session.getLecturer().getDepartment().getDepartmentName();
            }
        }

        return MentoringSessionResponseDTO.builder()
                .id(session.getId())
                .studentId(session.getStudent() != null ? session.getStudent().getId() : null)
                .studentName(studentName)
                .studentEmail(studentEmail)
                .lecturerId(session.getLecturer() != null ? session.getLecturer().getUserId() : null)
                .lecturerName(lecturerName)
                .lecturerDepartmentName(lecturerDepartmentName)
                .lecturerSpecialization(lecturerSpecialization)
                .sessionDate(session.getSessionDate())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .status(session.getStatus())
                .createdAt(session.getCreatedAt())
                .build();
    }

    private LecturerDTO toLecturerDTO(Lecturer lecturer) {
        String fullName = null;
        if (lecturer.getUser() != null && lecturer.getUser().getUserProfile() != null) {
            fullName = lecturer.getUser().getUserProfile().getFullName();
        }

        String departmentName = null;
        Integer departmentId = null;
        if (lecturer.getDepartment() != null) {
            departmentName = lecturer.getDepartment().getDepartmentName();
            departmentId = lecturer.getDepartment().getId();
        }

        return LecturerDTO.builder()
                .userId(lecturer.getUserId())
                .fullName(fullName)
                .specialization(lecturer.getSpecialization())
                .departmentName(departmentName)
                .departmentId(departmentId)
                .build();
    }

    @Override
    @Transactional
    public void approveSession(Integer sessionId, Integer equipmentId, Integer quantity, Integer approverId) {
        MentoringSession session = findSessionById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (session.getStatus() != MentoringSessionStatus.PENDING) {
            throw new RuntimeException("Session is not pending");
        }

        session.setStatus(MentoringSessionStatus.COMPLETED);
        mentoringSessionRepository.save(session);

        // Create a borrowing record in PENDING_APPROVAL state. Do NOT decrement equipment here;
        // admin must confirm issuance and perform the actual inventory deduction.
        BorrowingRecord borrowingRecord = new BorrowingRecord();
        borrowingRecord.setSession(session);
        borrowingRecord.setStatus(BorrowingStatus.PENDING_APPROVAL);
        borrowingRecord = borrowingRecordRepository.save(borrowingRecord);

        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new RuntimeException("Equipment not found"));


        BorrowingDetailId bdId = new BorrowingDetailId();
        bdId.setBorrowingRecordId(borrowingRecord.getId());
        bdId.setEquipmentId(equipmentId);

        BorrowingDetail borrowingDetail = BorrowingDetail.builder()
                .id(bdId)
                .borrowingRecord(borrowingRecord)
                .equipment(equipment)
                .quantity(quantity)
                .build();

        borrowingDetailRepository.save(borrowingDetail);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MentoringSession> getStudentSessions(Integer studentId, String sort) {
        var sessions = mentoringSessionRepository.findAll().stream()
                .filter(s -> s.getStudent().getId().equals(studentId))
                .collect(Collectors.toList());

        if ("date_asc".equals(sort) || sort == null || sort.isEmpty()) {
            sessions.sort(Comparator.comparing(MentoringSession::getSessionDate)
                    .thenComparing(MentoringSession::getStartTime));
        } else if ("date_desc".equals(sort)) {
            sessions.sort(Comparator.comparing(MentoringSession::getSessionDate).reversed()
                    .thenComparing(Comparator.comparing(MentoringSession::getStartTime).reversed()));
        }

        return sessions;
    }
}