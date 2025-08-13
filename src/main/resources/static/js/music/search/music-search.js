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
        const paginationEl = document.getElementById('pagination');
        const loadingEl = document.getElementById('loading');
        const errorBox = document.getElementById('errorBox');
        const searchMeta = document.getElementById('searchMeta');

        const keywordInput = document.getElementById('searchKeyword');
        const categorySelect = document.getElementById('searchCategory');
        const sideKeywordInput = document.getElementById('sideSearchKeyword');
        const sideCategorySelect = document.getElementById('sideSearchCategory');
        const inputPage = document.getElementById('inputPage');
        const inputSize = document.getElementById('inputSize');

        let currentPage = parseInt(inputPage.value || '1', 10) || 1;
        let pageSize = parseInt(inputSize.value || '20', 10) || 20;
        let totalPages = 1;

        // 중복 API 호출 방지 플래그 (in-flight)
        let inFlight = false;

        // URL 파라미터에서 값 읽어 폼에 반영
        const urlParams = new URLSearchParams(window.location.search);
        const urlKeyword = urlParams.get('keyword') || '';
        const urlSearchType = urlParams.get('searchType') || '';
        const urlPage = parseInt(urlParams.get('page') || inputPage.value || '1', 10) || 1;
        const urlSize = parseInt(urlParams.get('size') || inputSize.value || '20', 10) || 20;
        if (urlKeyword) {
            keywordInput.value = urlKeyword;
            sideKeywordInput.value = urlKeyword; // 사이드바 검색어도 동기화
        }
        if (urlSearchType) {
            categorySelect.value = urlSearchType;
            sideCategorySelect.value = urlSearchType; // 사이드바 카테고리도 동기화
        }
        currentPage = urlPage;
        pageSize = urlSize;

        // 초기 로드: keyword가 있으면 한 번만 API 호출
        // (여기서 한 번 호출한 뒤에는 inFlight 로 중복 방지)
        if (keywordInput.value && keywordInput.value.trim() !== '') {
            doSearch(currentPage);
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

            // UI reset
            resultsEl.innerHTML = '';
            paginationEl.innerHTML = '';
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
                currentPage = resp.page || page;
                totalPages = resp.totalPages || 1;

                renderMeta(resp);
                renderItems(resp.items || []);
                renderPaginationLinks(currentPage, totalPages);
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
                const albumImage = item.album?.albumImageUrl || '/assets/default-album.png';
                const duration = item.duration || '0:00';
                // 아티스트들을 개별 span으로 생성
                const artistsHtml = (item.artist || []).map((artist, index) => {
                    const comma = index > 0 ? ', ' : '';
                    return `${comma}<span class="artist-item" data-artist-id="${escapeHtml(artist.artistId || '')}">${escapeHtml(artist.artistName)}</span>`;
                }).join('');

                const a = document.createElement('a');
                a.dataset.trackId = item.trackId;
                a.dataset.albumId = item.album?.albumId || '';

                a.href = '#';
                a.className = 'list-group-item list-group-item-action track-item';

                a.innerHTML = `
                    <img class="track-thumb" src="${escapeHtml(albumImage)}" alt="${escapeHtml(albumName)}">
                    <div class="track-main">
                        <div class="track-title">${escapeHtml(item.trackName)}</div>
                        <div class="track-artist">${artistsHtml}</div>
                    </div>
                    <div class="track-album">${escapeHtml(albumName)}</div>
                    <div class="track-right">
                        <div class="track-duration">${escapeHtml(duration)}</div>
                        <div><a class="btn btn-sm btn-outline-primary btn-spotify" href="${escapeHtml(item.trackSpotifyUrl)}" target="_blank">Spotify</a></div>
                    </div>
                `;

                frag.appendChild(a);
            });

            resultsEl.appendChild(frag);
        }

        // renderPaginationLinks: 보라 테마, 처음/이전/숫자/다음/끝 포함, 링크는 페이지 이동 방식
        function renderPaginationLinks(current, total) {
            paginationEl.innerHTML = '';
            if (total <= 1) return;

            // helper: URL 빌드
            const buildUrl = (page) => {
                const params = new URLSearchParams();
                params.set('keyword', keywordInput.value || '');
                params.set('searchType', categorySelect.value || '전체');
                params.set('page', String(page));
                params.set('size', String(pageSize));
                return `${window.location.pathname}?${params.toString()}`;
            };

            // helper: li 생성
            const createLi = (labelHtml, href, disabled = false, active = false, ariaLabel = null) => {
                const li = document.createElement('li');
                li.className = 'page-item' + (disabled ? ' disabled' : '') + (active ? ' active' : '');
                const a = document.createElement('a');
                a.className = 'page-link';
                a.href = disabled ? '#' : href;
                a.innerHTML = labelHtml;
                if (ariaLabel) a.setAttribute('aria-label', ariaLabel);
                li.appendChild(a);
                return li;
            };

            // wrapper에 클래스 적용
            paginationEl.className = 'pagination pagination-purple';

            // 처음 (맨앞) - 1로 이동
            const firstHtml = '<span class="icon">««</span><span class="visually-hidden">처음</span>';
            paginationEl.appendChild(createLi(firstHtml, buildUrl(1), current <= 1, false, '처음'));

            // 이전
            const prevPage = Math.max(1, current - 1);
            const prevHtml = '<span class="icon">«</span><span class="visually-hidden">이전</span>';
            paginationEl.appendChild(createLi(prevHtml, buildUrl(prevPage), current <= 1, false, '이전'));

            // 숫자 페이지 (최대 노출 개수는 화면 너비에 따라 다름)
            const maxVisible = (window.innerWidth < 576) ? 5 : 9; // 작은 화면이면 5개, 넓으면 9개
            let start = Math.max(1, current - Math.floor(maxVisible / 2));
            let end = start + maxVisible - 1;
            if (end > total) {
                end = total;
                start = Math.max(1, end - maxVisible + 1);
            }

            // 앞쪽 생략 표시
            if (start > 1) {
                paginationEl.appendChild(createLi('1', buildUrl(1)));
                if (start > 2) {
                    const li = document.createElement('li');
                    li.className = 'page-item disabled';
                    li.innerHTML = `<span class="page-link">…</span>`;
                    paginationEl.appendChild(li);
                }
            }

            for (let i = start; i <= end; i++) {
                paginationEl.appendChild(createLi(String(i), buildUrl(i), false, i === current, i === current ? `페이지 ${i}, 현재` : `페이지 ${i}`));
            }

            // 뒤쪽 생략 표시
            if (end < total) {
                if (end < total - 1) {
                    const li = document.createElement('li');
                    li.className = 'page-item disabled';
                    li.innerHTML = `<span class="page-link">…</span>`;
                    paginationEl.appendChild(li);
                }
                paginationEl.appendChild(createLi(String(total), buildUrl(total)));
            }

            // 다음
            const nextPage = Math.min(total, current + 1);
            const nextHtml = '<span class="icon">»</span><span class="visually-hidden">다음</span>';
            paginationEl.appendChild(createLi(nextHtml, buildUrl(nextPage), current >= total, false, '다음'));

            // 마지막 (맨끝)
            const lastHtml = '<span class="icon">»»</span><span class="visually-hidden">마지막</span>';
            paginationEl.appendChild(createLi(lastHtml, buildUrl(total), current >= total, false, '마지막'));

            // 접근성: 네비게이션 래퍼가 필요한 경우 감싸기
            // (paginationEl은 ul 태그임을 가정)
            // ul.pagination는 이미 적절하게 위치해 있으니 추가 래퍼는 필요 없음.
        }

        function escapeHtml(text) {
            if (text === null || text === undefined) return '';
            return String(text)
                .replaceAll('&', '&amp;')
                .replaceAll('<', '&lt;')
                .replaceAll('>', '&gt;')
                .replaceAll('"', '&quot;')
                .replaceAll("'", '&#39;');
        }

        // 검색 폼 제출 시 page=1 로 세팅 (정상 submit -> 페이지 이동)
        const searchForm = document.getElementById('searchForm');
        searchForm.addEventListener('submit', function () {
            inputPage.value = '1';
            // 정상 submit 허용 (페이지 이동) — 클릭으로 AJAX 호출하지 않음
        });
    });
}
