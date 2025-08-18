<%--
  Created by IntelliJ IDEA.
  User: user
  Date: 25. 8. 11.
  Time: 오후 4:49
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:include page="../include/common/head.jsp" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/music/chart/chart.css"/>
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
                <div class="container" style="margin-left: 130px; width: 80%;">
                    <div class="app-header">
                        <div>
                            <h3 class="mb-0">실시간 차트 뷰어</h3>
                            <div class="small-muted">음원 제공처: Melon · VIBE · genie · Bugs</div>
                        </div>

                        <div class="d-flex align-items-center gap-2">
                            <div id="tabs" class="d-flex gap-2">
                                <div class="source-pill source-melon" data-source="melon"><img style="height:1.2em;vertical-align:middle;" alt="멜론 로고" src="https://upload.wikimedia.org/wikipedia/commons/c/c5/Melon_logo.png"/></div>
                                <div class="source-pill source-vibe active"  data-source="vibe"><img style="height:1em;vertical-align:middle;" alt="바이브 로고" src="https://i.namu.wiki/i/sxMPdR5wxHn0htIVvoEg3LUxfG2Mu4wOXTvETGl9u_ksTBHKIItx6Ht_xS8y_N80rCKmkKKeh9Cw2ZXrrVIZIg.svg"/></div>
                                <div class="source-pill source-genie" data-source="genie"><img style="height:1.5em;vertical-align:middle;" alt="지니 로고" src="https://www.geniemusic.co.kr/assets/images/common/sub_love_logo01.png"/></div>
                                <div class="source-pill source-bugs"  data-source="bugs"><img style="height:1.3em;vertical-align:middle;" alt="벅스 로고" src="${pageContext.request.contextPath}/images/music/logo/bugs-music.png"/></div>
                            </div>
                        </div>
                    </div>

                    <div class="card mb-3">
                        <div class="card-body">
                            <div class="d-flex justify-content-between align-items-center mb-3">
                                <div>
                                    <strong id="chart-title">Genie · 실시간 차트 (상위 100)</strong><br />
                                    <span id="chart-sub" class="small-muted">마지막 업데이트: -</span>
                                </div>
                                <div>
                                    <button id="refreshBtn" class="btn btn-sm btn-outline-primary"><i class="bi bi-arrow-clockwise"></i> 새로고침</button>
                                </div>
                            </div>

                            <div id="content">
                                <div id="loading" class="centered">
                                    <div class="spinner-border" role="status" aria-hidden="true"></div>
                                    <span class="ms-3 small-muted">차트 불러오는 중...</span>
                                </div>

                                <div id="error" class="alert alert-warning d-none" role="alert"></div>

                                <div id="tracks" class="chart-grid" style="display:none;"></div>
                            </div>
                        </div>
                    </div>

                    <footer class="text-center small-muted">
                        제공된 JSON 응답을 axios로 받아 화면에 렌더링합니다.
                    </footer>
                </div>
            </div>
        </div>

        <%-- 오늘의 감정 플레이리스트 --%>
        <jsp:include page="../include/common/asidePlayList.jsp" />
        <%-- //오늘의 감정 플레이리스트 --%>
    </main>
</div>






<script src="${pageContext.request.contextPath}/js/music/chart/chart.js"></script>
</body>
</html>
