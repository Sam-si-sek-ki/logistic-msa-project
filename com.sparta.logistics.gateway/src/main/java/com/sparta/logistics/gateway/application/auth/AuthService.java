package com.sparta.logistics.gateway.application.auth;

// 응용 계층 DIP 적용을 위한 인증 서비스 인터페이스
public interface AuthService {
    Boolean verifyUser(String username);
}
