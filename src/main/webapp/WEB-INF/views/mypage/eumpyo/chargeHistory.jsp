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
.chargeList {
	margin: 56px 0;
}

.chargeHistory .table {
	table-layout: fixed;
	font-variant-numeric: tabular-nums; /* 모든 숫자 폭을 동일하게 만들어 정렬 안정화 */
}

.chargeHistory .table thead th {
	border-top: 1px solid #E0E0E0;
	border-bottom: 1px solid #E0E0E0;
	padding: 12px 12px;
	font-size: 16px;
	font-weight: 700;
	color: #8A8A8A;
	white-space: nowrap; /* 헤더 줄바꿈 방지 */
}

.chargeHistory .table thead th,
.chargeHistory .table tbody td {
	text-align: center;
	vertical-align: middle;
}

.chargeHistory .table tbody td {
	padding: 15px 15px;
	font-size: 15px;
	font-weight: 500;
	color: #000;
	border-top: solid 1px #E0E0E0;
}

/* 최소 수정: 색상만 유지 */
.chargeHistory .table tbody td.col-coin {
	color: #2A52BE;
}

.chargeHistory .table.table-hover tbody tr:hover {
	background: #FAFAFA;
}

/* 열 폭(고정 레이아웃과 함께 사용) */
.col-no     { width: 5%;  }
.col-date   { width: 30%; }
.col-coin   { width: 25%; white-space: nowrap; }
.col-after  { width: 30%; white-space: nowrap; }
.col-amount { width: 10%; white-space: nowrap; }

/* 페이지바 컨테이너 */
#pagination {
	margin: 30px auto;
	display: flex;
	justify-content: center;
}
/*
// 페이지바 공통
#pagination .pg-bar {
	list-style: none;
	padding: 0;
	margin: 0;
	display: flex;
	align-items: center;
	font-variant-numeric: tabular-nums;
}

#pagination .pg-item--prev {
	margin-right: 10px;
}

#pagination .pg-item--next {
	margin-left: 10px;
}


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

// 숫자 칩 공통(링크/현재) : 크기/라인 고정으로 흔들림 방지
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

// 비활성 스타일 표시
#pagination .is-disabled {
	opacity: 0.2;
}

#pagination .is-disabled a {
	pointer-events: none; // 마우스만 차단
	cursor: default;
}

// 화살표(« ‹ › »)
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
*/

/* [ADD] SSR/JS 공용 버튼 스타일 */
.pg-link, .pg-current, .pg-text {
    display: inline-block;
    padding: 6px 9px;
    min-width: 28px;
    line-height: 1;
    text-align: center;
    text-decoration: none;
    border: 1px solid transparent;
    box-sizing: border-box;
    color: #8A8A8A;
    font-weight: 500;
    background: transparent;
}
.pg-link:hover { color: #5A5A5A; }

/* [ADD] 현재 페이지 강조 */
.pg-item--num .pg-current {
    border-color: #5A5A5A;
    color: #5A5A5A;
    background: #fff;
    font-weight: 700;
    border-radius: 6px;
}

/* [ADD] 비활성 상태 */
.pg-item.is-disabled,
.pg-item.is-disabled .pg-text { opacity: .35; }
.pg-item.is-disabled a { pointer-events: none; cursor: default; }

/* [ADD] 화살표 아이콘 baseline 보정 */
.pg-text--icon, .pg-link--icon {
    width: 24px;
    font-size: 24px;
    padding-bottom: 2px;
}
.pg-bar .pg-item { display: inline-block; }
</style>

<script type="text/javascript">
    (function($){
        const ctxPath = '<%= ctxPath %>';

        /* [ADD] CORS/세션-쿠키 의존 시에만 true
           - 동일 출처 + Bearer 토큰 인증이면 false 권장 */
        const USE_CREDENTIALS = false;

        // [ADD] URL 파라미터 읽기
        function getParam(name, def) {
            const u = new URL(window.location.href);
            const v = u.searchParams.get(name);
            if (!v) return def;
            const n = parseInt(v,10);
            return isNaN(n) ? def : n;
        }

        const state = {
            page: getParam('page', 1),
            size: getParam('size', 10),
            totalCount: 0
        };

        function fmtNum(n){ try { return Number(n||0).toLocaleString(); } catch(e){ return n; } }

        // [CHANGE] 백틱 템플릿 → 문자열 연결
        function buildRows(list, totalCount, page, size){
            const $tbody = $('#chargeTbody'); // [MOD] 표적 tbody id 사용
            $tbody.empty();

            if (!list || list.length === 0) {
                $tbody.append('<tr><td colspan="5" class="text-center">충전내역이 없습니다.</td></tr>');
                return;
            }

            list.forEach(function(row, idx){
                const no = totalCount - ((page-1) * size) - idx;
                const chargedAt   = row.chargedAt || '-';
                const chargedCoin = row.chargedCoin != null ? ('+'+fmtNum(row.chargedCoin)+' 음표') : '-';
                const after       = row.coinBalance != null ? (fmtNum(row.coinBalance)+' 음표') : '-';
                const paid        = row.paidAmount != null ? (fmtNum(row.paidAmount)+'원') : '-';

                var html = ''
                    + '<tr>'
                    +   '<td class="col-no">'     + no          + '</td>'
                    +   '<td class="col-date">'   + chargedAt   + '</td>'
                    +   '<td class="col-coin">'   + chargedCoin + '</td>'
                    +   '<td class="col-after">'  + after       + '</td>'
                    +   '<td class="col-amount">' + paid        + '</td>'
                    + '</tr>';

                $tbody.append(html);
            });
        }

        // [CHANGE] 백틱 템플릿 → 문자열 연결
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
                html += '<li class="pg-item pg-item--prev is-disabled"><span class="pg-text pg-text--icon">&lsaquo;</span></li>';
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
                html += '<li class="pg-item pg-item--next is-disabled"><span class="pg-text pg-text--icon">&rsaquo;</span></li>';
            }

            // » 마지막
            if (current <= totalPage - blockSize) {
                html += '<li class="pg-item pg-item--last"><a class="pg-link pg-link--icon pg-last" href="#" data-page="' + totalPage + '">&raquo;</a></li>';
            }

            html += '</ul>';
            $pg.html(html);
        }

        // [UNCHANGED] 데이터 로드/이벤트 바인딩
        function loadPage(page, size){
            return AuthFunc.apiRequest(() =>
                axios.get(ctxPath + '/api/mypage/eumpyo/history/charge', {
                    params: { page, size },
                    headers: AuthFunc.getAuthHeader(),         // [ADD] Authorization
                    withCredentials: USE_CREDENTIALS           // [ADD] 필요 시 쿠키 동반
                })
            ).then(function(r){
                const data = r.data || {};
                if (data.result !== 'success') throw new Error('fail');

                state.page = data.page || page;
                state.size = data.size || size;
                state.totalCount = data.totalCount || 0;

                buildRows(data.list || [], state.totalCount, state.page, state.size);
                buildPagination(state.totalCount, state.size, state.page);

                // [ADD] 잔액 동기화(선택)
                return AuthFunc.apiRequest(() =>
                    axios.get(ctxPath + '/api/mypage/eumpyo/charge/balance', {
                        headers: AuthFunc.getAuthHeader(),
                        withCredentials: USE_CREDENTIALS
                    })
                ).then(function(rb){
                    if (rb.data && rb.data.result === 'success') {
                        $('#myCoinBalance').text((rb.data.coinBalance || 0).toLocaleString());
                    }
                }).catch(function(){});
            });
        }

        // [UNCHANGED] 페이지바 클릭(위임)
        $(document).on('click', '#pagination a.pg-link', function(e){
            e.preventDefault();
            const p = parseInt($(this).data('page'), 10);
            if (isNaN(p)) return;
            loadPage(p, state.size);

            // (선택) 주소표시줄 page 동기화
            const u = new URL(window.location.href);
            u.searchParams.set('page', p);
            u.searchParams.set('size', state.size);
            window.history.replaceState({}, '', u.toString());
        });

        // [UNCHANGED] 초기 로드
        $(function(){
            loadPage(state.page, state.size).catch(function(){
                $('#chargeTbody').html('<tr><td colspan="5" class="text-center">목록을 불러오지 못했습니다.</td></tr>');
            });
        });

    })(jQuery);
