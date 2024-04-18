package com.example.gameproject.handler;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;


import java.util.ArrayList;
import java.util.List;

public class WebSocketHandler extends TextWebSocketHandler {
  private List<WebSocketSession> sessions = new ArrayList<>();




  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    System.out.println("afterConnectionEstablished! Session ID: " + session.getId());
    sessions.add(session);
    System.out.println("Session added. Total sessions: " + sessions.size());
  }

  @Override
  public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    System.out.println("Message received: " + message.getPayload());
    for (WebSocketSession s : sessions) {
      s.sendMessage(message);
    }
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    System.out.println("WebSocket connection closed. Session ID: " + session.getId());
    System.out.println("Close status: " + status);
    sessions.remove(session);
    System.out.println("Session removed. Total sessions: " + sessions.size());
  }

  @Override
  public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
    System.err.println("WebSocket error. Session ID: " + session.getId());
    exception.printStackTrace();
  }


  public void sendMessageToAll(String message) {
    System.out.println("in sendMessageToAll");
    for (WebSocketSession session : sessions) {
      try {
        System.out.println("Before Message sent: " + message + " to session: " + session.getId());
        session.sendMessage(new TextMessage(message));
        System.out.println("Message sent: " + message + " to session: " + session.getId());
      } catch (Exception e) {
        e.printStackTrace();
        System.out.println("Not Message send to session: " + session.getId());
      }
    }
  }
}
