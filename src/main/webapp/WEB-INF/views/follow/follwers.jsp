<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<html>
<% String ctxPath = request.getContextPath(); %>
<jsp:include page="../include/common/head.jsp" />
<body>
<style type="text/css">

div#container {

margin-left: 8%;
}	

.tabs {
	display: flex;
	gap: 10px;
	margin: 10px 0;
}

.tab {
	padding: 10px 16px;
	cursor: pointer;
	border-style: none;
	background-color: white;
	font-weight: 480;
	font-size: 30px;
}

.tab.active {
	padding: 10px 16px;
	cursor: pointer;
	border-style: none;
	background-color: white;
	font-weight: 450;
	font-size: 30px;
	margin-right: 30px;
}

#listContainer {
	display: grid;
	grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
	gap: 12px;
}

.card {
	padding: 12px;
	border: 1px solid #e0e0e0;
	border-radius: 8px;
	display: flex;
	gap: 10px;
	align-items: center;
	background: #fff;
}

.thumb {
	width: 48px;
	height: 48px;
	border-radius: 50%;
	background: #ddd;
	overflow: hidden;
	flex: 0 0 48px;
}

.thumb img {
	width: 100%;
	height: 100%;
	object-fit: cover;
}

.meta {
	flex: 1;
}

.name {
	font-weight: 600;
}

.sub {
	font-size: 0.9em;
	color: #666;
}

.btn-follow {
	padding: 6px 10px;
	border-radius: 6px;
	cursor: pointer;
	border: none;
}

.btn-follow.following {
	background: #e0e0e0;
}

#searchInput {
	padding: 6px 30px 6px 10px;
	border-radius: 6px;
	border: 0px solid #ccc;
	width: 550px;
	height: 40px;
	box-sizing: border-box;
	background-color: #f5f5f5;
	outline: none;
}

button#clearBtn {
	position: absolute;
	right: 6px;
	top: 50%;
	transform: translateY(-50%);
	border: none;
	background: transparent;
	font-weight: bold;
	cursor: pointer;
	font-size: 25px;
	color: #999;
	display: none;
	z-index: 10
}
</style>

	<script type="text/javascript">

$(function(){
	const userId = '41';
	$('span#followers').css('color', '#e0e0e0');
	
	  // 입력에 따라 X 버튼 보이기/숨기기
	  $('#searchInput').on('input', function(){
	    if ($(this).val().length > 0) {
	      $('#clearBtn').show();
	    } else {
	      $('#clearBtn').hide();
	    }
	  });

	  // X 버튼 클릭 시 검색창 초기화
	  $('#clearBtn').on('click', function(){
	    $('#searchInput').val('').focus();
	    $(this).hide();


	  });
	  
	  
	  // 팔로우 클릭시
	  $('span#followers').on('click',function(){
		  $('span#followers').css('color', 'black');
		  $('span#following').css('color', '#e0e0e0');
		  getfollower(userId);
	  });
	  
	  // 팔로워 클릭시
	  $('span#following').on('click',function(){
		  $('span#following').css('color', 'black');
		  $('span#followers').css('color', '#e0e0e0');	  
		  getfollowing(userId);
	  })
	  
	  
	  
	}); // end of function(){}

	function getfollowing(userId) { // 내가 팔로워
		
		$.ajax({
			url:'<%= ctxPath%>/api/follow/follower/' + userId,
			type:'get',
			dataType:'json',
			success:function(json) {
				console.log(JSON.stringify(json));
				
				
			},
			error: function(request, status, error){
				   alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
			} 
			
			
		});
		
	};
	
	function getfollower(userId) { // 나를 팔로잉
		
		$.ajax({
			url:'<%= ctxPath%>/api/follow/followee/' + userId,
			type:'get',
			dataType:'json',
			success:function(json) {
				console.log(JSON.stringify(json));
				
				
			},
			error: function(request, status, error){
				   alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
			} 
			
			
		});
		
	}; // end of getfollower
	
	
</script>

	<div id="wrap">
		<main class="">
			<%-- 왼쪽 사이드 네비게이션 & 관련 팝업들 --%>
			<jsp:include page="../include/common/asideNavigation.jsp" />
			<%-- //왼쪽 사이드 네비게이션 & 관련 팝업들 --%>

			<!-- 메인 컨텐츠 -->
			<div class="main-contents">
				<div class="inner">
				
					<div class="container">
						<div>
							<div class="tabs">
								<span class="tab active" id ="following">팔로우 333명</span> 
								<span class="tab" id ="followers">팔로워 n명</span>
							</div>
						</div>

						<!-- 검색창 -->
						<div class="mt-3"
							style="position: relative; margin-bottom: 12px; width: 520px;">
							<input type="text" id="searchInput" placeholder="검색" />
							<button id="clearBtn">×</button>
						</div>
					</div>
					
				</div>
			</div>
			<!-- //메인 컨텐츠 -->

			<%-- 오늘의 감정 플레이리스트 --%>
			<jsp:include page="../include/common/asidePlayList.jsp" />
			<%-- //오늘의 감정 플레이리스트 --%>
		</main>
	</div>
</body>
</html>
