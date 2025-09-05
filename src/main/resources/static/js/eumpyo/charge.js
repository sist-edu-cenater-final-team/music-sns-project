(function () {
    const contextPath =
        window.ctxPath ||
        document.querySelector('meta[name="ctxPath"]')?.content ||
        '';

    // 미로그인시 로그인 화면으로 보내기
    try {
        if (!localStorage.getItem('accessToken')) {
            alert('로그인이 필요합니다.');
            location.href = `${contextPath}/auth/login`;
            return;
        }
    } catch (e) {}

    // 인증 헤더 만들기
    function getAuthHeader() {
        const fromApp = window.AuthFunc?.getAuthHeader?.();
        if (fromApp && Object.keys(fromApp).length) return fromApp;

        const accessToken = localStorage.getItem('accessToken');
        const tokenType = localStorage.getItem('tokenType') || 'Bearer';
        return accessToken ? { Authorization: `${tokenType} ${accessToken}` } : {};
    }

    // POST 요청 보내기
    function ajaxPost(url, body) {
        return $.ajax({
            url,
            type: 'POST',
            data: JSON.stringify(body || {}),
            headers: Object.assign({}, getAuthHeader(), { 'Content-Type': 'application/json' }),
            contentType: 'application/json',
            dataType: 'json'
        });
    }

    // 토큰 만료시, 한 번만 새 토큰 받고, 같은 요청 다시 시도
    function withAuthRetry(run) {
        var dfd = $.Deferred();
        var retried = false;

        function attempt() {
            run()
                .done(function (data) {
                    dfd.resolve(data); 
                })
                .fail(function (xhr, textStatus, errorThrown) {
                    if (xhr && xhr.status === 401 && !retried) {
                        retried = true;
                        var refreshFn = AuthFunc.refreshAuthToken;
                        if (typeof refreshFn === 'function') {
                            $.when(refreshFn())
                                .done(function () {
                                    attempt(); 
                                })
                                .fail(function () {
                                    // 새 토큰 받기 실패 → 저장된 토큰 지우고 종료
                                    try {
                                        localStorage.removeItem('accessToken');
                                        localStorage.removeItem('tokenType');
                                    } catch (e) {}
                                    dfd.reject(xhr, textStatus, errorThrown);
                                });
                        } else {
                            try {
                                localStorage.removeItem('accessToken');
                                localStorage.removeItem('tokenType');
                            } catch (e) {}
                            dfd.reject(xhr, textStatus, errorThrown);
                        }
                        return;
                    }
                    dfd.reject(xhr, textStatus, errorThrown);
                });
        }

        attempt();
        return dfd.promise();
    }

    const unwrapData = (response) => {
        const data = response?.data ?? response;
        return data?.success?.responseData ?? data?.responseData ?? data;
    };

    // 메타 태그 값 읽기
    const meta = (name) =>
        document.querySelector(`meta[name="${name}"]`)?.content?.trim() || '';

    // 결제자 정보 만들기
    function getBuyerInfo() {
        const w = window.currentUser || {};
        const name  = (w.name        ?? meta('userName')  ?? '').toString().trim();
        const email = (w.email       ?? meta('userEmail') ?? '').toString().trim();
        const phone = (w.phoneNumber ?? meta('userPhone') ?? '').toString().trim();
        return { name, email, phoneNumber: phone };
    }

    // 결제 준비 요청 보내기
    function prepareCharge(amountKRW) {
        return withAuthRetry(() =>
            ajaxPost(`${contextPath}/api/mypage/eumpyo/charge/ready`, { amount: amountKRW })
        ).then(unwrapData);
    }

    // 결제 완료 확인 요청 보내기
    function confirmCharge(impUid, merchantUid) {
        return withAuthRetry(() =>
            ajaxPost(`${contextPath}/api/mypage/eumpyo/charge/complete`, { impUid, merchantUid })
        ).then(unwrapData);
    }

    // 충전 버튼 클릭
    async function handleChargeClick(amountKRW, displayedCoin) {
        if (!amountKRW || amountKRW % 100 !== 0) {
            alert('결제 금액이 올바르지 않습니다.');
            return;
        }

        try {
            // 1) 결제 준비 호출
            const data = await prepareCharge(amountKRW);
            const merchantUid = data?.merchantUid;
            const payAmount   = data?.amountKRW;
            const chargedCoin = data?.chargedCoin ?? payAmount / 100;
            if (!merchantUid || !payAmount) throw new Error('결제 준비 응답 오류');

            // 2) 포트원(IMP) 결제창 열기
            const IMP = window.IMP;
            if (!IMP) { alert('결제 모듈 로드 실패'); return; }
            IMP.init(window.PORTONE_IMP || 'imp26556260');

            const buyer = getBuyerInfo();
            const payParams = {
                pg: 'html5_inicis',
                pay_method: 'card',
                merchant_uid: merchantUid,
                name: `음표 ${displayedCoin || chargedCoin}개`,
                amount: payAmount,
                buyer_name: buyer.name || '',
                buyer_email: buyer.email || '',
                buyer_tel: buyer.phoneNumber || '',
            };

            // 3) 결제 결과
            IMP.request_pay(payParams, function (payResponse) {
                if (!payResponse?.success) {
                    alert(
                        payResponse?.error_msg?.includes('취소')
                            ? '결제를 취소하셨습니다.'
                            : '결제 실패 또는 취소'
                    );
                    return;
                }

                // 4)  결제 완료 확인
                confirmCharge(payResponse.imp_uid, merchantUid)
                    .then((confirmResult) => {
                        alert(
                            `충전 완료!\n` +
                            `결제금액: ₩${Number(confirmResult?.amount || payAmount).toLocaleString()}\n` +
                            `충전음표: ${Number(confirmResult?.chargedCoin || chargedCoin).toLocaleString()}개\n` +
                            `보유음표: ${Number(confirmResult?.coinBalance || 0).toLocaleString()}개`
                        );
                        window.reloadCoinBalance?.(); // 상단 잔액 갱신(있으면)
                        location.href = `${contextPath}/mypage/eumpyo/chargeHistory`; // 내 충전내역으로 이동
                    })
                    .fail(() => {
                        alert('결제 확인 중 오류가 발생했습니다.');
                    });
            });
        } catch (error) {
            // 5) 에러 처리(401이면 로그인으로)
            var status  = error?.status || error?.response?.status;
            var message = error?.response?.data?.message || error?.message || '결제 준비 중 오류';

            if (status == 401 || !localStorage.getItem('accessToken')) {
                location.replace(`${contextPath}/auth/login?redirect=${encodeURIComponent(location.href)}`);
                return;
            }
            alert(message);
        }
    }

    // 충전 버튼에 클릭 이벤트 연결
    document.addEventListener('DOMContentLoaded', () => {
        document.querySelectorAll('.btn-charge').forEach((btn) => {
            btn.addEventListener('click', () => {
                handleChargeClick(
                    parseInt(btn.getAttribute('data-amount'), 10),
                    parseInt(btn.getAttribute('data-coin'), 10)
                );
            });
        });
    });
})();
