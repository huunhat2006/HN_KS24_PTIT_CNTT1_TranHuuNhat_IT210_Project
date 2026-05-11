package com.restaurant.hnks24cntt1it210tranhuunhatproject.controller;

import com.restaurant.hnks24cntt1it210tranhuunhatproject.config.WebRequestSupport;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.User;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.repository.UserRepository;
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
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserRepository userRepository;

    @GetMapping
    public String userList(
            @RequestParam(value = "sort", defaultValue = "name_asc") String sort,
            HttpSession session,
            HttpServletRequest request,
            Model model
    ) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }

        var users = userRepository.findAll();

        if ("name_asc".equals(sort)) {
            users.sort((a, b) -> a.getUsername().compareToIgnoreCase(b.getUsername()));
        } else if ("name_desc".equals(sort)) {
            users.sort((a, b) -> b.getUsername().compareToIgnoreCase(a.getUsername()));
        }

        model.addAttribute("users", users);
        model.addAttribute("currentSort", sort);
        model.addAttribute("pageTitle", "Quản lý Người dùng");

        return WebRequestSupport.view("admin/user-list", WebRequestSupport.isAjaxRequest(request, null));
    }
}