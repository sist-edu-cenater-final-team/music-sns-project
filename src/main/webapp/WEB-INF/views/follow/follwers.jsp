<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../include/common/head.jsp"/>
<script src="<%= request.getContextPath() %>/js/follow/follow.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/follow/follow.css"/>

<body>


<div id="wrap">
    <main class="">
        <%-- 왼쪽 사이드 네비게이션 & 관련 팝업들 --%>
        <jsp:include page="../include/common/asideNavigation.jsp"/>
        <%-- //왼쪽 사이드 네비게이션 & 관련 팝업들 --%>

        <!-- 메인 컨텐츠 -->
        <div class="main-contents">
            <div class="inner">
                <div class="followes-container">
                    <div>
                        <div class="tabs">
                            <span class="tab active" id="following"></span>
                            <span class="tab" id="followers"></span>
                            <span class="tab" id="favorite"></span>
                        </div>
                    </div>


                    <!-- 검색창 -->
                    <div class="mt-3" style="position: relative; margin-bottom: 12px;">
                        <input type="text" id="searchInput" placeholder="닉네임, 이메일, 전화번호 검색"/>
                        <button type="button" id="clearBtn">×</button>

                        <div id="searchResult" class="search-result"
                             style="display: none;"></div>
                    </div>

                    <div class="follow-container"></div>
                </div>
            </div>
        </div>
        <!-- //메인 컨텐츠 -->

        <%-- 오늘의 감정 플레이리스트 --%>
        <jsp:include page="../include/common/asidePlayList.jsp"/>
        <%-- //오늘의 감정 플레이리스트 --%>
    </main>
</div>
</body>
</html>
