var matchingfindinterval;

 document.addEventListener('socketMessage', function(event) {

 gamefind();

     var jsonData = event.detail;


       body = JSON.stringify({
                   connect: "3"
               })

async function gameBaseChange(response) {
    for (var userKey in jsonData) {
        if (jsonData.hasOwnProperty(userKey)) {
            if (jsonData[userKey].connect == "3" && jsonData[userKey].email == response.email) {
      hideMatchingContainer();
                                                 showBoardContainer();

                            if (jsonData[userKey].position == "0") {

                            }
                if (jsonData[userKey].position == "1") {

                }


            }else if (jsonData[userKey].connect != "3" && jsonData[userKey].email == response.email) {
             hideBoardContainer();
             showMatchingContainer();
            }
        }
    }
}


     function fail(){}
          httpRequestStandbyGET("GET", "/api/getemail", body, gameBaseChange, fail)
 });

document.querySelector("#matchStartButton").addEventListener("click",() => {
   body = JSON.stringify({
              connect: "2"
          })
          function success(){
            showLoader();
          }
          function fail(){
          window.location.href = '/login';
          }
          connectUpdate(body,success,fail );
           matchingfindinterval = setInterval(matchingfind, 3000); //
});

document.querySelector("#matchCancel").addEventListener("click",() => {
clearInterval(matchingfindinterval);
   body = JSON.stringify({
              connect: "1"
          })
         function success(){
          hideLoader();
          }

          function fail(){
          alert("실패");
          }

  connectUpdate(body,success,fail);
});




function gamefind(){

function success(data){
if(data == null){
return;
}
   body = JSON.stringify({
              connect: "3"
          })
          function success1(){};
          function fail(){};
      connectUpdate(body,success1,fail);
}

function fail(){}

     httpRequestStandbyGET("GET", "/api/gamefind", body, success, fail)
}


function matchingfind(){


  body = JSON.stringify({
              find: "1"
          })


function matchingfindsuccess(data){

if(data[1].email == null){
return;
}

clearInterval(matchingfindinterval);

body = JSON.stringify({
               connect: "3"
           })

function success(){}
function fail(){}

connectUpdate(body,success,fail);

const body1 = {
    meconnectE: {
        connect: data[0].connect,
        email: data[0].email,
        position: data[0].position,
        matching: data[0].matching
    },
    opconnectE: {
        connect: data[1].connect,
        email: data[1].email,
        position: data[1].position,
        matching: data[1].matching
    }
};

 httpRequestStandby("POST", "/api/firstgamesave", JSON.stringify(body1), success, fail)
}

function fail(){}

    httpRequestStandbyGET("GET", "/api/matchingfind", body, matchingfindsuccess, fail)
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





function showLoader(){
console.log("showLoader")
       document.getElementById("loader").style.display = "block";
        document.getElementById("matchStartButton").style.display = "none";
        document.getElementById("matchCancel").style.display = "block";
}

function hideLoader() {
console.log("hideLoader")
  document.getElementById("loader").style.display = "none";
  document.getElementById("matchStartButton").style.display = "block";
         document.getElementById("matchCancel").style.display = "none";
}




function connectUpdate(body,success,fail){
httpRequestStandby("PUT", "/api/connectupdateorsave", body, success, fail)
}








//-=====================================================================

function httpRequestStandby(method, url, body, success, fail) {
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
                            httpRequest(method, url, body, success, fail);
                        })
                        .catch(
                        error => fail());
                } else {
                    return fail();
                }
            })
            .then(data => {
                 //data
            });
        }


        function httpRequestStandbyGET(method, url, body, success, fail) {
                    fetch(url, {
                        method: method,
                        headers: { // 로컬 스토리지에서 액세스 토큰 값을 가져와 헤더에 추가
                            Authorization: 'Bearer ' + localStorage.getItem('access_token'),
                            'Content-Type': 'application/json',
                        },
                    }).then(response => {
                        if (response.status === 200 || response.status === 201) {
                             return response.json(); // JSON 형식의 데이터로 변환
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
                                    httpRequest(method, url, body, success, fail);
                                })
                                .catch(
                                error => fail());
                        } else {
                            return fail();
                        }
                    })
                    .then(data => {
                        success(data);
                    });
                }
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