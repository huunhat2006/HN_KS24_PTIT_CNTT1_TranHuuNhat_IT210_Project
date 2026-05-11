package com.restaurant.hnks24cntt1it210tranhuunhatproject.controller;

import com.restaurant.hnks24cntt1it210tranhuunhatproject.config.WebRequestSupport;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.dto.AdminDashboardDTO;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.User;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminDashboardController {

    private final AdminService adminService;

    @GetMapping({"", "/"})
    public String home(HttpSession session, HttpServletRequest request, Model model) {
        return dashboard(session, request, model);
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, HttpServletRequest request, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }

        AdminDashboardDTO dto = adminService.getDashboardStats();
        model.addAttribute("totalEquipments", dto.getTotalEquipments());
        model.addAttribute("totalUsers", dto.getTotalUsers());
        model.addAttribute("totalMentoringSessions", dto.getTotalMentoringSessions());
        model.addAttribute("topLecturerNamesJson", toJsonStrings(dto.getTopLecturerNames()));
        model.addAttribute("topLecturerCountsJson", toJsonNumbers(dto.getTopLecturerCounts()));
        model.addAttribute("overviewLabelsJson", toJsonStrings(dto.getOverviewLabels()));
        model.addAttribute("overviewValuesJson", toJsonNumbers(dto.getOverviewValues()));
        model.addAttribute("pageTitle", "Admin Dashboard");
        return WebRequestSupport.view("admin/dashboard", WebRequestSupport.isAjaxRequest(request, null));
    }

    @GetMapping("/pending-borrows")
    public String pendingBorrows(
            @RequestParam(value = "sort", defaultValue = "lecturer_asc") String sort,
            HttpSession session,
            HttpServletRequest request,
            Model model
    ) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("pendingBorrows", adminService.getPendingBorrowRequests(sort));
        model.addAttribute("currentSort", sort);
        model.addAttribute("pageTitle", "Thiết bị chờ mượn");
        return WebRequestSupport.view("admin/pending-borrows", WebRequestSupport.isAjaxRequest(request, null));
    }

    @PostMapping("/pending-borrows/confirm")
    public String confirmPendingBorrow(
            @RequestParam("borrowingRecordId") Integer borrowingRecordId,
            @RequestParam(value = "sort", defaultValue = "lecturer_asc") String sort,
            HttpSession session,
            HttpServletRequest request,
            Model model
    ) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }

        try {
            adminService.confirmBorrowingIssuance(borrowingRecordId);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
        }

        model.addAttribute("pendingBorrows", adminService.getPendingBorrowRequests(sort));
        model.addAttribute("currentSort", sort);
        model.addAttribute("pageTitle", "Thiết bị chờ mượn");
        return WebRequestSupport.view("admin/pending-borrows", WebRequestSupport.isAjaxRequest(request, null));
    }

    private String toJsonStrings(java.util.List<String> list) {
        java.util.List<String> safe = list == null ? java.util.Collections.emptyList() : list;
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < safe.size(); i++) {
            String v = safe.get(i) == null ? "" : safe.get(i);
            sb.append('"').append(v.replace("\\", "\\\\").replace("\"", "\\\"")).append('"');
            if (i < safe.size() - 1) {
                sb.append(',');
            }
        }
        sb.append(']');
        return sb.toString();
    }

    private String toJsonNumbers(java.util.List<Long> list) {
        java.util.List<Long> safe = list == null ? java.util.Collections.emptyList() : list;
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < safe.size(); i++) {
            sb.append(safe.get(i) == null ? 0 : safe.get(i));
            if (i < safe.size() - 1) {
                sb.append(',');
            }
        }
        sb.append(']');
        return sb.toString();
    }
}