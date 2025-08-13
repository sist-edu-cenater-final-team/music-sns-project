<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%
    String ctxPath = request.getContextPath();
%>

<style>
<%--
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
  --%>
  
 .tabs.eumpyoChargeTab3 {
	display:flex;
	align-items:flex-end; justify-content:space-between;
	border-bottom:1px solid var(--line);
	margin:12px 0 20px;
	list-style:none;
 }
.tabs.eumpyoChargeTab > li{
  flex:1 1 0;            /* 균등 폭 */
  margin:0;              /* 칸 간격 제거 */
}
.tabs.eumpyoChargeTab .tab{
  display:block;         /* 칸 전체 클릭 */
  text-align:center;
  padding:16px 0;        /* 높이 보정(원하면 52px로 고정해도 OK) */
  line-height:1; color:#8a8ea3; font-weight:700; position:relative;
}
.tabs.eumpyoChargeTab .tab:hover{ color:#6C63FF; }

/* 활성 밑줄: 좌우 12px 여백 주고 꽉 차 보이게 */
.tabs.eumpyoChargeTab .tab::after{
  content:""; position:absolute; left:12px; right:12px; bottom:-1px;
  height:3px; background:#6C63FF; border-radius:2px;
  transform:scaleX(0); transform-origin:left; transition:transform .2s ease;
}
.tabs.eumpyoChargeTab .tab.active{ color:#6C63FF; }
.tabs.eumpyoChargeTab .tab.active::after{ transform:scaleX(1); }

/* 모바일 */
@media (max-width: 991.98px){
  .tabs.eumpyoChargeTab{ padding:0 20px; }
  .tabs.tabs-3 .tab{ padding:14px 0; font-size:14px; }
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

<%-- 
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

--%>


<ul class="tabs eumpyoChargeTab" role="tablist">
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