const eventSource1 = new EventSource("http://localhost:8080/connectview")
eventSource1.onmessage=(event)=> {

const data = JSON.parse(event.data);

fetchUserList();
}


document.addEventListener("DOMContentLoaded", function() {


    // 페이지 로드 시 사용자 목록을 가져와서 표시
    fetchUserList();
     setInterval(fetchUserList, 8000); //
});

   // 서버로부터 사용자 목록을 가져오는 함수
    function fetchUserList() {
        var xhr = new XMLHttpRequest();

        // GET 방식으로 '/userlist' 엔드포인트에 요청
        xhr.open('GET', '/userlist', true);

        // 요청이 완료되었을 때 실행되는 콜백 함수
        xhr.onload = function() {
            if (xhr.status >= 200 && xhr.status < 400) {
                // 서버에서 전달받은 JSON 데이터를 파싱
                var userList = JSON.parse(xhr.responseText);

                // 사용자 목록을 HTML에 추가
                renderUsers(userList);
            } else {
                console.error('Failed to fetch user list');
            }
        };

        // 요청 전송
        xhr.send();
    }

    // 사용자 목록을 HTML에 표시하는 함수
    function renderUsers(userList) {
        var accessList = document.querySelector('.access-list');
        var userElements = document.querySelectorAll('.user');
        userElements.forEach(function(element) {
            element.remove();
        });
        userList.forEach(function(user) {
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