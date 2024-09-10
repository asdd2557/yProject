package com.example.gameproject.controller;

import com.example.gameproject.entity.Connect_E;
import com.example.gameproject.entity.Game_E;
import com.example.gameproject.entity.User_E;
import com.example.gameproject.handler.WebSocketHandler;
import com.example.gameproject.service.BoardService;
import com.example.gameproject.service.ConnectService;
import com.example.gameproject.service.GameService;
import com.example.gameproject.service.UserDetailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequiredArgsConstructor
public class BoardController {
  private final ConnectService connectService;
  private final GameService gameService;
  private final UserDetailService userDetailService;
  private final BoardService boardService;
  private final WebSocketHandler webSocketHandler;
  private final ObjectMapper objectMapper;


  @GetMapping("/api/getBoardById")
  public Mono<String[][]> getBoardById(Principal principal) {
    Connect_E connectE = connectService.loadByEmail(principal.getName());
    return gameService.findById(connectE.getPosition()).map(data -> {
      return data.getTable();
    });
  }

  @GetMapping("/api/getPlayers")
  public Mono<Map<String, String>> getPlayers(Principal principal) {
    Connect_E connectE = connectService.loadByEmail(principal.getName());
    User_E userE = userDetailService.loadUserByUsername(principal.getName());
    Map<String, String> playerList = new HashMap<>();

    if (userE.getSubnickname() != null) {
      playerList.put("player1", userE.getSubnickname());
    } else {
      playerList.put("player1", principal.getName());
    }
    return gameService.findById(connectE.getPosition()).map(data -> {
      if (Objects.equals(data.getUser1(), principal.getName())) {
        User_E user_e = userDetailService.loadUserByUsername(data.getUser2());
        if (user_e.getSubnickname() != null) {
          playerList.put("player2", user_e.getSubnickname());

        } else {
          playerList.put("player2", data.getUser2());
        }
        return playerList;
      } else {
        User_E user_e = userDetailService.loadUserByUsername(data.getUser1());

        if (user_e.getSubnickname() != null) {
          playerList.put("player2", user_e.getSubnickname());
        } else {
          playerList.put("player2", data.getUser1());
        }

        return playerList;
      }
    });
  }

  @GetMapping("/api/getCheckLock")
  public Mono<Map<String, String>> getCheckLock(Principal principal) {
    Connect_E connectE = connectService.loadByEmail(principal.getName());
    Mono<Game_E> game_eMono = gameService.findById(connectE.getPosition());

    if (!Objects.equals(connectE.getConnect(), "3")) {
      return null;
    }

    return game_eMono.map(data -> {
      Map<String, String> map = new HashMap<>();
      if (Objects.equals(data.getUser1(), principal.getName())) {
        map.put("lockColor", "Black");
      } else {
        map.put("lockColor", "White");
      }
      return map;
    });
  }

