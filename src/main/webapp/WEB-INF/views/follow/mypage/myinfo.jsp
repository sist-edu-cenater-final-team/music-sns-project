<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<% String ctxPath = request.getContextPath(); %>

<html>
<jsp:include page="../../include/common/head.jsp" />
<style>
body {
    font-family: 'Noto Sans KR', sans-serif;
    background-color: #fafafa;
    color: #262626;
    margin: 0;
}

/* 컨테이너 */
.profile-container {
    margin: 40px auto;
    width: 80%;
    max-width: 935px;
    background: #fff;
    padding: 40px;
    border: 0px;
    border-radius: 12px;
}

/* 프로필 이미지 */
.profile-img {
    width: 150px;
    height: 150px;
    border-radius: 50%;
    object-fit: cover;
    border: 1px solid #dbdbdb;
}

#user_info {
	margin-left: 3%;

}

/* 이름 */
.profile-container h2 {
    font-size: 24px;
    font-weight: 600;
    margin-bottom: 12px;
}

/* 통계 */
.profile-stats {
    display: flex;
    gap: 30px;
    margin-bottom: 12px;
}
.profile-stats span {
    font-size: 16px;
    font-weight: 500;
    color: #262626;
}
.profile-stats strong {
    font-weight: 600;
    margin-left: 6px;
}

/* 소개 */
.profile-container p {
    font-size: 14px;
    color: #555;
    margin-top: 8px;
}

/* 버튼 */
.custom-btn {
    flex: 1;
    height: 36px;
    border-radius: 8px;
    border: 1px solid #dbdbdb;
    background: #fff;
    font-size: 14px;
    font-weight: 600;
    color: #262626;
    transition: background 0.2s;
}
.custom-btn:hover {
    background: #fafafa;
}

/* 드롭다운 버튼 */
.dropdown-toggle {
    width: 36px;
    height: 36px;
    border-radius: 8px;
    border: 1px solid #dbdbdb;
    font-size: 18px;
    text-align: center;
    padding: 0;
    background: #fff;
}
.dropdown-toggle::after {
    display: none !important;
}

/* 드롭다운 메뉴 */
.dropdown-menu {
    border-radius: 8px;
    font-size: 14px;
    border: 1px solid #dbdbdb;
    padding: 4px 0;
}
.dropdown-item {
    padding: 8px 14px;
}
.dropdown-item:hover {
    background: #fafafa;
}

/* 게시물 없음 영역 */
.empty-posts {
    text-align: center;
    padding: 80px 20px;
    color: #999;
}
.empty-posts a {
    display: inline-block;
    margin-top: 12px;
    font-weight: 600;
    color: #0095f6;
    font-size: 14px;
}
.empty-posts a:hover {
    text-decoration: underline;
}

/* 게시물 그리드 */
.post-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr); /* 컬럼 너비 200px 이상, 유연하게 채움 */
    gap: 16px; /* 아이템 간 간격 */
}

/* 각 게시물 */
.post-item img {
    width: 100%;       
    height: 200px;     
    object-fit: cover; 
    border-radius: 8px;
    cursor:pointer;
}

/* 사진 없는 게시물 카드 스타일 */
.post-item.no-image {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 200px;
    background-color: #f0f0f0;
    border-radius: 8px;
    color: #555;
    font-weight: 600;
    font-size: 16px;
    text-align: center;
    padding: 10px;
    cursor: default;
    transition: background 0.2s;
    cursor:pointer;
}

.post-item.no-image:hover {
    background-color: #e0e0e0;
}
</style>

