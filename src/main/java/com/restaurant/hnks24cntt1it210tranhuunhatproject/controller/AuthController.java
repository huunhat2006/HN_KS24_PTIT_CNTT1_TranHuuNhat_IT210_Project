package com.restaurant.hnks24cntt1it210tranhuunhatproject.controller;

import com.restaurant.hnks24cntt1it210tranhuunhatproject.dto.UserLoginDTO;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.dto.UserRegisterDTO;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.User;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.enums.UserRole;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        if (!model.containsAttribute("userLoginDTO")) {
            model.addAttribute("userLoginDTO", new UserLoginDTO());
        }
        return "auth/login";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }


    @PostMapping("/login")
    public String login(
            @Valid @ModelAttribute("userLoginDTO") UserLoginDTO userLoginDTO,
            BindingResult bindingResult,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "auth/login";
        }

        try {
            User user = authService.login(userLoginDTO);
            session.setAttribute("loggedUser", user);
            return "redirect:" + resolveRedirectUrl(user);
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Sai tài khoản hoặc mật khẩu");
            redirectAttributes.addFlashAttribute("userLoginDTO", UserLoginDTO.builder()
                    .username(userLoginDTO.getUsername())
                    .build());
            return "redirect:/login";
        }
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        if (!model.containsAttribute("userRegisterDTO")) {
            model.addAttribute("userRegisterDTO", new UserRegisterDTO());
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("userRegisterDTO") UserRegisterDTO userRegisterDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        try {
            authService.register(userRegisterDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Đăng ký thành công. Vui lòng đăng nhập.");
            return "redirect:/login";
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "auth/register";
        }
    }

    private String resolveRedirectUrl(User user) {
        if (user.getRole() == UserRole.STUDENT) {
            return "/student";
        }
        if (user.getRole() == UserRole.ADMIN) {
            return "/admin/dashboard";
        }
        if (user.getRole() == UserRole.LECTURER) {
            return "/lecturer/dashboard";
        }
        return "/home";
    }

}