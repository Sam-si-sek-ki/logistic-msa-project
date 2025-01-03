package com.sparta.logistics.hub.application.dto;

public record HubResponseDto(
        String hubId,
        String name,
        String address,
        String addressDetail,
        double latitude,
        double longitude
) {
}
