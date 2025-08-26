
// URL 객체를 사용하여 파라미터 가져오기
const url = new URL(window.location.href);
const code = url.searchParams.get('code');
const params = { authorizationCode: code };

(async function requestLoginOrRegister() {//템플릿 리터럴 사용을위해 \역슬래시 한번
    const response = await axios.post(`/api/oauth/${provider}`, params);
    console.log(response.headers['x-is-connection']);
    console.log(response);
    const responseData = response.data.success.responseData;

    if (response.status === 200) {
        const isConnection = response.headers['x-is-connection'] === 'true';
        // console.log('Login or Register successful:', data);
        //로그인 성공후 부모창의 함수호출
        if (window.opener && !window.opener.closed)
            window.opener.handleLoginSuccess(isConnection, responseData, providerValue);
        self.close();
    } else if (response.status === 201) {
        // console.log('User registered successfully:', data);
        if (window.opener && !window.opener.closed)
            window.opener.handleSignUpRequest(responseData);
        // self.close();
    } else {
        console.error('Login or Register failed:', response.statusText);
        // Handle error
    }
})();