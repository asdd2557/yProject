package com.example.gameproject.config;

import com.example.gameproject.config.TokenAuthenticationFilter;
import com.example.gameproject.config.jwt.TokenProvider;
import com.example.gameproject.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.example.gameproject.config.oauth.OAuth2SuccessHandler;
import com.example.gameproject.config.oauth.OAuth2UserCustomService;
import com.example.gameproject.entity.User_E;
import com.example.gameproject.repository.DynamoRefreshTokenRepository;
import com.example.gameproject.repository.DynamoUserRepository;
import com.example.gameproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@RequiredArgsConstructor
@Configuration
public class WebOauthSecurityConfig {

 private final OAuth2UserCustomService oAuth2UserCustomService;
 private final TokenProvider tokenProvider;
 private final DynamoUserRepository dynamoUserRepository;
private final DynamoRefreshTokenRepository dynamoRefreshTokenRepository;

@Bean
public WebSecurityCustomizer configure(){
 return (web) -> web.ignoring()
     .requestMatchers(toH2Console())
     .requestMatchers(
         new AntPathRequestMatcher("/img/**"),
         new AntPathRequestMatcher("/css/**"),
         new AntPathRequestMatcher("/js/**")
     );
}



 @Bean
 public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
  //토큰 방식으로 인증을 하기 때문에 기존에 사용하던 폼 로그인, 세션 비활성화 bearer방식을 쓸거기 때문에 아래 있는것들 disable해야함
  http.csrf().disable()
      .httpBasic().disable() //Basic안씀,
      .formLogin().disable() //폼로그인 안함
      .logout().disable();

  http.sessionManagement()
      .sessionCreationPolicy(SessionCreationPolicy.STATELESS);//세션 사용 안함

  http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class); //그냥 필터 넣으면 안됨 이후, 전으로 넣어야함
//UsernamePasswordAuthenticationFilter는 login요청해서 username, password 전송하함(post) 그런데 formLogin.disable해서 비활성화 되어서 따로 위에 명시해줌
// UsernamePasswordAuthenticationFilter는 login요청해서 username, password 전송하면(post) 동작함

  http.authorizeRequests()
      .antMatchers("/api/token").permitAll()
      .antMatchers("/api/**").authenticated()//인증이 필요함
      .antMatchers("/mong_save").authenticated()//인증이 필요함
      .antMatchers("/ws").permitAll()
      .anyRequest().permitAll();


  http.oauth2Login()
      .loginPage("/login")
      .authorizationEndpoint()//구글 로그인이 완료된 뒤의 후처리가 필요함(엑세스토큰+사용자프로필정보)를 리턴해줌
      // Authorization 요청과 관련된 상태 저장
      .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository())
      .and()
      .successHandler(oAuth2SuccessHandler()) //인증 성공 시 실행할 핸들러
      .userInfoEndpoint()
      .userService(oAuth2UserCustomService);
  http.logout()
      .logoutSuccessUrl("/login");

// api로 시작하는 url 인 경우 401 상태 코드를 반환하도록 예외 처리
  http.exceptionHandling()
      .defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
          new AntPathRequestMatcher("/api/**"));

  return http.build();
 }


 @Bean
 public OAuth2SuccessHandler oAuth2SuccessHandler() {
  return new OAuth2SuccessHandler(tokenProvider,dynamoRefreshTokenRepository,
      oAuth2AuthorizationRequestBasedOnCookieRepository(),
      dynamoUserRepository
  );
 }

 @Bean
 public TokenAuthenticationFilter tokenAuthenticationFilter() {
  return new TokenAuthenticationFilter(tokenProvider);
 }

 @Bean
 public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
  return new OAuth2AuthorizationRequestBasedOnCookieRepository();
 }

 @Bean
 public BCryptPasswordEncoder bCryptPasswordEncoder() {
  return new BCryptPasswordEncoder();
 }
}

