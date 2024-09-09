package com.example.gameproject.controller;

import com.example.gameproject.entity.Connect_E;
import com.example.gameproject.entity.User_E;
import com.example.gameproject.service.ConnectService;
import com.example.gameproject.service.UserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ChangeStreamEvent;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
@RequiredArgsConstructor
@RestController
@Component
public class ConnectViewController {
  private final ConnectService connectService;
  private final UserDetailService userDetailService;

  @Async
  @GetMapping("/userlist")
  public List<Map<String, String>> getUserListAsync() {

    try {
      TimeUnit.SECONDS.sleep(1);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    // 사용자 정보를 리스트로 반환
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

      userMap.put("connect", connectE.getConnect());

      userList.add(userMap);
    }

    return userList;
  }

  @PutMapping ("/savesessionid")
  public String receiveSessionId(@RequestBody SocketIdPayload payload) {
    String socketId = payload.getSocketId();
    System.out.println("받은 세션 ID: " + socketId);
    // 여기서 세션 ID를 처리하는 로직을 추가할 수 있음
    return "세션 ID 전송 완료";
  }

  // 세션 ID를 받기 위한 DTO 클래스
  static class SocketIdPayload {
    private String socketId;

    public String getSocketId() {
      return socketId;
    }

    public void setSocketId(String socketId) {
      this.socketId = socketId;
    }
  }

  // 세션 ID를 받기 위한 DTO 클래스
  static class SessionIdPayload {
    private String sessionId;

    public String getSessionId() {
      return sessionId;
    }

    public void setSessionId(String sessionId) {
      this.sessionId = sessionId;
    }
  }



}
