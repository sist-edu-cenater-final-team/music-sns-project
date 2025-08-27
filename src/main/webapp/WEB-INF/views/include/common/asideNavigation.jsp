<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% String ctxPath= request.getContextPath(); %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<link rel="stylesheet" href="<%=ctxPath%>/css/profile.css" />
<link rel="stylesheet" href="<%=ctxPath%>/css/music/search/search.css" />
<script type="text/javascript" src="<%=ctxPath%>/js/music/search/search.js"></script>

<script type="text/javascript">
	$(function(){
		const userId = '41';
		getInfo(userId);
		
		
		
	}); // end of $(function(){}) -----

	function getInfo(userId) {
		$.ajax({
			url:"<%= ctxPath%>/api/userInfo/getInfo/"+userId,
			dataType:"json",
			success:function(json){
				console.log(json);
			},
			error: function(request, status, error) {
	            alert("code: " + request.status + "\nmessage: " + request.responseText + "\nerror: " + error);
	        }
		});
	}

</script>

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
                <button type="button" class="btn profile" data-target="profileLayer" >프로필</button>
            </li>
            <li>
                <button type="button" class="btn setting">설정</button>
            </li>
            <li>
                <button type="button" class="btn post" data-toggle="modal" data-target="#postModal">게시물작성</button>
            </li>
			<li>
				<button type="button" onclick="location.href='<%=ctxPath%>/mypage/purchase/list'">구매리스트</button>
			</li>
        </ul>
    </div>
</div>
<!-- //왼쪽 네비게이션 사이드바 -->

<!-- 클릭했을 때 나오는 스으윽 팝업 -->
<div id="searchLayer" class="aside-navigation-layer sidebar">
    <div class="inner">
        <form id="sideSearchForm" action="${pageContext.request.contextPath}/music/search" method="get" class="search-container">
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
        <!-- 추천 섹션 -->
        <div class="side-recommend-section">
            <!-- 인기 검색어 -->
            <div class="side-popular-keywords">
                <h4 class="side-section-title">인기 검색어</h4>
                <div id="keywordList" class="side-keyword-list"></div>
            </div>

            <!-- 추천 곡 -->
            <div class="side-recommend-tracks">
                <h4 class="side-section-title">추천 곡</h4>
                <div id="trackList" class="side-track-list"></div>
            </div>
        </div>
    </div>
</div>
<div id="notiLayer" class="aside-navigation-layer sidebar">
    <div class="inner">
        알림 스으윽
    </div>
</div>



