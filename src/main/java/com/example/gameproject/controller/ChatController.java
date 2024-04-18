package com.example.gameproject.controller;

import com.example.gameproject.entity.Chat_E;
import com.example.gameproject.repository.MongDBChetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class ChatController {
  private final MongDBChetRepository mongDBChetRepository;
  @GetMapping(value = "/sender/{sender}/receiver/{receiver}", produces = MediaType.TEXT_EVENT_STREAM_VALUE) //계속 유지됨
  public Flux<Chat_E> getMsg(@PathVariable String sender, @PathVariable String receiver){

    return mongDBChetRepository.mfindBySender(sender, receiver).subscribeOn(Schedulers.boundedElastic());
  }



  @PostMapping("/chat") //계속 유지됨
  public Mono<Chat_E> setMsg(@RequestBody Chat_E chatE){
    chatE.setCreateAt(LocalDateTime.now());
    return mongDBChetRepository.save(chatE);
  }




}
