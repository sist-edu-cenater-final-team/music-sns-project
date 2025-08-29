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

    // GET 요청 보내기
    function ajaxGet(url, params) {
        return $.ajax({
            url,
            type: 'GET',
            data: params || {},
            headers: getAuthHeader(),
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
                        var refreshFn = window.refreshAuthToken;
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

    const unwrapResponse = (data) => {
        const ok = data?.result === 'success' || !!data?.success;
        const body = data?.success?.responseData ?? data?.responseData ?? data;
        return { ok, body };
    };

    const getQueryInt = (name, defaultValue) => {
        const raw = new URL(location.href).searchParams.get(name);
        const num = raw == null ? NaN : parseInt(raw, 10);
        return Number.isNaN(num) ? defaultValue : num;
    };

    // 현재 페이지 상태 저장
    const paginationState = {
        page: getQueryInt('page', 1),
        size: getQueryInt('size', 10),
        totalCount: 0
    };

    // 숫자 3자리마다 콤마 찍기
    const formatNumber = (n) => {
        try { return Number(n || 0).toLocaleString(); }
        catch { return n ?? '0'; }
    };

    // 날짜 문자열에서 YYYYMMDD만 추출
    function toYmd(chargedAt) {
        const only = (chargedAt || '').replace(/\D/g, '');
        return only.length >= 8 ? only.slice(0, 8) : '';
    }

    // 거래번호 만들기: CHGYYYYMMDD-XXXX
    function buildChargeNoFallback(row) {
        const ymd = toYmd(row?.chargedAt);
        const idStr = String(row?.coinHistoryId ?? '').padStart(4, '0').slice(-4);
        return (ymd && idStr) ? `CHG${ymd}-${idStr}` : '-';
    }

    // 충전내역
    function renderChargeRows(list, total, page, size) {
        const $tbody = $('#chargeTbody').empty();

        if (!list?.length) {
            $tbody.append('<tr><td colspan="5" class="text-center">충전내역이 없습니다.</td></tr>');
            return;
        }

        list.forEach((row) => {
            const chargeNo = row.chargeNo || buildChargeNoFallback(row);

            $tbody.append(
                `<tr>
                    <td class="col-no">${chargeNo}</td>
                    <td class="col-date">${row.chargedAt || '-'}</td>
                    <td class="col-coin">+${formatNumber(row.chargedCoin)} 음표</td>
                    <td class="col-after">${formatNumber(row.coinBalance)} 음표</td>
                    <td class="col-amount">${formatNumber(row.paidAmount)}원</td>
                </tr>`
            );
        });
    }

    // 페이지 버튼
    function renderPagination(total, size, currentPage) {
        const $container = $('#pagination').empty();

        const totalPage = Math.max(1, Math.ceil(total / Math.max(1, size)));
        const blockSize = 5;
        const start = Math.floor((currentPage - 1) / blockSize) * blockSize + 1;
        const end = Math.min(start + blockSize - 1, totalPage);

        let html = '<ul class="pg-bar">';

        // « 맨 처음
        if (currentPage > blockSize) {
            html += `<li class="pg-item--first"><a class="pg-link" data-page="1">&laquo;</a></li>`;
        } else {
            html += `<li class="pg-item--first is-disabled"><span>&laquo;</span></li>`;
        }

        // ‹ 이전
        if (currentPage > 1) {
            html += `<li class="pg-item--prev"><a class="pg-link" data-page="${currentPage - 1}">&lsaquo;</a></li>`;
        } else {
            html += `<li class="pg-item--prev is-disabled"><span>&lsaquo;</span></li>`;
        }

        // 숫자 버튼
        for (let p = start; p <= end; p++) {
            if (p === currentPage) {
                html += `<li class="pg-item--num is-current"><span class="pg-current">${p}</span></li>`;
            } else {
                html += `<li class="pg-item--num"><a class="pg-link" data-page="${p}">${p}</a></li>`;
            }
        }

        // › 다음
        if (currentPage < totalPage) {
            html += `<li class="pg-item--next"><a class="pg-link" data-page="${currentPage + 1}">&rsaquo;</a></li>`;
        } else {
            html += `<li class="pg-item--next is-disabled"><span>&rsaquo;</span></li>`;
        }

        // » 마지막
        if (end < totalPage) {
            html += `<li class="pg-item--last"><a class="pg-link" data-page="${totalPage}">&raquo;</a></li>`;
        } else {
            html += `<li class="pg-item--last is-disabled"><span>&raquo;</span></li>`;
        }

        html += '</ul>';
        $container.html(html);
    }

    // 충전내역 한 페이지 불러오기
    function loadChargePage(page, size) {
        const $balanceEl = $('#myCoinBalance');

        // 토큰 없으면 잔액 0
        if (!localStorage.getItem('accessToken')) {
            $balanceEl.text('0');
            return;
        }

        // 1) 목록 불러오기 → 2) 표/페이지 표시 → 3) 잔액 불러와서 표시
        withAuthRetry(() => ajaxGet(`${contextPath}/api/mypage/eumpyo/history/charge`, { page, size }))
            .then((data) => {
                const { ok, body } = unwrapResponse(data);
                if (!ok) throw new Error('목록 로드 실패');

                paginationState.page = body.page;
                paginationState.size = body.size;
                paginationState.totalCount = body.totalCount;

                renderChargeRows(body.list, body.totalCount, body.page, body.size);
                renderPagination(body.totalCount, body.size, body.page);

                // 잔액 조회
                return withAuthRetry(() => ajaxGet(`${contextPath}/api/mypage/eumpyo/charge/balance`));
            })
            .then((balanceData) => {
                const data =
                    balanceData?.success?.responseData ??
                    balanceData?.responseData ??
                    balanceData;

                if (typeof data?.coinBalance !== 'undefined') {
                    $('#myCoinBalance').text(Number(data.coinBalance).toLocaleString());
                }
            })
            .fail(() => {
                $('#chargeTbody').html('<tr><td colspan="5" class="text-center">목록 로드 실패</td></tr>');
            });
    }

    // 페이지 번호 클릭 시, 해당 페이지 불러오기
    $(document).on('click', '#pagination a.pg-link', function (e) {
        e.preventDefault();
        const nextPage = parseInt($(this).data('page'), 10);
        if (!Number.isInteger(nextPage)) return;

        loadChargePage(nextPage, paginationState.size);

        const url = new URL(location.href);
        url.searchParams.set('page', nextPage);
        url.searchParams.set('size', paginationState.size);
        history.replaceState({}, '', url);
    });

    // 목록 불러오기
    $(function () {
        loadChargePage(paginationState.page, paginationState.size);
    });
})();
