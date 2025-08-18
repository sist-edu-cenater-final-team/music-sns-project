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
<link href="${pageContext.request.contextPath}/css/music/search/music-search.css" rel="stylesheet">
<!-- Our JS -->
<script src="${pageContext.request.contextPath}/js/music/search/music-search.js" defer></script>
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



                <div class="container py-4" id="music-search-root">
                    <!-- 검색 폼 (GET 전송으로 페이지 이동) -->
                    <form id="searchForm" class="card p-3 mb-4" action="${pageContext.request.contextPath}/music/search" method="get">
                        <div class="row g-2 align-items-center">
                            <div class="col-auto">
                                <select class="form-select form-select-sm" id="searchCategory" name="searchType">
                                    <option value="all">전체</option>
                                    <option value="track">제목</option>
                                    <option value="artist">아티스트</option>
                                    <option value="album">앨범</option>
                                </select>
                            </div>

                            <div class="col">
                                <input type="text" id="searchKeyword" name="keyword" class="form-control form-control-sm" placeholder="검색어를 입력하세요" value="${param.keyword != null ? param.keyword : '' }">
                            </div>
                        </div>

<%--                        <input type="hidden" id="inputPage" name="page" value="${param.page != null ? param.page : '1'}">--%>
<%--                        <input type="hidden" id="inputSize" name="size" value="${param.size != null ? param.size : '20'}">--%>

                        <div class="mt-3">
                            <button id="btnSearch" class="btn btn-purple w-100" type="submit">검색</button>
                        </div>
                    </form>

                    <!-- 메타 -->
                    <div id="searchMeta" class="mb-3 text-muted d-none"></div>

                    <!-- 결과 리스트: 1열(전체 너비) -->
                    <div id="searchResults" class="list-group list-group-flush"></div>

                    <div class="text-center mt-3">
                        <button id="loadMoreBtn" class="btn btn-loadmore d-none">
                            더보기 <i class="bi bi-chevron-down arrow-down"></i>
                        </button>
                    </div>



                    <div id="loading" class="text-center my-4 d-none">
                        <div class="spinner-border text-purple" role="status"><span class="visually-hidden">Loading...</span></div>
                    </div>

                    <div id="errorBox" class="alert alert-danger d-none"></div>
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

