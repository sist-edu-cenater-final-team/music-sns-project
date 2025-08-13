<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% String ctxPath= request.getContextPath(); %>
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
                <button type="button" class="btn chart">플리</button>
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
        검색 스으윽
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