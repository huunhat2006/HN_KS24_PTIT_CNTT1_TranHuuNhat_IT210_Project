package com.restaurant.hnks24cntt1it210tranhuunhatproject.repository;

import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.MentoringSession;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.enums.MentoringSessionStatus;
import java.time.LocalDate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MentoringSessionRepository extends JpaRepository<MentoringSession, Integer> {

	@Query("SELECT s.lecturer.user.userProfile.fullName as name, COUNT(s) as cnt " +
			"FROM MentoringSession s GROUP BY s.lecturer.user.userProfile.fullName ORDER BY cnt DESC")
	List<Object[]> findTopLecturers();

	@Query("SELECT s.lecturer.user.userProfile.fullName as name, COUNT(s) as cnt " +
			"FROM MentoringSession s GROUP BY s.lecturer.user.userProfile.fullName ORDER BY cnt DESC")
	List<Object[]> findTopLecturers(Pageable pageable);

	@Query("SELECT DISTINCT s FROM MentoringSession s " +
			"LEFT JOIN FETCH s.student student " +
			"LEFT JOIN FETCH student.userProfile studentProfile " +
			"LEFT JOIN FETCH s.lecturer lecturer " +
			"LEFT JOIN FETCH lecturer.user lecturerUser " +
			"LEFT JOIN FETCH lecturerUser.userProfile lecturerProfile")
	List<MentoringSession> findAllWithDetails();

	@Query("SELECT s FROM MentoringSession s WHERE s.lecturer.userId = :lecturerId")
	List<MentoringSession> findByLecturerId(@Param("lecturerId") Integer lecturerId);

	@Query("SELECT s FROM MentoringSession s WHERE s.lecturer.userId = :lecturerId AND s.sessionDate = :sessionDate")
	List<MentoringSession> findByLecturerIdAndSessionDate(@Param("lecturerId") Integer lecturerId, @Param("sessionDate") LocalDate sessionDate);

	@Query("SELECT COUNT(s) FROM MentoringSession s WHERE s.student.id = :studentId AND s.status = :status")
	long countStudentSessionsByStatus(@Param("studentId") Integer studentId, @Param("status") MentoringSessionStatus status);

	@Query("SELECT COUNT(s) FROM MentoringSession s WHERE s.student.id = :studentId")
	long countStudentSessions(@Param("studentId") Integer studentId);

	@Query("SELECT DISTINCT s FROM MentoringSession s " +
			"LEFT JOIN FETCH s.lecturer lecturer " +
			"LEFT JOIN FETCH lecturer.user lecturerUser " +
			"LEFT JOIN FETCH lecturerUser.userProfile lecturerProfile " +
			"WHERE s.student.id = :studentId " +
			"ORDER BY s.sessionDate ASC, s.startTime ASC")
	List<MentoringSession> findStudentSessionsOrderByDateAsc(@Param("studentId") Integer studentId);

	@Query("SELECT DISTINCT s FROM MentoringSession s " +
			"LEFT JOIN FETCH s.lecturer lecturer " +
			"LEFT JOIN FETCH lecturer.user lecturerUser " +
			"LEFT JOIN FETCH lecturerUser.userProfile lecturerProfile " +
			"WHERE s.student.id = :studentId " +
			"ORDER BY s.sessionDate DESC, s.startTime DESC")
	List<MentoringSession> findStudentSessionsOrderByDateDesc(@Param("studentId") Integer studentId);

	@Query("SELECT DISTINCT s FROM MentoringSession s " +
			"LEFT JOIN FETCH s.lecturer lecturer " +
			"LEFT JOIN FETCH lecturer.user lecturerUser " +
			"LEFT JOIN FETCH lecturerUser.userProfile lecturerProfile " +
			"WHERE s.student.id = :studentId " +
			"ORDER BY s.startTime ASC, s.sessionDate ASC")
	List<MentoringSession> findStudentSessionsOrderByTimeAsc(@Param("studentId") Integer studentId);

	@Query("SELECT DISTINCT s FROM MentoringSession s " +
			"LEFT JOIN FETCH s.lecturer lecturer " +
			"LEFT JOIN FETCH lecturer.user lecturerUser " +
			"LEFT JOIN FETCH lecturerUser.userProfile lecturerProfile " +
			"WHERE s.student.id = :studentId " +
			"ORDER BY s.startTime DESC, s.sessionDate DESC")
	List<MentoringSession> findStudentSessionsOrderByTimeDesc(@Param("studentId") Integer studentId);

	@Query(value = "SELECT ms.id AS sessionId, lp.full_name AS lecturerName, ms.session_date AS sessionDate, ms.start_time AS startTime, ms.end_time AS endTime, ae.evaluation_notes AS evaluationNotes, ae.performance_rating AS performanceRating, e.equipment_name AS equipmentName " +
			"FROM mentoring_sessions ms " +
			"JOIN lecturers l ON ms.lecturer_id = l.user_id " +
			"JOIN users lu ON l.user_id = lu.id " +
			"JOIN user_profiles lp ON lu.id = lp.user_id " +
			"LEFT JOIN academic_evaluations ae ON ae.session_id = ms.id " +
			"LEFT JOIN borrowing_records br ON br.session_id = ms.id " +
			"LEFT JOIN borrowing_details bd ON bd.borrowing_record_id = br.id " +
			"LEFT JOIN equipments e ON e.id = bd.equipment_id " +
			"WHERE ms.student_id = :studentId AND ms.status = 'COMPLETED' " +
			"ORDER BY ms.session_date DESC, ms.start_time DESC, e.equipment_name ASC", nativeQuery = true)
	List<Object[]> findStudentAcademicProfileRows(@Param("studentId") Integer studentId);

	long countByLecturer_UserIdAndStatus(Integer lecturerId, MentoringSessionStatus status);

	@Query("SELECT DISTINCT s FROM MentoringSession s " +
			"LEFT JOIN FETCH s.student student " +
			"LEFT JOIN FETCH student.userProfile studentProfile " +
			"LEFT JOIN FETCH s.lecturer lecturer " +
			"LEFT JOIN FETCH lecturer.user lecturerUser " +
			"LEFT JOIN FETCH lecturerUser.userProfile lecturerProfile " +
			"LEFT JOIN FETCH s.borrowingRecords borrowingRecords " +
			"LEFT JOIN FETCH s.academicEvaluation evaluation " +
			"WHERE lecturer.userId = :lecturerId AND s.status = :status")
	List<MentoringSession> findByLecturerIdAndStatusWithEvaluation(@Param("lecturerId") Integer lecturerId, @Param("status") MentoringSessionStatus status);

	@Query("SELECT DISTINCT s FROM MentoringSession s " +
			"LEFT JOIN FETCH s.student student " +
			"LEFT JOIN FETCH student.userProfile studentProfile " +
			"LEFT JOIN FETCH s.lecturer lecturer " +
			"LEFT JOIN FETCH lecturer.user lecturerUser " +
			"LEFT JOIN FETCH lecturerUser.userProfile lecturerProfile " +
			"LEFT JOIN FETCH s.borrowingRecords borrowingRecords " +
			"LEFT JOIN FETCH s.academicEvaluation evaluation " +
			"WHERE lecturer.userId = :lecturerId AND s.status = :status " +
			"ORDER BY s.sessionDate ASC, s.startTime ASC")
	List<MentoringSession> findByLecturerIdAndStatusWithEvaluationOrderByDateTimeAsc(@Param("lecturerId") Integer lecturerId, @Param("status") MentoringSessionStatus status);

	@Query("SELECT DISTINCT s FROM MentoringSession s " +
			"LEFT JOIN FETCH s.student student " +
			"LEFT JOIN FETCH student.userProfile studentProfile " +
			"LEFT JOIN FETCH s.lecturer lecturer " +
			"LEFT JOIN FETCH lecturer.user lecturerUser " +
			"LEFT JOIN FETCH lecturerUser.userProfile lecturerProfile " +
			"LEFT JOIN FETCH s.borrowingRecords borrowingRecords " +
			"LEFT JOIN FETCH s.academicEvaluation evaluation " +
			"WHERE lecturer.userId = :lecturerId AND s.status = :status " +
			"ORDER BY s.sessionDate DESC, s.startTime DESC")
	List<MentoringSession> findByLecturerIdAndStatusWithEvaluationOrderByDateTimeDesc(@Param("lecturerId") Integer lecturerId, @Param("status") MentoringSessionStatus status);

}