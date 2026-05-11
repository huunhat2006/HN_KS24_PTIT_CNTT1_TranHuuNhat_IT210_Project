package com.restaurant.hnks24cntt1it210tranhuunhatproject.controller;

import com.restaurant.hnks24cntt1it210tranhuunhatproject.config.WebRequestSupport;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.dto.UserProfileDTO;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.User;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.service.UserProfileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final UserProfileService userProfileService;

    @GetMapping
    public String showProfile(HttpSession session, HttpServletRequest request, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }

        UserProfileDTO profileDTO = userProfileService.getProfileDTOByUserId(loggedUser.getId());
        model.addAttribute("userProfileDTO", profileDTO);
        model.addAttribute("pageTitle", "Hồ sơ cá nhân");
        return WebRequestSupport.view("profile", WebRequestSupport.isAjaxRequest(request, null));
    }

    @PostMapping
    public String updateProfile(
            @ModelAttribute("userProfileDTO") UserProfileDTO profileDTO,
            HttpSession session,
            HttpServletRequest request,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }

        try {
            userProfileService.updateProfile(loggedUser.getId(), profileDTO);
            if (WebRequestSupport.isAjaxRequest(request, null)) {
                model.addAttribute("userProfileDTO", userProfileService.getProfileDTOByUserId(loggedUser.getId()));
                model.addAttribute("successMessage", "Cập nhật hồ sơ thành công.");
                model.addAttribute("pageTitle", "Hồ sơ cá nhân");
                return "profile :: content";
            }
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật hồ sơ thành công.");
        } catch (RuntimeException ex) {
            if (WebRequestSupport.isAjaxRequest(request, null)) {
                model.addAttribute("userProfileDTO", userProfileService.getProfileDTOByUserId(loggedUser.getId()));
                model.addAttribute("errorMessage", ex.getMessage());
                model.addAttribute("pageTitle", "Hồ sơ cá nhân");
                return "profile :: content";
            }
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/profile";
    }
}