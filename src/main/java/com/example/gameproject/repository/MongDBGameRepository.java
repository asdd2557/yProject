package com.example.gameproject.repository;


import com.example.gameproject.entity.Chat_E;
import com.example.gameproject.entity.Game_E;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface MongDBGameRepository extends ReactiveMongoRepository<Game_E, String> {
  @Query("{user1:?0}")
  Flux<Game_E> findByUser1(String sender); //reponse 를 유지하며 데이터를 계속 흘려보내기
  @Query("{user2:?0}")
  Flux<Game_E> findByUser2(String sender); //reponse 를 유지하며 데이터를 계속 흘려보내기
}
