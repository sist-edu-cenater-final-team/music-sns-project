<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
    String ctxPath = request.getContextPath();
%>

<html>
<head>
    <title>post</title>
    <link rel="stylesheet" href="<%= ctxPath %>/lib/bootstrap-4.6.2-dist/css/bootstrap.min.css">
    <script src="<%= ctxPath %>/lib/js/jquery-3.7.1.min.js"></script>
    <script src="<%= ctxPath %>/lib/bootstrap-4.6.2-dist/js/bootstrap.bundle.min.js"></script>
    <!-- TUI CSS (jsDelivr로 교체) -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/tui-color-picker@2.2.8/dist/tui-color-picker.min.css" />
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/tui-image-editor@3.15.3/dist/tui-image-editor.min.css" />

    <!-- JS (순서 중요, jsDelivr로 교체) -->
    <script src="https://cdn.jsdelivr.net/npm/fabric@3.6.3/dist/fabric.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/tui-code-snippet@2.3.2/dist/tui-code-snippet.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/tui-color-picker@2.2.8/dist/tui-color-picker.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/tui-image-editor@3.15.3/dist/tui-image-editor.min.js"></script>



    <style>
        /* 모달이 에디터 너비를 충분히 사용할 수 있도록 */
        #postModal .modal-dialog {
            max-width: none; /* Bootstrap 기본 max-width 해제 */
            width: auto;     /* JS에서 정확한 px를 넣어줌 */
        }
        /* 여백 제거: 에디터가 꽉 차게 */
        #postModal .modal-body {
            padding: 0;
        }
        /* 스크롤바가 모달 바깥에 생기지 않도록 */
        #postModal .modal-content {
            overflow: hidden;
        }
    </style>

    <script type="text/javascript">
        let imageEditor = null;
        let editorResizeObserver = null;

        $(document).ready(function () {
            // 모달 열릴 때 Bootstrap 포커스 트랩 해제 (텍스트 입력 이슈 해결)
            $('#postModal').on('shown.bs.modal', function () {
                $(document).off('focusin.bs.modal');
                // 에디터가 이미 있다면 모달 크기를 에디터에 맞춤
                requestAnimationFrame(() => syncModalToEditor());
            });

            // 윈도우 리사이즈 시에도 동기화
            $(window).on('resize', debounce(syncModalToEditor, 100));

            // Step1 → Step2
            $('#btnNext').on('click', function () {
                if ($("#contents").val().trim() === "") {
                    alert("문구를 입력하세요");
                    $("#contents").focus();
                    return false;
                }

                $("#step1").hide();
                $("#step2").show();

                // 기존 에디터 제거
                try { imageEditor?.destroy?.(); } catch (ignore) {}
                if (editorResizeObserver) { try { editorResizeObserver.disconnect(); } catch (ignore) {} editorResizeObserver = null; }
                $('#tui-image-editor').empty();

                // 원하는 기본 UI 크기
                const uiWidth  = 900;
                const uiHeight = 500;

                imageEditor = new tui.ImageEditor('#tui-image-editor', {
                    includeUI: {
                        menu: ['crop', 'flip', 'rotate', 'draw', 'shape', 'icon', 'text', 'mask', 'filter'],
                        initMenu: '',
                        uiSize: { width: uiWidth, height: uiHeight },
                        menuBarPosition: 'bottom'
                    },
                    cssMaxWidth: uiWidth,
                    cssMaxHeight: uiHeight
                });

                // 모달을 에디터 크기에 맞춤
                requestAnimationFrame(() => {
                    syncModalToEditor();
                    observeEditorSize(); // 에디터 내부 UI가 변해도 자동 맞춤
                });
            });
        });

        // 모달을 에디터 크기에 맞추는 함수
        function syncModalToEditor() {
            const $modal  = $('#postModal');
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
            if (editorResizeObserver) { try { editorResizeObserver.disconnect(); } catch (ignore) {} }
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

        $(function (){

            // step2 에서 다음 버튼을 누르면 편집한 이미지와 작성한 글을 미리보기로 보여준다.
            $("#btnNextStep2").on("click", function(){
                $("#step2").hide();
                $("#step3").show();

                $("#previewText").text($("#contents").val());
                $("#previewImage").attr("src", imageEditor.toDataURL());
                // console.log($("#previewImage").attr("src", imageEditor.toDataURL()))
                // ce.fn.init

            })

        })


    </script>


</head>
<body>

<h1>post</h1>
<div class="container mt-5">
    <button class="btn btn-primary" type="button" data-toggle="modal" data-target="#postModal">
        새 게시물 만들기
    </button>
</div>

<!-- 모달 -->
<div class="modal fade" id="postModal" tabindex="-1" aria-labelledby="postModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-lg">
        <div class="modal-content">

            <!-- 헤더 -->
            <div class="modal-header">
                <h5 class="modal-title" id="postModalLabel">새 게시물 만들기</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="닫기">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>

            <!-- 바디 -->
            <div class="modal-body text-center">

                <!-- STEP 1 -->
                <div id="step1" style="width: 500px; margin: 15% auto;">
                    <textarea class="form-control mb-3" id="contents" name="contents" rows="4" placeholder="문구를 입력하세요..."></textarea>
                    <div class="d-flex justify-content-between">
                        <button type="button" class="btn btn-secondary" id="btnNext">다음</button>
                        <button type="button" class="btn btn-primary" id="btnUploadStep1">올리기</button>
                    </div>
                </div>

                <!-- STEP 2 -->
                <div id="step2" style="display:none;">
                    <div id="tui-image-editor" style="height:500px;"></div>
                    <button type="button" class="btn btn-primary mt-3" id="btnNextStep2">다음</button>
                </div>

                <div id="step3" style="display:none;">
                    <img id="previewImage" style="width: 500px; height: 500px;"/>
                    <div class="mt-3">
                        <p id="previewText"></p>
                    </div>
                </div>

            </div>
        </div>
    </div>
</div>

</body>
</html>
