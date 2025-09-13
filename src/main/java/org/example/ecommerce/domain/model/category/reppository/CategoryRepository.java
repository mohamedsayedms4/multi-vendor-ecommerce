package org.example.ecommerce.domain.model.category.reppository;

import org.example.ecommerce.domain.model.category.Category;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Category entities.
 * Provides basic CRUD operations and custom queries for categories.
 */
public interface CategoryRepository {

    /**
     * Saves a category entity to the repository.
     * If the category already exists, it will be updated.
     *
     * @param category the category entity to save
     * @return the saved category entity
     */
    Category save(Category category);

    /**
     * Finds a category by its English name.
     *
     * @param name the English name of the category
     * @return Optional containing the category if found
     */
    Optional<Category> findByNameEn(String name);

    /**
     * Finds a category by its Arabic name.
     *
     * @param name the Arabic name of the category
     * @return Optional containing the category if found
     */
    Optional<Category> findByNameAr(String name);

    /**
     * Finds a category by its ID.
     *
     * @param id the ID of the category
     * @return Optional containing the category if found
     */
    Optional<Category> findById(Long id);

    /**
     * Finds a category by its custom categoryId (string identifier).
     *
     * @param categoryId the custom ID of the category
     * @return Optional containing the category if found
     */
    Optional<Category> findByCategoryId(String categoryId);

    /**
     * Finds all child categories of a given parent category.
     *
     * @param parent the parent category
     * @return list of child categories
     */
    List<Category> findByParentCategory(Category parent);

    /**
     * Retrieves all categories in the repository.
     *
     * @return list of all categories
     */
    List<Category> findAll();

    /**
     * Deletes a single category entity.
     *
     * @param entity the category entity to delete
     */
    void delete(Category entity);

    /**
     * Deletes multiple category entities at once.
     *
     * @param entities the iterable collection of categories to delete
     */
    void deleteAll(Iterable<? extends Category> entities);
}
