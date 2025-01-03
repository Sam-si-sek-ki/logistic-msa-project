package com.sparta.logistics.delivery.application.service;

import com.sparta.logistics.delivery.application.dto.transferroute.CreateTransferRouteRequest;
import com.sparta.logistics.delivery.application.dto.transferroute.TransferRouteResponse;
import com.sparta.logistics.delivery.domain.model.Delivery;
import com.sparta.logistics.delivery.domain.model.Driver;
import com.sparta.logistics.delivery.domain.model.TransferRoute;
import com.sparta.logistics.delivery.domain.repository.delivery.DeliveryRepository;
import com.sparta.logistics.delivery.domain.repository.transferroute.TransferRouteRepository;
import com.sparta.logistics.delivery.infrastructure.client.hub.HubTransferClient;
import com.sparta.logistics.delivery.infrastructure.client.hub.HubTransferResponse;
import com.sparta.logistics.delivery.infrastructure.client.hub.HubTransferRouteRequest;
import com.sparta.logistics.delivery.infrastructure.client.notification.NotificationRequest;
import com.sparta.logistics.delivery.infrastructure.client.notification.NotificationServiceClient;
import com.sparta.logistics.delivery.libs.exception.ErrorCode;
import com.sparta.logistics.delivery.libs.exception.GlobalException;
import jakarta.transaction.Transactional;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferRouteService {

  private final TransferRouteRepository transferRouteRepository;
  private final DeliveryRepository deliveryRepository;
  private final DriverService driverService;
  private final HubTransferClient hubTransferClient;
  private final NotificationServiceClient notificationServiceClient;

  @Transactional
  public TransferRouteResponse createTransferRoute(CreateTransferRouteRequest request) {

    Delivery delivery = deliveryRepository.findByDeliveryId(request.getDeliveryId())
        .orElseThrow(() -> new GlobalException(ErrorCode.DELIVERY_NOT_FOUND));

    Driver driver = driverService.getNextSequenceDriver(delivery.getFromHubId());
    log.info(String.valueOf(driver));

    HubTransferRouteRequest hubRequest = HubTransferRouteRequest.builder()
        .fromHubId(delivery.getFromHubId())
        .toHubId(delivery.getToHubId())
        .build();

    ResponseEntity<HubTransferResponse> hubResponse =
        hubTransferClient.getHubTransfer(hubRequest);

    if (!hubResponse.getStatusCode().is2xxSuccessful()) {
      throw new GlobalException(ErrorCode.HUB_TRANSFER_NOT_FOUND);
    }

    TransferRoute transferRoute = request.toEntity(
        delivery,
        driver,
        Objects.requireNonNull(hubResponse.getBody())
    );

    sendTransferRouteNotification(transferRoute, delivery, driver);

    return TransferRouteResponse.fromEntity(transferRouteRepository.save(transferRoute));
  }

  private void sendTransferRouteNotification(TransferRoute transferRoute, Delivery delivery, Driver driver) {
    String message = String.format("""
            주문 번호 : %s
            배송 경로 순서 : %d
            출발 허브 : %s
            도착 허브 : %s
            예상 이동 거리 : %dkm
            예상 소요 시간 : %d분
            배송담당자 : %s
            """,
        delivery.getOrderId(),
        transferRoute.getSequence(),
        transferRoute.getFromHubId(),  // 필요시 Hub 서비스에서 이름 조회
        transferRoute.getToHubId(),    // 필요시 Hub 서비스에서 이름 조회
        transferRoute.getExpectedDistance(),
        transferRoute.getExpectedDuration(),
        driver.getUsername()
    );

    notificationServiceClient.sendNotification(
        new NotificationRequest(delivery.getSlackId(), message)
    );
  }
}
