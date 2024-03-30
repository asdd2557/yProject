package com.example.gameproject.config.jwt;


import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

import javax.persistence.*;
import java.util.UUID;


@Setter
@Getter
@DynamoDbBean
public class RefreshToken {

  private String id;

  private String userId;

  private String refreshToken;
  @DynamoDbPartitionKey
  public String getId(){
    return id;
  }

  @DynamoDbSecondaryPartitionKey(indexNames = {"refresh_token-index"})
  @DynamoDbAttribute("refresh_token")
  public String getRefreshToken() {
    return refreshToken;
  }

  @DynamoDbSecondaryPartitionKey(indexNames = {"user_id-index"})
  @DynamoDbAttribute("user_id")
  public String getUserId(){
    return userId;
  }

  public RefreshToken() {
    // 기본 생성자 추가
  }

  public RefreshToken(String userId, String refreshToken){
    this.id =  UUID.randomUUID().toString();
    this.userId = userId;
    this.refreshToken = refreshToken;
  }


  public RefreshToken update(String newRefreshToken){
    this.refreshToken = newRefreshToken;
    return this;
  }

}