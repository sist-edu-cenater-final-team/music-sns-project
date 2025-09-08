<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!-- 오늘의 감정 플레이리스트 -->
<script src="${pageContext.request.contextPath}/js/sidePlayList.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/sidePlayList.css"/>
<div class="aside-playlist sidebar">
    <div class="inner">
        <h2>오늘의 감정 플레이리스트</h2>
        <div class="emotions">
            <!-- class active 추가 시 활성화 -->
            <button type="button" id="emotion" class="btn natural active" value="1"><span class="blind">평온</span></button>
            <button type="button" id="emotion" class="btn happy" value="4"><span class="blind">행복</span></button>
            <button type="button" id="emotion" class="btn love" value="3"><span class="blind">사랑</span></button>
            <button type="button" id="emotion" class="btn sad" value="2"><span class="blind">우울</span></button>
            <button type="button" id="emotion" class="btn angry" value="6"><span class="blind">분노</span></button>
            <button type="button" id="emotion" class="btn tire" value="5"><span class="blind">힘듬</span></button>
        </div>

        <h3>추천 음악</h3>
        <div class="recommended-music">
        
        </div>
    </div>
</div>
<!-- //오늘의 감정 플레이리스트 -->
