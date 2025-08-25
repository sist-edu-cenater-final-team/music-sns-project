(() => {

const authHeader = AuthFunc.getAuthHeader();//즉시호출
const apiRequest = AuthFunc.apiRequest;//함수참조
let imageEditor = null;
let editorResizeObserver = null;

$(document).ready(function () {
    // 모달 열릴 때 Bootstrap 포커스 트랩 해제 (텍스트 입력 이슈 해결)
    $('#postModal').on('shown.bs.modal', function () {
        $(document).off('focusin.bs.modal');
        // 에디터가 이미 있다면 모달 크기를 에디터에 맞춤
        requestAnimationFrame(() => syncModalToEditor());
    });

    // 모달이 닫힐 때 정리(메모리 누수 및 중복 초기화 방지)
    $('#postModal').on('hidden.bs.modal', function () {
        try { imageEditor?.destroy?.(); } catch (ignore) {}
        imageEditor = null;

        if (editorResizeObserver) {
            try { editorResizeObserver.disconnect(); } catch (ignore) {}
            editorResizeObserver = null;
        }
        imageArray = [];
        imageSeq = 0;
        $('#tui-image-editor').empty();
        $("#title").val("");
        $("#contents").val("");
        $("#previewText").text("");
        $("#previewImage").removeAttr("src");
        $("#step1").show();
        $("#step2").hide();
        $("#step3").hide();
        $('#selectImageDelete').empty();
        cleanupPreviewCarousel();
        resetEmotionToCalm();
    });

    // 윈도우 리사이즈/회전 시에도 동기화
    const onWindowResize = debounce(syncModalToEditor, 100);
    $(window).on('resize', onWindowResize);
    window.addEventListener('orientationchange', onWindowResize, { passive: true });

    // Step1 → Step2
    $('#btnNext').on('click', function () {
        const contents = $("#contents");
        const title = $("#title");

        if(title.val().trim() === "") {
            alert("제목을 입력하세요");
            title.focus();
            return false;
        }

        if (contents.val().trim() === "") {
            alert("문구를 입력하세요");
            contents.focus();
            return false;
        }

        $("#step1").hide();
        $("#step2").show();

        createImageEditor();

    });



    // Step2 → Step3 (미리보기)
    $("#btnNextStep2").on("click", function () {

        if(!imageArray.length > 0){
            alert("사진을 선택하세요");
            return false;
        }

        $("#step2").hide();
        $("#step3").show();

        $('#previewTitle').text($("#title").val());
        $("#previewText").text($("#contents").val());
        renderPreviewCarousel(imageArray);
    });

    // 이미지저장버튼을 누르면 이미지를 리스트에 저장한다.
    let imageArray = [];
    let imageSeq = 0;

    // 1) 저장 버튼 클릭 시: 배열에 파일 넣고 버튼 영역 다시 렌더
    $('button#imageSave').on('click', function () {
        const src = imageEditor.toDataURL();
        const name = `image_${++imageSeq}.png`;
        imageArray.push(base64toFile(src, name));

        createImageEditor(); // 기존 로직 유지

        renderImageDeleteButtons(); // 버튼 다시 그리기
    });

    // 2) 버튼 렌더링 함수: data-idx로 배열 인덱스 보관
    function renderImageDeleteButtons() {
        let html = '';
        imageArray.forEach((file, idx) => {
            html += `
      <button type="button" class="btn btn-danger mr-2 mb-2 imageArrayDelete" data-idx="${idx}">
        ${idx + 1}번 이미지 삭제
      </button>`;
        });
        $('div#selectImageDelete').html(html);
    }

    // 3) 삭제: 동적 요소는 위임으로 처리
    $('div#selectImageDelete').on('click', 'button.imageArrayDelete', function () {
        const idx = Number($(this).data('idx'));
        if (Number.isNaN(idx)) return;

        // 배열에서 제거
        imageArray.splice(idx, 1);

        console.log(imageArray);

        // 버튼을 다시 그려 인덱스를 재배열
        renderImageDeleteButtons();
    });

    let previewObjectUrls = [];

    // 캐러셀 채우기
    function renderPreviewCarousel(files) {
        const $carousel = $('#previewCarousel');
        const $indicators = $carousel.find('.carousel-indicators');
        const $inner = $carousel.find('.carousel-inner');

        // 이전 내용 정리
        cleanupPreviewCarousel();

        if (!files || files.length === 0) {
            $indicators.empty();
            $inner.html('<div class="d-flex align-items-center justify-content-center h-100 text-muted">이미지가 없습니다</div>');
            return;
        }

        let indicatorsHtml = '';
        let innerHtml = '';

        files.forEach((file, idx) => {
            const url = URL.createObjectURL(file); // File -> 미리보기 URL
            previewObjectUrls.push(url);

            indicatorsHtml += `<li data-target="#previewCarousel" data-slide-to="${idx}" ${idx === 0 ? 'class="active"' : ''}></li>`;
            innerHtml += `
      <div class="carousel-item ${idx === 0 ? 'active' : ''}" style="width:100%; height:100%;">
        <img class="d-block w-100 h-100" src="${url}" alt="preview ${idx + 1}" style="object-fit: contain; background:#000;">
      </div>`;
        });

        $indicators.html(indicatorsHtml);
        $inner.html(innerHtml);

        // 첫 슬라이드로 초기화
        $carousel.carousel(0);
    }

    // 캐러셀 정리(메모리 해제 + DOM 비우기)
    function cleanupPreviewCarousel() {
        if (Array.isArray(previewObjectUrls)) {
            previewObjectUrls.forEach(url => { try { URL.revokeObjectURL(url); } catch (e) {} });
        }
        previewObjectUrls = [];

        const $carousel = $('#previewCarousel');
        $carousel.find('.carousel-indicators').empty();
        $carousel.find('.carousel-inner').empty();
    }



    const emotionButton = document.querySelectorAll('.post_step1 .emotions .btn');
    // console.log(emotionButton);

    let userEmotion = "평온";
    emotionButton.forEach(function (btn , index) {
        btn.addEventListener('click', function () {
            const emotionNumber = index;
            // console.log(emotionNumber);

            switch (emotionNumber) {
                case 0:
                    userEmotion = "평온";
                    break;
                case 1:
                    userEmotion = "기쁨";
                    break;
                case 2:
                    userEmotion = "사랑";
                    break;
                case 3:
                    userEmotion = "우울";
                    break;
                case 4:
                    userEmotion = "화남";
                    break;
                case 5:
                    userEmotion = "피곤";
                    break;

            }
            // console.log(userEmotion);
        });
    });

    //
    $('#btnUploadStep1').on('click', function () {

        const title = $("#title").val();
        const contents = $("#contents").val();
        const ctxPath = $("#btnUploadStep1").data('contextPath');

        // console.log(userEmotion);
        return apiRequest(() =>
            $.ajax({
                url: `/api/post/postTextAndTitle`,
                type: "post",
                contentType: 'application/json; charset=UTF-8',
                processData: false, // 문자열을 그대로 보냄
                dataType: "json",
                headers: authHeader,
                data: JSON.stringify({
                    title: title,
                    contents: contents,
                    userEmotion: userEmotion
                }),
                success: function (json) {
                    console.log(JSON.stringify(json));

                    alert('업로드 성공했습니다.');
                    location.reload();


                },
                error: function (request, status, error) {
                    console.error('code: ' + request.status + '\nmessage: ' + (request.responseText || '') + '\nerror: ' + error);
                }
            })
        );
    })

    $('#btnUploadStep3').on('click', function () {
        if (!Array.isArray(imageArray) || imageArray.length === 0) {
            alert('업로드할 이미지가 없습니다.');
            return;
        }

        const formData = new FormData();
        for (const file of imageArray) {
            formData.append('files', file, file.name); // @RequestPart("files")와 일치
        }

        const ctxPath = $('#btnUploadStep3').data('contextPath');
        const url = `${ctxPath}/api/storage?directory=${encodeURIComponent('profile_images')}&isBig=false`;

        const $btn = $(this).prop('disabled', true).text('업로드 중...');

        return apiRequest(() =>
            $.ajax({
                url: url,
                type: 'POST',
                data: formData,
                processData: false,
                contentType: false,
                dataType: 'json',
                xhr: function () {
                    const xhr = $.ajaxSettings.xhr();
                    if (xhr.upload) {
                        xhr.upload.addEventListener('progress', function (e) {
                            if (e.lengthComputable) {
                                const percent = Math.round((e.loaded / e.total) * 100);
                                // 진행률 UI 업데이트 가능
                            }
                        });
                    }
                    return xhr;
                },
                success: function (json) {
                    // 1) 업로드 응답에서 파일 목록 추출
                    const uploads = json?.success?.responseData ?? [];


                    // 2) URL과 이름 분리 하면 안되고 객체로 만들어줘야됨
                    const imageUrls = uploads.map(f => f.fileUrl).filter(Boolean);
                    const imageNames = uploads.map(f => f.fileName).filter(Boolean);
                    // 2) 파일명/URL을 가진 객체 배열 만들기 (유효한 것만)
                    const images = uploads
                        .map(f => ({ fileName: f.fileName, fileUrl: f.fileUrl }))
                        .filter(f => f.fileName && f.fileUrl);


                    // 3) 게시글 저장 요청
                    const postLoad = {
                        title: $("#title").val(),
                        contents: $("#contents").val(),
                        userEmotion: userEmotion, // 서버가 기대하는 값 형태 확인(예: enum명/코드)
                        images
                    };

                    $.ajax({
                        url: `/api/post/postTextAndTitle`,
                        type: 'POST',
                        contentType: 'application/json',
                        headers: authHeader,
                        data: JSON.stringify(postLoad),
                        success: function (json) {
                            // 게시글 저장 성공 시에만 UI 정리
                            imageArray = [];
                            $('#postModal').modal('hide');
                            // 필요 시 성공 알림/리다이렉트 등
                            location.href = '/index';
                        },
                        error: function (request, status, error) {
                            console.error('게시글 저장 실패:', status, error, request.responseText);
                            alert('게시글 저장 중 오류가 발생했습니다.');
                        }
                        // complete는 바깥 complete에서 버튼 제어
                    });
                },
                error: function (request, status, error) {
                    console.error('업로드 실패:', status, error, request.responseText);
                    alert('code: ' + request.status + '\nmessage: ' + (request.responseText || '') + '\nerror: ' + error);
                },
                complete: function () {
                    $btn.prop('disabled', false).text('업로드');
                }
            })
        );
    }); // end of $('#btnUploadStep3').on('click', function () {})

    // step2 에서 이전 버튼을 누르면 step1 의 모달을 띄워준다.
    $('#btnBeforeStep2').on('click', function () {

        $('#step2').hide();
        $('#step1').show();

    })

    // step3 에서 이전 버튼을 누르면 step2 의 모달을 띄워준다.
    $('#btnBeforeStep3').on('click', function () {
        $('#step3').hide();
        $('#step2').show();
        createImageEditor();
        cleanupPreviewCarousel();
    })


}); // end of $(document).ready(function () {})

// 모달 내부 감정 선택을 평온(CALM)으로 되돌리는 헬퍼
function resetEmotionToCalm() {
    const $emotions = $('.post_step1 .emotions');
    $emotions.find('.btn').removeClass('active').attr('aria-pressed', 'false');
    $emotions.find('.btn.natural').addClass('active').attr('aria-pressed', 'true');

    // 선택 감정을 따로 저장하는 곳이 있다면 함께 초기화 (있을 때만 동작)
    if (window.selectedEmotion !== undefined) window.selectedEmotion = 'CALM';
    if (typeof $('#selectedEmotion').val === 'function') $('#selectedEmotion').val('CALM');
}



// base64 를 File 객체로 변환
function base64toFile(base_data, filename) {

    var arr = base_data.split(','),
        mime = arr[0].match(/:(.*?);/)[1],
        bstr = atob(arr[1]),
        n = bstr.length,
        u8arr = new Uint8Array(n);

    while(n--){
        u8arr[n] = bstr.charCodeAt(n);
    }

    return new File([u8arr], filename, {type:mime});
}

function createImageEditor() {

    // toastUI image editor 초기화하고 다시 toastUI image editor 띄우기
    try { imageEditor?.destroy?.(); } catch (ignore) {}
    imageEditor = null;
    if (editorResizeObserver) { try { editorResizeObserver.disconnect(); } catch (ignore) {} editorResizeObserver = null; }
    $('#tui-image-editor').empty();

    // 원하는 기본 UI 크기
    const uiWidth  = 900;
    const uiHeight = 500;

    // TUI ImageEditor 생성
    try {
        imageEditor = new tui.ImageEditor('#tui-image-editor', {
            includeUI: {
                menu: ['crop', 'flip', 'rotate', 'draw', 'shape', 'icon', 'text', 'mask', 'filter'],
                initMenu: '',
                uiSize: { width: uiWidth, height: uiHeight },
                menuBarPosition: 'bottom'
            },
            cssMaxWidth: uiWidth,
            cssMaxHeight: uiHeight,
            selectionStyle: {
                cornerSize: 10,
                rotatingPointOffset: 24
            }
        });
    } catch (e) {
        console.error('TUI ImageEditor 초기화 실패:', e);
        alert('이미지 에디터 초기화 중 오류가 발생했습니다.');
        return;
    }

    // 모달을 에디터 크기에 맞춤 + 에디터 내부 UI 변동 감시
    requestAnimationFrame(() => {
        syncModalToEditor();
        observeEditorSize(); // 에디터 내부 UI가 변해도 자동 맞춤
    });
}



// 모달을 에디터 크기에 맞추는 함수
function syncModalToEditor() {
    const $modal = $('#postModal');
    if (!$modal.is(':visible')) return;

    const $dialog = $modal.find('.modal-dialog');
    const $body   = $modal.find('.modal-body');
    const $header = $modal.find('.modal-header');
    const $footer = $modal.find('.modal-footer');

    // 에디터 컨테이너 실제 렌더 크기
    const el = document.querySelector('#tui-image-editor');
    if (!el) return;

    const rect = el.getBoundingClientRect();
    let editorW = Math.round(rect.width)  || 900;
    let editorH = Math.round(rect.height) || 500;

    // 화면을 넘지 않도록 안전 여백(24px x 2)을 고려
    const maxDialogW = Math.max(320, window.innerWidth  - 48);
    const maxDialogH = Math.max(240, window.innerHeight - 48);

    // 헤더/푸터 높이만큼 body 높이를 계산
    const headerH = $header.outerHeight(true) || 0;
    const footerH = $footer.outerHeight(true) || 0;

    // 최종 너비/높이(뷰포트 한계 내)
    const finalW = Math.min(editorW, maxDialogW);
    const finalBodyH = Math.min(editorH, maxDialogH - headerH - footerH);

    // 모달에 반영
    $dialog.css('width', finalW + 'px');
    $body.css({ height: finalBodyH + 'px', overflow: 'auto' });

    // Bootstrap 레이아웃 보정(스크롤/여백 재계산)
    try { $modal.modal('handleUpdate'); } catch (ignore) {}

    // TUI 에디터도 모달 크기에 맞춰 리사이즈(지원되는 경우)
    try {
        if (imageEditor?.ui?.resizeEditor) {
            imageEditor.ui.resizeEditor({
                uiSize: { width: finalW + 'px', height: finalBodyH + 'px' }
            });
        }
    } catch (e) {
        // 일부 버전에서 ui.resizeEditor 미제공 시 무시
    }
}

// 에디터 내부 UI 변동(메뉴 열고 닫기 등)에도 모달 크기를 맞추기 위해 관찰
function observeEditorSize() {
    const el = document.querySelector('#tui-image-editor');
    if (!el || !('ResizeObserver' in window)) return;

    if (editorResizeObserver) {
        try { editorResizeObserver.disconnect(); } catch (ignore) {}
    }

    editorResizeObserver = new ResizeObserver(() => {
        // 레이아웃 안정화 후 동기화
        requestAnimationFrame(() => syncModalToEditor());
    });
    editorResizeObserver.observe(el);
}

// 간단한 디바운스
function debounce(fn, wait) {
    let t;
    return function () {
        clearTimeout(t);
        t = setTimeout(() => fn.apply(this, arguments), wait);
    };
}
})();
