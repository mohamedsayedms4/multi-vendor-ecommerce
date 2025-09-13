package org.example.ecommerce.application.service.category.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommerce.application.service.category.CategoryService;
import org.example.ecommerce.domain.common.exception.ImageIsRequired;
import org.example.ecommerce.domain.model.category.Category;
import org.example.ecommerce.domain.model.category.exception.CategoryAlreadyExistsException;
import org.example.ecommerce.domain.model.category.exception.CategoryNotFoundException;
import org.example.ecommerce.domain.model.category.reppository.CategoryRepository;
import org.example.ecommerce.infrastructure.dto.category.CategoryAdminDto;
import org.example.ecommerce.infrastructure.dto.category.CategoryUserDtoEn;
import org.example.ecommerce.infrastructure.dto.category.CreateCategoryDto;
import org.example.ecommerce.infrastructure.dto.category.CreateCategoryWithoutIconDto;
import org.example.ecommerce.infrastructure.event.CategoryEvent;
import org.example.ecommerce.infrastructure.event.Type;
import org.example.ecommerce.infrastructure.mapper.CategoryMapper;
import org.example.ecommerce.infrastructure.utils.ImageUploadUtil;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ImageUploadUtil imageUploadUtil;
    private final ApplicationEventPublisher eventPublisher;




    @Override
    @Transactional
    public Optional<CreateCategoryDto> createCategory(CreateCategoryWithoutIconDto categoryDto, MultipartFile icon) {
        log.info("Starting category creation: name={}, categoryId={}, parentCategoryId={}, level={}",
                categoryDto.nameEn(),
                categoryDto.nameAr(),
                categoryDto.categoryId(),
                categoryDto.parentCategoryId(),
                categoryDto.level()
        );

        if (icon.isEmpty()) {
            throw new ImageIsRequired("logo is required");
        }
        String iconImageUrl = imageUploadUtil.saveImage(icon);

        // التحقق من وجود التصنيفات مسبقًا
        if(categoryRepository.findByNameEn(categoryDto.nameEn()).isPresent()) {
            throw new CategoryAlreadyExistsException("Category with name " + categoryDto.nameEn() + " already exists");
        }
        if(categoryRepository.findByNameAr(categoryDto.nameAr()).isPresent()) {
            throw new CategoryAlreadyExistsException("Category with name " + categoryDto.nameAr() + " already exists");
        }
        if(categoryRepository.findByCategoryId(categoryDto.categoryId()).isPresent()) {
            throw new CategoryAlreadyExistsException("Category with id " + categoryDto.categoryId() + " already exists");
        }

        // إنشاء كيان الـ Category
        Category category = new Category();
        category.setNameEn(categoryDto.nameEn());
        category.setNameAr(categoryDto.nameAr());
        category.setCategoryId(categoryDto.categoryId());
        category.setLevel(categoryDto.level());
        category.setImageUrl(iconImageUrl); // استخدم الـ URL الجديد

        if (categoryDto.parentCategoryId() != null) {
            log.debug("Fetching parent category with id={}", categoryDto.parentCategoryId());
            Category parent = categoryRepository.findById(categoryDto.parentCategoryId())
                    .orElseThrow(() -> {
                        log.error("Parent category not found for id={}", categoryDto.parentCategoryId());
                        return new CategoryNotFoundException("Parent category not found");
                    });
            category.setParentCategory(parent);
            log.debug("Parent category set successfully: {}", parent.getNameEn());
        }

        // حفظ التصنيف
        Category saved = categoryRepository.save(category);
        String username;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            username = userDetails.getUsername();
        } else {
            username = principal.toString();
        }

