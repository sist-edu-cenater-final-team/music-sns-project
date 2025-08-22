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

.purchaseHistory .table {
	table-layout: fixed;
	font-variant-numeric: tabular-nums; /* 숫자 폭 고정 */
}

.purchaseHistory .table thead th {
	border-top: 1px solid #E0E0E0;
	border-bottom: 1px solid #E0E0E0;
	padding: 12px;
	font-size: 16px;
	font-weight: 700;
	color: #8A8A8A;
	white-space: nowrap;
	text-align: center;
}

.purchaseHistory .table thead th,
.purchaseHistory .table tbody td {
	text-align: center;
	vertical-align: middle;
}

.purchaseHistory .table tbody td {
	padding: 15px;
	font-size: 15px;
	font-weight: 500;
	color: #000;
	border-top: 1px solid #E0E0E0;
}

.purchaseHistory .table tbody td.col-usedCoin {
	color: #AE1932;
}

.purchaseHistory .table.table-hover tbody tr:hover {
	background: #F3F2F8;
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
</style>

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
						<tbody>
							<c:set var="list" value="${requestScope.list}" />
							<c:choose>
								<c:when test="${not empty list}">
									<c:forEach var="row" items="${list}" varStatus="st">
										<c:set var="no" value="${requestScope.totalCount - (requestScope.currentShowPageNo - 1) * requestScope.sizePerPage - st.index}" />
										<tr>
											<!-- 번호 / 구매일자 -->
											<td class="col-no"><c:out value="${no}" /></td>
											<td class="col-date"><c:out value="${row.purchasedAt}" /></td>

											<!-- 상품명: 대표이미지 + 대표곡명 외 N건 -->
											<td>
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

											<!-- 사용된 음표 -->
											<td class="col-usedCoin">
												<c:choose>
													<c:when test="${not empty row.usedCoin}">
														<fmt:formatNumber value="${0 - row.usedCoin}" pattern="#,##0; -#,##0" /> 음표
													</c:when>
													<c:otherwise>-</c:otherwise>
												</c:choose>
											</td>

											<!-- 구매 후 음표 -->
											<td class="col-balance">
												<c:choose>
													<c:when test="${not empty row.coinBalance}">
														<fmt:formatNumber value="${row.coinBalance}" pattern="#,###" /> 음표
													</c:when>
													<c:otherwise>-</c:otherwise>
												</c:choose>
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