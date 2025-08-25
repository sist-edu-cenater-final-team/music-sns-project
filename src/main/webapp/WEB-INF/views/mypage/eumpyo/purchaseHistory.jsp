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

<!-- mypage-point 용 -->
<link rel="stylesheet" href="../../css/mypage.css" />

<style>
.purchaseList {
	margin: 56px 0;
}


/* 헤더(항목 영역)도 카드처럼 보이게 */
.purchaseHistory .table thead th { /* 수정 */
  padding: 20px;
  font-size: 16px;
  font-weight: 500;
  color: black;
  white-space: nowrap;
  text-align: center;

  background: #F3F2F8;              /* 유지 */
  border-top: 1px solid #E6E6E8;     /* 추가 */
  border-bottom: 1px solid #E6E6E8;  /* 추가 */
  border-left: none;                 /* 추가 */
  border-right: none;                /* 추가 */
}

/* 헤더 좌/우 끝 셀에만 둥근 모서리 + 좌우 테두리 */
.purchaseHistory .table thead th:first-child { /* 추가 */
  border-top-left-radius: 10px;
  border-bottom-left-radius: 10px;
  border-left: 1px solid #E6E6E8;
}
.purchaseHistory .table thead th:last-child { /* 추가 */
  border-top-right-radius: 10px;
  border-bottom-right-radius: 10px;
  border-right: 1px solid #E6E6E8;
}

/* =========================
   테이블: 행 카드형(최소 수정)
   ========================= */
/* (수정) 카드형을 위해 separate + 행 간격 부여 */
.purchaseHistory .table { /* 수정 */
  table-layout: fixed;
  width: 100%;
  word-wrap: break-word;
  border-collapse: separate;   /* 수정: collapse -> separate */
  border-spacing: 0 8px;       /* 수정: 행 간격(세로) */
}

/* 헤더(상단 바) */
.purchaseHistory .table thead th {
  padding: 20px;
  font-size: 16px;
  font-weight: 500;
  border: none;
  color: black;
  white-space: nowrap;
  text-align: center;
  background-color: #F3F2F8;
}

/* 셀 정렬 */
.purchaseHistory .table thead th,
.purchaseHistory .table tbody td {
  text-align: center;
  vertical-align: middle;
}

/* (수정) 각 행을 카드처럼 보이게: td 배경/테두리 적용 */
.purchaseHistory .table tbody td { /* 수정 */
  padding: 15px;
  font-size: 15px;
  font-weight: 500;
  color: #000;
  background: #fff;              /* 수정 */
  border-top: 1px solid #E6E6E8; /* 수정 */
  border-bottom: 1px solid #E6E6E8; /* 수정 */
  /* 좌우 테두리는 기본 제거 -> 첫/마지막 셀에서만 살림 */
  border-left: none;             /* 수정 */
  border-right: none;            /* 수정 */
}

/* (수정) 행의 첫/마지막 셀에만 radius + 좌우 테두리 */
.purchaseHistory .table tbody tr td:first-child { /* 수정 */
  border-top-left-radius: 10px;
  border-bottom-left-radius: 10px;
  border-left: 1px solid #E6E6E8;
}
.purchaseHistory .table tbody tr td:last-child { /* 수정 */
  border-top-right-radius: 10px;
  border-bottom-right-radius: 10px;
  border-right: 1px solid #E6E6E8;
}

/* (수정) hover 시 행 전체가 카드처럼 강조되도록 td 배경만 변경 */
.purchaseHistory .table.table-hover tbody tr:hover td { /* 수정 */
  background: #FAFAFB;
}

/* 마지막 열 폭 고정 */
.purchaseHistory .table th:last-child,
.purchaseHistory .table td:last-child {
  width: 120px;
  white-space: nowrap;
}

/* 사용된 음표 색상 */
.purchaseHistory .table tbody td.col-usedCoin {
	color: #AE1932;
}

/* 음악 셀 내부 레이아웃 */
.music-info {
	display: flex;
	align-items: center;
	gap: 15px;
	justify-content: left;
	padding-left: 15px;
	min-width: 0;
}
.music-img {
	width: 30px;
	height: 30px;
	border-radius: 6px;
	overflow: hidden;
	background: #eee;
	flex: none;
}
.music-img img {
	width:30px;
	height: 30px;
	object-fit: cover;
	display: block;
}
.music-text {
	display: flex;
	align-items: center;
	gap: 6px;
	min-width: 0; 
}
.music-text .title-ellipsis {
	flex: 1 1 auto;
	min-width: 0;
	white-space: nowrap;
	overflow: hidden;
	text-overflow: ellipsis;
}
.music-text .extra-count {
	flex: none;
	white-space: nowrap;
}
.music-artist,
.music-album {
	margin: 0;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
	max-width: 320px;
}

