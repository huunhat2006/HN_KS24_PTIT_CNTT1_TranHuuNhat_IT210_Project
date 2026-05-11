package com.restaurant.hnks24cntt1it210tranhuunhatproject.controller;

import com.restaurant.hnks24cntt1it210tranhuunhatproject.config.WebRequestSupport;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.dto.BookSessionDTO;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.User;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.service.StudentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/student")
public class StudentMentoringController {

    private final StudentService studentService;

    @GetMapping("/book-session")
    public String bookSessionForm(HttpSession session, HttpServletRequest request, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }

        if (!model.containsAttribute("bookSessionDTO")) {
            model.addAttribute("bookSessionDTO", new BookSessionDTO());
        }

        model.addAttribute("departments", studentService.getAllDepartments());
        model.addAttribute("lecturers", studentService.getLecturers(null));
        return WebRequestSupport.view("student/book-session", WebRequestSupport.isAjaxRequest(request, null));
    }

    @PostMapping("/book-session")
    public String bookSessionSubmit(
            @ModelAttribute("bookSessionDTO") BookSessionDTO bookSessionDTO,
            HttpSession session,
            HttpServletRequest request,
            Model model
    ) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }

        bookSessionDTO.setStudentId(loggedUser.getId());

        try {
            studentService.bookSession(bookSessionDTO);
            model.addAttribute("successMessage", "Đặt lịch tư vấn thành công.");
            model.addAttribute("bookSessionDTO", new BookSessionDTO()); // Reset form sau khi thành công
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
        }

        model.addAttribute("departments", studentService.getAllDepartments());
        model.addAttribute("lecturers", studentService.getLecturers(bookSessionDTO.getDepartmentId()));
        return WebRequestSupport.view("student/book-session", WebRequestSupport.isAjaxRequest(request, null));
    }

    @GetMapping("/my-sessions")
    public String mySessions(
            @RequestParam(value = "sort", defaultValue = "date_asc") String sort,
            HttpSession session,
            HttpServletRequest request,
            Model model
    ) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }

        var sessions = studentService.getStudentSessions(loggedUser.getId(), sort);
        model.addAttribute("mentoringSessions", sessions);
        model.addAttribute("currentSort", sort);

        return WebRequestSupport.view("student/my-sessions", WebRequestSupport.isAjaxRequest(request, null));
    }

    @PostMapping("/cancel-session/{id}")
    public String cancelSession(
            @PathVariable Integer id,
            @RequestParam(value = "sort", defaultValue = "date_asc") String sort,
            HttpSession session,
            HttpServletRequest request,
            Model model
    ) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }

        try {
            studentService.cancelSession(id, loggedUser.getId());
            model.addAttribute("successMessage", "Hủy lịch thành công.");
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
        }

        var sessions = studentService.getStudentSessions(loggedUser.getId(), sort);
        model.addAttribute("mentoringSessions", sessions);
        model.addAttribute("currentSort", sort);
        return WebRequestSupport.view("student/my-sessions", WebRequestSupport.isAjaxRequest(request, null));
    }
}