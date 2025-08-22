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
    width: 90%;
    max-width: 935px;
    background: #fff;
    padding: 40px;
    border-radius: 12px;
}

/* 상단 프로필 영역 */
.profile-top {
    display: flex;
    flex-wrap: wrap; /* 화면 좁아지면 줄바꿈 */
    gap: 20px;
    align-items: center;
}

/* 프로필 이미지 */
.profile-img-wrap {
    flex: 0 0 auto; /* 이미지 고정 */
}
.profile-img {
    width: 150px;
    height: 150px;
    border-radius: 50%;
    object-fit: cover;
    border: 1px solid #dbdbdb;
}

/* 사용자 정보 */
#user_info {
    flex: 1 1 0; /* 남은 공간 채우기 */
    min-width: 0;
    word-break: break-word;
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
    flex-wrap: wrap;
    gap: 20px;
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

/* 버튼 그룹 */
.profile-btns {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    margin: 20px 0;
}
.custom-btn {
    flex: 1 1 120px; /* 최소 120px, 화면 좁으면 밑으로 내려감 */
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
    grid-template-columns: repeat(3, 1fr); /* 최대 3열 고정 */
    gap: 16px;
    justify-content: center; /* 빈 공간 중앙 배치 */
}

/* 모바일 대응 */
@media (max-width: 768px) {
    .post-grid {
        grid-template-columns: repeat(2, 1fr); /* 태블릿에서는 2열 */
    }
}

@media (max-width: 480px) {
    .post-grid {
        grid-template-columns: 1fr; /* 모바일에서는 1열 */
    }
}

/* 각 게시물 */
.post-item img {
    width: 100%;       
    height: 200px;     
    object-fit: cover; 
    border-radius: 8px;
    cursor:pointer;
}

/* 사진 없는 게시물 카드 */
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
}
.post-item.no-image:hover {
    background-color: #e0e0e0;
}

/* 반응형 */
@media (max-width: 768px) {
    .profile-container {
        padding: 20px;
    }
    .profile-img {
        width: 100px;
        height: 100px;
    }
    .profile-container h2 {
        font-size: 20px;
    }
    .custom-btn {
        font-size: 12px;
        min-width: 100px;
    }
    .post-item img, .post-item.no-image {
        height: 150px;
    }
}

@media (max-width: 480px) {
    .profile-stats {
        flex-direction: column;
        gap: 10px;
    }
    .profile-btns {
        flex-direction: column;
    }
}
</style>

<script type="text/javascript">
$(function(){
    const userId = '41';
    getUserPost(userId);
});

function getUserPost(userId) {
    $.ajax({
        url:"<%= ctxPath%>/api/userInfo/post",
        data:{"userId":userId},
        dataType:"json",
        success:function(json){
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
                json.forEach(item => {
                    // postImageUrl이 배열이면 그대로, 문자열이면 배열로 변환
                    let images = Array.isArray(item.postImageUrl) ? item.postImageUrl : (item.postImageUrl ? [item.postImageUrl] : []);
                    if(images.length === 0) {
                        html += `<div class="post-item no-image">\${item.title || ''}</div>`;
                    } else {
                        html += `<div class="post-item"><img src="\${images[0]}" alt="post image"></div>`;
                    }
                });
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
	<main>
		<jsp:include page="../../include/common/asideNavigation.jsp" />

		<div class="main-contents">
			<div class="inner">
				<div class="col-10 p-4 profile-container">

					<!-- 상단 프로필 -->
					<div class="profile-top mb-4">
						<div class="profile-img-wrap">
							<img src="<%= ctxPath%>/images/common/userprofile/test.jpg"
								class="profile-img" alt="">
						</div>

						<div id="user_info">
							<h2 class="mb-3">${mvo.nickname}</h2>
							<div class="profile-stats mb-3">
								<div class="text-center mr-1">
									<span style="font-weight: 600; font-size: 14pt">게시물</span>
									<strong class="mr-2">${mvo.postCount}</strong>
								</div>

								<div class="mr-3 text-center">
									<span style="font-weight: 600; font-size: 14pt">팔로워</span>
									<a href="<%= ctxPath%>/mypage/myFollowers" class="text-dark text-center">
										<strong class="mr-2">${mvo.followeeCount}</strong>
									</a>
								</div>

								<div class="text-center">
									<span style="font-weight: 600; font-size: 14pt">팔로우</span>
									<a href="<%= ctxPath%>/mypage/myFollowers" class="text-dark text-center">
										<strong class="mr-2">${mvo.followerCount}</strong>
									</a>
								</div>
							</div>

							<div class="my-3">${mvo.username}</div>
							<p class="mb-0 text-muted">${mvo.profileMessage}</p>
						</div>
					</div>

					<!-- 버튼 그룹 -->
					<div class="profile-btns">
						<button class="btn custom-btn" onclick="location.href='<%= ctxPath%>/mypage/updateInfo'">프로필 편집</button>
						<button class="btn custom-btn">위시리스트</button>
						<button class="btn custom-btn" onclick="location.href='<%= ctxPath%>/cart/list'">장바구니</button>

						<div class="dropdown">
							<button class="btn custom-btn dropdown-toggle" type="button"
								id="dropdownMenuButton" data-toggle="dropdown"
								aria-haspopup="true" aria-expanded="false">
								...
							</button>
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

					<!-- 게시물 영역 -->
					<div class="post-list"></div>
					
				</div>
			</div>
		</div>

		

		<jsp:include page="../../include/common/asidePlayList.jsp" />
	</main>
</div>
</body>
</html>
