// music-search-purple.js (수정본)
// 1) 중복 로드 방지: 전역 플래그 체크
if (window.__musicSearchPurpleInitialized) {
    // 이미 로드되어 초기화 된 상태면 재실행 차단
    console.warn('music-search-purple already initialized - skip.');
} else {
    window.__musicSearchPurpleInitialized = true;

    document.addEventListener('DOMContentLoaded', function () {
        const API_URL = '/api/music/spotify/search';
        const resultsEl = document.getElementById('searchResults');
        const loadingEl = document.getElementById('loading');
        const errorBox = document.getElementById('errorBox');
        const searchMeta = document.getElementById('searchMeta');
        const loadMoreBtn = document.getElementById('loadMoreBtn');


        const keywordInput = document.getElementById('searchKeyword');
        const categorySelect = document.getElementById('searchCategory');
        const sideKeywordInput = document.getElementById('sideSearchKeyword');
        const sideCategorySelect = document.getElementById('sideSearchCategory');
        // const inputPage = document.getElementById('inputPage');
        // const inputSize = document.getElementById('inputSize');

        let currentPage = 1;
        let pageSize = 20;
        let totalPages = 1;
        let hasNext = false;

        // 중복 API 호출 방지 플래그 (in-flight)
        let inFlight = false;

        // URL 파라미터에서 값 읽어 폼에 반영
        const urlParams = new URLSearchParams(window.location.search);
        const urlKeyword = urlParams.get('keyword') || '';
        const urlSearchType = urlParams.get('searchType') || '';


        if (urlKeyword) {
            keywordInput.value = urlKeyword;
            sideKeywordInput.value = urlKeyword; // 사이드바 검색어도 동기화
        }
        if (urlSearchType) {
            categorySelect.value = urlSearchType;
            sideCategorySelect.value = urlSearchType; // 사이드바 카테고리도 동기화
        }
        // currentPage = urlPage;
        // pageSize = urlSize;

        // alert(`초기화: keyword=${urlKeyword}, searchType=${urlSearchType}, page=${currentPage}, size=${pageSize}`);

        // 초기 로드: keyword가 있으면 한 번만 API 호출
        // (여기서 한 번 호출한 뒤에는 inFlight 로 중복 방지)
        if (keywordInput.value && keywordInput.value.trim() !== '') {
            // UI reset
            resultsEl.innerHTML = '';
            doSearch();
        }

        // 화면 절반 크기 팝업창으로 Spotify 페이지 열기
        function openSpotifyPopup(url) {
            const screenWidth = window.screen.width;
            const screenHeight = window.screen.height;

            const popupWidth = Math.floor(screenWidth / 2);
            const popupHeight = Math.floor(screenHeight * 0.8);
            const left = Math.floor((screenWidth - popupWidth) / 2);
            const top = Math.floor((screenHeight - popupHeight) / 2);

            const features = `width=${popupWidth},height=${popupHeight},left=${left},top=${top},scrollbars=yes,resizable=yes`;

            window.open(url, 'spotifyPopup', features);
        }

        // doSearch: inFlight 체크로 중복 요청 차단
        async function doSearch(page = 1) {
            if (inFlight) {
                // 이미 요청 진행 중이면 무시
                return;
            }

            const keyword = (keywordInput.value || '').trim();
            const searchType = categorySelect.value || 'all';

            if (!keyword) {
                resultsEl.innerHTML = `<div class="list-group-item no-results">검색어를 입력하세요.</div>`;
                return;
            }

            // UI reset 필요시 호출하도록 함수밖으로 꺼냄
            // resultsEl.innerHTML = '';
            // paginationEl.innerHTML = '';
            errorBox.classList.add('d-none');
            searchMeta.classList.add('d-none');
            loadingEl.classList.remove('d-none');

            inFlight = true; // 요청 시작

            try {
                const params = { keyword, searchType, page, size: pageSize };
                const res = await axios.get(API_URL, { params });

                const data = res.data;
                if (!data || !data.success || !data.success.responseData) {
                    throw new Error('서버 응답 구조가 예상과 다릅니다.');
                }

                const resp = data.success.responseData;
                console.log(resp);
                currentPage = resp.page || page;
                hasNext = resp.hasNext || false;
                // alert(`현재 페이지: ${currentPage}, 다음 페이지 여부: ${hasNext}`);

                // renderMeta(resp);
                renderItems(resp.items || []);
                if(hasNext)
                    loadMoreBtn.classList.remove('d-none');
                else
                    loadMoreBtn.classList.add('d-none');
                // renderPaginationLinks(currentPage, totalPages);
            } catch (err) {
                console.error(err);
                errorBox.textContent = err.response?.data?.message || err.message || '검색 중 오류가 발생했습니다.';
                errorBox.classList.remove('d-none');
            } finally {
                inFlight = false; // 요청 끝
                loadingEl.classList.add('d-none');
            }
        }

        function renderMeta(resp) {
            const totalItems = resp.totalItems ?? 0;
            searchMeta.textContent = `총 ${totalItems}개 · ${resp.page}/${resp.totalPages} 페이지`;
            searchMeta.classList.remove('d-none');
        }

        function renderItems(items) {
            if (!items || items.length === 0) {
                resultsEl.innerHTML = `<div class="list-group-item no-results">검색 결과가 없습니다.</div>`;
                return;
            }

            const frag = document.createDocumentFragment();

            items.forEach(item => {
                // const artists = (item.artist || []).map(a => a.artistName).join(', ');
                const albumName = item.album?.albumName || '-';
                const releaseDate = item.album?.releaseDate || '-';
                const albumImage = item.album?.albumImageUrl || '/assets/default-album.png';
                const duration = item.track.duration || '0:00';
                // 아티스트들을 개별 span으로 생성
                const artistsHtml = (item.track.artists || []).map((artist, index) => {
                    const comma = index > 0 ? ', ' : '';
                    return `${comma}<a class="artist-item" href="${ctxPath}/music/artist/${escapeHtml(artist.artistId || '')}" data-artist-id="${escapeHtml(artist.artistId || '')}">${escapeHtml(artist.artistName)}</a>`;
                }).join('');


                const a = document.createElement('a');
                a.dataset.trackId = item.track.trackId;
                const albumId = item.album?.albumId || '';
                a.dataset.albumId = albumId;

                // a.href = '#';
                a.className = 'list-group-item list-group-item-action track-item';

                a.innerHTML = `
            <img class="track-thumb" data-album-id="${escapeHtml(item.album?.albumId || '')}" src="${escapeHtml(albumImage)}" alt="${escapeHtml(albumName)}" onclick="window.location.href='${ctxPath}/music/album/${escapeHtml(albumId)}'">
            <div class="track-main">
                <div class="track-title" data-track-id="${escapeHtml(item.trackId)}">${escapeHtml(item.track.trackName)}</div>
                <div class="track-artist">${artistsHtml}</div>
            </div>
            <div class="track-album" data-album-id="${escapeHtml(item.album?.albumId || '')}">
                <div class="track-album-name">
                    <a href="${ctxPath}/music/album/${escapeHtml(item.album?.albumId || '')}" style="text-decoration: none; color: inherit;">
                        ${escapeHtml(albumName)}
                    </a>
                </div>
                <div class="track-release-date">
                    <a href="${ctxPath}/music/album/${escapeHtml(item.album?.albumId || '')}" style="text-decoration: none; color: inherit;">
                        ${escapeHtml(releaseDate)}
                    </a>
                </div>
            </div>
            <div class="track-right">
                <div class="track-duration">${escapeHtml(duration)}</div>
                <button class="add-to-cart-btn" onclick="addCart(this)" title="장바구니에 추가">
                    <i class="bi bi-cart-plus"></i>
                </button>
            </div>
        `;

                frag.appendChild(a);

                // 트랙 제목 클릭 이벤트 추가
                const trackTitle = a.querySelector('.track-title');
                trackTitle.addEventListener('click', function(e) {
                    e.preventDefault();
                    e.stopPropagation();

                    const spotifyUrl = item.track.trackSpotifyUrl;
                    if (spotifyUrl) {
                        openSpotifyPopup(spotifyUrl);
                    }
                });
            });
            // 헤더 추가
            if(currentPage === 1) {
                const header = document.createElement('div');
                header.className = 'track-header';
                header.innerHTML = `
            <div class="track-thumb"></div>
            <div class="track-main">제목</div>
            <div class="track-album">앨범</div>
            <div class="track-right">
                <i class="bi bi-clock"></i>
            </div>
        `;
                resultsEl.appendChild(header);
            }
            resultsEl.appendChild(frag);
        }

        // renderPaginationLinks: 보라 테마, 처음/이전/숫자/다음/끝 포함, 링크는 페이지 이동 방식
        // function renderPaginationLinks(current, total) {
        //     paginationEl.innerHTML = '';
        //     if (total <= 1) return;
        //
        //     const buildUrl = (page) => {
        //         const params = new URLSearchParams();
        //         params.set('keyword', keywordInput.value || '');
        //         params.set('searchType', categorySelect.value || '전체');
        //         params.set('page', String(page));
        //         return `${window.location.pathname}?${params.toString()}`;
        //     };
        //
        //     const createLi = (labelHtml, href, disabled = false, active = false, ariaLabel = null) => {
        //         const li = document.createElement('li');
        //         li.className = 'page-item' + (disabled ? ' disabled' : '') + (active ? ' active' : '');
        //         const a = document.createElement('a');
        //         a.className = 'page-link';
        //         a.href = disabled ? '#' : href;
        //         a.innerHTML = labelHtml;
        //         if (ariaLabel) a.setAttribute('aria-label', ariaLabel);
        //         li.appendChild(a);
        //         return li;
        //     };
        //
        //     paginationEl.className = 'pagination pagination-purple';
        //
        //     // 이전
        //     const prevPage = Math.max(1, current - 1);
        //     paginationEl.appendChild(createLi('«', buildUrl(prevPage), current <= 1, false, '이전'));
        //
        //     const maxVisible = 5; // 항상 5개 버튼 고정
        //     let start = Math.max(1, current - Math.floor(maxVisible / 2));
        //     let end = start + maxVisible - 1;
        //     if (end > total) {
        //         end = total;
        //         start = Math.max(1, end - maxVisible + 1);
        //     }
        //
        //     // 앞쪽 생략 (... 표시)
        //     if (start > 1) {
        //         paginationEl.appendChild(createLi('1', buildUrl(1)));
        //         if (start > 2) {
        //             const li = document.createElement('li');
        //             li.className = 'page-item disabled';
        //             li.innerHTML = `<span class="page-link">…</span>`;
        //             paginationEl.appendChild(li);
        //         }
        //     }
        //
        //     // 페이지 번호
        //     for (let i = start; i <= end; i++) {
        //         paginationEl.appendChild(createLi(String(i), buildUrl(i), false, i === current, i === current ? `페이지 ${i}, 현재` : `페이지 ${i}`));
        //     }
        //
        //     // // 뒤쪽 생략 (... 표시)
        //     // if (end < total) {
        //     //     if (end < total - 1) {
        //     //         const li = document.createElement('li');
        //     //         li.className = 'page-item disabled';
        //     //         li.innerHTML = `<span class="page-link">…</span>`;
        //     //         paginationEl.appendChild(li);
        //     //     }
        //     //     paginationEl.appendChild(createLi(String(total), buildUrl(total)));
        //     // }
        //
        //     // 다음
        //     const nextPage = Math.min(total, current + 1);
        //     paginationEl.appendChild(createLi('»', buildUrl(nextPage), current >= total, false, '다음'));
        // }


        function escapeHtml(text) {
            if (text === null || text === undefined) return '';
            return String(text)
                .replaceAll('&', '&amp;')
                .replaceAll('<', '&lt;')
                .replaceAll('>', '&gt;')
                .replaceAll('"', '&quot;')
                .replaceAll("'", '&#39;');
        }
        loadMoreBtn.addEventListener('click', ()=>{
            loadMoreBtn.classList.add('d-none');
            if(hasNext) {
                currentPage++;
                doSearch(currentPage);
            }
        });
        //검색 창에서 엔터 쳤을시 이벤트 등록
        // keywordInput.addEventListener('keydown', function (e) {
        //     if (e.key === 'Enter') {
        //         e.preventDefault(); // 기본 엔터 동작 방지
        //         currentPage = 1; // 페이지 초기화
        //         doSearch(); // 검색 실행
        //     }
        // });

        // // 검색 폼 제출 시 page=1 로 세팅 (정상 submit -> 페이지 이동)
        // const searchForm = document.getElementById('searchForm');
        // searchForm.addEventListener('submit', function (e) {
        //     // inputPage.value = '1';
        // });
    });


    const apiRequest = AuthFunc.apiRequest;//함수참조

    // 장바구니 담기
    function addCart(btn) {

        const trackId = btn.closest('.track-item').dataset.trackId;

        // 장바구니 추가 로직 구현
        return apiRequest(() =>
            axios.post(`/api/cart/add?trackId=${trackId}`,{}, {
                headers: AuthFunc.getAuthHeader(),
            })
        )
        .then((response) => {
            console.log("response : ", response);
            if(!confirm("장바구니에 추가되었습니다. 장바구니로 이동하시겠습니까?")) return;
            location.href = `${ctxPath}/cart/list`;
        })
        .catch((error) => {
            console.error('오류:', error);
            if (error.response) {
                const errorData = error.response.data.error;
                if (errorData) {
                    if (error.response.status === 401) {
                        // 인증 오류 처리 (예: 로그인 페이지로 리다이렉트)
                        alert('로그인이 필요합니다. 로그인 페이지로 이동합니다.');
                        location.href = `${ctxPath}/auth/login`;
                        return;
                    } else {
                        alert(errorData.customMessage);
                    }
                } else {
                    alert("장바구니 담기 실패: 알 수 없는 오류가 발생했습니다.");
                }
            }
        });
    }
}
