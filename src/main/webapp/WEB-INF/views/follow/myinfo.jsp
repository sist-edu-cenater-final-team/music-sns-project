<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<% String ctxPath = request.getContextPath(); %>

<html>
<jsp:include page="../include/common/head.jsp" />
<style>
	.profile-container {
	  margin: 100px auto 0 auto;
	  max-width: 900px;
	}

    body {
      font-family: 'Noto Sans KR', sans-serif;
      background-color: #fafafa;
    }
    a {
      text-decoration: none !important;
      color: inherit;
    }

    /* 프로필 이미지 */
    .profile-img {
      width: 120px;
      height: 120px;
      object-fit: cover;
      border: 2px solid #ddd;
    }

    /* 통계 */
    .profile-stats div {
      font-size: 16px;
    }
    .profile-stats strong {
      font-size: 20px;
    }

    /* 버튼 스타일 */
.custom-btn {
  width:200px;
  background-color: #f7f5fb; /* 연한 회색-보라톤 */
  border-radius: 8px; /* 둥근 모서리 */
  font-weight: bold;
}
.custom-btn:hover {
  background-color: #eceaf1; /* hover 시 조금 진하게 */
}

    /* 드롭다운 버튼 */
    .dropdown-toggle::after {
      margin-left: 8px;
    }
</style>
<body>
	<div id="wrap">
		<main class="">
			<jsp:include page="../include/common/asideNavigation.jsp" />

			<!-- 가운데 프로필 영역 -->
			<div class="col-10 p-4 profile-container">
				<!-- 상단 프로필 정보 -->
				<div class="d-flex align-items-center mb-4">
					<img src="https://via.placeholder.com/150"
						class="rounded-circle mr-4 profile-img"
						alt="프로필 이미지">

					<div>
						<h4 class="mb-3">leess</h4>
						<div class="d-flex mb-3 mr-3 profile-stats">
							<span class="mr-2" style="color: #CCCCCC; font-weight: 500; font-size: 13pt">게시물</span>
							<div class="text-center mr-3">
								<strong>300</strong>
							</div>
							<div class="mr-3 text-center">
								<span class="mr-2" style="color: #CCCCCC; font-weight: 500; font-size: 13pt">팔로워</span>
								<a href="<%= ctxPath%>/mypage/myFollowers" class="text-dark ml-2 mr-2 text-center">
									<strong >33.3만</strong>
								</a>
							</div>
							<div class="text-center">
								<span class="mr-3" style="color: #CCCCCC; font-weight: 600; font-size: 13pt">팔로우</span>
								<a href="<%= ctxPath%>/mypage/myFollowing" class="text-dark text-center">
									<strong>333</strong>
								</a>
							</div>
						</div>
						
						<div>
							이순신
						</div>
						
						<p class="mb-0 text-muted">
							자기소개 자기소개 자기소개 자기소개 자기소개 자기소개 자기소개 자기소개 자기소개 자기소개
						</p>
					</div>
				</div>

				<!-- 버튼 영역 -->
				<div class="d-flex mb-5">
					  <button class="btn  px-4 py-2 mx-2 custom-btn">프로필 편집</button>
					  <button class="btn  px-4 py-2 mx-2 custom-btn">위시리스트</button>
					  <button class="btn  px-4 py-2 mx-2 custom-btn">장바구니</button>
					  <button class="btn  px-4 py-2 mx-2" style="background-color: #f7f5fb; font-weight: bold; border-radius: 8px" data-toggle="collapse" data-target="#moreMenu">
					    ...
					  </button>
				</div>
				<div class="collapse mt-2 text-center" id="moreMenu">
				  <div class="card card-body">
				    <a href="#" class="d-block py-1">주문내역</a>
				    <a href="#" class="d-block py-1">쿠폰함</a>
				    <a href="#" class="d-block py-1">로그아웃</a>
				  </div>
				</div>

				<!-- 게시물 없음 영역 -->
				<div class="text-center py-5">
					<p class="mb-1">작성하신 게시물이 없습니다.</p>
					<a href="/create-post" class="text-primary font-weight-bold">첫 게시물을 만들어보세요.</a>
				</div>
			</div>

			<jsp:include page="../include/common/asidePlayList.jsp" />
		</main>
	</div>
</body>
</html>