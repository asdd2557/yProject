package com.example.gameproject.config;

import com.example.gameproject.config.jwt.TokenProvider;
import com.example.gameproject.entity.User_E;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Header;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
  private final TokenProvider tokenProvider;
  private final static String HEADER_AUTHORIZATION = "Authorization";
  private final static String TOKEN_PREFIX = "Bearer";


  //권한이 필요한 사이트에 올경우 이필터를 타게됨
  //해더의 Authorization값으로 토큰값을 받아주고 유효한 토큰인지 확인
  @Override
  protected void doFilterInternal( // 이필터
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    // 요청 헤더의 Authorization 키의 값 조회
    String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION); //hearer가져옴
    // 가져온 값에서 접두사 제거 접두사 Bearer 제거 만약 값이 Null이거나 Bearer로 시작하지 않으면 null을 반환함
    String token = getAccessToken(authorizationHeader);


    //가져온 토큰이 유효한지 확인하고, 유효한 때는 인증 정보 설정
    if (tokenProvider.validToken(token)) { //토큰이 유효시 True 그렇지 않으면 False
      Authentication authentication = tokenProvider.getAuthentication(token); //메서드를 사용해 인증 정보를 가져오면 유저 객체가 반환됩니다. 유저 객체에는 유저 이름 , 권한 목록 과 같은 인증 정보가 포함됩니다.
      SecurityContextHolder.getContext().setAuthentication(authentication); //시큐리티 컨텍스트에 인증 정보를 설정
      // SecurityContextHolder.getContext()까지는 시큐리티를 저장할 수 있는 공간을 찾은것
      //SecurityContextHolder.getContext().setAuthentication(authentication);는 강제로 시큐리티의 세션에 접근하여 Authentication 객체를 저장
      //Spring Security에서 현재 실행 중인 스레드의 보안 컨텍스트에 인증 객체를 설정합니다. 이 메서드는 리턴값이 없으며, 단순히 현재 실행 중인 스레드의 보안 컨텍스트에 인증 객체를 설정하는 역할을 합니다.
    }
    filterChain.doFilter(request, response); //다 된다면 다시 체인을 탐
  }

  //토큰의 앞자리가 Bearer인지 확인하고 bearer를 짜른 토큰값을 넘겨줌
  private String getAccessToken(String authorizationHeader) {//해더에서 토큰 가져오는 함수
    if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
      return authorizationHeader.substring(TOKEN_PREFIX.length());
    }
    return null;
  }


}
