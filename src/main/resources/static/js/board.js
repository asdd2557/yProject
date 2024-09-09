

   let currentPlayer;
    let previewStone = null; // 오목알 미리보기 요소를 저장할 변수
    //타임루프 바
const timeLimit = 15000; // 15초
let timeLeft = timeLimit;
let timer;
let progressBar;

document.addEventListener('DOMContentLoaded', function() {
 progressBar = document.getElementById('progress-bar');

    // 보드 요소 선택
    const board = document.querySelector('.board');
    let previewStone = null; // 오목알 미리보기 요소를 저장할 변수

    // 오목판 생성
    for (let i = 0; i < 19; i++) {
        for (let j = 0; j < 19; j++) {
            const intersection = document.createElement('div');
            intersection.classList.add('intersection');
            intersection.dataset.row = i;
            intersection.dataset.col = j;
            board.appendChild(intersection);

            // 마우스 진입 이벤트 (교차점에 마우스를 올렸을 때)
            intersection.addEventListener('mouseenter', function() {
                const row = parseInt(this.dataset.row, 10);
                const col = parseInt(this.dataset.col, 10);

                // 이미 돌이 놓여있지 않은 경우에만 반투명한 오목알 표시
                if (!isStonePlaced(row, col)) {
                    previewStone = createPreviewStone(currentPlayer);
                    intersection.appendChild(previewStone);
                }
            });

            // 마우스 이탈 이벤트 (교차점에서 마우스를 떼었을 때)
            intersection.addEventListener('mouseleave', function() {
                // 오목알 미리보기 요소 제거
                if (previewStone) {
                    previewStone.remove();
                    previewStone = null; // 미리보기 요소 초기화
                }
            });

            // 클릭 이벤트 추가 (교차점을 클릭했을 때)
            intersection.addEventListener('click', function() {
                const row = parseInt(intersection.dataset.row, 10);
                const col = parseInt(intersection.dataset.col, 10);

                function success(data) {
                console.log(data);
                for(let i=0; i < 8; i++){
                        if (data.hasOwnProperty('TthreeRow'+i)) {
                          placeTthree(data['TthreeRow' + i], data['TthreeCol' + i]);
                        }
                        if(data.hasOwnProperty('DTthreeRow'+i)){
                            DTthree(data['DTthreeRow' + i], data['DTthreeCol' + i]);
                        }

                }


                //=============================================
                    if (data.lockColor === "Black") {
                        currentPlayer = "Black";
                        // 이미 돌이 놓여있지 않은 경우에만 돌을 놓기
                        if (!isStonePlaced(row, col)) {
                            placeStone(row, col, currentPlayer);
                            initializeOrResetTimer();
                            // 클릭 후 오목알 미리보기 요소 제거
                            if (previewStone) {
                                previewStone.remove();
                                previewStone = null; // 미리보기 요소 초기화
                            }
                        }
                    } else if (data.lockColor === "White") {
                        currentPlayer = "White";
                        // 이미 돌이 놓여있지 않은 경우에만 돌을 놓기
                        if (!isStonePlaced(row, col)) {
                            placeStone(row, col, currentPlayer);
                            initializeOrResetTimer();
                            // 클릭 후 오목알 미리보기 요소 제거
                            if (previewStone) {
                                previewStone.remove();
                                previewStone = null; // 미리보기 요소 초기화
                            }
                        }
                    }
                }

                function fail() {
                    console.log("Failed to place stone.");
                }


                const point = [row.toString(), col.toString()]; // String[][] 형태로 변환
                const body = JSON.stringify(point);
                httpRequestBoardClick("POST", "/api/clickBoard", body, success, fail);
            });
        }
    }
   });

    function checkUserConnect(event) {
        console.log("checkUserConnect");
        const body = JSON.stringify({ connect: "3" }); // 필요 없음

        function success(response) {
            event.forEach(function(user) {
                if (user.connect === "3" && user.email === response.email) {
                    return true;
                } else if (user.connect !== "3" && user.email === response.email) {
                    return false;
                }
            });
        }

        function fail() {}

        httpRequestStandbyGET("GET", "/api/getemail", body, success, fail);
         console.log("checkUserConnect exit");
    }

    // 돌이 이미 놓여있는지 확인하는 함수
    function isStonePlaced(row, col) {
        const targetIntersection = document.querySelector(`.intersection[data-row="${row}"][data-col="${col}"]`);
        return targetIntersection.querySelector('.piece') !== null; // 돌이 존재하면 true 반환
    }

    // 반투명한 오목알 미리보기 요소 생성 함수
    function createPreviewStone(color) {
        const previewPiece = document.createElement('div');
        previewPiece.classList.add('preview-piece'); // 미리보기 요소 클래스 추가
        previewPiece.style.backgroundColor = color; // 플레이어의 돌 색상 설정
        return previewPiece;
    }

    // 돌을 놓는 함수
    function placeStone(row, col, color) {
        const piece = document.createElement('div');
        piece.classList.add('piece');
        piece.style.backgroundColor = color; // 플레이어의 돌 색상 설정
        const targetIntersection = document.querySelector(`.intersection[data-row="${row}"][data-col="${col}"]`);

        // 교차점의 중앙에 돌을 추가
        targetIntersection.innerHTML = '';
        targetIntersection.appendChild(piece);
    }

function placeTthree(row, col) {

    const pairThreeIcon = document.createElement('div');
    pairThreeIcon.className = 'pair-three-icon';
    const crossLine1 = document.createElement('div');
    crossLine1.className = 'cross-line';
    const crossLine2 = document.createElement('div');
    crossLine2.className = 'cross-line cross-line-rotate';
    pairThreeIcon.appendChild(crossLine1);
    pairThreeIcon.appendChild(crossLine2);
    document.body.appendChild(pairThreeIcon);

     const targetIntersection = document.querySelector(`.intersection[data-row="${row}"][data-col="${col}"]`);
             // 교차점의 중앙에 돌을 추가
             targetIntersection.innerHTML = '';
             targetIntersection.appendChild(pairThreeIcon);
}

