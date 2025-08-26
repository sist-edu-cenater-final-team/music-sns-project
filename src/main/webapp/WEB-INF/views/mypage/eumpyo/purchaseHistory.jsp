<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
	
<%
	String ctxPath = request.getContextPath();
%>

<jsp:include page="../../include/common/head.jsp" />

<meta name="ctxPath" content="<%= ctxPath %>">

<script src="<%= ctxPath %>/js/eumpyo/purchaseHistory.js" defer></script>
<link rel="stylesheet" href="<%= ctxPath %>/css/mypage.css" />

<style>
	.purchaseList {
	    margin: 56px 0;
	}
	
	.purchaseHistory .table {
		table-layout: fixed;
		font-variant-numeric: tabular-nums;
		width: 100%;
	}
	
	.purchaseHistory .table thead th {
		border-top: 1px solid #E0E0E0;
		border-bottom: 1px solid #E0E0E0;
		padding: 12px;
		font-size: 16px;
		font-weight: 700;
		color: #8A8A8A;
		white-space: nowrap;
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
	
	.purchaseHistory .table tbody td.col-usedCoin { color: #AE1932; }
	.purchaseHistory .table.table-hover tbody tr:hover { background: #FAFAFA; }
	
	tr.purchase-history-row { cursor: pointer; }
	tr.purchase-history-row * { cursor: pointer; }
	
	.music-info {
		display: flex;
		align-items: center;
		gap: 15px;
		justify-content: left;
		padding-left: 15px;
		min-width: 0;
	}
	
	.music-img {
		width: 48px;
		height: 48px;
		border-radius: 6px;
		overflow: hidden;
		background: #eee;
		flex: none;
	}
	
	.music-img img {
		width: 48px;
		height: 48px;
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
	
	.music-artist,
	.music-album {
		margin: 0;
		max-width: 320px;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
	}
	
	.col-no             { width: 15%; }
	.col-date           { width: 15%; }
	.col-purchasedetail { width: 40%; white-space: nowrap; }
	.col-usedCoin       { width: 20%; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
	.col-balance        { width: 10%; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
	
	.purchase-detail .detail-loading {
		padding: 12px;
		color: #666;
	}
	
	.detail-list {
		padding: 20px;
		list-style: none;
	}
	
	.detail-item {
		display: flex;
		align-items: center;
		gap: 12px;
		padding: 10px 20px;
		border: 1px solid #eee;
		border-radius: 10px;
		background: #fff;
		width: 100%;
	}
	
	.detail-item + .detail-item { margin-top: 10px; }
	
	.detail-thumb {
		width: 44px;
		height: 44px;
		border-radius: 6px;
		overflow: hidden;
		background: #eee;
		flex: none;
	}
	
	.detail-thumb img {
		width: 44px;
		height: 44px;
		object-fit: cover;
		display: block;
	}
	
	.detail-meta {
		flex: 1;
		min-width: 0;
		display: flex;
		align-items: center;
	}
	
	.detail-line {
		min-width: 0;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
	}
	
	.detail-line .t {			
		font-weight: 700;
	}
	
	.detail-line .s {			
		color: #666;
	}
	
	.used-coin {
		font-variant-numeric: tabular-nums;
		margin-left: auto;
		font-size: 13px;
		font-weight: 700;
		white-space: nowrap;
		padding: 6px 10px; 
		border-radius: 999px;
		border: 1px solid #F2B8C2;
		background: #FFF5F7;
		color: #AE1932;
		line-height: 1;
	}
	
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
		cursor: pointer;
	}
	
	#pagination a:hover,
	#pagination a:active { color: #5A5A5A; }
	
	#pagination .is-current .pg-current {
		border-color: #5A5A5A;
		color: #5A5A5A;
		background: #fff;
		font-weight: 700;
	}
	
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
	
	#pagination .is-disabled { opacity: 0.3; }
	#pagination .is-disabled a {
		pointer-events: none;
		cursor: default;
	}
	
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
	
	/* 호버 피드백(미세 강조) */
	.detail-item:hover {
		box-shadow: 0 2px 6px rgba(0,0,0,.06);
		border-color: #E0E0E7;
	}
</style>

<body>
	<div id="wrap">
		<main class="purchaseHistory">
			<jsp:include page="../../include/common/asideNavigation.jsp" />
			<div class="main-contents">
				<div class="inner">
					<jsp:include page="../../include/mypage/mypagePointInfo.jsp" />
					<jsp:include page="../../include/mypage/eumpyoChargeTab.jsp" />

					<table class="table table-hover purchaseList">
						<thead>
							<tr>
								<th scope="col" class="col-no">구매번호</th>
								<th scope="col" class="col-date">구매일자</th>
								<th scope="col" class="col-purchasedetail">구매내역</th>
								<th scope="col" class="col-usedCoin">사용된 음표</th>
								<th scope="col" class="col-balance">구매 후 음표</th>
							</tr>
						</thead>
						<tbody id="purchaseTbody"></tbody>
					</table>

					<div id="pagination"></div>
				</div>
			</div>
			<jsp:include page="../../include/common/asidePlayList.jsp" />
		</main>
	</div>
</body>
</html>
