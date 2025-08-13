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
        $('#tui-image-editor').empty();
        $("#contents").val("");
        $("#previewText").text("");
        $("#previewImage").removeAttr("src");
        $("#step1").show();
        $("#step2").hide();
        $("#step3").hide();
    });

    // 윈도우 리사이즈/회전 시에도 동기화
    const onWindowResize = debounce(syncModalToEditor, 100);
    $(window).on('resize', onWindowResize);
    window.addEventListener('orientationchange', onWindowResize, { passive: true });

    // Step1 → Step2
    $('#btnNext').on('click', function () {
        const $contents = $("#contents");
        if ($contents.val().trim() === "") {
            alert("문구를 입력하세요");
            $contents.focus();
            return false;
        }

        $("#step1").hide();
        $("#step2").show();

        // 기존 에디터 제거
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
    });

    // Step2 → Step3 (미리보기)
    $("#btnNextStep2").on("click", function () {
        $("#step2").hide();
        $("#step3").show();

        $("#previewText").text($("#contents").val());
        $("#previewImage").attr("src", imageEditor.toDataURL());

    });

    // 이미지저장버튼을 누르면 이미지를 리스트에 저장한다.
    $('button#imageSave').on('click', function () {

        let imageArray = [];



    })

    // 올리기 버튼을 누르면 데이터 삽입
    $('#btnUploadStep1').on('click', function () {



    })

});




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
