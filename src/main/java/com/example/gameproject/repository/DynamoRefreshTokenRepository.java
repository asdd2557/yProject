package com.example.gameproject.repository;

import com.example.gameproject.config.jwt.RefreshToken;
import com.example.gameproject.dto.User_A;
import com.example.gameproject.entity.User_E;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

@Repository
public class DynamoRefreshTokenRepository {

  private final DynamoDbTable<RefreshToken> refreshTokenDynamoDbTable;
  public DynamoRefreshTokenRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
    this.refreshTokenDynamoDbTable = dynamoDbEnhancedClient.table("RefreshToken", TableSchema.fromBean(RefreshToken.class));
  }

  public void save(RefreshToken userE){
    refreshTokenDynamoDbTable.putItem(userE);
  }

  public RefreshToken findBy(RefreshToken refreshToken){
    return refreshTokenDynamoDbTable.getItem(refreshToken);
  }

  public RefreshToken findByrefreshToken(String refreshToken){
    DynamoDbIndex<RefreshToken> index = refreshTokenDynamoDbTable.index("refresh_token-index");
    QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder().partitionValue(refreshToken).build());


    SdkIterable<Page<RefreshToken>> queryResult = index.query(QueryEnhancedRequest.builder()
        .queryConditional(queryConditional)
        .limit(10)
        .build());

    for (Page<RefreshToken> page : queryResult) {
      for (RefreshToken refreshToken1 : page.items()) {
        return refreshToken1; // 첫 번째 항목 반환
      }
    }

    return null; // 검색 결과가 없을 경우
  }

  public RefreshToken findByUserId(String userId){
    DynamoDbIndex<RefreshToken> index = refreshTokenDynamoDbTable.index("user_id-index");
    QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder().partitionValue(userId).build());


    SdkIterable<Page<RefreshToken>> queryResult = index.query(QueryEnhancedRequest.builder()
        .queryConditional(queryConditional)
        .limit(10)
        .build());

    for (Page<RefreshToken> page : queryResult) {
      for (RefreshToken refreshToken : page.items()) {
        return refreshToken; // 첫 번째 항목 반환
      }
    }

    return null; // 검색 결과가 없을 경우
  }

  public RefreshToken update(RefreshToken refreshToken){
    return refreshTokenDynamoDbTable.updateItem(refreshToken);
  }
  public void delete(RefreshToken refreshToken){
    refreshTokenDynamoDbTable.deleteItem(refreshToken);
  }
}
