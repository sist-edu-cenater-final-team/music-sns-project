<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<% String ctxPath = request.getContextPath(); %>
<html>
<head>
    <title>Title</title>
    <link rel="stylesheet" href="<%=ctxPath%>/css/reset.css">
    <script src="<%=ctxPath%>/lib/jquery-3.7.1.min.js"></script>
    <link rel="stylesheet" href="<%=ctxPath%>/css/common.css" />
</head>
<body>
<div id="wrap">
    <main class="">
        <%-- 왼쪽 사이드 네비게이션 & 관련 팝업들 --%>
        <jsp:include page="include/asideNavigation.jsp" />
        <%-- //왼쪽 사이드 네비게이션 & 관련 팝업들 --%>

        <!-- 메인 컨텐츠 -->
        <div class="main-contents">
            <div class="inner">
                메인컨텐츠츠츠으으
            </div>
        </div>
        <!-- //메인 컨텐츠 -->

        <%-- 오늘의 감정 플레이리스트 --%>
        <jsp:include page="include/asidePlayList.jsp" />
        <%-- //오늘의 감정 플레이리스트 --%>
    </main>
</div>

<script src="<%=ctxPath%>/js/common.js"></script>
</body>
</html>