  @PostMapping("/api/clickBoard")
  public Mono<Map<String, String>> clickBoard(@RequestBody String[] point, Principal principal) throws JsonProcessingException {
    Connect_E connectE = connectService.loadByEmail(principal.getName());
    Mono<Game_E> game_eMono = gameService.findById(connectE.getPosition());
    Map<String, String> map = new HashMap<>();
    return game_eMono.flatMap(gameE -> {
      Mono<String> userColor = boardService.getUserColor(Mono.just(gameE), principal); // User Color
      Mono<String> turn = Mono.just(gameE.getTurn());
      AtomicReference<String> user1SocketID = new AtomicReference<>();
      AtomicReference<String> user2SocketID = new AtomicReference<>();
      String[][] table = gameE.getTable();
      Map<String, Object> msg = new HashMap<>();
      msg.put("type", "ClickBoard");
      msg.put("point", point);

      // Setting user socket IDs
      user1SocketID.set(connectService.loadByEmail(gameE.getUser1()).getSocket());
      user2SocketID.set(connectService.loadByEmail(gameE.getUser2()).getSocket());


      // Combine the `turn` and `userColor` to determine the game state and result
      return Mono.zip(turn, userColor).flatMap(tuple -> {
        String turnData = tuple.getT1();
        String colorData = tuple.getT2();
        String returnColor;
        msg.put("color", colorData);
        if (colorData.equals("Black") && turnData.equals("0") && Objects.equals(table[Integer.parseInt(point[0])][Integer.parseInt(point[1])], "0")) {
          table[Integer.parseInt(point[0])][Integer.parseInt(point[1])] = "1";
          returnColor = "Black";
          map.put("lockColor", "Black");
          gameE.setTurn("1");
          board3x3Check(table, point, map, msg);
        } else if (colorData.equals("White") && turnData.equals("1") && Objects.equals(table[Integer.parseInt(point[0])][Integer.parseInt(point[1])], "0") || Objects.equals(table[Integer.parseInt(point[0])][Integer.parseInt(point[1])], "3")) {
          table[Integer.parseInt(point[0])][Integer.parseInt(point[1])] = "2";
          returnColor = "White";
          map.put("lockColor", "White");
          gameE.setTurn("0");
          board3x3Check(table, point, map, msg);
        } else {
          System.out.println("둘다 해당하지 않음: " + table[Integer.parseInt(point[0])][Integer.parseInt(point[1])]);
          returnColor = null;
        }

        if (returnColor != null) {
          if (returnColor.equals("Black"))
            boardService.boardTimerMove(gameE.getUser2(), gameE.getUser1());
          if (returnColor.equals("White"))
            boardService.boardTimerMove(gameE.getUser1(), gameE.getUser2()); //타이머

          gameE.setTable(table);
          gameE.getTableLog().add(table);

          return gameService.save(gameE).flatMap(savedGameE -> {
            String targetSocketId = colorData.equals("Black") ? user2SocketID.get() : user1SocketID.get();
            try {
              Map<String, String> victoryCount = countingLock(1, 1, 1, 1, Integer.parseInt(point[0]), Integer.parseInt(point[1]), 0, point, table, returnColor);
              for (String value : victoryCount.values()) {
                System.out.println("value: " + value);
                System.out.println("returnColor: " + returnColor);
                if (returnColor.equals("Black") && Objects.equals(value, "5")) {
                  boardService.boardTimerStop();
                  System.out.println("흑이 이김");
                  boardService.UserVictoryAddOrSocketSend(gameE.getUser1());
                  boardService.UserDefeatAddOrSocketSend(gameE.getUser2());

                  return Mono.empty();
                }
                if (returnColor.equals("White") && Integer.parseInt(value) >= 5) {
                  boardService.boardTimerStop();
                  System.out.println("백이 이김");
                  boardService.UserVictoryAddOrSocketSend(gameE.getUser2());
                  boardService.UserDefeatAddOrSocketSend(gameE.getUser1());
                  return Mono.empty();
                }
                webSocketHandler.sendMessageToTarget(objectMapper.writeValueAsString(msg), targetSocketId);
              }

            } catch (JsonProcessingException e) {
              return Mono.error(new RuntimeException(e));
            }
            return Mono.justOrEmpty(map);
          });
        } else {
          return Mono.empty();
        }
      });
    });
  }


  public Map<String, Object> checkBoardRule(String[][] table, String color) {
    Map<String, String> result = new HashMap<>();
    result.putAll(victoryWidth(table, color));
    return null; //임시
  }

  private boolean isValidMove(String[][] table, int row, int col, String colorData, String turnData) {
    if (row < 0 || col < 0 || row >= table.length || col >= table[row].length) {
      return false;
    }
    return (colorData.equals("Black") && turnData.equals("0") && "0".equals(table[row][col])) ||
        (colorData.equals("White") && turnData.equals("1") && "0".equals(table[row][col]));
  }

  private boolean isInBounds(String[][] table, int row, int col) {
    return row >= 0 && row < table.length && col >= 0 && col < table[row].length;
  }