/* 열 폭 */
.col-no             { width: 5%;  }
.col-date           { width: 25%; }
.col-purchasedetail { width: 40%; white-space: nowrap; }
.col-usedCoin       { width: 20%; white-space: nowrap; }
.col-balance        { width: 10%; white-space: nowrap; }

/* 페이지바 */
#pagination {
	margin: 30px auto;
	display: flex;
	justify-content: center;
}
#pagination .pg-bar {
	list-style: none;
	padding: 0;
	margin: 0;
	display: flex;
	align-items: center;
	font-variant-numeric: tabular-nums;
}
#pagination .pg-item--prev { margin-right: 10px; }
#pagination .pg-item--next { margin-left: 10px; }
#pagination a {
	color: #8A8A8A;
	text-decoration: none;
	font-weight: 500;
}
#pagination a:hover,
#pagination a:active {
	color: #5A5A5A;
}
#pagination .is-current .pg-current {
	border-color: #5A5A5A;
	color: #5A5A5A;
	background: #fff;
	font-weight: 700;
}
/* 숫자 */
#pagination .pg-item--num .pg-link,
#pagination .pg-item--num .pg-current {
	display: inline-block;
	padding: 6px 9px;
	border: 1px solid transparent;
	box-sizing: border-box;
	min-width: 28px;
	text-align: center;
	line-height: 1;
}
/* 비활성 */
#pagination .is-disabled { opacity: 0.3; }
#pagination .is-disabled a {
	pointer-events: none;
	cursor: default;
}
/* 화살표(« ‹ › ») */
#pagination .pg-item--first a,
#pagination .pg-item--prev  a,
#pagination .pg-item--next  a,
#pagination .pg-item--last  a,
#pagination .pg-item--first span,
#pagination .pg-item--prev  span,
#pagination .pg-item--next  span,
#pagination .pg-item--last  span {
	display: inline-block;
	width: 24px;
	text-align: center;
	padding-bottom: 2px;
	line-height: 1;
	font-size: 25px;
	font-weight: 700;
}

.purchase-history-row { cursor: pointer; }
</style>


