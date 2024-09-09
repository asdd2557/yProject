package com.example.gameproject.service;

import com.example.gameproject.entity.Game_E;
import com.example.gameproject.exception.GameNotFoundException;
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


  public Mono<Void> delete(Game_E gameE) {
   return mongDBGameRepository.delete(gameE);
  }


  public Mono<Game_E> save(Game_E gameE){
    try {

      return mongDBGameRepository.save(gameE);
    }catch(Exception e){
      System.out.println("sout:  "+e);
      return mongDBGameRepository.save(gameE);
  }
}

  public Mono<Game_E> findById(String id) {
    return mongDBGameRepository.findById(id)
        .flatMap(game -> Mono.justOrEmpty(game)) // 검색된 결과가 있을 경우 해당 결과 반환
        .switchIfEmpty(Mono.empty()); // 검색된 결과가 없을 경우 빈 결과 반환
  }


}
