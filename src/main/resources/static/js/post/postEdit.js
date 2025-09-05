$(document).ready(function(){

    const postId = new URLSearchParams(location.search).get('postId');
    if (!postId) {
        console.warn('postId가 없습니다.');
        return;
    }

    AuthFunc.apiRequest(() => {
        return axios.get(`/api/post/postEdit`,
            {params: {postId: postId},
                headers: AuthFunc.getAuthHeader()}
        )
    }).then(function (response) {

        const data = response.data;
        console.log(data);

        // 제목
        document.getElementById("editTitle").value = data.title;

        // 내용
        document.getElementById("content").value = data.contents;

        // 감정 초기화: 서버에서 내려준 한글 감정을 기반으로 버튼 active 및 전송값 설정
        (function() {
            try {
                const mapENtoKO = { CALM:'평온', HAPPY:'기쁨', LOVE:'사랑', SAD:'우울', ANGRY:'화남', TIRED:'피곤' };
                const mapKOtoEN = Object.fromEntries(Object.entries(mapENtoKO).map(([en, ko]) => [ko, en]));
                if (data.userEmotion) {
                    // 서버 값(한글) 저장 → 전송에 그대로 사용
                    userEmotion = data.userEmotion;
                    // 해당하는 버튼 찾아 active 설정
                    const enCode = mapKOtoEN[userEmotion];
                    if (enCode) {
                        const targetBtn = document.querySelector(`.editEmotion .emotions .btn[value="${enCode}"]`);
                        if (targetBtn) {
                            document.querySelector('.editEmotion .emotions .btn.active')?.classList.remove('active');
                            targetBtn.classList.add('active');
                        }
                    }
                }
            } catch (e) {
                console.warn('감정 초기화 실패:', e);
            }
        })();

        // 이미지 캐러셀
        const carouselInner = document.querySelector("#postCarousel .carousel-inner");
        carouselInner.innerHTML = "";
        data.imageUrls.forEach((url, idx) => {
            const item = document.createElement("div");
            item.className = "carousel-item" + (idx === 0 ? " active" : "");
            item.innerHTML = `
                <img src="${url}" 
                     class="d-block" 
                     style="width:100%; height:100%; object-fit:contain;">
            `;
            carouselInner.appendChild(item);
        });


    }).catch(function (error) {
        const status = error?.response?.status;
        if (status === 403) {
            // 권한 없음: 알림만 띄우고 현재 페이지에서 뒤로가기 처리 (postView로 복귀)
            const msg = error?.response?.data?.error?.customMessage || '권한이 없습니다.';
            alert(msg);
            // postEdit는 직접 접근할 수 없으므로, 브라우저 히스토리에 이전 페이지가 postView라면 돌아가고,
            // 히스토리가 없으면 피드로 이동하지 않고 현재 창만 컨텐츠 초기화하여 편집 불가 상태를 표시합니다.
            if (document.referrer && /\/post(\/postView)?/.test(new URL(document.referrer, location.origin).pathname)) {
                // 안전한 뒤로가기: history.length가 1인 경우는 새 탭 직접 접근 가능성이 크다
                if (history.length > 1) {
                    history.back();
                } else {
                    // 리퍼러는 있으나 back 불가하면 리퍼러로 직접 이동
                    location.href = document.referrer;
                }
            } else {
                // 최소한의 UX: 폼 비활성화 + 안내 메시지 표시
                document.querySelectorAll('#postEditForm input, #postEditForm textarea, #postEditForm button').forEach(el => el.disabled = true);
                const info = document.createElement('div');
                info.className = 'alert alert-warning mt-3';
                info.textContent = '권한이 없어 편집할 수 없습니다.';
                document.getElementById('postEditForm')?.appendChild(info);
            }
            return;
        }
        console.error(error);
    })

    const buttons = document.querySelectorAll('.editEmotion .emotions .btn');

    // 매핑: 버튼의 영문 코드 <-> 서버가 기대하는 한글 라벨
    const EN_TO_KO = {
        CALM: '평온',
        HAPPY: '기쁨',
        LOVE: '사랑',
        SAD: '우울',
        ANGRY: '화남',
        TIRED: '피곤'
    };
    const KO_TO_EN = Object.fromEntries(Object.entries(EN_TO_KO).map(([en, ko]) => [ko, en]));

    // 서버에 전송할 값은 한글 라벨로 유지
    let userEmotion = '평온';

    buttons.forEach((btn) => {
        btn.addEventListener('click', () => {
            // 이전 active 제거
            document.querySelector('.editEmotion .emotions .btn.active')?.classList.remove('active');

            // 현재 active 설정
            btn.classList.add('active');

            // 버튼의 영문 값을 서버가 요구하는 한글로 변환하여 저장
            userEmotion = EN_TO_KO[btn.value] || btn.value;
        });
    });

    // 수정하기 버튼을 누르면
    document.querySelector('button.editButton').addEventListener('click', function () {

        // 제목에 값이 없다면

        const editTitle = $('textarea#editTitle').val().trim();
        if(editTitle == ""){
            alert("제목을 입력하세요.");
            return;
        }
        console.log(editTitle);

        // 내용에 값이 없다면

        const editContent = $('textarea#content').val().trim();
        if(editContent == ""){
            alert("내용을 입력하세요.");
            return;
        }
        console.log(editContent);

        AuthFunc.apiRequest(() => {
            return axios.put("/api/post/goPostEdit",
                {
                    postId: Number(postId),
                    title: editTitle,
                    contents: editContent,
                    userEmotion: userEmotion
                },
                {
                    headers: {
                        ...AuthFunc.getAuthHeader(),
                        'Content-Type': 'application/json'
                    }
                }
            )
        }).then(function (response) {

            alert("수정이 완료되었습니다.");
            location.href = `${ctxPath}/index`;

        }).catch(function (error) {

            const status = error?.response?.status;
            const data = error?.response?.data;
            if (status === 403) {
                const msg = data?.error?.customMessage || data?.customMessage || '본인 게시글만 수정가능합니다.';
                alert(msg);
                return; // 이동 없이 종료
            }
            // 기타 오류는 서버 메시지를 우선 노출
            const fallbackMsg = (typeof data === 'string' && data) || data?.error?.customMessage || data?.customMessage || '수정이 실패했습니다.';
            alert(fallbackMsg);
            console.error(error);

        })
    })



})