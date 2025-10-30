<%--
  Created by IntelliJ IDEA.
  User: sihu
  Date: 25. 10. 30.
  Time: 오후 1:08
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!-- 비밀번호 찾기 모달 -->
<div class="modal fade" id="forgotPasswordModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">비밀번호 찾기</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <!-- 단계 1: 이메일/전화번호 입력 -->
                <div id="step1" class="forgot-step">
                    <p class="step-description">계정을 찾기 위해 이메일 또는 전화번호를 입력해주세요.</p>
                    <div class="input-group mb-3">
                        <label for="forgotIdentifier">이메일 또는 전화번호</label>
                        <div class="forgot-input-wrapper">
                            <i class="bi bi-person-fill"></i>
                            <input type="text" id="forgotIdentifier" class="form-control" placeholder="이메일 또는 전화번호 입력">
                        </div>
                    </div>
                    <button type="button" class="btn btn-primary w-100" onclick="sendVerificationCode()">인증 코드 전송</button>
                </div>

                <!-- 단계 2: 인증 코드 입력 -->
                <div id="step2" class="forgot-step" style="display: none;">
                    <p class="step-description">인증 코드를 입력해주세요.</p>
                    <div class="input-group mb-3">
                        <label for="verificationCode">인증 코드</label>
                        <div class="forgot-input-wrapper">
                            <i class="bi bi-shield-check"></i>
                            <input type="text" id="verificationCode" class="form-control" placeholder="인증 코드 입력">
                        </div>
                        <small class="text-muted">남은 시간: <span id="timerDisplay">3:00</span></small>
                    </div>
                    <button type="button" class="btn btn-primary w-100" onclick="verifyCode()">인증</button>
                    <button type="button" class="btn btn-outline-secondary w-100 mt-2" onclick="goBackToStep1()">이전</button>
                </div>

                <!-- 단계 3: 새 비밀번호 설정 -->
                <div id="step3" class="forgot-step" style="display: none;">
                    <p class="step-description">새로운 비밀번호를 설정해주세요.</p>
                    <div class="input-group mb-3">
                        <label for="newPassword">새 비밀번호</label>
                        <div class="forgot-input-wrapper">
                            <i class="bi bi-lock-fill"></i>
                            <input type="password" id="newPassword" class="form-control" placeholder="새 비밀번호 입력">
                            <button type="button" class="password-toggle" onclick="toggleForgotPassword('newPassword')">
                                <i class="bi bi-eye"></i>
                            </button>
                        </div>
                    </div>
                    <div class="input-group mb-3">
                        <label for="confirmPassword">비밀번호 확인</label>
                        <div class="forgot-input-wrapper">
                            <i class="bi bi-lock-fill"></i>
                            <input type="password" id="confirmPassword" class="form-control" placeholder="비밀번호 확인">
                            <button type="button" class="password-toggle" onclick="toggleForgotPassword('confirmPassword')">
                                <i class="bi bi-eye"></i>
                            </button>
                        </div>
                    </div>
                    <button type="button" class="btn btn-primary w-100" onclick="changePassword()">비밀번호 변경</button>
                </div>
            </div>
        </div>
    </div>
</div>

<style>
    .forgot-step {
        animation: slideIn 0.3s ease-in-out;
    }

    @keyframes slideIn {
        from {
            opacity: 0;
            transform: translateY(10px);
        }
        to {
            opacity: 1;
            transform: translateY(0);
        }
    }

    .step-description {
        color: #666;
        margin-bottom: 1.5rem;
        font-size: 0.95rem;
    }

    .forgot-input-wrapper {
        display: flex;
        align-items: center;
        border: 1px solid #ddd;
        border-radius: 8px;
        padding: 0.5rem;
        margin-top: 0.5rem;
    }

    .forgot-input-wrapper i {
        margin-right: 0.5rem;
        color: #999;
    }

    .forgot-input-wrapper input {
        border: none;
        flex: 1;
        outline: none;
    }

    .password-toggle {
        background: none;
        border: none;
        cursor: pointer;
        color: #999;
    }

    .password-toggle:hover {
        color: #333;
    }
</style>
