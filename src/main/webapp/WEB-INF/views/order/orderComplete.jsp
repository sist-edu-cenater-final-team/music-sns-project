<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>Muodle : 주문완료</title>
	<script src="${pageContext.request.contextPath}/lib/jquery-3.7.1.min.js"></script>
	<script src="${pageContext.request.contextPath}/js/auth/token.js"></script>
	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">

	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/reset.css" />
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css" />
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/order.css" />
<body>
<div id="wrap">
	<div class="order-container">
		<div class="order-wrapper">
			<!-- 로고 섹션 -->
			<div class="logo-section">
				<div class="logo">
					<i class="bi bi-music-note-beamed"></i>
					<span>Music SNS</span>
				</div>
				<p class="welcome-text">음악과 함께하는 특별한 순간</p>
			</div>

			<!-- 로그인 폼 -->
			<div class="order-form-container">
				<h2 class="form-title">주문 완료</h2>
				<p class="form-text">주문이 완료 되었습니다!</p>
				<div class="btn-form">
					<button type="button" class="btn" onclick="link.goMain()">메인페이지 가기</button>
					<button type="button" class="btn" onclick="link.goPurchaseMusicList()">구매한 음악 보러가기</button>
					<button type="button" class="btn" onclick="link.goPurchaseList()">구매내역 보러가기</button>
				</div>
			</div>
		</div>
	</div>
</div>




<script>
	const link = {
        goMain : () => {
            location.href = `${ctxPath}/`;
		},
        goPurchaseMusicList : () => {
            location.href = `${ctxPath}/mypage/purchase/list`;
        },
		goPurchaseList : () => {
            location.href = `${ctxPath}/mypage/eumpyo/purchaseHistory`;
        }
	}
</script>
</body>
</html>
