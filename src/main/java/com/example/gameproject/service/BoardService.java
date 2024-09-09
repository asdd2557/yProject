package com.example.gameproject.service;

import com.example.gameproject.entity.Connect_E;
import com.example.gameproject.entity.Game_E;
import com.example.gameproject.entity.User_E;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import com.example.gameproject.handler.WebSocketHandler;
import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import static java.util.Collections.emptyMap;

@RequiredArgsConstructor
@Service
public class BoardService {

  private final ConnectService connectService;
  private final UserDetailService userDetailService;
  private final GameService gameService;
  private final WebSocketHandler webSocketHandler;
  private final ObjectMapper objectMapper;

  @Autowired
  private ThreadPoolTaskScheduler threadPoolTaskScheduler;

  private ScheduledFuture<?> scheduledFuture;                //타이머를 위한 변수

  public Mono<String> getUserTurn(Mono<Game_E> game_eMono) {
    return game_eMono.map(Game_E::getTurn);
  }

  public Mono<String> getUserColor(Mono<Game_E> game_eMono, Principal principal) {
    return game_eMono.map(data -> {
      if (data.getUser1().equals(principal.getName())) {
        return "Black";
      }
      if (data.getUser2().equals(principal.getName())) {
        return "White";
      }
      return null;
    });
  }

  public void boardTimerMove(String userName1, String userName2){
    if(scheduledFuture != null){

      scheduledFuture.cancel(true);
    }
    scheduledFuture = threadPoolTaskScheduler.schedule(() -> {
      try {UserDefeatAddOrSocketSend(userName1);
          UserVictoryAddOrSocketSend(userName2);
      } catch (JsonProcessingException e) {throw new RuntimeException(e);}
    }, new Date(System.currentTimeMillis() + 15000));
  }


  public void UserDefeatAddOrSocketSend(String userName) throws JsonProcessingException {
    Map<String, String> msg = new HashMap<>();
    Connect_E connectE =  connectService.loadByEmail(userName);
    User_E userE = userDetailService.loadUserByUsername(userName);
    userE.setDefeat(userE.getDefeat() + 1);
    connectE.setConnect("1");
    connectService.updateConnect(connectE);
    userDetailService.update(userE);
    msg.put("type", "deFeat");
    webSocketHandler.sendMessageToTarget(objectMapper.writeValueAsString(msg),connectE.getSocket());
    gameService.findById(connectE.getPosition())
        .flatMap(data -> gameService.delete(data))
        .subscribe();
  }

  public void UserVictoryAddOrSocketSend(String userName) throws JsonProcessingException {
    Map<String, String> msg = new HashMap<>();
    Connect_E connectE =  connectService.loadByEmail(userName);
    connectE.setConnect("1");
    connectService.updateConnect(connectE);
    User_E userE = userDetailService.loadUserByUsername(userName);
    userE.setVictory(userE.getVictory() + 1);
    userDetailService.update(userE);
    msg.put("type", "vicTory");
    webSocketHandler.sendMessageToTarget(objectMapper.writeValueAsString(msg),connectE.getSocket());
  }

}
