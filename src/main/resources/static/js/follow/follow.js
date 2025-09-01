
$(function () {
	
    // 초기 데이터 로드
    getCounts();
    getfollowing();

    // ===============================
    // UI 이벤트 바인딩
    // ===============================

    // 검색창 외부 클릭 시 결과 닫기
    $(document).on("click", function () {
        $("#searchResult").hide();
    });
    
    $('.tab').on('click', function() {
        // 기존 active 클래스 제거
        $('.tab').removeClass('active');
        // 클릭한 탭에 active 클래스 추가
        $(this).addClass('active');

        // 클릭된 탭 아이디에 따라 원하는 함수 호출 (예시)
        const id = $(this).attr('id');
        if (id === 'following') {
            getfollowing();
        } else if (id === 'followers') {
            getfollower();
        } else if (id === 'favorite') {
            favoriteList();
        }
    });
    
    // 기본 탭 색상 설정
    $('span#followers').css('color', '#e0e0e0');
    $('span#favorite').css('color', '#e0e0e0');

    // 탭 클릭 이벤트
    $('span#followers').on('click', function () {
        $('span#following').css('color', '#e0e0e0');
        $('span#followers').css('color', 'black');
        $('span#favorite').css('color', '#e0e0e0');
        getfollower();
    });

    $('span#following').on('click', function () {
        $('span#following').css('color', 'black');
        $('span#followers').css('color', '#e0e0e0');
        $('span#favorite').css('color', '#e0e0e0');
        getfollowing();
        //friendFollow();
    });

    $('span#favorite').on('click', function () {
        $('span#following').css('color', '#e0e0e0');
        $('span#followers').css('color', '#e0e0e0');
        $('span#favorite').css('color', 'black');
        favoriteList();
    });

    // 검색 입력창 이벤트
    $('#searchInput').on('input', function () {
        if ($(this).val().length > 0) {
            $('#clearBtn').show();
        } else {
            $('#clearBtn').hide();
        }
    });

    $('#clearBtn').on('click', function () {
        $('#searchInput').val('').focus();
        $(this).hide();
    });

    $('input:text[id="searchInput"]').on('keyup', function (e) {
        const searchWord = $(this).val();
        const result = $("#searchResult");

        if (!searchWord || searchWord.trim() === "") {
            result.empty();
            result.hide();
            return;
        }

        if (e.keyCode == 13) {
            searchUser(searchWord);
        }
    });

    // 메뉴 버튼 클릭 이벤트
    $(document).on("click", ".menu-btn", function (e) {
        e.stopPropagation();
        const dropdownMenu = $(this).siblings(".dropdown-menu");
        $(".dropdown-menu").not(dropdownMenu).hide(); // 다른 건 닫기
        dropdownMenu.toggle();
    })

    // 화면 아무데나 클릭 시 메뉴 닫기
    $(document).on("click", function () {
        $(".dropdown-menu").hide();
    });
}); // end of $(function(){})


// ===============================
// 팔로우 관련 AJAX
// ===============================


// 인원 count
function getCounts() {
    const apiRequest = AuthFunc.apiRequest;
    const authHeader = AuthFunc.getAuthHeader;

    const urls = [
        { url: '/api/follow/follower', selector: '#following', label: '팔로워' },
        { url: '/api/follow/followee', selector: '#followers', label: '팔로잉' },
        { url: '/api/follow/favorite', selector: '#favorite', label: '즐겨찾기' }
    ];

    urls.forEach(item => {
        apiRequest(() =>
            new Promise((resolve, reject) => {
                $.ajax({
                    url: item.url,
                    headers: authHeader(),
                    dataType: 'json',
                    success: json => $(item.selector).text(`${item.label} ${json.length}명`),
                    error: (xhr, textStatus, errorThrown) => handleAjaxError(xhr, textStatus, errorThrown, reject)
                });
            })
        );
    });
}


