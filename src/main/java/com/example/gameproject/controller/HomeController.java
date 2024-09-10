package com.example.gameproject.controller;

import com.example.gameproject.dto.ArticleListView_A;
import com.example.gameproject.dto.Article_A;
import com.example.gameproject.entity.Article_E;
import com.example.gameproject.entity.User_E;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

@Controller
@Slf4j //로깅을 위한 거(어노테이션) 로깅 : 뭐든 기록하는거
@RequiredArgsConstructor
public class HomeController {
  @GetMapping("/")
  public String homePage() {
    return "home/home";
  }

  }

