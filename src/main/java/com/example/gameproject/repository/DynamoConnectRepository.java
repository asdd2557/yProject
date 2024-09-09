package com.example.gameproject.repository;

import com.example.gameproject.dto.Connect_A;
import com.example.gameproject.entity.Connect_E;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;
import software.amazon.awssdk.services.dynamodb.streams.DynamoDbStreamsClient;

import javax.persistence.Index;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DynamoConnectRepository {

  private final DynamoDbTable<Connect_E> connectDynamoDbTable;

  public DynamoConnectRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
    this.connectDynamoDbTable = dynamoDbEnhancedClient.table("Connect", TableSchema.fromBean(Connect_E.class));
  }

  public void dynamoDbStream() {
    DynamoDbStreamsClient dynamoDbEnhancedClient = DynamoDbStreamsClient.builder()
        .region(Region.AP_SOUTHEAST_2)
        .build();
  }

  public Connect_E save(Connect_E connectE) {
    connectDynamoDbTable.putItem(connectE);
    return connectE;
  }

  public Connect_A save(Connect_A connectA) {
    connectDynamoDbTable.putItem(connectA.toEntity(connectA));
    return connectA;
  }

  public Connect_E findBy(Connect_E connectE) {
    return connectDynamoDbTable.getItem(connectE);
  }

  public List<Connect_E> findByConnectAll(String connect) {
    DynamoDbIndex<Connect_E> index = connectDynamoDbTable.index("connect-index");
    QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder().partitionValue(connect).build());

    List<Connect_E> resultList = new ArrayList<>();

    SdkIterable<Page<Connect_E>> queryResult = index.query(QueryEnhancedRequest.builder()
        .queryConditional(queryConditional)
        .limit(10)
        .build());

    for (Page<Connect_E> page : queryResult) {
      for (Connect_E connectE : page.items()) {
        resultList.add(connectE); // 결과를 리스트에 추가
      }
    }
    return resultList; // 전체 결과 리스트 반환
  }

  public Connect_E findByEmail(String email) {
    QueryConditional conditional = QueryConditional.keyEqualTo(Key.builder().partitionValue(email).build());

    QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
        .queryConditional(conditional)
        .limit(1)
        .build();
    return connectDynamoDbTable.query(queryRequest).items().stream()
        .findAny()
        .orElseGet(() -> null);
  }

  public Connect_E update(Connect_E connectE) {
    return connectDynamoDbTable.updateItem(connectE);
  }






}
