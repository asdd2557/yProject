package com.example.gameproject;

import com.example.gameproject.service.DynamoStreamService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.services.dynamodb.streams.DynamoDbStreamsClient;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableMongoRepositories
@EnableJpaAuditing // created_at, update_at 자동 없데이트
@SpringBootApplication
public class GameProjectApplication {





  @Value("${AWS_ACCESS_KEY_ID}")
  private String awsAccessKeyId;

  @Value("${AWS_SECRET_ACCESS_KEY}")
  private String awsSecretAccessKey;



  public static void main(String[] args) {

    SpringApplication.run(GameProjectApplication.class, args);

  }


  // 다른 클래스나 메서드에서 사용할 수 있는 getter 메서드
  public String getAwsAccessKeyId() {
    return awsAccessKeyId;
  }

  public String getAwsSecretAccessKey() {
    return awsSecretAccessKey;
  }


}
