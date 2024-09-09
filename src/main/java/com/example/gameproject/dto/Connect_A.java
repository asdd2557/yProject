package com.example.gameproject.dto;

import com.example.gameproject.entity.Connect_E;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class Connect_A {
  private String connect;
  private String email;
  private String position;
  private int matching;
  private String socket;
  public Connect_E toEntity(Connect_A connectA){
    Connect_E connectE = new Connect_E();
    connectE.setConnect(connectA.getConnect());
    connectE.setEmail(connectA.getEmail());
    connectE.setPosition(connectA.getPosition());
    connectE.setMatching(connectA.getMatching());
    connectE.setSocket(connectA.getSocket());
    return connectE;
  }
}