  //빈칸 기준 붙어있는 돌 카운팅
  public Map<String, String> countingLock(int left, int leftDiagonal, int up, int upRightDiagonal, int row, int col, int wayCount, String[] point, String[][] table, String color) {
    switch (wayCount) {
      case 0:
        row--;
        if (isInBounds(table, row, col) && checkLockThere(table, row, col, color)) {
          left++;
          return countingLock(left, leftDiagonal, up, upRightDiagonal, row, col, wayCount, point, table, color);
        } else {
          return countingLock(left, leftDiagonal, up, upRightDiagonal, Integer.parseInt(point[0]), Integer.parseInt(point[1]), 4, point, table, color);
        }
      case 1:
        row--;
        col--;
        if (isInBounds(table, row, col) && checkLockThere(table, row, col, color)) {
          leftDiagonal++;
          return countingLock(left, leftDiagonal, up, upRightDiagonal, row, col, wayCount, point, table, color);
        } else {
          return countingLock(left, leftDiagonal, up, upRightDiagonal, Integer.parseInt(point[0]), Integer.parseInt(point[1]), 5, point, table, color);
        }
      case 2:
        col--;
        if (isInBounds(table, row, col) && checkLockThere(table, row, col, color)) {
          up++;
          return countingLock(left, leftDiagonal, up, upRightDiagonal, row, col, wayCount, point, table, color);
        } else {
          return countingLock(left, leftDiagonal, up, upRightDiagonal, Integer.parseInt(point[0]), Integer.parseInt(point[1]), 6, point, table, color);
        }
      case 3:
        col--;
        row++;
        if (isInBounds(table, row, col) && checkLockThere(table, row, col, color)) {
          upRightDiagonal++;
          return countingLock(left, leftDiagonal, up, upRightDiagonal, row, col, wayCount, point, table, color);
        } else {
          return countingLock(left, leftDiagonal, up, upRightDiagonal, Integer.parseInt(point[0]), Integer.parseInt(point[1]), 7, point, table, color);
        }
      case 4:
        row++;
        if (isInBounds(table, row, col) && checkLockThere(table, row, col, color)) {
          left++;
          return countingLock(left, leftDiagonal, up, upRightDiagonal, row, col, wayCount, point, table, color);
        } else {
          return countingLock(left, leftDiagonal, up, upRightDiagonal, Integer.parseInt(point[0]), Integer.parseInt(point[1]), 1, point, table, color);
        }
      case 5:
        row++;
        col++;
        if (isInBounds(table, row, col) && checkLockThere(table, row, col, color)) {
          leftDiagonal++;
          return countingLock(left, leftDiagonal, up, upRightDiagonal, row, col, wayCount, point, table, color);
        } else {
          return countingLock(left, leftDiagonal, up, upRightDiagonal, Integer.parseInt(point[0]), Integer.parseInt(point[1]), 2, point, table, color);
        }
      case 6:
        col++;
        if (isInBounds(table, row, col) && checkLockThere(table, row, col, color)) {
          up++;
          return countingLock(left, leftDiagonal, up, upRightDiagonal, row, col, wayCount, point, table, color);
        } else {
          return countingLock(left, leftDiagonal, up, upRightDiagonal, Integer.parseInt(point[0]), Integer.parseInt(point[1]), 3, point, table, color);
        }
      case 7:
        row--;
        col++;
        if (isInBounds(table, row, col) && checkLockThere(table, row, col, color)) {
          upRightDiagonal++;
          return countingLock(left, leftDiagonal, up, upRightDiagonal, row, col, wayCount, point, table, color);
        } else {
          Map<String, String> result = new HashMap<>();
          result.put("left", Integer.toString(left));
          result.put("leftDiagonal", Integer.toString(leftDiagonal));
          result.put("up", Integer.toString(up));
          result.put("upRightDiagonal", Integer.toString(upRightDiagonal));
          return result;
        }
    }
    return null;
  }

