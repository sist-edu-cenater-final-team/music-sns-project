
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:include page="../include/common/head.jsp" />
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
        <jsp:include page="../include/common/asideNavigation.jsp" />
        <%-- //왼쪽 사이드 네비게이션 & 관련 팝업들 --%>

        <div class="main-contents">
            <div class="inner">
                <div class="album-info">
                    <div class="album-header d-flex align-items-center">
                        <img src="https://i.scdn.co/image/ab67616d0000b273ba5db46f4b838ef6027e6f96" alt="Album Image" class="album-image img-fluid rounded">
                        <div class="album-details ms-3">
                            <h2 class="album-name">÷ (Deluxe)</h2>
                            <p class="artist-name"><a href="https://open.spotify.com/artist/6eUKZXaKkcviH0Ku9w2n3V" target="_blank">Ed Sheeran</a></p>
                            <p class="release-date">Release Date: 2017-03-03</p>
                            <p class="album-type">Album Type: ALBUM</p>
                            <p class="album-genres"><strong>Genres: </strong>Pop</p>
                        </div>
                    </div>

                    <!-- Popularity Gauge -->
                    <div class="popularity">
                        <label for="popularity">Popularity</label>
                        <div class="progress" style="height: 20px;">
                            <div id="popularity-bar" class="progress-bar" role="progressbar" style="width: 86%;" aria-valuenow="86" aria-valuemin="0" aria-valuemax="100"></div>
                        </div>
                        <span id="popularity-text" class="popularity-text">86% Popular</span>
                    </div>
                </div>
                <p class="artist-name">
                    <a href="https://open.spotify.com/artist/6eUKZXaKkcviH0Ku9w2n3V" target="_blank">
                        <i class="bi bi-spotify"></i> Ed Sheeran
                    </a>
                </p>



            </div>
        </div>

        <%-- 오늘의 감정 플레이리스트 --%>
        <jsp:include page="../include/common/asidePlayList.jsp" />
        <%-- //오늘의 감정 플레이리스트 --%>
    </main>
</div>
</body>
</html>

