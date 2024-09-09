


var accessToken = localStorage.getItem('access_token');

var socket = new WebSocket(`ws://godding-elastic-env-2.eba-mygzmyet.ap-northeast-2.elasticbeanstalk.com/home/ws?token=${accessToken}`);

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

socket.onclose = function(event) {
    console.log("WebSocket connection closed, code: ", event.code, "reason: ", event.reason);
    console.log("Was the connection cleanly closed?: ", event.wasClean);
};

socket.onerror = function(error) {
    console.log("WebSocket error: ", error);
};

