<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<% String ctxPath = request.getContextPath(); %>

<jsp:include page="../include/common/head.jsp" />
<!doctype html>
<html lang="ko">

<style>
.input-box {
  display:flex;
  position: relative;
  background: #f5f5f5;   
  border-radius: 8px;
  padding: 10px; 
  width: 400px;
  height:50px;
  margin: 20px auto;
  
}

.input-box .fixed-label {

  position: absolute;
  top: 6px;
  left: 12px;
  font-size: 12px;
  color: #666;
}

.input-box input {
  border: none;
  outline: none;
  background: transparent;
  width: 100%;
  font-size: 14px;
  margin-top: 14px; /* 라벨과 간격 */
}
.custom-dropdown {
  position: relative;
 
  width: 100%;
  cursor: pointer;
  background: #f5f5f5;
  border-radius: 6px;
  border: none;
  outline: none;
}

.custom-dropdown .selected {
  padding: 10px;
  background: #f5f5f5;
  border-radius: 6px;
  border: none;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
  color: #333;
  outline: none;
}

.custom-dropdown .selected::after {
  content: "Ⅴ";
  font-weight:bold;
  font-size: 15px;
  margin-left: 10px;
  color: #666;
}

.custom-dropdown .options {
  position: absolute;
  top: 100%;
  left: 0;
  width: 100%;
  background: #f5f5f5;
  border: none;
  border-radius: 6px;
  margin-top: 4px;
  display: none;
  max-height: 150px;
  overflow-y: auto;
  z-index: 10;
  list-style: none;
  padding: 0;
  box-shadow: 0 2px 5px rgba(0,0,0,0.1);
}

.custom-dropdown .options li {
  padding: 10px;
  font-size: 14px;
  color: #333;
  cursor: pointer;
}

.custom-dropdown .options li:hover {
  background: #e0e0e0;
}
</style>

<script>

$(function(){

	const authHeader = AuthFunc.getAuthHeader;
    const apiRequest = AuthFunc.apiRequest;

    apiRequest(() =>
    	new Promise((resolve, reject) => {
	        $.ajax({
	            url: '<%= ctxPath%>/api/userInfo/getInfo',
	            headers: authHeader(),
	            dataType: 'json',
	            success: function(json) {
	                // 프로필 이미지
	                if(json.myuser.profile_image) {
	                    $('#profile_img').attr('src', json.myuser.profile_image);
	                }
	
	                // 닉네임
	                $('input[name="nickname"]').val(json.myuser.nickname);
	
	                // 이메일 (읽기 전용)
	                $('input[type="email"]').val(json.myuser.email).prop('readonly', true);
	
	                // 상태 메시지
	                $('input[name="profileMessage"]').val(json.myuser.profileMessage || '');
	
	                // 성별 선택
	                if(json.myuser.gender) {
	                    $('.custom-dropdown .selected').text(json.myuser.gender);
	                    $('input[name="gender"]').val(json.myuser.gender);
	                }
	            },
	            error: function(xhr, textStatus, errorThrown) {
	                // axios 스타일의 에러 객체로 변환
	                const error = new Error(errorThrown || textStatus);
	                error.response = {
	                    status: xhr.status,
	                    statusText: xhr.statusText,
	                    data: xhr.responseJSON || xhr.responseText
	                };
	                error.request = xhr;
	                reject(error);
	        }
	        })
        })
    );
	
	
	
	$("#profile_image").on("change", function(e){
		  let reader = new FileReader();
		  reader.onload = function(e){
		    $("#profile_img").attr("src", e.target.result);
		  }
		  if(this.files && this.files[0]) {
		    reader.readAsDataURL(this.files[0]);
		  }

		});
		

	$(".custom-dropdown .selected").click(function(){
	  $(this).siblings(".options").toggle();
	});
	
	$(".custom-dropdown .options li").click(function(){
	  let value = $(this).data("value");
	  $(this).closest(".custom-dropdown").find(".selected").text(value);
	  $(this).closest(".custom-dropdown").find("input[type=hidden]").val(value);
	  $(this).parent(".options").hide();
	});
	
	// 바깥 클릭 시 옵션 닫기
	$(document).click(function(e){
	  if(!$(e.target).closest(".custom-dropdown").length){
	    $(".custom-dropdown .options").hide();
	  }
	});

	$('button[id="submit"]').on("click", function() {

	    const frm = document.updateInfoFrm;
	    const formData = new FormData(frm);
	    const profile_image = document.getElementById("profile_image");
	    const directory = "user_profile_image";

	    const authHeader = AuthFunc.getAuthHeader;
	    const apiRequest = AuthFunc.apiRequest;

	    // 프로필 이미지가 있으면 먼저 업로드
	    if (profile_image.files.length > 0) {
	        const imgFormData = new FormData();
	        imgFormData.append("files", profile_image.files[0]);
	        imgFormData.append("directory", directory);

	        
	        apiRequest(() => 
	        	new Promise((resolve, reject) => {
			        $.ajax({
			            url: "<%= ctxPath%>/api/storage",
			            type: "post",
			            data: imgFormData,
			            processData: false,
			            contentType: false,
			            headers: authHeader(),
			            dataType: "json",
			            success: function(json) {
			               
			                const profile_image_url = json.success.responseData[0].fileUrl;
			                formData.append("profile_image", profile_image_url);
		
			                // 유저 정보 업데이트
			                updateUserInfo(formData);
			            },
			            error: function(xhr, textStatus, errorThrown) {
			                // axios 스타일의 에러 객체로 변환
			                const error = new Error(errorThrown || textStatus);
			                error.response = {
			                    status: xhr.status,
			                    statusText: xhr.statusText,
			                    data: xhr.responseJSON || xhr.responseText
			                };
			                error.request = xhr;
			                reject(error);
			        	}
			        })
	        	})
	        );
	    } else {
	       
	        updateUserInfo(formData);
	    }
	});

	// 유저 정보 업데이트 함수
	function updateUserInfo(formData) {

	    const authHeader = AuthFunc.getAuthHeader;
	    const apiRequest = AuthFunc.apiRequest;
	    
	    apiRequest(() =>
	    	new Promise((resolve, reject) => {
		    	$.ajax({
			        url: "<%= ctxPath%>/api/userInfo/update",
			        type: "post",
			        data: formData,
			        processData: false,
			        contentType: false,
			        headers: authHeader(),
			        dataType: "json",
			        success: function(json) {
			            console.log("프로필 업데이트 성공:", json);
			            alert("업데이트 성공");
			            location.href="<%= ctxPath%>/mypage/myinfo";
			        },
			        error: function(xhr, textStatus, errorThrown) {
		                // axios 스타일의 에러 객체로 변환
		                const error = new Error(errorThrown || textStatus);
		                error.response = {
		                    status: xhr.status,
		                    statusText: xhr.statusText,
		                    data: xhr.responseJSON || xhr.responseText
		                };
		                error.request = xhr;
		                reject(error);
		        	}
			    })
	    	})
	    );
	}

	

}); // end of furnction(){}

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
