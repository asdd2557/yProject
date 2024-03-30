// 검색 폼 요소를 가져옵니다.
const searchForm = document.getElementById('searchForm');

// 검색 폼의 제출 이벤트를 처리합니다.
searchForm.addEventListener('submit', function(event) {
    // 폼의 기본 동작을 막습니다.
    event.preventDefault();

    // 검색어를 가져옵니다.
    const id = document.getElementById('id').value;

    // 서버로 보낼 요청 URL을 생성합니다.
    const url = `/home/searchid?id=${id}`;

    // 서버로 GET 요청을 보냅니다.
    fetch(url)
        .then(response => {
            // 응답을 JSON 형식으로 파싱합니다.
            return response.json();
        })
        .then(data => {
            // 받은 데이터를 화면에 표시합니다.
            const resultContainer = document.getElementById('searchResult');
            resultContainer.innerHTML = ''; // 이전 검색 결과를 지웁니다.
            const accountInfo = `
                <p>ID: ${data.id}</p>
                <p>Password: ${data.passWord}</p>
                <p>Nickname: ${data.nickName}</p>
                <p>Email: ${data.email}</p>
                <p>Birthday: ${data.birthday}</p>
            `;
            resultContainer.insertAdjacentHTML('beforeend', accountInfo);
        })
        .catch(error => {
            // 에러가 발생했을 때 처리합니다.
            console.error('Error:', error);
        });
});
