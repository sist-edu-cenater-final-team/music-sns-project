<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
  String ctxPath = request.getContextPath();
%>

<jsp:include page="../../include/common/head.jsp" />

<meta name="ctxPath" content="<%= ctxPath %>">

<meta name="userName"  content="<c:out value='${loginUser.name}'/>">
<meta name="userEmail" content="<c:out value='${loginUser.email}'/>">
<meta name="userPhone" content="<c:out value='${loginUser.phone}'/>">

<link rel="stylesheet" href="<%= ctxPath %>/css/mypage.css" />

<!-- PortOne -->
<script src="https://service.iamport.kr/js/iamport.payment-1.1.2.js"></script>

<script src="https://cdn.jsdelivr.net/npm/axios@1.7.7/dist/axios.min.js"></script>

<script id="currentUserJSON" type="application/json">
{
  "name": "<c:out value='${loginUser.name}'/>",
  "email": "<c:out value='${loginUser.email}'/>",
  "phoneNumber": "<c:out value='${loginUser.phone}'/>"
}
</script>

<script>
  // 가맹점 식별코드
  window.PORTONE_IMP = window.PORTONE_IMP || 'imp26556260';

  try {
    window.currentUser = JSON.parse(document.getElementById('currentUserJSON').textContent) || {};
  } catch (e) {
    window.currentUser = {};
  }
</script>

<script src="<%= ctxPath %>/js/eumpyo/charge.js" defer></script>

<style>
	.tabs.eumpyoTab {
		display: flex;
		border-bottom: 1px solid #E0E0E0;
		margin: 60px 0 30px;
	}

	.tabs.eumpyoTab .tab {
		display: block;
		text-align: center;
		padding: 16px;
		font-size: 18px;
		color: #5A5A5A;
		font-weight: 500;
		position: relative;
		text-decoration: none;
	}

	.tabs.eumpyoTab .tab:hover {
		font-weight: 700;
		text-decoration: none;
	}

	.tabs.eumpyoTab .tab.active {
		border-bottom: 3px solid;
		color: #6633FF;
		font-weight: 700;
	}

	.row-item {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 25px 30px;
		border-bottom: 1px solid #E0E0E0;
	}

	.item-left,
	.item-right {
		display: flex;
		align-items: center;
		gap: 14px;
		flex-wrap: nowrap;
		min-width: 0;
	}

	.chargeOptions {
		position: relative;
		width: 100%;
		padding: 0 30px;
	}

	.coinIcon {
		width: 28px;
		border-radius: 6px;
		background: #F3F2F8;
		display: block;
	}

	.item-title {
		color: #5A5A5A;
		font-size: 16px;
		font-weight: 500;
		white-space: nowrap;
		word-break: keep-all;
		overflow: hidden;
		text-overflow: ellipsis;
		max-width: 100%;
	}

	.btn-charge {
		min-width: 100px;
		border: none;
		border-radius: 8px;
		padding: 8px 10px;
		font-size: 16px;
		font-weight: 600;
		background: #6633FF;
		color: #fff;
		cursor: pointer;
	}

	.btn-charge:hover {
		background: #7547FF;
	}
</style>

<body>
	<div id="wrap">
		<main class="charge">
			<jsp:include page="../../include/common/asideNavigation.jsp" />
			<div class="main-contents">
				<div class="inner">
					<jsp:include page="../../include/mypage/mypagePointInfo.jsp" />
					<jsp:include page="../../include/mypage/eumpyoChargeTab.jsp" />

					<div class="row columns-2">
						<div class="col-lg-6 chargeOptions">
							<div class="chargePrices">
								<div class="row-item">
									<div class="item-left">
										<img src="<%= ctxPath %>/images/mypage/eumpyo.png" class="coinIcon" />
										<div class="item-title">음표 1개</div>
									</div>
									<button type="button" class="price-btn btn-charge" data-amount="100" data-coin="1">100원</button>
								</div>
								<div class="row-item">
									<div class="item-left">
										<img src="<%= ctxPath %>/images/mypage/eumpyo.png" class="coinIcon" />
										<div class="item-title">음표 5개</div>
									</div>
									<button type="button" class="price-btn btn-charge" data-amount="500" data-coin="5">500원</button>
								</div>
								<div class="row-item">
									<div class="item-left">
										<img src="<%= ctxPath %>/images/mypage/eumpyo.png" class="coinIcon" />
										<div class="item-title">음표 10개</div>
									</div>
									<button type="button" class="price-btn btn-charge" data-amount="1000" data-coin="10">1,000원</button>
								</div>
								<div class="row-item">
									<div class="item-left">
										<img src="<%= ctxPath %>/images/mypage/eumpyo.png" class="coinIcon" />
										<div class="item-title">음표 30개</div>
									</div>
									<button type="button" class="price-btn btn-charge" data-amount="3000" data-coin="30">3,000원</button>
								</div>
								<div class="row-item">
									<div class="item-left">
										<img src="<%= ctxPath %>/images/mypage/eumpyo.png" class="coinIcon" />
										<div class="item-title">음표 50개</div>
									</div>
									<button type="button" class="price-btn btn-charge" data-amount="5000" data-coin="50">5,000원</button>
								</div>
							</div>
						</div>

						<div class="col-lg-6 chargeOptions">
							<div class="chargePrices">
								<div class="row-item">
									<div class="item-right">
										<img src="<%= ctxPath %>/images/mypage/eumpyo.png" class="coinIcon" />
										<div class="item-title">음표 100개</div>
									</div>
									<button type="button" class="price-btn btn-charge" data-amount="10000" data-coin="100">10,000원</button>
								</div>
								<div class="row-item">
									<div class="item-right">
										<img src="<%= ctxPath %>/images/mypage/eumpyo.png" class="coinIcon" />
										<div class="item-title">음표 200개</div>
									</div>
									<button type="button" class="price-btn btn-charge" data-amount="20000" data-coin="200">20,000원</button>
								</div>
								<div class="row-item">
									<div class="item-right">
										<img src="<%= ctxPath %>/images/mypage/eumpyo.png" class="coinIcon" />
										<div class="item-title">음표 300개</div>
									</div>
									<button type="button" class="price-btn btn-charge" data-amount="30000" data-coin="300">30,000원</button>
								</div>
								<div class="row-item">
									<div class="item-right">
										<img src="<%= ctxPath %>/images/mypage/eumpyo.png" class="coinIcon" />
										<div class="item-title">음표 500개</div>
									</div>
									<button type="button" class="price-btn btn-charge" data-amount="50000" data-coin="500">50,000원</button>
								</div>
								<div class="row-item">
									<div class="item-right">
										<img src="<%= ctxPath %>/images/mypage/eumpyo.png" class="coinIcon" />
										<div class="item-title">음표 1,000개</div>
									</div>
									<button type="button" class="price-btn btn-charge" data-amount="100000" data-coin="1000">100,000원</button>
								</div>
							</div>
						</div>
					</div>

				</div>
			</div>

			<jsp:include page="../../include/common/asidePlayList.jsp" />
		</main>
	</div>
</body>
</html>
