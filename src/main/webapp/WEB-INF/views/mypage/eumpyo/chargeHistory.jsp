<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	
<%
	String ctxPath = request.getContextPath();
%>

<jsp:include page="../../include/common/head.jsp" />

<meta name="ctxPath" content="<%= ctxPath %>">

<script src="<%= ctxPath %>/js/eumpyo/chargeHistory.js" defer></script>
<link rel="stylesheet" href="<%= ctxPath %>/css/mypage.css" />

<style>
	.chargeList {
		margin: 56px 0;
	}

	.chargeHistory .table {
		table-layout: fixed;
		font-variant-numeric: tabular-nums; /* 숫자 폭 고정 */
	}

	.chargeHistory .table thead th {
		border-top: 1px solid #E0E0E0;
		border-bottom: 1px solid #E0E0E0;
		padding: 12px;
		font-size: 16px;
		font-weight: 700;
		color: #8A8A8A;
		white-space: nowrap;
	}

	.chargeHistory .table thead th,
	.chargeHistory .table tbody td {
		text-align: center;
		vertical-align: middle;
	}

	.chargeHistory .table tbody td {
		padding: 15px;
		font-size: 15px;
		font-weight: 500;
		color: #000;
		border-top: 1px solid #E0E0E0;
	}

	/* 색상 유지 */
	.chargeHistory .table tbody td.col-coin {
		color: #2A52BE;
	}

	.chargeHistory .table.table-hover tbody tr:hover {
		background: #FAFAFA;
	}

	/* 열 폭 */
	.col-no     { width: 15%; }
	.col-date   { width: 20%; }
	.col-coin   { width: 25%; white-space: nowrap; }
	.col-after  { width: 30%; white-space: nowrap; }
	.col-amount { width: 10%; white-space: nowrap; }

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
	#pagination a:active {
		color: #5A5A5A;
	}

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

	/* 비활성 스타일 표시 */
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
		<main class="chargeHistory">
			<jsp:include page="../../include/common/asideNavigation.jsp" />
			<div class="main-contents">
				<div class="inner">
					<jsp:include page="../../include/mypage/mypagePointInfo.jsp" />
					<jsp:include page="../../include/mypage/eumpyoChargeTab.jsp" />

					<table class="table chargeList table-hover">
						<thead>
							<tr>
								<th class="col-no">충전거래번호</th>
								<th class="col-date">충전일자</th>
								<th class="col-coin">충전음표</th>
								<th class="col-after">충전 후 음표</th>
								<th class="col-amount">결제금액</th>
							</tr>
						</thead>
						<tbody id="chargeTbody">
							<tr><td colspan="5" class="text-center">불러오는 중...</td></tr>
						</tbody>
					</table>

					<div id="pagination"></div>
				</div>
			</div>

			<jsp:include page="../../include/common/asidePlayList.jsp" />
		</main>
	</div>
</body>
</html>
