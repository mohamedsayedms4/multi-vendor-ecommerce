package org.example.ecommerce.domain.model.category.reppository;

import org.example.ecommerce.domain.model.category.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {

    Category save(Category category);   // نفس توقيع JPA
    Optional<Category> findByNameEn(String name);

    Optional<Category> findByNameAr(String name);

    Optional<Category> findById(Long id);

    Optional<Category> findByCategoryId(String categoryId);

    List<Category> findByParentCategory(Category parent);

    void delete(Category entity);

    void deleteById(Long id);

    void deleteAll();

    void deleteAll(Iterable<? extends Category> entities);



}
