package com.example.gameproject.service;


import com.example.gameproject.entity.Chat_E;


import com.example.gameproject.repository.MongDBChetRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ChatService {

  private final MongDBChetRepository mongoDBRepository;



  public Mono<Chat_E> createChat(Chat_E chatE) {
    return mongoDBRepository.save(chatE);
  }

  public Mono<Chat_E> updateChat(String Chat_EId, Chat_E updatedChat_E) {
    return mongoDBRepository.findById(Chat_EId)
        .flatMap(existingChat_E -> {
          existingChat_E.setCreateAt(updatedChat_E.getCreateAt());
          existingChat_E.setSender(updatedChat_E.getReceiver());
          return mongoDBRepository.save(existingChat_E);
        });
  }



  public Flux<Chat_E> findAllChats() {
    return mongoDBRepository.findAll();
  }

  public Mono<Void> deleteChat(String Chat_EId) {
    return mongoDBRepository.deleteById(Chat_EId);
  }
}



