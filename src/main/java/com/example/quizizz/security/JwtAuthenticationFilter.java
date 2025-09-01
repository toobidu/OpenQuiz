package com.example.quizizz.security;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.quizizz.enums.PermissionCode;
import com.example.quizizz.service.Interface.IRedisService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final IRedisService redisService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String path = request.getRequestURI();

        // Bỏ qua các endpoint public
        if (
                path.startsWith("/api/v1/auth") ||
                        path.startsWith("/swagger-ui") ||
                        path.startsWith("/v3/api-docs") ||
                        path.startsWith("/swagger-resources") ||
                        path.startsWith("/webjars") ||
                        path.equals("/swagger-ui.html") ||
                        path.equals("/error")
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = request.getHeader("Authorization");
        if (token == null) {
            token = request.getHeader("accessToken");
        }

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);

            try {
                if (!jwtUtil.validateToken(token)) {
                    log.warn("Invalid token for path: {}", path);
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token không hợp lệ");
                    return;
                }
                if (redisService.isTokenBlacklisted(token)) {
                    log.warn("Blacklisted token used for path: {}", path);
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token đã bị thu hồi");
                    return;
                }
                Long userId = jwtUtil.getUserIdFromToken(token);
                Collection<SimpleGrantedAuthority> authorities = getAuthoritiesFromRedis(userId);

                log.debug("User {} accessing {} with authorities: {}", userId, path, authorities);

                UserDetails userDetails = User.withUsername(userId.toString())
                        .password("")
                        .authorities(authorities)
                        .build();

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, authorities
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                log.error("JWT Authentication error: {}", e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token không hợp lệ");
                return;
            }
        } else {
            log.warn("Missing auth token for path: {}", path);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Thiếu token xác thực");
            return;
        }
        filterChain.doFilter(request, response);
    }

    private Collection<SimpleGrantedAuthority> getAuthoritiesFromRedis(Long userId) {
        Set<PermissionCode> permissions = redisService.getUserPermissions(userId);
        if (permissions == null || permissions.isEmpty()) {
            log.warn("No permissions found in Redis for user: {}", userId);
            return Collections.emptyList();
        }

        // Map PermissionCode.getCode() thay vì name() để match với @PreAuthorize
        Collection<SimpleGrantedAuthority> authorities = permissions.stream()
                .map(PermissionCode::getCode)  // "user:manage" thay vì "USER_MANAGE"
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        log.debug("Authorities for user {}: {}", userId, authorities);
        return authorities;
    }
}