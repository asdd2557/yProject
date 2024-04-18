package com.example.gameproject.service;

import com.example.gameproject.entity.Game_E;
import com.example.gameproject.repository.MongDBGameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
@Service
public class GameService {
  private final MongDBGameRepository mongDBGameRepository;

  public Flux<Game_E> findByUser1(String username){
   return  mongDBGameRepository.findByUser1(username);
  }
  public Flux<Game_E> findByUser2(String username){
    return  mongDBGameRepository.findByUser2(username);
  }

  public Mono<Game_E> save(Game_E gameE){
    return mongDBGameRepository.save(gameE);
  }
}
