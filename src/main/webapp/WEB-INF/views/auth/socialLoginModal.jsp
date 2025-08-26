<%--
  Created by IntelliJ IDEA.
  User: sihu
  Date: 25. 8. 25.
  Time: 오후 5:03
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!-- 소셜 로그인 모달 -->
<div class="modal fade" id="socialLoginModal" tabindex="-1" aria-labelledby="socialLoginModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="socialLoginModalLabel">소셜 로그인</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body p-0">
                <iframe id="socialLoginFrame" src="" style="width: 100%; height: 500px; border: none;"></iframe>
            </div>
        </div>
    </div>
</div>
