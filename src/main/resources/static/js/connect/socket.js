


var accessToken = localStorage.getItem('access_token');

var socket = new WebSocket(`ws://godding-elastic-env-2.eba-mygzmyet.ap-northeast-2.elasticbeanstalk.com/ws?token=${accessToken}`);

var accessToken = localStorage.getItem('access_token');
console.log("Access Token:", accessToken);

var sessionId = ""; // 세션 ID 변수 초기화

socket.onopen = function(event) {
    accessToken = localStorage.getItem('access_token');
    console.log("WebSocket connection established");
    sessionId = event.currentTarget.url.split("?")[1].split("=")[1];
    console.log("세션 ID:", sessionId);
};

socket.onmessage = function(event) {
    console.log("Message received: ", event.data); // 로그 추가
    var jsonData = JSON.parse(event.data);
    var dataDiv = document.getElementById("data");
    console.log(jsonData);

    if (Array.isArray(jsonData) && jsonData.length > 0) {
        if (jsonData[0].type === "userList") { // 조건 수정: jsonData[0].type == "userList" -> jsonData[0].type === "userList"
            matchingfind();
            console.log("Socket userList: "+ jsonData);
            renderUsers(jsonData);

            return;
        }
    }

    if (jsonData.type === "msg") {
        msgReceive(jsonData);
        return;
    }
    if(jsonData.type === "ClickBoard") {
    boardSocketReceive(jsonData);
                    for(let i=0; i < 8; i++){
                            if (jsonData.hasOwnProperty('TthreeRow'+i)) {
                              placeTthree(jsonData['TthreeRow' + i], jsonData['TthreeCol' + i]);
                            }
                            if(jsonData.hasOwnProperty('DTthreeRow'+i)){
                                DTthree(jsonData['DTthreeRow' + i], jsonData['DTthreeCol' + i]);
                            }
                    }
    }

     if (jsonData.type === "deFeat") {
            alert("패배하셨습니다. ㅋㅋ");
            hideLoader();
            hideBoardContainer();
            showMatchingContainer();
             initBoard();
        }

        if(jsonData.type === "vicTory"){
        alert("승리하셨습니다. ㅋㅋ");
        hideLoader();
                    hideBoardContainer();
                    showMatchingContainer();
                     initBoard();

        }


    document.dispatchEvent(new CustomEvent('socketMessage', { detail: jsonData }));
};


// WebSocket 연결이 닫혔을 때 즉시 경고창 띄우기
socket.onclose = function(event) {
    console.log("WebSocket connection closed, code: ", event.code, "reason: ", event.reason);
    console.log("Was the connection cleanly closed?: ", event.wasClean);

    isWebSocketClosed = true;  // WebSocket이 닫혔음을 기록
    handleWebSocketCloseOrError();  // WebSocket이 닫혔을 때 처리
};

// WebSocket 에러 발생 시 즉시 경고창 띄우기
socket.onerror = function(error) {
    console.log("WebSocket error: ", error);

    isWebSocketError = true;  // WebSocket에서 에러가 발생했음을 기록
    handleWebSocketCloseOrError();  // WebSocket 에러 발생 시 처리
};

// WebSocket이 닫히거나 에러가 발생했을 때 경고창 처리
function handleWebSocketCloseOrError() {
    if (document.visibilityState === 'visible' && document.hasFocus()) {
        // 페이지가 활성 상태이며 포커스를 얻고 있으면 즉시 경고창을 띄움
        showAlert();
    } else {
        // 페이지가 비활성화되었거나 포커스를 잃은 경우, 포커스를 다시 얻거나 활성화되었을 때 경고창을 띄우도록 설정
        document.addEventListener("visibilitychange", handleVisibilityAndFocus);
        window.addEventListener("focus", handleVisibilityAndFocus);
    }
}

// 경고창을 띄우고 이벤트 리스너 제거
function handleVisibilityAndFocus() {
    if (document.visibilityState === 'visible' && document.hasFocus()) {
        showAlert();  // 경고창 띄우기
        // 이벤트 리스너 제거
        document.removeEventListener("visibilitychange", handleVisibilityAndFocus);
        window.removeEventListener("focus", handleVisibilityAndFocus);
    }
}

// 경고창 표시 함수
function showAlert() {
    let message = "";
    if (isWebSocketClosed) {
        message = "세션이 만료되었습니다. 로그인 페이지로 이동하시겠습니까?";
    } else if (isWebSocketError) {
        message = "WebSocket 에러가 발생했습니다. 로그인 페이지로 이동하시겠습니까?";
    }

    // 경고창을 띄우고, 사용자가 확인을 누르면 로그인 페이지로 이동
    if (window.confirm(message)) {
        window.location.href = "/login";  // 로그인 페이지로 이동
    }

    // 상태 초기화
    isWebSocketClosed = false;
    isWebSocketError = false;
}

// 페이지의 가시성 상태가 변경될 때 실행되는 부분
document.addEventListener("visibilitychange", function() {
    if (document.visibilityState === 'visible' && (isWebSocketClosed || isWebSocketError)) {
        showAlert();  // 경고창 띄우기
    }
});

// 창에 포커스가 맞춰질 때 실행되는 부분
window.addEventListener("focus", function() {
    if (isWebSocketClosed || isWebSocketError) {
        showAlert();  // 경고창 띄우기
    }
});


/*
socket.onclose = function(event) {
    console.log("WebSocket connection closed, code: ", event.code, "reason: ", event.reason);
    console.log("Was the connection cleanly closed?: ", event.wasClean);
        if (window.confirm("세션이 만료되었습니다. 로그인 페이지로 이동하시겠습니까?")) {
            // 사용자가 확인을 누르면 로그인 페이지로 이동
            window.location.href = "/login"; // 로그인 페이지 URL로 이동
        }
};

socket.onerror = function(error) {
    console.log("WebSocket error: ", error);
        if (window.confirm("세션이 만료되었습니다. 로그인 페이지로 이동하시겠습니까?")) {
            // 사용자가 확인을 누르면 로그인 페이지로 이동
            window.location.href = "/login"; // 로그인 페이지 URL로 이동
        }
};
*/
