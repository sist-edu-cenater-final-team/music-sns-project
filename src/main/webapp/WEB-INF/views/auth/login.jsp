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
    <script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>
    <link rel="stylesheet" href="<%=ctxPath%>/css/auth/login.css">
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
                    <button class="social-btn kakao" onclick="socialLogin('kakao')">
                        <i class="bi bi-chat-fill"></i>
                        카카오
                    </button>
                    <button class="social-btn naver" onclick="socialLogin('naver')">
                        <span class="naver-n">N</span>
                        네이버
                    </button>
                    <button class="social-btn github" onclick="socialLogin('github')">
                        <i class="bi bi-github"></i>
                        GitHub
                    </button>
                    <button class="social-btn microsoft" onclick="socialLogin('microsoft')">
                        <i class="bi bi-microsoft"></i>
                        Microsoft
                    </button>
                    <button class="social-btn google" onclick="socialLogin('google')">
                        <i class="bi bi-google"></i>
                        Google
                    </button>
                    <button class="social-btn twitter" onclick="socialLogin('twitter')">
                        <i class="bi bi-twitter"></i>
                        Twitter
                    </button>
                    <button class="social-btn facebook" onclick="socialLogin('facebook')">
                        <i class="bi bi-facebook"></i>
                        Facebook
                    </button>
                </div>
            </div>

            <!-- 회원가입 링크 -->
            <div class="signup-link">
                <p>아직 계정이 없으신가요? <a href="/signup">회원가입</a></p>
            </div>
        </div>
    </div>
</div>

<script src="/js/auth/login.js"></script>
</body>
</html>