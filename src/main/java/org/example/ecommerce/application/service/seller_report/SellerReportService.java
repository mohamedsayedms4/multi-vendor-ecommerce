package org.example.ecommerce.application.service.seller_report;

import org.example.ecommerce.domain.model.seller_report.SellerReport;

public interface SellerReportService {

    SellerReport getSellerReport(String sellerId) ;
    SellerReport updateSellerReport(SellerReport sellerReport) ;
}
