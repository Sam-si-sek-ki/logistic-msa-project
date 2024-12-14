package com.sparta.logistics.delivery.application.service;

import com.sparta.logistics.delivery.application.dto.delivery.CreateDeliveryRequest;
import com.sparta.logistics.delivery.application.dto.delivery.CreateDeliveryResponse;
import com.sparta.logistics.delivery.domain.model.Delivery;
import com.sparta.logistics.delivery.domain.repository.DeliveryRepository;
import com.sparta.logistics.delivery.infrastructure.client.CompanyServiceClient;
import com.sparta.logistics.delivery.infrastructure.client.HubTransferServiceClient;
import com.sparta.logistics.delivery.infrastructure.client.OrderServiceClient;
import com.sparta.logistics.delivery.infrastructure.client.dto.CompanyClientResponse;
import com.sparta.logistics.delivery.infrastructure.client.dto.HubTransferClientResponse;
import com.sparta.logistics.delivery.infrastructure.client.dto.OrderResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class DeliveryService {

  private final DeliveryRepository deliveryRepository;
  private final OrderServiceClient orderServiceClient;
  private final CompanyServiceClient companyServiceClient;

  @Transactional
  public CreateDeliveryResponse createDelivery(OrderResponseDto orderResponseDto) {

    // 1. 수령 업체 정보 조회
    CompanyClientResponse receiverCompany = companyServiceClient.getCompany(
        orderResponseDto.getReceiverCompanyId()
    );

    // 2. 공급 업체 정보 조회
    CompanyClientResponse supplierCompany = companyServiceClient.getCompany(
        orderResponseDto.getSupplierCompanyId()
    );

    CreateDeliveryRequest request = CreateDeliveryRequest.of(
        orderResponseDto,
        receiverCompany,
        supplierCompany
    );

    Delivery delivery = request.toEntity();
    delivery = deliveryRepository.save(delivery);

    return CreateDeliveryResponse.fromEntity(delivery);
  }

}
