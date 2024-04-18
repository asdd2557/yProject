/*const eventSource = new EventSource("http://godding-elastic-env-2.eba-mygzmyet.ap-northeast-2.elasticbeanstalk.com/sender/asdd2t557@gmail.com/receiver/cos")

eventSource.onmessage=(event)=> {
console.log(1,event);
const data = JSON.parse(event.data);
console.log(2,data);
}
*/

document.querySelector("#chatButton").addEventListener("click",() => {
msgInput();
});

document.querySelector("#chatOutput").addEventListener("keydown", (e) => {
    if (e.keyCode === 13) {
msgInput();
}
});




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

function getUserName(){
return document.querySelector("#username").textContent;
}