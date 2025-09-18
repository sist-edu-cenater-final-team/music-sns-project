<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%
	String ctxPath = request.getContextPath();
%>

<jsp:include page="../include/common/head.jsp" />

<meta name="ctxPath" content="<%= ctxPath %>" />

<script src="https://code.highcharts.com/highcharts.js"></script>
<script src="https://code.highcharts.com/modules/exporting.js"></script>
<script src="https://code.highcharts.com/modules/export-data.js"></script>
<script src="https://code.highcharts.com/modules/accessibility.js"></script>

<script src="<%= ctxPath %>/js/admin/stats.js" defer></script>

<link rel="stylesheet" href="<%= ctxPath %>/css/mypage.css" />

<style>
	body {
		background: #F8FAFC;
		color: #0F172A;
	}

	.row {
		justify-content: center;
	}

	.adminstatss {
		margin: 0 auto;
		float: none;
		max-width: 1120px;
	}

	.card-center {
		max-width: 960px;
		margin: 0 auto;
	}

	.card {
		background: #FFFFFF;
		border: 1px solid #EEF0F4;
		border-radius: 16px;
		box-shadow: 0 6px 18px rgba(17, 24, 39, 0.06);
	}

	.section-title {
		text-align: center;
		font-weight: 800;
		font-size: 18px;
		margin: 20px 0 30px 0;
		color: #0F172A;
	}

	.filters {
		padding: 16px;
	}
	
	.filters .form-label {
		color: #334155;
		font-weight: 700;
		font-size: 14px;
		display: block;
		margin-bottom: 15px;
	}
	
	.filters .form-control {
		border-radius: 10px;
		border: 1px solid #E5E7EB;
		padding: 10px 12px;
		color: #0F172A;
	}
	
	.btn {
		border-radius: 10px;
	}
	
	.btn-dark {
		background: #6633FF;
		border-color: #6633FF;
	}
	
	.btn-dark:hover {
		background: #5529D4;
		border-color: #5529D4;
	}
	
	.btn-outline-secondary {
		color: #334155;
		border-color: #E5E7EB;
		background: #FFFFFF;
	}
	
	.btn-outline-secondary:hover {
		color: #000000;
		border-color: #D1D5DB;
		background: #F4F3FF;
	}
	
	.btn-bar {
		display: flex;
		flex-wrap: wrap;
		gap: 10px;
	}

	#summary-cards {
		margin-bottom: 24px;
	}
	
	#summary-cards .card {
		padding: 18px;
	}
	
	#summary-cards .card .h3 {
		font-weight: 800;
	}
	
	#summary-cards .text-muted {
		font-size: 14px;
	}

	.table thead th {
		border-top: 1px solid #E5E7EB;
		border-bottom: 1px solid #E5E7EB;
		color: #334155;
		font-weight: 800;
		font-size: 14px;
		background: #FBFBFE;
		text-align: center;
	}
	
	.table tbody td {
		border-top: 1px solid #F1F2F6;
		color: #0B1220;
		font-weight: 500;
	}
	
	.table.table-sm tbody tr:hover {
		background: #FAFAFF;
	}

	.table {
		font-variant-numeric: tabular-nums; 
	}
	
	.table thead th.text-end,
	.table tbody td.text-end,
	td.nickname {
		text-align: center;      
	}
	
	.table thead th.text-center,
	.table tbody td.text-center {
		text-align: center !important;
	}
	
	.table th:first-child,
	.table td:first-child {
		width: 56px;
		text-align: center;
	}

	#chart-charged,
	#chart-used,
	#chart-revenue,
	#chart-new-members,
	#chart-hourly-visitors,
	#chart-daily-visitors {
		border-radius: 12px;
	}

	.block {
		margin-bottom: 28px;
	}

	.card .section-block + .section-block {
		margin-top: 16px;
	}

	.split-2 {
		display: grid;
		grid-template-columns: 1fr 1fr;
		gap: 12px;
	}
</style>

