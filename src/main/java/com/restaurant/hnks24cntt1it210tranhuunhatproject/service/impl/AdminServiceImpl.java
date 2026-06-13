package com.restaurant.hnks24cntt1it210tranhuunhatproject.service.impl;

import com.restaurant.hnks24cntt1it210tranhuunhatproject.dto.AdminDashboardDTO;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.dto.PendingBorrowRequestDTO;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.MentoringSession;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.User;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.enums.BorrowingStatus;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.repository.BorrowingRecordRepository;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.repository.EquipmentRepository;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.repository.MentoringSessionRepository;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.repository.UserRepository;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final EquipmentRepository equipmentRepository;
    private final UserRepository userRepository;
    private final MentoringSessionRepository mentoringSessionRepository;
    private final BorrowingRecordRepository borrowingRecordRepository;

    @Override
    @Transactional(readOnly = true)
    public AdminDashboardDTO getDashboardStats() {
        long totalEquipments = equipmentRepository.count();
        long totalUsers = userRepository.count();
        long totalSessions = mentoringSessionRepository.count();
        List<Object[]> topLecturers = mentoringSessionRepository.findTopLecturers(PageRequest.of(0, 5));

        List<String> names = new ArrayList<>();
        List<Long> counts = new ArrayList<>();

        for (Object[] row : topLecturers) {
            names.add(row[0] != null ? row[0].toString() : "");
            counts.add(row[1] != null ? ((Number) row[1]).longValue() : 0L);
        }

        return AdminDashboardDTO.builder()
                .totalEquipments(totalEquipments)
                .totalUsers(totalUsers)
                .totalMentoringSessions(totalSessions)
                .topLecturerNames(names)
                .topLecturerCounts(counts)
                .overviewLabels(List.of("Thiết bị", "Người dùng", "Lịch cố vấn"))
                .overviewValues(List.of(totalEquipments, totalUsers, totalSessions))
                .build();
    }

    @Override
    @Transactional
    public void confirmBorrowingIssuance(Integer borrowingRecordId) {
        var opt = borrowingRecordRepository.findById(borrowingRecordId);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Borrowing record not found: " + borrowingRecordId);
        }

        var record = opt.get();
        if (record.getStatus() == null || record.getStatus() != BorrowingStatus.PENDING_APPROVAL) {
            if (record.getStatus() == BorrowingStatus.ISSUED) {
                return;
            }
            throw new IllegalStateException("Borrowing record is not in pending approval state: " + borrowingRecordId);
        }

        // Validate availability for each borrowing detail and decrement atomically
        if (record.getBorrowingDetails() == null || record.getBorrowingDetails().isEmpty()) {
            throw new IllegalStateException("Borrowing record has no details: " + borrowingRecordId);
        }

        for (var detail : record.getBorrowingDetails()) {
            var equipment = equipmentRepository.findById(detail.getEquipment().getId())
                    .orElseThrow(() -> new IllegalStateException("Equipment not found for detail: " + detail.getId()));
            int avail = equipment.getAvailableQuantity() == null ? 0 : equipment.getAvailableQuantity();
            if (avail < detail.getQuantity()) {
                throw new IllegalStateException("Insufficient quantity for equipment '" + equipment.getEquipmentName() + "'. Requested: " + detail.getQuantity() + ", available: " + avail);
            }
        }

        for (var detail : record.getBorrowingDetails()) {
            var equipment = equipmentRepository.findById(detail.getEquipment().getId())
                    .orElseThrow(() -> new IllegalStateException("Equipment not found for detail: " + detail.getId()));
            int newAvail = Math.max(0, (equipment.getAvailableQuantity() == null ? 0 : equipment.getAvailableQuantity()) - detail.getQuantity());
            equipment.setAvailableQuantity(newAvail);
            equipmentRepository.save(equipment);
        }

        record.setStatus(BorrowingStatus.ISSUED);
        borrowingRecordRepository.save(record);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getTop5Lecturers() {
        return mentoringSessionRepository.findTopLecturers(PageRequest.of(0, 5));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsers(String sort) {
        List<User> users = new ArrayList<>(userRepository.findAllWithProfile());
        Comparator<User> comparator = Comparator.comparing(this::userDisplayName, String.CASE_INSENSITIVE_ORDER);
        if ("name_desc".equals(sort)) {
            comparator = comparator.reversed();
        }
        users.sort(comparator.thenComparing(User::getUsername, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)));
        return users;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MentoringSession> getMentoringSessions(String sort) {
        List<MentoringSession> sessions = new ArrayList<>(mentoringSessionRepository.findAllWithDetails());
        Comparator<MentoringSession> comparator;
        if ("date_desc".equals(sort)) {
            comparator = Comparator.comparing(MentoringSession::getSessionDate, Comparator.nullsLast(Comparator.naturalOrder()))
                    .reversed()
                    .thenComparing(MentoringSession::getStartTime, Comparator.nullsLast(Comparator.reverseOrder()));
        } else {
            comparator = Comparator.comparing(MentoringSession::getSessionDate, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(MentoringSession::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()));
        }
        sessions.sort(comparator);
        return sessions;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PendingBorrowRequestDTO> getPendingBorrowRequests(String sort) {
        String safeSort = (sort == null || sort.isEmpty()) ? "lecturer_asc" : sort;

        List<Object[]> raw = borrowingRecordRepository.findPendingBorrowRequestsRaw(safeSort);
        List<PendingBorrowRequestDTO> list = new ArrayList<>();

        for (Object[] r : raw) {
            PendingBorrowRequestDTO dto = new PendingBorrowRequestDTO();
            dto.setBorrowingRecordId(r[0] != null ? ((Number)r[0]).intValue() : 0);
            dto.setSessionId(r[1] != null ? ((Number)r[1]).intValue() : 0);
            dto.setStudentName((String) r[2]);
            dto.setLecturerName((String) r[3]);
            dto.setEquipmentName((String) r[4]);
            dto.setLabTypeName((String) r[5]);
            dto.setQuantity(r[6] != null ? ((Number)r[6]).intValue() : 0);
            list.add(dto);
        }

        return list;
    }

    private String userDisplayName(User user) {
        if (user == null || user.getUserProfile() == null || user.getUserProfile().getFullName() == null || user.getUserProfile().getFullName().isBlank()) {
            return user != null && user.getUsername() != null ? user.getUsername() : "";
        }
        return user.getUserProfile().getFullName();
    }
}