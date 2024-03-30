package com.example.gameproject.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Enum_E { // 스프링 시큐리티에서는 권한 코드에 항상 ROLE_이 앞에 있어야만 한다.
  GUEST("ROLE_GUEST", "GUEST"),
  USER("ROLE_USER", "USER");

  private final String key;
  private final String string;
}