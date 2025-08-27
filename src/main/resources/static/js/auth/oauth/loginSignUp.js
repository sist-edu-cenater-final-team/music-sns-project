
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