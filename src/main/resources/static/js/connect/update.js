// 페이지 진입 시 실행할 함수

document.addEventListener('DOMContentLoaded', function() {
    onPageEnter();
});

function onPageEnter() {
    // 현재 URL 가져오기
    const currentURL = window.location.href;
    const pattern = /^http:\/\/localhost:8080\/.*/;
    // URL 패턴 확인
  if (pattern.test(currentURL)) {
   body = JSON.stringify({
              connect: "1"
          })
         function success() {
         // 채팅 내용을 표시할 div 요소 선택
         const chatContentDiv = document.getElementById("chatContent");

         // 새로운 메시지를 생성하고 텍스트 내용 설정
         const newMessageElement = document.createElement("p");
         newMessageElement.textContent = "admin: 좋은 하루 입니다.";

         // 새로운 메시지를 채팅 영역에 추가
         chatContentDiv.appendChild(newMessageElement);
        };
                 function fail() {
                 // 채팅 내용을 표시할 div 요소 선택
                 const chatContentDiv = document.getElementById("chatContent");

                 // 새로운 메시지를 생성하고 텍스트 내용 설정
                 const newMessageElement = document.createElement("p");
                 newMessageElement.textContent = "admin: login이 필요합니다.";

                 // 새로운 메시지를 채팅 영역에 추가
                 chatContentDiv.appendChild(newMessageElement);

                };
httpRequest3('PUT','/api/connectupdateorsave',body, success, fail);
    }
}

// 페이지 떠날 때 실행할 함수
function onPageLeave() {
   alert("나갔다.");
   body = JSON.stringify({
              connect: "0"
          })
                   function success() {
                          alert("나갔다.");
                          console.log("안녕히가세요.");
                          }
                          function fail(){
                          console.log("Error");
                          }
    // 페이지를 떠날 때 실행할 액션 추가
    console.log("페이지를 떠났습니다!");
    httpRequest3('PUT','/api/connectupdateorsave',body, success, fail);
    // 원하는 액션을 실행합니다.
}

var unloadEventFired = false;
// 페이지를 떠날 때 onPageLeave 함수 호출
window.addEventListener('beforeunload', function(event) {
    if (!unloadEventFired) {
        // 페이지를 떠나기 전에 실행할 작업 수행
        onPageLeave();

        // 페이지를 떠나기 전에 실행했음을 플래그로 표시
        unloadEventFired = true;
    }
});






//====================================================================================


// 쿠키를 가져오는 함수
function getCookie(key) {
    var result = null;
    var cookie = document.cookie.split(';');
    cookie.some(function (item) {
        item = item.replace(' ', '');

        var dic = item.split('=');

        if (key === dic[0]) {
            result = dic[1];
            return true;
        }
    });

    return result;
}

// HTTP 요청을 보내는 함수
function httpRequest3(method, url, body, success, fail) {
    fetch(url, {
        method: method,
        headers: { // 로컬 스토리지에서 액세스 토큰 값을 가져와 헤더에 추가
            Authorization: 'Bearer ' + localStorage.getItem('access_token'),
            'Content-Type': 'application/json',
        },
        body: body,
    }).then(response => {
        if (response.status === 200 || response.status === 201) {
            return success();
        }
        const refresh_token = getCookie('refresh_token');
        if (response.status === 401 && refresh_token) {
            fetch('/api/token', {
                method: 'POST',
                headers: {
                    Authorization: 'Bearer ' + localStorage.getItem('access_token'),
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    refreshToken: getCookie('refresh_token'),
                }),
            })
                .then(res => {
                    if (res.ok) {
                        return res.json();
                    }
                })
                .then(result => { // 재발급이 성공하면 로컬 스토리지값을 새로운 액세스 토큰으로 교체
                    localStorage.setItem('access_token', result.accessToken);
                    httpRequest3(method, url, body, success);
                })
                .catch(error => fail());
        } else {
            return fail();
        }
    });
}