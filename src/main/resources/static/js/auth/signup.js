// 회원가입 모달 관련 함수들
document.addEventListener('DOMContentLoaded', function() {
    // 회원가입 링크 클릭 시 모달 열기
    const signupLinks = document.querySelectorAll('a[href="/signup"]');
    signupLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            openSignupModal();
        });
    });

    // 회원가입 폼 제출 이벤트
    const signupForm = document.getElementById('signupForm');
    if (signupForm) {
        signupForm.addEventListener('submit', function(e) {
            e.preventDefault();
            handleSignup();
        });
    }

    // 비밀번호 확인 실시간 검증
    const passwordConfirm = document.getElementById('passwordConfirm');
    if (passwordConfirm) {
        passwordConfirm.addEventListener('input', validatePasswordMatch);
    }
});
function openSignupModal() {

    // Bootstrap 5 방식으로 모달 초기화
    const signupModal = document.getElementById('signupModal');
    if (signupModal) {
        // 이메일 인증 상태 초기화
        resetEmailVerification();
        const modal = bootstrap.Modal.getOrCreateInstance(signupModal);

        // 모달이 완전히 열린 후 포커스 관리
        signupModal.addEventListener('shown.bs.modal', function() {
            // 첫 번째 입력 필드에 포커스
            const firstInput = signupModal.querySelector('input');
            if (firstInput) {
                firstInput.focus();
            }
        }, { once: true });

        // 모달이 숨겨지기 전 포커스 해제
        signupModal.addEventListener('hide.bs.modal', function() {
            // 현재 포커스된 요소가 모달 내부에 있으면 포커스 해제
            const activeElement = document.activeElement;
            if (signupModal.contains(activeElement)) {
                activeElement.blur();
            }
        }, { once: true });

        modal.show();
    }
}
function toggleSignupPassword(inputId) {
    const passwordInput = document.getElementById(inputId);
    const toggleBtn = passwordInput.nextElementSibling.querySelector('i');

    if (passwordInput.type === 'password') {
        passwordInput.type = 'text';
        toggleBtn.className = 'bi bi-eye-slash';
    } else {
        passwordInput.type = 'password';
        toggleBtn.className = 'bi bi-eye';
    }
}

function validatePasswordMatch() {
    const password = document.getElementById('signupPassword').value;
    const passwordConfirm = document.getElementById('passwordConfirm').value;
    const confirmInput = document.getElementById('passwordConfirm');

    if (passwordConfirm && password !== passwordConfirm) {
        confirmInput.style.borderColor = '#e53e3e';
        confirmInput.style.background = '#fff5f5';
    } else {
        confirmInput.style.borderColor = '#48bb78';
        confirmInput.style.background = '#f0fff4';
    }
}

function handleSignup() {
    // 기존 메시지 제거
    removeSignupMessage();

    // 폼 데이터 수집
    const formData = {
        email: document.getElementById('signupEmail').value,
        nickname: document.getElementById('signupNickname').value,
        username: document.getElementById('signupName').value,
        phoneNumber: document.getElementById('signupPhone').value,
        // dateOfBirth: document.getElementById('dateOfBirth').value,
        password: document.getElementById('signupPassword').value,
        passwordConfirm: document.getElementById('passwordConfirm').value
    };

    // 유효성 검사
    if (!validateSignupForm(formData)) {
        return;
    }

    // 약관 동의 확인
    if (!document.getElementById('agreeTerms').checked) {
        showSignupMessage('이용약관 및 개인정보처리방침에 동의해주세요.', 'error');
        return;
    }

    // 로딩 상태 표시
    const signupBtn = document.querySelector('.signup-btn');
    const originalText = signupBtn.innerHTML;
    signupBtn.innerHTML = '<i class="bi bi-hourglass-split"></i> 가입 중...';
    signupBtn.disabled = true;

    // API 호출
    axios.post('/api/auth/sign-up', formData)
        .then(response => {
            console.log(response)
            showSignupMessage('회원가입이 완료되었습니다!', 'success');

            // 3초 후 모달 닫기 및 로그인 폼으로 이동
            setTimeout(() => {
                const signupModal = bootstrap.Modal.getInstance(document.getElementById('signupModal'));
                signupModal.hide();

                // 로그인 폼에 이메일 자동 입력
                const loginIdentifier = document.getElementById('identifier');
                if (loginIdentifier) {
                    loginIdentifier.value = formData.email;
                }

                // 로그인 페이지의 showMessage 함수 호출
                if (typeof showMessage === 'function') {
                    showMessage('회원가입이 완료되었습니다. 로그인해주세요.', 'success');
                }
            }, 2000);
        })
        .catch(error => {
            console.error('회원가입 오류:', error);

            if (error.response && error.response.data && error.response.data.error) {
                const errorData = error.response.data.error;
                showSignupMessage(errorData.customMessage || '회원가입에 실패했습니다.', 'error');
            } else if (error.request) {
                showSignupMessage('서버에 연결할 수 없습니다.', 'error');
            } else {
                showSignupMessage('알 수 없는 오류가 발생했습니다.', 'error');
            }
        })
        .finally(() => {
            // 로딩 상태 해제
            signupBtn.innerHTML = originalText;
            signupBtn.disabled = false;
        });
}

