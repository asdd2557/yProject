package com.example.gameproject.controller;
import com.example.gameproject.entity.Connect_E;
import com.example.gameproject.entity.User_E;
import com.example.gameproject.handler.WebSocketHandler;
import com.example.gameproject.service.ConnectService;
import com.example.gameproject.service.UserDetailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@Slf4j
public class DynamoDBStreamController {
    @Autowired
    private WebSocketHandler webSocketHandler;
  private final ConnectService connectService;
  private final UserDetailService userDetailService;
  @PostMapping("/data")
  public String receiveData(@RequestBody String jsonData) {
    System.out.println("Received data: " + jsonData);
    // 받은 데이터에 대한 처리 로직을 작성
    // 예시: 받은 JSON 데이터를 파싱하여 원하는 작업을 수행
    try{
      ObjectMapper objectMapper = new ObjectMapper();
      JsonNode rootNode = objectMapper.readTree(jsonData);

      JsonNode recordsNode  = rootNode.path("records");
      for(JsonNode recordNode : recordsNode){
        String eventName = recordNode.path("eventName").asText();
        if("MODIFY".equals(eventName)){

          List<Map<String, String>> userList = new ArrayList<>();
          List<Connect_E> connectEs = new ArrayList<>();
          connectEs.addAll(connectService.loadByConnect("1"));
          connectEs.addAll(connectService.loadByConnect("2"));
          connectEs.addAll(connectService.loadByConnect("3"));

          for (Connect_E connectE : connectEs) {
            User_E user_e = userDetailService.loadUserByUsername(connectE.getEmail());
            String nickname = (user_e.getSubnickname() != null && !user_e.getSubnickname().isEmpty()) ?
                user_e.getSubnickname() : user_e.getNickname();

            Map<String, String> userMap = new HashMap<>();
            userMap.put("nickname", nickname);
            userMap.put("email", connectE.getEmail());
            userMap.put("position", connectE.getPosition());
            userMap.put("connect", connectE.getConnect());

            userList.add(userMap);
          }
          String userListJson = objectMapper.writeValueAsString(userList);
          webSocketHandler.sendMessageToAll(userListJson);
        }
      }
    }catch (Exception e){
      System.out.println(e);
    }
    return "Data received successfully";
  }
}
