(function () {
    const signature = {
        melon: {name: 'MELON', colorClass: 'border-melon', pillClass: 'source-melon'},
        vibe: {name: 'VIBE', colorClass: 'border-vibe', pillClass: 'source-vibe'},
        genie: {name: 'GENIE', colorClass: 'border-genie', pillClass: 'source-genie'},
        bugs: {name: 'BUGS', colorClass: 'border-bugs', pillClass: 'source-bugs'}
    };

    const tracksNode = document.getElementById('tracks');
    const loadingNode = document.getElementById('loading');
    const errorNode = document.getElementById('error');
    const chartTitle = document.getElementById('chart-title');
    const chartSub = document.getElementById('chart-sub');
    const refreshBtn = document.getElementById('refreshBtn');
    let currentSource = 'vibe';

    // attach tab click handlers
    document.querySelectorAll('#tabs .source-pill').forEach(p => {
        p.addEventListener('click', () => {
            const src = p.getAttribute('data-source');
            if (!src) return;
            currentSource = src;
            // visual active
            document.querySelectorAll('#tabs .source-pill').forEach(x => x.classList.remove('active'));
            p.classList.add('active');
            loadChart(src);
        });
    });

    refreshBtn.addEventListener('click', () => loadChart(currentSource));

    function setLoading(isLoading) {
        loadingNode.style.display = isLoading ? 'flex' : 'none';
        tracksNode.style.display = isLoading ? 'none' : '';
        errorNode.classList.add('d-none');
    }

    function showError(msg) {
        loadingNode.style.display = 'none';
        tracksNode.style.display = 'none';
        errorNode.classList.remove('d-none');
        errorNode.textContent = msg;
    }

    function fetchChart(source) {
        // Build endpoint. user said: axios로 /api/music/genie/chart get요청
        // We'll assume pattern /api/music/{source}/chart
        const endpoint = `/api/music/${source}/chart`;
        return axios.get(endpoint, {timeout: 10000}).then(r => r.data);
    }
    // 타임스탬프를 한국어 날짜 형식으로 변환하는 함수
    function formatKoreanDateTime(timestamp) {
        if (!timestamp) return '-';

        const date = new Date(timestamp);
        const year = date.getFullYear();
        const month = date.getMonth() + 1; // getMonth()는 0부터 시작
        const day = date.getDate();
        const hours = date.getHours();
        const minutes = date.getMinutes();
        const seconds = date.getSeconds();

        return `${year}년 ${month}월 ${day}일 ${hours}시 ${minutes}분 ${seconds}초`;
    }
    function renderTracks(data, source) {
        tracksNode.innerHTML = '';
        if (!data || !data.success || !data.success.responseData) {
            showError('유효한 차트 데이터가 없습니다.');
            return;
        }
        const list = data.success.responseData;

        // update header
        chartTitle.textContent = `${signature[source].name} · 실시간 차트 (상위 ${list.length})`;
        chartSub.textContent = `마지막 업데이트: ${formatKoreanDateTime(data.success.timestamp || data.success?.timestamp)}`;

        // create cards
        list.forEach(item => {
            const card = document.createElement('div');
            card.className = `track-card ${signature[source].colorClass}`;

            // rank
            const rankBadge = document.createElement('div');
            rankBadge.className = 'rank-badge';

            let rankStatusHtml = '';
            if (source !== 'melon') { // melon 제외
                rankStatusHtml = `
        <div class="rank-status ${escapeHtml(String(item.rankStatus))}">
            ${statusIcon(item.rankStatus)}
            <span style="font-weight:600; font-size:.8rem;">
                ${escapeHtml(String(item.rankStatus === 'static' ? '' : (item.changedRank || 0)))}
            </span>
        </div>
    `;
            }

            rankBadge.innerHTML = `
    <div class="rank-num">${escapeHtml(String(item.rank))}</div>
    ${rankStatusHtml}
`;

            // album art
            const album = document.createElement('div');
            album.className = 'album';
            album.innerHTML = `
    <a href="${ctxPath}/music/search?searchType=album&keyword=${escapeHtml(item.albumName)}">
        <img src="${escapeHtml(item.albumArt)}" alt="${escapeHtml(item.albumName)}" loading="lazy">
    </a>
`;

            // meta
            const meta = document.createElement('div');
            meta.className = 'meta';
            console.log(ctxPath)
            meta.innerHTML = `
                <a class="title" href="${ctxPath}/music/search?searchType=all&keyword=${escapeHtml(item.title)}">
                    ${escapeHtml(item.title)}
                </a>            
                            
                <a class="artist" href="${ctxPath}/music/search?searchType=artist&keyword=${escapeHtml(item.artistName)}">
                    ${escapeHtml(item.artistName)}
                </a>
                <a class="small-muted" href="${ctxPath}/music/search?searchType=album&keyword=${escapeHtml(item.albumName)}">
                    ${escapeHtml(item.albumName)}
                </a>
          `;

            // right small text
            const right = document.createElement('div');
            right.className = 'text-end small-muted';
            right.style.minWidth = '90px';
          //   right.innerHTML = `
          //   <div>Song #${escapeHtml(String(item.songNumber))}</div>
          //   <div style="font-size:.82rem;">&nbsp;</div>
          // `;

            card.appendChild(rankBadge);
            card.appendChild(album);
            card.appendChild(meta);
            card.appendChild(right);

            // hover-only tooltip via title attr
            card.title = `${item.title} — ${item.artistName}`;

            tracksNode.appendChild(card);
        });
    }

    // helpers
    function statusIcon(status, changed) {
        if (status === 'up') {
            return `<i class="bi bi-arrow-up-circle-fill" style="color:#198754;font-size:1.0rem"></i>`;
        } else if (status === 'down') {
            return `<i class="bi bi-arrow-down-circle-fill" style="color:#d6333f;font-size:1.0rem"></i>`;
        } else {
            return `<i class="bi bi-dash-circle-fill" style="color:#6c757d;font-size:1.0rem"></i>`;
        }
    }

    function escapeHtml(s) {
        if (s === null || s === undefined) return '';
        return String(s)
            .replaceAll('&', '&amp;')
            .replaceAll('<', '&lt;')
            .replaceAll('>', '&gt;')
            .replaceAll('"', '&quot;')
            .replaceAll("'", '&#39;');
    }

    // main loader
    function loadChart(source) {
        setLoading(true);
        fetchChart(source)
            .then(data => {
                setLoading(false);
                renderTracks(data, source);
                tracksNode.style.display = '';
            })
            .catch(err => {
                console.error(err);
                const msg = err?.response?.status ? `차트 로드 실패 (HTTP ${err.response.status})` : '차트 요청 중 네트워크 에러가 발생했습니다.';
                showError(msg);
            });
    }

    // initial
    loadChart(currentSource);

})();