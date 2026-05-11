package com.restaurant.hnks24cntt1it210tranhuunhatproject.service;

import com.restaurant.hnks24cntt1it210tranhuunhatproject.dto.EquipmentDTO;
import java.util.List;

public interface EquipmentService {
    List<EquipmentDTO> getAllEquipments();
    EquipmentDTO getEquipmentById(Integer id);
    EquipmentDTO createEquipment(EquipmentDTO equipmentDTO);
    EquipmentDTO updateEquipment(Integer id, EquipmentDTO equipmentDTO);
    void deleteEquipment(Integer id);
}