package com.example.gameproject.controller;

import com.example.gameproject.dto.Article_A;
import com.example.gameproject.entity.Article_E;
import com.example.gameproject.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class BlogApiController {

  private final BlogService blogService;

  @PostMapping("/api/articles")
  public ResponseEntity<Article_E> addArticle(@RequestBody Article_A article_a, Principal principal) {
    Article_E article_e = blogService.save(article_a,principal.getName());

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

