<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>  

<%
	String ctxPath = request.getContextPath();
%> 

<html>
<jsp:include page="../../include/common/head.jsp" />
<link rel="stylesheet" href="../../css/mypage.css" />

<body>
<div id="wrap">
    <main class="purchasedList">
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
			
				<%-- 충전내역 시작 --%>

	            <table class="table table-hover">
				    <thead>
				    	<tr>
				    		<th width="10%">번호</th>
				    		<th>제목</th>
				    		<th width="15%">작성자</th>
				    		<th width="15%">작성일자</th>
				    		<th width="10%">조회수</th>
				    	</tr>
				    </thead>
			    
			    	<tbody>
			        	<c:if test="${not empty requestScope.boardDtoList}">
				        	<c:forEach var="board" items="${requestScope.boardDtoList}" varStatus="status"> 
				    			<tr class="exists">
				    				<td>
				    				   <span>${board.num}</span>
				    				    ${ (requestScope.totalDataCount) - (requestScope.currentShowPageNo - 1) * (requestScope.sizePerPage) - (status.index) }    
				    				 </td>
				    				<td>${board.subject}</td>
				    				<td>${board.member.userName}</td>
				    				<td>${fn:substring(board.regDate, 0, 10)}</td>
				    				<td style="padding-left: 2%;">${board.readCount}</td>
				    			</tr>
				    		</c:forEach>
			        	</c:if>
			        
				        <c:if test="${empty requestScope.boardDtoList}">
				            <tr>
				    		  <td colspan="4" class="text-center" style="color: red;">게시글 데이터가 존재하지 않습니다</td>
				    		</tr>
				        </c:if>
			    
			    	</tbody>
		   		</table>	 
		   
			   	<div align="center" style="border: solid 0px gray; width: 80%; margin: 30px auto;">  
				    	${requestScope.pageBar}
				</div>
			</div>
        </div>
        <%-- 메인 컨텐츠 끝 --%>

        <%-- 오늘의 감정 플레이리스트 --%>
        <jsp:include page="../../include/common/asidePlayList.jsp" />
        <%-- //오늘의 감정 플레이리스트 --%>
    </main>
</div>

</body>
</html>