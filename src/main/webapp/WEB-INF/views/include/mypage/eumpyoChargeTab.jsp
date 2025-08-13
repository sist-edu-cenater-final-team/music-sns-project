<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%
    String ctxPath = request.getContextPath();
%>

<style>
/* ▼ 탭: 좌정렬 + 라벨 길이만 밑줄 */
  .tabs{
    display:flex; align-items:flex-end; justify-content:flex-start; /* NEW */
    padding:0 66px;                 /* NEW : 좌우 여백 */
    border-bottom:1px solid var(--line);
    margin:12px 0 20px; list-style:none;
  }
  .tabs>li{ margin:0 24px 0 0; }    /* NEW */
  .tabs>li:last-child{ margin-right:0; }
  .tabs .tab{
    position:relative; display:inline-block; /* NEW : 라벨만큼 */
    padding:12px 4px; line-height:1; color:#8a8ea3; font-weight:600;
    text-decoration:none; transition:color .2s ease;
  }
  .tabs .tab:hover{ color:#6C63FF; }
  .tabs .tab::after{
    content:""; position:absolute; left:0; bottom:-1px;
    width:0; height:2px; background:#6C63FF; border-radius:2px;
    transition:width .22s ease;
  }
  .tabs .tab.active{ color:#6C63FF; }
  .tabs .tab.active::after{ width:100%; }
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

<ul class="tabs" role="tablist">
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