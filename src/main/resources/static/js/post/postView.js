// token.js에서 함수들 가져오기
const authHeader = AuthFunc.getAuthHeader();//즉시호출
const apiRequest = AuthFunc.apiRequest;//함수참조

$(document).ready(function(){

    return apiRequest(()=>
        $.ajax({
            url: "/api/post/postView",
            type: "get",
            headers: authHeader,
            success: function (data) {

                let v_html = ``;

                data.forEach(item => {

                    // console.log(item.username);
                    // console.log(item.profileImage);

                    // followPostVOList 가 비어있는 경우, 뷰단에는 "등록된 게시물이 없습니다." 를 나타나게 함
                    if(item == null){
                        v_html += `<span style="font-weight: bold; text-align: center">등록된 게시물이 없습니다.</span>`;
                    }
                    else{
                        v_html += `
                        <div class="post">
                            <div class="post-header">
                                <div class="post-header-left">
                                    <img src="${item.profileImage}" alt="프로필">
                                    <span class="username">${item.username}</span>
                                </div>
                                <div class="post-header-right">
                                    <button type="button" class="menu-btn">⋮</button>
                                </div>
                            </div>`;

                        const postImageUrls = item.post_image_urls;

                        // 이미지가 2장 이상일 때만 indicator 표시 (JSP와 동일하게)
                        if (postImageUrls && postImageUrls.length > 1) {
                            const carouselId = `carousel-${item.postId}`; // 숫자 id 대비용 접두어

                            // indicators
                            const indicatorsHtml = postImageUrls
                                .map((_, idx) =>
                                    `<li data-target="#${carouselId}" data-slide-to="${idx}" ${idx === 0 ? 'class="active"' : ''}></li>`
                                )
                                .join('');

                            // slides
                            const slidesHtml = postImageUrls
                                .map((url, idx) =>
                                    `<div class="carousel-item ${idx === 0 ? 'active' : ''}">
                                         <img src="${url}" class="d-block w-100" alt="">
                                     </div>`
                                )
                                .join('');

                            // 전체 마크업
                            v_html += `<div id="${carouselId}" class="carousel slide" data-interval="false">
                                          <ol class="carousel-indicators">
                                            ${indicatorsHtml}
                                          </ol>
                                          <div class="carousel-inner">
                                            ${slidesHtml}
                                          </div>
                                          <a class="carousel-control-prev" href="#${carouselId}" role="button" data-slide="prev">
                                            <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                                            <span class="sr-only">Previous</span>
                                          </a>
                                          <a class="carousel-control-next" href="#${carouselId}" role="button" data-slide="next">
                                            <span class="carousel-control-next-icon" aria-hidden="true"></span>
                                            <span class="sr-only">Next</span>
                                          </a>
                                       </div>`;
                        } // end of if(postImageUrls && postImageUrls.length > 1) {}

                        v_html += `<div class="post-actions">`;

                        if(item.myLiked){
                            v_html += `<button type="button" class="btn like" style="background-image: url('/images/like/purpleLove.png') " onclick="goLike(${item.postId}, this)"><span class="blind">하트</span></button>`;
                        }
                        else {
                            v_html += `<button type="button" class="btn like" onclick="goLike(${item.postId}, this)"><span class="blind">하트</span></button>`;
                        }

                        v_html += `    <button type="button" name="comments" id="comments">💬</button>
                                       <button type="button" name="" id="">📤</button>
                                   </div>
                                   <div class="post-content">
                                        <div class="title">${item.title}</div>
                                        <div class="likes"></div>
                                        <div class="caption"><b>${item.username}</b> ${item.contents}</div>
                                   </div>
                               </div>`;
                    }

                }) // end of data.forEach(item => {})

                $('div#feed').html(v_html);




            },
            error: function (xhr) {
                console.error("요청 실패:", xhr.status, xhr.responseText);
            }


        }) // end of $.ajax({})
    );

}) // end of $(document).ready(function(){})

function goLike(postId, thisBtn) {

    return apiRequest(()=>
        $.ajax({
            url:"/api/like/goLike",
            data: {postId: postId},
            dataType: "json",
            type: "POST",
            headers: authHeader,
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

                if (json.postLikeCnt > 0) {
                    $('div.likes').text(`좋아요 ${json.postLikeCnt}개`);
                } else {
                    $('div.likes').text('좋아요 0개');
                }


            },
            error: function (xhr, status, error) {
                alert(xhr.responseText);
            }
        })
    );

}