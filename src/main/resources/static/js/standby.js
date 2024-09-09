function gameBaseChange(event) {
        console.log("gameBaseChange");

  var body = JSON.stringify({
        connect: "3" //필요 없음
    });
        function success(response){
        event.forEach(function(user) {
        if (user.connect == "3" && user.email == response.email){
                    hideMatchingContainer();
                    showBoardContainer();
        }else if(user.connect != "3" && user.email == response.email){
                            hideBoardContainer();
                            showMatchingContainer();
        }
            });
        }
        function fail() {}


    httpRequestStandbyGET("GET", "/api/getemail", body, success, fail);
     console.log("gameBaseChange exit");
}



document.querySelector("#matchStartButton").addEventListener("click", () => {
    var body = JSON.stringify({
        connect: "2"
    });

    function success() {
        showLoader();
    }

    function fail() {
        window.location.href = '/login';
    }

    connectUpdate(body, success, fail);
});

document.querySelector("#matchCancel").addEventListener("click", () => {
    var body = JSON.stringify({
        connect: "1"
    });

    function success() {
        hideLoader();
    }

    function fail() {
        alert("실패");
    }

    connectUpdate(body, success, fail);
});




function gamefind() {
 console.log("gamefind");
    function success(data) {
    console.log("gamefind: ", data)
        if (data == null) {
              hideBoardContainer();
              showMatchingContainer();
              return;
        }else{
           hideMatchingContainer();
          showBoardContainer();
                            refreshBoard();
                            playerNameUpdate();
                          setTimeout(userStoneCheck, 2000);
                          console.log("currentPlayer: ",currentPlayer);
        }


        var body = JSON.stringify({
            connect: "3"
        });

        function success1() {}

        function fail() {}

        connectUpdate(body, success1, fail);
    }

    function fail() {}
        var body = JSON.stringify({ // 필요 없음
            connect: "1"
        });
    httpRequestStandbyGET("GET", "/api/gamefind", body, success, fail);
     console.log("gamefind exit");
}

function matchingfind() {

    function matchingfindsuccess(data) {
        if(!data){
        return;
        }

        if (data[1].email == null) {
            return;
        }

        var body = JSON.stringify({
            connect: "3"
        });

        function success() {

                            hideMatchingContainer();
                             showBoardContainer();
                                currentPlayer = "";
                             setTimeout(playerNameUpdate, 2000);
                             setTimeout(userStoneCheck, 2000);
                            }

        function fail() {}

        const body1 = {
            opconnectE: {
                connect: data[1].connect,
                email: data[1].email,
                position: data[1].position,
                matching: data[1].matching
            }
        };

        httpRequestStandby("POST", "/api/firstgamesave", JSON.stringify(body1), success, fail);

    }

    function fail() {}

    httpRequestStandbyGET("GET", "/api/matchingfind", body, matchingfindsuccess, fail);

}



function showMatchingContainer() {
    document.getElementById('matchingContainer').style.display = 'block';
}

function showBoardContainer() {
    document.getElementById('boardContainer').style.display = 'block';
}

function hideMatchingContainer() {
    document.getElementById('matchingContainer').style.display = 'none';
}

function hideBoardContainer() {
    document.getElementById('boardContainer').style.display = 'none';
}

function showLoader() {
    console.log("showLoader");
    document.getElementById("loader").style.display = "block";
    document.getElementById("matchStartButton").style.display = "none";
    document.getElementById("matchCancel").style.display = "block";
}

function hideLoader() {
    console.log("hideLoader");
    document.getElementById("loader").style.display = "none";
    document.getElementById("matchStartButton").style.display = "block";
    document.getElementById("matchCancel").style.display = "none";
}

function connectUpdate(body, success, fail) {
    httpRequestStandby("PUT", "/api/connectUpdate", body, success, fail);
}

function httpRequestStandby(method, url, body, success, fail) {
    fetch(url, {
        method: method,
        headers: {
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
                .then(result => {
                    localStorage.setItem('access_token', result.accessToken);
                    httpRequestStandby(method, url, body, success, fail);
                })
                .catch(error => fail());
        } else {
            return fail();
        }
    }).then(data => {
        //data
    });
}

function httpRequestStandbyGET(method, url, body, success, fail) {
    fetch(url, {
        method: method,
        headers: {
            Authorization: 'Bearer ' + localStorage.getItem('access_token'),
            'Content-Type': 'application/json',
        },
    }).then(response => {
        if (response.status === 200 || response.status === 201) {

                       if (response.headers.get('Content-Length') === '0') {
                                   return null; // 빈 객체로 처리
                               }
                        return response.json();

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
                .then(result => {
                    localStorage.setItem('access_token', result.accessToken);
                    httpRequestStandbyGET(method, url, body, success, fail);
                })
                .catch(error => fail());
        } else {
            return fail();
        }
    }).then(data => {
    console.log(data);
        success(data);
    });
}

function getCookie(key) {
    var result = null;
    var cookie = document.cookie.split(';');
    cookie.some(function(item) {
        item = item.replace(' ', '');
        var dic = item.split('=');
        if (key === dic[0]) {
            result = dic[1];
            return true;
        }
    });
    return result;
}
