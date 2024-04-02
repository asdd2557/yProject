
package com.example.gameproject.service;

import com.example.gameproject.entity.Member_E;
import com.example.gameproject.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {
  @Autowired
  MemberRepository memberRepository;
  public void test(){
    memberRepository.save(new Member_E(1L,"A"));

    Optional<Member_E> memberE = memberRepository.findById(1L); //단건 조회
    List<Member_E> allMembers = memberRepository.findAll();

    memberRepository.deleteById(1L);
  }
}

