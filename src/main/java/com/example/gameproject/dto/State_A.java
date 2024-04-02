package com.example.gameproject.dto;

import com.example.gameproject.entity.State_E;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;


@Getter
@Setter
@ToString
public class State_A {
  private String id;
  private String nickName;

  private List<Integer> head;
  private List<Integer> body;
  private List<Integer> foot;
  private List<Integer> hand;
  private List<Integer> ring;
  private int level;

  public static State_A getState_A(State_E state_e){
    State_A state_a = new State_A();
    state_a.setId(state_e.getId());
    state_a.setNickName(state_e.getNickName());
    state_a.setHead(state_e.getHead());
    state_a.setBody((state_e.getBody()));
    state_a.setHand(state_e.getHand());
    state_a.setRing(state_e.getRing());
    state_a.setFoot(state_e.getFoot());
    state_a.setLevel(state_a.getLevel());
    return state_a;
  }
}
