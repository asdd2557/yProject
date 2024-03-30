package com.example.gameproject.controller;

import com.example.gameproject.dto.ArticleListView_A;
import com.example.gameproject.dto.Article_A;
import com.example.gameproject.entity.Article_E;
import com.example.gameproject.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class BlogViewController {
  private final BlogService blogService;

  @GetMapping("/articles")
  public String getArticle(Model model){
    List<ArticleListView_A> articles = blogService.findAll().stream()
        .map(ArticleListView_A::new)
        .toList();
    model.addAttribute("articles", articles);

    return "articleList/articleList";
  }

  @GetMapping("/articles/{id}")
  public String getArticle(@PathVariable Long id, Model model){
    Article_E article = blogService.findById(id);
    model.addAttribute("article", new ArticleListView_A(article));

    return "article/article";
  }

  @GetMapping("/new-article")
  public String newArticle(@RequestParam(required = false) Long id, Model model){
    if(id == null){
      model.addAttribute("article", new ArticleListView_A());
    }else {
      Article_E article_e = blogService.findById(id);
      model.addAttribute("article", new ArticleListView_A(article_e));
    }
    return "newArticle/newArticle";
  }


  @GetMapping("/login")
  public String login(){
    return "oauthLogin/oauthLogin";
  }
}
