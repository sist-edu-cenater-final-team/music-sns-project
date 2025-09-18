<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<% String ctxPath = request.getContextPath(); %>
<html>
<jsp:include page="../include/common/head.jsp" />
<body>
<div id="wrap">
  <main class="">
    <%-- 왼쪽 사이드 네비게이션 & 관련 팝업들 --%>
    <jsp:include page="../include/common/asideNavigation.jsp" />
    <%-- //왼쪽 사이드 네비게이션 & 관련 팝업들 --%>

    <!-- 메인 컨텐츠 -->
    <div class="main-contents">

      <div class="d-flex justify-content-center align-items-center"
           style="min-height: 100vh; padding: 2rem;">

        <div class="post-edit-container d-flex shadow rounded overflow-hidden"
             style="max-width: 1100px; width: 800px; height: 650px; margin: 0 auto;">

          <!-- 왼쪽: 이미지 캐러셀 -->
          <div class="post-left bg-dark" style="flex: 2; height: 100%;">
            <div id="postCarousel" class="carousel slide h-100" data-interval="false">
              <div class="carousel-inner h-100">
                <%-- JS에서 동적으로 채워넣음 --%>
              </div>
              <a class="carousel-control-prev" href="#postCarousel" role="button" data-slide="prev">
                <span class="carousel-control-prev-icon"></span>
              </a>
              <a class="carousel-control-next" href="#postCarousel" role="button" data-slide="next">
                <span class="carousel-control-next-icon"></span>
              </a>
            </div>
          </div>

          <!-- 오른쪽: 수정 폼 -->
          <div class="post-right bg-white p-4 d-flex flex-column justify-content-between" style="flex: 1;">
            <form id="postEditForm" action="${ctxPath}/post/update" method="post" class="h-100 d-flex flex-column">
              <input type="hidden" id="postId" name="postId" value="${param.postId}">

              <!-- 제목 -->
              <div class="form-group mb-3 flex-grow-0">
                <label for="editTitle" class="font-weight-bold">제목</label>
                <textarea class="form-control no-border" id="editTitle" name="editTitle" rows="2"></textarea>
              </div>

              <!-- 내용 -->
              <div class="form-group mb-3 flex-grow-1">
                  <label for="content" class="font-weight-bold">내용</label>
                  <textarea class="form-control no-border h-100" id="content" name="content"></textarea>
              </div>

              <!-- 감정 -->
              <div class="form-group mb-3 editEmotion">
                <label class="font-weight-bold">오늘의 감정</label>
                <div class="emotions">
                  <button type="button" class="btn natural active" value="CALM"><span class="blind">평온</span></button>
                  <button type="button" class="btn happy" value="HAPPY"><span class="blind">행복</span></button>
                  <button type="button" class="btn love" value="LOVE"><span class="blind">사랑</span></button>
                  <button type="button" class="btn sad" value="SAD"><span class="blind">우울</span></button>
                  <button type="button" class="btn angry" value="ANGRY"><span class="blind">분노</span></button>
                  <button type="button" class="btn tire" value="TIRED"><span class="blind">힘듬</span></button>
                </div>
              </div>

              <!-- 버튼 -->
              <div class="text-right">
                <button type="button" class="btn btn-primary editButton">수정하기</button>
                <a href="/index" class="btn btn-secondary">취소</a>
              </div>
            </form>
          </div>
        </div>
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

