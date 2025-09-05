let isFormSubmitted = false; // 회원가입 완료 여부 확인용
// 회원가입 모달 관련 함수들
document.addEventListener('DOMContentLoaded', function () {
    const signupModal = document.getElementById('signupModal');

    // 회원가입 링크 클릭 시 모달 열기
    const signupLinks = document.querySelector('#signUpBtn');

    signupLinks.addEventListener('click', function (e) {
        document.getElementById("passwordDiv").style.display = "flex";
        document.getElementById("signupModalLabel").innerText = "회원가입";

        openSignupModal();
    });

    // 회원가입 폼 제출 이벤트
    const signupForm = document.getElementById('signupForm');
    if (signupForm) {
        signupForm.addEventListener('submit', function (e) {
            e.preventDefault();
            handleSignup();
        });
    }


    // 전화번호 자동 포맷팅
    const phoneInput = document.getElementById('signupPhone');
    if (phoneInput) {
        phoneInput.addEventListener('input', function (e) {
            formatPhoneNumber(e.target);
        });

        // 붙여넣기 시에도 포맷팅 적용
        phoneInput.addEventListener('paste', function (e) {
            setTimeout(() => {
                formatPhoneNumber(e.target);
            }, 10);
        });
    }
    // 비밀번호 실시간 검증
    const passwordInput = document.getElementById('signupPassword');
    if (passwordInput) {
        passwordInput.addEventListener('input', validatePassword);
    }

// 비밀번호 확인 실시간 검증
    const passwordConfirm = document.getElementById('passwordConfirm');
    if (passwordConfirm) {
        passwordConfirm.addEventListener('input', validatePasswordMatch);
    }

    // 모달 닫기 전 확인
    signupModal.addEventListener('hide.bs.modal', function (e) {
        // 회원가입이 완료된 경우나 폼이 비어있는 경우는 확인하지 않음
        if (isFormSubmitted || isFormEmpty()) {
            return;
        }

        // 모달 닫기 중단
        e.preventDefault();

        // SweetAlert2로 확인
        Swal.fire({
            title: '가입을 취소하시겠습니까?',
            text: "입력하신 정보가 모두 사라집니다.",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#6633ff',
            cancelButtonColor: '#d33',
            confirmButtonText: '확인',
            cancelButtonText: '취소',
            customClass: {
                popup: 'swal-signup-confirm'
            }
        }).then((result) => {
            if (result.isConfirmed) {
                // 확인 버튼을 눌렀을 때 모달 닫기
                isFormSubmitted = true; // 다시 확인하지 않도록
                const modal = bootstrap.Modal.getInstance(signupModal);
                modal.hide();

                // 폼 초기화
                setTimeout(() => {
                    resetSignupForm();
                    isFormSubmitted = false;
                }, 300);
            }
        });
    });
    // ESC 키 처리
    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape' && signupModal.classList.contains('show')) {
            e.preventDefault();
            e.stopPropagation();

            if (!isFormSubmitted && !isFormEmpty()) {
                signupModal.dispatchEvent(new Event('hide.bs.modal'));
            }
        }
    });
});

// 폼이 비어있는지 확인하는 함수
function isFormEmpty() {
    const inputs = document.querySelectorAll('#signupForm input[type="text"], #signupForm input[type="tel"], #signupForm input[type="password"]');
    for (let input of inputs) {
        if (input.value.trim() !== '') {
            return false;
        }
    }
    return true;
}

