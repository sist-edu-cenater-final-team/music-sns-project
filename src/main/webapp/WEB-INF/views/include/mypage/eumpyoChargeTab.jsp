<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%
    String ctxPath = request.getContextPath();
%>

<style>
 .tabs.eumpyoTab {
	display: flex;
	border-bottom: solid 1px #E0E0E0;
	margin: 60px 0 30px;
 }
 
.tabs.eumpyoTab > li{
	flex:1;      
	font-size: 18px;
}

.tabs.eumpyoTab .tab{
	display:block;        
	text-align:center;
	padding: 16px;    
	font-size: 20px;
	color:#5A5A5A;
	font-weight:500;
	position:relative;
}

.tabs.eumpyoTab .tab:hover{
	color:#5A5A5A;
	font-weight:700;
	text-decoration: none;
}

.tabs.eumpyoTab .tab.active{
	border-bottom: solid 3px;
	color:#6633FF;
	font-weight:700;
}

/* 모바일 */
@media (max-width: 991.98px){
  .tabs.eumpyoTab{ padding:0 20px; }
  .tabs.eumpyoTab .tab{ padding:14px 0; font-size:14px; }
}
</style>


<c:choose>
  <c:when test="${not empty param.activeTab}">
    <c:set var="active" value="${param.activeTab}" />
  </c:when>
  <c:otherwise>
    <c:choose>
      <c:when test="${fn:endsWith(pageContext.request.requestURI, '/sellingList')}">
        <c:set var="active" value="charge" />
      </c:when>
      <c:when test="${fn:endsWith(pageContext.request.requestURI, '/purchasedList')}">
        <c:set var="active" value="chargeHistory" />
      </c:when>
      <c:when test="${fn:endsWith(pageContext.request.requestURI, '/purchasedProductLists')}">
        <c:set var="active" value="usageHistory" />
      </c:when>
      <c:otherwise>
        <c:set var="active" value="charge" />
      </c:otherwise>
    </c:choose>
  </c:otherwise>
</c:choose>


<ul class="tabs eumpyoTab" role="tablist">
  <li>
    <a role="tab"
       aria-selected="${active eq 'charge'}"
       class="tab${active eq 'charge' ? ' active' : ''}"
       href="<%= ctxPath %>/mypage/eumpyo/sellingList">음표충전</a>
  </li>
  <li>
    <a role="tab"
       aria-selected="${active eq 'chargeHistory'}"
       class="tab${active eq 'chargeHistory' ? ' active' : ''}"
       href="<%= ctxPath %>/mypage/eumpyo/purchasedList">충전내역</a>
  </li>
  <li>
    <a role="tab"
       aria-selected="${active eq 'usageHistory'}"
       class="tab${active eq 'usageHistory' ? ' active' : ''}"
       href="<%= ctxPath %>/mypage/eumpyo/purchasedProductLists">사용내역</a>
  </li>
</ul>