<script type="text/javascript">
(function($){
  const ctxPath = '<%= ctxPath %>';

  /* CORS/세션-쿠키가 정말 필요한 경우만 true 로 */
  const USE_CREDENTIALS = false;

  /* URL 파라미터 읽기 (초기 page/size 동기화) */
  function getParam(name, def) {
    const u = new URL(window.location.href);
    const v = u.searchParams.get(name);
    if (!v) return def;
    const n = parseInt(v,10);
    return isNaN(n) ? def : n;
  }

  /* 클라이언트 상태 */
  const state = {
    page: getParam('page', 1),
    size: getParam('size', 10),
    totalCount: 0
  };

  function fmtNum(n){ try { return Number(n||0).toLocaleString(); } catch(e){ return n; } }

  /* 상세 음악 렌더 */
  function renderMusic($detailRow, purchaseMusic) {
    const $content = $detailRow.find('.detail-content');
    if (!purchaseMusic || purchaseMusic.length === 0) {
      $content.html('<div>구매한 음악이 없습니다.</div>');
      return;
    }

    var html = '<ul style="margin:0; padding:0; list-style:none; display:grid; grid-template-columns:repeat(2, minmax(0,1fr)); gap:10px;">';
    purchaseMusic.forEach(function(t){
      const img = t.albumImageUrl ?
        ('<img src="' + t.albumImageUrl + '" alt="' + (t.musicName || t.musicId || '-') + '" style="width:44px;height:44px;border-radius:6px;object-fit:cover;" />') :
        ('<img src="' + ctxPath + '/images/mypage/noAlbumImage.png" alt="-" style="width:44px;height:44px;border-radius:6px;object-fit:cover;" />');

      const title = (t.musicName || t.musicId || '-');
      const artist = (t.artistName || '');
      const album  = (t.albumName  || '');
      const used   = (typeof t.usedCoin === 'number') ? t.usedCoin : (t.usedCoin || '');

      html += ''
        + '<li style="display:flex;align-items:center;gap:12px;padding:8px 10px;border:1px solid #eee;border-radius:8px;background:#fff;">'
        +   '<div>' + img + '</div>'
        +   '<div style="min-width:0; flex:1;">'
        +     '<div style="font-weight:700; white-space:nowrap; overflow:hidden; text-overflow:ellipsis;">' + title + '</div>'
        +     '<div style="font-size:12px; color:#666; white-space:nowrap; overflow:hidden; text-overflow:ellipsis;">' + artist + (artist && album ? ' · ' : '') + album + '</div>'
        +   '</div>'
        +   '<div style="font-variant-numeric:tabular-nums; white-space:nowrap;">-' + used + ' 음표</div>'
        + '</li>';
    });
    html += '</ul>';
    $content.html(html);
  }

  /* tbody 렌더 */
  function buildRows(list, totalCount, page, size){
    const $tbody = $('#purchaseTbody');
    $tbody.empty();

    if (!list || list.length === 0) {
      $tbody.append('<tr><td colspan="5" class="text-center">구매내역이 없습니다.</td></tr>');
      return;
    }

    list.forEach(function(row, idx){
      const no   = totalCount - ((page-1) * size) - idx;
      const pid  = row.purchaseHistoryId;
      const date = row.purchasedAt || '-';

      const titleSummary = row.titleSummary || row.musicName || '-';
      var mainTitle = titleSummary;
      var extraText = '';
      if (titleSummary && titleSummary.indexOf(' 외 ') > -1) {
        mainTitle = titleSummary.split(' 외 ')[0];
        extraText = '외 ' + titleSummary.split(' 외 ')[1];
      }

      const imgTag = row.albumImageUrl
        ? ('<img src="' + row.albumImageUrl + '" alt="' + (row.musicName||'-') + '" width="48" height="48" />')
        : ('<img src="' + ctxPath + '/images/mypage/noAlbumImage.png" alt="앨범 이미지 없음" width="48" height="48" class="album-placeholder" />');

      const usedCoin = (row.usedCoin != null) ? ('-' + fmtNum(row.usedCoin) + ' 음표') : '-';
      const balance  = (row.coinBalance != null) ? (fmtNum(row.coinBalance) + ' 음표') : '-';

      var mainRow = ''
        + '<tr class="purchase-history-row" data-pid="' + pid + '">'
        +   '<td class="col-no">' + no + '</td>'
        +   '<td class="col-date">' + date + '</td>'
        +   '<td class="col-purchasedetail">'
        +     '<div class="music-info">'
        +       '<div class="music-img">' + imgTag + '</div>'
        +       '<div class="music-text">'
        +         '<span class="title-ellipsis">' + mainTitle + '</span>'
        +         (extraText ? '<span class="extra-count">' + extraText + '</span>' : '')
        +       '</div>'
        +     '</div>'
        +   '</td>'
        +   '<td class="col-usedCoin">' + usedCoin + '</td>'
        +   '<td class="col-balance">' + balance + '</td>'
        + '</tr>';

      var detailRow = ''
        + '<tr class="purchase-detail" data-pid="' + pid + '" style="display:none;">'
        +   '<td colspan="5">'
        +     '<div class="detail-container" style="padding:16px;">'
        +       '<div class="detail-loading" style="display:none;">불러오는 중...</div>'
        +       '<div class="detail-content"></div>'
        +     '</div>'
        +   '</td>'
        + '</tr>';

      $tbody.append(mainRow);
      $tbody.append(detailRow);
    });
  }

  /* 페이지바 렌더 */
  function buildPagination(totalCount, size, current){
    const $pg = $('#pagination');
    $pg.empty();

    const totalPage = Math.max(1, Math.ceil(totalCount / Math.max(1, size)));
    const blockSize = 5;
    const startNo = Math.floor((current-1)/blockSize)*blockSize + 1;
    const endNo = Math.min(startNo + blockSize - 1, totalPage);

    var html = '<ul class="pg-bar">';

    // « 맨처음
    if (current > blockSize) {
      html += '<li class="pg-item pg-item--first"><a class="pg-link pg-link--icon pg-first" href="#" data-page="1">&laquo;</a></li>';
    }

    // ‹ 이전
    if (current > 1) {
      html += '<li class="pg-item pg-item--prev"><a class="pg-link pg-link--icon pg-prev" href="#" data-page="' + (current-1) + '">&lsaquo;</a></li>';
    } else {
      html += '<li class="pg-item pg-item--prev is-disabled"><span class="pg-current">&lsaquo;</span></li>';
    }

    // 숫자
    for (var p = startNo; p <= endNo; p++) {
      if (p === current) {
        html += '<li class="pg-item pg-item--num is-current"><span class="pg-current" aria-current="page">' + p + '</span></li>';
      } else {
        html += '<li class="pg-item pg-item--num"><a class="pg-link" href="#" data-page="' + p + '">' + p + '</a></li>';
      }
    }

    // › 다음
    if (current < totalPage) {
      html += '<li class="pg-item pg-item--next"><a class="pg-link pg-link--icon pg-next" href="#" data-page="' + (current+1) + '">&rsaquo;</a></li>';
    } else {
      html += '<li class="pg-item pg-item--next is-disabled"><span class="pg-current">&rsaquo;</span></li>';
    }

    // » 마지막
    if (current <= totalPage - blockSize) {
      html += '<li class="pg-item pg-item--last"><a class="pg-link pg-link--icon pg-last" href="#" data-page="' + totalPage + '">&raquo;</a></li>';
    }

    html += '</ul>';
    $pg.html(html);
  }

  /* 서버에서 페이지 데이터 로드 (401 자동재시도) */
  function loadPage(page, size){
    return AuthFunc.apiRequest(() =>
      // [MOD axios->ajax] 목록 호출
      $.ajax({
        url: ctxPath + '/api/mypage/eumpyo/history/purchase',
        method: 'GET',
        data: { page: page, size: size },      // axios params → $.ajax data
        headers: AuthFunc.getAuthHeader(),
        dataType: 'json',
        xhrFields: USE_CREDENTIALS ? { withCredentials: true } : undefined
      })
    ).then(function(r){
      // [MOD axios->ajax] $.ajax 는 data 래핑 없음 → r 자체가 payload
      const data = r || {};
      if (data.result !== 'success') throw new Error('fail');

      state.page = data.page || page;
      state.size = data.size || size;
      state.totalCount = data.totalCount || 0;

      buildRows(data.list || [], state.totalCount, state.page, state.size);
      buildPagination(state.totalCount, state.size, state.page);

      /* 잔액도 동기화 */
      return AuthFunc.apiRequest(() =>
        // [MOD axios->ajax] 잔액 호출
        $.ajax({
          url: ctxPath + '/api/mypage/eumpyo/charge/balance',
          method: 'GET',
          headers: AuthFunc.getAuthHeader(),
          dataType: 'json',
          xhrFields: USE_CREDENTIALS ? { withCredentials: true } : undefined
        })
      ).then(function(rb){
        // [MOD axios->ajax] $.ajax 응답 그대로 사용
        if (rb && rb.result === 'success') {
          $('#myCoinBalance').text((rb.coinBalance || 0).toLocaleString());
        }
      }).catch(function(){});
    });
  }

  /* 페이지바 클릭(위임) */
  $(document).on('click', '#pagination a.pg-link', function(e){
    e.preventDefault();
    const p = parseInt($(this).data('page'), 10);
    if (isNaN(p)) return;
    loadPage(p, state.size);

    // 주소표시줄 page 동기화 (선택)
    const u = new URL(window.location.href);
    u.searchParams.set('page', p);
    u.searchParams.set('size', state.size);
    window.history.replaceState({}, '', u.toString());
  });

  /* 상세행 토글 + 상세 AJAX (401 자동재시도 + 인증헤더) */
  $(document).on('click', '.purchase-history-row', function(){
    const $row = $(this);
    const pid  = $row.data('pid');
    const $detailRow = $('.purchase-detail[data-pid="'+pid+'"]');

    // 이미 로드된 경우 토글만
    if ($detailRow.data('loaded') === true) {
      $detailRow.toggle();
      return;
    }

    // 최초 로드
    $detailRow.show();
    const $loading = $detailRow.find('.detail-loading');
    $loading.show();

    AuthFunc.apiRequest(() =>
      // [MOD axios->ajax] 상세 목록 호출
      $.ajax({
        url: ctxPath + '/api/mypage/eumpyo/history/purchase/' + pid + '/purchaseMusic',
        method: 'GET',
        headers: AuthFunc.getAuthHeader(),
        dataType: 'json',
        xhrFields: USE_CREDENTIALS ? { withCredentials: true } : undefined
      })
    ).then(function(resp){
      $loading.hide();
      // [MOD axios->ajax] $.ajax 응답 그대로 사용
      if (!resp || resp.result !== 'success') {
        $detailRow.find('.detail-content').html('<div>목록을 불러오지 못했습니다.</div>');
        return;
      }
      renderMusic($detailRow, resp.purchaseMusic || []);
      $detailRow.data('loaded', true);
    }).catch(function(){
      $loading.hide();
      $detailRow.find('.detail-content').html('<div>오류가 발생했습니다.</div>');
    });
  });

  /* 초기 로드 */
  $(function(){
    loadPage(state.page, state.size).catch(function(){
      $('#purchaseTbody').html('<tr><td colspan="5" class="text-center">목록을 불러오지 못했습니다.</td></tr>');
    });
  });

})(jQuery);
</script>

