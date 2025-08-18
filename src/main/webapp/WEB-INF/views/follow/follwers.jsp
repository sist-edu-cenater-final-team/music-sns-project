<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<html>
<% String ctxPath = request.getContextPath(); %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../include/common/head.jsp" />
<body>
<style type="text/css">

div#container {

margin-left: 8%;
}	


.followes-container {
	max-width:550px;
	margin: 0 auto;
}

.follow-container {
	padding: 3px 16px;
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
	width: 100%;
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

.followInfo {
	justify-content: space-between;
}
.followInfo:not(:last-child){
margin-bottom:24px;
}


.search-result {
    position: absolute;
    top: 100%;
    left: 0;
    right: 0;
    background: #fff;
    border: 1px solid #ddd;
    border-radius: 8px;
    box-shadow: 0 2px 6px rgba(0,0,0,0.1);
    max-height: 300px;
    overflow-y: auto;
    z-index: 9999;
}

.search-result-item {
    display: flex;
    align-items: center;
    padding: 8px 12px;
    cursor: pointer;
}

.search-result-item:hover {
    background: #f5f5f5;
}

.profile-img {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    margin-right: 10px;
}

.user-info {
    flex-grow: 1;
}

.user-actions {
    margin-left: auto;
}

</style>

<script type="text/javascript">

	$(function(){
		const userId = '41';
		
		getfollower(userId);
		getCounts(userId);
		getfollowing(userId);
	
		$('input:text[id="searchInput"]').on('keyup', function(e) {
		    const searchWord = $(this).val();
		    const result = $("#searchResult");

	
		    if (!searchWord || searchWord.trim() === "") {
		        result.empty();
		        result.hide();
		        return;
		    }

		
		    if (e.keyCode == 13) {
		        searchUser(userId, searchWord);
		    }
		});
		
		
		$('span#followers').css('color', '#e0e0e0');
		
		 
		  $('#searchInput').on('input', function(){
		    if ($(this).val().length > 0) {
		      $('#clearBtn').show();
		    } else {
		      $('#clearBtn').hide();
		    }
		  });
	
		  
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
		friendFollow(userId);
		$.ajax({
			url:'<%= ctxPath%>/api/follow/follower/' + userId,
			type:'get',
			dataType:'json',
			success:function(json) {
				
				followList(json);
			
			},
			error: function(request, status, error){
				   alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
			} 
			
			
		});
		
	}; // end of function getfollowing(userId) ---
	
	
	function friendFollow(userId) {
		
		$.ajax({
			url:'<%= ctxPath%>/api/follow/findCommonFriend/'+ userId,
			dataTpye:"json",
			success:function(json){
				
			},
			error: function(request, status, error){
				   alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
			} 
			
		});
		
	}; // end of function friendFollow()
	
	
	function getfollower(userId) { // 나를 팔로잉
		
		$.ajax({
			url:'<%= ctxPath%>/api/follow/followee/' + userId,
			type:'get',
			dataType:'json',
			success:function(json) {
				followerList(json);
				
				
				
			},
			error: function(request, status, error){
				   alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
			} 
			
			
		});
		
	}; // end of getfollower
	
	function getCounts(userId) {
	    // 팔로잉 수
	    $.getJSON('<%= ctxPath%>/api/follow/follower/' + userId, function(json){
	        $('#following').text('팔로워 '+json.length+'명');
	    });

	    // 팔로워 수
	    $.getJSON('<%= ctxPath%>/api/follow/followee/' + userId, function(json){
	        $('#followers').text('팔로잉 '+json.length+'명');
	    });
	} // end of getCounts -------
	
	function followList(json) {
		
		const list = $('div.follow-container');
		list.empty();
		
		
		json.forEach(item => {
	        const user = item.user;


	        const v_html = `
	        <div class="followInfo row">
	            <div style="display:flex; align-items:center; ">
		            <span class="">
		            	<img style="width: 100px; height: 100px; object-fit: cover; border: 2px solid #ddd;" 
		            			class="rounded-circle mr-4 profile-img" src="\${user.profile_image}"/>
		            </span>
		            <span>
		            	<div>\${user.email}</div>
		            	<div>\${user.nickname}</div>
		            	<div>\${user.profileMessage}</div>
		            </span>
	            </div>
	            <button type="button" style="background-color: #6633FF; border-radius: 8px; height:40px; margin: auto 0; color:white; width:200px;" id="\${user.userId}">메세지 보내기</button>
	            
	        </div>`;
	        
	        list.append(v_html);
		});
	}
	
	
function followerList(json) {
		
		const list = $('div.follow-container');
		list.empty();
		
		
		json.forEach(item => {
	        const user = item.user;
			
	        let a_html = ``;
	        
	        if (!item.teist) {
                a_html = `<button type="button" style="background-color: #9966FF; border-radius: 8px; height:40px; margin: auto 0; color:white; width:200px;" 
                                id="${user.userId}" onclick="gofollow('\${user.userId}')">맞팔로우 하기</button>`;
            } else {
                a_html = `<button type="button" style="background-color: #6633FF; border-radius: 8px; height:40px; margin: auto 0; color:white; width:200px;" 
                                id="${user.userId}" onclick="">메세지 보내기</button>`;
            }

	        const v_html = `
	        <div class="followInfo row">
	            <div style="display:flex; align-items:center; ">
		            <span class="">
		            	<img style="width: 100px; height: 100px; object-fit: cover; border: 2px solid #ddd;" 
		            			class="rounded-circle mr-4 profile-img" src="\${user.profile_image}"/>
		            </span>
		            <span>
		            	<div>\${user.email}</div>
		            	<div>\${user.nickname}</div>
		            	<div>\${user.profileMessage}</div>
		            </span>
	            </div>
				
	            \${a_html}
	            
	        </div>`;
	        
	        list.append(v_html);
		});
	}
	
	function gofollow(userId) {

		$.ajax({
			url:'<%= ctxPath%>/api/follow/addFollow',
			data:{'follower':${myId},
				  'followee':userId},
			dataType:"json",
			success:function(json) {
				alert("팔로우 아마 완료?");
			},
			error: function(request, status, error){
				   alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
			} 
		
		});
		
		followerList(json);
	}
	
	function searchUser(userId, searchWord) {
	    $.ajax({
	        url: "<%= ctxPath%>/api/follow/searchUser",
	        data: {
	            "searchWord": searchWord,
	            "userId": userId
	        },
	        dataType: "json",
	        success: function(json) {
	           console.log(json);

	            let result = $("#searchResult");
	            result.empty();

	            if (json.length === 0) {
	                result.append("<div class='search-result-item'>검색 결과 없음</div>");
	            } else {
	                $.each(json, function(i, follow) {
	                    let item = `
	                        <div class="search-result-item">
	                            <img style="height:30px; weight:30px;" src="\${follow.user.profile_image}"/>
	                            <div class="user-info ml-3">
	                                <div><strong>\${follow.user.nickname}</strong></div>
	                                <div style="font-size:14px;color:gray;">\${follow.user.email}</div>
	                            </div>
	                            <div class="user-actions">
	                            `
	                            
	                            	if(follow.teist) {
	                            		item +=	`<button class="btn" style="background-color: #6633FF; color: white;">메세지</button>`
	                            	}
	                            	else{
	                            		item += `<button class="btn" style="background-color: #9966FF; color: white;" onclick="gofollow(\${follow.user.userId})">팔로우</button>`
	                            	}
	                        item += `
	                            </div>
	                        </div>`;
	                    result.append(item);
	                });
	            }
	            result.show();
	        },
	        error: function(request, status, error) {
	            alert("code: " + request.status + "\n" +
	                  "message: " + request.responseText + "\n" +
	                  "error: " + error);
	        }
	    }); // end of ajax
	}

	
	
</script>
	
	<div id="wrap">
		<main class="">
			<%-- 왼쪽 사이드 네비게이션 & 관련 팝업들 --%>
			<jsp:include page="../include/common/asideNavigation.jsp" />
			<%-- //왼쪽 사이드 네비게이션 & 관련 팝업들 --%>

			<!-- 메인 컨텐츠 -->
			<div class="main-contents">
				<div class="inner">
					<div class="followes-container">
						<div>
							<div class="tabs">
								<span class="tab active" id ="following"></span> 
								<span class="tab" id ="followers"></span>
							</div>
						</div>


						<!-- 검색창 -->
						<div class="mt-3"
							style="position: relative; margin-bottom: 12px;">
							<input type="text" id="searchInput" placeholder="검색" />
							<button type="button" id="clearBtn">×</button>
							
							<div id="searchResult" class="search-result" style="display:none;">
						    </div>
						</div>
						
						<div class="follow-container"></div>
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
