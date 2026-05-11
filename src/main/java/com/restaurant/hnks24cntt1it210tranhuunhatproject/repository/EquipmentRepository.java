package com.restaurant.hnks24cntt1it210tranhuunhatproject.repository;

import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface EquipmentRepository extends JpaRepository<Equipment, Integer> {

	@Query("SELECT e FROM Equipment e LEFT JOIN FETCH e.labType")
	List<Equipment> findAllWithLabType();

}