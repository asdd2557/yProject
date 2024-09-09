package com.example.gameproject.controller;

import com.example.gameproject.dto.Article_A;
import com.example.gameproject.entity.Article_E;
import com.example.gameproject.entity.User_E;
import com.example.gameproject.service.BlogService;
import com.example.gameproject.service.UserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class BlogApiController {

  private final BlogService blogService;
  private final UserDetailService userDetailService;

  @GetMapping("/api/getname")
  public String getName(Principal principal) {

    return userDetailService.loadUserByUsername(principal.getName()).getNickname();
  }
  @GetMapping("/api/getemail")
  public Map<String, String> getEmail(Principal principal){
    Map<String, String> response = new HashMap<>();
    response.put("email", principal.getName());  // 가정: principal.getName()이 이메일 주소를 반환
    return response;
  }

  @GetMapping("/api/getrecord")
  public String getRecord(Principal principal) {
    User_E userE = userDetailService.loadUserByUsername(principal.getName());
    String record = "승리: "+ String.valueOf(userE.getVictory())  +"패배: "+ String.valueOf(userE.getDefeat());
    return record;
  }




  @PostMapping ("/api/getsubnamebysubname")
  public String getSubNameBySubNickname(@RequestBody Map<String, String> requestBody) {
    String subnickname = requestBody.get("subnickname");
    try {
      return userDetailService.loadUserBySubNickname(subnickname).getSubnickname();
    }catch (Exception e){
      return null;
    }
  }

  @GetMapping("/api/getsubnamebyname")
  public String getSubNameByName(Principal principal) {
    try {
      return userDetailService.loadUserByUsername(principal.getName()).getSubnickname();

    }catch (Exception e){
      return null;
    }
  }


  @PutMapping("/api/updatesubnickname")
  public String UpdateSubNickName(@RequestBody Map<String, String> requestBody, Principal principal) {
    try {
      String subnickname = requestBody.get("subnickname");
      userDetailService.updateBySubNickname(userDetailService.loadUserByUsername(principal.getName()),subnickname);
      return subnickname;
    }catch (Exception e){
      return null;
    }
  }

  @GetMapping("/api/profile")
  public String getProfile(HttpServletRequest request) {
    return (String) request.getSession().getAttribute("profilePictureUrl");
  }

  @GetMapping("/api/detail")
  public User_E getdetail(Principal principal) {
    return userDetailService.loadUserByUsername(principal.getName());
  }


  @PostMapping("/api/articles")
  public ResponseEntity<Article_E> addArticle(@RequestBody Article_A article_a, Principal principal) {
    Article_E article_e = blogService.save(article_a, principal.getName());

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(article_e);
  }

  @GetMapping("api/articles")
  public ResponseEntity<List<Article_A>> findAllArticles() {
    List<Article_A> articles = blogService.findAll()
        .stream()
        .map(Article_A::new)
        .toList();

    return ResponseEntity.ok()
        .body(articles);
  }

  @GetMapping("/api/articles/{id}")
  public ResponseEntity<Article_A> findArticle(@PathVariable long id) {
    Article_E article_e = blogService.findById(id);

    return ResponseEntity.ok()
        .body(new Article_A(article_e));
  }

  @DeleteMapping("/api/articles/{id}")
  public ResponseEntity<Void> deleteArticle(@PathVariable long id) {
    blogService.delete(id);

    return ResponseEntity.ok()
        .build();
  }

  @PutMapping("/api/articles/{id}")
  public ResponseEntity<Article_E> updateArticle(@PathVariable long id, @RequestBody Article_A article_a) {
    Article_E article_e = blogService.update(id, article_a);

    return ResponseEntity.ok()
        .body(article_e);
  }


}