// نشر الحدث
        eventPublisher.publishEvent(new CategoryEvent(saved, Type.CREATED, username));        log.info("Category saved successfully with id={} and name={}", saved.getId(), saved.getNameEn());

        // تجهيز DTO للرد
        CreateCategoryDto responseDto = new CreateCategoryDto(
                saved.getNameEn(),
                saved.getNameAr(),
                saved.getCategoryId(),
                saved.getParentCategory() != null ? saved.getParentCategory().getId() : null,
                saved.getLevel(),
                saved.getImageUrl()
        );

        log.info("Returning response DTO for categoryId={}", responseDto.categoryId());
        return Optional.of(responseDto);
    }


    @Override
    @Transactional
    public Optional<CreateCategoryDto> updateCategory(Long id, CreateCategoryWithoutIconDto createCategoryDto, MultipartFile icon) {
        log.info("==== Start update category request ====");
        log.info("Received category ID: {}", id);

        log.info("Parsed category details: nameEn={}, nameAr={}, categoryId={}, parentCategoryId={}, level={}",
                createCategoryDto.nameEn(),
                createCategoryDto.nameAr(),
                createCategoryDto.categoryId(),
                createCategoryDto.parentCategoryId(),
                createCategoryDto.level()
        );

        // التحقق من الصورة
        if (icon.isEmpty()) {
            log.error("Icon file is missing for category update: categoryId={}", createCategoryDto.categoryId());
            throw new ImageIsRequired("Logo is required");
        }

        log.info("Icon file received: originalFilename={}, size={} bytes", icon.getOriginalFilename(), icon.getSize());
        String iconImageUrl = imageUploadUtil.saveImage(icon);
        log.info("Icon image saved successfully: {}", iconImageUrl);

        // التحقق من التكرارات
        if (createCategoryDto.nameEn() != null
                && categoryRepository.findByNameEn(createCategoryDto.nameEn())
                .filter(c -> !c.getId().equals(id))
                .isPresent()) {
            throw new CategoryAlreadyExistsException("Category with name " + createCategoryDto.nameEn() + " already exists");
        }

        if (createCategoryDto.nameAr() != null
                && categoryRepository.findByNameAr(createCategoryDto.nameAr())
                .filter(c -> !c.getId().equals(id))
                .isPresent()) {
            throw new CategoryAlreadyExistsException("Category with name " + createCategoryDto.nameAr() + " already exists");
        }

        if (createCategoryDto.categoryId() != null
                && categoryRepository.findByCategoryId(createCategoryDto.categoryId())
                .filter(c -> !c.getId().equals(id))
                .isPresent()) {
            throw new CategoryAlreadyExistsException("Category with id " + createCategoryDto.categoryId() + " already exists");
        }

        // جلب الكاتيجوري الحالي
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category with id " + id + " not found"));

        // تحديث البيانات
        if (createCategoryDto.nameEn() != null) category.setNameEn(createCategoryDto.nameEn());
        if (createCategoryDto.nameAr() != null) category.setNameAr(createCategoryDto.nameAr());
        if (createCategoryDto.categoryId() != null) category.setCategoryId(createCategoryDto.categoryId());
        if (createCategoryDto.level() != null) category.setLevel(createCategoryDto.level());
        category.setImageUrl(iconImageUrl); // استخدام الصورة الجديدة

        if (createCategoryDto.parentCategoryId() != null) {
            Category parent = categoryRepository.findById(createCategoryDto.parentCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException("Parent category with id " + createCategoryDto.parentCategoryId() + " not found"));
            category.setParentCategory(parent);
        }

        Category updatedCategory = categoryRepository.save(category);
        String username;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            username = userDetails.getUsername();
        } else {
            username = principal.toString();
        }

// نشر الحدث
        eventPublisher.publishEvent(new CategoryEvent(updatedCategory, Type.UPDATED, username));
        CreateCategoryDto dto = new CreateCategoryDto(
                updatedCategory.getNameEn(),
                updatedCategory.getNameAr(),
                updatedCategory.getCategoryId(),
                updatedCategory.getParentCategory() != null ? updatedCategory.getParentCategory().getId() : null,
                updatedCategory.getLevel(),
                updatedCategory.getImageUrl()
        );

        log.info("==== Category updated successfully: id={}, name={} ====", updatedCategory.getId(), updatedCategory.getNameEn());
        return Optional.of(dto);
    }


    @Override
    @Transactional
    public Boolean deleteCategory(Long id) {
        log.info("Attempting to delete category with id={}", id);

        Optional<Category> categoryOpt = categoryRepository.findById(id);
        if (categoryOpt.isEmpty()) {
            log.error("Category not found with id={}", id);
            throw new CategoryNotFoundException("Category with id " + id + " not found");
        }

        Category category = categoryOpt.get();

        List<Category> childCategories = categoryRepository.findByParentCategory(category);
        if (!childCategories.isEmpty()) {
            log.warn("Category id={} has {} child categories. Deleting them as well.", id, childCategories.size());
            categoryRepository.deleteAll(childCategories);
        }

        categoryRepository.delete(category);
        String username;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            username = userDetails.getUsername();
        } else {
            username = principal.toString();
        }

// نشر الحدث
        eventPublisher.publishEvent(new CategoryEvent(category, Type.DELETED, username));
        log.info("Category deleted successfully: id={}", id);
        return true;
    }
    @Override
    public Optional<Category> getCategory(Long id) {
        return categoryRepository.findById(id);
    }


    //    todo
    @Override
    public Optional<CategoryAdminDto> getCategoryByAdmin(Long id) {
        return Optional.empty();
    }
//  todo
    @Override
    public Optional<CategoryUserDtoEn> getCategoryByUser(Long id) {
        return Optional.empty();
    }
//  todo
    @Override
    public Page<CategoryAdminDto> getCategories(Pageable pageable) {
        return null;
    }
}