<body>
    <div id="wrap">
        <main class="purchaseHistory">
            <%-- 왼쪽 사이드 네비게이션 & 관련 팝업들 --%>
            <jsp:include page="../../include/common/asideNavigation.jsp" />

            <%-- 메인 컨텐츠 시작 --%>
            <div class="main-contents">
                <div class="inner">
                    <%-- 보유 음표 영역 --%>
                    <jsp:include page="../../include/mypage/mypagePointInfo.jsp" />

                    <%-- 음표 구매 탭 --%>
                    <jsp:include page="../../include/mypage/eumpyoChargeTab.jsp" />

                    <!-- 구매내역 리스트 시작 -->
                    <table class="table table-hover purchaseList">
                        <thead>
                            <tr>
                                <th scope="col" class="col-no">번호</th>
                                <th scope="col" class="col-date">구매일자</th>
                                <th scope="col" class="col-purchasedetail">구매내역</th>
                                <th scope="col" class="col-usedCoin">사용된 음표</th>
                                <th scope="col" class="col-balance">구매 후 음표</th>
                            </tr>
                        </thead>
                        <tbody id="purchaseTbody"><!-- 클라이언트 렌더 표적 -->
                            <c:set var="list" value="${requestScope.list}" />
                            <c:choose>
                                <c:when test="${not empty list}">
                                    <c:forEach var="row" items="${list}" varStatus="st">
                                        <c:set var="no" value="${requestScope.totalCount - (requestScope.currentShowPageNo - 1) * requestScope.sizePerPage - st.index}" />
                                        <tr class="purchase-history-row" data-pid="${row.purchaseHistoryId}">
                                            <td class="col-no"><c:out value="${no}" /></td>
                                            <td class="col-date"><c:out value="${row.purchasedAt}" /></td>
                                            <td class="col-purchasedetail">
                                                <div class="music-info">
                                                    <div class="music-img">
                                                        <c:choose>
                                                            <c:when test="${not empty row.albumImageUrl}">
                                                                <img src="${row.albumImageUrl}" alt="${fn:escapeXml(row.musicName)}" width="48" height="48" />
                                                            </c:when>
                                                            <c:otherwise>
                                                                <img src="<%= ctxPath%>/images/mypage/noAlbumImage.png" alt="앨범 이미지 없음" width="48" height="48" class="album-placeholder" />
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                    <div class="music-text">
                                                        <c:set var="ts" value="${row.titleSummary}" />
                                                        <span class="title-ellipsis">
                                                            <c:choose>
                                                                <c:when test="${not empty ts}">
                                                                    <c:choose>
                                                                        <c:when test="${fn:contains(ts, ' 외 ')}">
                                                                            <c:out value="${fn:substringBefore(ts, ' 외 ')}" />
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <c:out value="${ts}" />
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <c:out value="${row.musicName}" default="-" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </span>
                                                        <c:if test="${not empty ts and fn:contains(ts, ' 외 ')}">
                                                            <span class="extra-count">
                                                                외 <c:out value="${fn:substringAfter(ts, ' 외 ')}" />
                                                            </span>
                                                        </c:if>
                                                    </div>
                                                </div>
                                            </td>
                                            <td class="col-usedCoin">
                                                <c:choose>
                                                    <c:when test="${not empty row.usedCoin}">
                                                        <fmt:formatNumber value="${0 - row.usedCoin}" pattern="#,##0; -#,##0" /> 음표
                                                    </c:when>
                                                    <c:otherwise>-</c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="col-balance">
                                                <c:choose>
                                                    <c:when test="${not empty row.coinBalance}">
                                                        <fmt:formatNumber value="${row.coinBalance}" pattern="#,###" /> 음표
                                                    </c:when>
                                                    <c:otherwise>-</c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                        <tr class="purchase-detail" data-pid="${row.purchaseHistoryId}" style="display:none;">
                                            <td colspan="5">
                                                <div class="detail-container" style="padding:16px;">
                                                    <div class="detail-loading" style="display:none;">불러오는 중...</div>
                                                    <div class="detail-content"></div>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="5" class="text-center">구매내역이 없습니다.</td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                    <%-- 구매내역 리스트 끝 --%>

                    <!-- 페이지바 -->
                    <div id="pagination">
                        ${requestScope.pageBar}
                    </div>
                </div>
            </div>
            <%-- 메인 컨텐츠 끝 --%>

            <%-- 오늘의 감정 플레이리스트 --%>
            <jsp:include page="../../include/common/asidePlayList.jsp" />
        </main>
    </div>
</body>
</html>