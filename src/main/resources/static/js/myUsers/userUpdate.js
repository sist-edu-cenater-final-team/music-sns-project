$(function(){

	const authHeader = AuthFunc.getAuthHeader;
    const apiRequest = AuthFunc.apiRequest;

    apiRequest(() =>
    	new Promise((resolve, reject) => {
	        $.ajax({
	            url: '/api/userInfo/getInfo',
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
			            url: "/api/storage",
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
			        url: "/api/userInfo/update",
			        type: "post",
			        data: formData,
			        processData: false,
			        contentType: false,
			        headers: authHeader(),
			        dataType: "json",
			        success: function(json) {
			            console.log("프로필 업데이트 성공:", json);
			            alert("업데이트 성공");
			            location.href="/mypage/myinfo";
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
