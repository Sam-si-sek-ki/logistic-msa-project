package com.sparta.logistics.order.infrastructure.client;

import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "company-service")
public interface CompanyServiceClient {

    @GetMapping("/companies/{supplierCompanyId}")
    ResponseEntity<Void> supplierCompanyExist(
        @PathVariable UUID supplierCompanyId
    );

    @GetMapping("/companies/{receiverCompanyId}")
    ResponseEntity<Void> receiverCompanyExist(
        @PathVariable UUID receiverCompanyId
    );
}