<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>  

<%
  String ctxPath = request.getContextPath();
%>

<html>
<jsp:include page="../../include/common/head.jsp" />
<link rel="stylesheet" href="../../css/mypage.css" />

<style>
 .tabs.eumpyoTab {
 	display: flex;
 	border-bottom: 1px solid #E0E0E0;
 	margin: 60px 0 30px;
 }
 
 .tabs.eumpyoTab > li {
 	flex: 1;
 	font-size: 18px;
 }
 .tabs.eumpyoTab .tab {
 	display: block; text-align: center;
 	padding: 16px;
 	font-size: 18px;
 	color: #5A5A5A;
 	font-weight: 500;
 	position: relative;
 	text-decoration: none;
 }
 
 .tabs.eumpyoTab .tab:hover {
 	color: #5A5A5A;
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
 
 .item-left, .item-right {
 	display: flex;
 	align-items: center;
 	gap: 14px;
 	flex-wrap: nowrap;
 	min-width: 0;
 }
 
 .chargeOptions {
 	position: relative;
 	width: 100%;
 	padding: 0px 30px;
 }
 
 .coinIcon {
 	width: 28px;
 	border-radius: 6px;
 	background: #F3F2F8;
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
 }
 
 .btn-charge:hover {
 	background:#7547FF;
 }
</style>

<script type="text/javascript" src="https://service.iamport.kr/js/iamport.payment-1.1.2.js"></script>

<script type="text/javascript">
  	// PortOne(Iamport) 초기화
  	var IMP = window.IMP; IMP.init('imp26556260'); // 가맹점 식별코드

  	// 금액 버튼 클릭 이벤트
  	$(document).on('click', '.btn-charge', function () {
    	var $button = $(this);
    	var paymentAmount = parseInt($button.data('amount'), 10); // 결제금액(원)
    	if (!paymentAmount || paymentAmount <= 0) {
      		alert('선택한 금액이 올바르지 않습니다.');
      		return;
    	}

    	// 결제 준비
    	$.ajax({
      		url: '<%= ctxPath %>/mypage/eumpyo/charge/ready',
      		type: 'POST',
      		contentType: 'application/json; charset=UTF-8',
      		dataType: 'json',
      		data: JSON.stringify({ amount: paymentAmount }),
      		success: function (readyResponse) {
        		if (!readyResponse || readyResponse.result !== 'success' ||
            		!readyResponse.merchantUid ||
            		typeof readyResponse.amountKRW !== 'number' ||
            		typeof readyResponse.chargedCoin !== 'number') {
          				alert(readyResponse?.message || '지금은 결제를 시작할 수 없습니다.');
          				return;
        			}

       			// PortOne 결제창 호출
       			IMP.request_pay({
         			pg: 'html5_inicis',
        			pay_method: 'card',
        		 	merchant_uid: readyResponse.merchantUid,
         			name: '음표 ' + readyResponse.chargedCoin + '개',
         			amount: readyResponse.amountKRW
       			}, function (paymentResponse) {
         			if (!paymentResponse || !paymentResponse.success) {
           				const reason = paymentResponse?.error_msg || '';
           				if (reason.includes('취소')) {
           					alert('결제를 취소하셨습니다.');
           				}
           				else {
           					alert('결제가 완료되지 않았습니다.');
           				}
           				return;
         			}

         			// 결제 완료 검증
         			$.ajax({
           				url: '<%= ctxPath %>/mypage/eumpyo/charge/complete',
           				type: 'POST',
           				contentType: 'application/json; charset=UTF-8',
           				dataType: 'json',
           				data: JSON.stringify({
             				impUid: paymentResponse.imp_uid,
             				merchantUid: paymentResponse.merchant_uid
           				}),
           				success: function (verifyResult) {
          				 	if (verifyResult && verifyResult.result == 'success') {
             					alert(
	                  				'음표 충전이 완료되었습니다.\n' +
	                  				'결제금액: ₩' + (verifyResult.amount || 0).toLocaleString() + '\n' +
	                  				'충전음표: ' + (verifyResult.chargedCoin || 0).toLocaleString() + '개\n' +
	                  				'보유음표: ' + (verifyResult.coinBalance || 0).toLocaleString() + '개'
             					);

             					// 결제 후 표시값은 항상 users.coin 재조회로 동기화
             					$.ajax({
             						url: '<%= ctxPath %>/mypage/eumpyo/charge/balance',
             						type: 'GET',
             						dataType: 'json',
             						success: function(r) {
               							if (r && r.result === 'success') {
                 							var $coinBalance = $('#myCoinBalance');
                 							if ($coinBalance.length) {
                   								$coinBalance.text((r.coinBalance || 0).toLocaleString());
                 							}
              			 				}
             						}
             					});
           					} else {
             						alert(verifyResult?.message || '결제 확인에 실패했습니다.');
           					}
           				},
           				error: function () {
             				alert('결제 확인 중 오류가 발생했습니다.');
           				}
         			});
       			});
      		},
      		error: function () {
        		alert('처리 중 오류가 발생했습니다.');
			}
    	});
  	}); // end of click handler
</script>

<body>
	<div id="wrap">
  		<main class="charge">

	    	<%-- 왼쪽 사이드 네비게이션 & 관련 팝업들 --%>
	    	<jsp:include page="../../include/common/asideNavigation.jsp" />
	
	    	<%-- 메인 컨텐츠 시작 --%>
	    	<div class="main-contents">
	      		<div class="inner">
	
	        		<%-- 보유 음표 영역 --%>
	        		<jsp:include page="../../include/mypage/mypagePointInfo.jsp" />
	
	        		<%-- 음표 충전 탭 --%>
	        		<jsp:include page="../../include/mypage/eumpyoChargeTab.jsp" />
	
	        		<%-- 음표충전 리스트 시작 --%>
	        		<div class="row columns-2">
	          			<div class="col-lg-6 chargeOptions">
	            			<div class="chargePrices">
	              				<div class="row-item">
	                				<div class="item-left">
	                  					<img src="<%= ctxPath%>/images/mypage/eumpyo.png" class="coinIcon" />
	                  					<div class="item-title">음표 1개</div>
	                				</div>
	                				<button type="button" class="price-btn btn-charge" data-amount="100" data-coin="1">100원</button>
	              				</div>
	              				<div class="row-item">
	               					 <div class="item-left">
	                  					<img src="<%= ctxPath%>/images/mypage/eumpyo.png" class="coinIcon" />
	                  					<div class="item-title">음표 5개</div>
	                				 </div>
	                				<button type="button" class="price-btn btn-charge" data-amount="500" data-coin="5">500원</button>
	              				</div>
	              				<div class="row-item">
	               				 	<div class="item-left">
	                  					<img src="<%= ctxPath%>/images/mypage/eumpyo.png" class="coinIcon" />
	                  					<div class="item-title">음표 10개</div>
	                				</div>
	                				<button type="button" class="price-btn btn-charge" data-amount="1000" data-coin="10">1,000원</button>
	              				</div>
	              				<div class="row-item">
	                				<div class="item-left">
	                  					<img src="<%= ctxPath%>/images/mypage/eumpyo.png" class="coinIcon" />
	                  					<div class="item-title">음표 30개</div>
	                				</div>
	                				<button type="button" class="price-btn btn-charge" data-amount="3000" data-coin="30">3,000원</button>
	              				</div>
	              				<div class="row-item">
	                				<div class="item-left">
	                  					<img src="<%= ctxPath%>/images/mypage/eumpyo.png" class="coinIcon" />
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
	                  					<img src="<%= ctxPath%>/images/mypage/eumpyo.png" class="coinIcon" />
	                  					<div class="item-title">음표 100개</div>
	                				</div>
	                				<button type="button" class="price-btn btn-charge" data-amount="10000" data-coin="100">10,000원</button>
	              				</div>
	              				<div class="row-item">
	                				<div class="item-right">
	                  					<img src="<%= ctxPath%>/images/mypage/eumpyo.png" class="coinIcon" />
	                  					<div class="item-title">음표 200개</div>
	                				</div>
	                				<button type="button" class="price-btn btn-charge" data-amount="20000" data-coin="200">20,000원</button>
	              				</div>
	              				<div class="row-item">
	                				<div class="item-right">
	                  					<img src="<%= ctxPath%>/images/mypage/eumpyo.png" class="coinIcon" />
	                  					<div class="item-title">음표 300개</div>
	                				</div>
	                				<button type="button" class="price-btn btn-charge" data-amount="30000" data-coin="300">30,000원</button>
	              				</div>
	              				<div class="row-item">
	                				<div class="item-right">
	                  					<img src="<%= ctxPath%>/images/mypage/eumpyo.png" class="coinIcon" />
	                 			 		<div class="item-title">음표 500개</div>
	                				</div>
	                				<button type="button" class="price-btn btn-charge" data-amount="50000" data-coin="500">50,000원</button>
	              				</div>
	              				<div class="row-item">
	                				<div class="item-right">
	                  					<img src="<%= ctxPath%>/images/mypage/eumpyo.png" class="coinIcon" />
	                  					<div class="item-title">음표 1,000개</div>
	                				</div>
	                				<button type="button" class="price-btn btn-charge" data-amount="100000" data-coin="1000">100,000원</button>
	              				</div>
	            			</div>
	          			</div>
	        		</div>
	        		<%-- 음표충전 리스트 끝 --%>
	
	      		</div>
	    	</div>
	    	<%-- 메인 컨텐츠 끝 --%>
	
	    	<%-- 오늘의 감정 플레이리스트 --%>
	    	<jsp:include page="../../include/common/asidePlayList.jsp" />
	    	
  		</main>
	</div>
</body>
</html>