function validateSignupForm(formData) {
    // 이메일 인증 확인
    if (!emailVerified) {
        showSignupMessage('이메일 인증을 완료해주세요.', 'error');
        return false;
    }
    // 필수 필드 검사 (이메일 제외 - 이미 인증됨)
    const requiredFields = ['nickname', 'username', 'phoneNumber', 'password', 'passwordConfirm'];
    for (const field of requiredFields) {
        if (!formData[field] || formData[field].trim() === '') {
            showSignupMessage('모든 필수 항목을 입력해주세요.', 'error');
            return false;
        }
    }

    // 전화번호 형식 검사
    const phoneRegex = /^010\d{8}$/;
    if (!phoneRegex.test(formData.phoneNumber)) {
        showSignupMessage('전화번호는 010으로 시작하는 11자리 숫자를 입력해주세요.', 'error');
        return false;
    }

    // 비밀번호 일치 검사
    if (formData.password !== formData.passwordConfirm) {
        showSignupMessage('비밀번호가 일치하지 않습니다.', 'error');
        return false;
    }

    // 비밀번호 강도 검사
    const passwordRegex = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{8,}$/;
    if (!passwordRegex.test(formData.password)) {
        showSignupMessage('비밀번호는 8자 이상, 영문, 숫자, 특수문자를 포함해야 합니다.', 'error');
        return false;
    }

    return true;
}

function showSignupMessage(message, type) {
    removeSignupMessage();

    const messageDiv = document.createElement('div');
    messageDiv.id = 'signup-message';
    messageDiv.className = `signup-message ${type}`;

    let icon = '';
    switch(type) {
        case 'success':
            icon = 'bi-check-circle-fill';
            break;
        case 'error':
            icon = 'bi-exclamation-circle-fill';
            break;
        default:
            icon = 'bi-info-circle-fill';
    }

    messageDiv.innerHTML = `
        <div class="signup-message-content">
            <i class="bi ${icon}"></i>
            <span>${message}</span>
        </div>
    `;

    const modalBody = document.querySelector('#signupModal .modal-body');
    modalBody.insertBefore(messageDiv, modalBody.firstChild);

    // 3초 후 자동 제거 (성공 메시지가 아닌 경우)
    if (type !== 'success') {
        setTimeout(() => {
            removeSignupMessage();
        }, 5000);
    }
}

function removeSignupMessage() {
    const existingMessage = document.getElementById('signup-message');
    if (existingMessage) {
        existingMessage.remove();
    }
}


// 타이머 관련 변수
let verificationTimer = null;
let timeRemaining = 600; // 10분 = 600초

