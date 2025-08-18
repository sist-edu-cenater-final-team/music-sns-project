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

 .chargeHistory .table.table-hover tbody tr:hover {
 	background: #FAFAFA;
 }

 /* 열 폭(고정 레이아웃과 함께 사용) */
 .col-no     { width: 5%;  }
 .col-date   { width: 30%; }
 .col-coin   { width: 30%; white-space: nowrap; }
 .col-after  { width: 25%; white-space: nowrap; }
 .col-amount { width: 10%; white-space: nowrap; }

 /* 페이지바 컨테이너 */
 #pagination {
    margin: 30px auto;
    display: flex;
    justify-content: center;
 }

 /* 페이지바 공통 */
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

/* 숫자 칩 공통(링크/현재) : 크기/라인 고정으로 흔들림 방지 */
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
 #pagination .is-disabled {
  	opacity: 0.3;
 }

 #pagination .is-disabled a {
  	pointer-events: none;   /* 마우스만 차단 */
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
		    <%-- 왼쪽 사이드 네비게이션 & 관련 팝업들 --%>
	        <jsp:include page="../../include/common/asideNavigation.jsp" />
	        <%-- //왼쪽 사이드 네비게이션 & 관련 팝업들 --%>
	
			<%-- 메인 컨텐츠 시작 --%>
	        <div class="main-contents">
	            <div class="inner">
	                <%-- 보유 음표 영역 --%>
	                <jsp:include page="../../include/mypage/mypagePointInfo.jsp" />
	                
	                <%-- 음표 충전 탭 --%>
					<jsp:include page="../../include/mypage/eumpyoChargeTab.jsp" />
	
			        <!-- 충전내역 리스트 시작 -->
			        <table class="table table-hover chargeList">
			          	<thead>
			          		<tr>
			            		<th class="col-no">번호</th>
			            		<th class="col-date">충전일자</th>
			            		<th class="col-coin">충전음표</th>
			            		<th class="col-after">충전 후 음표</th>
			            		<th class="col-amount">결제금액</th>
			          		</tr>
			          	</thead>
						<tbody>
			          		<c:set var="list" value="${not empty requestScope.chargeList ? requestScope.chargeList : requestScope.list}" />
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
							                        	<fmt:formatNumber value="${row.chargedCoin}" pattern="#,###" />
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