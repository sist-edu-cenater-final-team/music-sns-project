<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<jsp:include page="../../include/common/head.jsp" />
<script src="${pageContext.request.contextPath}/js/purchase/purchase.js" defer></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/mypage.css" />
<body>
<div id="wrap">
	<main id="musicPurchase" class="cart-container">
		<%-- 왼쪽 사이드 네비게이션 & 관련 팝업들 --%>
		<jsp:include page="../../include/common/asideNavigation.jsp" />
		<%-- //왼쪽 사이드 네비게이션 & 관련 팝업들 --%>

		<!-- 메인 컨텐츠 -->
		<div class="main-contents">
			<div class="inner">
				<%-- 보유 음표 영역 --%>
				<jsp:include page="../../include/mypage/mypagePointInfo.jsp" />
				<%-- 보유 음표 영역 --%>
				<h2 class="cart-title">내가 구매한 음악리스트</h2>
				<div id="myPurchaseMusic"></div>
			</div>
		</div>
		<!-- //메인 컨텐츠 -->

		<%-- 오늘의 감정 플레이리스트 --%>
		<jsp:include page="../../include/common/asidePlayList.jsp" />
		<%-- //오늘의 감정 플레이리스트 --%>
	</main>
</div>
</body>
</html>