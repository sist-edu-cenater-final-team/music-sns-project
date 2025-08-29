$(function() {
	
    $(".emotions .btn").on("click", function() {
        $(".emotions .btn").removeClass("active"); // 기존 active 제거
        $(this).addClass("active"); // 현재 클릭한 버튼만 active
        let emotionId = 2;
        musicList(emotionId);
    });
 
	
}); // end of function(){}

function musicList(emotionId) {
    const authHeader = AuthFunc.getAuthHeader;
    const apiRequest = AuthFunc.apiRequest;
	
	apiRequest(() =>
 		new Promise((resolve, reject) => {
			$.ajax({
				url:'/api/music/playList',
				data: {"emotionId":emotionId},
				headers:authHeader(),
				dataType:"json",
				success:function(json){
					console.log(json)
				},
		        error: function (xhr, textStatus, errorThrown) {
		
		            alert("code: " + xhr.status + "\nmessage: " + xhr.responseText + "\nerror: " + error);
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
				
			});
 		})
 	)
}