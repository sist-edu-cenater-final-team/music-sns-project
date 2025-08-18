<%--
  Created by IntelliJ IDEA.
  User: user
  Date: 25. 8. 13.
  Time: 오전 10:39
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:include page="../include/common/head.jsp" />
<!-- Optional custom CSS -->
<link href="${pageContext.request.contextPath}/css/music/artist.css" rel="stylesheet">
<!-- Our JS -->
<script src="${pageContext.request.contextPath}/js/music/artist.js" defer></script>
<!doctype html>
<html lang="ko">
<body>

<div id="wrap">
    <main class="">
        <%-- 왼쪽 사이드 네비게이션 & 관련 팝업들 --%>
        <jsp:include page="../include/common/asideNavigation.jsp" />
        <%-- //왼쪽 사이드 네비게이션 & 관련 팝업들 --%>

        <div class="main-contents">
            <div class="inner">
                <div class="artist-profile">
                    <div class="artist-header">
                        <img class="artist-image" src="" alt="아티스트 이미지">
                        <div class="artist-info">
                            <h1 class="artist-name"></h1>
                            <p class="artist-genres"></p>
                            <p class="artist-followers"></p>
                        </div>
                        <a class="spotify-link" href="#" target="_blank">Spotify에서 보기</a>
                    </div>
                    <div class="stat-item">
                        <span class="stat-label"><i class="bi bi-bar-chart-fill"></i> 인기도</span>
                        <div class="progress popularity-bar">
                            <div class="progress-bar bg-success" role="progressbar"
                                 style="width: 0%" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100">
                                0%
                            </div>
                        </div>
                    </div>

<%--                    <div class="artist-stats">--%>
<%--                        <div class="stat-item">--%>
<%--                            <span class="stat-label"><i class="bi bi-bar-chart-fill"></i> 인기도</span>--%>
<%--                            <div class="progress popularity-bar">--%>
<%--                                <div class="progress-bar bg-success" role="progressbar"--%>
<%--                                     style="width: 0%" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100">--%>
<%--                                    0%--%>
<%--                                </div>--%>
<%--                            </div>--%>
<%--                        </div>--%>
<%--                    </div>--%>
                </div>

                <div class="album-container">
                    <div id="album-list" class="album-list"></div>
                    <button id="load-more" class="load-more" style="display: none;">더보기</button>
                </div>
            </div>
        </div>

        <%-- 오늘의 감정 플레이리스트 --%>
        <jsp:include page="../include/common/asidePlayList.jsp" />
        <%-- //오늘의 감정 플레이리스트 --%>
    </main>
</div>
</body>
</html>

