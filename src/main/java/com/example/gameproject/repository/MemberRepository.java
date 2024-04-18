package com.example.gameproject.repository;

import com.example.gameproject.entity.Member_E;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member_E, Long> {
}
