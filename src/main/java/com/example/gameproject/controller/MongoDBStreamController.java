package com.example.gameproject.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MongoDBStreamController {

  @PostMapping("/receive-data")
  public void receiveData(@RequestBody Object data) {
    // 데이터를 처리하는 로직을 여기에 작성합니다.

  }
}
