document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('loginForm');

    loginForm.addEventListener('submit', function(e) {
        e.preventDefault();

        const identifier = document.getElementById('identifier').value;
        const password = document.getElementById('password').value;

        if (!identifier || !password) {
            showMessage('모든 필드를 입력해주세요.', 'warning');
            return;
        }

        // 로그인 처리
        handleLogin(identifier, password);
    });
});

function togglePassword() {
    const passwordInput = document.getElementById('password');
    const toggleBtn = document.querySelector('.password-toggle i');

    if (passwordInput.type === 'password') {
        passwordInput.type = 'text';
        toggleBtn.className = 'bi bi-eye-slash';
    } else {
        passwordInput.type = 'password';
        toggleBtn.className = 'bi bi-eye';
    }
}
let lockoutTimer = null; // 계정 잠금 타이머

function handleLogin(identifier, password) {
    // 기존 메시지 제거
    removeMessage();

    // 로딩 상태 표시
    const loginBtn = document.querySelector('.login-btn');
    const originalText = loginBtn.innerHTML;
    loginBtn.innerHTML = '<i class="bi bi-hourglass-split"></i> 로그인 중...';
    loginBtn.disabled = true;

    // axios API 호출
    axios.post('/api/auth/login', {
        emailOrPhoneNumber: identifier,
        password: password
    })
        .then(response => {
            const responseData = response.data.success.responseData;
            localStorage.setItem('accessToken', responseData.accessToken);
            localStorage.setItem('tokenType', responseData.tokenType);


            // 성공시 타이머 정리
            clearLockoutTimer();
            showMessage('로그인에 성공했습니다.', 'success');
            setTimeout(() => {
                window.location.href = '/';
            }, 1000);
        })
        .catch(error => {
            console.error('로그인 오류:', error);
            if (error.response) {
                const errorData = error.response.data.error;
                console.log('에러 정보:', errorData);

                if (errorData.request) {
                    const requestInfo = errorData.request;
                    // console.log(requestInfo.status === "잠긴 계정")
                    // 계정 잠김 상태 확인
                    if (requestInfo.status === "잠긴 계정") {
                        console.log(requestInfo.status)
                        handleAccountLockout(requestInfo.failureDate);
                        loginBtn.innerHTML = '<i class="bi bi-hourglass-split"></i> 잠김';
                    }
                    // 실패 횟수가 있는 경우 (5번 미만 실패)
                    else if (requestInfo.failureCount !== undefined) {
                        const remainingAttempts = 5 - requestInfo.failureCount;
                        if (remainingAttempts <= 3) {
                            showWarningMessage(remainingAttempts, errorData.customMessage);
                        } else {
                            showMessage(errorData.customMessage, 'error');
                        }
                    }
                    // 기타 에러
                    else {
                        console.log(errorData.request);
                        showMessage(errorData.customMessage || '로그인에 실패했습니다.', 'error');
                    }
                } else {
                    showMessage(errorData.customMessage || '로그인에 실패했습니다.', 'error');
                }
            } else if (error.request) {
                showMessage('서버에 연결할 수 없습니다.', 'error');
            } else {
                showMessage('알 수 없는 오류가 발생했습니다.', 'error');
            }
        })
        .finally(() => {
            // 로딩 상태 해제 (잠긴 계정이 아닌 경우에만)
            if (!isAccountLocked()) {
                loginBtn.innerHTML = originalText;
                loginBtn.disabled = false;
            }
        });
}
function showMessage(message, type) {
    // 기존 메시지 제거
    removeMessage();

    const messageDiv = document.createElement('div');
    messageDiv.id = 'auth-message';
    messageDiv.className = `auth-message ${type}`;

    let icon = '';
    switch(type) {
        case 'success':
            icon = 'bi-check-circle-fill';
            break;
        case 'error':
            icon = 'bi-exclamation-circle-fill';
            break;
        case 'warning':
            icon = 'bi-exclamation-triangle-fill';
            break;
        default:
            icon = 'bi-info-circle-fill';
    }

    messageDiv.innerHTML = `
        <div class="message-content">
            <i class="bi ${icon}"></i>
            <span>${message}</span>
        </div>
    `;

    // 로그인 폼 앞에 삽입
    const loginFormContainer = document.querySelector('.login-container');
    const formTitle = loginFormContainer.querySelector('.logo-section');
    formTitle.insertAdjacentElement('beforeend', messageDiv);

    // 3초 후 자동 제거 (에러가 아닌 경우)
    if (type !== 'error' && type !== 'warning') {
        setTimeout(() => {
            removeMessage();
        }, 3000);
    }
}
function showWarningMessage(remainingAttempts, customMessage) {
    // 기존 메시지 제거
    removeMessage();

    const messageDiv = document.createElement('div');
    messageDiv.id = 'auth-message';
    messageDiv.className = 'auth-message warning-attempt';

    messageDiv.innerHTML = `
        <div class="warning-content">
            <div class="warning-header">
                <i class="bi bi-exclamation-triangle-fill"></i>
                <span>비밀번호 오류</span>
            </div>
            <div class="warning-body">
<!--                <p class="error-text">${customMessage}</p>-->
                <p class="attempt-warning">
                    <strong>${remainingAttempts}번 더 실패 할 시 계정이 5분간 잠깁니다.</strong>
                </p>
            </div>
        </div>
    `;

    // 로그인 폼 앞에 삽입
    const loginFormContainer = document.querySelector('.login-container');
    const formTitle = loginFormContainer.querySelector('.logo-section');
    formTitle.insertAdjacentElement('beforeend', messageDiv);
}
function removeMessage() {
    const existingMessage = document.getElementById('auth-message');
    if (existingMessage) {
        existingMessage.remove();
    }
}

