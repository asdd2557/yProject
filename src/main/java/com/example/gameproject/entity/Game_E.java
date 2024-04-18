package com.example.gameproject.entity;

import com.example.gameproject.dto.Game_A;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import java.time.LocalDateTime;


@Data
@Document(collection = "game")
public class Game_E {
  @Id
  private String id;
  @Indexed
  private String user1;
  @Indexed
  private String user2;


 public Game_A toArticle(Game_E gameE){
   Game_A gameA = new Game_A();
   gameA.setId(gameE.getId());
   gameA.setUser1(gameE.getUser1());
   gameA.setUser2(gameE.getUser2());

   return gameA;
 }

}
