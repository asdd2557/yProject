package com.example.gameproject.service;


import com.example.gameproject.dto.User_A;
import com.example.gameproject.entity.User_E;

import com.example.gameproject.repository.DynamoUserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserService {
  private final DynamoUserRepository dynamoUserRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

public String save(User_A user_a){
  user_a.setPassword(bCryptPasswordEncoder.encode(user_a.getPassword()));
  user_a.setId(UUID.randomUUID().toString());
  dynamoUserRepository.save(user_a);
  return user_a.getId();
  }

  public User_E findById(String userId){
  User_E user_e = dynamoUserRepository.findById(userId);
    if (user_e == null) {
      throw new IllegalArgumentException("User not found with email: " + userId);
    }
    return user_e;
  }

  public User_E findByEmail(String email) {
    User_E userE = dynamoUserRepository.findByEmail(email);
    if (userE == null) {
      throw new IllegalArgumentException("User not found with email: " + email);
    }
    return userE;
  }






}