// 팔로우 리스트
function getfollowing() {
    const apiRequest = AuthFunc.apiRequest;
    const authHeader = AuthFunc.getAuthHeader;
	const ctxPath = "http://localhost:8080/";
    apiRequest(() =>
    
    	new Promise((resolve, reject) => {
    		
    	
            $.ajax({
                url:  '/api/follow/follower',
                headers: authHeader(),
                dataType: 'json',
                success: function (json) {
                    const list = $('div.follow-container');
                    list.empty();


                    json.forEach(item => {
                        const user = item.user;

                        let v_html = `
			    	        	<div class="followInfo row">
			    	        	    <div>
			    	        	    	<a style="display:flex; align-items:center; color:black; text-decoration: none;  " href="${ctxPath}mypage/myinfo?targetUserId=${user.userId}">
				    	        	        <span>
				    	        	            <img style="width: 100px; height: 100px; object-fit: cover; border: 2px solid #ddd;" 
				    	        	                 class="rounded-circle mr-4 profile-img" src="${user.profile_image}"/>
				    	        	        </span>
				    	        	        
				    	        	        <span>
				    	        	            <div>${user.email}</div>
				    	        	            <div>${user.nickname}</div>`;

                        if (user.profileMessage == null || user.profileMessage.trim() === "") {
                            v_html += ``;
                        } else {
                            v_html += `<div>${user.profileMessage}</div>`;
                        }

                        v_html += `
				    	                 </span>
				    	        	 </a>
			    	             </div>
		
			    	             <div style="display:flex; align-items:center; gap: 10px; margin-top: 10px;">
			    	                 <button type="button" style="background-color: #6633FF; border-radius: 8px; height:40px; color:white; width:200px;" id="msg_${user.userId}">메세지 보내기</button>
			    	                 
			    	                 <!-- 메뉴 아이콘 버튼 -->
			    	                 <div class="dropdown" style="position: relative;">

			    	                     <button class ="btn menu-btn">...</button>
			    	                     <div class="dropdown-menu" id="menu_${user.userId}" style="display:none; position:absolute;">`;
			    	                     
			    	                     	if(item.favorite == false) {
			    	                         	v_html += `<div class="dropdown-item" style="padding:8px; cursor:pointer;" onclick="addFavorite('${user.userId}')">즐겨찾기 추가</div>`;
			    	                     	}
			    	                     	else{
			    	                     		v_html += `<div class="dropdown-item" style="padding:8px; cursor:pointer;" onclick="unFavorite('${user.userId}')">즐겨찾기 삭제</div>`;
			    	                     	}
			    	                     
			    	                       v_html += `
			    	                         <div class="dropdown-item" style="padding:8px; cursor:pointer;" onclick="unfollow('${user.userId}')">팔로우 취소</div>
			    	                     </div>
			    	                 </div>
			    	             </div>
			    	         </div>
			    	         `;

                        list.append(v_html);
                    });

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
            })
        }));

};


// 팔로워 리스트
function getfollower() {
    const apiRequest = AuthFunc.apiRequest;
    const authHeader = AuthFunc.getAuthHeader;
	const ctxPath = "http://localhost:8080/";
    apiRequest(() =>
        new Promise((resolve, reject) => {

            $.ajax({
                url:  '/api/follow/followee',
                headers: authHeader(),
                dataType: 'json',
                success: function (json) {

                    const list = $('div.follow-container');
                    list.empty();


                    json.forEach(item => {


                        const user = item.user;


                        let a_html = ``;

                        if (!item.teist) {
                            a_html = `
		    	        		   <div style="display:flex; align-items:center; gap: 10px; margin-top: 10px;">
		    	        		       <button type="button" style="background-color: #9966FF; border-radius: 8px; height:40px; margin: auto 0; color:white; width:200px;" 
		    	        		               id="${user.userId}" onclick="gofollow('${user.userId}')">맞팔로우 하기 </button>
	
		    	        		       <div class="dropdown" style="position: relative;">
		    	        		           <button class="btn menu-btn">...</button>
		    	        		           <div class="dropdown-menu" id="menu_${user.userId}">
		    	        		               <div class="dropdown-item" style="padding:8px; cursor:pointer; color:red;" onclick="block('${user.userId}')">응 너차단</div>
		    	        		               <div class="dropdown-item" style="padding:8px; cursor:pointer;" onclick="">응 너신고</div>
		    	        		           </div>
		    	        		       </div>
		    	        		   </div>
		    	        		`;

                        } else {
                            a_html = `<div style="display:flex; align-items:center; gap: 10px; margin-top: 10px;">
		                    			<button type="button" style="background-color: #6633FF; border-radius: 8px; height:40px; margin: auto 0; color:white; width:200px;" 
		                                    id="${user.userId}" onclick="">메세지 보내기</button>
		                                    
		                                   
		       	    	                 <div class="dropdown" style="position: relative;">
		       	    	                    
		       	    	                     <button class ="btn menu-btn">...</button>
		       	    	                     <div class="dropdown-menu" id="menu_${user.userId}">`;
				       	    	                  	if(item.favorite == false) {
					    	                         	a_html += `<div class="dropdown-item" style="padding:8px; cursor:pointer;" onclick="addFavorite('${user.userId}')">즐겨찾기 추가</div>`;
					    	                     	}
					    	                     	else{
					    	                     		a_html += `<div class="dropdown-item" style="padding:8px; cursor:pointer;" onclick="unFavorite('${user.userId}')">즐겨찾기 삭제</div>`;
					    	                     	}
				       	    	                  
				       	    	             a_html += `
		       	    	                         <div class="dropdown-item" style="padding:8px; cursor:pointer;" onclick="unfollow('${user.userId}')">팔로우 취소</div>
		       	    	                     </div>
		       	    	                 </div>
		       	    	              </div>
		       	    	               `;
                        }

                        let v_html = `
		    	        	<div class="followInfo row">
		    	        	    <div>
			    	        	    	<a style="display:flex; align-items:center; color:black; text-decoration: none;" href="${ctxPath}mypage/myinfo?targetUserId=${user.userId}">
			    	        	        <span>
			    	        	            <img style="width: 100px; height: 100px; object-fit: cover; border: 2px solid #ddd;" 
			    	        	                 class="rounded-circle mr-4 profile-img" src="${user.profile_image}"/>
			    	        	        </span>
			    	        	        <span>
			    	        	            <div>${user.email}</div>
			    	        	            <div>${user.nickname}</div>`;

                        if (user.profileMessage == null || user.profileMessage.trim() === "") {
                            v_html += ``;
                        } else {
                            v_html += `<div>${user.profileMessage}</div>`;
                        }

                        v_html += `
			    		            </span>
			    	        	 </a>
		    	            </div>
		    				
		    	            ${a_html}
		    	            
		    	        </div>`;

                        list.append(v_html);
                    });
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
            })
        })
    );
}

// 즐겨찾기 리스트
function favoriteList() {
    const apiRequest = AuthFunc.apiRequest;
    const authHeader = AuthFunc.getAuthHeader;

    apiRequest(() =>
    	new Promise((resolve, reject) => {
            $.ajax({
                url:  '/api/follow/favorite',
                headers: authHeader(),
                dataType: 'json',
                success: function (json) {

                    const list = $('div.follow-container');
                    list.empty();


                    json.forEach(item => {
                        const user = item.user;

                        let v_html = `
					        	<div class="followInfo row">
					        	    <div style="display:flex; align-items:center;">
					        	        <span>
					        	            <img style="width: 100px; height: 100px; object-fit: cover; border: 2px solid #ddd;" 
					        	                 class="rounded-circle mr-4 profile-img" src="${user.profile_image}"/>
					        	        </span>
					        	        <span>
					        	            <div>${user.email}</div>
					        	            <div>${user.nickname}</div>`;

                        if (user.profileMessage == null || user.profileMessage.trim() === "") {
                            v_html += ``;
                        } else {
                            v_html += `<div>${user.profileMessage}</div>`;
                        }

                        v_html += `
					                 </span>
					             </div>
		
					             <div style="display:flex; align-items:center; gap: 10px; margin-top: 10px;">
					                 <button type="button" style="background-color: #6633FF; border-radius: 8px; height:40px; color:white; width:200px;" id="msg_${user.userId}">메세지 보내기</button>
					                 
					                 <!-- 메뉴 아이콘 버튼 -->
					                 <div class="dropdown" style="position: relative;">
					                   
					                     <button class ="btn menu-btn">...</button>
					                     <div class="dropdown-menu" id="menu_${user.userId}" 
					                          style="display:none; position:absolute; top:100%; right:0; background:white; border:1px solid #ccc; border-radius:6px; min-width:150px; box-shadow: 0 2px 6px rgba(0,0,0,0.2); z-index:100;">
					                         <div class="dropdown-item" style="padding:8px; cursor:pointer;" onclick="unFavorite('${user.userId}')">즐겨찾기 삭제</div>
					                         <div class="dropdown-item" style="padding:8px; cursor:pointer;" onclick="unfollow('${user.userId}')">팔로우 취소</div>
					                     </div>
					                 </div>
					             </div>
					         </div>
					         `;

                        list.append(v_html);
                    });

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
            })


        }));

}


// 팔로우 하기
function gofollow(userId) {
    const authHeader = AuthFunc.getAuthHeader;
    const apiRequest = AuthFunc.apiRequest;

    return apiRequest(() =>
   	 new Promise((resolve, reject) => {
            $.ajax({
                url:  '/api/follow/addFollow',
                headers: authHeader(),
                data: {
                    'followee': userId
                },
                dataType: "json",
                success: function (json) {
                    alert("팔로우 완료");
                    getfollower();
                    getCounts();
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
            })

        }));

    followerList(json);
}

// 언팔로우 하기
function unfollow(userId) {

    const authHeader = AuthFunc.getAuthHeader;
    const apiRequest = AuthFunc.apiRequest;

    return apiRequest(() =>
    	new Promise((resolve, reject) => {
            $.ajax({
                url:  '/api/follow/unFollow',
                headers: authHeader(),
                data: {
                    'followee': userId
                },
                dataType: "json",
                success: function (json) {
                    alert("언팔 완료");
                    getfollower();
                    getCounts();
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
            })

        }));


} // end of function unfollow(userId) {} ------------


// 즐겨찾기 추가
function addFavorite(userId) {

    const authHeader = AuthFunc.getAuthHeader;
    const apiRequest = AuthFunc.apiRequest;

    apiRequest(() =>
    	new Promise((resolve, reject) => {
            $.ajax({
                url:  '/api/follow/addFavorite',
                headers: authHeader(),
                data: {
                    'followee': userId
                },
                dataType: "json",
                success: function (json) {
                    alert("즐겨찾기 완료");
                    getfollowing();
                    getCounts();
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
            })


        }));


} // end of function addFavorite(userId) {} -----


// 즐겨찾기 삭제 하기
function unFavorite(userId) {
    const authHeader = AuthFunc.getAuthHeader;
    const apiRequest = AuthFunc.apiRequest;

    return apiRequest(() =>
    	new Promise((resolve, reject) => {
            $.ajax({
                url:  '/api/follow/unFavorite',
                headers: authHeader(),
                data: {
                    'followee': userId
                },
                dataType: "json",
                success: function (json) {
                    alert("즐겨찾기 취소 완료");
                    favoriteList();
                    getCounts();
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
            })

        }));


} // end of function unFavorite(userId)  -----------

// 차단
function block(userId) {
    const authHeader = AuthFunc.getAuthHeader;
    const apiRequest = AuthFunc.apiRequest;

    return apiRequest(() =>
   		new Promise((resolve, reject) => {
            $.ajax({
                url:  '/api/follow/block',
                headers: authHeader(),
                data: {
                    'blockUser': userId
                },
                dataType: "json",
                success: function (json) {
                    alert("응 너 차단완료^^");
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
            })

        })); // end of ajax ---
}


// 유저 검색
function searchUser(searchWord) {
    const authHeader = AuthFunc.getAuthHeader;
    const apiRequest = AuthFunc.apiRequest;
	const ctxPath = "http://localhost:8080/";
    apiRequest(() =>
    	new Promise((resolve, reject) => {
            $.ajax({
                url:  "/api/follow/searchUser",
                headers: authHeader(),
                data: {
                    "searchWord": searchWord
                },

                dataType: "json",
                success: function (json) {

                    let result = $("#searchResult");
                    result.empty();

                    if (json.length === 0) {
                        result.append("<div class='search-result-item'>검색 결과 없음</div>");
                    } else {
                        $.each(json, function (i, follow) {
                            let item = `
			                        <div class="search-result-item">
                            			<a style="display:flex; align-items:center; color:black; text-decoration: none;" href="${ctxPath}mypage/myinfo?targetUserId=${follow.user.userId}">
			                            <img style="height:30px; weight:30px;" src="${follow.user.profile_image}"/>
			                            <div class="user-info ml-3">
			                                <div><strong>${follow.user.nickname}</strong></div>
			                                <div style="font-size:14px;color:gray;">${follow.user.email}</div>
			                            </div>
			                            <div class="user-actions">
			                            </a>
			                            `

                            if (follow.teist) {
                                item += `<button class="btn" style="background-color: #6633FF; color: white;">메세지</button>`
                            } else {
                                item += `<button class="btn" style="background-color: #9966FF; color: white;" onclick="gofollow(${follow.user.userId})">팔로우</button>`
                            }
                            item += `
			                            </div>
			                        </div>`;
                            result.append(item);
                        });
                    }
                    result.show();
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
            })
        })); // end of ajax
}