function openSignupModal() {

    // Bootstrap 5 방식으로 모달 초기화
    const signupModal = document.getElementById('signupModal');
    if (signupModal) {
        // 이메일 인증 상태 초기화
        resetEmailVerification();
        // 전화번호 인증 초기화
        resetPhoneVerification();
        // 닉네임 인증 초기화
        resetNicknameVerification();
        const modal = bootstrap.Modal.getOrCreateInstance(signupModal);

        // 모달이 완전히 열린 후 포커스 관리
        signupModal.addEventListener('shown.bs.modal', function () {
            // 첫 번째 입력 필드에 포커스
            const firstInput = signupModal.querySelector('input');
            if (firstInput) {
                firstInput.focus();
            }
        }, {once: true});

        // 모달이 숨겨지기 전 포커스 해제
        signupModal.addEventListener('hide.bs.modal', function () {
            // 현재 포커스된 요소가 모달 내부에 있으면 포커스 해제
            const activeElement = document.activeElement;
            if (signupModal.contains(activeElement)) {
                activeElement.blur();
            }
        }, {once: true});

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

    removeSignupMessage('passwordConfirm');

    if (passwordConfirm && password !== passwordConfirm) {
        // 입력창 빨간색으로 변경
        confirmInput.style.borderColor = '#f56565';
        confirmInput.style.background = 'rgba(245, 101, 101, 0.05)';

        // 메시지 표시
        showPasswordMessage('passwordConfirm', '비밀번호와 비밀번호 확인이 다릅니다.', 'error');
    } else if (passwordConfirm && password === passwordConfirm) {
        // 일치할 때 정상 스타일
        confirmInput.style.borderColor = '#48bb78';
        confirmInput.style.background = 'rgba(72, 187, 120, 0.05)';
    } else {
        // 입력값이 없을 때 기본 스타일
        confirmInput.style.borderColor = '#e1e5e9';
        confirmInput.style.background = '#f8f9fa';
    }
}

function createSignUpFormData() {
    return window.socialSignUpData ? {
        email: document.getElementById('signupEmail').value,
        nickname: document.getElementById('signupNickname').value,
        username: document.getElementById('signupName').value,
        phoneNumber: getPhoneNumberOnly(document.getElementById('signupPhone').value), // 하이픈 제거
        socialId: window.socialSignUpData.socialId,
        provider: window.socialSignUpData.provider
    } : {
        email: document.getElementById('signupEmail').value,
        nickname: document.getElementById('signupNickname').value,
        username: document.getElementById('signupName').value,
        phoneNumber: getPhoneNumberOnly(document.getElementById('signupPhone').value), // 하이픈 제거
        // dateOfBirth: document.getElementById('dateOfBirth').value,
        password: document.getElementById('signupPassword').value,
        passwordConfirm: document.getElementById('passwordConfirm').value
    }
}

function handleSignup() {
    // 기존 메시지 제거
    removeSignupMessage();

    // 폼 데이터 수집
    const formData = createSignUpFormData();
    const endPoint = ctxPath +
        window.socialSignUpData ? '/api/oauth/sign-up' : '/api/auth/sign-up';

    // 유효성 검사
    if (!validateSignupForm(formData)) return;


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
    axios.post(endPoint, formData)
        .then(response => {
            isFormSubmitted = true; // 성공 시 설정
            console.log(response)
            showSignupMessage(response.data.success.message, 'success');

            // 3초 후 모달 닫기 및 로그인 폼으로 이동
            setTimeout(() => {
                const signupModal = bootstrap.Modal.getInstance(document.getElementById('signupModal'));
                // 회원가입 폼 초기화
                resetSignupForm();
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
                isFormSubmitted = false; // 초기화
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

// 회원가입 폼 초기화 함수 추가
function resetSignupForm() {
    // 폼 입력값 초기화
    const form = document.getElementById('signupForm');
    if (form) {
        form.reset();
    }

    // 이메일 및 전화번호 인증 상태 초기화
    resetNicknameVerification();
    resetEmailVerification();
    resetPhoneVerification();

    // 약관 동의 체크박스 초기화
    const agreeTerms = document.getElementById('agreeTerms');
    if (agreeTerms) {
        agreeTerms.checked = false;
    }
    //비밀번호 스타일 초기화
    const passwordInput = document.getElementById('signupPassword');
    if (passwordInput) {
        passwordInput.style.borderColor = '';
        passwordInput.style.background = '';
    }

    // 비밀번호 확인 입력창 스타일 초기화
    const passwordConfirm = document.getElementById('passwordConfirm');
    if (passwordConfirm) {
        passwordConfirm.style.borderColor = '';
        passwordConfirm.style.background = '';
    }

    // 메시지 제거
    removeSignupMessage();
    window.socialSignUpData = null;
}

function validateSignupForm(formData) {
    // 닉네임 인증 확인
    if (!nicknameVerified) {
        showSignupMessage('닉네임 중복확인을 완료해주세요.', 'error');
        return false;
    }
    // 이메일 인증 확인
    if (!emailVerified) {
        showSignupMessage('이메일 인증을 완료해주세요.', 'error');
        return false;
    }
    // 전화번호 인증 확인
    if (!phoneVerified) {
        showSignupMessage('전화번호 인증을 완료해주세요.', 'error');
        return false;
    }
    // 필수 필드 검사 (이메일 전화번호 제외 - 이미 인증됨) 소셜가입시 비밀번호제외
    const requiredFields = formData.socialId ?
        ['username'] : ['username', 'password', 'passwordConfirm'];
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
    if (formData.socialId && formData.password !== formData.passwordConfirm) {
        showSignupMessage('비밀번호가 일치하지 않습니다.', 'error');
        return false;
    }

    // 비밀번호 강도 검사
    const passwordRegex = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{8,}$/;
    if (!formData.socialId && !passwordRegex.test(formData.password)) {
        showSignupMessage('비밀번호는 8자 이상, 영문, 숫자, 특수문자를 포함해야 합니다.', 'error');
        return false;
    }

    return true;
}


// 메시지 타이머 관리 변수 추가
let signupMessageTimer = null;

function showSignupMessage(message, type) {
    removeSignupMessage();

    const messageDiv = document.createElement('div');
    messageDiv.id = 'signup-message';
    messageDiv.className = `signup-message ${type}`;

    let icon = '';
    switch (type) {
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
    // 이전 타이머 정리
    if (signupMessageTimer) {
        clearTimeout(signupMessageTimer);
        signupMessageTimer = null;
    }
    // 5초 후 자동 제거 (성공 메시지가 아닌 경우)
    if (type !== 'success') {
        signupMessageTimer = setTimeout(() => {
            removeSignupMessage();
            signupMessageTimer = null;
        }, 5000);
    }
}

function removeSignupMessage() {
    // 타이머 정리
    if (signupMessageTimer) {
        clearTimeout(signupMessageTimer);
        signupMessageTimer = null;
    }
    // 기존 메시지 제거
    removePasswordMessage('signupPassword');
    removePasswordMessage('passwordConfirm');

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

    if(window.socialSignUpData && window.socialSignUpData.email === email){
        sendVerificationEmail(email);
        return;
    }


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
window.addEventListener('beforeunload', function () {
    clearVerificationTimer();
});
// 전화번호 인증 상태 변수
let phoneVerified = false;
let phoneLocked = false;
let phoneVerificationTimer = null;
let phoneTimeRemaining = 600; // 10분

function showPhoneStatus(message, type) {
    const statusDiv = document.getElementById('phoneStatus');
    statusDiv.className = `verification-status ${type}`;
    statusDiv.textContent = message;
    statusDiv.style.display = 'block';
}

function checkPhoneDuplicate() {
    const phone = document.getElementById('signupPhone').value;
    const checkBtn = document.getElementById('phoneCheckBtn');

    if (!phone) {
        showPhoneStatus('전화번호를 입력해주세요.', 'error');
        return;
    }
    const phoneOnly = getPhoneNumberOnly(phone); // 하이픈 제거

    // 전화번호 형식 검사
    const phoneRegex = /^010\d{8}$/;
    if (!phoneRegex.test(phoneOnly)) {
        showPhoneStatus('010으로 시작하는 11자리 숫자를 입력해주세요.', 'error');
        return;
    }

    // 기존 메시지 숨기기
    document.getElementById('phoneStatus').style.display = 'none';

    // 로딩 상태
    checkBtn.innerHTML = '확인중...';
    checkBtn.disabled = true;

    // 중복 확인 API 호출
    axios.get(`/api/phone/duplicate?phoneNumber=${encodeURIComponent(phoneOnly)}`)
        .then(response => {
            const isDuplicate = !response.data.success.responseData;

            if (isDuplicate) {
                showPhoneStatus('이미 사용중인 전화번호입니다.', 'error');
                checkBtn.innerHTML = '중복확인';
                checkBtn.disabled = false;
            } else {
                sendVerificationSMS(phoneOnly);
            }
        })
        .catch(error => {
            console.error('전화번호 중복 확인 오류:', error);
            showPhoneStatus('중복 확인 중 오류가 발생했습니다.', 'error');
            checkBtn.innerHTML = '중복확인';
            checkBtn.disabled = false;
        });
}

function sendVerificationSMS(phone) {
    const checkBtn = document.getElementById('phoneCheckBtn');

    checkBtn.innerHTML = '인증문자 발송중...';

    // 인증 문자 발송 API 호출
    axios.post(`/api/phone/send?phoneNumber=${encodeURIComponent(phone)}`)
        .then(response => {
            // 전화번호 입력창 잠금
            const phoneInput = document.getElementById('signupPhone');
            phoneInput.disabled = true;
            phoneInput.classList.add('verified');
            phoneLocked = true;

            // 버튼 상태 변경
            checkBtn.innerHTML = '발송완료';
            checkBtn.classList.add('verified');
            checkBtn.disabled = true;

            // 인증번호 입력 섹션 표시
            document.getElementById('phoneVerificationSection').style.display = 'block';

            // 타이머 시작
            startPhoneVerificationTimer();

            showPhoneVerificationStatus('인증번호가 발송되었습니다. 문자메시지를 확인해주세요.', 'success');

            // 인증번호 입력창에 포커스
            document.getElementById('phoneVerificationCode').focus();
        })
        .catch(error => {
            console.error('문자 발송 오류:', error);
            const message = error.response.data.error.customMessage;
            showPhoneStatus(message || '문자 발송 중 오류가 발생했습니다.', 'error');
            checkBtn.innerHTML = '중복확인';
            checkBtn.disabled = false;
        });
}

function verifyPhone() {
    const phone = document.getElementById('signupPhone').value;
    const phoneOnly = getPhoneNumberOnly(phone); // 하이픈 제거
    const code = document.getElementById('phoneVerificationCode').value;
    const verifyBtn = document.getElementById('phoneVerifyBtn');

    if (!code || code.length !== 6) {
        showPhoneVerificationStatus('6자리 인증번호를 입력해주세요.', 'error');
        return;
    }

    // 시간 만료 체크
    if (phoneTimeRemaining <= 0) {
        showPhoneVerificationStatus('인증 시간이 만료되었습니다.', 'error');
        return;
    }

    // 로딩 상태
    verifyBtn.innerHTML = '확인중...';
    verifyBtn.disabled = true;

    // 인증번호 확인 API 호출
    axios.get(`/api/phone/verify?phoneNumber=${encodeURIComponent(phoneOnly)}&code=${code}`)
        .then(response => {
            const isVerified = response.data.success.responseData;

            if (isVerified) {
                // 인증 성공
                phoneVerified = true;
                clearPhoneVerificationTimer(); // 타이머 중지

                // UI 업데이트
                verifyBtn.innerHTML = '인증완료';
                verifyBtn.classList.add('verified');
                verifyBtn.disabled = true;

                document.getElementById('phoneVerificationCode').disabled = true;
                document.getElementById('phoneVerificationCode').classList.add('verified');

                showPhoneVerificationStatus('전화번호 인증이 완료되었습니다!', 'success');
            } else {
                showPhoneVerificationStatus('인증번호가 올바르지 않습니다.', 'error');
                verifyBtn.innerHTML = '인증확인';
                verifyBtn.disabled = false;
            }
        })
        .catch(error => {
            console.error('전화번호 인증 오류:', error);
            showPhoneVerificationStatus('인증 확인 중 오류가 발생했습니다.', 'error');
            verifyBtn.innerHTML = '인증확인';
            verifyBtn.disabled = false;
        });
}

function showPhoneVerificationStatus(message, type) {
    const statusDiv = document.getElementById('phoneVerificationStatus');
    statusDiv.className = `verification-status ${type}`;
    statusDiv.textContent = message;
    statusDiv.style.display = 'block';
}

function startPhoneVerificationTimer() {
    phoneTimeRemaining = 600; // 10분 초기화
    const timerElement = document.getElementById('phoneVerificationTimer');
    const timerText = document.getElementById('phoneTimerText');

    timerElement.style.display = 'flex';
    updatePhoneTimerDisplay();

    phoneVerificationTimer = setInterval(() => {
        phoneTimeRemaining--;

        if (phoneTimeRemaining <= 0) {
            // 시간 만료
            clearPhoneVerificationTimer();
            handlePhoneTimerExpired();
        } else {
            updatePhoneTimerDisplay();
        }
    }, 1000);
}

function updatePhoneTimerDisplay() {
    const minutes = Math.floor(phoneTimeRemaining / 60);
    const seconds = phoneTimeRemaining % 60;
    const timerText = document.getElementById('phoneTimerText');
    const timerElement = document.getElementById('phoneVerificationTimer');

    // 시간 형식 (MM:SS)
    const timeString = `${minutes}:${seconds.toString().padStart(2, '0')}`;
    timerText.textContent = timeString;

    // 시간에 따른 색상 변경
    timerElement.className = 'verification-timer';
    if (phoneTimeRemaining <= 60) { // 1분 이하
        timerElement.classList.add('danger');
    } else if (phoneTimeRemaining <= 180) { // 3분 이하
        timerElement.classList.add('warning');
    }
}

function clearPhoneVerificationTimer() {
    if (phoneVerificationTimer) {
        clearInterval(phoneVerificationTimer);
        phoneVerificationTimer = null;
    }

    const timerElement = document.getElementById('phoneVerificationTimer');
    if (timerElement) {
        timerElement.style.display = 'none';
    }
}

function handlePhoneTimerExpired() {
    // 인증번호 입력창과 버튼 비활성화
    const verificationCode = document.getElementById('phoneVerificationCode');
    const verifyBtn = document.getElementById('phoneVerifyBtn');

    verificationCode.disabled = true;
    verifyBtn.disabled = true;
    verifyBtn.innerHTML = '시간만료';

    showPhoneVerificationStatus('인증 시간이 만료되었습니다. 전화번호 중복확인을 다시 진행해주세요.', 'error');

    // 전화번호 입력창 다시 활성화
    resetPhoneVerification();
}

function resetPhoneVerification() {
    // 타이머 정리
    clearPhoneVerificationTimer();

    // 상태 초기화
    phoneVerified = false;
    phoneLocked = false;
    phoneTimeRemaining = 600;

    // UI 초기화
    const phoneInput = document.getElementById('signupPhone');
    if (phoneInput) {
        phoneInput.disabled = false;
        phoneInput.classList.remove('verified');
    }

    const checkBtn = document.getElementById('phoneCheckBtn');
    if (checkBtn) {
        checkBtn.innerHTML = '중복확인';
        checkBtn.classList.remove('verified');
        checkBtn.disabled = false;
    }

    const verifyBtn = document.getElementById('phoneVerifyBtn');
    if (verifyBtn) {
        verifyBtn.innerHTML = '인증확인';
        verifyBtn.classList.remove('verified');
        verifyBtn.disabled = false;
    }

    const verificationCode = document.getElementById('phoneVerificationCode');
    if (verificationCode) {
        verificationCode.value = '';
        verificationCode.disabled = false;
        verificationCode.classList.remove('verified');
    }

    const verificationSection = document.getElementById('phoneVerificationSection');
    if (verificationSection) {
        verificationSection.style.display = 'none';
    }

    const phoneStatus = document.getElementById('phoneStatus');
    if (phoneStatus) {
        phoneStatus.style.display = 'none';
    }

    const verificationStatus = document.getElementById('phoneVerificationStatus');
    if (verificationStatus) {
        verificationStatus.style.display = 'none';
    }
}


// 전화번호 자동 포맷팅 함수
function formatPhoneNumber(input) {
    // 숫자만 추출
    let value = input.value.replace(/[^0-9]/g, '');

    // 길이 제한 (11자리까지)
    if (value.length > 11) {
        value = value.slice(0, 11);
    }

    // 포맷팅 적용
    let formattedValue = '';
    if (value.length <= 3) {
        formattedValue = value;
    } else if (value.length <= 7) {
        formattedValue = value.slice(0, 3) + '-' + value.slice(3);
    } else {
        formattedValue = value.slice(0, 3) + '-' + value.slice(3, 7) + '-' + value.slice(7);
    }

    input.value = formattedValue;
}

// 전화번호에서 하이픈 제거한 순수 숫자만 반환
function getPhoneNumberOnly(phoneNumber) {
    return phoneNumber.replace(/[^0-9]/g, '');
}

// 닉네임 인증 상태 변수
let nicknameVerified = false;
let nicknameLocked = false;

function showNicknameStatus(message, type) {
    const statusDiv = document.getElementById('nicknameStatus');
    statusDiv.className = `verification-status ${type}`;
    statusDiv.textContent = message;
    statusDiv.style.display = 'block';
}

function checkNicknameDuplicate() {
    const nickname = document.getElementById('signupNickname').value;
    const checkBtn = document.getElementById('nicknameCheckBtn');

    if (!nickname) {
        showNicknameStatus('닉네임을 입력해주세요.', 'error');
        return;
    }

    // 닉네임 길이 검사 (2-20자)
    if (nickname.length < 2 || nickname.length > 20) {
        showNicknameStatus('닉네임은 2-20자로 입력해주세요.', 'error');
        return;
    }

    // 기존 메시지 숨기기
    document.getElementById('nicknameStatus').style.display = 'none';

    // 로딩 상태
    checkBtn.innerHTML = '확인중...';
    checkBtn.disabled = true;

    // 중복 확인 API 호출
    axios.get(`/api/account/nickname/duplicate?nickname=${encodeURIComponent(nickname)}`)
        .then(response => {
            const isAvailable = response.data.success.responseData;

            if (isAvailable) {
                // 닉네임 사용 가능
                nicknameVerified = true;
                nicknameLocked = true;

                // UI 업데이트
                const nicknameInput = document.getElementById('signupNickname');
                nicknameInput.disabled = true;
                nicknameInput.classList.add('verified');

                checkBtn.innerHTML = '확인완료';
                checkBtn.classList.add('verified');
                checkBtn.disabled = true;

                showNicknameStatus('사용 가능한 닉네임입니다.', 'success');
            } else {
                // 닉네임 중복
                showNicknameStatus('이미 사용중인 닉네임입니다.', 'error');
                checkBtn.innerHTML = '중복확인';
                checkBtn.disabled = false;
            }
        })
        .catch(error => {
            console.error('닉네임 중복 확인 오류:', error);
            showNicknameStatus('중복 확인 중 오류가 발생했습니다.', 'error');
            checkBtn.innerHTML = '중복확인';
            checkBtn.disabled = false;
        });
}

function resetNicknameVerification() {
    // 상태 초기화
    nicknameVerified = false;
    nicknameLocked = false;

    // UI 초기화
    const nicknameInput = document.getElementById('signupNickname');
    if (nicknameInput) {
        nicknameInput.disabled = false;
        nicknameInput.classList.remove('verified');
    }

    const checkBtn = document.getElementById('nicknameCheckBtn');
    if (checkBtn) {
        checkBtn.innerHTML = '중복확인';
        checkBtn.classList.remove('verified');
        checkBtn.disabled = false;
    }

    const nicknameStatus = document.getElementById('nicknameStatus');
    if (nicknameStatus) {
        nicknameStatus.style.display = 'none';
    }
}

//비밀번호 실시간 검증
// 비밀번호 검증 함수 추가
function validatePassword() {
    const password = document.getElementById('signupPassword').value;
    const passwordInput = document.getElementById('signupPassword');

    // 기존 메시지 제거
    removePasswordMessage('signupPassword');

    // 비밀번호 조건 검사
    const passwordRegex = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{8,20}$/;

    if (password && !passwordRegex.test(password)) {
        // 입력창 빨간색으로 변경
        passwordInput.style.borderColor = '#f56565';
        passwordInput.style.background = 'rgba(245, 101, 101, 0.05)';

        // 메시지 표시
        showPasswordMessage('signupPassword', '8자 이상 20자 이하 영문, 숫자, 특수문자 조합이어야 합니다.', 'error');
    } else if (password) {
        // 조건 충족 시 정상 스타일
        passwordInput.style.borderColor = '#48bb78';
        passwordInput.style.background = 'rgba(72, 187, 120, 0.05)';
        // 메시지 제거는 이미 위에서 호출됨
    } else {
        // 입력값이 없을 때 기본 스타일
        passwordInput.style.borderColor = '#e1e5e9';
        passwordInput.style.background = '#f8f9fa';
    }

    // 비밀번호 확인 필드도 재검증
    if (document.getElementById('passwordConfirm').value) {
        validatePasswordMatch();
    }
}

function removePasswordMessage(inputId) {
    const inputWrapper = document.getElementById(inputId).parentElement;
    const existingMessage = inputWrapper.parentElement.querySelector('.password-message');

    if (existingMessage) {
        existingMessage.remove();
    }
}

function showPasswordMessage(inputId, message, type) {
    const inputWrapper = document.getElementById(inputId).parentElement;
    const existingMessage = inputWrapper.parentElement.querySelector('.password-message');

    // 기존 메시지 제거
    if (existingMessage) {
        existingMessage.remove();
    }

    const messageDiv = document.createElement('div');
    messageDiv.className = `password-message ${type}`;
    messageDiv.innerHTML = `
        <span>${message}</span>
    `;

    inputWrapper.parentElement.appendChild(messageDiv);
}