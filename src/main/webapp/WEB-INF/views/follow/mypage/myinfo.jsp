<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<% String ctxPath = request.getContextPath(); %>

<html>
<jsp:include page="../../include/common/head.jsp" />

<script src="<%= ctxPath%>/js/myUsers/userInfo.js"></script>
<link rel="stylesheet" href="<%= ctxPath%>/css/users/userInfo.css" />



<div id="wrap">
    <main>
        <jsp:include page="../../include/common/asideNavigation.jsp" />
		
	
		
        <div class="main-contents">
            <div class="inner">
                <div class="col-10 p-4 profile-container">

					<div class="profile-top mb-4"></div>
                    <!-- 버튼 그룹 -->
                    <div class="profile-btns"></div>
					
                    <!-- 게시물 영역 -->
                    <div class="post-list"></div>
                    
                </div>
            </div>
        </div>

		<div class="modal fade" id="blockedUser" tabindex="-1" role="dialog" aria-hidden="true">
		  <div class="modal-dialog modal-dialog-centered" role="document">
		    <div class="modal-content rounded-2xl p-3">
		      <div class="modal-header">
		        <h5 class="modal-title">차단한 유저 목록</h5>
		        <button type="button" class="close" data-dismiss="modal" aria-label="닫기">
		          <span aria-hidden="true">&times;</span>
		        </button>
		      </div>
		      <div class="modal-body blocked-list">
		        <!-- 차단유저 -->
		        
		      </div>
		    </div>
		  </div>
		</div>


        <jsp:include page="../../include/common/asidePlayList.jsp" />
    </main>
</div>
</body>
</html>