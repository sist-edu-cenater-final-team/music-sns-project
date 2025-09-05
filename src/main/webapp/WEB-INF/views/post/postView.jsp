<%--
  Created by IntelliJ IDEA.
  User: jks93
  Date: 25. 8. 19.
  Time: 오후 4:20
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<% String ctxPath= request.getContextPath(); %>

<link rel="stylesheet" href="<%=ctxPath%>/css/post/postView.css"/>
<script src="<%=ctxPath%>/js/post/postView.js"></script>



<div class="feed-container" id="feed"></div>


<%-- 여기서부터는 모달 나올거임 --%>

<div class="modal fade" id="twoColumnModal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-lg  modal-dialog-centered postViewModal" role="document"><!-- modal-dialog-centered: 화면 정가운데 -->
        <div class="modal-content">

            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="닫기">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>

            <div class="modal-body">
                <div class="row no-gutters">
                    <!-- 왼쪽: 이미지 -->
                    <div class="col-7 pr-3">
                        <div class="post-image-area"></div>
                    </div>

                    <!-- 오른쪽: 텍스트 -->
                    <div class="col-5 pl-3 d-flex flex-column rightText">

                        <!-- 맨 위: 프로필 -->
                        <div class="profileImageUserName d-flex align-items-center mb-2">
                            <img src="" class="pcUserImage rounded-circle mr-2" alt="userImage"
                                 style="width:40px; height:40px; object-fit:cover;">
                            <span class="pcUserName font-weight-bold"></span>
                        </div>

                        <!-- 그 아래: 본문 -->
                        <div class="contents mb-2">
                            <h5 class="pcPostTitle"></h5>
                            <p class="pcPostContent"></p>

                            <%-- 댓글부분 들어갈 곳 --%>
                            <div class="pcCommentsArea mt-2"></div>
                        </div>

                        <!-- 맨 아래: 댓글 입력 -->
                        <div class="postComment d-flex" style="margin-top: auto;">
                            <%--<input type="text" class="form-control pcComments mr-2" placeholder="댓글을 입력하세요">--%>
                            <textarea class="pcCommentInput flex-grow-1 border-0" rows="1" placeholder="댓글을 입력하세요" style="resize:none; overflow:hidden;"></textarea>
                            <button type="button" class="btn btn-link p-0 commentPost" style="color: cornflowerblue;" disabled>게시</button>
                        </div>

                    </div>
                </div>
            </div>

        </div>
    </div>
</div>
