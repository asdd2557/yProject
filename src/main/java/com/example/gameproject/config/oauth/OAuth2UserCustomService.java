package com.example.gameproject.config.oauth;

import com.example.gameproject.dto.User_A;
import com.example.gameproject.entity.User_E;
import com.example.gameproject.repository.DynamoUserRepository;
import com.example.gameproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class OAuth2UserCustomService extends DefaultOAuth2UserService {
  private final DynamoUserRepository dynamoUserRepository;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    // 요청을 바탕으로 유저 정보를 담은 객체 반환
    OAuth2User user = super.loadUser(userRequest); //loadUser를 통해 사용자 정보를 조회함
    saveOrUpdate(user); // user테이블에 사용자 정보가 없다면 saveOrUpdate()메서드를 실행해 users 테이블에 회원 데이터를 추가합니다.
    return user;
  }

  //유저가 있으면 업데이트, 없으면 유저 생성
  private User_E saveOrUpdate(OAuth2User oAuth2User) {
    Map<String, Object> attributes = oAuth2User.getAttributes();
    String email = (String) attributes.get("email");
    String name = (String) attributes.get("name");
    User_E user_e = dynamoUserRepository.findByEmail(email);
    if(user_e != null){
      user_e.update(name);
      return dynamoUserRepository.update(user_e);
    }else{
      user_e = new User_E();
      user_e.setId(UUID.randomUUID().toString());
      user_e.setEmail(email);
      user_e.setNickname(name);
      return dynamoUserRepository.save(user_e);
    }
  }




}