<script type="text/javascript">

	$(function(){
		const userId = '41';
		
		getUserPost(userId);
		
	}); // enf od $(function(){}) -----------

	function getUserPost(userId) {
	    $.ajax({
	        url:"<%= ctxPath%>/api/userInfo/post",
	        data:{"userId":userId},
	        dataType:"json",
	        success:function(json){
	        	console.log(json);
	            let post = $('.post-list');
	            post.empty();
	            
	            if(json.length === 0) {
	                post.html(
	                    '<div class="empty-posts">' +
	                        '<p class="mb-1">작성하신 게시물이 없습니다.</p>' +
	                        '<a class="btn post" data-toggle="modal" data-target="#postModal">첫 게시물을 만들어보세요.</a>' +
	                    '</div>'
	                );
	            } else {
	                let html = '<div class="post-grid">';
	                for(let i=0; i<json.length; i++){
	                    let item = json[i];
	                    let images = Array.isArray(item.postImageUrl) ? item.postImageUrl : [];
	                    let firstImage = images[0];
	                    let icon = ''
	                    if(images == 0) {
	                    	html += `<div class="post-item no-image">\${item.title} </div>`	
	                    }
	                    else{
		                    html += `<div class="post-item">
		                                <img src="\${firstImage}" alt="post image">
		                            </div>`;
	                    }
	                }
	                html += '</div>';
	                
	                post.html(html);
	            }
	        },
	        error: function(request, status, error) {
	            alert("code: " + request.status + "\nmessage: " + request.responseText + "\nerror: " + error);
	        }
	    });
	}
</script>

<div id="wrap">
	<main class="">
		<%-- 왼쪽 사이드 네비게이션 & 관련 팝업들 --%>
		<jsp:include page="../../include/common/asideNavigation.jsp" />
		<%-- //왼쪽 사이드 네비게이션 & 관련 팝업들 --%>

		<div class="main-contents">
			<div class="inner">
				<div class="col-10 p-4 profile-container">

					<div class="d-flex align-items-center mb-4">
						<img src="<%= ctxPath%>/images/common/userprofile/test.jpg"
							class="rounded-circle mr-4 profile-img" alt="">

						<div id = "user_info">
							<h2 class="mb-3">leess</h2>
							<div class="d-flex mb-3 mr-3 profile-stats">
								<div class="text-center mr-1">
									<span class="mr-3" style="font-weight: 600; font-size: 14pt">게시물</span>
									<a href="" class="text-dark text-center"> <strong
										class="mr-2">300</strong>
									</a>
								</div>

								<div class="mr-3 text-center">
									<span class="mr-3" style="font-weight: 600; font-size: 14pt">팔로워</span>
									<a href="<%= ctxPath%>/mypage/myFollowers"
										class="text-dark text-center"> <strong class="mr-2">33.3만</strong>
									</a>
								</div>

								<div class="text-center">
									<span class="mr-3" style="font-weight: 600; font-size: 14pt">팔로우</span>
									<a href="<%= ctxPath%>/mypage/myFollowers"
										class="text-dark text-center"> <strong class="mr-2">333</strong>
									</a>
								</div>
							</div>

							<div class="my-3">이순신</div>

							<p class="mb-0 text-muted">자기소개 자기소개 자기소개 자기소개 자기소개 자기소개 자기소개
								자기소개 자기소개 자기소개</p>
						</div>
					</div>

					<div class="d-flex my-5">
						<button class="btn flex-fill mx-2 custom-btn">프로필 편집</button>
						<button class="btn flex-fill mx-2 custom-btn">위시리스트</button>
						<button class="btn flex-fill mx-2 custom-btn" onclick="location.href='<%= ctxPath%>/cart/list'">장바구니</button>

						<div class="dropdown">
							<button class="btn mx-2 custom-btn dropdown-toggle" type="button"
								id="dropdownMenuButton" data-toggle="dropdown"
								aria-haspopup="true" aria-expanded="false"
								style="width: 50px; padding: 0 10px;">...</button>
							<div class="dropdown-menu dropdown-menu-right shadow-sm"
								aria-labelledby="dropdownMenuButton"
								style="min-width: 150px; border-radius: 10px;">
								<a class="dropdown-item" href="#">여기에</a> 
								<a class="dropdown-item" href="#">뭐넣어?</a>
								<div class="dropdown-divider"></div>
								<a class="dropdown-item text-danger font-weight-bold" href="#">로그아웃</a>
							</div>
						</div>
					</div>

					<!-- 게시물 없음 영역 -->
					<div class="post-list"></div>
					
				</div>
			</div>
		</div>

		<%-- 오늘의 감정 플레이리스트 --%>
		<jsp:include page="../../include/common/asidePlayList.jsp" />
		<%-- //오늘의 감정 플레이리스트 --%>
	</main>
</div>
</body>
</html>
