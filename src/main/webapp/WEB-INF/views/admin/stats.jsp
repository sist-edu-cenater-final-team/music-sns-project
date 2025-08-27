<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%
    String ctxPath = request.getContextPath();
%>

<jsp:include page="../include/common/head.jsp" />

<meta name="ctxPath" content="<%= ctxPath %>"/>

<script src="https://code.highcharts.com/highcharts.js"></script>
<script src="<%= ctxPath %>/js/admin/stats.js" defer></script>

<div class="container my-4" id="admin-stats">
  <h2 class="mb-3">관리자 통계</h2>

  <!-- 필터 영역 -->
  <div class="card p-3 mb-3">
    <div class="row g-2 align-items-end">
      <div class="col-sm-3">
        <label class="form-label">시작일</label>
        <input type="date" id="startDate" class="form-control">
      </div>
      <div class="col-sm-3">
        <label class="form-label">종료일</label>
        <input type="date" id="endDate" class="form-control">
      </div>
      <div class="col-sm-6">
        <div class="d-flex flex-wrap gap-2 mt-4 mt-sm-0">
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
  <div class="row g-3 mb-3" id="summary-cards">
    <div class="col-md-4">
      <div class="card p-3 text-center">
        <div class="h6 mb-1 text-muted">음표 충전 총합</div>
        <div id="sumChargedCoin" class="h3 mb-0">-</div>
      </div>
    </div>
    <div class="col-md-4">
      <div class="card p-3 text-center">
        <div class="h6 mb-1 text-muted">매출 총합</div>
        <div id="sumRevenue" class="h3 mb-0">-</div>
      </div>
    </div>
    <div class="col-md-4">
      <div class="card p-3 text-center">
        <div class="h6 mb-1 text-muted">사용 음표 총합</div>
        <div id="sumUsedCoin" class="h3 mb-0">-</div>
      </div>
    </div>
  </div>

  <!-- 시계열(일 단위 고정) -->
  <div class="card p-3 mb-3">
    <h5 class="mb-3">일자별 추이</h5>
    <div id="chart-charged" class="mb-3" style="height: 300px;"></div>
    <div id="chart-used" style="height: 300px;"></div>
  </div>

  <!-- TOP 테이블 -->
  <div class="row g-3">
    <div class="col-md-6">
      <div class="card p-3">
        <h6 class="mb-2">Top 10 충전 유저</h6>
        <div class="table-responsive">
          <table class="table table-sm align-middle">
            <thead>
              <tr><th>#</th><th>닉네임</th><th class="text-end">충전합</th></tr>
            </thead>
            <tbody id="tbl-top-chargers"></tbody>
          </table>
        </div>
      </div>
    </div>
    <div class="col-md-6">
      <div class="card p-3">
        <h6 class="mb-2">Top 10 사용 유저</h6>
        <div class="table-responsive">
          <table class="table table-sm align-middle">
            <thead>
              <tr><th>#</th><th>닉네임</th><th class="text-end">사용합</th></tr>
            </thead>
            <tbody id="tbl-top-spenders"></tbody>
          </table>
        </div>
      </div>
    </div>
  </div>

  <div class="card p-3 mt-3">
    <h6 class="mb-2">Top 10 베스트셀러 음악</h6>
    <div class="table-responsive">
      <table class="table table-sm align-middle">
        <thead>
          <tr><th>#</th><th>Music ID</th><th class="text-end">판매수</th><th class="text-end">코인합</th></tr>
        </thead>
        <tbody id="tbl-top-music"></tbody>
      </table>
    </div>
  </div>
</div>