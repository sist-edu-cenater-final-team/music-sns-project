// token.js에서 함수들 가져오기
const apiRequest = AuthFunc.apiRequest;//함수참조

$(document).ready(function(){

    return apiRequest(()=>
        new Promise((resolve, reject)=>{

            $.ajax({
                url: "/api/post/postView",
                type: "get",
                headers: AuthFunc.getAuthHeader(),
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
                            <div class="post" data-post-id="${item.postId}">
                                <div class="post-header">
                                    <div class="post-header-left">
                                        <img class="profileImage" src="${item.profileImage}" alt="프로필">
                                        <span class="username">${item.username}</span>
                                    </div>
                                    <div class="post-header-right">
                                        <span style="color: #5e35b1; font-weight: bold">${item.emotionLabel}</span>
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

                            v_html += `    <button type="button" name="comments" id="comments" data-post-id="${item.postId}" data-toggle="modal" data-target="#twoColumnModal">💬</button>
                                           <button type="button" name="" id="">📤</button>
                                       </div>
                                       <div class="post-content">
                                            <div class="title">${item.title}</div>
                                            <div class="likes">좋아요 ${item.postLikeCnt ?? 0}개</div>
                                            <div class="caption"><span style="font-size: 11pt; font-weight: bold">${item.username}</span> <b class="contents">${item.contents}</b></div>
                                       </div>
                                   </div>`;
                        }

                    }) // end of data.forEach(item => {})

                    $('div#feed').html(v_html);

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


            }) // end of $.ajax({})


        })
    );



    /*function 댓글등록(){
        const body = {
            'title' : "제목",
            'contents' : '내용'
        }
        AuthFunc.apiRequest(()=>{
             axios.post('/api/post/comment',
                body
                ,{
                    headers: AuthFunc.getAuthHeader()
                })
        }).then(function (response) {


            const data = response.data;
        }).catch(function (error) {

        })
    }*/

}) // end of $(document).ready(function(){})

