package com.example.gameproject.dto;

import com.example.gameproject.entity.Article_E;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor // 기본 생성자 추가
@AllArgsConstructor
@Getter
public class Article_A {
  private String title;
  private String content;

  public Article_E toEntity(String author){
    return Article_E.builder().title(title).content(content).author(author).build();
  }

  public Article_A(Article_E article_e){
    this.title = article_e.getTitle();
    this.content = article_e.getContent();
  }
}
