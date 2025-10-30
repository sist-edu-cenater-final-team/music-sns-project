let forgotPasswordTimer;
let verificationToken = null;

function openForgotPasswordModal(event) {
    event.preventDefault();
    const modal = new bootstrap.Modal(document.getElementById('forgotPasswordModal'));
    modal.show();
    resetForgotPasswordModal();
}

function resetForgotPasswordModal() {
    document.getElementById('step1').style.display = 'block';
    document.getElementById('step2').style.display = 'none';
    document.getElementById('step3').style.display = 'none';
    document.getElementById('forgotIdentifier').value = '';
    document.getElementById('verificationCode').value = '';
    document.getElementById('newPassword').value = '';
    document.getElementById('confirmPassword').value = '';
}

function sendVerificationCode() {
    const identifier = document.getElementById('forgotIdentifier').value.trim();

    if (!identifier) {
        Swal.fire('입력 오류', '이메일 또는 전화번호를 입력해주세요.', 'warning');
        return;
    }

    axios.post(ctxPath + '/api/auth/forgot-password/send-code', {},{
        params: { identifier: identifier }
    })
        .then(response => {
            console.log(response);
            verificationToken = response.data.data.token;
            showStep(2);
            startTimer(600); // 10분 타이머
            Swal.fire('성공', '인증 코드가 전송되었습니다.', 'success');
        })
        .catch(error => {
            console.log(error.response.data.error);
            Swal.fire('오류', error.response?.data?.error?.customMessage || '인증 코드 전송 실패', 'error');
        });
}

function verifyCode() {
    const verificationCode = document.getElementById('verificationCode').value.trim();

    if (!verificationCode) {
        Swal.fire('입력 오류', '인증 코드를 입력해주세요.', 'warning');
        return;
    }

    axios.post(ctxPath + '/api/auth/forgot-password/verify-code', {
        token: verificationToken,
        code: verificationCode
    })
        .then(response => {
            verificationToken = response.data.data.token;
            showStep(3);
            clearInterval(forgotPasswordTimer);
            Swal.fire('성공', '인증이 완료되었습니다.', 'success');
        })
        .catch(error => {
            Swal.fire('오류', error.response?.data?.message || '인증 실패', 'error');
        });
}

function changePassword() {
    const newPassword = document.getElementById('newPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;

    if (!newPassword || !confirmPassword) {
        Swal.fire('입력 오류', '비밀번호를 입력해주세요.', 'warning');
        return;
    }

    if (newPassword !== confirmPassword) {
        Swal.fire('오류', '비밀번호가 일치하지 않습니다.', 'error');
        return;
    }

    if (newPassword.length < 8) {
        Swal.fire('오류', '비밀번호는 최소 8자 이상이어야 합니다.', 'error');
        return;
    }

    axios.post(ctxPath + '/api/auth/forgot-password/change', {
        token: verificationToken,
        newPassword: newPassword
    })
        .then(response => {
            Swal.fire('성공', '비밀번호가 변경되었습니다.', 'success').then(() => {
                bootstrap.Modal.getInstance(document.getElementById('forgotPasswordModal')).hide();
                resetForgotPasswordModal();
            });
        })
        .catch(error => {
            Swal.fire('오류', error.response?.data?.message || '비밀번호 변경 실패', 'error');
        });
}

function showStep(stepNumber) {
    document.getElementById('step1').style.display = stepNumber === 1 ? 'block' : 'none';
    document.getElementById('step2').style.display = stepNumber === 2 ? 'block' : 'none';
    document.getElementById('step3').style.display = stepNumber === 3 ? 'block' : 'none';
}

function goBackToStep1() {
    clearInterval(forgotPasswordTimer);
    showStep(1);
    document.getElementById('verificationCode').value = '';
}

function startTimer(seconds) {
    let remainingSeconds = seconds;
    updateTimerDisplay(remainingSeconds);

    forgotPasswordTimer = setInterval(() => {
        remainingSeconds--;
        updateTimerDisplay(remainingSeconds);

        if (remainingSeconds <= 0) {
            clearInterval(forgotPasswordTimer);
            Swal.fire('시간 초과', '인증 코드의 유효 시간이 만료되었습니다.', 'warning');
            goBackToStep1();
        }
    }, 1000);
}

function updateTimerDisplay(seconds) {
    const minutes = Math.floor(seconds / 60);
    const secs = seconds % 60;
    document.getElementById('timerDisplay').textContent =
        `${minutes}:${secs.toString().padStart(2, '0')}`;
}

function toggleForgotPassword(inputId) {
    const input = document.getElementById(inputId);
    const icon = event.target.closest('.password-toggle').querySelector('i');

    if (input.type === 'password') {
        input.type = 'text';
        icon.classList.remove('bi-eye');
        icon.classList.add('bi-eye-slash');
    } else {
        input.type = 'password';
        icon.classList.remove('bi-eye-slash');
        icon.classList.add('bi-eye');
    }
}