$(function () {

    // 댓글 모양을 누르면 모달을 나오게 함
    $('div.feed-container').on('click', 'button[name="comments"]', function () {
        const post = $(this).closest('.post');
        // console.log(post);

        const postId = post.data('post-id');
        const postTitle = post.find('.title').text().trim();
        const postContent = post.find('.contents').text().trim();
        const username = post.find('.username').text().trim();
        // 프로필 이미지 선택자가 다른 이미지와 섞이지 않도록 구체화 권장
        const profileImage = post.find('.profileImage').attr('src') || '';
        // 모든 이미지 URL
        let postImageUrls = post.find('.carousel-inner img').map((_, img) => $(img).attr('src')).get();


        // console.log(postId);
        // console.log(postTitle);
        // console.log(postContent);
        // console.log(username);
        // console.log(profileImage);
        // console.log(imageUrls);

        let carouselSeq = 0;
        function nextCarouselId(postId) {
            return `carousel-${postId}-${++carouselSeq}-${Math.random().toString(36).slice(2,6)}`;
        }

        const carouselId = nextCarouselId(postId);
        const html = buildCarouselHtml(postImageUrls, carouselId);

        $('.post-image-area').html(html);
        $('.pcUserImage').attr('src', profileImage);
        $('.pcUserName').text(username);
        $('.pcPostTitle').text(postTitle);
        $('.pcPostContent').text(postContent);

        AuthFunc.apiRequest(()=>{
            axios.get('/api/comment/getCommentList',
                {params: {postId: postId}},
                {headers: AuthFunc.getAuthHeader()})
                .then(async function (response) {

                    let html = ``;

                    const commentList = response.data;

                    // console.log(commentList);

                    commentList.forEach(item => {

                        html += `
                                     <div class="comment d-flex mb-2"
                                     data-comment-id="${item.commentId}"
                                     data-writer="${item.writer}">
                                        <!-- 프로필 이미지 -->
                                        <img src= "${item.writerProfileImageUrl}" class="rounded-circle mr-2" 
                                             alt="userImage" style="width:32px; height:32px; object-fit:cover;">
                                
                                        <!-- 닉네임 + 댓글 내용 -->
                                        <div class="d-flex flex-column">
                                            <div>
                                                <span class="font-weight-bold mr-1">${item.writer}</span>
                                                <span>${item.contents}</span>
                                            </div>
                                
                                            <!-- 답글 -->
                                            <div class="text-muted small mt-1">
                                                <span>${item.createdAt}</span>
                                                <span class="ml-3 reply-btn" role="button" tabindex="0" style="font-style: italic">답글 달기( 개)</span>
                                            </div>
                                        </div>
                                        <!-- ✅ 답글 n개 보기 버튼 -->
                                        <div class="text-muted small mt-1 view-replies-btn" role="button" tabindex="0" style="cursor:pointer">
                                          -- 답글 보기
                                        </div>
                                    
                                        <!-- ✅ 대댓글이 들어갈 영역 -->
                                        <div class="replies pl-4 mt-2" style="display:none;"></div>
                                    </div>
                        `;

                    })

                    await $('.pcCommentsArea').html(html);

                }).catch(function (error) {

            })

        })

        // 댓글쓰기에서 값이 없으면 disabled, 값이 있으면 해제
        $('textarea.pcCommentInput').on('input', function() {
            $('button.commentPost').prop('disabled', $(this).val().trim() === "");
        });

        $('textarea.pcCommentInput').on('keyup', function(e) {
            if(e.keyCode === 13) {
                $('button.commentPost').trigger('click');
            }
        })

        // 답글보기 버튼을 누르면 나타나는 것
        $('div.view-replies-btn').on('click', function () {
            $('div.replies').style.display = 'block';
        })

        // 답글달기 버튼을 누르면 댓글쓰기에 값 넣어주고, 부모댓글아이디 만들기
        let parentCommentId = null;

        $('span.reply-btn').on('click', function () {

            const writer = $(this).closest('.comment').data('writer');
            parentCommentId = $(this).closest('.comment').data('comment-id');

            $('textarea.pcCommentInput').val(`@${writer} `).focus();
        })

        // "게시" 버튼을 누르면 댓글을 db에 저장하는 것
        $('button.commentPost').on('click', function () {

            const body = {
                'postId': postId,
                'comment': $('textarea.pcCommentInput').val().trim(),
                'parentCommentId': parentCommentId,
            }
            AuthFunc.apiRequest(() => {
                axios.post('/api/comment/insertComment',
                    body,
                    {headers: AuthFunc.getAuthHeader()})
            }).then(function (response) {

                if(response.parentCommentId != null) {
                    let html1 = ``;

                    html1 += `
                            <div class="comment d-flex mb-2"
                                     data-comment-id="${response.commentId}"
                                     data-writer="${response.writer}">
                                        <!-- 프로필 이미지 -->
                                        <img src= "${response.writerProfileImageUrl}" class="rounded-circle mr-2" 
                                             alt="userImage" style="width:32px; height:32px; object-fit:cover;">
                                
                                        <!-- 닉네임 + 댓글 내용 -->
                                        <div class="d-flex flex-column">
                                            <div>
                                                <span class="font-weight-bold mr-1">${response.writer}</span>
                                                <span>${response.contents}</span>
                                            </div>
                                        </div>
                            </div>
                    `;

                    // $('div.replies').last().append(html1);
                    $('div.replies').html(html1);
                    return;
                }

                $('textarea.pcCommentInput').val('');

            }).catch(function (error) {
                console.error(error);

                const status = error?.response?.status;
                const data = error?.response?.data;

                if (status === 409) {
                    const msg =
                        (typeof data === 'string' && data) ||
                        data?.customMessage ||
                        '요청이 충돌되어 처리할 수 없습니다.';
                    alert(msg);
                } else {
                    alert('댓글 입력이 안됨.');
                }

            })

        })


    }) // end of $('div.feed-container').on('click', 'button[name="comments"]', function () {})

}) // end of $(function () {})




