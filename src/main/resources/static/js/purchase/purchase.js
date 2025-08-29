// api/purchaseMusic/list
document.addEventListener("DOMContentLoaded", function () {
    purchase.createList(1);
});

const purchase = {
    tbody : document.querySelector('#myPurchaseMusicBody'),
    checkAll : document.querySelector('#purchaseMusicAllCheck'),
    createList : (pageNo) => {
        console.log("넘긴거 받았어요~~!! :: ", pageNo);
        // 스프링 시큐리티 인증 토큰을 헤더에 추가하여 주문 목록 요청
        return AuthFunc.apiRequest(() =>
            axios.get(`${ctxPath}/api/purchaseMusic/list?pageNo=${pageNo}`, {
                headers: AuthFunc.getAuthHeader()
            })
        )
        .then(response => {
            console.log('구매한 상품 목록:', response.data);
            purchase.renderMusicList(response.data);
            console.log("response.data.pageNo :: ", response.data.pageNo);
            purchase.paginationCall(response.data.pageNo, response.data.totalPages);
        })
        .catch(error => {
            console.error('오류:', error);
            if (error.response) {
                const errorData = error.response.data.error;
                if (errorData){
                    alert(errorData.customMessage);

                    if(errorData.httpStatus === "NOT_ACCEPTABLE"){
                        location.href = `${ctxPath}/auth/login`;
                    }
                }
            }
        });
    },
    renderMusicList : (musicData) => {
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
                        <button type="button" class="btn btn-set-profile" onclick='purchase.addProfileMusic("${item.musicId}")'>설정하기</button>
                    </td>
                </tr>
            `;
        });

        purchase.tbody.innerHTML = HTML;


        // 체크박스 이벤트 연결
        //purchase.initCheckEvents();
    },
    addProfileMusic : (musicId) => {
        console.log("프로필 음악아읻 : ", musicId);
        return AuthFunc.apiRequest(() =>
            axios.post(`${ctxPath}/api/profileMusic/add?musicId=${musicId}`,{}, {
                headers: AuthFunc.getAuthHeader()
            })
        )
        .then(response => {
            console.log('가져온 음악 정보:', response.data);
        })
        .catch(error => {
            console.error('오류:', error);
            if (error.response) {
                const errorData = error.response.data.error;
                if (errorData){
                    alert(errorData.customMessage);

                    if(errorData.httpStatus === "NOT_ACCEPTABLE"){
                        location.href = `${ctxPath}/auth/login`;
                    }
                }
            }
        });
    },
    initCheckEvents: () => {
        const rowChecks = purchase.tbody.querySelectorAll('input[name="musicCheck"]');

        // 전체 선택 클릭 시
        purchase.checkAll.addEventListener('change', () => {
            rowChecks.forEach(item => item.checked = purchase.checkAll.checked);
            purchase.updateMasterState();
        });

        // 개별 체크박스 클릭 시
        rowChecks.forEach(item => {
            item.addEventListener('change', () => purchase.updateMasterState());
        });

        // 초기 상태 반영
        purchase.updateMasterState();
    },
    updateMasterState: () => {
        const rowChecks = purchase.tbody.querySelectorAll('input[name="musicCheck"]');
        const checkedCount = purchase.tbody.querySelectorAll('input[name="musicCheck"]:checked').length;

        if (checkedCount === 0) {
            purchase.checkAll.checked = false;
            purchase.checkAll.indeterminate = false;
        } else if (checkedCount === rowChecks.length) {
            purchase.checkAll.checked = true;
            purchase.checkAll.indeterminate = false;
        } else {
            purchase.checkAll.checked = false;
            purchase.checkAll.indeterminate = true;
        }
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