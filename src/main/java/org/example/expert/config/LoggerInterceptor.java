package org.example.expert.config;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.auth.exception.AuthException;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.lang.NonNullApi;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@RequiredArgsConstructor
public class LoggerInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) throws Exception {

        String authorizationHeader = request.getHeader("Authorization");
        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new AuthException("인증이 필요합니다.");
        }

        String token = jwtUtil.substringToken(authorizationHeader);
        Claims claims = jwtUtil.extractClaims(token);
        UserRole userRole = UserRole.valueOf(claims.get("userRole", String.class));

        if(!UserRole.ADMIN.equals(userRole)) {
            throw new AuthException("관리자만 사용가능합니다");
        }

        log.info("=========================[ADMIN REQUEST]===========================");
        log.info("요청 URI : {}", request.getRequestURI());
        log.info("요청 시간 : {}", LocalDateTime.now());
        log.info("===================================================================");

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
