<%--
  Created by IntelliJ IDEA.
  User: sihu
  Date: 25. 8. 21.
  Time: 오후 2:27
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%String ctxPath = request.getContextPath();%>
<script>const ctxPath = '<%=ctxPath%>'</script>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>로그인 - Music SNS</title>
    <script src="<%=ctxPath%>/lib/jquery-3.7.1.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    <script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>
    <link rel="stylesheet" href="<%=ctxPath%>/css/auth/login.css">
    <link rel="stylesheet" href="<%=ctxPath%>/css/auth/signup.css">
</head>
<body>
<div class="login-container">
    <div class="login-wrapper">
        <!-- 로고 섹션 -->
        <div class="logo-section">
            <div class="logo">
                <i class="bi bi-music-note-beamed"></i>
                <span>Music SNS</span>
            </div>
            <p class="welcome-text">음악과 함께하는 특별한 순간</p>
        </div>

        <!-- 로그인 폼 -->
        <div class="login-form-container">
            <h2 class="form-title">로그인</h2>
            <!-- 테스트 계정 선택 -->
            <div class="test-account-section">
                <div class="test-account-header" onclick="toggleTestAccounts()">
                    <i class="bi bi-person-gear"></i>
                    <span>테스트 계정으로 빠른 로그인</span>
                    <i class="bi bi-chevron-down toggle-icon" id="testToggleIcon"></i>
                </div>
                <div class="test-accounts-list" id="testAccountsList">
                    <div class="test-account-item" onclick="selectTestAccount('abc1@abc.com')">
                        <i class="bi bi-person-circle"></i>
                        <span>박한빈 (abc1@abc.com)</span>
                    </div>
                    <div class="test-account-item" onclick="selectTestAccount('abc2@abc.com')">
                        <i class="bi bi-person-circle"></i>
                        <span>엄정화 (abc2@abc.com)</span>
                    </div>
                    <div class="test-account-item" onclick="selectTestAccount('abc3@abc.com')">
                        <i class="bi bi-person-circle"></i>
                        <span>카리나 (abc3@abc.com)</span>
                    </div>
                    <div class="test-account-item" onclick="selectTestAccount('abc4@abc.com')">
                        <i class="bi bi-person-circle"></i>
                        <span>이시후 (abc4@abc.com)</span>
                    </div>
                </div>
            </div>

            <form class="login-form" id="loginForm">
                <div class="input-group">
                    <label for="identifier">이메일 또는 전화번호</label>
                    <div class="input-wrapper">
                        <i class="bi bi-person-fill"></i>
                        <input type="text" id="identifier" name="identifier" required>
                    </div>
                </div>

                <div class="input-group">
                    <label for="password">비밀번호</label>
                    <div class="input-wrapper">
                        <i class="bi bi-lock-fill"></i>
                        <input type="password" id="password" name="password" required autocomplete="current-password">
                        <button type="button" class="password-toggle" onclick="togglePassword()">
                            <i id="passwordEye" class="bi bi-eye"></i>
                        </button>
                    </div>
                </div>

                <div class="form-options">
                    <label class="remember-me">
                        <input type="checkbox" name="remember">
                        <span class="checkmark"></span>
                        이메일 또는 전화번호 저장
                    </label>
                    <a href="#" class="forgot-password">비밀번호 찾기</a>
                </div>

                <button type="submit" class="login-btn">
                    <i class="bi bi-box-arrow-in-right"></i>
                    로그인
                </button>
            </form>

            <!-- 구분선 -->
            <div class="divider">
                <span>또는</span>
            </div>

            <!-- 소셜 로그인 -->
            <div class="social-login">
                <h3>소셜 로그인</h3>
                <div class="social-buttons">
                    <button class="social-btn" onclick="socialLogin('kakao')" title="카카오 로그인">
                        <img src="<%=ctxPath%>/images/oauth/kakao.png" alt="카카오">
                    </button>
                    <button class="social-btn" onclick="socialLogin('naver')" title="네이버 로그인">
                        <img src="<%=ctxPath%>/images/oauth/naver.png" alt="네이버">
                    </button>
                    <button class="social-btn" onclick="socialLogin('github')" title="GitHub 로그인">
                        <img src="<%=ctxPath%>/images/oauth/github.png" alt="GitHub">
                    </button>
                    <button class="social-btn" onclick="socialLogin('microsoft')" title="Microsoft 로그인">
                        <img src="<%=ctxPath%>/images/oauth/microsoft.png" alt="Microsoft">
                    </button>
                    <button class="social-btn" onclick="socialLogin('google')" title="Google 로그인">
                        <img src="<%=ctxPath%>/images/oauth/google.png" alt="Google">
                    </button>
                    <button class="social-btn" onclick="socialLogin('twitter')" title="Twitter 로그인">
                        <img src="<%=ctxPath%>/images/oauth/twitter.png" alt="Twitter">
                    </button>
                    <button class="social-btn" onclick="socialLogin('facebook')" title="Facebook 로그인">
                        <img src="<%=ctxPath%>/images/oauth/facebook.png" alt="Facebook">
                    </button>
                </div>
            </div>

            <!-- 회원가입 링크 -->
            <div class="signup-link">
                <p>아직 계정이 없으신가요? <a id="signUpBtn">회원가입</a></p>
            </div>
        </div>
    </div>
</div>

<script src="<%=ctxPath%>/js/auth/login.js"></script>
<script src="<%=ctxPath%>/js/auth/signup.js"></script>
<script src="<%=ctxPath%>/js/auth/oauth/loginSignUp.js"></script>
<jsp:include page="./signUpModal.jsp"/>

<script src="<%=ctxPath%>/js/auth/token.js"></script>
<script>
    //만약 로그인 된 상태라면 메인으로 이동
    const authHeader = AuthFunc.getAuthHeader();
    console.log('Auth Header:', authHeader); // 디버그용 로그
    const isLoggedIn = AuthFunc.primaryKey().then(isLoggedIn => {
        if (isLoggedIn) {
            window.location.href = ctxPath + '/music/chart';
        }
    })

    // 테스트 계정 관련 함수들
    function toggleTestAccounts() {
        const testAccountsList = document.getElementById('testAccountsList');
        const toggleIcon = document.getElementById('testToggleIcon');

        if (testAccountsList.classList.contains('show')) {
            testAccountsList.classList.remove('show');
            toggleIcon.classList.remove('rotated');
        } else {
            testAccountsList.classList.add('show');
            toggleIcon.classList.add('rotated');
        }
    }

    function selectTestAccount(email) {
        document.getElementById('identifier').value = email;
        document.getElementById('password').value = '12341234a!';

        // 테스트 계정 목록 닫기
        const testAccountsList = document.getElementById('testAccountsList');
        const toggleIcon = document.getElementById('testToggleIcon');
        testAccountsList.classList.remove('show');
        toggleIcon.classList.remove('rotated');

        // 선택 효과
        showMessage('계정의 이메일과 비밀번호가 입력되었습니다.<br>로그인 버튼을 클릭해 주세요.', 'success');
    }
</script>

</body>
</html>