package com.example.gameproject.repository;

import com.example.gameproject.entity.Article_E;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogRepository extends JpaRepository<Article_E, Long> {
}
