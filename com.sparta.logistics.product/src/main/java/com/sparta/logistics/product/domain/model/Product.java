package com.sparta.logistics.product.domain.model;

import com.sparta.logistics.product.libs.model.BaseEntity;
import com.sparta.logistics.product.presentation.dto.ProductRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "p_product")
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID productId;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private int stockQuantity;

    @Column(nullable = false)
    private String description;

//    @Column(nullable = false)
//    private UUID hubId;
//
//    @Column(nullable = false)
//    private UUID companyId;

    public static Product create(ProductRequestDto request) {
        return Product.builder()
            .productName(request.getProductName())
            .stockQuantity(request.getStockQuantity())
            .description(request.getDescription())
//            .hubId(request.getHubId())
//            .companyId(request.getCompanyId())
            .build();
    }

    public void update(ProductRequestDto request) {
        this.productName = request.getProductName();
        this.stockQuantity = request.getStockQuantity();
        this.description = request.getDescription();
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}