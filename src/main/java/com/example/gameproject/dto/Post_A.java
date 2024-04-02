package com.example.gameproject.dto;

import com.example.gameproject.entity.Post_E;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Post_A {
  private String title;
  private String content;

  public Post_E toEntity(){
    Post_E postE = new Post_E();
    postE.setTitle(title);
    postE.setContent(content);
    return postE;
  }
}
