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
	
});
</script>

<body>
	<div id="wrap">
  		<main class="purchaseHistory">
		    <%-- 왼쪽 사이드 네비게이션 & 관련 팝업들 --%>
	        <jsp:include page="../../include/common/asideNavigation.jsp" />
	        <%-- //왼쪽 사이드 네비게이션 & 관련 팝업들 --%>
	
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
			            		<th scope="col">노래제목</th>
			            		<th scope="col">아티스트</th>
			            		<th scope="col">앨범</th>
			            		<th scope="col" class="col-coinAmount">사용된 음표</th>
			            		<th scope="col" class="col-balance">구매 후 음표</th>
			          		</tr>
			          	</thead>
						<tbody>
			          		<c:set var="list" value="${not empty requestScope.purchaseList ? requestScope.purchaseList : requestScope.list}" />
			          		<c:choose>
			            		<c:when test="${not empty list}">
				              		<c:forEach var="row" items="${list}" varStatus="st">
					                	<c:set var="no" value="${requestScope.totalCount - (requestScope.currentShowPageNo - 1) * requestScope.sizePerPage - st.index}" />             
					                	<tr>
					                  		<td class="col-no">${no}</td>
					                  		<td class="col-date">${row.purchasedAt}</td> <!-- yyyy.mm.dd -->
					                  		<td>
						                  		<p class="music-text">${item.musicName}</p>
					                  		</td>
					                  		<td>
						                  		<p class="music-artist">${item.artistName}</p>
					                  		</td>
					                  		<td>
						                  		<p class="music-artist">${item.albumName}</p>
					                  		</td>
					                 	 	<td class="col-coinAmount">
					                    		<c:choose>
						                      		<c:when test="${not empty row.coinAmount}">
						                        		<fmt:formatNumber value="${row.coinAmount}" pattern="#,###" /> 음표
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