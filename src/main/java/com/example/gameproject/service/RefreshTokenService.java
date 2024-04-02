package com.example.gameproject.service;


import com.example.gameproject.config.jwt.RefreshToken;
import com.example.gameproject.repository.DynamoRefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {
  private final DynamoRefreshTokenRepository dynamoRefreshTokenRepository;

  public RefreshToken findByRefreshToken(String refreshToken) {

    RefreshToken reFreshToken = dynamoRefreshTokenRepository.findByrefreshToken(refreshToken);

    if (reFreshToken == null) {
      throw new IllegalArgumentException("User not found with refreshToken: " + refreshToken);
    }
    return reFreshToken;
  }
}
