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

  //사큐리티 session(내부 Authentication(내부UserDetails))
  //url/login으로 신호가 올때 자동 동작함
  //세션에 담음(권한 관리를 위해)
  public User_E loadUserByUsername(String email) {
    System.out.println("loaduserbyUsername");
    User_E userE = dynamoUserRepository.findByEmail(email);
    if (userE == null) {
      throw new IllegalArgumentException("User not found with email: " + email);
    }
    return userE;
  }

  public User_E loadUserBySubNickname(String subnickname) {
    User_E userE = dynamoUserRepository.findBySubNickname(subnickname);
    if (userE == null) {
      return null;
    }
    return userE;
  }

  public User_E updateBySubNickname(User_E userE, String subnickname) {
    System.out.println("subnickname!! :" +subnickname);
    userE.update(subnickname);
    System.out.println("updateBySubNickname:" +userE.getSubnickname());
    return dynamoUserRepository.update(userE);
  }

}