  //쌍삼 카운팅
  public Map<String, String> countingLock1(int countBean, int left, int leftDiagonal, int up, int upRightDiagonal, int row, int col, int wayCount, String[] point, String[][] table, String color) {
    switch (wayCount) {
      case 0:
        row--;
        if (isInBounds(table, row, col) && checkLockThere(table, row, col, color)) {
          left++;
          return countingLock1(countBean, left, leftDiagonal, up, upRightDiagonal, row, col, wayCount, point, table, color);
        } else if (isInBounds(table, row, col) && countBean < 1 && !checkLockThere(table, row, col, "White")) {
          countBean++;
          return countingLock1(countBean, left, leftDiagonal, up, upRightDiagonal, row, col, wayCount, point, table, color);
        } else if (isInBounds(table, row, col) && checkLockThere(table, row, col, "White")) {
          countBean = 0;
          left = 0;
          return countingLock1(countBean, left, leftDiagonal, up, upRightDiagonal, Integer.parseInt(point[0]), Integer.parseInt(point[1]), 1, point, table, color);
        } else {
          countBean = 0;
          return countingLock1(countBean, left, leftDiagonal, up, upRightDiagonal, Integer.parseInt(point[0]), Integer.parseInt(point[1]), 4, point, table, color);
        }

      case 1:
        row--;
        col--;
        if (isInBounds(table, row, col) && checkLockThere(table, row, col, color)) {
          leftDiagonal++;
          return countingLock1(countBean, left, leftDiagonal, up, upRightDiagonal, row, col, wayCount, point, table, color);
        } else if (isInBounds(table, row, col) && countBean < 1 && !checkLockThere(table, row, col, "White")) {
          countBean++;
          return countingLock1(countBean, left, leftDiagonal, up, upRightDiagonal, row, col, wayCount, point, table, color);
        } else if (isInBounds(table, row, col) && checkLockThere(table, row, col, "White")) {
          countBean = 0;
          leftDiagonal = 0;
          return countingLock1(countBean, left, leftDiagonal, up, upRightDiagonal, Integer.parseInt(point[0]), Integer.parseInt(point[1]), 2, point, table, color);
        } else {
          countBean = 0;
          return countingLock1(countBean, left, leftDiagonal, up, upRightDiagonal, Integer.parseInt(point[0]), Integer.parseInt(point[1]), 5, point, table, color);
        }
      case 2:
        col--;
        if (isInBounds(table, row, col) && checkLockThere(table, row, col, color)) {
          up++;
          return countingLock1(countBean, left, leftDiagonal, up, upRightDiagonal, row, col, wayCount, point, table, color);
        } else if (isInBounds(table, row, col) && countBean < 1 && !checkLockThere(table, row, col, "White")) {
          countBean++;
          return countingLock1(countBean, left, leftDiagonal, up, upRightDiagonal, row, col, wayCount, point, table, color);
        } else if (isInBounds(table, row, col) && checkLockThere(table, row, col, "White")) {
          countBean = 0;
          up = 0;
          return countingLock1(countBean, left, leftDiagonal, up, upRightDiagonal, Integer.parseInt(point[0]), Integer.parseInt(point[1]), 3, point, table, color);
        } else {
          countBean = 0;
          return countingLock1(countBean, left, leftDiagonal, up, upRightDiagonal, Integer.parseInt(point[0]), Integer.parseInt(point[1]), 6, point, table, color);
        }
      case 3:
        col--;
        row++;
        if (isInBounds(table, row, col) && checkLockThere(table, row, col, color)) {
          upRightDiagonal++;
          return countingLock1(countBean, left, leftDiagonal, up, upRightDiagonal, row, col, wayCount, point, table, color);
        } else if (isInBounds(table, row, col) && countBean < 1 && !checkLockThere(table, row, col, "White")) {
          countBean++;
          return countingLock1(countBean, left, leftDiagonal, up, upRightDiagonal, row, col, wayCount, point, table, color);
        } else if (isInBounds(table, row, col) && checkLockThere(table, row, col, "White")) {
          upRightDiagonal = 0;
          Map<String, String> result = new HashMap<>();
          result.put("left", Integer.toString(left));
          result.put("leftDiagonal", Integer.toString(leftDiagonal));
          result.put("up", Integer.toString(up));
          result.put("upRightDiagonal", Integer.toString(upRightDiagonal));
          return result;
        } else {
          countBean = 0;
          return countingLock1(countBean, left, leftDiagonal, up, upRightDiagonal, Integer.parseInt(point[0]), Integer.parseInt(point[1]), 7, point, table, color);
        }
      case 4:
        row++;
        if (isInBounds(table, row, col) && checkLockThere(table, row, col, color)) {
          left++;
          return countingLock1(countBean, left, leftDiagonal, up, upRightDiagonal, row, col, wayCount, point, table, color);
        } else if (isInBounds(table, row, col) && countBean < 1 && !checkLockThere(table, row, col, "White")) {
          countBean++;
          return countingLock1(countBean, left, leftDiagonal, up, upRightDiagonal, row, col, wayCount, point, table, color);
        } else if (isInBounds(table, row, col) && checkLockThere(table, row, col, "White")) {
          left = 0;
          countBean = 0;
          return countingLock1(countBean, left, leftDiagonal, up, upRightDiagonal, Integer.parseInt(point[0]), Integer.parseInt(point[1]), 1, point, table, color);
        } else {
          countBean = 0;
          return countingLock1(countBean, left, leftDiagonal, up, upRightDiagonal, Integer.parseInt(point[0]), Integer.parseInt(point[1]), 1, point, table, color);
        }
      case 5:
        row++;
        col++;
        if (isInBounds(table, row, col) && checkLockThere(table, row, col, color)) {
          leftDiagonal++;
          return countingLock1(countBean, left, leftDiagonal, up, upRightDiagonal, row, col, wayCount, point, table, color);
        } else if (isInBounds(table, row, col) && countBean < 1 && !checkLockThere(table, row, col, "White")) {
          countBean++;
          return countingLock1(countBean, left, leftDiagonal, up, upRightDiagonal, row, col, wayCount, point, table, color);
        } else if (isInBounds(table, row, col) && checkLockThere(table, row, col, "White")) {
          leftDiagonal = 0;
          countBean = 0;
          return countingLock1(countBean, left, leftDiagonal, up, upRightDiagonal, Integer.parseInt(point[0]), Integer.parseInt(point[1]), 2, point, table, color);
        } else {
          countBean = 0;
          return countingLock1(countBean, left, leftDiagonal, up, upRightDiagonal, Integer.parseInt(point[0]), Integer.parseInt(point[1]), 2, point, table, color);
        }
      case 6:
        col++;
        if (isInBounds(table, row, col) && checkLockThere(table, row, col, color)) {
          up++;
          return countingLock1(countBean, left, leftDiagonal, up, upRightDiagonal, row, col, wayCount, point, table, color);
        } else if (isInBounds(table, row, col) && countBean < 1 && !checkLockThere(table, row, col, "White")) {
          countBean++;
          return countingLock1(countBean, left, leftDiagonal, up, upRightDiagonal, row, col, wayCount, point, table, color);
        } else if (isInBounds(table, row, col) && checkLockThere(table, row, col, "White")) {
          up = 0;
          countBean = 0;
          return countingLock1(countBean, left, leftDiagonal, up, upRightDiagonal, Integer.parseInt(point[0]), Integer.parseInt(point[1]), 3, point, table, color);
        } else {
          countBean = 0;
          return countingLock1(countBean, left, leftDiagonal, up, upRightDiagonal, Integer.parseInt(point[0]), Integer.parseInt(point[1]), 3, point, table, color);
        }
      case 7:
        row--;
        col++;
        if (isInBounds(table, row, col) && checkLockThere(table, row, col, color)) {
          upRightDiagonal++;
          return countingLock1(countBean, left, leftDiagonal, up, upRightDiagonal, row, col, wayCount, point, table, color);
        } else if (isInBounds(table, row, col) && countBean < 1 && !checkLockThere(table, row, col, "White")) {
          countBean++;
          return countingLock1(countBean, left, leftDiagonal, up, upRightDiagonal, row, col, wayCount, point, table, color);
        } else if (isInBounds(table, row, col) && checkLockThere(table, row, col, "White")) {
          upRightDiagonal = 0;
          Map<String, String> result = new HashMap<>();
          result.put("left", Integer.toString(left));
          result.put("leftDiagonal", Integer.toString(leftDiagonal));
          result.put("up", Integer.toString(up));
          result.put("upRightDiagonal", Integer.toString(upRightDiagonal));
          return result;
        } else {
          Map<String, String> result = new HashMap<>();
          result.put("left", Integer.toString(left));
          result.put("leftDiagonal", Integer.toString(leftDiagonal));
          result.put("up", Integer.toString(up));
          result.put("upRightDiagonal", Integer.toString(upRightDiagonal));
          return result;
        }
    }
    return null;
  }

