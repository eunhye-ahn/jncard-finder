package com.esstudy.jncardsearch.service;

import com.esstudy.jncardsearch.domain.User;
import com.esstudy.jncardsearch.dto.LoginRequest;
import com.esstudy.jncardsearch.dto.TokenResponse;
import com.esstudy.jncardsearch.exception.CustomException;
import com.esstudy.jncardsearch.exception.ErrorCode;
import com.esstudy.jncardsearch.jwt.JwtProvider;
import com.esstudy.jncardsearch.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RedisService redisService;

    //로그인
    public TokenResponse login(LoginRequest request) {
        //유저조회
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_PASSWORD));

        //비밀번호검증
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        //토큰발급
        String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getRole());
        String refreshToken = jwtProvider.generateRefreshToken(user.getId());

        //RT redis 저장
        redisService.saveRefreshToken(user.getId(),refreshToken, jwtProvider.getRefreshExpiration());

        return new TokenResponse(accessToken, refreshToken);
    }
}
