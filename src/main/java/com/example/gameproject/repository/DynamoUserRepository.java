package com.example.gameproject.repository;


import com.example.gameproject.dto.User_A;
import com.example.gameproject.entity.User_E;
import org.apache.catalina.User;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.*;

import javax.persistence.Index;
import javax.persistence.Table;

@Repository
public class DynamoUserRepository {

  private final DynamoDbTable<User_E> userDynamoDbTable;


  public DynamoUserRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient) {

    this.userDynamoDbTable = dynamoDbEnhancedClient.table("Account", TableSchema.fromBean(User_E.class));
  }

  public User_E save(User_E userE) {
    userDynamoDbTable.putItem(userE);
    return userE;
  }



  public User_A save(User_A userA) {
    userDynamoDbTable.putItem(User_E.getUser_E(userA));
    return userA;
  }

  public User_E findBy(User_E userE) {
    return userDynamoDbTable.getItem(userE);
  }

  public User_E findByEmail(String email) {
    DynamoDbIndex<User_E> index = userDynamoDbTable.index("email-index");
    QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder().partitionValue(email).build());


    SdkIterable<Page<User_E>> queryResult = index.query(QueryEnhancedRequest.builder()
        .queryConditional(queryConditional)
        .limit(10)
        .build());

    for (Page<User_E> page : queryResult) {
      for (User_E user : page.items()) {
        return user; // 첫 번째 항목 반환
      }
    }

    return null; // 검색 결과가 없을 경우
  }

  public User_E findBySubNickname(String subnickname) {
    DynamoDbIndex<User_E> index = userDynamoDbTable.index("subnickname-index");
    QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder().partitionValue(subnickname).build());


    SdkIterable<Page<User_E>> queryResult = index.query(QueryEnhancedRequest.builder()
        .queryConditional(queryConditional)
        .limit(10)
        .build());

    for (Page<User_E> page : queryResult) {
      for (User_E user : page.items()) {
        return user; // 첫 번째 항목 반환
      }
    }

    return null; // 검색 결과가 없을 경우
  }

  public User_E findById(String userId) {
    QueryConditional conditional = QueryConditional.keyEqualTo(Key.builder().partitionValue(userId).build());

    QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
        .queryConditional(conditional)
        .limit(1)
        .build();
    return userDynamoDbTable.query(queryRequest).items().stream()
        .findAny()
        .orElseGet(() -> null);
  }

  public User_E update(User_E userE) {
    return userDynamoDbTable.updateItem(userE);
  }

  public void delete(User_E userE) {
    userDynamoDbTable.deleteItem(userE);
  }


}
