package com.example.gameproject.config.oauth;

import com.example.gameproject.dto.User_A;
import com.example.gameproject.entity.Connect_E;
import com.example.gameproject.entity.User_E;
import com.example.gameproject.repository.DynamoConnectRepository;
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
public class OAuth2UserCustomService extends DefaultOAuth2UserService { //google에 회원가입을 완료한 후 후처리
  private final DynamoUserRepository dynamoUserRepository;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException { //이넘이 실행됨!!
    // 요청을 바탕으로 유저 정보를 담은 객체 반환
    //userRequest는 사용자 이메일
    //System.out.println(userRequest.getAccessToken()); //엑세스 토큰
    //System.out.println(userRequest.getClientRegistration()); //구글 id,password
    //System.out.println(super.loadUser(userRequest).getAttributes());//이 정보만 필요함 id number, 이름 , 사진, email
    //구글 로그인 버튼 클릭 -> 구글로그인창 -> 로그인을 완료 -> code를 리턴(Oauth-Client라이브러리가) -> AccessToken요청
    //userRequest받음 -> 회원 프로필 받아야함(loadUser함수) -> 회원 프로필 정보 받음 구글로 부터
    System.out.println("loadUser!!");
    OAuth2User user = super.loadUser(userRequest); //loadUser를 통해 사용자 정보를 조회함
    System.out.println("user:"+user);
    saveOrUpdate(user); // user테이블에 사용자 정보가 없다면 saveOrUpdate()메서드를 실행해 users 테이블에 회원 데이터를 추가합니다.
    return user;
  }

  //유저가 있으면 업데이트, 없으면 유저 생성
  private User_E saveOrUpdate(OAuth2User oAuth2User) {
    Map<String, Object> attributes = oAuth2User.getAttributes();
    String email = (String) attributes.get("email");
    String name = (String) attributes.get("name");
    String profile_url = (String) attributes.get("picture");
    User_E user_e = dynamoUserRepository.findByEmail(email);

    if(user_e != null){
      user_e.update(name, profile_url);
      return dynamoUserRepository.update(user_e);
    }else{
      user_e = new User_E();
      user_e.setId(attributes.get("sub").toString());
      user_e.setDefeat(0);
      user_e.setVictory(0);
      user_e.setProfile_url(profile_url);
      user_e.setEmail(email);
      user_e.setNickname(name);
      return dynamoUserRepository.save(user_e);
    }
  }




}