  //흰돌 쌍삼 삭제
  public void countingLock2(int key, int countBean, int left, int leftDiagonal, int up, int upRightDiagonal, int row, int col, int wayCount, String[] point, String[][] table, String color, Map<String, String> map, Map<String, Object> msg) {
    switch (wayCount) {
      case 0:
        System.out.println("countingLock2-0");
        row--;
        if (isInBounds(table, row, col) && checkLockThere(table, row, col, color)) {
          left++;
          countingLock2(key, countBean, left, leftDiagonal, up, upRightDiagonal, row, col, wayCount, point, table, color, map, msg);
        } else if (isInBounds(table, row, col) && countBean < 1 && left > 0 && !checkLockThere(table, row, col, "White") && !checkLockThere(table, row, col, "Tthree")) {
          System.out.println("00: " + row + col);
          countBean++;
          countingLock2(key, countBean, left, leftDiagonal, up, upRightDiagonal, row, col, wayCount, point, table, color, map, msg);
        } else if (isInBounds(table, row, col) && left > 0 && checkLockThere(table, row, col, "Tthree")) {
          System.out.println("0: " + row + col);
          board3x3Check(table, point, map, msg);
        } else {
          countingLock2(key, 0, 0, 0, 0, 0, Integer.parseInt(point[0]), Integer.parseInt(point[1]), 4, point, table, color, map, msg);
        }
        break;
      case 1:
        System.out.println("countingLock2-1");
        row--;
        col--;
        if (isInBounds(table, row, col) && checkLockThere(table, row, col, color)) {
          leftDiagonal++;
          countingLock2(key, countBean, left, leftDiagonal, up, upRightDiagonal, row, col, wayCount, point, table, color, map, msg);
        } else if (isInBounds(table, row, col) && countBean < 1 && leftDiagonal > 0 && !checkLockThere(table, row, col, "White") && !checkLockThere(table, row, col, "Tthree")) {
          System.out.println("11: " + row + col);
          countBean++;
          countingLock2(key, countBean, left, leftDiagonal, up, upRightDiagonal, row, col, wayCount, point, table, color, map, msg);
        } else if (isInBounds(table, row, col) && leftDiagonal > 0 && checkLockThere(table, row, col, "Tthree")) {
          System.out.println("1: " + row + col);
          board3x3Check(table, point, map, msg);
        } else {
          countingLock2(key, 0, 0, 0, 0, 0, Integer.parseInt(point[0]), Integer.parseInt(point[1]), 5, point, table, color, map, msg);
        }
        break;
      case 2:
        System.out.println("countingLock2-2");
        col--;
        if (isInBounds(table, row, col) && checkLockThere(table, row, col, color)) {
          System.out.println("22: " + row + col);
          up++;
          countingLock2(key, countBean, left, leftDiagonal, up, upRightDiagonal, row, col, wayCount, point, table, color, map, msg);
        } else if (isInBounds(table, row, col) && countBean < 1 && up > 0 && !checkLockThere(table, row, col, "White") && !checkLockThere(table, row, col, "Tthree")) {
          countBean++;
          countingLock2(key, countBean, left, leftDiagonal, up, upRightDiagonal, row, col, wayCount, point, table, color, map, msg);
        } else if (isInBounds(table, row, col) && up > 0 && checkLockThere(table, row, col, "Tthree")) {
          System.out.println("2: " + row + col);
          board3x3Check(table, point, map, msg);
        } else {
          countingLock2(key, 0, 0, 0, 0, 0, Integer.parseInt(point[0]), Integer.parseInt(point[1]), 6, point, table, color, map, msg);
        }
        break;
      case 3:
        System.out.println("countingLock2-3");
        col--;
        row++;
        if (isInBounds(table, row, col) && checkLockThere(table, row, col, color)) {
          upRightDiagonal++;
          countingLock2(key, countBean, left, leftDiagonal, up, upRightDiagonal, row, col, wayCount, point, table, color, map, msg);
        } else if (isInBounds(table, row, col) && countBean < 1 && upRightDiagonal > 0 && !checkLockThere(table, row, col, "White") && !checkLockThere(table, row, col, "Tthree")) {
          System.out.println("33: " + row + col);
          countBean++;
          countingLock2(key, countBean, left, leftDiagonal, up, upRightDiagonal, row, col, wayCount, point, table, color, map, msg);
        } else if (isInBounds(table, row, col) && upRightDiagonal > 0 && checkLockThere(table, row, col, "Tthree")) {
          System.out.println("3: " + row + col);
          board3x3Check(table, point, map, msg);
        } else {
          countingLock2(key, 0, 0, 0, 0, 0, Integer.parseInt(point[0]), Integer.parseInt(point[1]), 7, point, table, color, map, msg);
        }
        break;
      case 4:
        System.out.println("countingLock2-4");
        row++;
        if (isInBounds(table, row, col) && checkLockThere(table, row, col, color)) {
          left++;
          countingLock2(key, countBean, left, leftDiagonal, up, upRightDiagonal, row, col, wayCount, point, table, color, map, msg);
        } else if (isInBounds(table, row, col) && countBean < 1 && left > 0 && !checkLockThere(table, row, col, "White") && !checkLockThere(table, row, col, "Tthree")) {
          System.out.println("44: " + row + col);
          countBean++;
          countingLock2(key, countBean, left, leftDiagonal, up, upRightDiagonal, row, col, wayCount, point, table, color, map, msg);
        } else if (isInBounds(table, row, col) && left > 0 && checkLockThere(table, row, col, "Tthree")) {
          System.out.println("4: " + row + col);
          board3x3Check(table, point, map, msg);
        } else {
          countingLock2(key, 0, 0, 0, 0, 0, Integer.parseInt(point[0]), Integer.parseInt(point[1]), 1, point, table, color, map, msg);
        }
        break;
      case 5:
        System.out.println("countingLock2-5");
        row++;
        col++;
        if (isInBounds(table, row, col) && checkLockThere(table, row, col, color)) {
          leftDiagonal++;
          countingLock2(key, countBean, left, leftDiagonal, up, upRightDiagonal, row, col, wayCount, point, table, color, map, msg);
        } else if (isInBounds(table, row, col) && countBean < 1 && leftDiagonal > 0 && !checkLockThere(table, row, col, "White") && !checkLockThere(table, row, col, "Tthree")) {
          System.out.println("55: " + row + col);
          countBean++;
          countingLock2(key, countBean, left, leftDiagonal, up, upRightDiagonal, row, col, wayCount, point, table, color, map, msg);
        } else if (isInBounds(table, row, col) && leftDiagonal > 0 && checkLockThere(table, row, col, "Tthree")) {
          System.out.println("5: " + row + col);
          board3x3Check(table, point, map, msg);
        } else {
          countingLock2(key, 0, 0, 0, 0, 0, Integer.parseInt(point[0]), Integer.parseInt(point[1]), 2, point, table, color, map, msg);
        }
        break;
      case 6:
        System.out.println("countingLock2-6");
        col++;
        if (isInBounds(table, row, col) && checkLockThere(table, row, col, color)) {
          up++;
          countingLock2(key, countBean, left, leftDiagonal, up, upRightDiagonal, row, col, wayCount, point, table, color, map, msg);
        } else if (isInBounds(table, row, col) && countBean < 1 && up > 0 && !checkLockThere(table, row, col, "White") && !checkLockThere(table, row, col, "Tthree")) {
          System.out.println("66: " + row + col);
          countBean++;
          countingLock2(key, countBean, left, leftDiagonal, up, upRightDiagonal, row, col, wayCount, point, table, color, map, msg);
        } else if (isInBounds(table, row, col) && up > 0 && checkLockThere(table, row, col, "Tthree")) {
          System.out.println("6: " + row + col);
          board3x3Check(table, point, map, msg);
        } else {
          countingLock2(key, 0, 0, 0, 0, 0, Integer.parseInt(point[0]), Integer.parseInt(point[1]), 3, point, table, color, map, msg);
        }
        break;
      case 7:
        System.out.println("countingLock2-7");
        row--;
        col++;
        if (isInBounds(table, row, col) && checkLockThere(table, row, col, color)) {
          upRightDiagonal++;
          countingLock2(key, countBean, left, leftDiagonal, up, upRightDiagonal, row, col, wayCount, point, table, color, map, msg);
        } else if (isInBounds(table, row, col) && countBean < 1 && upRightDiagonal > 0 && !checkLockThere(table, row, col, "White") && !checkLockThere(table, row, col, "Tthree")) {
          System.out.println("77: " + row + col);
          countBean++;
          countingLock2(key, countBean, left, leftDiagonal, up, upRightDiagonal, row, col, 7, point, table, color, map, msg);
        } else if (isInBounds(table, row, col) && upRightDiagonal > 0 && checkLockThere(table, row, col, "Tthree")) {
          System.out.println("7: " + row + col);
          board3x3Check(table, point, map, msg);
        }
        break;
    }
  }

