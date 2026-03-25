package com.furnicraft.cart.repository;

import com.furnicraft.cart.entity.Cart;
import com.furnicraft.cart.enums.CartStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {

    Optional<Cart> findByUserId(UUID userId);

    Optional<Cart> findByUserIdAndStatus(UUID userId, CartStatus status);

    boolean existsByUserId(UUID userId);
}