function startVerificationTimer() {
    timeRemaining = 600; // 10분 초기화
    const timerElement = document.getElementById('verificationTimer');
    const timerText = document.getElementById('timerText');

    timerElement.style.display = 'flex';
    updateTimerDisplay();

    verificationTimer = setInterval(() => {
        timeRemaining--;

        if (timeRemaining <= 0) {
            // 시간 만료
            clearVerificationTimer();
            handleTimerExpired();
        } else {
            updateTimerDisplay();
        }
    }, 1000);
}
function updateTimerDisplay() {
    const minutes = Math.floor(timeRemaining / 60);
    const seconds = timeRemaining % 60;
    const timerText = document.getElementById('timerText');
    const timerElement = document.getElementById('verificationTimer');

    // 시간 형식 (MM:SS)
    const timeString = `${minutes}:${seconds.toString().padStart(2, '0')}`;
    timerText.textContent = timeString;

    // 시간에 따른 색상 변경
    timerElement.className = 'verification-timer';
    if (timeRemaining <= 60) { // 1분 이하
        timerElement.classList.add('danger');
    } else if (timeRemaining <= 180) { // 3분 이하
        timerElement.classList.add('warning');
    }
}

function clearVerificationTimer() {
    if (verificationTimer) {
        clearInterval(verificationTimer);
        verificationTimer = null;
    }

    const timerElement = document.getElementById('verificationTimer');
    if (timerElement) {
        timerElement.style.display = 'none';
    }
}

function handleTimerExpired() {
    // 인증번호 입력창과 버튼 비활성화
    const verificationCode = document.getElementById('verificationCode');
    const verifyBtn = document.getElementById('verifyBtn');

    verificationCode.disabled = true;
    verifyBtn.disabled = true;
    verifyBtn.innerHTML = '시간만료';

    showVerificationStatus('인증 시간이 만료되었습니다. 이메일 중복확인을 다시 진행해주세요.', 'error');

    // 이메일 입력창 다시 활성화
    resetEmailVerification();
}

// 이메일 인증 상태 변수
let emailVerified = false;
let emailLocked = false;

function showEmailStatus(message, type) {
    const statusDiv = document.getElementById('emailStatus');
    statusDiv.className = `verification-status ${type}`;
    statusDiv.textContent = message;
    statusDiv.style.display = 'block';
}

function checkEmailDuplicate() {
    const email = document.getElementById('signupEmail').value;
    const checkBtn = document.getElementById('emailCheckBtn');

    if (!email) {
        showEmailStatus('이메일을 입력해주세요.', 'error');
        return;
    }

    // 이메일 형식 검사
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
        showEmailStatus('올바른 이메일 형식을 입력해주세요.', 'error');
        return;
    }

    // 기존 메시지 숨기기
    document.getElementById('emailStatus').style.display = 'none';

    // 로딩 상태
    checkBtn.innerHTML = '확인중...';
    checkBtn.disabled = true;

    // 중복 확인 API 호출
    axios.get(`/api/email/duplicate?email=${encodeURIComponent(email)}`)
        .then(response => {
            const isDuplicate = !response.data.success.responseData;

            if (isDuplicate) {
                showEmailStatus('이미 사용중인 이메일입니다.', 'error');
                checkBtn.innerHTML = '중복확인';
                checkBtn.disabled = false;
            } else {
                sendVerificationEmail(email);
            }
        })
        .catch(error => {
            console.error('이메일 중복 확인 오류:', error);
            showEmailStatus('중복 확인 중 오류가 발생했습니다.', 'error');
            checkBtn.innerHTML = '중복확인';
            checkBtn.disabled = false;
        });
}

function sendVerificationEmail(email) {
    const checkBtn = document.getElementById('emailCheckBtn');

    checkBtn.innerHTML = '인증메일 발송중...';

    // 인증 메일 발송 API 호출
    axios.post(`/api/email/send?email=${encodeURIComponent(email)}`)
        .then(response => {
            // 이메일 입력창 잠금
            const emailInput = document.getElementById('signupEmail');
            emailInput.disabled = true;
            emailInput.classList.add('verified');
            emailLocked = true;

            // 버튼 상태 변경
            checkBtn.innerHTML = '발송완료';
            checkBtn.classList.add('verified');
            checkBtn.disabled = true;

            // 인증번호 입력 섹션 표시
            document.getElementById('verificationSection').style.display = 'block';

            // 타이머 시작
            startVerificationTimer();

            showVerificationStatus('인증번호가 발송되었습니다. 이메일을 확인해주세요.', 'success');

            // 인증번호 입력창에 포커스
            document.getElementById('verificationCode').focus();
        })
        .catch(error => {
            console.error('이메일 발송 오류:', error);
            showEmailStatus('이메일 발송 중 오류가 발생했습니다.', 'error');
            checkBtn.innerHTML = '중복확인';
            checkBtn.disabled = false;
        });
}

