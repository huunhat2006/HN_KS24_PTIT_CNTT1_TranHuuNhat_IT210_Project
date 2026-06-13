package com.restaurant.hnks24cntt1it210tranhuunhatproject.controller;

import com.restaurant.hnks24cntt1it210tranhuunhatproject.config.WebRequestSupport;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.dto.EquipmentDTO;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.service.EquipmentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/equipment")
public class AdminEquipmentController {

    private final EquipmentService equipmentService;

    @GetMapping({"", "/"})
    public String listEquipments(
            @RequestParam(value = "query", required = false) String query,
            HttpServletRequest request,
            Model model
    ) {
        List<EquipmentDTO> equipments = equipmentService.getAllEquipments();
        if (query != null && !query.isBlank()) {
            String keyword = query.toLowerCase(Locale.ROOT);
            equipments = equipments.stream()
                    .filter(equipment -> equipment.getEquipmentName() != null && equipment.getEquipmentName().toLowerCase(Locale.ROOT).contains(keyword))
                    .collect(Collectors.toList());
        }
        model.addAttribute("equipments", equipments);
        model.addAttribute("searchQuery", query == null ? "" : query);
        model.addAttribute("pageTitle", "Quản lý Thiết bị");
        return WebRequestSupport.view("admin/equipment-list", WebRequestSupport.isAjaxRequest(request, null));
    }

    @GetMapping("/add")
    public String showAddForm(HttpServletRequest request, Model model) {
        model.addAttribute("equipmentDTO", new EquipmentDTO());
        model.addAttribute("labTypes", equipmentService.getAllLabTypes());
        model.addAttribute("pageTitle", "Thêm mới thiết bị");
        return WebRequestSupport.view("admin/equipment-form", WebRequestSupport.isAjaxRequest(request, null));
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, HttpServletRequest request, Model model) {
        EquipmentDTO equipmentDTO = equipmentService.getEquipmentById(id);
        model.addAttribute("equipmentDTO", equipmentDTO);
        model.addAttribute("labTypes", equipmentService.getAllLabTypes());
        model.addAttribute("pageTitle", "Cập nhật thiết bị");
        return WebRequestSupport.view("admin/equipment-form", WebRequestSupport.isAjaxRequest(request, null));
    }

    @GetMapping("/delete/{id}/confirm")
    public String showDeleteConfirm(@PathVariable Integer id, HttpServletRequest request, Model model) {
        EquipmentDTO equipmentDTO = equipmentService.getEquipmentById(id);
        model.addAttribute("equipmentDTO", equipmentDTO);
        model.addAttribute("pageTitle", "Xác nhận xóa thiết bị");
        return WebRequestSupport.view("admin/equipment-delete-confirm", WebRequestSupport.isAjaxRequest(request, null));
    }

    @PostMapping("/save")
    public String saveEquipment(
            @ModelAttribute("equipmentDTO") EquipmentDTO equipmentDTO,
            HttpServletRequest request,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            if (equipmentDTO.getId() != null) {
                equipmentService.updateEquipment(equipmentDTO.getId(), equipmentDTO);
            } else {
                equipmentService.createEquipment(equipmentDTO);
            }
            if (WebRequestSupport.isAjaxRequest(request, null)) {
                model.addAttribute("equipments", equipmentService.getAllEquipments());
                model.addAttribute("successMessage", equipmentDTO.getId() != null ? "Cập nhật thiết bị thành công." : "Thêm mới thiết bị thành công.");
                model.addAttribute("pageTitle", "Quản lý Thiết bị");
                return WebRequestSupport.view("admin/equipment-list", true);
            }
            redirectAttributes.addFlashAttribute("successMessage", equipmentDTO.getId() != null ? "Cập nhật thiết bị thành công." : "Thêm mới thiết bị thành công.");
        } catch (RuntimeException ex) {
            if (WebRequestSupport.isAjaxRequest(request, null)) {
                model.addAttribute("equipmentDTO", equipmentDTO);
                model.addAttribute("errorMessage", ex.getMessage());
                model.addAttribute("pageTitle", equipmentDTO.getId() != null ? "Cập nhật thiết bị" : "Thêm mới thiết bị");
                model.addAttribute("labTypes", equipmentService.getAllLabTypes());
                return WebRequestSupport.view("admin/equipment-form", true);
            }
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/admin/equipment";
    }

    @PostMapping("/delete/{id}")
    public String deleteEquipment(@PathVariable Integer id, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        try {
            equipmentService.deleteEquipment(id);
            if (WebRequestSupport.isAjaxRequest(request, null)) {
                model.addAttribute("equipments", equipmentService.getAllEquipments());
                model.addAttribute("successMessage", "Xóa thiết bị thành công.");
                model.addAttribute("pageTitle", "Quản lý Thiết bị");
                return WebRequestSupport.view("admin/equipment-list", true);
            }
            redirectAttributes.addFlashAttribute("successMessage", "Xóa thiết bị thành công.");
        } catch (RuntimeException ex) {
            if (WebRequestSupport.isAjaxRequest(request, null)) {
                model.addAttribute("equipments", equipmentService.getAllEquipments());
                model.addAttribute("errorMessage", ex.getMessage());
                model.addAttribute("pageTitle", "Quản lý Thiết bị");
                return WebRequestSupport.view("admin/equipment-list", true);
            }
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/admin/equipment";
    }
}