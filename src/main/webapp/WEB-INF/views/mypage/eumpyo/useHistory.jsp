<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	
<%
  String ctxPath = request.getContextPath();
%>

<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>


<%-- 음표 사용내역 페이지 

<!DOCTYPE html>
<html lang="ko">
<head>

<link rel="stylesheet" href="<%=ctxPath %>/bootstrap-4.6.2-dist/css/bootstrap.min.css">

<style>
    .app-wrap { 
    	display:flex;
    	min-height:100vh; 
    	background:#fff;
   	}
    .app-left  { width:260px; border-right:1px solid #eee; background:#fafafa; }
    .app-right { width:320px; border-left:1px solid #eee; background:#fafafa; }
    .app-main  { flex:1; }
    .balance-bar { background:#f5f3fb; border:1px solid #eee; border-radius:6px; padding:14px 16px; margin:24px 24px 8px 24px; display:flex; align-items:center; justify-content:space-between; }
    .balance-bar .num { color:#6b3cff; font-weight:700; margin-left:6px; }
    .btn-outline-lightgray { background:#fff; border:1px solid #ddd; padding:4px 10px; font-size:12px; border-radius:6px; }
    .point-tabs { display:flex; gap:120px; margin:20px 24px 0 24px; }
    .point-tab { position:relative; padding-bottom:10px; font-weight:600; color:#777; }
    .point-tab.active { color:#6b3cff; }
    .point-tab.active::after { content:""; position:absolute; left:0; bottom:0; width:120px; height:3px; background:#6b3cff; border-radius:2px; }
    .list-col { width:50%; padding:0 24px; }
    .row-wrap { display:flex; }
    .point-list { margin-top:24px; }
    .point-item { display:flex; align-items:center; justify-content:space-between; border-bottom:1px solid #eee; padding:18px 0; }
    .point-left { display:flex; align-items:center; gap:14px; }
    .icon-note { width:28px; height:28px; border-radius:6px; background:#e9e9ee; display:inline-flex; align-items:center; justify-content:center; font-style:normal; }
    .point-title { color:#444; }
    .btn-charge { background:#6b3cff; border:none; color:#fff; font-weight:600; border-radius:8px; padding:6px 14px; min-width:84px; }
    .col-divider { width:1px; background:#f0f0f0; margin:24px 0; }
    @media (max-width: 1200px) { .app-right { display:none; } }
    @media (max-width: 992px)  { .app-left { display:none; } .row-wrap { flex-direction:column; } .list-col { width:100%; } .col-divider { display:none; } }
</style>
  
<script src="<%=ctxPath %>/js/jquery-3.7.1.min.js"></script>
<script src="<%=ctxPath %>/bootstrap-4.6.2-dist/js/bootstrap.bundle.min.js"></script>
<script>
  $(function(){
    $(document).on('click', '.btn-charge', function(){
      const unit  = $(this).data('unit');
      const price = $(this).data('price');
      if(unit && price){
        location.href = '<%=ctxPath %>/point/charge?unit=' + unit + '&amount=' + price;
      }
    });
  });
</script>
  
</head>
<body>

<div class="app-wrap">
  <aside class="app-left">
    <jsp:include page="../../include/common/asideNavigation.jsp" />
  </aside>

  <main class="app-main">
    <div class="balance-bar">
      <div>
        <span>보유 음표</span>
        <span class="num">
          <fmt:formatNumber value="${empty pointBalance ? 3000 : pointBalance}" pattern="#,###"/>
        </span>
        <span>음표</span>
      </div>
      <button type="button" class="btn btn-outline-lightgray">상세보기</button>
    </div>

    <div class="point-tabs">
      <div class="point-tab active">음표충전</div>
      <div class="point-tab">충전내역</div>
      <div class="point-tab">사용내역</div>
    </div>

    <div class="row-wrap">
      <section class="list-col">
        <div class="point-list">
          <c:choose>
            <c:when test="${not empty chargeOptions}">
              <c:set var="options" value="${chargeOptions}"/>
            </c:when>
            <c:otherwise>
              <c:set var="options" value="1:100,5:500,10:1000,30:3000,50:5000"/>
            </c:otherwise>
          </c:choose>

          <c:forEach var="opt" items="${fn:split(options, ',')}">
            <c:set var="pair"  value="${fn:split(opt, ':')}"/>
            <c:set var="unit"  value="${pair[0]}"/>
            <c:set var="price" value="${pair[1]}"/>

            <div class="point-item">
              <div class="point-left">
                <i class="icon-note">♪</i>
                <div class="point-title">음표 ${unit}개</div>
              </div>
              <button type="button" class="btn-charge" data-unit="${unit}" data-price="${price}">
                <fmt:formatNumber value="${price}" pattern="#,###"/>원
              </button>
            </div>
          </c:forEach>
        </div>
      </section>

      <div class="col-divider"></div>

      <section class="list-col">
        <div class="point-list">
          <div class="point-item">
            <div class="point-left"><i class="icon-note">♪</i><div class="point-title">음표 100개</div></div>
            <button type="button" class="btn-charge" data-unit="100" data-price="10000">10,000원</button>
          </div>
          <div class="point-item">
            <div class="point-left"><i class="icon-note">♪</i><div class="point-title">음표 200개</div></div>
            <button type="button" class="btn-charge" data-unit="200" data-price="20000">20,000원</button>
          </div>
          <div class="point-item">
            <div class="point-left"><i class="icon-note">♪</i><div class="point-title">음표 300개</div></div>
            <button type="button" class="btn-charge" data-unit="300" data-price="30000">30,000원</button>
          </div>
          <div class="point-item">
            <div class="point-left"><i class="icon-note">♪</i><div class="point-title">음표 500개</div></div>
            <button type="button" class="btn-charge" data-unit="500" data-price="50000">50,000원</button>
          </div>
          <div class="point-item">
            <div class="point-left"><i class="icon-note">♪</i><div class="point-title">음표 1,000개</div></div>
            <button type="button" class="btn-charge" data-unit="1000" data-price="100000">100,000원</button>
          </div>
        </div>
      </section>
    </div>
  </main>

  <aside class="app-right">
    <jsp:include page="../include/asidePlayList.jsp" />
  </aside>
</div>

</body>
</html>

--%>