package org.example.ecommerce.domain.model.admin;

import java.util.List;

public interface AdminRepository {
    List<Admin> findAll();
}