  //쌍삼 가지
  public void board3x3Check(String[][] table, String[] point, Map<String, String> map, Map<String, Object> msg) {
    int point1 = Integer.parseInt(point[0]);
    int point2 = Integer.parseInt(point[1]);
    int rowM = Math.max(point1 - 6, 0);
    int rowP = Math.min(point1 + 6, table.length - 1);
    int colM = Math.max(point2 - 6, 0);
    int colP = Math.min(point2 + 6, table[0].length - 1);
    int key = 0;
    int key1 = 0;
    for (int row = rowM; row <= rowP; row++) {
      for (int col = colM; col <= colP; col++) {
        if ("0".equals(table[row][col]) || "3".equals(table[row][col])) {
          int x33Count = 0;
          boolean x66Count = false;
          Map<String, String> lockCount_2S = countingLock1(0, 0, 0, 0, 0, row, col, 0, new String[]{Integer.toString(row), Integer.toString(col)}, table, "Black");
          Map<String, String> lockCount_6S = countingLock(1,1,1,1,row, col,0,new String[]{Integer.toString(row), Integer.toString(col)},table,"Black");

          if ("2".equals(lockCount_2S.get("left"))) {
            System.out.println("left2");
            x33Count++;
          }
          if ("2".equals(lockCount_2S.get("leftDiagonal"))) {
            System.out.println("leftDiagonal2");
            x33Count++;
          }
          if ("2".equals(lockCount_2S.get("up"))) {
            System.out.println("up2");
            x33Count++;
          }
          if ("2".equals(lockCount_2S.get("upRightDiagonal"))) {
            System.out.println("upRightDiagonal2");
            x33Count++;
          }

          if (Integer.parseInt(lockCount_6S.get("left")) >= 6 ||
              Integer.parseInt(lockCount_6S.get("leftDiagonal")) >=6 ||
              Integer.parseInt(lockCount_6S.get("up")) >= 6 ||
              Integer.parseInt(lockCount_6S.get("upRightDiagonal")) >= 6) {

            map.put("TthreeRow" + key, Integer.toString(row));
            map.put("TthreeCol" + key, Integer.toString(col));
            msg.put("TthreeRow" + key, Integer.toString(row));
            msg.put("TthreeCol" + key, Integer.toString(col));
            x66Count = true;
          }



          if (x33Count >= 2) {
            table[row][col] = "3";
            map.put("TthreeRow" + key, Integer.toString(row));
            map.put("TthreeCol" + key, Integer.toString(col));
            msg.put("TthreeRow" + key, Integer.toString(row));
            msg.put("TthreeCol" + key, Integer.toString(col));
            key++;
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@33입니다.");
          } else if ("3".equals(table[row][col]) && !x66Count) {
            table[row][col] = "0";
            map.put("DTthreeRow" + key1, Integer.toString(row));
            map.put("DTthreeCol" + key1, Integer.toString(col));
            msg.put("DTthreeRow" + key1, Integer.toString(row));
            msg.put("DTthreeCol" + key1, Integer.toString(col));
            key1++;
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@33이 아니게 됐습니ㅏㄷ.");
          }
        } else if ("1".equals(table[row][col])) {
        }
      }
    }
  }

