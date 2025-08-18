<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%
    String ctxPath = request.getContextPath();
%>

<style>
 .tabs.eumpyoTab {
    display: flex;
    border-bottom: 1px solid #E0E0E0;
    margin: 60px 0 30px;
 }
 
 .tabs.eumpyoTab > li {
 	flex: 1;
 	font-size: 18px;
 }
 
 .tabs.eumpyoTab .tab {
    display: block;
    text-align: center;
    padding: 16px;
    font-size: 18px;
    color: #5A5A5A;
    font-weight: 500;
    position: relative;
    text-decoration: none;
 }
 
 .tabs.eumpyoTab .tab:hover {
 	color: #5A5A5A;
 	font-weight: 700;
 	text-decoration: none;
 }
 
 .tabs.eumpyoTab .tab.active {
 	border-bottom: 3px solid;
 	color: #6633FF;
 	font-weight: 700;
 }
 
</style>

<%-- 활성 탭 결정 --%>
<c:choose>
	<c:when test="${not empty param.activeTab}">
    	<c:set var="active" value="${param.activeTab}" />
  	</c:when>
  	<c:otherwise>
	    <%-- forward 된 경우 원래 요청 URI, 아니면 현재 URI --%>
	    <c:set var="uri" value="${empty requestScope['jakarta.servlet.forward.request_uri'] ? pageContext.request.requestURI : requestScope['jakarta.servlet.forward.request_uri']}" />       
	    
	    <%-- 뒤에 / 로 끝나면 잘라내기 --%>
	    <c:if test="${fn:endsWith(uri, '/')}">
	      	<c:set var="uri" value="${fn:substring(uri, 0, fn:length(uri)-1)}" />
	    </c:if>
	    
	    <%-- 마지막 경로 조각만 뽑기 --%>
	    <c:set var="parts" value="${fn:split(uri, '/')}"/>
	    <c:set var="last"  value="${parts[fn:length(parts)-1]}"/>
	    
	    <%-- active 값으로 사용 --%>
	    <c:set var="active" value="${last}" />
  	</c:otherwise>
</c:choose>

<%-- 예상치 못한 값이면 기본값을 charge로 --%>
<c:if test="${active ne 'charge' and active ne 'chargeHistory' and active ne 'useHistory'}">
	<c:set var="active" value="charge"/>
</c:if>

<%-- active 값이 일치하는 탭에만 'active' 클래스를 붙인다. --%>
<ul class="tabs eumpyoTab">
  <li>
    <a class="tab${active eq 'charge' ? ' active' : ''}"
       href="<%= ctxPath %>/mypage/eumpyo/charge">음표충전</a>
  </li>
  <li>
    <a class="tab${active eq 'chargeHistory' ? ' active' : ''}"
       href="<%= ctxPath %>/mypage/eumpyo/chargeHistory">충전내역</a>
  </li>
  <li>
    <a class="tab${active eq 'useHistory' ? ' active' : ''}"
       href="<%= ctxPath %>/mypage/eumpyo/useHistory">사용내역</a>
  </li>
</ul>
