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
                    throw error;
                }
            }
            throw error;
        }
    }

    const unwrapResponse = (resp) => {
        const data = resp?.data ?? resp;
        const ok = data?.result === 'success' || !!data?.success;
        const body = data?.success?.responseData ?? data?.responseData ?? data;
        return { ok, body };
    };

    const getQueryInt = (name, defaultValue) => {
        const raw = new URL(location.href).searchParams.get(name);
        const num = raw == null ? NaN : parseInt(raw, 10);
        return Number.isNaN(num) ? defaultValue : num;
    };

    const paginationState = {
        page: getQueryInt('page', 1),
        size: getQueryInt('size', 10),
        totalCount: 0
    };

    const formatNumber = (n) => {
        try { return Number(n || 0).toLocaleString(); }
        catch { return n ?? '0'; }
    };

    function toYmd(chargedAt) {
        const only = (chargedAt || '').replace(/\D/g, '');
        return only.length >= 8 ? only.slice(0, 8) : '';
    }

    function buildChargeNoFallback(row) {
        const ymd = toYmd(row?.chargedAt);
        const idStr = String(row?.coinHistoryId ?? '').padStart(4, '0').slice(-4);
        return (ymd && idStr) ? `CHG${ymd}-${idStr}` : '-';
    }

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

    function loadChargePage(page, size) {
        const $balanceEl = $('#myCoinBalance');

        if (!localStorage.getItem('accessToken')) {
            $balanceEl.text('0');
            return;
        }

        withAuthRetry(() =>
            axios.get(`${contextPath}/api/mypage/eumpyo/history/charge`, {
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

            renderChargeRows(body.list, body.totalCount, body.page, body.size);
            renderPagination(body.totalCount, body.size, body.page);

            return withAuthRetry(() =>
                axios.get(`${contextPath}/api/mypage/eumpyo/charge/balance`, {
                    headers: getAuthHeader()
                })
            );
        })
        .then((balanceRes) => {
            const data = balanceRes?.data?.success?.responseData
                      ?? balanceRes?.data?.responseData
                      ?? balanceRes?.data;
            if (typeof data?.coinBalance !== 'undefined') {
                $('#myCoinBalance').text(Number(data.coinBalance).toLocaleString());
            }
        })
        .catch(() => {
            $('#chargeTbody').html('<tr><td colspan="5" class="text-center">목록 로드 실패</td></tr>');
        });
    }

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

    $(function () {
        loadChargePage(paginationState.page, paginationState.size);
    });
})();
