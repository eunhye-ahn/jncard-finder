package com.esstudy.jncardsearch.service;

import com.esstudy.jncardsearch.domain.User;
import com.esstudy.jncardsearch.dto.SignupRequest;
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
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RedisService redisService;

    //회원가입
    public TokenResponse save(SignupRequest request) {
        //email 유효성 검사
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        User user = User.builder()
                        .name(request.getName())
                        .email(request.getEmail())
                        .password(passwordEncoder.encode(request.getPassword()))
                .build();
        userRepository.save(user);

        //토큰발급
        String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getRole());
        String refreshToken = jwtProvider.generateRefreshToken(user.getId());

        //rt 저장
        redisService.saveRefreshToken(user.getId(), refreshToken, jwtProvider.getRefreshExpiration());

        return new TokenResponse(accessToken);
    }
}
