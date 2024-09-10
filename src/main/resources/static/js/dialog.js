
document.querySelector("#chatButton").addEventListener("click",() => {
sendMessage();
msgInput();
});

document.querySelector("#chatOutput").addEventListener("keydown", (e) => {
    if (e.keyCode === 13) {
    sendMessage();
msgInput();
}
});



function msgReceive(data){
let chatContent = document.querySelector("#chatContent");
let chatIn = document.createElement("div");
chatIn.className = "chatIn";
chatIn.innerHTML =data.sender +": "+ data.msg;
chatContent.append(chatIn);
chatContent.scrollTop = chatContent.scrollHeight;
}


function msgInput(){
let chatContent = document.querySelector("#chatContent");
let chatIn = document.createElement("div");
let msgInput = document.querySelector("#chatOutput");


chatIn.className = "chatIn";
chatIn.innerHTML =getUserName() +": "+ msgInput.value;
chatContent.append(chatIn);
chatContent.scrollTop = chatContent.scrollHeight;
msgInput.value ="";
}

function sendMessage(){
let msgInput = document.querySelector("#chatOutput");
message = msgInput.value;

function success(responseData){
console.log(responseData,"Dialog Token Access");
};
function fail(){
console.log("Dialog Token Fail");
};

     body = JSON.stringify({
        msg: message
    });


        httpRequestDialog("POST","/api/chat", body, success, fail)

}

function getUserName(){
return document.querySelector("#username").textContent;
}



//=======================================================================================

        function httpRequestDialog(method, url, body, success, fail) {
                    fetch(url, {
                        method: method,
                        headers: { // 로컬 스토리지에서 액세스 토큰 값을 가져와 헤더에 추가
                            Authorization: 'Bearer ' + localStorage.getItem('access_token'),
                            'Content-Type': 'application/json',
                        },
                        body: body,
                    }).then(response => {
                        if (response.status === 200 || response.status === 201) {
                        console.log("respones dialog",response)
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
                                    httpRequestDialog(method, url, body, success, fail);
                                })
                                .catch(
                                error => fail());
                        } else {
                            return fail();
                        }
                    })
                    .then(data => {
                    success(data);
                            console.log("dialog success");
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