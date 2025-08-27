(function () {
	const contextPath =
		window.ctxPath ||
		document.querySelector('meta[name="ctxPath"]')?.content ||
		'';

	// 미로그인 시 로그인 페이지 이동
	try {
		if (!localStorage.getItem('accessToken')) {
			alert('로그인이 필요합니다.');
			location.href = `${contextPath}/auth/login`;
			return;
		}
	} catch (e) {}

	// 공통 인증 헤더
	function getAuthHeader() {
		const fromApp = window.AuthFunc?.getAuthHeader?.();
		if (fromApp && Object.keys(fromApp).length) return fromApp;

		const accessToken = localStorage.getItem('accessToken');
		const tokenType = localStorage.getItem('tokenType') || 'Bearer';
		return accessToken ? { Authorization: `${tokenType} ${accessToken}` } : {};
	}

	async function withAuthRetry(run) {
		try {
			return await run();
		} catch (error) {
			if (error?.response?.status == 401) {
				try {
					await window.refreshAuthToken?.();
					return await run();
				} catch {
					localStorage.removeItem('accessToken');
					localStorage.removeItem('tokenType');
					throw error;
				}
			}
			throw error;
		}
	}

	const unwrapData = (response) => {
		const data = response?.data ?? response;
		return data?.success?.responseData ?? data?.responseData ?? data;
	};

	const meta = (name) =>
		document.querySelector(`meta[name="${name}"]`)?.content?.trim() || '';

	function getBuyerInfo() {
		const w = window.currentUser || {};
		const name =
			(w.name ?? meta('userName') ?? '').toString().trim();
		const email =
			(w.email ?? meta('userEmail') ?? '').toString().trim();
		const phoneNumber =
			(w.phoneNumber ?? meta('userPhone') ?? '').toString().trim();

		return { name, email, phoneNumber };
	}

	function prepareCharge(amountKRW) {
		return withAuthRetry(() =>
			axios.post(
				`${contextPath}/api/mypage/eumpyo/charge/ready`,
				{ amount: amountKRW },
				{ headers: getAuthHeader() }
			)
		).then(unwrapData);
	}

	function confirmCharge(impUid, merchantUid) {
		return withAuthRetry(() =>
			axios.post(
				`${contextPath}/api/mypage/eumpyo/charge/complete`,
				{ impUid, merchantUid },
				{ headers: getAuthHeader() }
			)
		).then(unwrapData);
	}

	async function handleChargeClick(amountKRW, displayedCoin) {
		if (!amountKRW || amountKRW % 100 !== 0) {
			alert('결제 금액이 올바르지 않습니다.');
			return;
		}

		try {
			const data = await prepareCharge(amountKRW);
			const merchantUid = data?.merchantUid;
			const payAmount = data?.amountKRW;
			const chargedCoin = data?.chargedCoin ?? payAmount / 100;

			if (!merchantUid || !payAmount) throw new Error('결제 준비 응답 오류');

			const IMP = window.IMP;
			if (!IMP) {
				alert('결제 모듈 로드 실패');
				return;
			}
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

			IMP.request_pay(payParams, function (payResponse) {
				if (!payResponse?.success) {
					alert(
						payResponse?.error_msg?.includes('취소')
							? '결제를 취소하셨습니다.'
							: '결제 실패 또는 취소'
					);
					return;
				}

				confirmCharge(payResponse.imp_uid, merchantUid)
					.then((confirmResult) => {
						alert(
							`충전 완료!\n` +
							`결제금액: ₩${Number(
								confirmResult?.amount || payAmount
							).toLocaleString()}\n` +
							`충전음표: ${Number(
								confirmResult?.chargedCoin || chargedCoin
							).toLocaleString()}개\n` +
							`보유음표: ${Number(
								confirmResult?.coinBalance || 0
							).toLocaleString()}개`
						);
						window.reloadCoinBalance?.();
						location.href = `${contextPath}/mypage/eumpyo/chargeHistory`;
					})
					.catch(() => alert('결제 확인 중 오류가 발생했습니다.'));
			});
		} catch (error) {
			const message =
				error?.response?.data?.message ||
				error?.message ||
				'결제 준비 중 오류';

			if (error?.response?.status == 401 || !localStorage.getItem('accessToken')) {
				location.replace(
					`${contextPath}/auth/login?redirect=${encodeURIComponent(location.href)}`
				);
				return;
			}
			alert(message);
		}
	}

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
