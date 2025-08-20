<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:include page="../include/common/head.jsp"/>
<!-- Optional custom CSS -->
<link href="${pageContext.request.contextPath}/css/music/album.css" rel="stylesheet">
<!-- Our JS -->
<script src="${pageContext.request.contextPath}/js/music/album.js" defer></script>
<!doctype html>
<html lang="ko">
<body>

<div id="wrap">
    <main class="">
        <%-- 왼쪽 사이드 네비게이션 & 관련 팝업들 --%>
        <jsp:include page="../include/common/asideNavigation.jsp"/>
        <%-- //왼쪽 사이드 네비게이션 & 관련 팝업들 --%>

        <div class="main-contents">
            <div class="inner">
                <div class="album-container">
                    <div class="album-top-head">
                        <div class="album-image-container">
                            <img class="album-image loading" src="" alt="Album Image">
                            <div class="loading-spinner"></div>
                        </div>
                        <div class="album-top-right">

                            <div class="album-header">
                                <div class="album-info">
                                    <div class="info-box">
                                        <p class="album-type"></p>
                                        <h1 class="album-name"></h1>
                                        <p class="album-artists"></p>
                                        <p class="album-genres"></p>
                                        <p class="album-release-date"></p>
                                    </div>
                                </div>
                                <a class="spotify-link" href="#" target="_blank"></a>

                            </div>
                            <div class="album-bottom">
                                <p class="album-duration"></p>
                                <p class="album-label"></p>
                            </div>
                        </div>
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

                    <div class="track-list-container">
                        <div class="track-list-header">
                            <div class="track-header-number">#</div>
                            <div class="track-header-title">제목</div>
                            <div class="track-header-artist">아티스트</div>
                            <div class="track-header-duration"><i class="bi bi-clock"></i></div>
                            <div class="track-header-action"></div>
                        </div>
                        <div class="track-list-body">

                        </div>
                    </div>


                </div>
            </div>
        </div>

        <%-- 오늘의 감정 플레이리스트 --%>
        <jsp:include page="../include/common/asidePlayList.jsp"/>
        <%-- //오늘의 감정 플레이리스트 --%>
    </main>
</div>
</body>
</html>

