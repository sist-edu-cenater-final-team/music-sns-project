<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<jsp:include page="../include/common/head.jsp" />
<script src="${pageContext.request.contextPath}js/cart/cart.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/mypage.css" />
<body>
<div id="wrap">
    <main id="musicCart">
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
                        <button type="button" class="btn btn-payment">결제하기</button>
                    </div>
                    <div class="music-check-info">
                        <div>
                            <p class="title">선택 곡 수 : </p>
                            <p class="text">1곡</p>
                        </div>
                        <div>
                            <p class="title point">총 결제 음표 : </p>
                            <p class="text">1음표</p>
                        </div>
                    </div>
                </div>
                <table class="music-cart-table table">
                    <thead>
                        <tr>
                            <th scope="col">
                                <label for="cartAllCheck">
                                    <input type="checkbox" id="cartAllCheck">
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
                    <tbody>
                        <tr>
                            <td>
                                <label for="cartCheck">
                                    <input type="checkbox" id="cartCheck" name="cartCheck">
                                </label>
                            </td>
                            <td scope="row">1</td>
                            <td>
                                <div class="music-info">
                                    <div class="music-img">
                                        <img src="/static/images/common/no_image.png" alt="노래 이미지" />
                                    </div>
                                    <p class="music-text">Golden</p>
                                </div>
                            </td>
                            <td>
                                <p class="music-artist">데몬 헌터스</p>
                            </td>
                            <td>
                                <p class="music-artist">데몬 헌터스 l HUNTR/X</p>
                            </td>
                            <td>
                                <p class="music-text">1음표</p>
                            </td>
                            <td>
                                <button type="button" class="btn-cart-delete"></button>
                            </td>
                        </tr>
                    </tbody>
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