function isAccountLocked() {
    return document.getElementById('lockout-message') !== null;
}


function handleAccountLockout(failureDate) {
    // 기존 메시지 제거
    removeMessage();

    // failureDate 문자열을 Date 객체로 변환
    const lockStartTime = new Date(failureDate);
    const lockDuration = 5 * 60 * 1000; // 5분 (밀리초)
    const unlockTime = new Date(lockStartTime.getTime() + lockDuration);

    // 현재 시간과 비교하여 잠금 해제까지 남은 시간 계산
    const now = new Date();
    const remainingTime = unlockTime.getTime() - now.getTime();

    if (remainingTime > 0) {
        // 아직 잠금 상태
        showLockoutMessage(remainingTime);
        startLockoutTimer(remainingTime);

        // 로그인 버튼 비활성화
        const loginBtn = document.querySelector('.login-btn');
        loginBtn.disabled = true;
    } else {
        // 잠금 시간이 지남
        showMessage('계정 잠금이 해제되었습니다. 다시 로그인해주세요.', 'success');
    }
}

function showLockoutMessage(remainingTime) {
    const minutes = Math.floor(remainingTime / (1000 * 60));
    const seconds = Math.floor((remainingTime % (1000 * 60)) / 1000);

    // 기존 메시지 제거
    removeLockoutMessage();

    // 잠금 메시지 생성
    const messageDiv = document.createElement('div');
    messageDiv.id = 'lockout-message';
    messageDiv.className = 'lockout-message';
    messageDiv.innerHTML = `
        <div class="lockout-content">
            <div class="lockout-header">
                <i class="bi bi-lock-fill"></i>
                <h4>계정이 잠겼습니다</h4>
            </div>
            <div class="lockout-body">
                <p>비밀번호를 5번 잘못 입력하여 계정이 잠겼습니다.</p>
                <div class="countdown">
                    남은 시간: <span id="countdown-time">${minutes}분 ${seconds}초</span>
                </div>
            </div>
        </div>
    `;

    // 로그인 폼 앞에 삽입
    const loginFormContainer = document.querySelector('.login-container');
    const formTitle = loginFormContainer.querySelector('.logo-section');
    formTitle.insertAdjacentElement('beforeend', messageDiv);
}


function startLockoutTimer(remainingTime) {
    let timeLeft = remainingTime;

    lockoutTimer = setInterval(() => {
        timeLeft -= 1000;

        if (timeLeft <= 0) {
            // 잠금 해제
            clearLockoutTimer();
            removeLockoutMessage();

            // 로그인 버튼 활성화
            const loginBtn = document.querySelector('.login-btn');
            loginBtn.disabled = false;
            loginBtn.innerHTML = '<i class="bi bi-box-arrow-in-right"></i> 로그인';

            showMessage('계정 잠금이 해제되었습니다. 다시 로그인해주세요.', 'success');
        } else {
            // 남은 시간 업데이트
            const minutes = Math.floor(timeLeft / (1000 * 60));
            const seconds = Math.floor((timeLeft % (1000 * 60)) / 1000);

            const countdownElement = document.getElementById('countdown-time');
            if (countdownElement) {
                countdownElement.textContent = `${minutes}분 ${seconds}초`;
            }
        }
    }, 1000);
}


function clearLockoutTimer() {
    if (lockoutTimer) {
        clearInterval(lockoutTimer);
        lockoutTimer = null;
    }
}

function removeLockoutMessage() {
    const existingMessage = document.getElementById('lockout-message');
    if (existingMessage) {
        existingMessage.remove();
    }
}

// 페이지 언로드시 타이머 정리
window.addEventListener('beforeunload', clearLockoutTimer);

async function requestAuthUrl(provider) {
    const redirectUri = `${window.location.origin}${ctxPath}/oauth/${provider}/callback`;
    const apiUrl = `${ctxPath}/api/oauth/${provider}`;
    const response = await axios.get(apiUrl, {
        params: {redirectUri: redirectUri}
    });
    if (response.status !== 200) {
        console.error('소셜 로그인 URL 요청 실패:', response);
        return;
    }
    return response.data.success.responseData;
}

async function socialLogin(provider) {

    const authUrl = await requestAuthUrl(provider);
    requestOAuthLogin(authUrl);
}

function requestOAuthLogin(authUrl) {
    // 새 창으로 인증 URL 열기
    const width = 600;
    const height = 700;
    const left = (window.innerWidth - width) / 2;
    const top = (window.innerHeight - height) / 2;

    window.open(authUrl, 'OAuth Login', `width=${width},height=${height},left=${left},top=${top}`);
}