<body>
	<div id="wrap">
		<main class="charge">
			<jsp:include page="../include/common/asideNavigation.jsp" />

			<div class="main-contents">
				<div class="inner">
					<div class="row">
						<div class="adminstatss">

							<!-- 날짜 선택 -->
							<div class="card filters block">
								<div class="row g-2 align-items-end">
									<div class="col-sm-3">
										<label class="form-label">시작일</label>
										<input type="date" id="startDate" class="form-control" />
									</div>
									<div class="col-sm-3">
										<label class="form-label">종료일</label>
										<input type="date" id="endDate" class="form-control" />
									</div>
									<div class="col-sm-6">
										<div class="btn-bar">
											<button type="button" class="btn btn-outline-secondary" id="btnToday">오늘</button>
											<button type="button" class="btn btn-outline-secondary" id="btnWeek">이번 주</button>
											<button type="button" class="btn btn-outline-secondary" id="btnMonth">이번 달</button>
											<button type="button" class="btn btn-outline-secondary" id="btn7">최근 7일</button>
											<button type="button" class="btn btn-outline-secondary" id="btn30">최근 30일</button>
											<button type="button" class="btn btn-dark" id="btnSearch">조회</button>
											<button type="button" class="btn btn-outline-secondary" id="btnReset">초기화</button>
										</div>
									</div>
								</div>
							</div>

							<!-- 요약 카드 -->
							<div class="row g-3 block" id="summary-cards">
								<div class="col-md-4">
									<div class="card text-center">
										<div class="h6 text-muted" style="margin: 12px 0 8px 0;">음표 충전 총합</div>
										<div id="sumChargedCoin" class="h3" style="margin: 0 0 12px 0;">-</div>
									</div>
								</div>
								<div class="col-md-4">
									<div class="card text-center">
										<div class="h6 text-muted" style="margin: 12px 0 8px 0;">매출 총합</div>
										<div id="sumRevenue" class="h3" style="margin: 0 0 12px 0;">-</div>
									</div>
								</div>
								<div class="col-md-4">
									<div class="card text-center">
										<div class="h6 text-muted" style="margin: 12px 0 8px 0;">사용 음표 총합</div>
										<div id="sumUsedCoin" class="h3" style="margin: 0 0 12px 0;">-</div>
									</div>
								</div>
							</div>

							<!-- 거래·매출 현황 -->
							<div class="card card-center block" style="padding: 20px;">
								<div class="section-title">거래·매출 현황</div>
								<div class="section-block">
									<div id="chart-charged" style="height: 300px;"></div>
								</div>
								<div class="section-block">
									<div id="chart-used" style="height: 300px;"></div>
								</div>
								<div class="section-block">
									<div id="chart-revenue" style="height: 300px;"></div>
								</div>
							</div>

							<!-- TOP 유저 -->
							<div class="row g-3 block">
								<div class="col-md-6">
									<div class="card" style="padding: 20px;">
										<div class="section-title">음표 충전 유저 Top 10</div>
										<div class="table-responsive">
											<table class="table table-sm align-middle">
												<thead>
													<tr>
														<th>No</th>
														<th>닉네임</th>
														<th class="text-end">충전음표</th>
													</tr>
												</thead>
												<tbody id="tbl-top-chargers"></tbody>
											</table>
										</div>
									</div>
								</div>
								<div class="col-md-6">
									<div class="card" style="padding: 20px;">
										<div class="section-title">음표 사용 유저 Top 10</div>
										<div class="table-responsive">
											<table class="table table-sm align-middle">
												<thead>
													<tr>
														<th>No</th>
														<th>닉네임</th>
														<th class="text-end">사용음표</th>
													</tr>
												</thead>
												<tbody id="tbl-top-spenders"></tbody>
											</table>
										</div>
									</div>
								</div>
							</div>

							<!-- Top 10 베스트셀러 음악 -->
							<div class="card card-center block" style="padding: 20px;">
								<div class="section-title">Top 10 베스트셀러 음악</div>
								<div class="table-responsive">
									<table class="table table-sm align-middle">
										<thead>
											<tr>
												<th>No</th>
												<th>제목 / 아티스트</th>
												<th class="text-end">판매수</th>
												<th class="text-end">음표수익</th>
											</tr>
										</thead>
										<tbody id="tbl-top-music"></tbody>
									</table>
								</div>
							</div>

							<!-- 회원 현황 -->
							<div class="card card-center block" style="padding: 20px;">
								<div class="section-title">회원 현황</div>
								<div id="chart-new-members" style="height: 280px;"></div>
							</div>

							<!-- 이용 현황 -->
							<div class="card card-center block" style="padding: 20px;">
								<div class="section-title">이용 현황</div>
								<div class="row g-3">
									<div class="col-md-4 mx-auto">
										<div class="card text-center" style="margin-bottom: 30px;">
											<div class="h6 text-muted" style="margin: 12px 0 8px 0;">전체 이용자 수</div>
											<div id="totalUsers" class="h3" style="margin: 0 0 12px 0;">-</div>
										</div>
									</div>
								</div>
								<div class="section-block">
									<div id="chart-hourly-visitors" style="height: 260px;"></div>
								</div>
								<div class="section-block">
									<div id="chart-daily-visitors" style="height: 260px;"></div>
								</div>
							</div>

						</div>

						<jsp:include page="../include/common/asidePlayList.jsp" />
					</div>
				</div>
			</div>
		</main>
	</div>
</body>
</html>
