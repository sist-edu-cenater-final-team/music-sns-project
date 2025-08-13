<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<% String ctxPath = request.getContextPath(); %>
<html>
<head>
    <title>Title</title>
    <link rel="stylesheet" href="<%=ctxPath%>/css/reset.css">
    <link rel="stylesheet" href="<%= ctxPath %>/lib/bootstrap-4.6.2-dist/css/bootstrap.min.css">
    <script src="<%=ctxPath%>/lib/jquery-3.7.1.min.js"></script>
    <script src="<%= ctxPath %>/lib/bootstrap-4.6.2-dist/js/bootstrap.bundle.min.js"></script>
    <link rel="stylesheet" href="<%=ctxPath%>/css/common.css" />
    <!-- TUI CSS (jsDelivr로 교체) -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/tui-color-picker@2.2.8/dist/tui-color-picker.min.css" />
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/tui-image-editor@3.15.3/dist/tui-image-editor.min.css" />

    <!-- JS (순서 중요, jsDelivr로 교체) -->
    <script src="https://cdn.jsdelivr.net/npm/fabric@3.6.3/dist/fabric.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/tui-code-snippet@2.3.2/dist/tui-code-snippet.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/tui-color-picker@2.2.8/dist/tui-color-picker.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/tui-image-editor@3.15.3/dist/tui-image-editor.min.js"></script>

    <link rel="stylesheet" href="<%=ctxPath%>/css/indexPost.css" />
    <script src="<%=ctxPath%>/js/indexPost.js"></script>
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

<!-- post 모달 -->
<div class="modal fade" id="postModal" tabindex="-1" aria-labelledby="postModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-lg">
        <div class="modal-content">

            <!-- 헤더 -->
            <div class="modal-header">
                <h5 class="modal-title" id="postModalLabel">새 게시물 만들기</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="닫기">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>

            <!-- 바디 -->
            <div class="modal-body text-center">

                <!-- STEP 1 -->
                <div id="step1" style="width: 500px; margin: 15% auto;">
                    <textarea class="form-control mb-3" id="contents" name="contents" rows="4" placeholder="문구를 입력하세요..."></textarea>
                    <div class="d-flex justify-content-between">
                        <button type="button" class="btn btn-secondary" id="btnNext">다음</button>
                        <button type="button" class="btn btn-primary" id="btnUploadStep1">올리기</button>
                    </div>
                </div>

                <!-- STEP 2 -->
                <div id="step2" style="display:none;">
                    <button type="button" class="btn btn-secondary" id="imageSave">이미지저장</button>
                    <div id="tui-image-editor" style="height:500px;"></div>
                    <button type="button" class="btn btn-primary mt-3" id="btnNextStep2">다음</button>
                </div>

                <div id="step3" style="display:none;">
                    <img id="previewImage" style="width: 500px; height: 500px;" class="mt-3"/>
                    <div class="mt-3" style="width: 500px; height: 200px; margin: auto" >
                        <p id="previewText" style="text-align: left"></p>
                    </div>
                    <button type="button" id="btnUploadStep3" class="btn btn-success mt-5 mb-4">올리기</button>
                </div>

            </div>
        </div>
    </div>
</div>
<%-- 여기까지 post 모달 --%>

<script src="<%=ctxPath%>/js/common.js"></script>
</body>
</html>
