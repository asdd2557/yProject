package com.example.gameproject.controller;

import com.example.gameproject.entity.Chat_E;
import com.example.gameproject.entity.Game_E;
import com.example.gameproject.entity.User_E;
import com.example.gameproject.repository.DynamoConnectRepository;
import com.example.gameproject.repository.MongDBChetRepository;
import com.example.gameproject.repository.MongDBGameRepository;
import com.example.gameproject.service.ChatService;
import com.example.gameproject.service.UserDetailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.WebSocketHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
@RestController
@RequiredArgsConstructor
public class ChatController {
  private final com.example.gameproject.handler.WebSocketHandler webSocketHandler;
  private final ObjectMapper objectMapper;
  private final MongDBChetRepository mongDBChetRepository;
  private final MongDBGameRepository mongDBGameRepository;
  private final ChatService chatService;
  private final DynamoConnectRepository dynamoConnectRepository;
  private final UserDetailService userDetailService;
  @GetMapping(value = "/sender/{sender}/receiver/{receiver}", produces = MediaType.TEXT_EVENT_STREAM_VALUE) //계속 유지됨
  public Flux<Chat_E> getMsg(@PathVariable String sender, @PathVariable String receiver){

    return mongDBChetRepository.mfindBySender(sender, receiver).subscribeOn(Schedulers.boundedElastic());
  }



/*
  @PostMapping("/chat") //계속 유지됨
  public Mono<Chat_E> setMsg(@RequestBody Chat_E chatE){

    chatE.setCreateAt(LocalDateTime.now());
    return mongDBChetRepository.save(chatE);
  }
*/


  @PostMapping("/api/chat")
  public Mono<Chat_E> sendMsg(@RequestBody Map<String, Object> chatE, Principal principal) {
    Chat_E chat_e = new Chat_E();
    User_E userE = userDetailService.loadUserByUsername(principal.getName());
    Flux<Game_E> gameUser1 = mongDBGameRepository.findByUser1(principal.getName());
    Flux<Game_E> gameUser2 = mongDBGameRepository.findByUser2(principal.getName());

    chat_e.setMsg((String) chatE.get("msg"));
    chat_e.setCreateAt(LocalDateTime.now());
    if(userE.getSubnickname() != null){
      chat_e.setSender(userE.getSubnickname());
    }else{
      chat_e.setSender(principal.getName());
    }
    Mono<Void> gameUser1Processing = gameUser1.collectList()
        .flatMap(list -> {
          if (list.isEmpty()) {
            return Mono.empty();
          } else {
            return gameUser1.flatMap(game -> {
              String userSocket = dynamoConnectRepository.findByEmail(game.getUser2()).getSocket();
              chat_e.setReceiver(game.getUser2());

              try {
                Map<String, Object> chatMap = objectMapper.convertValue(chat_e, Map.class);
                chatMap.put("type", "msg");
                String jsonMessage = objectMapper.writeValueAsString(chatMap);
                webSocketHandler.sendMessageToTarget(jsonMessage, userSocket);
              } catch (JsonProcessingException e) {
                return Mono.error(e);
              }
              return Mono.empty();
            }).then();
          }
        });

    Mono<Void> gameUser2Processing = gameUser2.collectList()
        .flatMap(list -> {
          if (list.isEmpty()) {
            return Mono.empty();
          } else {
            return gameUser2.flatMap(game -> {
              String userSocket = dynamoConnectRepository.findByEmail(game.getUser1()).getSocket();
              chat_e.setReceiver(game.getUser1());
              try {
                Map<String, Object> chatMap = objectMapper.convertValue(chat_e, Map.class);
                chatMap.put("type", "msg");
                String jsonMessage = objectMapper.writeValueAsString(chatMap);
                webSocketHandler.sendMessageToTarget(jsonMessage, userSocket);
              } catch (JsonProcessingException e) {
                return Mono.error(e);
              }
              return Mono.empty();
            }).then();
          }
        });

    return Mono.when(gameUser1Processing, gameUser2Processing)
        .then(Mono.defer(() -> {
          return chatService.saveChat(chat_e);
        }));
  }



/*@PostMapping("/api/chat")
  public void chatTest(@RequestBody Map<String, Object> chatE, Principal principal){
  System.out.println("chatTest1"+(String) chatE.get("msg"));
  System.out.println("chatTest: "+principal.getName());
  System.out.println("chatTest On");
}*/

}
