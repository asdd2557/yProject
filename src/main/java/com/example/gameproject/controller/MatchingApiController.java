package com.example.gameproject.controller;

import com.example.gameproject.entity.Connect_E;
import com.example.gameproject.repository.DynamoConnectRepository;
import com.example.gameproject.repository.MongDBChetRepository;
import com.example.gameproject.service.ConnectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MatchingApiController {
  private final ConnectService connectService;


  @PutMapping("/api/matching")
  public void matchingStart(@RequestBody Map<String, String> requestBody,Principal principal) {
    String connect = requestBody.get("connect");
    Connect_E connectE =  connectService.loadByEmail(principal.getName());
    connectE.setConnect("2");
    connectService.updateConnect(connectE);
  }
}
