package com.example.gameproject.entity;

import com.example.gameproject.dto.Connect_A;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@DynamoDbBean
public class Connect_E {
    private String connect;
    private String email;
    private String position;
    private int matching;
  @DynamoDbPartitionKey
  @DynamoDbAttribute("email")
  public String getEmail(){
    return email;
  }


  @DynamoDbSecondaryPartitionKey(indexNames = {"connect-index"})
  @DynamoDbAttribute("connect")
  public String getConnect() {
    return connect;
  }

  @DynamoDbAttribute("position")
  public String getPosition() {
    return position;
  }

  @DynamoDbAttribute("matching")
  public int getMatching() {
    return matching;
  }

  public Connect_A toArticle(Connect_E connectE){
    Connect_A connectA = new Connect_A();
    connectA.setConnect(connectE.getConnect());
    connectA.setEmail(connectE.getEmail());
    connectA.setPosition(connectE.getPosition());
    connectA.setMatching(connectE.getMatching());
    return connectA;
  }




}
