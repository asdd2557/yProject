package com.example.gameproject.config.oauth;

import com.example.gameproject.config.jwt.RefreshToken;
import com.example.gameproject.config.jwt.TokenProvider;
import com.example.gameproject.entity.User_E;
import com.example.gameproject.repository.DynamoRefreshTokenRepository;
import com.example.gameproject.repository.DynamoUserRepository;
import com.example.gameproject.until.CookieUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.Duration;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
  public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);
  public static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(1);
  public static final String REDIRECT_PATH = "/home";


  private final TokenProvider tokenProvider;
  private final DynamoRefreshTokenRepository dynamoRefreshTokenRepository;
  private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
  private final DynamoUserRepository dynamoUserRepository;


  //최초 로그인 할시 실행됨
  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {


    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

    //picture
    addProfilePictureUrlCookie(response, oAuth2User);

    User_E user_e = dynamoUserRepository.findByEmail((String) oAuth2User.getAttributes().get("email")); //구글에 있는 이메일 가져와서 내 db에서 find

    String refreshToken = tokenProvider.generateToken(user_e, REFRESH_TOKEN_DURATION);

    saveRefreshToken(user_e.getId(), refreshToken);
    addRefreshTokenToCookie(request, response, refreshToken);

    // 1. 세션 생성 및 principal 값 저장 (리다이렉트 전에 수행)
    HttpSession session = request.getSession();
    session.setAttribute("principal", user_e);

    // 2. 액세스 토큰 생성 -> 패스에 액세스 토큰 추가
    String accessToken = tokenProvider.generateToken(user_e, ACCESS_TOKEN_DURATION);
    System.out.println("onAuthenticationSuccess User Name: " + user_e.getNickname());
    System.out.println("onAuthenticationSuccess AccessToken: " + accessToken);
    String targetUrl = getTargetUrl(accessToken);
    // 3. 인증 관련 설정값, 쿠키 제거
    clearAuthenticationAttributes(request, response);
    System.out.println("onAuthenticationSuccess: "+ accessToken);
    // 4. 리다이렉트
    getRedirectStrategy().sendRedirect(request, response, targetUrl);


  }

  //생성된 리프레시 토큰을 전달받아 데이터 베이스에 저장
  private void saveRefreshToken(String userId, String newRefreshToken) {
    RefreshToken refreshToken = dynamoRefreshTokenRepository.findByUserId(userId);
        if(refreshToken != null){
          refreshToken.update(newRefreshToken);
          dynamoRefreshTokenRepository.update(refreshToken);
        }else{
           refreshToken = new RefreshToken(userId, newRefreshToken);
          System.out.println("onAuthenticationSuccess User: " + userId);
          System.out.println("onAuthenticationSuccess refreshToken: " + refreshToken.getRefreshToken());
          dynamoRefreshTokenRepository.save(refreshToken);
        }
  }

  // 인증 관련 설정값, 쿠키 제거
  private void addRefreshTokenToCookie(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
    int cookieMaxAge = (int) REFRESH_TOKEN_DURATION.toSeconds();
    CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);
    CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, cookieMaxAge);
  }

  private void addProfilePictureUrlCookie(HttpServletResponse response, OAuth2User oAuth2User){
    String profilePictureUrl = (String) oAuth2User.getAttribute("picture");
    Cookie profileCookie = new Cookie("profilePictureUrl", profilePictureUrl);
    profileCookie.setPath("/");
    response.addCookie(profileCookie);
  }


  // 인증 관련 설정값, 쿠키 제거
  private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
    super.clearAuthenticationAttributes(request);
    authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
  }

  private String getTargetUrl(String token) {
    return UriComponentsBuilder.fromUriString(REDIRECT_PATH)
        .queryParam("token", token)
        .build()
        .toUriString();
  }
}