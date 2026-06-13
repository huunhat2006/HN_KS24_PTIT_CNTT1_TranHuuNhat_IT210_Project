package com.restaurant.hnks24cntt1it210tranhuunhatproject.controller;

import com.restaurant.hnks24cntt1it210tranhuunhatproject.config.WebRequestSupport;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.User;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/mentoring")
public class AdminMentoringController {

    private final AdminService adminService;

    @GetMapping
    public String mentoringList(
            @RequestParam(value = "sort", defaultValue = "date_asc") String sort,
            HttpSession session,
            HttpServletRequest request,
            Model model
    ) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("mentoringSessions", adminService.getMentoringSessions(sort));
        model.addAttribute("currentSort", sort);
        model.addAttribute("pageTitle", "Quản lý Lịch Cố vấn");

        return WebRequestSupport.view("admin/mentoring-list", WebRequestSupport.isAjaxRequest(request, null));
    }
}