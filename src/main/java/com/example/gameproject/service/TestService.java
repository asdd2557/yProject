package com.example.gameproject.service;

import com.example.gameproject.entity.Member_E;
import com.example.gameproject.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestService {

  @Autowired
  MemberRepository memberRepository; //빈 주입

  public List<Member_E> getAllMembers(){
    return memberRepository.findAll();
  }
}
