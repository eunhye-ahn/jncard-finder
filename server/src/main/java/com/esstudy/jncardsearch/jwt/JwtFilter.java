package com.esstudy.jncardsearch.jwt;

import com.esstudy.jncardsearch.exception.CustomException;
import com.esstudy.jncardsearch.exception.ErrorCode;
import com.esstudy.jncardsearch.exception.ErrorResponse;
import com.esstudy.jncardsearch.service.RedisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * [WHAT] 컨트롤러 진입 필터 로직
 * [흐름]
 *      *  1. 헤더에서 토큰 추출
 *      *  2. /auth/reissue 요청이면 검증 없이 통과 (RT 재발급 엔드포인트)
 *      *  3. 토큰이 있으면 -> 검증 -> securitycontext 인증 정보 저장
 *      *  4. 토큰 없으면 -> 저장 안함 -> security가 비인증 요청으로 처리
 *      *  5. 다음 필터로 넘기거나 컨트롤러 진입
 */
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;

    public String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            //헤더에서 토큰 정보 추출
            String token = resolveToken(request);
            System.out.println(token);

            if (request.getRequestURI().equals("/api/auth/reissue")) {
                filterChain.doFilter(request, response);
                return;
            }
            if(token != null) {
                if(redisService.isBlackList(token)){
                    throw new CustomException(ErrorCode.EXPIRED_TOKEN);
                }
                jwtProvider.validateToken(token);
                Long userId = jwtProvider.getUserIdFromToken(token);
                String role = jwtProvider.getRoleFromToken(token);

                //시큐리티 인증객체로 저장
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userId,
                                null,
                                List.of(new SimpleGrantedAuthority(role))
                        );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        catch (Exception e) {
            //mvc밖에서 예외처리 형식 통일화 보충***
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().println(
                    objectMapper.writeValueAsString(
                            new ErrorResponse(500,
                                    ErrorCode.INTERNAL_SERVER_ERROR.name(),
                                    e.getMessage())
                    )
            );
            return;
        }
        filterChain.doFilter(request, response);
    }
}
