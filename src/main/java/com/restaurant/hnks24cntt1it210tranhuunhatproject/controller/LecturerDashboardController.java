package com.restaurant.hnks24cntt1it210tranhuunhatproject.controller;

import com.restaurant.hnks24cntt1it210tranhuunhatproject.config.WebRequestSupport;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.dto.LecturerDashboardDTO;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.User;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.repository.MentoringSessionRepository;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.service.LecturerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/lecturer")
public class LecturerDashboardController {

    private final MentoringSessionRepository mentoringSessionRepository;
    private final LecturerService lecturerService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, HttpServletRequest request, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }

        LecturerDashboardDTO dto = lecturerService.getDashboard(loggedUser.getId());
        model.addAttribute("pendingCount", dto.getPendingCount());
        model.addAttribute("completedCount", dto.getCompletedCount());
        model.addAttribute("overdueCount", dto.getOverdueCount());
        model.addAttribute("totalCount", dto.getTotalCount());
        model.addAttribute("chartLabelsJson", toJsonStrings(dto.getLabels()));
        model.addAttribute("chartValuesJson", toJsonNumbers(dto.getValues()));

        return WebRequestSupport.view("lecturer/dashboard", WebRequestSupport.isAjaxRequest(request, null));
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