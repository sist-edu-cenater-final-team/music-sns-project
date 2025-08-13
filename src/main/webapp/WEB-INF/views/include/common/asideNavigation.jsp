<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% String ctxPath= request.getContextPath(); %>


<link rel="stylesheet" href="<%=ctxPath%>/css/music/search/search.css" />
<script type="text/javascript" src="<%=ctxPath%>/js/music/search/search.js"></script>
<!-- 왼쪽 네비게이션 사이드바 -->
<div class="aside-navigation sidebar">
    <div class="inner">
        <a href="javascript:;" class="btn-logo"><span class="blind">홈으로가기</span></a>
        <ul class="navigation-list">
            <li>
                <button type="button" class="btn home">홈</button>
            </li>
            <li>
                <button type="button" class="btn search" data-target="searchLayer">검색</button>
            </li>
            <li>
                <button type="button" class="btn chart" onclick="location.href='<%=ctxPath%>/music/chart'">플리</button>
            </li>
            <li>
                <button type="button" class="btn noti"  data-target="notiLayer">알림</button>
            </li>
            <li>
                <button type="button" class="btn profile">프로필</button>
            </li>
            <li>
                <button type="button" class="btn setting">설정</button>
            </li>
            <li>
                <button type="button" class="btn post" data-toggle="modal" data-target="#postModal">게시물작성</button>
            </li>
        </ul>
    </div>
</div>
<!-- //왼쪽 네비게이션 사이드바 -->

<!-- 클릭했을 때 나오는 스으윽 팝업 -->
<div id="searchLayer" class="aside-navigation-layer sidebar">
    <div class="inner">
        <form id="searchForm" action="${pageContext.request.contextPath}/music/search" method="get" class="search-container">
            <div class="search-top">
                <label for="sideSearchCategory"></label>
                <select name="searchType" id="sideSearchCategory" class="search-select">
                    <option value="all">전체</option>
                    <option value="track">제목</option>
                    <option value="artist">아티스트</option>
                    <option value="album">앨범</option>
                </select>
                <label for="sideSearchKeyword"></label>
                <input type="text" name="keyword" id="sideSearchKeyword" class="search-input" placeholder="검색어를 입력하세요">
            </div>
            <button type="submit" class="search-btn">검색</button>
        </form>
    </div>
</div>
<div id="notiLayer" class="aside-navigation-layer sidebar">
    <div class="inner">
        알림 스으윽
    </div>
</div>
<!-- //클릭했을 때 나오는 스으윽 팝업 -->

<div class="fixed-talk">
    <button type="button" id="btnTalk" class="btn-talk">메시지</button>
</div>
<div id="talkLayer" class="layer">
    <div class="layer-header">
        <h3 class="layer-title">메시지</h3>
        <button type="button" id="btnTalkClose" class="layer-close">X</button>
    </div>
    <div class="layer-body">
        <ul class="talk-list">
            <li>
                <div class="talk-img">
                    <img src="<%= ctxPath%>/images/emotion/angry.png" alt="사용자 프로필 이미지" />
                </div>
                <div class="talk-info">
                    <p class="talk-title">HanBinId</p>
                    <p class="talk-text">7강의실 아이돌 한빈</p>
                </div>
            </li>
            <li>
                <div class="talk-img">
                    <img src="<%= ctxPath%>/images/emotion/angry.png" alt="사용자 프로필 이미지" />
                </div>
                <div class="talk-info">
                    <p class="talk-title">HanBinId</p>
                    <p class="talk-text">7강의실 아이돌 한빈</p>
                </div>
            </li>
            <li>
                <div class="talk-img">
                    <img src="<%= ctxPath%>/images/emotion/angry.png" alt="사용자 프로필 이미지" />
                </div>
                <div class="talk-info">
                    <p class="talk-title">HanBinId</p>
                    <p class="talk-text">7강의실 아이돌 한빈</p>
                </div>
            </li>
            <li>
                <div class="talk-img">
                    <img src="<%= ctxPath%>/images/emotion/angry.png" alt="사용자 프로필 이미지" />
                </div>
                <div class="talk-info">
                    <p class="talk-title">HanBinId</p>
                    <p class="talk-text">7강의실 아이돌 한빈</p>
                </div>
            </li>
            <li>
                <div class="talk-img">
                    <img src="<%= ctxPath%>/images/emotion/angry.png" alt="사용자 프로필 이미지" />
                </div>
                <div class="talk-info">
                    <p class="talk-title">HanBinId</p>
                    <p class="talk-text">7강의실 아이돌 한빈</p>
                </div>
            </li>
            <li>
                <div class="talk-img">
                    <img src="<%= ctxPath%>/images/emotion/angry.png" alt="사용자 프로필 이미지" />
                </div>
                <div class="talk-info">
                    <p class="talk-title">HanBinId</p>
                    <p class="talk-text">7강의실 아이돌 한빈</p>
                </div>
            </li>
            <li>
                <div class="talk-img">
                    <img src="<%= ctxPath%>/images/emotion/angry.png" alt="사용자 프로필 이미지" />
                </div>
                <div class="talk-info">
                    <p class="talk-title">HanBinId</p>
                    <p class="talk-text">7강의실 아이돌 한빈</p>
                </div>
            </li>
            <li>
                <div class="talk-img">
                    <img src="<%= ctxPath%>/images/emotion/angry.png" alt="사용자 프로필 이미지" />
                </div>
                <div class="talk-info">
                    <p class="talk-title">HanBinId</p>
                    <p class="talk-text">7강의실 아이돌 한빈</p>
                </div>
            </li>
            <li>
                <div class="talk-img">
                    <img src="<%= ctxPath%>/images/emotion/angry.png" alt="사용자 프로필 이미지" />
                </div>
                <div class="talk-info">
                    <p class="talk-title">HanBinId</p>
                    <p class="talk-text">7강의실 아이돌 한빈</p>
                </div>
            </li>
            <li>
                <div class="talk-img">
                    <img src="<%= ctxPath%>/images/emotion/angry.png" alt="사용자 프로필 이미지" />
                </div>
                <div class="talk-info">
                    <p class="talk-title">HanBinId</p>
                    <p class="talk-text">7강의실 아이돌 한빈</p>
                </div>
            </li>
        </ul>
    </div>
    <button type="button" class="btn-talk-write">작성버튼</button>
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