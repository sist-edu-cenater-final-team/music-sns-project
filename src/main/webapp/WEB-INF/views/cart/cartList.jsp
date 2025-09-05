<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<jsp:include page="../include/common/head.jsp" />
<script src="${pageContext.request.contextPath}/js/cart/cart.js" defer></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/mypage.css" />
<body>
<div id="wrap">
    <main id="musicCart" class="cart-container">
        <%-- 왼쪽 사이드 네비게이션 & 관련 팝업들 --%>
        <jsp:include page="../include/common/asideNavigation.jsp" />
        <%-- //왼쪽 사이드 네비게이션 & 관련 팝업들 --%>

        <!-- 메인 컨텐츠 -->
        <div class="main-contents">
            <div class="inner">
                <%-- 보유 음표 영역 --%>
                <jsp:include page="../include/mypage/mypagePointInfo.jsp" />
                <%-- 보유 음표 영역 --%>
                <%-- 음악 장바구니 리스트 --%>
                <div class="music-cart-top">
                    <div class="btn-form">
                        <button type="button" class="btn btn-delete">선택 삭제</button>
                        <button type="button" class="btn btn-order">주문하기</button>
                    </div>
                    <div class="music-check-info">
                        <div>
                            <p class="title">담겨있는 곡 수 : </p>
                            <p class="text"><span id="musicCartCount">0</span>곡</p>
                        </div>
                        <div>
                            <p class="title">선택 곡 수 : </p>
                            <p class="text"><span id="musicCount">0</span>곡</p>
                        </div>
                        <div>
                            <p class="title point">총 결제 음표 : </p>
                            <p class="text"><span id="musicPrice">0</span>음표</p>
                        </div>
                    </div>
                </div>
                <table class="music-cart-table table">
                    <thead>
                        <tr>
                            <th scope="col">
								<label class="check-form">
									<input type="checkbox" id="cartAllCheck" />
									<span class="check"></span>
								</label>
                            </th>
                            <th scope="col">번호</th>
                            <th scope="col">노래제목</th>
                            <th scope="col">아티스트</th>
                            <th scope="col">앨범</th>
                            <th scope="col">가격</th>
                            <th scope="col">삭제</th>
                        </tr>
                    </thead>
                    <tbody id="cartBody"></tbody>
                </table>
                <%-- //음악 장바구니 리스트 --%>
            </div>
        </div>
        <!-- //메인 컨텐츠 -->

        <%-- 오늘의 감정 플레이리스트 --%>
        <jsp:include page="../include/common/asidePlayList.jsp" />
        <%-- //오늘의 감정 플레이리스트 --%>
    </main>
</div>
</body>
</html>