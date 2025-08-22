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



<script type="text/javascript">

    $(document).ready(function () {



    });

    function goLike(postId, thisBtn) {

        $.ajax({
            url:"/api/like/goLike",
            data: {postId: postId},
            dataType: "json",
            type: "POST",
            success: function (json) {

                if(json.isExist) {
                    // like 테이블에 save 가 성공되어지면 속이빈 하트를 속이 찬 하트로 바꾼다.
                    $(thisBtn).css('background-image', "url('/images/like/purpleLove.png')");
                    // 페이지 다시 로딩
                    location.reload();

                }
                else{
                    // like 테이블에 이미 데이터가 있다면 속이 빈 하트로 바꿔준다.
                    $(thisBtn).css('background-image', "url('/images/like/emptyLove.png')");
                    // 페이지 다시 로딩
                    location.reload();
                }
                $('span#postLikeCnt').text(json.n);
            },
            error: function (xhr, status, error) {
                alert(xhr.responseText);
            }
        })

    }

</script>

<div class="feed-container" id="feed">

    <c:if test="${empty requestScope.followPostVOList}">
        <span style="font-weight: bold; text-align: center">등록된 게시물이 없습니다.</span>
    </c:if>

    <c:if test="${not empty requestScope.followPostVOList}">

        <!-- JSTL로 게시글 리스트 출력 -->
        <c:forEach var="post" items="${requestScope.followPostVOList}">
            <div class="post">
                <div class="post-header">
                    <div class="post-header-left">
                        <img src="${post.profileImage}" alt="프로필">
                        <span class="username">${post.username}</span>
                    </div>
                    <div class="post-header-right">
                        <button type="button" class="menu-btn">⋮</button>
                    </div>
                </div>

                <c:set var="carouselId" value="post-carousel-${post.postId}" />
                <c:choose>
                    <c:when test="${fn:length(post.post_image_urls) > 1}">
                        <div id="${carouselId}" class="carousel slide" data-interval="false">
                            <ol class="carousel-indicators">
                                <c:forEach var="img" items="${post.post_image_urls}" varStatus="i">
                                    <li data-target="#${carouselId}"
                                        data-slide-to="${i.index}"
                                        class="${i.first ? 'active' : ''}"></li>
                                </c:forEach>
                            </ol>

                            <div class="carousel-inner">
                                <c:forEach var="img" items="${post.post_image_urls}" varStatus="i">
                                    <div class="carousel-item ${i.first ? 'active' : ''}">
                                        <img src="${img}" class="d-block w-100 post-img" alt="게시글 이미지 ${i.index + 1}">
                                    </div>
                                </c:forEach>
                            </div>

                            <a class="carousel-control-prev" href="#${carouselId}" role="button" data-slide="prev">
                                <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                                <span class="sr-only">Previous</span>
                            </a>
                            <a class="carousel-control-next" href="#${carouselId}" role="button" data-slide="next">
                                <span class="carousel-control-next-icon" aria-hidden="true"></span>
                                <span class="sr-only">Next</span>
                            </a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="img" items="${post.post_image_urls}">
                            <img src="${img}" class="d-block w-100 post-img" alt="게시글 이미지">
                        </c:forEach>
                    </c:otherwise>
                </c:choose>



                <div class="post-actions">
                    <c:choose>
                        <c:when test="${post.myLiked}">
                            <button type="button" class="btn like" style="background-image: url('/images/like/purpleLove.png') " onclick="goLike(${post.postId}, this)"><span class="blind">하트</span></button>
                        </c:when>
                        <c:otherwise>
                            <button type="button" class="btn like" onclick="goLike(${post.postId}, this)"><span class="blind">하트</span></button>
                        </c:otherwise>
                    </c:choose>
                    <button type="button" name="comments" id="comments">💬</button>
                    <button type="button" name="" id="">📤</button>
                </div>

                <div class="post-content">
                    <div class="title">${post.title}</div>
                    <div class="likes">좋아요 ${requestScope.n}개</div>
                    <div class="caption"><b>${post.username}</b> ${post.contents}</div>
                </div>
            </div>
        </c:forEach>

    </c:if>

</div>

