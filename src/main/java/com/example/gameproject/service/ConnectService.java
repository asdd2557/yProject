package com.example.gameproject.service;

import com.example.gameproject.entity.Connect_E;
import com.example.gameproject.repository.DynamoConnectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConnectService {
 private final DynamoConnectRepository dynamoConnectRepository;


  public Connect_E loadByEmail(String email){
  return dynamoConnectRepository.findByEmail(email);
  }




  public List<Connect_E> loadByConnect(String connect){
    return dynamoConnectRepository.findByConnectAll(connect);
  }

  public Connect_E updateConnect(Connect_E connectE){
    return dynamoConnectRepository.update(connectE);
  }

  public Connect_E save(Principal principal){
    Connect_E connectE = new Connect_E();
    connectE.setEmail(principal.getName());
    connectE.setConnect("1");
    connectE.setPosition("0");
    connectE.setMatching(0);
    return dynamoConnectRepository.save(connectE);
  }








}
