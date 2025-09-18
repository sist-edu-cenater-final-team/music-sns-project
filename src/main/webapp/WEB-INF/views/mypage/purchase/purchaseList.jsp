<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<jsp:include page="../../include/common/head.jsp" />
<script src="${pageContext.request.contextPath}/js/purchase/purchase.js" defer></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/mypage.css" />
<body>
<div id="wrap">
	<main id="musicPurchase" class="cart-container">
		<%-- 왼쪽 사이드 네비게이션 & 관련 팝업들 --%>
		<jsp:include page="../../include/common/asideNavigation.jsp" />
		<%-- //왼쪽 사이드 네비게이션 & 관련 팝업들 --%>

		<!-- 메인 컨텐츠 -->
		<div class="main-contents">
			<div class="inner">
				<%-- 보유 음표 영역 --%>
				<jsp:include page="../../include/mypage/mypagePointInfo.jsp" />
				<%-- 보유 음표 영역 --%>
				<div class="purchase-tab">
<%--					<button type="button" class="btn active">전체</button>--%>
					<button type="button" class="btn">내가 구매한 음악</button>
<%--					<button type="button" class="btn">선물받은 음악</button>--%>
				</div>
				<table class="music-cart-table table">
					<thead>
						<tr>
							<th scope="col">노래제목</th>
							<th scope="col">아티스트</th>
							<th scope="col">앨범</th>
							<th scope="col">프로필 음악</th>
						</tr>
					</thead>
					<tbody id="myPurchaseMusicBody"></tbody>
				</table>
				<div id="pageBar"></div>
			</div>
		</div>
		<!-- //메인 컨텐츠 -->

		<%-- 오늘의 감정 플레이리스트 --%>
		<jsp:include page="../../include/common/asidePlayList.jsp" />
		<%-- //오늘의 감정 플레이리스트 --%>
	</main>

	<div class="modal fade" id="profileMusicModal" tabindex="-1" aria-hidden="true">
		<div class="modal-dialog modal-dialog-centered modal-lg">
			<div class="modal-content chat-modal-content">
				<div class="modal-header">
					<h5 class="modal-title">프로필 음악 설정하기</h5>
<%--					<button type="button" class="modal-close-btn" data-bs-dismiss="modal" aria-label="닫기" onclick="purchase.closeProfileMusicModal()">--%>
<%--						<i class="bi bi-x"></i>--%>
<%--					</button>--%>
				</div>
				<div id="profileMusicModalBody" class="modal-body p-0">
					<div class="emotions">
						<button type="button" class="btn natural active" data-emotion="CALM"><span class="blind">평온</span></button>
						<button type="button" class="btn happy" data-emotion="HAPPY"><span class="blind">행복</span></button>
						<button type="button" class="btn love" data-emotion="LOVE"><span class="blind">사랑</span></button>
						<button type="button" class="btn sad" data-emotion="SAD"><span class="blind">우울</span></button>
						<button type="button" class="btn angry" data-emotion="ANGRY"><span class="blind">분노</span></button>
						<button type="button" class="btn tire" data-emotion="TIRED"><span class="blind">힘듬</span></button>
					</div>
					<!-- 선택된 음악 정보 -->
					<div id="selectProfileMusic"></div>
				</div>
			</div>
		</div>
	</div>
</div>
</body>
</html>