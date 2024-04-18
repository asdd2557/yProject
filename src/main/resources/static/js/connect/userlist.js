    var socket = new WebSocket("ws://godding-elastic-env-2.eba-mygzmyet.ap-northeast-2.elasticbeanstalk.com/home/ws");

    socket.onopen = function() {
        console.log("WebSocket connection established");
    };

    socket.onmessage = function(event) {
        console.log("Message received: ", event.data); // 로그 추가
        var jsonData = JSON.parse(event.data);
        var dataDiv = document.getElementById("data");
        renderUsers(jsonData);
        document.dispatchEvent(new CustomEvent('socketMessage', { detail: jsonData }));
    };

    socket.onclose = function(event) {
        console.log("WebSocket connection closed, code: ", event.code, "reason: ", event.reason);
        console.log("Was the connection cleanly closed?: ", event.wasClean);
    };

    socket.onerror = function(error) {
        console.log("WebSocket error: ", error);
    };







    // 사용자 목록을 HTML에 표시하는 함수
    function renderUsers(userList) {
    console.log("rederUser in: " + userList)
        var accessList = document.querySelector('.access-list');
        var userElements = document.querySelectorAll('.user');
        userElements.forEach(function(element) {
            element.remove();
        });
        userList.forEach(function(user) {
        console.log("user Log")
        console.log(user.connect);
            var userDiv = document.createElement('div');
            userDiv.classList.add('user');

            var userState = '';
            switch (user.connect) {
                case "1":
                    userState = '접속중';
                    break;
                case "2":
                    userState = '매칭 중';
                    break;
                case "3":
                    userState = '게임 중';
                    break;
                default:
                    userState = '알 수 없음';
            }

            var nickname = user.nickname;

            var userContent = `
                <div class="user-info">
                    <h2 class="nickname">${nickname}</h2>
                    <p class="user-state">${userState}</p>
                </div>
            `;

            userDiv.innerHTML = userContent;
            accessList.appendChild(userDiv);
        });
    }