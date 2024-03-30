package com.example.gameproject.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article_E {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false)
  private Long id;

  @Column(name="title", nullable = false)
  private String title;

  @Column(name="content", nullable = false)
  private String content;

  @Column(name = "author", nullable = false)
  private String author;

  @CreatedDate
  @Column(name = "created_at")
  private LocalDateTime createAt;

  @LastModifiedDate //엔티티가 수정될 때 수정 시간 저장
  @Column(name = "updated_at")
  private LocalDateTime updateAt;

  @Builder
  public Article_E(String author, String title, String content){
    this.author = author;
    this.title = title;
    this.content = content;
  }

  public void update(String title, String content){
    this.title = title;
    this.content = content;
  }



}
