package com.example.gameproject.controller;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j //로깅을 위한 거(어노테이션) 로깅 : 뭐든 기록하는거
@RequiredArgsConstructor
public class PageController {


  @GetMapping("/home")
  public String homePage() {
    return "/home/home";
  }

  @GetMapping("/test2")
  public String testPage() {
    return "/test/test";
  }

  @GetMapping("/join")
  public String joinPage() {
    return "/join/join";
  }

  @GetMapping("/search")
  public String searchPage() {
    return "/search/search";
  }


  @GetMapping("/signup")
  public String signupPage() {
    return "/signup/signup";
  }



}