(function () {
	const contextPath =
		window.ctxPath ||
		document.querySelector('meta[name="ctxPath"]')?.content ||
		'';

	// 미로그인 시 로그인 페이지 이동
	try {
		if (!localStorage.getItem('accessToken')) {
			alert("로그인이 필요합니다.");
			location.href = `${ctxPath}/auth/login`;
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

	// 401 발생 시 토큰 리프레시 후 재시도
	async function withAuthRetry(run) {
		try {
			return await run();
		} catch (error) {
			if (error?.response?.status === 401) {
				try {
					await window.refreshAuthToken?.();
					return await run();
				} catch {
					localStorage.removeItem('accessToken');
					localStorage.removeItem('tokenType');
					alert('세션이 만료되었습니다. 다시 로그인해주세요.');
					location.href = `${contextPath}/auth/login`;
					return;
				}
			}
			throw error;
		}
	}

	const unwrapResponse = (resp) => {
		const data = resp?.data ?? resp;
		const ok = data?.result === 'success' || !!data?.success;
		return { ok, body: data?.success?.responseData ?? data?.responseData ?? data };
	};

	// 페이지/사이즈 가져오기
	const getQueryInt = (name, defaultValue) => {
		const raw = new URL(location.href).searchParams.get(name);
		const num = raw == null ? NaN : parseInt(raw, 10);
		return Number.isNaN(num) ? defaultValue : num;
	};

	const formatNumber = (n) => {
		try { return Number(n || 0).toLocaleString(); }
		catch { return n; }
	};

	function toYmdFromPurchasedAt(purchasedAt) {
		const ymd = (purchasedAt || '').replace(/\D/g, '');
		if (ymd && ymd.length === 8) return ymd;
		const d = new Date();
		const yyyy = d.getFullYear();
		const mm = String(d.getMonth() + 1).padStart(2, '0');
		const dd = String(d.getDate()).padStart(2, '0');
		return `${yyyy}${mm}${dd}`;
	}

	function buildPurchaseNo(row) {
		if (row?.purchaseNo) return row.purchaseNo;
		const ymd = toYmdFromPurchasedAt(row?.purchasedAt); // YYYYMMDD
		const idStr = String(row?.purchaseHistoryId ?? '').padStart(4, '0'); // 뒤 4자리만 사용
		return `ORD${ymd}-${idStr}`;
	}

	// 페이지 상태 관리
	const paginationState = {
		page: getQueryInt('page', 1),
		size: getQueryInt('size', 10),
		totalCount: 0
	};

	function getTbodySel() {
		const $tb = $('#purchaseTbody');
		return $tb.length ? $tb : $('.purchaseList tbody');
	}

	// 구매 상세 음악 목록 렌더링
	function renderPurchaseDetailList($detailRow, items) {
		const $content = $detailRow.find('.detail-content');
		if (!items?.length) {
			$content.html('<div class="detail-loading">구매한 음악이 없습니다.</div>');
		 return;
		}

		let html = '<ul class="detail-list">';
		items.forEach((t) => {
			const title  = t.musicName || t.musicId || '-';
			const artist = t.artistName || '';
			const album  = t.albumName  || '';
			const used   = Number(t.usedCoin || 0);
			const thumb  = t.albumImageUrl
				? `<img src="${t.albumImageUrl}" alt="${title}">`
				: `<img src="${contextPath}/images/mypage/noAlbumImage.png" alt="-">`;

			// 한 줄 텍스트 조합
			const metaLine = [
				title,
				artist ? `· ${artist}` : '',
				album  ? `· ${album}`  : ''
			].join(' ');

			html +=
				`\n\t<li class="detail-item">` +
				`\n\t\t<div class="detail-thumb">${thumb}</div>` +
				`\n\t\t<div class="detail-meta">` +
				`\n\t\t\t<div class="detail-line">` +
				`\n\t\t\t\t<span class="t">${title}</span>` +
				(artist ? ` <span class="s">· ${artist}</span>` : '') +
				(album  ? ` <span class="s">· ${album}</span>`   : '') +
				`\n\t\t\t</div>` +
				`\n\t\t</div>` +
				`\n\t\t<div class="used-coin">-${formatNumber(used)} 음표</div>` +
				`\n\t</li>`;
		});
		html += '\n</ul>';

		$content.html(html);
	}

	// 구매내역 테이블 행 렌더링
	function renderPurchaseRows(list, total, page, size) {
		const $tbody = getTbodySel().empty();
		if (!list?.length) {
			$tbody.append('<tr><td colspan="5" class="text-center">구매내역이 없습니다.</td></tr>');
			return;
		}

		list.forEach((row) => {
			const purchaseNo = buildPurchaseNo(row);
			const title = row.titleSummary || '-';
			const thumb = row.albumImageUrl
				? `<img src="${row.albumImageUrl}" width="48" height="48" alt="">`
				: `<img src="${contextPath}/images/mypage/noAlbumImage.png" width="48" height="48" alt="">`;

			$tbody.append(`
				<tr class="purchase-history-row" data-pid="${row.purchaseHistoryId}">
					<td class="col-purchaseNo">${purchaseNo}</td>
					<td>${row.purchasedAt || '-'}</td>
					<td>
						<div class="music-info">
							<div class="music-img">${thumb}</div>
							<div class="music-text">
								<span class="title-ellipsis">${title}</span>
							</div>
						</div>
					</td>
					<td class="col-usedCoin">-${formatNumber(row.usedCoin)} 음표</td>
					<td class="col-balance">${formatNumber(row.coinBalance)} 음표</td>
				</tr>
				<tr class="purchase-detail" data-pid="${row.purchaseHistoryId}" style="display:none;">
					<td colspan="5">
						<div class="detail-loading" style="display:none;">불러오는 중...</div>
						<div class="detail-content"></div>
					</td>
				</tr>
			`);
		});
	}

	// 페이지네이션 렌더링
	function renderPagination(total, size, currentPage) {
		const $container = $('#pagination').empty();
		const totalPage = Math.max(1, Math.ceil(total / Math.max(1, size)));
		const blockSize = 5;
		const start = Math.floor((currentPage - 1) / blockSize) * blockSize + 1;
		const end = Math.min(start + blockSize - 1, totalPage);

		let html = '<ul class="pg-bar">';

		if (currentPage > blockSize) {
			html += `<li class="pg-item--first"><a class="pg-link" data-page="1">&laquo;</a></li>`;
		} else {
			html += `<li class="pg-item--first is-disabled"><span>&laquo;</span></li>`;
		}

		if (currentPage > 1) {
			html += `<li class="pg-item--prev"><a class="pg-link" data-page="${currentPage - 1}">&lsaquo;</a></li>`;
		} else {
			html += `<li class="pg-item--prev is-disabled"><span>&lsaquo;</span></li>`;
		}

		for (let p = start; p <= end; p++) {
			if (p === currentPage) {
				html += `<li class="pg-item--num is-current"><span class="pg-current">${p}</span></li>`;
			} else {
				html += `<li class="pg-item--num"><a class="pg-link" data-page="${p}">${p}</a></li>`;
			}
		}

		if (currentPage < totalPage) {
			html += `<li class="pg-item--next"><a class="pg-link" data-page="${currentPage + 1}">&rsaquo;</a></li>`;
		} else {
			html += `<li class="pg-item--next is-disabled"><span>&rsaquo;</span></li>`;
		}

		if (end < totalPage) {
			html += `<li class="pg-item--last"><a class="pg-link" data-page="${totalPage}">&raquo;</a></li>`;
		} else {
			html += `<li class="pg-item--last is-disabled"><span>&raquo;</span></li>`;
		}

		html += '</ul>';
		$container.html(html);
	}

	// 구매 내역 데이터 불러오기
	function loadPurchasePage(page, size) {
		const $balanceEl = $('#myCoinBalance');

		if (!localStorage.getItem('accessToken')) {
			$balanceEl.text('0');
			return;
		}

		withAuthRetry(() =>
			axios.get(`${contextPath}/api/mypage/eumpyo/history/purchase`, {
				params: { page, size },
				headers: getAuthHeader()
			})
		)
		.then((res) => {
			const { ok, body } = unwrapResponse(res);
			if (!ok) throw new Error('목록 로드 실패');

			paginationState.page = body.page;
			paginationState.size = body.size;
			paginationState.totalCount = body.totalCount;

			renderPurchaseRows(body.list, body.totalCount, body.page, body.size);
			renderPagination(body.totalCount, body.size, body.page);

			return withAuthRetry(() =>
				axios.get(`${contextPath}/api/mypage/eumpyo/charge/balance`, { headers: getAuthHeader() })
			);
		})
		.then((balanceRes) => {
			const data = balanceRes?.data?.success?.responseData ?? balanceRes?.data?.responseData ?? balanceRes?.data;
			if (typeof data?.coinBalance !== 'undefined') {
				$balanceEl.text(Number(data.coinBalance).toLocaleString());
			}
		})
		.catch(() => {
			getTbodySel().html('<tr><td colspan="5" class="text-center">목록 로드 실패</td></tr>');
		});
	}

	// 페이지네이션 클릭 이벤트
	$(document).on('click', '#pagination a.pg-link', function (e) {
		e.preventDefault();
		const nextPage = parseInt($(this).data('page'), 10);
		if (!Number.isInteger(nextPage)) return;

		loadPurchasePage(nextPage, paginationState.size);

		const url = new URL(location.href);
		url.searchParams.set('page', nextPage);
		url.searchParams.set('size', paginationState.size);
		history.replaceState({}, '', url);
	});

	// 구매내역 행 클릭 시 상세 토글
	$(document).on('click', '.purchase-history-row', function () {
		const purchaseId = $(this).data('pid');
		const $detailRow = $(`.purchase-detail[data-pid="${purchaseId}"]`);

		if ($detailRow.data('loaded')) {
			$detailRow.toggle();
			return;
		}

		$detailRow.show();
		$detailRow.find('.detail-loading').show();

		withAuthRetry(() =>
			axios.get(`${contextPath}/api/mypage/eumpyo/history/purchase/${purchaseId}/purchaseMusic`, {
				headers: getAuthHeader()
			})
		)
		.then((res) => {
			$detailRow.find('.detail-loading').hide();

			const { ok, body } = unwrapResponse(res);
			if (!ok) {
				$detailRow.find('.detail-content').text('불러오기 실패');
				return;
			}

			renderPurchaseDetailList($detailRow, body.purchaseMusic || body.list || body);
			$detailRow.data('loaded', true);
		})
		.catch(() => {
			$detailRow.find('.detail-loading').hide();
			$detailRow.find('.detail-content').text('오류');
		});
	});

	$(function () {
		loadPurchasePage(paginationState.page, paginationState.size);
	});
})();
