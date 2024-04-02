package com.example.gameproject.entity;


import com.example.gameproject.dto.State_A;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@ToString

public class State_E {

  private String id;

  private String passWord;

  private String nickName;

  private int level;


  private List<Integer> head;

  private List<Integer> body;

  private List<Integer> foot;

  private List<Integer> hand;

  private List<Integer> ring;


  public static State_E toSaveEntity(State_A state_a){
    State_E state_e =  new State_E();
    state_e.setId(state_a.getId());
    state_e.setNickName(state_a.getNickName());
    state_e.setHead(state_a.getHead());
    state_e.setBody(state_a.getBody());
    state_e.setHand(state_a.getHand());
    state_e.setFoot(state_a.getFoot());
    state_e.setRing(state_a.getRing() );
    state_e.setLevel(state_a.getLevel());
    return state_e;
  }

}
