package com.bookstore.Repository;

import com.bookstore.Entity.PurchaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseRepository extends JpaRepository<PurchaseEntity, Integer> {
    List<PurchaseEntity> findByUserId(Integer userId);
}