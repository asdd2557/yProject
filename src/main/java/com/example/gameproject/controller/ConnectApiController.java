package com.example.gameproject.controller;

import com.example.gameproject.dto.Connect_A;
import com.example.gameproject.entity.Connect_E;
import com.example.gameproject.entity.Game_E;
import com.example.gameproject.entity.User_E;
import com.example.gameproject.repository.MongDBGameRepository;
import com.example.gameproject.service.ConnectService;

import com.example.gameproject.service.GameService;
import com.example.gameproject.service.UserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@RestController
public class ConnectApiController {
  private final ConnectService connectService;
  private final MongDBGameRepository mongDBGameRepository;
  private final GameService gameService;

@PostMapping("/api/checkLogin")
public void checkLogin(@RequestBody Map<String, String> requestBody, Principal principal){

}


  @PutMapping("/api/connectUpdate")
  public Connect_E connectUpdate(@RequestBody Map<String, String> requestBody, Principal principal) {
    System.out.println("connectUpdate: "+ requestBody.get("connect"));
    LocalTime currentTime = LocalTime.now();
    String connect = requestBody.get("connect");
    int updatedate = currentTime.getHour() * 10000 + currentTime.getMinute() * 100 + currentTime.getSecond();
    Connect_E connectE = connectService.loadByEmail(principal.getName());
    connectE.setConnect(connect);
    connectE.setMatching(updatedate);
    return connectService.updateConnect(connectE);
  }

  @GetMapping("/api/matchingfind")
  public List<Connect_E> matchingUserFind(Principal principal) {

    String connect = connectService.loadByEmail(principal.getName()).getConnect();

    if (!Objects.equals(connect.trim(), "2")) {
      return null;
    }

    List<Connect_E> connectEs = connectService.loadByConnect("2");
    Connect_E opconnectE = new Connect_E();
    Connect_E meconnectE = new Connect_E();
    List<Connect_E> connect_es = new ArrayList<>();
    opconnectE.setMatching(9999999);

    for (Connect_E connectE1 : connectEs) {
      if (opconnectE.getMatching() > connectE1.getMatching() && !Objects.equals(principal.getName(), connectE1.getEmail())) {
        opconnectE = connectE1;
      }
      if (Objects.equals(principal.getName(), connectE1.getEmail())) {
        meconnectE = connectE1;
      }
    }
    connect_es.add(meconnectE);
    connect_es.add(opconnectE);

    return connect_es;
  }

  @PostMapping("/api/firstgamesave")
  public Mono<Game_E> firstgamesave(@RequestBody Map<String, Object> requestData, Principal principal) {
    Connect_E connectE1 = connectService.loadByEmail(principal.getName());
    Map<String, Object> opconnectE = (Map<String, Object>) requestData.get("opconnectE");
  Connect_E connectE2 = connectService.loadByEmail((String)opconnectE.get("email"));
    Game_E gameE = new Game_E();
    if ((Integer) connectE1.getMatching() < (Integer) opconnectE.get("matching")) {
      gameE.setUser1(connectE1.getEmail());
      gameE.setUser2(opconnectE.get("email").toString());
    } else {
      return null;
    }
    String gameID = LocalDateTime.now().toString() + connectE1.getEmail() + connectE2.getEmail();
    gameE.setTurn("0");
    gameE.setTime("15");
    gameE.setId(gameID);
    gameE.setTable(getTable19n19());
    gameE.setTableLog(new ArrayList<>());
    connectE1.setPosition(gameID);
    connectE1.setConnect("3");
    connectE2.setPosition(gameID);
    connectE2.setConnect("3");

    connectService.updateConnect(connectE1);
    connectService.updateConnect(connectE2);
    return gameService.save(gameE);
  }



  @GetMapping("/api/gamefind")
  public Mono<Game_E> gameFindByUser(Principal principal) {
    System.out.println("gamefind");
    // 이메일로 Connect_E 객체 로드
    Connect_E connectE = connectService.loadByEmail(principal.getName());
    return gameService.findById(connectE.getPosition());
  }


  public String[][] getTable19n19() {
    String[][] table = new String[19][19];
    for (int i = 0; i < 19; i++) {
      for (int ii = 0; ii < 19; ii++) {
        table[i][ii] = "0";
      }
    }
    return table;
  }
}
