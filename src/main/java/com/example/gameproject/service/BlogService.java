package com.example.gameproject.service;

import com.example.gameproject.dto.Article_A;
import com.example.gameproject.entity.Article_E;
import com.example.gameproject.repository.BlogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BlogService {
  private final BlogRepository blogRepository;

  public Article_E save(Article_A article_a, String userName){
    return blogRepository.save(article_a.toEntity(userName));
  }

  public List<Article_E> findAll(){
    return blogRepository.findAll();
  }

  public Article_E findById(long id){
    return blogRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
  }

  public void delete(long id){
    Article_E article_e = blogRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("not found : " +id));

    authorizeArticleAuthor(article_e);
    blogRepository.delete(article_e);
  }

  @Transactional
  public Article_E update(long id, Article_A article_a) {
    Article_E article_e = blogRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("not found: " +id));

     authorizeArticleAuthor(article_e);
    article_e.update(article_a.getTitle(), article_a.getContent());

    return article_e;
  }

  //게시글을 작성한 유저인지 확인
  private static void authorizeArticleAuthor(Article_E article_e){
    String userName = SecurityContextHolder.getContext().getAuthentication().getName();

    if(!article_e.getAuthor().equals(userName)){
      throw new IllegalArgumentException("not authorized");
    }
  }
}
