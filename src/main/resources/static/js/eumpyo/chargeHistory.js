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
    const appProvidedHeaders = window.AuthFunc?.getAuthHeader?.();
    if (appProvidedHeaders && Object.keys(appProvidedHeaders).length) return appProvidedHeaders;

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
  function withAuthRetry(runAjaxFunction) {
    var deferred = $.Deferred();
    var alreadyRetried = false;

    function attempt() {
      runAjaxFunction()
        .done(function (responseData) {
          deferred.resolve(responseData);
        })
        .fail(function (jqXhr, textStatus, errorThrown) {
          if (jqXhr && jqXhr.status == 401 && !alreadyRetried) {
            alreadyRetried = true;
            var refreshAuthTokenFn = AuthFunc.refreshAuthToken;
            if (typeof refreshAuthTokenFn == 'function') {
              $.when(refreshAuthTokenFn())
                .done(function () {
                  attempt();
                })
                .fail(function () {
                  // 새 토큰 받기 실패 → 저장된 토큰 지우고 종료
                  try {
                    localStorage.removeItem('accessToken');
                    localStorage.removeItem('tokenType');
                  } catch (e) {}
                  deferred.reject(jqXhr, textStatus, errorThrown);
                });
            } else {
              try {
                localStorage.removeItem('accessToken');
                localStorage.removeItem('tokenType');
              } catch (e) {}
              deferred.reject(jqXhr, textStatus, errorThrown);
            }
            return;
          }
          deferred.reject(jqXhr, textStatus, errorThrown);
        });
    }

    attempt();
    return deferred.promise();
  }

  const unwrapResponse = (rawResponse) => {
    const isOk = rawResponse?.result == 'success' || !!rawResponse?.success;
    const responseBody =
      rawResponse?.success?.responseData ??
      rawResponse?.responseData ??
      rawResponse;
    return { isOk, responseBody };
  };

  const getQueryInt = (paramName, defaultValue) => {
    const rawValue = new URL(location.href).searchParams.get(paramName);
    const parsedNumber = rawValue == null ? NaN : parseInt(rawValue, 10);
    return Number.isNaN(parsedNumber) ? defaultValue : parsedNumber;
  };

  // 현재 페이지 상태 저장
  const paginationState = {
    page: getQueryInt('page', 1),
    size: getQueryInt('size', 10),
    totalCount: 0
  };

  // 숫자 3자리마다 콤마 찍기
  const formatNumber = (n) => {
    try {
      return Number(n || 0).toLocaleString();
    } catch {
      return n ?? '0';
    }
  };

  // 날짜 문자열에서 YYYYMMDD만 추출
  function toYYYYMMDD(chargedAt) {
    const digitsOnly = (chargedAt || '').replace(/\D/g, '');
    return digitsOnly.length >= 8 ? digitsOnly.slice(0, 8) : '';
  }

  // 거래번호 만들기: CHGYYYYMMDD-XXXX
  function buildChargeNumberFallback(row) {
    const ymd = toYYYYMMDD(row?.chargedAt);
    const idString = String(row?.coinHistoryId ?? '').padStart(4, '0').slice(-4);
    return ymd && idString ? `CHG${ymd}-${idString}` : '-';
  }

  // 충전내역
  function renderChargeRows(chargeList, totalCount, page, size) {
    const $tbody = $('#chargeTbody').empty();

    if (!chargeList?.length) {
      $tbody.append('<tr><td colspan="5" class="text-center">충전내역이 없습니다.</td></tr>');
      return;
    }

    chargeList.forEach((row) => {
      const chargeNumber = row.chargeNo || buildChargeNumberFallback(row);

      $tbody.append(
        `<tr>
          <td class="col-no">${chargeNumber}</td>
          <td class="col-date">${row.chargedAt || '-'}</td>
          <td class="col-coin">+${formatNumber(row.chargedCoin)} 음표</td>
          <td class="col-after">${formatNumber(row.coinBalance)} 음표</td>
          <td class="col-amount">${formatNumber(row.paidAmount)}원</td>
        </tr>`
      );
    });
  }

  // 페이지 버튼
  function renderPagination(totalCount, size, currentPage) {
    const $container = $('#pagination').empty();

    const totalPageCount = Math.max(1, Math.ceil(totalCount / Math.max(1, size)));
    const blockSize = 5;
    const startPage = Math.floor((currentPage - 1) / blockSize) * blockSize + 1;
    const endPage = Math.min(startPage + blockSize - 1, totalPageCount);

    let htmlString = '<ul class="pg-bar">';

    // « 맨 처음
    if (currentPage > blockSize) {
      htmlString += `<li class="pg-item--first"><a class="pg-link" data-page="1">&laquo;</a></li>`;
    } else {
      htmlString += `<li class="pg-item--first is-disabled"><span>&laquo;</span></li>`;
    }

    // ‹ 이전
    if (currentPage > 1) {
      htmlString += `<li class="pg-item--prev"><a class="pg-link" data-page="${currentPage - 1}">&lsaquo;</a></li>`;
    } else {
      htmlString += `<li class="pg-item--prev is-disabled"><span>&lsaquo;</span></li>`;
    }

    // 숫자 버튼
    for (let pageNumber = startPage; pageNumber <= endPage; pageNumber++) {
      if (pageNumber == currentPage) {
        htmlString += `<li class="pg-item--num is-current"><span class="pg-current">${pageNumber}</span></li>`;
      } else {
        htmlString += `<li class="pg-item--num"><a class="pg-link" data-page="${pageNumber}">${pageNumber}</a></li>`;
      }
    }

    // › 다음
    if (currentPage < totalPageCount) {
      htmlString += `<li class="pg-item--next"><a class="pg-link" data-page="${currentPage + 1}">&rsaquo;</a></li>`;
    } else {
      htmlString += `<li class="pg-item--next is-disabled"><span>&rsaquo;</span></li>`;
    }

    // » 마지막
    if (endPage < totalPageCount) {
      htmlString += `<li class="pg-item--last"><a class="pg-link" data-page="${totalPageCount}">&raquo;</a></li>`;
    } else {
      htmlString += `<li class="pg-item--last is-disabled"><span>&raquo;</span></li>`;
    }

    htmlString += '</ul>';
    $container.html(htmlString);
  }

  // 충전내역 한 페이지 불러오기
  function loadChargePage(page, size) {
    const $myCoinBalanceElement = $('#myCoinBalance');

    // 토큰 없으면 잔액 0
    if (!localStorage.getItem('accessToken')) {
      $myCoinBalanceElement.text('0');
      return;
    }

    // 1) 목록 불러오기 → 2) 표/페이지 표시 → 3) 잔액 불러와서 표시
    withAuthRetry(() => ajaxGet(`${contextPath}/api/mypage/eumpyo/history/charge`, { page, size }))
      .then((response) => {
        const { isOk, responseBody } = unwrapResponse(response);
        if (!isOk) throw new Error('목록 로드 실패');

        paginationState.page = responseBody.page;
        paginationState.size = responseBody.size;
        paginationState.totalCount = responseBody.totalCount;

        renderChargeRows(responseBody.list, responseBody.totalCount, responseBody.page, responseBody.size);
        renderPagination(responseBody.totalCount, responseBody.size, responseBody.page);

        // 잔액 조회
        return withAuthRetry(() => ajaxGet(`${contextPath}/api/mypage/eumpyo/charge/balance`));
      })
      .then((balanceResponse) => {
        const balanceBody =
          balanceResponse?.success?.responseData ??
          balanceResponse?.responseData ??
          balanceResponse;

        if (typeof balanceBody?.coinBalance != 'undefined') {
          $('#myCoinBalance').text(Number(balanceBody.coinBalance).toLocaleString());
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
