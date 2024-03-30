function checkDuplicate() {
    // 입력된 ID 값 가져오기
    var userId = document.getElementById('id').value;

    // AJAX를 사용하여 컨트롤러로 ID 값을 전송
    var xhr = new XMLHttpRequest();
    xhr.open('GET', '/home/checkDuplicate?id=' + userId, true);
    xhr.onreadystatechange = function () {
        if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
            // 응답 처리
            var response = xhr.responseText;
            // 받은 데이터를 화면에 표시합니다.
            var resultContainer = document.getElementById('idduplicatecheck');
            resultContainer.innerHTML = ''; // 이전 검색 결과를 지웁니다.
            var accountInfo = '<p>' + response + '</p>';
            resultContainer.insertAdjacentHTML('beforeend', accountInfo);
        }
    };
    xhr.send();
}