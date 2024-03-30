package com.example.gameproject.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class User_A
{
  private String id = UUID.randomUUID().toString();
  private String email;
  private String password;
  private String nickname;



  

}
