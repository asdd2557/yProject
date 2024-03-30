package com.example.gameproject.entity;


import com.example.gameproject.dto.User_A;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

import java.util.Collection;
import java.util.List;
import java.util.UUID;


@Setter
@NoArgsConstructor
@DynamoDbBean
public class User_E implements UserDetails { //UserDetails를 상속받아 인증 객체로 사용

  private String id;

  private String email;

  private String nickname;

  private String password;

  @Builder
  public User_E(String email, String password, String nickname){
    this.email = email;
    this.password = password;
    this.nickname = nickname;
  }
  @DynamoDbPartitionKey
  @DynamoDbAttribute("id")
  public String getId(){
    return id;
  }

  @DynamoDbSecondaryPartitionKey(indexNames = {"email-index"})
  @DynamoDbAttribute("email")
  public String getEmail() {
    return email;
  }

  @DynamoDbAttribute("nickname")
  public String getNickname() {
    return nickname;
  }


  @DynamoDbAttribute("password")
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }


  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return  List.of(new SimpleGrantedAuthority("user"));
  }



  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }


  public static User_E getUser_E(User_A user_a){
    User_E userE = new User_E();
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    String encodedPassword = passwordEncoder.encode(user_a.getPassword());
    userE.setId(user_a.getId());
    userE.setNickname(user_a.getNickname());
    userE.setEmail(user_a.getEmail());
    userE.setPassword(encodedPassword);
    return userE;
  }

  public User_E update(String nickname){
    this.nickname = nickname;
    return this;
  }


}



