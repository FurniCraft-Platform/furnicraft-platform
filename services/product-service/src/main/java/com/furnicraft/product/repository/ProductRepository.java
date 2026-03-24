package com.furnicraft.product.repository;

import com.furnicraft.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    Page<Product> findAllByIsDeletedFalse(Pageable pageable);

    Optional<Product> findByIdAndIsDeletedFalse(UUID id);

    boolean existsByCodeAndIsDeletedFalse(String code);

    boolean existsByCodeAndIdNotAndIsDeletedFalse(String code, UUID id);
}
