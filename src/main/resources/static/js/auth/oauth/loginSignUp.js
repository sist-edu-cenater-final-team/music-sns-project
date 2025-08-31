
function handleLoginSuccess(isConnection, responseData, providerValue ){
    localStorage.setItem('accessToken', responseData.accessToken);
    localStorage.setItem('tokenType', responseData.tokenType);
    if(isConnection){
        // 성공시 타이머 정리
        clearLockoutTimer();
        showMessage("계정에 "+providerValue + ' 소셜 로그인이 연결 되었습니다.<br>잠시후 메인 페이지로 이동합니다.', 'success');
        setTimeout(() => {
            window.location.href = '/';
        }, 2000);

    } else {
        // 성공시 타이머 정리
        clearLockoutTimer();
        showMessage(providerValue + ' 로그인에 성공했습니다.', 'success');
        setTimeout(() => {
            window.location.href = '/';
        }, 1000);
    }
}
function replaceWithSocialForm(responseData){
    document.getElementById("passwordDiv").style.display = "none";
    document.getElementById("signupModalLabel").innerText =
        responseData.provider + " 계정으로 회원가입";
    document.getElementById("signupEmail").value = responseData.email;
    document.getElementById("signupNickname").value = responseData.nickname;


}

async function handleSignUpRequest(responseData){
    console.log(responseData);
    await replaceWithSocialForm(responseData);
    openSignupModal();
    window.socialSignUpData = responseData;

}
function handleLoginError(error){
    console.error('Login error:', error);
    if (error.response) {
        const status = error.response.status;
        if (status === 401) {
            showMessage('인증에 실패했습니다. 다시 시도해주세요.', 'error');
        } else if (status === 403) {
            showMessage('접근이 금지되었습니다. 관리자에게 문의하세요.', 'error');
        } else if (status === 429) {
            const retryAfter = error.response.headers['retry-after'];
            const waitTime = retryAfter ? parseInt(retryAfter, 10) : 60; // 기본 대기 시간 60초
            startLockoutTimer(waitTime);
            showMessage(`너무 많은 시도입니다. ${waitTime}초 후에 다시 시도해주세요.`, 'error');
        } else {
            showMessage(`로그인 중 오류가 발생했습니다<br> ${error.response.data.error.customMessage || '알 수 없는 오류'}`, 'error');
        }
    } else {
        showMessage('서버와의 연결에 실패했습니다. 네트워크 상태를 확인하세요.', 'error');
    }
}