function verifyEmail() {
    const email = document.getElementById('signupEmail').value;
    const code = document.getElementById('verificationCode').value;
    const verifyBtn = document.getElementById('verifyBtn');

    if (!code || code.length !== 6) {
        showVerificationStatus('6자리 인증번호를 입력해주세요.', 'error');
        return;
    }

    // 시간 만료 체크
    if (timeRemaining <= 0) {
        showVerificationStatus('인증 시간이 만료되었습니다.', 'error');
        return;
    }

    // 로딩 상태
    verifyBtn.innerHTML = '확인중...';
    verifyBtn.disabled = true;

    // 인증번호 확인 API 호출
    axios.get(`/api/email/verify?email=${encodeURIComponent(email)}&code=${code}`)
        .then(response => {
            const isVerified = response.data.success.responseData;

            if (isVerified) {
                // 인증 성공
                emailVerified = true;
                clearVerificationTimer(); // 타이머 중지

                // UI 업데이트
                verifyBtn.innerHTML = '인증완료';
                verifyBtn.classList.add('verified');
                verifyBtn.disabled = true;

                document.getElementById('verificationCode').disabled = true;
                document.getElementById('verificationCode').classList.add('verified');

                showVerificationStatus('이메일 인증이 완료되었습니다!', 'success');
            } else {
                showVerificationStatus('인증번호가 올바르지 않습니다.', 'error');
                verifyBtn.innerHTML = '인증확인';
                verifyBtn.disabled = false;
            }
        })
        .catch(error => {
            console.error('이메일 인증 오류:', error);
            showVerificationStatus('인증 확인 중 오류가 발생했습니다.', 'error');
            verifyBtn.innerHTML = '인증확인';
            verifyBtn.disabled = false;
        });
}

function showVerificationStatus(message, type) {
    console.log(message);
    const statusDiv = document.getElementById('verificationStatus');
    statusDiv.className = `verification-status ${type}`;
    statusDiv.textContent = message;
    statusDiv.style.display = 'block';
}

function resetEmailVerification() {
    // 타이머 정리
    clearVerificationTimer();

    // 상태 초기화
    emailVerified = false;
    emailLocked = false;
    timeRemaining = 600;

    // UI 초기화
    const emailInput = document.getElementById('signupEmail');
    if (emailInput) {
        emailInput.disabled = false;
        emailInput.classList.remove('verified');
    }

    const checkBtn = document.getElementById('emailCheckBtn');
    if (checkBtn) {
        checkBtn.innerHTML = '중복확인';
        checkBtn.classList.remove('verified');
        checkBtn.disabled = false;
    }

    const verifyBtn = document.getElementById('verifyBtn');
    if (verifyBtn) {
        verifyBtn.innerHTML = '인증확인';
        verifyBtn.classList.remove('verified');
        verifyBtn.disabled = false;
    }

    const verificationCode = document.getElementById('verificationCode');
    if (verificationCode) {
        verificationCode.value = '';
        verificationCode.disabled = false;
        verificationCode.classList.remove('verified');
    }

    const verificationSection = document.getElementById('verificationSection');
    if (verificationSection) {
        verificationSection.style.display = 'none';
    }

    const emailStatus = document.getElementById('emailStatus');
    if (emailStatus) {
        emailStatus.style.display = 'none';
    }

    const verificationStatus = document.getElementById('verificationStatus');
    if (verificationStatus) {
        verificationStatus.style.display = 'none';
    }
}

// 페이지 언로드시 타이머 정리
window.addEventListener('beforeunload', function() {
    clearVerificationTimer();
});