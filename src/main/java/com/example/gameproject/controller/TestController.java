package com.example.gameproject.controller;

import com.example.gameproject.dto.Article_A;
import com.example.gameproject.entity.Article_E;
import com.example.gameproject.entity.Member_E;
import com.example.gameproject.service.BlogService;
import com.example.gameproject.service.TestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Slf4j //로깅을 위한 거(어노테이션) 로깅 : 뭐든 기록하는거
@RequiredArgsConstructor
public class TestController {


  @GetMapping("/test")
  public String getAllMembers(){
    return "test/test";
  }


}
