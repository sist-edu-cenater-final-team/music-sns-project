// api/purchaseMusic/list
document.addEventListener("DOMContentLoaded", function () {
    purchase.createList(1);
});

const purchase = {
    tbody : document.querySelector('#myPurchaseMusicBody'),
    profileMusicModal : new bootstrap.Modal(document.getElementById("profileMusicModal")),
    profileMusicModalBody : document.querySelector('#profileMusicModalBody'),
    selectProfileMusic : document.querySelector('#selectProfileMusic'),
    musicList : (musicData) => {
        return AuthFunc.apiRequest(() =>
            axios.get(`${ctxPath}/api/profileMusic/list?musicId=${musicData}`, {
                headers: AuthFunc.getAuthHeader()
            })
        )
        .then(response => {
            console.log("musicDat212313a :: ", musicData);
            console.log(" profileMusic :: ", response.data);
        })
        .catch(error => {
            console.error('오류:', error);
            if (error.response) {
                const errorData = error.response.data.error;
                if (errorData){
                    alert(errorData.customMessage);
                }
            }
        });
    },
    createList : (pageNo) => {
        console.log("넘긴거 받았어요~~!! :: ", pageNo);
        // 스프링 시큐리티 인증 토큰을 헤더에 추가하여 주문 목록 요청
        return AuthFunc.apiRequest(() =>
            axios.get(`${ctxPath}/api/purchaseMusic/list?pageNo=${pageNo}`, {
                headers: AuthFunc.getAuthHeader()
            })
        )
        .then(response => {
            const res = (response.data.purchaseMusic.length > 0) ? response.data : 0;
            purchase.renderMusicList(res);
            console.log("response.data.pageNo :: ", response.data.pageNo);
            purchase.paginationCall(response.data.pageNo, response.data.totalPages);
        })
        .catch(error => {
            console.error('오류:', error);
            if (error.response) {
                const errorData = error.response.data.error;
                if (errorData){
                    alert(errorData.customMessage);
                }
            }
        });
    },
    renderMusicList : (musicData) => {

        console.log("musicData :::: ", musicData);
        if(musicData === 0) {
            purchase.tbody.innerHTML = `<tr><td colspan="4">구매한 음악이 없습니다.</td></tr>`;
            return;
        }

        let HTML = ``;
        musicData.purchaseMusic.forEach((item, index) => {
            HTML += `
                <tr>
                    <td class="link_td" onclick="window.open('https://open.spotify.com/track/${item.musicId}')">
                        <div class="music-info">
                            <div class="music-img">
                                <img src="${item.albumImageUrl}" alt="노래 이미지" />
                            </div>
                            <p class="music-text">${item.musicName}</p>
                        </div>
                    </td>
                    <td class="link_td" onclick="window.open('https://open.spotify.com/artist/${item.artistId}')">
                        <p class="music-artist">${item.artistName}</p>
                    </td>
                    <td class="link_td" onclick="window.open('https://open.spotify.com/album/${item.albumId}')">
                        <p class="music-artist">${item.albumName}</p>
                    </td>
                    <td class="btn-form">
                        <button type="button" class="btn btn-set-profile" onclick="purchase.openProfileMusicModal(this, '${item.musicId}')">설정하기</button>
                    </td>
                </tr>
            `;
        });
        purchase.tbody.innerHTML = HTML;

    },
    openProfileMusicModal : (el, musicId) => {

        // 프로필 설정 팝업 열기
        purchase.profileMusicModal.show();

        // emotions 있는지 체크
        const emotions = purchase.profileMusicModalBody.querySelector(".emotions");
        if (!emotions) return;

        // 기본 감정
        const defaultEmotion = "CALM";
        const buttons = emotions.querySelectorAll(".btn");

        // 기본 활성화 설정
        let activated = false;
        buttons.forEach((item) => {
            const isDefault = item.dataset.emotion === defaultEmotion;
            item.classList.toggle("active", isDefault);
            activated = activated || isDefault;
        });
        if (!activated && buttons.length) {
            buttons[0].classList.add("active");
        }

        // 현재 선택된 감정값
        const activeBtn = emotions.querySelector(".btn.active");
        let btnValue = activeBtn?.dataset.emotion || defaultEmotion;

        // 클릭 이벤트로 갱신
        emotions.onclick = (e) => {
            const btn = e.target.closest(".btn");
            if (!btn) return;

            buttons.forEach((item) => item.classList.remove("active"));
            btn.classList.add("active");

            btnValue = btn.dataset.emotion || defaultEmotion;
            btnIndex = Array.from(buttons).indexOf(btn)+1;

            console.log("btnIdx ::::: ", btnIndex);

        };

        // 모달 오픈 시점 값 확인
        // const current = emotions.querySelector(".btn.active")?.dataset.emotion || defaultEmotion;
        // console.log("btnValue (open):", current, "musicId:", musicId);

        // 클릭한 요소 기준으로 TR 찾기
        const row = el.closest("tr");
        if (!row) return;

        const musicContent = row.querySelectorAll("td:not(.btn-form)");

        let html = "";
        musicContent.forEach(cell => {
            html += cell.outerHTML; // 셀 자체를 복사
        });

        purchase.selectProfileMusic.innerHTML = `
                                                <div class="select-music-wrapper">${html}</div>
                                                <div class="btn-form">
                                                    <button type="button" class="btn btn-set-profile">프로필 음악 추가하기</button>
                                                </div>
                                                `;

        purchase.selectProfileMusic.querySelector(".btn-set-profile").onclick = () => {
            purchase.addProfileMusic(btnValue, musicId);
        };

    },
    closeProfileMusicModal : () => {
        purchase.profileMusicModal.hide();
        const btnEmotions = purchase.profileMusicModalBody.querySelectorAll(".emotions .btn");

        btnEmotions.forEach(btn => {
            btn.classList.remove("active")
        });

        // 첫 번째 버튼에만 active 추가 (존재할 때만)
        if (btnEmotions.length > 0) {
            btnEmotions[0].classList.add("active");
        }

    },
    addProfileMusic : (emotion, musicId) => {
        return AuthFunc.apiRequest(() =>
            // musicId=${musicId}&emotionId=${emotionId}
            axios.post(`${ctxPath}/api/profileMusic/add?musicId=${musicId}&emotion=${emotion}`,{}, {
                headers: AuthFunc.getAuthHeader()
            })
        )
        .then(response => {
            // console.log("추가완룧효훃훃", response.data);
            alert(response.data);
            //purchase.musicList(musicId);
            purchase.closeProfileMusicModal();
        })
        .catch(error => {
            console.error('오류:', error);
            if (error.response) {
                const errorData = error.response.data.error;
                if (errorData){
                    alert(errorData.customMessage);
                }
            }
        });
    },
    // 리뷰리스트 페이지네이션 보여주기
    paginationCall : (currentPage, totalPages) => {

        const pagination = document.querySelector('#pageBar');

        if(!pagination) return;

        if (totalPages <= 1) {
            pagination.innerHTML = '';
            return;
        }

        let paginationHTML = ``;

        // 이전
        if (currentPage > 1) {
            paginationHTML += `<button class="page-btn" data-page="${currentPage - 1}">‹</button>`;
        }

        // 페이지 번호들
        for (let i = 1; i <= totalPages; i++) {
            if (i === 1 || i === totalPages || Math.abs(i - currentPage) <= 1) {
                const active = i === currentPage ? 'active' : '';
                paginationHTML += `<button class="page-btn ${active}" data-page="${i}">${i}</button>`;
            }
        }

        // 다음
        if (currentPage < totalPages) {
            paginationHTML += `<button class="page-btn" data-page="${currentPage + 1}">›</button>`;
        }

        pagination.innerHTML = paginationHTML;

        // 클릭 이벤트
        pagination.onclick = (e) => {
            if (e.target.dataset.page) {
                console.log("currentPage : " +currentPage);
                console.log("e.target.dataset.page 넘깁니다 :::: ", e.target.dataset.page);
                purchase.createList(parseInt(e.target.dataset.page));
            }
        };

    }
}