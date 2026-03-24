package com.furnicraft.product.repository;

import com.furnicraft.product.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, UUID id);

    Optional<Category> findByIdAndIsDeletedFalse(UUID id);

    List<Category> findAllByIsDeletedFalse();
}
