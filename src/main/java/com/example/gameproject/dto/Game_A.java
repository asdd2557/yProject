package com.example.gameproject.dto;

import com.example.gameproject.entity.Game_E;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor // 기본 생성자 추가
@AllArgsConstructor
@Getter
@Setter
public class Game_A {
  private String id;
  private String user1;
  private String user2;
  private List<List<Integer>> table1;

public Game_E toEntity(Game_A gameA){
  Game_E gameE = new Game_E();
  gameE.setId(gameA.getId());
  gameE.setUser1(gameA.getUser1());
  gameE.setUser2(gameA.getUser2());
  return gameE;
}

}