  public void board6Check(String[][] table, String[] point, Map<String, String> map) {

  }

  public Map<String, String> victoryMsg(int victoryCount, String color) {
    if (victoryCount == 5) {
      Map<String, String> result_M = new HashMap<>();
      System.out.println("이겼따~~!~!");
      result_M.put("Victory", color);
      return result_M;
    }
    return null;
  }

  public boolean checkLockThere(String[][] table, int row, int col, String color) {
    String lock_s = table[row][col];
    String switchColor_s = "Black".equals(color) ? "1" : "White".equals(color) ? "2" : "Tthree".equals(color) ? "3" : "Unknown";
    return switchColor_s.equals(lock_s);
  }


  public Map<String, String> victoryWidth(String[][] table, String color) {
    int victoryCount = 0;
    Map<String, String> result = new HashMap<>();
    if (Objects.equals(color, "Black")) {
      for (int i = 0; i < table.length; i++) {
        for (int ii = 0; ii < table.length; ii++) {
          if (Objects.equals(table[i][ii], "1")) {
            victoryCount = victoryCount + 1;
            if (victoryCount == 5) {
              result.put("Victory", "Black");
              return result;
            }
          } else {
            victoryCount = 0;
          }
        }
      }
    }
    //===========
    if (Objects.equals(color, "White")) {
      for (int i = 0; i < table.length; i++) {
        for (int ii = 0; ii < table.length; ii++) {
          if (Objects.equals(table[i][ii], "2")) {
            victoryCount = victoryCount + 1;
            if (victoryCount == 5) {
              result.put("Victory", "White");
              return result;
            }
          } else {
            victoryCount = 0;
          }
        }
      }
    }
    return null;
  }
}




