package com.restaurant.hnks24cntt1it210tranhuunhatproject.service.impl;

import com.restaurant.hnks24cntt1it210tranhuunhatproject.dto.EquipmentDTO;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.Equipment;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.LabType;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.repository.EquipmentRepository;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.repository.LabTypeRepository;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.service.EquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final LabTypeRepository labTypeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<EquipmentDTO> getAllEquipments() {
        return equipmentRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EquipmentDTO getEquipmentById(Integer id) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Thiết bị không tìm thấy"));
        return toDTO(equipment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LabType> getAllLabTypes() {
        return labTypeRepository.findAll();
    }

    @Override
    @Transactional
    public EquipmentDTO createEquipment(EquipmentDTO equipmentDTO) {
        LabType labType = null;
        if (equipmentDTO.getLabTypeId() != null) {
            labType = labTypeRepository.findById(equipmentDTO.getLabTypeId())
                    .orElseThrow(() -> new RuntimeException("Loại phòng Lab không tìm thấy"));
        }

        Equipment equipment = Equipment.builder()
                .equipmentName(equipmentDTO.getEquipmentName())
                .description(equipmentDTO.getDescription())
                .labType(labType)
                .totalQuantity(equipmentDTO.getTotalQuantity() != null ? equipmentDTO.getTotalQuantity() : 0)
                .availableQuantity(equipmentDTO.getTotalQuantity() != null ? equipmentDTO.getTotalQuantity() : 0)
                .build();

        Equipment saved = equipmentRepository.save(equipment);
        return toDTO(saved);
    }

    @Override
    @Transactional
    public EquipmentDTO updateEquipment(Integer id, EquipmentDTO equipmentDTO) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Thiết bị không tìm thấy"));

        equipment.setEquipmentName(equipmentDTO.getEquipmentName());
        equipment.setDescription(equipmentDTO.getDescription());
        equipment.setTotalQuantity(equipmentDTO.getTotalQuantity() != null ? equipmentDTO.getTotalQuantity() : 0);

        if (equipmentDTO.getLabTypeId() != null) {
            LabType labType = labTypeRepository.findById(equipmentDTO.getLabTypeId())
                    .orElseThrow(() -> new RuntimeException("Loại phòng Lab không tìm thấy"));
            equipment.setLabType(labType);
        }

        Equipment updated = equipmentRepository.save(equipment);
        return toDTO(updated);
    }

    @Override
    @Transactional
    public void deleteEquipment(Integer id) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Thiết bị không tìm thấy"));
        equipmentRepository.delete(equipment);
    }

    private EquipmentDTO toDTO(Equipment equipment) {
        return EquipmentDTO.builder()
                .id(equipment.getId())
                .equipmentName(equipment.getEquipmentName())
                .labTypeId(equipment.getLabType() != null ? equipment.getLabType().getId() : null)
                .labTypeName(equipment.getLabType() != null ? equipment.getLabType().getTypeName() : "Không xác định")
                .description(equipment.getDescription())
                .totalQuantity(equipment.getTotalQuantity())
                .availableQuantity(equipment.getAvailableQuantity())
                .build();
    }
}