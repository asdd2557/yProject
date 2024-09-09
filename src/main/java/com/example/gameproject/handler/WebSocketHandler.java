package com.example.gameproject.handler;

import com.example.gameproject.entity.Connect_E;
import com.example.gameproject.service.ConnectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {

  private final ConnectService connectService;
  private Map<String, WebSocketSession> socketMap = new HashMap<>();
  private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    // Principal 대신에 attributes에서 username 가져오기
    String username = (String) session.getAttributes().get("username");
    System.out.println("afterConnectionEstablished: username: " + username);
       Connect_E connectE = connectService.loadByEmail(username);

    if (connectE == null) {
      connectE = new Connect_E();
      connectE.setConnect("1");
      connectE.setMatching(0);
      connectE.setEmail(username);
      connectE.setSocket(session.getId());
      connectService.save(connectE);
    } else {
      connectE.setConnect("1");
      connectE.setMatching(0);
      connectE.setSocket(session.getId());
      connectService.updateConnect(connectE);
    }

    sessions.add(session);
    socketMap.put(session.getId(), session);
    System.out.println("WebSocket connection Open. Session ID: " + session.getId() + " // Username: " + username);
  }

  public boolean connectCheckByPrincipal(String principal) {
    return connectService.loadByEmail(principal) != null;
  }

  @Override
  public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    System.out.println(message.getPayload());
    for (WebSocketSession s : sessions) {
      s.sendMessage(message);
    }
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    String username = (String) session.getAttributes().get("username");
    System.out.println("Session Closed: " + session.getId() + " // Username: " + username);
    sessions.remove(session);
    socketMap.remove(session.getId());
  }

  @Override
  public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
    String username = (String) session.getAttributes().get("username");
    System.out.println("Session Error ID: " + session.getId() + " // Username: " + username);
  }

  public void sendMessageToAll(String message) {
    for (WebSocketSession session : sessions) {
      try {
        session.sendMessage(new TextMessage(message));
      } catch (Exception ignored) {
      }
    }
  }

  public void sendMessageToTarget(String message, String target) {
    WebSocketSession session = socketMap.get(target);
    if (session != null) {
      try {
        session.sendMessage(new TextMessage(message));
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
    } else {
      System.out.println("No session found for target: " + target);
    }
  }
}