</script>

<body>
    <div id="wrap">
        <main class="chargeHistory">
            <%-- 왼쪽 사이드 네비게이션 & 관련 팝업들 --%>
            <jsp:include page="../../include/common/asideNavigation.jsp" />

            <%-- 메인 컨텐츠 시작 --%>
            <div class="main-contents">
                <div class="inner">
                    <%-- 보유 음표 영역 --%>
                    <jsp:include page="../../include/mypage/mypagePointInfo.jsp" />
                    
                    <%-- 음표 충전 탭 --%>
                    <jsp:include page="../../include/mypage/eumpyoChargeTab.jsp" />

                    <!-- 충전내역 리스트 시작 -->
                    <table class="table chargeList table-hover"><!-- [MOD] table-hover 유지 -->
                        <thead>
                            <tr>
                                <th class="col-no">번호</th>
                                <th class="col-date">충전일자</th>
                                <th class="col-coin">충전음표</th>
                                <th class="col-after">충전 후 음표</th>
                                <th class="col-amount">결제금액</th>
                            </tr>
                        </thead>
                        <tbody id="chargeTbody"><!-- [ADD] 클라이언트 렌더 표적 -->
                            <%-- SSR 초기 렌더를 남기고 싶다면 아래 블록을 유지하세요. JS 로딩 후에는 교체됨. --%>
                            <c:set var="list" value="${requestScope.list}" />
                            <c:choose>
                                <c:when test="${not empty list}">
                                    <c:forEach var="row" items="${list}" varStatus="st">
                                        <c:set var="no" value="${requestScope.totalCount - (requestScope.currentShowPageNo - 1) * requestScope.sizePerPage - st.index}" />             
                                        <tr>
                                            <td class="col-no">${no}</td>
                                            <td class="col-date">${row.chargedAt}</td> <!-- yyyy.mm.dd -->
                                            <td class="col-coin">
                                                <c:choose>
                                                    <c:when test="${not empty row.chargedCoin}">
                                                        <fmt:formatNumber value="${row.chargedCoin}" pattern="+#,##0;-#,##0" /> 음표
                                                    </c:when>
                                                    <c:otherwise>-</c:otherwise>
                                                </c:choose>
                                            </td>
                                            
                                            <td class="col-after">
                                                <c:choose>
                                                    <c:when test="${not empty row.coinBalance}">
                                                        <fmt:formatNumber value="${row.coinBalance}" pattern="#,###" /> 음표
                                                    </c:when>
                                                    <c:otherwise>-</c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="col-amount">
                                                <c:choose>
                                                    <c:when test="${not empty row.paidAmount}">
                                                        <fmt:formatNumber value="${row.paidAmount}" pattern="#,###" />원
                                                    </c:when>
                                                    <c:otherwise>-</c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="5" class="text-center">충전내역이 없습니다.</td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                    <%-- 충전내역 리스트 끝 --%>
                    
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