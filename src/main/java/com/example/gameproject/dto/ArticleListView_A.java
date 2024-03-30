package com.example.gameproject.dto;

import com.example.gameproject.entity.Article_E;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@NoArgsConstructor
@Getter
public class ArticleListView_A {
  private  Long id;
  private  String title;
  private  String content;
  private LocalDateTime createdAt;
  private String author;

  public ArticleListView_A(Article_E article_e){
    this.id = article_e.getId();
    this.title = article_e.getTitle();
    this.content = article_e.getContent();
    this.createdAt = article_e.getCreateAt();
    this.author = article_e.getAuthor();
  }
}
