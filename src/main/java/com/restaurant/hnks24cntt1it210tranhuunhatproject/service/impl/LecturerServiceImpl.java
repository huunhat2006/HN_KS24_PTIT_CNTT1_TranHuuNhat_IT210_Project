package com.restaurant.hnks24cntt1it210tranhuunhatproject.service.impl;

import com.restaurant.hnks24cntt1it210tranhuunhatproject.dto.ApproveSessionRequestDTO;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.dto.EquipmentDTO;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.dto.EvaluationRequestDTO;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.dto.LecturerDashboardDTO;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.AcademicEvaluation;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.BorrowingDetail;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.BorrowingDetailId;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.BorrowingRecord;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.Equipment;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.MentoringSession;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.enums.BorrowingStatus;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.enums.MentoringSessionStatus;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.repository.AcademicEvaluationRepository;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.repository.BorrowingRecordRepository;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.repository.EquipmentRepository;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.repository.MentoringSessionRepository;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.service.LecturerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LecturerServiceImpl implements LecturerService {

    private final MentoringSessionRepository mentoringSessionRepository;
    private final AcademicEvaluationRepository academicEvaluationRepository;
    private final BorrowingRecordRepository borrowingRecordRepository;
    private final EquipmentRepository equipmentRepository;

    @Override
    @Transactional(readOnly = true)
    public LecturerDashboardDTO getDashboard(Integer lecturerId) {
        long pendingCount = mentoringSessionRepository.countByLecturer_UserIdAndStatus(lecturerId, MentoringSessionStatus.PENDING);
        long completedCount = mentoringSessionRepository.countByLecturer_UserIdAndStatus(lecturerId, MentoringSessionStatus.COMPLETED);
        long overdueCount = mentoringSessionRepository.countByLecturer_UserIdAndStatus(lecturerId, MentoringSessionStatus.OVERDUE);
        long totalCount = pendingCount + completedCount + overdueCount;
        return LecturerDashboardDTO.builder()
                .pendingCount(pendingCount)
                .completedCount(completedCount)
                .overdueCount(overdueCount)
                .totalCount(totalCount)
                .labels(List.of("Đang chờ", "Đã hoàn thành", "Đã quá hạn"))
                .values(List.of(pendingCount, completedCount, overdueCount))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MentoringSession> getPendingSessions(Integer lecturerId, String sort) {
        return sortSessions(mentoringSessionRepository.findByLecturerIdAndStatusWithEvaluation(lecturerId, MentoringSessionStatus.PENDING), sort);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MentoringSession> getCompletedSessions(Integer lecturerId, String sort) {
        return sortSessions(mentoringSessionRepository.findByLecturerIdAndStatusWithEvaluation(lecturerId, MentoringSessionStatus.COMPLETED), sort);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MentoringSession> getOverdueSessions(Integer lecturerId, String sort) {
        return sortSessions(mentoringSessionRepository.findByLecturerIdAndStatusWithEvaluation(lecturerId, MentoringSessionStatus.OVERDUE), sort);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EquipmentDTO> getAvailableEquipments() {
        List<EquipmentDTO> items = new ArrayList<>();
        for (Equipment equipment : equipmentRepository.findAllWithLabType()) {
            Integer available = equipment.getAvailableQuantity();
            if (available != null && available > 0) {
                items.add(EquipmentDTO.builder()
                        .id(equipment.getId())
                        .equipmentName(equipment.getEquipmentName())
                        .labTypeId(equipment.getLabType() != null ? equipment.getLabType().getId() : null)
                        .labTypeName(equipment.getLabType() != null ? equipment.getLabType().getTypeName() : null)
                        .description(equipment.getDescription())
                        .totalQuantity(equipment.getTotalQuantity())
                        .availableQuantity(equipment.getAvailableQuantity())
                        .build());
            }
        }
        return items;
    }

    @Override
    @Transactional
    public void approveSession(Integer lecturerId, ApproveSessionRequestDTO requestDTO) {
        MentoringSession session = mentoringSessionRepository.findById(requestDTO.getSessionId())
                .orElseThrow(() -> new RuntimeException("Lịch cố vấn không tồn tại"));
        if (session.getLecturer() == null || !session.getLecturer().getUserId().equals(lecturerId)) {
            throw new RuntimeException("Bạn không có quyền duyệt lịch này");
        }
        if (session.getStatus() != MentoringSessionStatus.PENDING) {
            throw new RuntimeException("Lịch này không ở trạng thái chờ duyệt");
        }
        Equipment equipment = equipmentRepository.findById(requestDTO.getEquipmentId())
                .orElseThrow(() -> new RuntimeException("Thiết bị không tồn tại"));
        Integer availableQuantity = equipment.getAvailableQuantity() == null ? 0 : equipment.getAvailableQuantity();
        if (availableQuantity <= 0) {
            throw new RuntimeException("Thiết bị đã hết số lượng khả dụng");
        }
        session.setStatus(MentoringSessionStatus.COMPLETED);
        mentoringSessionRepository.save(session);

        BorrowingRecord borrowingRecord = BorrowingRecord.builder()
                .session(session)
                .status(BorrowingStatus.PENDING_APPROVAL)
                .build();
        borrowingRecord = borrowingRecordRepository.save(borrowingRecord);

        BorrowingDetail borrowingDetail = BorrowingDetail.builder()
                .id(BorrowingDetailId.builder()
                        .borrowingRecordId(borrowingRecord.getId())
                        .equipmentId(equipment.getId())
                        .build())
                .borrowingRecord(borrowingRecord)
                .equipment(equipment)
                .quantity(1)
                .build();
        borrowingRecord.getBorrowingDetails().add(borrowingDetail);
        borrowingRecordRepository.save(borrowingRecord);

        // Do NOT decrement available quantity here. Admin will confirm issuance and update equipment quantities.
    }

    @Override
    @Transactional
    public void evaluateSession(Integer lecturerId, EvaluationRequestDTO requestDTO) {
        MentoringSession session = mentoringSessionRepository.findById(requestDTO.getSessionId())
                .orElseThrow(() -> new RuntimeException("Lịch cố vấn không tồn tại"));
        if (session.getLecturer() == null || !session.getLecturer().getUserId().equals(lecturerId)) {
            throw new RuntimeException("Bạn không có quyền đánh giá lịch này");
        }
        if (session.getStatus() != MentoringSessionStatus.COMPLETED) {
            throw new RuntimeException("Chỉ có thể đánh giá lịch đã hoàn thành");
        }
        // prevent evaluation when a borrowing for this session is still pending approval
        if (session.getBorrowingRecords() != null) {
            boolean hasPending = session.getBorrowingRecords().stream()
                    .anyMatch(br -> br.getStatus() == BorrowingStatus.PENDING_APPROVAL);
            if (hasPending) {
                throw new RuntimeException("Không thể đánh giá: thiết bị đang chờ cấp phát.");
            }
        }
        if (requestDTO.getPerformanceRating() == null || requestDTO.getPerformanceRating() < 1 || requestDTO.getPerformanceRating() > 5) {
            throw new RuntimeException("Điểm đánh giá phải từ 1 đến 5");
        }
        AcademicEvaluation evaluation = academicEvaluationRepository.findBySession_Id(session.getId())
                .orElseGet(() -> AcademicEvaluation.builder().session(session).build());
        evaluation.setSession(session);
        evaluation.setPerformanceRating(requestDTO.getPerformanceRating());
        evaluation.setEvaluationNotes(requestDTO.getEvaluationNotes());
        academicEvaluationRepository.save(evaluation);
    }

    private List<MentoringSession> sortSessions(List<MentoringSession> sessions, String sort) {
        List<MentoringSession> sorted = new ArrayList<>(sessions);
        Comparator<MentoringSession> comparator;
        if ("date_desc".equals(sort)) {
            comparator = Comparator.comparing(MentoringSession::getSessionDate).reversed().thenComparing(MentoringSession::getStartTime, Comparator.reverseOrder());
        } else if ("time_asc".equals(sort)) {
            comparator = Comparator.comparing(MentoringSession::getStartTime).thenComparing(MentoringSession::getSessionDate);
        } else if ("time_desc".equals(sort)) {
            comparator = Comparator.comparing(MentoringSession::getStartTime).reversed().thenComparing(MentoringSession::getSessionDate, Comparator.reverseOrder());
        } else {
            comparator = Comparator.comparing(MentoringSession::getSessionDate).thenComparing(MentoringSession::getStartTime);
        }
        sorted.sort(comparator);
        return sorted;
    }
}