function DTthree(row, col) {
    // 지정된 row와 col을 가진 교차점을 찾습니다.
    const targetIntersection = document.querySelector(`.intersection[data-row="${row}"][data-col="${col}"]`);

    if (targetIntersection) {
        // 교차점 내에 있는 .pair-three-icon 요소를 찾습니다.
        const pairThreeIcon = targetIntersection.querySelector('.pair-three-icon');

        if (pairThreeIcon) {
            // 교차점에서 해당 아이콘을 제거합니다.
            targetIntersection.removeChild(pairThreeIcon);
        }
    }
}


    function userStoneCheck() {
            console.log("userSonteChoeck");

                    function userStoneCheckSuccess(data) {
                        console.log("userStoneCheck: ", data.lockColor)
                        currentPlayer = data.lockColor;
                    }

                    function fail() {console.log("userStoneCheck fail;")}

                    httpRequestBoard("GET", "/api/getCheckLock", userStoneCheckSuccess, fail);




 console.log("userSonteChoeck exit");
    }


    function boardSocketReceive(stringData){
              let point = stringData.point;
                        if (!isStonePlaced(point[0], point[1])) {
                            placeStone(point[0], point[1], stringData.color);
initializeOrResetTimer();
                        }
    }
    function playerNameUpdate(){
            function success(data){
            console.log(data);
                console.log("Type of data:", typeof data);

            document.getElementById("player1-nickname").textContent = data.player1;
            document.getElementById("player2-nickname").textContent = data.player2;
            }
          function  fail(){
            }
               httpRequestBoard("GET", "/api/getPlayers", success, fail);
    }

//보드 새로고침
function refreshBoard(){
function success(data){
console.log(data);
let thisColor = "";
for(let i = 0; i < data.length; i++){
  for (let j = 0; j < data[i].length; j++) {

        if(data[i][j] == "1"){
        placeStone(i, j, "Black");
        }
        if(data[i][j] == "2"){

        placeStone(i, j, "White");
        }
        if(data[i][j] == "3"){
        placeTthree(i,j);
        }
  }
  }
}


function fail(){
console.log("refreshBoard Fail");
}

 httpRequestBoard("GET", "/api/getBoardById", success, fail);

}

function initBoard(){
const elements = document.querySelectorAll('.piece');
elements.forEach(element => {
    element.remove(); // 각 요소 삭제
    });

        // 모든 .pair-three-icon 요소를 선택
        const allIcons = document.querySelectorAll('.pair-three-icon');

        // 각각의 요소를 순회하며 DOM에서 제거
        allIcons.forEach(icon => {
            icon.remove();
        });
}

//타임루프 바
function initializeOrResetTimer() {
    if (timer) {
        clearInterval(timer);
    }

    timeLeft = timeLimit;
    progressBar.style.width = '100%';
    progressBar.style.backgroundColor = 'green'; // 초기 색상

    timer = setInterval(() => {
        timeLeft -= 100;
        const progressWidth = (timeLeft / timeLimit) * 100;
        progressBar.style.width = `${progressWidth}%`;

        if (timeLeft <= 3000) {
            progressBar.style.backgroundColor = 'red';
        }

        if (timeLeft <= 0) {
            clearInterval(timer);

            // 여기서 추가적인 타임아웃 처리를 할 수 있습니다.
        }
    }, 100);
}


    //==================================================================================================

    function httpRequestBoard(method, url, success, fail) {
        fetch(url, {
            method: method,
            headers: { // 로컬 스토리지에서 액세스 토큰 값을 가져와 헤더에 추가
                Authorization: 'Bearer ' + localStorage.getItem('access_token'),
                'Content-Type': 'application/json',
            },
        })
        .then(response => {
            if (response.status === 200 || response.status === 201) {

                return response.json(); // JSON이 아닌 일반 텍스트로 응답 받기 여기 수정함 .text였음
            }
            const refresh_token = getCookie('refresh_token');
            if (response.status === 401 && refresh_token) {
                return fetch('/api/token', {
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
                    return httpRequestBoard(method, url, success, fail);
                })
                .catch(error => fail());
            } else {
                return null;
            }
        })
        .then(data => {
            console.log("httpRequestBoard: "+data);
            success(data);
        });
    }

    function httpRequestBoardClick(method, url, body, success, fail) {
        fetch(url, {
            method: method,
            headers: { // 로컬 스토리지에서 액세스 토큰 값을 가져와 헤더에 추가
                Authorization: 'Bearer ' + localStorage.getItem('access_token'),
                'Content-Type': 'application/json',
            },
            body: body,
        })
        .then(response => {
            if (response.status === 200 || response.status === 201) {

                return response.json(); // JSON이 아닌 일반 텍스트로 응답 받기
            }
            const refresh_token = getCookie('refresh_token');
            if (response.status === 401 && refresh_token) {
                return fetch('/api/token', {
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
                    return httpRequestBoardClick(method, url, body, success, fail);
                })
                .catch(error => fail());
            } else {
                return null;
            }
        })
        .then(data => {
         console.log("httpRequestBoardClick: "+data);
            success(data);
        });
    }

    // 쿠키를 가져오는 함수
    function getCookie(key) {
        let result = null;
        const cookie = document.cookie.split(';');
        cookie.some(function(item) {
            item = item.replace(' ', '');
            const dic = item.split('=');
            if (key === dic[0]) {
                result = dic[1];
                return true;
            }
        });
        return result;
    }
