package org.example.ecommerce.infrastructure.repository;

import org.example.ecommerce.domain.model.admin.Admin;
import org.example.ecommerce.domain.model.admin.AdminRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaAdminRepository extends JpaRepository<Admin,Long> , AdminRepository {
}
