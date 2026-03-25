package com.furnicraft.order.repository;

import com.furnicraft.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    Page<Order> findAllByUserIdAndIsDeletedFalse(UUID userId, Pageable pageable);

    Optional<Order> findByIdAndIsDeletedFalse(UUID id);

    boolean existsByIdAndUserId(UUID id, UUID userId);
}