package org.example.ecommerce.infrastructure.repository;

import org.example.ecommerce.domain.model.category.Category;
import org.example.ecommerce.domain.model.category.reppository.CategoryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface JpaCategoryRepository extends JpaRepository<Category,Long> , CategoryRepository {
    Optional<Category> findByCategoryId(String categoryId);

}
