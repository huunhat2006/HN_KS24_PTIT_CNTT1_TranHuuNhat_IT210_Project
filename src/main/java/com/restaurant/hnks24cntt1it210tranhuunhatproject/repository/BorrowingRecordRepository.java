package com.restaurant.hnks24cntt1it210tranhuunhatproject.repository;

import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.BorrowingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BorrowingRecordRepository extends JpaRepository<BorrowingRecord, Integer> {

	@Query(value = "SELECT br.id as borrowingRecordId, ms.id as sessionId, up.full_name as studentName, lu.full_name as lecturerName, e.equipment_name as equipmentName, lt.type_name as labTypeName, bd.quantity as quantity " +
			"FROM borrowing_records br " +
			"JOIN mentoring_sessions ms ON br.session_id = ms.id " +
			"JOIN users su ON ms.student_id = su.id " +
			"JOIN user_profiles up ON su.id = up.user_id " +
			"JOIN lecturers l ON ms.lecturer_id = l.user_id " +
			"JOIN users lu_user ON l.user_id = lu_user.id " +
			"JOIN user_profiles lu ON lu_user.id = lu.user_id " +
			"JOIN borrowing_details bd ON bd.borrowing_record_id = br.id " +
			"JOIN equipments e ON bd.equipment_id = e.id " +
			"LEFT JOIN lab_types lt ON e.lab_type_id = lt.id " +
			"WHERE br.status = 'PENDING_APPROVAL'", nativeQuery = true)
	List<Object[]> findPendingBorrowRequestsRaw();

	@Query(value = "SELECT br.id as borrowingRecordId, ms.id as sessionId, up.full_name as studentName, lu.full_name as lecturerName, e.equipment_name as equipmentName, lt.type_name as labTypeName, bd.quantity as quantity " +
			"FROM borrowing_records br " +
			"JOIN mentoring_sessions ms ON br.session_id = ms.id " +
			"JOIN users su ON ms.student_id = su.id " +
			"JOIN user_profiles up ON su.id = up.user_id " +
			"JOIN lecturers l ON ms.lecturer_id = l.user_id " +
			"JOIN users lu_user ON l.user_id = lu_user.id " +
			"JOIN user_profiles lu ON lu_user.id = lu.user_id " +
			"JOIN borrowing_details bd ON bd.borrowing_record_id = br.id " +
			"JOIN equipments e ON bd.equipment_id = e.id " +
			"LEFT JOIN lab_types lt ON e.lab_type_id = lt.id " +
			"WHERE br.status = 'PENDING_APPROVAL' " +
			"ORDER BY " +
			"CASE WHEN :sort = 'lecturer_asc' THEN lu.full_name END ASC, " +
			"CASE WHEN :sort = 'equipment_asc' THEN e.equipment_name END ASC, " +
			"CASE WHEN :sort = 'quantity_asc' THEN bd.quantity END ASC, " +
			"br.id ASC", nativeQuery = true)
	List<Object[]> findPendingBorrowRequestsRaw(@Param("sort") String sort);

}