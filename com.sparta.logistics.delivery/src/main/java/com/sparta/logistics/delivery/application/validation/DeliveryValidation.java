package com.sparta.logistics.delivery.application.validation;

import com.sparta.logistics.delivery.application.dto.delivery.CreateDeliveryRequest;
import com.sparta.logistics.delivery.domain.repository.DeliveryRepository;
import com.sparta.logistics.delivery.libs.exception.ErrorCode;
import com.sparta.logistics.delivery.libs.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryValidation {

  private final DeliveryRepository deliveryRepository;

  public void createDeliveryValidation(CreateDeliveryRequest request){
    // TODO: 세부 권한 확인 필요

    if (request.getFromHubId() == null || request.getToHubId() == null) {
      throw new GlobalException(ErrorCode.INVALID_HUB_INFO);
    }

    if (request.getFromHubId().equals(request.getToHubId())) {
      throw new GlobalException(ErrorCode.INVALID_HUB_ROUTE);
    }
  }
}