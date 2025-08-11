<%--
  Created by IntelliJ IDEA.
  User: user
  Date: 25. 8. 11.
  Time: 오후 4:49
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!doctype html>
<html lang="ko">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width,initial-scale=1" />
    <title>실시간 차트 뷰어</title>

    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">

    <style>
        :root{
            --color-melon: #B8E986;   /* 연두 */
            --color-vibe:  #2ECC71;   /* 초록 */
            --color-genie: #7BE4D5;   /* 민트 */
            --color-bugs:  #FF9F43;   /* 주황 */
            --card-bg: #ffffff;
            --muted: #6c757d;
        }

        body{
            background: linear-gradient(180deg, #f7f9fc 0%, #ffffff 100%);
            font-family: "Segoe UI", Roboto, "Noto Sans KR", sans-serif;
            padding: 24px;
        }

        .app-header{
            display:flex;
            gap:16px;
            align-items:center;
            justify-content:space-between;
            margin-bottom:18px;
        }

        .source-pill {
            display:inline-flex;
            gap:8px;
            align-items:center;
            padding:8px 14px;
            border-radius:999px;
            font-weight:600;
            cursor:pointer;
            user-select:none;
            transition:transform .15s ease, box-shadow .15s ease;
            box-shadow: 0 1px 4px rgba(20,20,20,0.04);
        }
        .source-pill:hover { transform:translateY(-2px); }

        .source-melon { background: linear-gradient(90deg, rgba(184,233,134,0.18), rgba(184,233,134,0.06)); color: #2f5b00; }
        .source-vibe  { background: linear-gradient(90deg, rgba(46,204,113,0.12), rgba(46,204,113,0.04)); color: #0b6138; }
        .source-genie { background: linear-gradient(90deg, rgba(123,228,213,0.12), rgba(123,228,213,0.04)); color: #00665a; }
        .source-bugs  { background: linear-gradient(90deg, rgba(255,159,67,0.12), rgba(255,159,67,0.04)); color: #6a3b00; }

        .chart-grid {
            display:grid;
            grid-template-columns: repeat(1, 1fr);
            gap:12px;
        }
        @media(min-width:700px){
            .chart-grid { grid-template-columns: repeat(2, 1fr); }
        }
        @media(min-width:1100px){
            .chart-grid { grid-template-columns: repeat(3, 1fr); }
        }

        .track-card{
            display:flex;
            gap:12px;
            align-items:center;
            padding:12px;
            border-radius:12px;
            background: var(--card-bg);
            transition: transform .12s ease, box-shadow .12s ease;
            border-left: 6px solid rgba(0,0,0,0.04);
        }
        .track-card:hover{
            transform: translateY(-6px);
            box-shadow: 0 10px 30px rgba(20,20,20,0.08);
        }

        .album{
            width:72px;
            height:72px;
            flex-shrink:0;
            border-radius:8px;
            overflow:hidden;
            background:linear-gradient(180deg,#f0f0f0,#eaeaea);
            display:flex;
            align-items:center;
            justify-content:center;
        }
        .album img { width:100%; height:100%; object-fit:cover; }

        .meta{
            flex:1;
            min-width:0;
            display:flex;
            flex-direction:column;
            gap:4px;
        }
        .meta .title{
            font-weight:700;
            font-size:1rem;
            white-space:nowrap;
            text-overflow:ellipsis;
            overflow:hidden;
        }
        .meta .artist { color:var(--muted); font-size:0.9rem; white-space:nowrap; text-overflow:ellipsis; overflow:hidden; }

        .rank-badge{
            min-width:72px;
            text-align:center;
            display:flex;
            flex-direction:column;
            gap:4px;
            align-items:center;
            justify-content:center;
        }
        .rank-num{
            font-weight:800;
            font-size:1.1rem;
        }
        .rank-status{
            font-size:0.85rem;
            padding:4px 8px;
            border-radius:999px;
            display:inline-flex;
            gap:6px;
            align-items:center;
            justify-content:center;
        }
        .rank-status.up { background: rgba(40,167,69,0.12); color:#198754; }
        .rank-status.down { background: rgba(220,53,69,0.08); color:#d6333f; }
        .rank-status.static { background: rgba(108,117,125,0.06); color:#6c757d; }

        /* signature left border per source */
        .border-melon  { border-left-color: var(--color-melon); }
        .border-vibe   { border-left-color: var(--color-vibe); }
        .border-genie  { border-left-color: var(--color-genie); }
        .border-bugs   { border-left-color: var(--color-bugs); }

        .small-muted{ color:var(--muted); font-size:0.85rem; }

        /* spinner wrapper */
        .centered {
            display:flex;
            align-items:center;
            justify-content:center;
            min-height:160px;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="app-header">
        <div>
            <h3 class="mb-0">실시간 차트 뷰어</h3>
            <div class="small-muted">음원 제공처: melon · vibe · genie · bugs (signature colors 반영)</div>
        </div>

        <div class="d-flex align-items-center gap-2">
            <div id="tabs" class="d-flex gap-2">
                <div class="source-pill source-melon" data-source="melon"><i class="bi bi-leaf-fill"></i> MELON</div>
                <div class="source-pill source-vibe"  data-source="vibe"><i class="bi bi-tree-fill"></i> VIBE</div>
                <div class="source-pill source-genie active" data-source="genie"><i class="bi bi-gem"></i> GENIE</div>
                <div class="source-pill source-bugs"  data-source="bugs"><i class="bi bi-fire"></i> BUGS</div>
            </div>
        </div>
    </div>

    <div class="card mb-3">
        <div class="card-body">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <div>
                    <strong id="chart-title">Genie · 실시간 차트 (상위 100)</strong><br />
                    <span id="chart-sub" class="small-muted">마지막 업데이트: -</span>
                </div>
                <div>
                    <button id="refreshBtn" class="btn btn-sm btn-outline-primary"><i class="bi bi-arrow-clockwise"></i> 새로고침</button>
                </div>
            </div>

            <div id="content">
                <div id="loading" class="centered">
                    <div class="spinner-border" role="status" aria-hidden="true"></div>
                    <span class="ms-3 small-muted">차트 불러오는 중...</span>
                </div>

                <div id="error" class="alert alert-warning d-none" role="alert"></div>

                <div id="tracks" class="chart-grid" style="display:none;"></div>
            </div>
        </div>
    </div>

    <footer class="text-center small-muted">
        제공된 JSON 응답을 axios로 받아 화면에 렌더링합니다.
    </footer>
</div>

<!-- Axios -->
<script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>
<!-- Bootstrap JS (optional for components) -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src=""></script>
<script>
    (function(){
        const signature = {
            melon:  { name: 'MELON', colorClass: 'border-melon', pillClass: 'source-melon'  },
            vibe:   { name: 'VIBE',  colorClass: 'border-vibe',  pillClass: 'source-vibe'   },
            genie:  { name: 'GENIE', colorClass: 'border-genie', pillClass: 'source-genie'  },
            bugs:   { name: 'BUGS',  colorClass: 'border-bugs',  pillClass: 'source-bugs'   }
        };

        const tracksNode = document.getElementById('tracks');
        const loadingNode = document.getElementById('loading');
        const errorNode = document.getElementById('error');
        const chartTitle = document.getElementById('chart-title');
        const chartSub = document.getElementById('chart-sub');
        const refreshBtn = document.getElementById('refreshBtn');
        let currentSource = 'genie';

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

        function setLoading(isLoading){
            loadingNode.style.display = isLoading ? 'flex' : 'none';
            tracksNode.style.display = isLoading ? 'none' : '';
            errorNode.classList.add('d-none');
        }

        function showError(msg){
            loadingNode.style.display = 'none';
            tracksNode.style.display = 'none';
            errorNode.classList.remove('d-none');
            errorNode.textContent = msg;
        }

        function fetchChart(source){
            // Build endpoint. user said: axios로 /api/music/genie/chart get요청
            // We'll assume pattern /api/music/{source}/chart
            const endpoint = `/api/music/${source}/chart`;
            return axios.get(endpoint, { timeout: 10000 }).then(r => r.data);
        }

        function renderTracks(data, source){
            tracksNode.innerHTML = '';
            if (!data || !data.success || !data.success.responseData){
                showError('유효한 차트 데이터가 없습니다.');
                return;
            }
            const list = data.success.responseData;

            // update header
            chartTitle.textContent = `${signature[source].name} · 실시간 차트 (상위 ${list.length})`;
            chartSub.textContent = `마지막 업데이트: ${data.success.timestamp || data.success?.timestamp || '-'} `;

            // create cards
            list.forEach(item => {
                const card = document.createElement('div');
                card.className = `track-card ${signature[source].colorClass}`;

                // rank
                const rankBadge = document.createElement('div');
                rankBadge.className = 'rank-badge';
                rankBadge.innerHTML = `
            <div class="rank-num">${escapeHtml(String(item.rank))}</div>
            <div class="rank-status ${escapeHtml(String(item.rankStatus))}">
              ${statusIcon(item.rankStatus, item.changedRank)}
              <span style="font-weight:600; font-size:.8rem;">${escapeHtml(String(item.rankStatus === 'static' ? '-' : (item.changedRank || 0)))}</span>
            </div>
          `;

                // album art
                const album = document.createElement('div');
                album.className = 'album';
                album.innerHTML = `<img src="${escapeHtml(item.albumArt)}" alt="${escapeHtml(item.albumName)}" loading="lazy">`;

                // meta
                const meta = document.createElement('div');
                meta.className = 'meta';
                meta.innerHTML = `
            <div class="title">${escapeHtml(item.title)}</div>
            <div class="artist">${escapeHtml(item.artistName)}</div>
            <div class="small-muted">${escapeHtml(item.albumName)}</div>
          `;

                // right small text
                const right = document.createElement('div');
                right.className = 'text-end small-muted';
                right.style.minWidth = '90px';
                right.innerHTML = `
            <div>Song #${escapeHtml(String(item.songNumber))}</div>
            <div style="font-size:.82rem;">&nbsp;</div>
          `;

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
        function statusIcon(status, changed){
            if (status === 'up'){
                return `<i class="bi bi-arrow-up-circle-fill" style="color:#198754;font-size:1.0rem"></i>`;
            } else if (status === 'down'){
                return `<i class="bi bi-arrow-down-circle-fill" style="color:#d6333f;font-size:1.0rem"></i>`;
            } else {
                return `<i class="bi bi-dash-circle-fill" style="color:#6c757d;font-size:1.0rem"></i>`;
            }
        }

        function escapeHtml(s){
            if (s === null || s === undefined) return '';
            return String(s)
                .replaceAll('&','&amp;')
                .replaceAll('<','&lt;')
                .replaceAll('>','&gt;')
                .replaceAll('"','&quot;')
                .replaceAll("'",'&#39;');
        }

        // main loader
        function loadChart(source){
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
</script>
</body>
</html>
