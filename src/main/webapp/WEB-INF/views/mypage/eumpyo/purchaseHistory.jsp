<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%
	String ctxPath = request.getContextPath();
%> 

<html>
<jsp:include page="../../include/common/head.jsp" />
<link rel="stylesheet" href="../../css/mypage.css" />

<style>
 .uselist {
 	margin: 56px 0;
 }

</style>

<script type="text/javascript">
(function(){
    const ctx  = '${pageContext.request.contextPath}';
    const API  = ctx + '/mypage/eumpyo/history/purchaseHistory';
    let page   = 1;
    let size   = 10;

    // snake_case / camelCase 대응 유틸
    const pick = (obj, keys) => { for (const k of keys) if (obj && obj[k] != null) return obj[k]; };

    const $tbody = document.getElementById('usageTbody');
    const $all   = document.getElementById('cartAllCheck');
    const $selCount = document.getElementById('selCount');
    const $selCoins = document.getElementById('selCoins');
    const $pagination = document.getElementById('usagePagination');
    const $btnViewSelected = document.getElementById('btnViewSelected');

    function toNum(n){ const x = Number(n); return Number.isFinite(x) ? x : 0; }
    function fmtNum(n){ try { return Number(n).toLocaleString(); } catch(e){ return n; } }
    function ymd(dt){ if(!dt) return ''; return (String(dt)).substring(0,10); }

    function mapItem(raw, idx, baseIndex){
        const coverUrl = pick(raw, ['coverUrl','coverurl','thumbnail']) || '/static/images/common/no_image.png';
        return {
            no: baseIndex + idx + 1,
            id: pick(raw, ['musicId','musicid','id']),
            title: pick(raw, ['title','music_title','musicTitle']) || '(제목 미상)',
            artist: pick(raw, ['artist','artist_name','artistName']) || '',
            album: pick(raw, ['album','album_title','albumTitle']) || '',
            usedCoin: toNum(pick(raw, ['usedCoin','usedcoin','coin'])),
            purchasedAt: ymd(pick(raw, ['purchasedAt','purchasedat','usedAt','usedat','createdAt','createdat'])),
            coverUrl
        };
    }

    function renderRows(items, totalCount){
        $tbody.innerHTML = '';
        if (!items || items.length === 0){
            $tbody.innerHTML = `<tr><td colspan="7" style="text-align:center;color:#8a8a8a;padding:22px;">구매내역이 없습니다.</td></tr>`;
            return;
        }
        const baseIndex = (page - 1) * size;
        const rows = items.map((raw, i) => {
            const it = mapItem(raw, i, baseIndex);
            return `
            <tr data-id="${it.id}" data-coins="${it.usedCoin}">
                <td>
                    <input type="checkbox" class="row-check">
                </td>
                <td scope="row">${it.no}</td>
                <td>
                    <div class="music-info">
                        <div class="music-img">
                            <img src="${it.coverUrl}" alt="노래 이미지" />
                        </div>
                        <p class="music-text" title="${it.title}">${it.title}</p>
                    </div>
                </td>
                <td><p class="music-artist" title="${it.artist}">${it.artist}</p></td>
                <td><p class="music-artist" title="${it.album}">${it.album}</p></td>
                <td><p class="music-text">${fmtNum(it.usedCoin)}음표</p></td>
                <td>
                    <button type="button" class="btn-cart-detail" title="상세 보기"></button>
                </td>
            </tr>`;
        }).join('');
        $tbody.innerHTML = rows;

        // 이벤트 바인딩
        $tbody.querySelectorAll('.row-check').forEach(chk => chk.addEventListener('change', updateSelection));
        $tbody.querySelectorAll('.btn-cart-detail').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const tr = e.currentTarget.closest('tr');
                const musicId = tr?.dataset?.id;
                // 상세 보기 로직(모달/새창 등) — 필요 시 구현
                alert('상세 보기: ' + (musicId || '(알 수 없음)'));
            });
        });

        // 전체 체크 초기화
        $all.checked = false;
        updateSelection();
    }

    function renderPagination(totalCount){
        $pagination.innerHTML = '';
        const totalPages = Math.max(1, Math.ceil((totalCount || 0)/size));
        const mk = (label, p, disabled=false, active=false) => {
            if (disabled) return `<span style="opacity:.5;pointer-events:none;border:1px solid #e0e0e0;padding:6px 10px;border-radius:6px;">${label}</span>`;
            if (active)   return `<span style="font-weight:700;border:1px solid #ccc;padding:6px 10px;border-radius:6px;">${label}</span>`;
            return `<a href="#" data-page="${p}" style="border:1px solid #e0e0e0;padding:6px 10px;border-radius:6px;text-decoration:none;color:#5A5A5A;">${label}</a>`;
        };
        const firstDis = page<=1, lastDis = page>=totalPages;
        $pagination.insertAdjacentHTML('beforeend', mk('«', 1, firstDis));
        $pagination.insertAdjacentHTML('beforeend', mk('‹', Math.max(1, page-1), firstDis));

        const start = Math.max(1, page-2);
        const end   = Math.min(totalPages, page+2);
        for (let p=start; p<=end; p++){
            $pagination.insertAdjacentHTML('beforeend', mk(String(p), p, false, p===page));
        }

        $pagination.insertAdjacentHTML('beforeend', mk('›', Math.min(totalPages, page+1), lastDis));
        $pagination.insertAdjacentHTML('beforeend', mk('»', totalPages, lastDis));

        $pagination.querySelectorAll('a[data-page]').forEach(a=>{
            a.addEventListener('click', (e)=>{
                e.preventDefault();
                const np = Number(e.currentTarget.dataset.page);
                if (!Number.isNaN(np) && np !== page){
                    page = np;
                    fetchAndRender();
                }
            });
        });
    }

    function updateSelection(){
        const checks = $tbody.querySelectorAll('.row-check');
        let cnt = 0, sum = 0;
        checks.forEach(chk=>{
            if (chk.checked){
                cnt++;
                const tr = chk.closest('tr');
                sum += toNum(tr?.dataset?.coins);
            }
        });
        $selCount.textContent = `${cnt}곡`;
        $selCoins.textContent = `${fmtNum(sum)}음표`;
    }

    function fetchAndRender(){
        fetch(`${API}?page=${page}&size=${size}`, { method:'GET', headers:{ 'Accept':'application/json' }})
        .then(r => r.json())
        .then(res => {
            if (!res || res.result !== 'success'){
                renderRows([], 0);
                renderPagination(0);
                alert(res?.message || '구매내역 조회 실패');
                return;
            }
            const data  = res.responseData || res.data || {};
            const items = data.items || data.list || [];
            const total = (data.totalCount ?? data.total ?? 0);
            // 상단 보유 음표 갱신(백엔드에서 내려줄 경우)
            const balance = (data.currentBalance ?? data.balance);
            if (balance != null){
                const node = document.querySelector('.mypage-point .real-point');
                if (node) node.textContent = (Number(balance).toLocaleString?.() || balance);
            }
            renderRows(items, total);
            renderPagination(total);
        })
        .catch(() => {
            renderRows([], 0);
            renderPagination(0);
            alert('네트워크 오류');
        });
    }

    // 전체 체크
    $all.addEventListener('change', function(){
        $tbody.querySelectorAll('.row-check').forEach(chk => { chk.checked = $all.checked; });
        updateSelection();
    });

    // 선택 상세 보기 (예시)
    $btnViewSelected.addEventListener('click', function(){
        const ids = [];
        $tbody.querySelectorAll('.row-check:checked').forEach(chk=>{
            const tr = chk.closest('tr');
            if (tr?.dataset?.id) ids.push(tr.dataset.id);
        });
        if (ids.length === 0) { alert('선택된 항목이 없습니다.'); return; }
        alert('선택 상세 보기: \n' + ids.join('\n'));
        // 필요 시 모달/새창으로 상세 조회 구현
    });

    // 초기 로드
    fetchAndRender();
})();
</script>


