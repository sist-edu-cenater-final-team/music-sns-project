<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<jsp:include page="../include/common/head.jsp" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/mypage.css" />
<body>
<div id="wrap">
    <main id="orderComplete" class="cart-container">
        <%-- 왼쪽 사이드 네비게이션 & 관련 팝업들 --%>
        <jsp:include page="../include/common/asideNavigation.jsp" />
        <%-- //왼쪽 사이드 네비게이션 & 관련 팝업들 --%>

        <!-- 메인 컨텐츠 -->
        <div class="main-contents">
            <div class="inner">
                <h2 class="cart-title">주문 완료</h2>
               	<p>주문이 완료 되었습니다!</p>
                <div class="btn-form">
					<button type="button" class="btn btn-main" onclick="link.goMain()">메인페이지 가기</button>
					<button type="button" class="btn btn-order" onclick="link.goPurchaseList()">구매내역 보러가기</button>
                </div>
            </div>
            <%-- 주문 정보 --%>
            <jsp:include page="../include/common/asidePlayList.jsp" />
            <%-- //오늘의 감정 플레이리스트 --%>
        </div>
		<!-- //메인 컨텐츠 -->
    </main>
</div>
<script>
	const link = {
        goMain : () => {
            location.href = `${ctxPath}/`;
		},
		goPurchaseList : () => {
            location.href = `${ctxPath}/mypage/eumpyo/purchaseHistory`;
        }
	}
</script>
</body>
</html>
