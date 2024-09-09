package com.example.gameproject.service;

import com.example.gameproject.entity.Connect_E;
import com.example.gameproject.entity.Game_E;
import com.example.gameproject.handler.WebSocketHandler;
import com.example.gameproject.repository.DynamoConnectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Mono;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
@Service
@RequiredArgsConstructor
public class ConnectService {

 private final DynamoConnectRepository dynamoConnectRepository;

  @Autowired
  private ReactiveMongoTemplate reactiveMongoTemplate;
  public Connect_E loadByEmail(String email){
  return dynamoConnectRepository.findByEmail(email);
  }




  public List<Connect_E> loadByConnect(String connect){
    return dynamoConnectRepository.findByConnectAll(connect);
  }

  public Connect_E updateConnect(Connect_E connectE){
    return dynamoConnectRepository.update(connectE);
  }

 /* public Connect_E save(Principal principal){
    Connect_E connectE = new Connect_E();
    connectE.setEmail(principal.getName());
    connectE.setConnect("1");
    connectE.setPosition("0");
    connectE.setMatching(0);
    return dynamoConnectRepository.save(connectE);
  }*/

  public Connect_E save(Connect_E connectE){
    return dynamoConnectRepository.save(connectE);
  }


}