<body>
	<div id="wrap">
    <main id="musicCart"><%-- cart 외형 재사용 --%>
        <%-- 왼쪽 사이드 네비게이션 & 관련 팝업들 --%>
        <jsp:include page="../../include/common/asideNavigation.jsp" />
        <%-- //왼쪽 사이드 네비게이션 & 관련 팝업들 --%>

        <!-- 메인 컨텐츠 -->
        <div class="main-contents">
            <div class="inner">
                <%-- 보유 음표 영역 --%>
                <jsp:include page="../../include/mypage/mypagePointInfo.jsp" />
                <%-- //보유 음표 영역 --%>

                <%-- ====== 상단 툴바 (라벨만 변경) ====== --%>
                <div class="music-cart-top">
                    <div class="btn-form">
                        <!-- 구매내역은 보통 삭제/결제 없음 → “선택 상세 보기” 정도로 대체 -->
                        <button type="button" class="btn btn-payment" id="btnViewSelected"><!-- CHANGED -->
                            선택 상세 보기
                        </button>
                    </div>
                    <div class="music-check-info">
                        <div>
                            <p class="title">선택 곡 수 : </p>
                            <p class="text" id="selCount">0곡</p><!-- CHANGED(동적) -->
                        </div>
                        <div>
                            <p class="title point">총 사용 음표 : </p>
                            <p class="text" id="selCoins">0음표</p><!-- CHANGED(동적) -->
                        </div>
                    </div>
                </div>

                <table class="music-cart-table table">
                    <thead>
                        <tr>
                            <th scope="col">
                                <label for="cartAllCheck">
                                    <input type="checkbox" id="cartAllCheck">
                                </label>
                            </th>
                            <th scope="col">번호</th>
                            <th scope="col">노래제목</th>
                            <th scope="col">아티스트</th>
                            <th scope="col">앨범</th>
                            <th scope="col">가격</th>
                            <th scope="col">삭제</th>
                        </tr>
                    </thead>
                    <tbody id="usageTbody"><!-- CHANGED: 동적 렌더 대상 -->
                        <%-- 데이터는 JS로 렌더링 --%>
                    </tbody>
                </table>

                <%-- 페이지네이션(필요 시) --%>
                <nav id="usagePagination" aria-label="pagination" style="display:flex;gap:4px;justify-content:center;margin:16px 0 32px;">
                </nav>
                <%-- 구매내역 리스트 --%>
            </div>
        </div>
        <!-- //메인 컨텐츠 -->

        <%-- 오늘의 감정 플레이리스트 --%>
        <jsp:include page="../../include/common/asidePlayList.jsp" />
        <%-- //오늘의 감정 플레이리스트 --%>
    </main>
</div>
</body>
</html>