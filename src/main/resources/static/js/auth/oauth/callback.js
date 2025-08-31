// URL 객체를 사용하여 파라미터 가져오기
const url = new URL(window.location.href);
const redirectUri = url.origin + url.pathname;
const code = url.searchParams.get('code');

const params = {
    authorizationCode: code
};

switch (provider) {
    case 'facebook':
    case  'microsoft':
    case 'google' :
        params.redirectUri = redirectUri;
        break;
    case 'naver':
        params.state = url.searchParams.get('state');
        break;
    case 'twitter':
        params.state = url.searchParams.get('state');
        params.redirectUri = redirectUri;
        break;
}




// axios를 사용하여 POST 요청 보내기}

(async function requestLoginOrRegister() {
    try{
        const response = await axios.post(`/api/oauth/${provider}`, params);
        console.log(response);
        const responseData = response.data.success.responseData;

        if (response.status === 200) {
            const isConnection = responseData.isConnection;
            // console.log('Login or Register successful:', data);
            //로그인 성공후 부모창의 함수호출
            if (window.opener && !window.opener.closed)
                await window.opener.handleLoginSuccess(isConnection, responseData, providerValue);
            self.close();
        } else if (response.status === 201) {
            console.log('User registered successfully:', responseData);
            if (window.opener && !window.opener.closed)
                await window.opener.handleSignUpRequest(responseData);
            self.close();
        } else {
            console.error('Login or Register failed:', response.statusText);
            // Handle error
        }
    }catch (error){
        console.error('Error during Login or Register:', error);
        if (window.opener && !window.opener.closed)
            await window.opener.handleLoginError(error);
        self.close();
    }

})();