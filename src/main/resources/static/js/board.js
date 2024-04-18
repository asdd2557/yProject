    document.addEventListener('DOMContentLoaded', function() {
      const board = document.querySelector('.board');
      let currentPlayer = 'black'; // 현재 플레이어 (흑돌 또는 흰돌)
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
                  const row = parseInt(this.dataset.row, 10);
                  const col = parseInt(this.dataset.col, 10);

                  // 이미 돌이 놓여있지 않은 경우에만 돌을 놓기
                  if (!isStonePlaced(row, col)) {
                      placeStone(row, col, currentPlayer);
                      currentPlayer = currentPlayer === 'black' ? 'white' : 'black'; // 플레이어 교대
                      // 클릭 후 오목알 미리보기 요소 제거
                      if (previewStone) {
                          previewStone.remove();
                          previewStone = null; // 미리보기 요소 초기화
                      }
                  }
              });
          }
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
  });