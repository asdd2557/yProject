package com.example.gameproject.config.websocket;

import com.example.gameproject.config.jwt.TokenProvider;
import com.example.gameproject.handler.WebSocketHandler;
import com.example.gameproject.service.ConnectService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

  private final ConnectService connectService;
  private final TokenProvider tokenProvider;

  @Bean
  public WebSocketHandler webSocketHandler() {
    return new WebSocketHandler(connectService);
  }

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(webSocketHandler(), "/ws")
        .addInterceptors(new HttpSessionHandshakeInterceptor(), new CustomHandshakeInterceptor(tokenProvider))
        .setAllowedOrigins("*"); // CORS 설정
  }
}