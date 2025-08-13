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
  :root{
    --accent:#6C63FF;
    --text:#1e1e2d;
    --muted:#9aa0a6;
    --line:#eceef3;
    --chip:#f6f7fb;
  }
  body{ color:var(--text); background:#fff; }

  /* ▼ 전체 영역: 고정폭 제거하고 좌우 66px 여백만 유지 (반응형) */
  .page-wrap{
    margin:32px auto 80px;
    padding:0 66px;
  }

  /* 상단 보유 음표 배너 좌우 정렬 맞춤 */
  .balance-bar-wrap{ padding:0 66px; } /* NEW */
  .balance-bar{
    display:flex; align-items:center; justify-content:space-between;
    background:var(--chip); border:1px solid var(--line); border-radius:12px;
    padding:12px 16px; margin-bottom:24px;
  }
  .balance-bar .label{ color:#6b7280; font-weight:600; }
  .balance-bar .value{
    color:#4f46e5; font-weight:800; font-size:18px; text-decoration:underline;
    text-underline-offset:2px;
  }

  .row.columns-2{ position:relative; } /* NEW */
  @media (min-width:992px){
    .row.columns-2 > [class*="col-lg-6"]{ position:relative; }
    .row.columns-2 > [class*="col-lg-6"]:not(:first-child)::before{
      content:""; position:absolute; left:-12px; top:0; width:1px; height:100%;
    }
  }

  /* 리스트/패널 기존 스타일 */
  .charge-list .row-item, .usage-list .row-item{
    display:flex; align-items:center; justify-content:space-between;
    padding:14px 8px; border-bottom:1px solid var(--line);
  }
  .row-item:last-child{ border-bottom:0; }
  .item-left{ display:flex; align-items:center; gap:12px; }
  .icon{ display:inline-flex; align-items:center; justify-content:center;
         width:28px; height:28px; border-radius:6px; background:#f3f2ff; font-size:16px; }
  .item-title{ font-weight:600; }
  .item-sub{ font-size:13px; color:var(--muted); }
  .btn-charge,.btn-usage{ min-width:96px; border:none; border-radius:8px; padding:8px 14px;
                          font-weight:700; background:var(--accent); color:#fff; }
  .btn-charge:hover,.btn-usage:hover{ opacity:.9; }
  .price-chip{ display:inline-block; background:#efedff; border:1px solid #e0ddff; color:#3e36d1;
               font-weight:800; border-radius:8px; padding:6px 12px; min-width:96px; text-align:center; }

  @media (max-width: 991.98px){
    .page-wrap{ padding:0 20px; } /* 모바일에서 여백 축소 */
    .col-lg-4 + .col-lg-4{ margin-top:24px; }
    .tabs{ padding:0 20px; }
    .balance-bar-wrap{ padding:0 20px; }
  }
</style>

<script>
  // 상세보기 이동
  $('#btnBalanceDetail').on('click', function(){
    window.location.href = '<%=ctxPath%>/mypage/eumpyo/history';
  });

  // 충전 버튼 클릭 샘플
  $('.btn-charge').on('click', function(){
    const $row = $(this).closest('.row-item');
    const qty  = Number($row.data('qty'));
    const price= Number($row.data('price'));
    $.ajax({
      url: '<%=ctxPath%>/api/eumpyo/charge',
      method: 'POST',
      contentType: 'application/json; charset=UTF-8',
      data: JSON.stringify({ quantity: qty, amount: price }),
      success: function(){ alert('충전 요청이 접수되었습니다. (샘플)'); location.reload(); },
      error: function(){ alert('충전 요청 중 오류가 발생했습니다.'); }
    });
  });
</script>

<body>
<div id="wrap">
    <main class="eumopyoCharge">
        <%-- 왼쪽 사이드 네비게이션 & 관련 팝업들 --%>
        <jsp:include page="../../include/common/asideNavigation.jsp" />
        <%-- //왼쪽 사이드 네비게이션 & 관련 팝업들 --%>

		<%-- 메인 컨텐츠 시작 --%>
        <div class="main-contents">
            <div class="inner">
                <%-- 보유 음표 영역 --%>
                <jsp:include page="../../include/mypage/mypagePointInfo.jsp" />
                
                <%-- 음표 충전 탭 --%>
				<jsp:include page="../../include/mypage/eumpyoChargeTab.jsp" />
			
				<%-- 음표충전 리스트 시작 --%>
			  	<div class="row columns-2">
				    <div class="col-lg-6">
			        	<div class="charge-list">
			          		<div class="row-item" data-qty="1" data-price="100">
				            	<div class="item-left">
				              		<span class="icon">🎵</span>
					              	<div>
					                	<div class="item-title">음표 1개</div>
					              	</div>
				           	 	</div>
				            	<button class="btn-charge" type="button">100원</button>
							</div>
			
			          		<div class="row-item" data-qty="5" data-price="500">
			            		<div class="item-left">
			              			<span class="icon">🎵</span>
			             			 <div class="item-title">음표 5개</div>
			           			</div>
			            		<button class="btn-charge" type="button">500원</button>
			          		</div>
			
			          		<div class="row-item" data-qty="10" data-price="1000">
			            		<div class="item-left">
			              			<span class="icon">🎵</span>
			              			<div class="item-title">음표 10개</div>
			            		</div>
			            		<button class="btn-charge" type="button">1,000원</button>
			          		</div>
			
			          		<div class="row-item" data-qty="30" data-price="3000">
			            		<div class="item-left">
			             			<span class="icon">🎵</span>
			              			<div class="item-title">음표 30개</div>
			            		</div>
			            		<button class="btn-charge" type="button">3,000원</button>
			          		</div>
			
			          		<div class="row-item" data-qty="50" data-price="5000">
			            		<div class="item-left">
			              			<span class="icon">🎵</span>
			              			<div class="item-title">음표 50개</div>
			            		</div>
			           			<button class="btn-charge" type="button">5,000원</button>
			          		</div>
				      	</div>
				    </div>
			
			    
				    <div class="col-lg-6">
			        	<div class="charge-list">
				          	<div class="row-item">
				            	<div class="item-left">
				              		<span class="icon">🎵</span>
				              		<div>
				                		<div class="item-title">음표 100개</div>
				              		</div>
				            	</div>
				            	<span class="price-chip">10,000원</span>
				         	</div>
				
							<div class="row-item">
				            	<div class="item-left">
				              		<span class="icon">🎵</span>
				              		<div class="item-title">음표 200개</div>
				            	</div>
				            	<span class="price-chip">20,000원</span>
				          	</div>
				
				          	<div class="row-item">
				            	<div class="item-left">
				              		<span class="icon">🎵</span>
				             	 	<div class="item-title">음표 300개</div>
				            	</div>
				            	<span class="price-chip">30,000원</span>
			          		</div>
			
			          		<div class="row-item">
			            		<div class="item-left">
			              			<span class="icon">🎵</span>
			              			<div class="item-title">음표 500개</div>
			            		</div>
			            		<span class="price-chip">50,000원</span>
			          		</div>
			
			          		<div class="row-item">
			            		<div class="item-left">
			              			<span class="icon">🎵</span>
			              			<div class="item-title">음표 1,000개</div>
			            		</div>
			            		<span class="price-chip">100,000원</span>
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
        <%-- //오늘의 감정 플레이리스트 --%>
    </main>
</div>

</body>
</html>