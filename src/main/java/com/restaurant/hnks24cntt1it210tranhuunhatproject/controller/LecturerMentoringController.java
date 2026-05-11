package com.restaurant.hnks24cntt1it210tranhuunhatproject.controller;

import com.restaurant.hnks24cntt1it210tranhuunhatproject.config.WebRequestSupport;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.dto.ApproveSessionRequestDTO;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.dto.EvaluationRequestDTO;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.User;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.service.LecturerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/lecturer")
public class LecturerMentoringController {

    private final LecturerService lecturerService;

    @GetMapping("/pending-sessions")
    public String pendingSessions(
            @RequestParam(value = "sort", defaultValue = "date_asc") String sort,
            HttpSession session,
            HttpServletRequest request,
            Model model
    ) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("mentoringSessions", lecturerService.getPendingSessions(loggedUser.getId(), sort));
        model.addAttribute("currentSort", sort);
        model.addAttribute("availableEquipments", lecturerService.getAvailableEquipments());
        return WebRequestSupport.view("lecturer/mentoring-pending", WebRequestSupport.isAjaxRequest(request, null));
    }

    @GetMapping("/completed-sessions")
    public String completedSessions(
            @RequestParam(value = "sort", defaultValue = "date_asc") String sort,
            HttpSession session,
            HttpServletRequest request,
            Model model
    ) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }

        List<com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.MentoringSession> sessions = lecturerService.getCompletedSessions(loggedUser.getId(), sort);
        model.addAttribute("mentoringSessions", sessions);
        model.addAttribute("currentSort", sort);
        model.addAttribute("blockedEvaluationSessionIds", sessions.stream()
                .filter(sessionItem -> sessionItem.getBorrowingRecords() != null && sessionItem.getBorrowingRecords().stream().anyMatch(record -> record.getStatus() == com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.enums.BorrowingStatus.PENDING_APPROVAL))
                .map(com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.MentoringSession::getId)
                .collect(Collectors.toList()));
        return WebRequestSupport.view("lecturer/mentoring-completed", WebRequestSupport.isAjaxRequest(request, null));
    }

    @GetMapping("/overdue-sessions")
    public String overdueSessions(
            @RequestParam(value = "sort", defaultValue = "date_asc") String sort,
            HttpSession session,
            HttpServletRequest request,
            Model model
    ) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("mentoringSessions", lecturerService.getOverdueSessions(loggedUser.getId(), sort));
        model.addAttribute("currentSort", sort);
        return WebRequestSupport.view("lecturer/overdue-sessions", WebRequestSupport.isAjaxRequest(request, null));
    }

    @PostMapping("/approve-session")
    public String approveSession(
            @ModelAttribute ApproveSessionRequestDTO requestDTO,
            HttpSession session,
            HttpServletRequest request,
            Model model
    ) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }

        try {
            lecturerService.approveSession(loggedUser.getId(), requestDTO);
            model.addAttribute("successMessage", "Đã duyệt lịch và gửi yêu cầu mượn thiết bị, chờ admin duyệt.");
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
        }

        model.addAttribute("mentoringSessions", lecturerService.getPendingSessions(loggedUser.getId(), "date_asc"));
        model.addAttribute("currentSort", "date_asc");
        model.addAttribute("availableEquipments", lecturerService.getAvailableEquipments());
        return WebRequestSupport.view("lecturer/mentoring-pending", WebRequestSupport.isAjaxRequest(request, null));
    }

    @PostMapping("/evaluate-session")
    public String evaluateSession(
            @ModelAttribute EvaluationRequestDTO requestDTO,
            HttpSession session,
            HttpServletRequest request,
            Model model
    ) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }

        try {
            lecturerService.evaluateSession(loggedUser.getId(), requestDTO);
            model.addAttribute("successMessage", "Đã lưu đánh giá thành công.");
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
        }

        List<com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.MentoringSession> sessions = lecturerService.getCompletedSessions(loggedUser.getId(), "date_asc");
        model.addAttribute("mentoringSessions", sessions);
        model.addAttribute("currentSort", "date_asc");
        model.addAttribute("blockedEvaluationSessionIds", sessions.stream()
                .filter(sessionItem -> sessionItem.getBorrowingRecords() != null && sessionItem.getBorrowingRecords().stream().anyMatch(record -> record.getStatus() == com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.enums.BorrowingStatus.PENDING_APPROVAL))
                .map(com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.MentoringSession::getId)
                .collect(Collectors.toList()));
        return WebRequestSupport.view("lecturer/mentoring-completed", WebRequestSupport.isAjaxRequest(request, null));
    }
}