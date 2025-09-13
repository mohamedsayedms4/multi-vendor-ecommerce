package org.example.ecommerce.infrastructure.repository;

import org.example.ecommerce.domain.model.category.Category;
import org.example.ecommerce.domain.model.category.reppository.CategoryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA repository for the Category entity.
 * Extends Spring Data JPA's JpaRepository for standard CRUD operations
 * and the custom CategoryRepository for domain-specific queries.
 */
@Repository
public interface JpaCategoryRepository extends JpaRepository<Category, Long>, CategoryRepository {

    /**
     * Finds a category by its custom string identifier.
     *
     * @param categoryId the custom identifier of the category
     * @return an Optional containing the Category if found, or empty if not
     */
    Optional<Category> findByCategoryId(String categoryId);
}
