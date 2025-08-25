<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!-- 회원가입 모달 -->
<div class="modal fade" id="signupModal" tabindex="-1" aria-labelledby="signupModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content signup-modal">
            <div class="modal-header">
                <div class="modal-title-section">
                    <div class="logo">
                        <i class="bi bi-music-note-beamed"></i>
                        <span>Music SNS</span>
                    </div>
                    <h4 class="modal-title" id="signupModalLabel">회원가입</h4>
                </div>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <form class="signup-form" id="signupForm">
                    <div class="row">
                        <div class="col-md-6">
                            <div class="input-group">
                                <label for="signupName">이름 <span class="required">*</span></label>
                                <div class="input-wrapper">
                                    <i class="bi bi-person-fill"></i>
                                    <input type="text" id="signupName" name="name" placeholder="홍길동">
                                </div>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="input-group">
                                <label for="signupNickname">닉네임 <span class="required">*</span></label>
                                <div class="nickname-verification-wrapper">
                                    <div class="input-wrapper">
                                        <i class="bi bi-at"></i>
                                        <input type="text" id="signupNickname" name="nickname" placeholder="무들">
                                        <button type="button" class="nickname-check-btn" id="nicknameCheckBtn" onclick="checkNicknameDuplicate()">
                                            중복확인
                                        </button>
                                    </div>
                                    <div class="verification-status" id="nicknameStatus" style="display: none;"></div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-md-6">
                            <div class="input-group">
                                <label for="signupEmail">이메일 <span class="required">*</span></label>
                                <div class="email-verification-wrapper">
                                    <div class="input-wrapper">
                                        <i class="bi bi-envelope-fill"></i>
                                        <input type="text" id="signupEmail" name="email" placeholder="example@naver.com">
                                        <button type="button" class="email-check-btn" id="emailCheckBtn" onclick="checkEmailDuplicate()">
                                            중복확인
                                        </button>
                                    </div>
                                    <!-- 이메일 상태 메시지 (항상 표시) -->
                                    <div class="verification-status" id="emailStatus" style="display: none;"></div>
                                    <!-- 인증번호 입력 섹션 -->
                                    <div class="verification-code-section" id="verificationSection" style="display: none;">
                                        <div class="input-wrapper">
                                            <i class="bi bi-shield-check"></i>
                                            <input type="text" id="verificationCode" placeholder="인증번호 6자리" maxlength="6">
                                            <button type="button" class="verify-btn" id="verifyBtn" onclick="verifyEmail()">
                                                인증확인
                                            </button>
                                            <div class="verification-timer" id="verificationTimer" style="display: none;">
                                                <i class="bi bi-clock"></i>
                                                <span id="timerText">10:00</span>
                                            </div>
                                        </div>
                                        <div class="verification-status" id="verificationStatus" style="display: none;"></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="input-group">
                                <label for="signupPhone">전화번호 <span class="required">*</span></label>
                                <div class="phone-verification-wrapper">
                                    <div class="input-wrapper">
                                        <i class="bi bi-telephone-fill"></i>
                                        <input type="tel" id="signupPhone" name="phoneNumber" placeholder="010-1234-5678">
                                        <button type="button" class="phone-check-btn" id="phoneCheckBtn" onclick="checkPhoneDuplicate()">
                                            중복확인
                                        </button>
                                    </div>
                                    <!-- 전화번호 상태 메시지 -->
                                    <div class="verification-status" id="phoneStatus" style="display: none;"></div>
                                    <!-- 인증번호 입력 섹션 -->
                                    <div class="verification-code-section" id="phoneVerificationSection" style="display: none;">
                                        <div class="input-wrapper">
                                            <i class="bi bi-shield-check"></i>
                                            <input type="text" id="phoneVerificationCode" placeholder="인증번호 6자리" maxlength="6">
                                            <button type="button" class="verify-btn" id="phoneVerifyBtn" onclick="verifyPhone()">
                                                인증확인
                                            </button>
                                            <div class="verification-timer" id="phoneVerificationTimer" style="display: none;">
                                                <i class="bi bi-clock"></i>
                                                <span id="phoneTimerText">10:00</span>
                                            </div>
                                        </div>
                                        <div class="verification-status" id="phoneVerificationStatus" style="display: none;"></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-md-6">
                            <div class="input-group">
                                <label for="signupPassword">비밀번호 <span class="required">*</span></label>
                                <div class="input-wrapper">
                                    <i class="bi bi-lock-fill"></i>
                                    <input type="password" id="signupPassword" name="password" autocomplete="new-password">
                                    <button type="button" class="password-toggle" onclick="toggleSignupPassword('signupPassword')">
                                        <i class="bi bi-eye"></i>
                                    </button>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="input-group">
                                <label for="passwordConfirm">비밀번호 확인 <span class="required">*</span></label>
                                <div class="input-wrapper">
                                    <i class="bi bi-lock-fill"></i>
                                    <input type="password" id="passwordConfirm" name="passwordConfirm" autocomplete="new-password">
                                    <button type="button" class="password-toggle" onclick="toggleSignupPassword('passwordConfirm')">
                                        <i class="bi bi-eye"></i>
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="terms-section">
                        <label class="terms-checkbox">
                            <input id="agreeTerms" type="checkbox" name="termsAgreed">
                            <span class="checkmark"></span>
                            <span class="terms-text">
                                <a href="#" class="terms-link">이용약관</a> 및
                                <a href="#" class="terms-link">개인정보처리방침</a>에 동의합니다. <span class="required">*</span>
                            </span>
                        </label>
                    </div>

                    <button type="submit" class="signup-btn">
                        <i class="bi bi-person-plus-fill"></i>
                        회원가입
                    </button>
                </form>
            </div>
        </div>
    </div>
</div>