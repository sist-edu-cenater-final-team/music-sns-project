<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<% String ctxPath = request.getContextPath(); %>

<jsp:include page="../include/common/head.jsp" />

<script src="<%= ctxPath%>/js/myUsers/userUpdate.js"></script>
<link rel="stylesheet" href="<%= ctxPath%>/css/users/userUpdate.css" />
<!doctype html>
<html lang="ko">

<style>

}
</style>

<script>


</script>

<body>

	<div id="wrap">
		<main class="">
			<%-- 왼쪽 사이드 네비게이션 & 관련 팝업들 --%>
			<jsp:include page="../include/common/asideNavigation.jsp" />
			<%-- //왼쪽 사이드 네비게이션 & 관련 팝업들 --%>

			<div class="main-contents">
				<div class="inner">
			
					<div>
						<div class="text-center">

								<img id="profile_img" src="" 
						    		class="rounded-circle mb-3" style="width:120px; height:120px; object-fit:cover;" id="profilePreview">
								<div>
									<input type="file" id="profile_image" name="profile_image" style="display: none;">
									<a href="#" onclick="$('#profile_image').click()">프로필 사진 변경</a>	
								</div>
						
							
							<form  method="post" name="updateInfoFrm" enctype="multipart/form-data">
						
								
								<div class="input-box">
								  <span class="fixed-label">닉네임</span>
								  <input type="text" name="nickname"/>
								</div>
								
								<div class="input-box">
								  <span class="fixed-label">이메일</span>
								  <input type="email" />
								</div>
								
								<div class="input-box">
								  <span class="fixed-label">상태 메시지</span>
								  <input type="text" name="profileMessage"/>
								</div>
	
								<div class="input-box">
								  <div class="custom-dropdown">
								    <div class="selected">성별 선택</div>
								    <ul class="options">
								      <li value="MALE" data-value="남성">남성</li>
								      <li value="FEMALE" data-value="여성">여성</li>
								      <li value="UNKNOWN" data-value="미정">미정</li>
								    </ul>
								    <input type="hidden" name="gender" />
								  </div>
								</div>						
								
								
								
								<button type="button" id="submit">o</button>
								<button type="button">x</button>
								
							</form>
						</div>
					</div>
					
				</div>
			</div>
			<%-- 오늘의 감정 플레이리스트 --%>
			<jsp:include page="../include/common/asidePlayList.jsp" />
			<%-- //오늘의 감정 플레이리스트 --%>
		</main>
	</div>


</body>
</html>
