package com.restaurant.hnks24cntt1it210tranhuunhatproject.repository;

import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.BorrowingDetail;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.BorrowingDetailId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BorrowingDetailRepository extends JpaRepository<BorrowingDetail, BorrowingDetailId> {
}