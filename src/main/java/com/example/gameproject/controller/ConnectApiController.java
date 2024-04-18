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
import org.springframework.scheduling.annotation.Async;
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


  @PutMapping("/api/connectupdateorsave")
  public Connect_E connectUpdateorSave(@RequestBody Map<String, String> requestBody, Principal principal) {
    LocalTime currentTime = LocalTime.now();
    String connect = requestBody.get("connect");
    int updatedate = currentTime.getHour() * 10000 + currentTime.getMinute() * 100 + currentTime.getSecond();
    Connect_E connectE = connectService.loadByEmail(principal.getName());
    if (connectE == null) {
      connectE = connectService.save(principal);
    }
    connectE.setConnect(connect);
    connectE.setMatching(updatedate);
    return connectService.updateConnect(connectE);
  }

  @GetMapping("/api/matchingfind")
  public List<Connect_E> matchingUserFind(Principal principal) {
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
  public Mono<Game_E> firstgamesave(@RequestBody Map<String, Object> requestData) {

    Map<String, Object> meconnectE = (Map<String, Object>) requestData.get("meconnectE");
    Map<String, Object> opconnectE = (Map<String, Object>) requestData.get("opconnectE");

    Game_E gameE = new Game_E();
    if ((Integer) meconnectE.get("matching") < (Integer) opconnectE.get("matching")) {
      gameE.setUser1(meconnectE.get("email").toString());
      gameE.setUser2(opconnectE.get("email").toString());

    } else {
      gameE.setUser1(opconnectE.get("email").toString());
      gameE.setUser2(meconnectE.get("email").toString());
    }
    gameE.setTurn("0");
    gameE.setTime("15");
    return gameService.save(gameE);
  }

  @GetMapping("/api/gamefind")
  public Mono<Game_E> gameFindByUser(Principal principal) {
    Flux<Game_E> gameuser1 = mongDBGameRepository.findByUser1(principal.getName());
    Flux<Game_E> gameuser2 = mongDBGameRepository.findByUser2(principal.getName());
    return Flux.concat(gameuser1, gameuser2)
        .take(1)
        .switchIfEmpty(Flux.error(new RuntimeException("No game found for the user")))
        .next();
  }
}
