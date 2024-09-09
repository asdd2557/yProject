package com.example.gameproject.config.websocket;

import com.example.gameproject.config.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.HandshakeFailureException;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;

import java.util.Map;

@RequiredArgsConstructor
public class CustomHandshakeInterceptor implements HandshakeInterceptor {

  private final TokenProvider tokenProvider;

  @Override
  public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                 WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
    // 토큰 추출
    String token = extractTokenFromQueryParams(request.getURI().getQuery());
    System.out.println("CustomHandshakeInterceptor User: "+ request.getPrincipal());
    System.out.println("CustomHandshakeInterceptor accessToken: " + token);

    // 토큰 유효성 검사
    if (token != null && tokenProvider.validToken(token)) {
      String username = tokenProvider.getUsernameFromToken(token);
      System.out.println("Valid Token, Username: " + username);
      attributes.put("username", username);  // WebSocket 세션에 사용자 정보 저장
      return true;  // 인증 성공 시 핸드셰이크 진행
    } else {
      System.out.println("Invalid Token");
      throw new HandshakeFailureException("Invalid token");
    }
  }

  @Override
  public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                             WebSocketHandler wsHandler, Exception exception) {
    // 핸드셰이크 후 처리 (필요 시 구현)
    System.out.println("After Handshake: " + (exception == null ? "Success" : "Failure"));
  }

  private String extractTokenFromQueryParams(String query) {
    if (query != null && query.contains("token=")) {
      return query.split("token=")[1];
    }
    return null;
  }
}
