package com.example.gameproject.service;


import com.example.gameproject.entity.User_E;

import com.example.gameproject.repository.DynamoUserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailService implements UserDetailsService {

  private final DynamoUserRepository dynamoUserRepository;
  // 사용자 이름(email)으로 사용자의 정보를 가져오는 메서드

  public User_E loadUserByUsername(String email) {
    User_E userE = dynamoUserRepository.findByEmail(email);
    if (userE == null) {
      throw new IllegalArgumentException("User not found with email: " + email);
    }
    return userE;
  }



}