<div id="profileLayer" class="aside-navigation-layer sidebar">
  <div class="inner" style="background-color: #F2F0FF;">
  
    <div class="text-center" style="font-weight:bold; font-size: 16pt;">leess</div>
    <div class="text-center" style="font-size: 12pt; margin-top: 10px;">거북선 사랑</div>
    <div>
      <img src="<%= ctxPath%>/images/common/userprofile/test.jpg"
           class="rounded-circle mr-4 profile-img" style="width: 128px; height: 128px; margin: 10px 85px; cursor:pointer;"
            onclick="location.href='<%= ctxPath%>/mypage/myinfo'" />
           
    </div>

    <div class="flex stats">
      
      <div>
        <div class="font-bold number-color">300</div>
        <div class="label-text">게시물</div>
      </div>
      
      <div>
        <div class="font-bold number-color" style="cursor:pointer;" onclick="location.href = '<%= ctxPath%>/mypage/myFollowers'">33.3만</div>
        <div class="label-text">팔로워</div>
      </div>
      
      <div>
        <div class="font-bold number-color" style="cursor:pointer;" onclick="location.href = '<%= ctxPath%>/mypage/myFollowers'">333</div>
        <div class="label-text">팔로우</div>
      </div>
    </div>
	
  </div>

  	<div class="inner">
  		<div class="font-bold" style="display:flex; justify-content: space-between;">
  		<span>leess님의 프로필 음악</span> 
  		<button style="margin-right: 3px;">⋮</button>
  		</div>
  		<div class="playlist-section">

		  <ul class="playlist-list mt-3">
		    <%-- <c:forEach var="song" items="${songs}" varStatus="status" begin="0" end="9"> --%>
		      <li class="playlist-item">
		      	<img src="<%= ctxPath%>/images/common/userprofile/test.jpg" style="width: 40px; height: 40px; margin-right: 10px;"/>
				
				<!-- 여기 foreach문 돌릴거임  -->
		        <div class="song-title">
			        <strong>Golden</strong>
			        <div class="song-artist">
			        	<div class="scrolling-wrapper scrolling">
					        <span>HUNTR/X,EJAE,AUDREY NUNA</span>
					        <span>HUNTR/X, EJAE, AUDREY NUNA</span>
					     </div>
			        </div>
		        </div>
		        
		        <div class="song-button">
		          <button>x</button>
		        </div>
		      </li>
		   
		  </ul>
		</div>
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
                </div>
                <div class="talk-info">
                    <p class="talk-title">HanBinId</p>
                    <p class="talk-text">7강의실 아이돌 한빈</p>
                </div>
            </li>
            <li>
                <div class="talk-img">
                </div>
                <div class="talk-info">
                    <p class="talk-title">HanBinId</p>
                    <p class="talk-text">7강의실 아이돌 한빈</p>
                </div>
            </li>
            <li>
                <div class="talk-img">
                </div>
                <div class="talk-info">
                    <p class="talk-title">HanBinId</p>
                    <p class="talk-text">7강의실 아이돌 한빈</p>
                </div>
            </li>
            <li>
                <div class="talk-img">
                </div>
                <div class="talk-info">
                    <p class="talk-title">HanBinId</p>
                    <p class="talk-text">7강의실 아이돌 한빈</p>
                </div>
            </li>
            <li>
                <div class="talk-img">
                </div>
                <div class="talk-info">
                    <p class="talk-title">HanBinId</p>
                    <p class="talk-text">7강의실 아이돌 한빈</p>
                </div>
            </li>
            <li>
                <div class="talk-img">
                </div>
                <div class="talk-info">
                    <p class="talk-title">HanBinId</p>
                    <p class="talk-text">7강의실 아이돌 한빈</p>
                </div>
            </li>
            <li>
                <div class="talk-img">
                </div>
                <div class="talk-info">
                    <p class="talk-title">HanBinId</p>
                    <p class="talk-text">7강의실 아이돌 한빈</p>
                </div>
            </li>
            <li>
                <div class="talk-img">
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
                <div class="post_step1">
                    <div class="emotions">
                        <!-- class active 추가 시 활성화 -->
                        <button type="button" class="btn natural active" value="CALM"><span class="blind">평온</span></button>
                        <button type="button" class="btn happy" value="HAPPY"><span class="blind">행복</span></button>
                        <button type="button" class="btn love" value="LOVE"><span class="blind">사랑</span></button>
                        <button type="button" class="btn sad" value="SAD"><span class="blind">우울</span></button>
                        <button type="button" class="btn angry" value="ANGRY"><span class="blind">분노</span></button>
                        <button type="button" class="btn tire" value="TIRED"><span class="blind">힘듬</span></button>
                    </div>
                </div>

                <div id="step1" style="width: 500px; margin: 5% auto;">
                    <textarea id="title" name="title" class="form-control mb-3" rows="1" cols="1" placeholder="제목을 입력하세요..."></textarea>
                    <textarea class="form-control mb-3" id="contents" name="contents" rows="4" placeholder="문구를 입력하세요..."></textarea>
                    <div class="d-flex justify-content-between">
                        <button type="button" class="btn btn-secondary" id="btnNext">다음</button>
                        <button type="button" class="btn btn-primary" id="btnUploadStep1" data-context-path="${pageContext.request.contextPath}">올리기</button>
                    </div>
                </div>

                <!-- STEP 2 -->
                <div id="step2" style="display:none;">
                    <button type="button" class="btn btn-secondary mb-3" id="imageSave">이미지저장</button>
                    <div id="selectImageDelete"></div>
                    <div id="tui-image-editor" style="height:500px;"></div>
                    <button type="button" class="btn btn-danger mr-3 mt-3" id="btnBeforeStep2">이전</button>
                    <button type="button" class="btn btn-primary mt-3" id="btnNextStep2">다음</button>
                </div>

                <%-- STEP 3 --%>
                <div id="step3" style="display:none;">
                    <div id="previewCarousel" class="carousel slide mt-3" data-ride="carousel" data-interval="3000" style="width: 500px; height: 500px; margin: auto;">
                        <ol class="carousel-indicators"></ol>
                        <div class="carousel-inner" style="width:100%; height:100%; background:#000;">
                            <!-- 자바스크립트로 슬라이드 아이템 주입 -->
                        </div>
                        <a class="carousel-control-prev" href="#previewCarousel" role="button" data-slide="prev">
                            <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                            <span class="sr-only">이전</span>
                        </a>
                        <a class="carousel-control-next" href="#previewCarousel" role="button" data-slide="next">
                            <span class="carousel-control-next-icon" aria-hidden="true"></span>
                            <span class="sr-only">다음</span>
                        </a>
                    </div>

                    <div class="mt-3" style="width: 500px; height: 100px; margin: auto" >
                        <h3 class="mb-4" id="previewTitle" style="text-align: left; font-weight: bold"></h3>
                        <p id="previewText" style="text-align: left"></p>
                    </div>
                    <button type="button" class="btn btn-danger mr-3 mt-5 mb-4" id="btnBeforeStep3">이전</button>
                    <button type="button" id="btnUploadStep3" class="btn btn-success mt-5 mb-4" data-context-path="${pageContext.request.contextPath}">올리기</button>
                </div>


            </div>
        </div>
    </div>
</div>
<%-- 여기까지 post 모달 --%>