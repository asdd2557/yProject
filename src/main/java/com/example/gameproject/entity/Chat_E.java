package com.example.gameproject.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import java.time.LocalDateTime;

@Data
@Document(collection = "chat")
public class Chat_E {
  @Id
  private String id;
  private String msg;
  @Indexed
  private String sender;
  @Indexed
  private String receiver;

  private LocalDateTime createAt;
}
