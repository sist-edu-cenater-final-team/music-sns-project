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
        const modal = bootstrap.Modal.getOrCreateInstance(signupModal);
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
        email: document.getElementById('email').value,
        nickname: document.getElementById('nickname').value,
        username: document.getElementById('username').value,
        phoneNumber: document.getElementById('phoneNumber').value,
        dateOfBirth: document.getElementById('dateOfBirth').value,
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
    // 필수 필드 검사
    for (const [key, value] of Object.entries(formData)) {
        if (!value || value.trim() === '') {
            showSignupMessage('모든 필수 항목을 입력해주세요.', 'error');
            return false;
        }
    }

    // 이메일 형식 검사
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(formData.email)) {
        showSignupMessage('올바른 이메일 형식을 입력해주세요.', 'error');
        return false;
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