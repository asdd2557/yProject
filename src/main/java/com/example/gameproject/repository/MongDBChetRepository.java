package com.example.gameproject.repository;

import com.example.gameproject.entity.Chat_E;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface MongDBChetRepository  extends ReactiveMongoRepository<Chat_E, String> {

  @Tailable//커서를 안닫고 계속 유지한다.
@Query("{sender:?0,receiver:?1}")
  Flux<Chat_E> mfindBySender(String sender, String receiver); //reponse 를 유지하며 데이터를 계속 흘려보내기

}
