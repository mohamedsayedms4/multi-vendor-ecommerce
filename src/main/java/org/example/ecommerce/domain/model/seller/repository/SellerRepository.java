package org.example.ecommerce.domain.model.seller.repository;

import org.example.ecommerce.domain.model.seller.Seller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface SellerRepository {
    Seller save(Seller seller);
    Optional<Seller> findById(Long id);
    Optional<Seller> findByUserId(Long id);
    Optional<Seller> findByBusinessDetails_BusinessName(String businessName);
    Optional<Seller> findByBusinessDetails_BusinessEmail(String businessEmail);
    Optional<Seller> findByBusinessDetails_BusinessMobile(String businessMobile);

    int deleteSellerById(Long id);   // كويس تخليها int أو void، بس ما تعملهاش ترجع كائن

    Boolean existsById(long id);

    Page<Seller> findAll(Pageable pageable);
}
