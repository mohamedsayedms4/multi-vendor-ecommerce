package org.example.ecommerce.application.service.seller.impl.handeler;

import org.example.ecommerce.infrastructure.dto.seller.UpdateSellerDto;

/**
 * Handler interface for updating seller profile information.
 */
public interface UpdateSellerProfile {

    /**
     * Updates a seller profile based on the given DTO.
     *
     * @param id seller ID
     * @param updateSellerDto DTO containing updated seller information
     * @return true if update succeeded
     */
    Boolean updateSellerProfile(Long id, UpdateSellerDto updateSellerDto);
}