// 댓글 이모티콘을 누르면 나오는 모달에 캐러셀만드는 함수
function buildCarouselHtml(imageUrls, carouselId) {
    if (!imageUrls || imageUrls.length === 0) {
        return '<div class="text-muted d-flex align-items-center justify-content-center" style="height: 200px;">이미지가 없습니다</div>';
    }

    if (imageUrls.length === 1) {
        return `<img src="${imageUrls[0]}" class="img-fluid rounded" alt="post image">`;
    }

    const indicatorsHtml = imageUrls
        .map((_, idx) =>
            `<li data-target="#${carouselId}" data-slide-to="${idx}" ${idx === 0 ? 'class="active"' : ''}></li>`
        )
        .join('');

    const slidesHtml = imageUrls
        .map((url, idx) =>
            `<div class="carousel-item ${idx === 0 ? 'active' : ''}">
         <img src="${url}" class="d-block w-100" alt="post image ${idx + 1}">
       </div>`
        )
        .join('');

    const controlsHtml = `
    <a class="carousel-control-prev" href="#${carouselId}" role="button" data-slide="prev">
      <span class="carousel-control-prev-icon" aria-hidden="true"></span>
      <span class="sr-only">Previous</span>
    </a>
    <a class="carousel-control-next" href="#${carouselId}" role="button" data-slide="next">
      <span class="carousel-control-next-icon" aria-hidden="true"></span>
      <span class="sr-only">Next</span>
    </a>`;

    return `
    <div id="${carouselId}" class="carousel slide" data-ride="carousel">
      <ol class="carousel-indicators">${indicatorsHtml}</ol>
      <div class="carousel-inner">${slidesHtml}</div>
      ${controlsHtml}
    </div>
  `;
}


// 좋아요를 누르면 하트색 변경 및 좋아요수 카운트 함수
function goLike(postId, thisBtn) {

    // 사용자의 연타 및 DB 중복, 성능저하를 방지하기 위해 busy 플래그 사용.
    const $btn = $(thisBtn);
    if ($btn.data('busy')) return;
    $btn.data('busy', true);

    return apiRequest(()=>
        $.ajax({
            url:"/api/like/goLike",
            data: {postId: postId},
            dataType: "json",
            type: "POST",
            headers: AuthFunc.getAuthHeader(),
            success: function (json) {

                const $card = $btn.closest('.post');           // 해당 카드 한 장
                const $likes = $card.find('.likes');

                if(json.isExist) {
                    // like 테이블에 save 가 성공되어지면 속이빈 하트를 속이 찬 하트로 바꾼다.
                    $(thisBtn).css('background-image', "url('/images/like/purpleLove.png')");
                }
                else{
                    // like 테이블에 이미 데이터가 있다면 속이 빈 하트로 바꿔준다.
                    $(thisBtn).css('background-image', "url('/images/like/emptyLove.png')");
                }

                // 좋아요 수 갱신 (서버에서 내려준 값이 있을 때)
                if (typeof json.postLikeCnt === 'number') {
                    $likes.text(`좋아요 ${json.postLikeCnt}개`);
                } else {
                    // 서버가 likeCount를 안 내려줄 경우의 안전한 폴백(낙관적 업데이트)
                    const prev = parseInt(($likes.text().match(/\d+/) || [0])[0], 10);
                    const next = json.isExist ? prev + 1 : Math.max(prev - 1, 0);
                    $likes.text(`좋아요 ${next}개`);
                }


            },
            error: function (xhr, status, error) {
                alert(xhr.responseText);
            },
            complete: function () {
                $btn.data('busy', false);
            }
        })
    );

} // end of function goLike(postId, thisBtn) {}


