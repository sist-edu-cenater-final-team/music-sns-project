<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<jsp:include page="../include/common/head.jsp" />
<script src="${pageContext.request.contextPath}/js/cart/order.js" defer></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/mypage.css" />
<body>
<div id="wrap">
    <main id="musicOrder" class="cart-container">
        <%-- 왼쪽 사이드 네비게이션 & 관련 팝업들 --%>
        <jsp:include page="../include/common/asideNavigation.jsp" />
        <%-- //왼쪽 사이드 네비게이션 & 관련 팝업들 --%>

        <!-- 메인 컨텐츠 -->
        <div class="main-contents">
            <div class="inner">
				<%-- 보유 음표 영역 --%>
				<jsp:include page="../include/mypage/mypagePointInfo.jsp" />
				<%-- 보유 음표 영역 --%>
				<h2 class="cart-title">주문 확인</h2>
                <table class="music-cart-table table">
                    <thead>
                    <tr>
                        <th scope="col">번호</th>
                        <th scope="col">노래제목</th>
                        <th scope="col">아티스트</th>
                        <th scope="col">앨범</th>
                    </tr>
                    </thead>
                    <tbody id="orderCartBody"></tbody>
					<tfoot id="orderCartFoot">
						<tr>
							<td colspan="4" class="text-left">
								<span>총 결제 음표 : </span>
								<span id="orderTotalPrice">0</span> 음표
							</td>
						</tr>
					</tfoot>
                </table>
                <div class="btn-form">
                    <button type="button" class="btn btn-order">주문 확정하기</button>
                </div>
            </div>
            <%-- 주문 정보 --%>
            <jsp:include page="../include/common/asidePlayList.jsp" />
            <%-- //오늘의 감정 플레이리스트 --%>
        </div>
            <!-- //메인 컨텐츠 -->
    </main>
</div>
</body>
</html>
