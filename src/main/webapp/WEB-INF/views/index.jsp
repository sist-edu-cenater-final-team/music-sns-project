<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<<<<<<< HEAD
<html>
<jsp:include page="include/common/head.jsp" />
<body>
<div id="wrap">
    <main class="">
        <%-- 왼쪽 사이드 네비게이션 & 관련 팝업들 --%>
        <jsp:include page="include/common/asideNavigation.jsp" />
        <%-- //왼쪽 사이드 네비게이션 & 관련 팝업들 --%>

        <!-- 메인 컨텐츠 -->
        <div class="main-contents">
            <div class="inner">
                메인컨텐츠츠츠으으
            </div>
        </div>
        <!-- //메인 컨텐츠 -->

        <%-- 오늘의 감정 플레이리스트 --%>
        <jsp:include page="include/common/asidePlayList.jsp" />
        <%-- //오늘의 감정 플레이리스트 --%>
    </main>
</div>
=======
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<% String ctxPath = request.getContextPath(); %>
<html>
<jsp:include page="include/common/head.jsp" />
<body>
<div id="wrap">
    <main class="">
        <%-- 왼쪽 사이드 네비게이션 & 관련 팝업들 --%>
        <jsp:include page="include/common/asideNavigation.jsp" />
        <%-- //왼쪽 사이드 네비게이션 & 관련 팝업들 --%>

        <!-- 메인 컨텐츠 -->
        <div class="main-contents">
            <div class="inner">
                메인컨텐츠츠츠으으
            </div>
        </div>
        <!-- //메인 컨텐츠 -->

        <%-- 오늘의 감정 플레이리스트 --%>
        <jsp:include page="include/common/asidePlayList.jsp" />
        <%-- //오늘의 감정 플레이리스트 --%>
    </main>
</div>

>>>>>>> branch 'main' of https://github.com/sist-edu-cenater-final-team/music-sns-project.git
</body>
</html>
