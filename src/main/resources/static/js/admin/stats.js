// src/main/webapp/js/admin/stats.js
(function () {
  const contextPath =
    window.ctxPath ||
    document.querySelector('meta[name="ctxPath"]')?.content ||
    '';

  // ===== 전역 중복 제어 플래그 =====
  var sessionRedirected = false;            // 세션 만료로 로그인 이동 1회만
  var permissionBlocked = false;            // 권한 없음 알림 1회만
  var genericFailAlertShown = false;        // 조회 실패 알림 1회만

  // ===== 미로그인 시 즉시 차단 =====
  try {
    if (!localStorage.getItem('accessToken')) {
      alert('로그인이 필요합니다.');
      location.href = contextPath + '/auth/login';
      return;
    }
  } catch (e) {}

  // ===== 공통 인증 헤더 =====
  function getAuthHeader() {
    try {
      var fromApp = window.AuthFunc?.getAuthHeader?.();
      if (fromApp && typeof fromApp === 'object' && Object.keys(fromApp).length) {
        if (!('X-Requested-With' in fromApp)) fromApp['X-Requested-With'] = 'XMLHttpRequest';
        return fromApp;
      }
    } catch (e) {}

    var accessToken = localStorage.getItem('accessToken');
    var tokenType   = localStorage.getItem('tokenType') || 'Bearer';
    var headers     = { 'X-Requested-With': 'XMLHttpRequest' };
    if (accessToken) headers.Authorization = tokenType + ' ' + accessToken;
    return headers;
  }

  // ===== 보호 유틸 =====
  function isRedirecting() { return !!window.__SESSION_REDIRECTING_TO_LOGIN__; }

  function alertOnce(msg) {
    if (window.__ALERT_ONCE_SHOWN__) return;
    window.__ALERT_ONCE_SHOWN__ = true;
    try { alert(msg); } catch (e) {}
  }

  function alertGenericFailOnce() {
    if (genericFailAlertShown || sessionRedirected || permissionBlocked || isRedirecting()) return;
    genericFailAlertShown = true;
    alert('일부 대시보드 데이터를 불러오지 못했습니다.');
  }

  function handleSessionExpiredOnce() {
    if (window.__SESSION_REDIRECTING_TO_LOGIN__) return;
    window.__SESSION_REDIRECTING_TO_LOGIN__ = true;
    if (sessionRedirected) return;
    sessionRedirected = true;

    try {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('tokenType');
    } catch (e) {}

    alertOnce('세션이 만료되었습니다. 다시 로그인해주세요.');
    location.replace(contextPath + '/auth/login'); // 히스토리에 남기지 않음
  }

  function handlePermissionDeniedOnce() {
    if (permissionBlocked) return;
    permissionBlocked = true;
    alert('관리자 권한이 필요합니다.');
    location.replace(contextPath + '/auth/login');
  }

  // ===== AJAX 공통 =====
  function ajaxGet(url, params) {
    return $.ajax({
      url: url,
      type: 'GET',
      data: params || {},
      headers: getAuthHeader(),
      dataType: 'json'
    });
  }

  function isHtmlResponse(xhr) {
    try {
      var ct = xhr?.getResponseHeader?.('content-type');
      return ct && typeof ct === 'string' && ct.indexOf('text/html') !== -1;
    } catch (e) { return false; }
  }

  // ===== [NEW] 리프레시 단일화(single-flight) =====
  var refreshPromise = null;
  function ensureRefreshed() {
    if (refreshPromise) return refreshPromise; // 이미 진행 중이면 그 Promise 대기

    var dfd = $.Deferred();
    refreshPromise = dfd.promise();

    var refreshFn = window.refreshAuthToken;
    if (typeof refreshFn !== 'function') {
      setTimeout(function(){ dfd.reject('NO_REFRESH_FN'); refreshPromise = null; }, 0);
      return dfd.promise();
    }

    $.when(refreshFn())
      .done(function(){ dfd.resolve(); })
      .fail(function(){ dfd.reject('REFRESH_FAIL'); })
      .always(function(){ refreshPromise = null; });

    return dfd.promise();
  }

  // 401 → 리프레시 단일화 → 재시도
  function withAuthRetry(run) {
    var dfd = $.Deferred();
    var retried = false;

    function attempt() {
      if (isRedirecting()) { dfd.reject(); return; }

      // 진행 중 리프레시가 있으면 완료까지 대기한 뒤 시도
      var gate = refreshPromise ? refreshPromise : $.Deferred().resolve().promise();

      gate
        .done(function () {
          run()
            .done(function (data, _textStatus, xhr) {
              if (isHtmlResponse(xhr)) { handleSessionExpiredOnce(); dfd.reject(xhr); return; }
              dfd.resolve(data);
            })
            .fail(function (xhr) {
              if (isHtmlResponse(xhr)) { handleSessionExpiredOnce(); dfd.reject(xhr); return; }

              if (xhr && xhr.status === 401) {
                if (retried) { handleSessionExpiredOnce(); dfd.reject(xhr); return; }
                retried = true;
                ensureRefreshed()
                  .done(function(){ attempt(); })
                  .fail(function(){ handleSessionExpiredOnce(); dfd.reject(xhr); });
                return;
              }

              if (xhr && xhr.status === 403) { handlePermissionDeniedOnce(); dfd.reject(xhr); return; }

              console.warn('데이터 로딩 실패:', xhr?.status);
              alertGenericFailOnce();
              dfd.reject(xhr);
            });
        })
        .fail(function () {
          handleSessionExpiredOnce();
          dfd.reject();
        });
    }

    attempt();
    return dfd.promise();
  }

  function unwrapData(resp) {
    return resp?.success?.responseData ?? resp?.responseData ?? resp;
  }

  // ===== 포맷터 =====
  function formatNumber(v) {
    try { return Number(v == null ? 0 : v).toLocaleString(); }
    catch { return (v == null ? 0 : v) + ''; }
  }

  function formatDate(d) {
    var y = d.getFullYear();
    var m = String(d.getMonth() + 1); if (m.length === 1) m = '0' + m;
    var day = String(d.getDate());    if (day.length === 1) day = '0' + day;
    return y + '-' + m + '-' + day;
  }

  // ===== 날짜 범위(입력 컨트롤용) =====
  function setTodayRange() {
    var today = new Date();
    $('#startDate').val(formatDate(today));
    $('#endDate').val(formatDate(today));
  }
  function setThisWeekRange() {
    var now = new Date();
    var dayOfWeek = now.getDay();
    var mondayOffset = (dayOfWeek === 0 ? -6 : 1 - dayOfWeek);
    var monday = new Date(now);
    monday.setDate(now.getDate() + mondayOffset);
    var sunday = new Date(monday);
    sunday.setDate(monday.getDate() + 6);
    $('#startDate').val(formatDate(monday));
    $('#endDate').val(formatDate(sunday));
  }
  function setThisMonthRange() {
    var now = new Date();
    var first = new Date(now.getFullYear(), now.getMonth(), 1);
    var last  = new Date(now.getFullYear(), now.getMonth() + 1, 0);
    $('#startDate').val(formatDate(first));
    $('#endDate').val(formatDate(last));
  }
  function setLastNDaysRange(n) {
    var end = new Date();
    var start = new Date();
    start.setDate(end.getDate() - (n - 1));
    $('#startDate').val(formatDate(start));
    $('#endDate').val(formatDate(end));
  }

  // ===== 공통 파라미터/부제 =====
  function buildQueryParams() {
    var params = {};
    var start = $('#startDate').val();
    var end   = $('#endDate').val();
    if (start) params.startDate = start;
    if (end)   params.endDate   = end;
    return params;
  }

  function buildSubtitle(unitText) {
    var s = $('#startDate').val() || '전체';
    var e = $('#endDate').val()   || '전체';
    return '기간: ' + s + ' ~ ' + e + (unitText ? ' · 단위: ' + unitText : '');
  }

  // ===== [신규] 최근 7일 고정 차트(예제 포맷) =====
  function last7DaysDates() {
    var arr = [];
    var today = new Date();
    for (var i = 6; i >= 0; i--) {
      var d = new Date(today);
      d.setDate(today.getDate() - i);
      arr.push(d);
    }
    return arr; // 과거→오늘
  }
  function toYmd(d) { return formatDate(d); } // YYYY-MM-DD
  function toMd(d)  { // MM/DD
    var m = String(d.getMonth() + 1).padStart(2, '0');
    var da = String(d.getDate()).padStart(2, '0');
    return m + '/' + da;
  }
  function normKey(s) {
    if (!s) return '';
    var only = String(s).replace(/\D/g, '');
    return (only.length >= 8) ? (only.slice(0,4) + '-' + only.slice(4,6) + '-' + only.slice(6,8)) : s;
  }
  function arrToMap(rows) {
    var m = {};
    if (Array.isArray(rows)) {
      rows.forEach(function (r) {
        var k = normKey(r.bucket || r.date || r.day);
        var v = Number(r.value || r.count || 0) || 0;
        if (k) m[k] = v;
      });
    }
    return m;
  }

  function drawWeeklyUsedChargedChart(labels, chargedData, usedData, startYmd, endYmd) {
    if (!document.getElementById('chart-weekly-used-vs-charged')) return; // 컨테이너 없으면 스킵
    Highcharts.chart('chart-weekly-used-vs-charged', {
      chart: { zooming: { type: 'xy' } },
      title: { text: '최근 7일 충전/사용 음표', align: 'left' },
      subtitle: { text: '기간: ' + startYmd + ' ~ ' + endYmd + ' · 단위: 개', align: 'left' },
      credits: { enabled: false },
      xAxis: [{ categories: labels, crosshair: true }],
      yAxis: [{
        labels: { format: '{value} 개' },
        title: { text: '사용 음표' },
        lineColor: Highcharts.getOptions().colors[1],
        lineWidth: 2
      }, {
        title: { text: '충전 음표' },
        labels: { format: '{value} 개' },
        lineColor: Highcharts.getOptions().colors[0],
        lineWidth: 2,
        opposite: true
      }],
      tooltip: { shared: true },
      legend: { align: 'left', verticalAlign: 'top' },
      series: [{
        name: '충전 음표',
        type: 'column',
        yAxis: 1,
        data: chargedData,
        tooltip: { valueSuffix: ' 개' }
      }, {
        name: '사용 음표',
        type: 'spline',
        data: usedData,
        tooltip: { valueSuffix: ' 개' }
      }]
    });
  }

  function loadWeeklyUsedVsChargedChart() {
    var days = last7DaysDates();
    var labels = days.map(toMd);
    var keys   = days.map(toYmd);
    var startYmd = keys[0];
    var endYmd   = keys[keys.length - 1];

    var param = { startDate: startYmd, endDate: endYmd };

    var pCharged = withAuthRetry(function () {
      return ajaxGet(contextPath + '/api/admin/stats/series/charged', param);
    });
    var pUsed = withAuthRetry(function () {
      return ajaxGet(contextPath + '/api/admin/stats/series/used', param);
    });

    $.when(pCharged, pUsed)
      .done(function (respCharged, respUsed) {
        var rowsCharged = unwrapData(respCharged) || [];
        var rowsUsed    = unwrapData(respUsed)    || [];

        var mapC = arrToMap(rowsCharged);
        var mapU = arrToMap(rowsUsed);

        var chargedData = keys.map(function (k) { return mapC[k] || 0; });
        var usedData    = keys.map(function (k) { return mapU[k] || 0; });

        drawWeeklyUsedChargedChart(labels, chargedData, usedData, startYmd, endYmd);
      })
      .fail(function () {
        console.warn('주간 충전/사용 차트 로드 실패');
      });
  }

  // ===== 기존 차트/테이블 =====
  function drawChargedColumnChart(categories, dataArr, subtitleText) {
    Highcharts.chart('chart-charged', {
      chart: { type: 'column' },
      title: { text: '충전 음표 추이(일)' },
      subtitle: { text: subtitleText || '' },
      xAxis: { categories: categories, title: { text: '일자' } },
      yAxis: { title: { text: '충전 음표(개)' }, min: 0 },
      legend: { enabled: false },
      tooltip: { headerFormat: '일자: <b>{point.key}</b><br/>', pointFormat: '충전: <b>{point.y}</b>' },
      credits: { enabled: false },
      series: [{ name: '일자별', data: dataArr, dataLabels: { enabled: true, format: '{point.y}' } }]
    });
  }

  function drawUsedColumnChart(categories, dataArr, subtitleText) {
    Highcharts.chart('chart-used', {
      chart: { type: 'column' },
      title: { text: '사용 음표 추이(일)' },
      subtitle: { text: subtitleText || '' },
      xAxis: { categories: categories, title: { text: '일자' } },
      yAxis: { title: { text: '사용 음표(개)' }, min: 0 },
      legend: { enabled: false },
      tooltip: { headerFormat: '일자: <b>{point.key}</b><br/>', pointFormat: '사용: <b>{point.y}</b>' },
      credits: { enabled: false },
      series: [{ name: '일자별', data: dataArr, dataLabels: { enabled: true, format: '{point.y}' } }]
    });
  }

  function drawRevenueColumnChart(categories, dataArr, subtitleText) {
    Highcharts.chart('chart-revenue', {
      chart: { type: 'column' },
      title: { text: '음표 수익 추이(일)' },
      subtitle: { text: subtitleText || '' },
      xAxis: { categories: categories, title: { text: '일자' } },
      yAxis: { title: { text: '수익(코인 기준)' }, min: 0 },
      legend: { enabled: false },
      tooltip: { headerFormat: '일자: <b>{point.key}</b><br/>', pointFormat: '수익: <b>{point.y}</b>' },
      credits: { enabled: false },
      series: [{ name: '일자별', data: dataArr, dataLabels: { enabled: true, format: '{point.y}' } }]
    });
  }

  function drawNewMembersColumnChart(categories, dataArr, subtitleText) {
    Highcharts.chart('chart-new-members', {
      chart: { type: 'column' },
      title: { text: '신규가입 회원 수(일)' },
      subtitle: { text: subtitleText || '' },
      xAxis: { categories: categories, title: { text: '일자' } },
      yAxis: { title: { text: '신규가입(명)' }, min: 0 },
      legend: { enabled: false },
      tooltip: { headerFormat: '일자: <b>{point.key}</b><br/>', pointFormat: '신규가입: <b>{point.y}</b>' },
      credits: { enabled: false },
      series: [{ name: '일자별', data: dataArr, dataLabels: { enabled: true, format: '{point.y}' } }]
    });
  }

  function drawActiveRatioDonut(active, inactive, subtitleText) {
    Highcharts.chart('chart-active-ratio', {
      chart: { type: 'pie' },
      title: { text: '활성/비활성 이용자 비율' },
      subtitle: { text: subtitleText || '' },
      plotOptions: { pie: { innerSize: '60%', dataLabels: { enabled: true, format: '{point.name}: {point.y}' } } },
      credits: { enabled: false },
      series: [{
        name: '이용자',
        data: [
          { name: '활성',   y: Number(active)   || 0 },
          { name: '비활성', y: Number(inactive) || 0 }
        ]
      }]
    });
  }

  function drawHourlyVisitorsLineChart(categories, dataArr, subtitleText) {
    Highcharts.chart('chart-hourly-visitors', {
      chart: { type: 'line' },
      title: { text: '시간별 방문자 현황' },
      subtitle: { text: subtitleText || '' },
      xAxis: { categories: categories, title: { text: '시간' } },
      yAxis: { title: { text: '방문자(명)' }, min: 0 },
      legend: { enabled: false },
      tooltip: { headerFormat: '시간: <b>{point.key}</b><br/>', pointFormat: '방문자: <b>{point.y}</b>' },
      credits: { enabled: false },
      series: [{ name: '시간별', data: dataArr, dataLabels: { enabled: true, format: '{point.y}' } }]
    });
  }

  function drawDailyVisitorsColumnChart(categories, dataArr, subtitleText) {
    Highcharts.chart('chart-daily-visitors', {
      chart: { type: 'column' },
      title: { text: '일자별 방문자 현황' },
      subtitle: { text: subtitleText || '' },
      xAxis: { categories: categories, title: { text: '일자' } },
      yAxis: { title: { text: '방문자(명)' }, min: 0 },
      legend: { enabled: false },
      tooltip: { headerFormat: '일자: <b>{point.key}</b><br/>', pointFormat: '방문자: <b>{point.y}</b>' },
      credits: { enabled: false },
      series: [{ name: '일자별', data: dataArr, dataLabels: { enabled: true, format: '{point.y}' } }]
    });
  }

  // ===== 데이터 로더 =====
  function loadSummaryCards() {
    return withAuthRetry(function () {
      return ajaxGet(contextPath + '/api/admin/stats/summary', buildQueryParams());
    }).done(function (resp) {
      var data = unwrapData(resp) || {};
      var sumCharged = (data.sumChargedCoin != null) ? data.sumChargedCoin : 0;
      var sumRevenue = (data.sumRevenue     != null) ? data.sumRevenue     : 0;
      var sumUsed    = (data.sumUsedCoin    != null) ? data.sumUsedCoin    : 0;
      $('#sumChargedCoin').text(formatNumber(sumCharged));
      $('#sumRevenue').text(formatNumber(sumRevenue));
      $('#sumUsedCoin').text(formatNumber(sumUsed));
    });
  }

  function loadChargedSeriesChart() {
    return withAuthRetry(function () {
      return ajaxGet(contextPath + '/api/admin/stats/series/charged', buildQueryParams());
    }).done(function (resp) {
      var rows = unwrapData(resp) || [];
      var categories = [], seriesData = [];
      if (rows && rows.length) {
        $.each(rows, function (_i, row) {
          categories.push(row.bucket);
          seriesData.push(Number(row.value) || 0);
        });
      }
      drawChargedColumnChart(categories, seriesData, buildSubtitle('음표'));
    });
  }

  function loadUsedSeriesChart() {
    return withAuthRetry(function () {
      return ajaxGet(contextPath + '/api/admin/stats/series/used', buildQueryParams());
    }).done(function (resp) {
      var rows = unwrapData(resp) || [];
      var categories = [], seriesData = [];
      if (rows && rows.length) {
        $.each(rows, function (_i, row) {
          categories.push(row.bucket);
          seriesData.push(Number(row.value) || 0);
        });
      }
      drawUsedColumnChart(categories, seriesData, buildSubtitle('음표'));
    });
  }

  function loadRevenueSeriesChart() {
    return withAuthRetry(function () {
      return ajaxGet(contextPath + '/api/admin/stats/series/revenue', buildQueryParams());
    }).done(function (resp) {
      var rows = unwrapData(resp) || [];
      var categories = [], seriesData = [];
      if (rows && rows.length) {
        $.each(rows, function (_i, row) {
          categories.push(row.bucket);
          seriesData.push(Number(row.value) || 0);
        });
      }
      drawRevenueColumnChart(categories, seriesData, buildSubtitle('코인'));
    });
  }

  function loadTopChargersTable() {
    return withAuthRetry(function () {
      return ajaxGet(contextPath + '/api/admin/stats/top/chargers', buildQueryParams());
    }).done(function (resp) {
      var rows = unwrapData(resp) || [];
      var $tbody = $('#tbl-top-chargers').empty();
      if (rows && rows.length) {
        $.each(rows, function (i, row) {
          var $tr = $('<tr/>');
          $('<td/>').text(i + 1).appendTo($tr);
          $('<td/>').addClass('nickname').text(row.nickname || '').appendTo($tr);
          $('<td/>').addClass('text-end').text(formatNumber(row.totalcoin)).appendTo($tr);
          $tbody.append($tr);
        });
      } else {
        $tbody.append('<tr><td colspan="3" class="text-center text-muted">데이터 없음</td></tr>');
      }
    });
  }

  function loadTopSpendersTable() {
    return withAuthRetry(function () {
      return ajaxGet(contextPath + '/api/admin/stats/top/spenders', buildQueryParams());
    }).done(function (resp) {
      var rows = unwrapData(resp) || [];
      var $tbody = $('#tbl-top-spenders').empty();
      if (rows && rows.length) {
        $.each(rows, function (i, row) {
          var $tr = $('<tr/>');
          $('<td/>').text(i + 1).appendTo($tr);
          $('<td/>').addClass('nickname').text(row.nickname || '').appendTo($tr);
          $('<td/>').addClass('text-end').text(formatNumber(row.usedcoin)).appendTo($tr);
          $tbody.append($tr);
        });
      } else {
        $tbody.append('<tr><td colspan="3" class="text-center text-muted">데이터 없음</td></tr>');
      }
    });
  }

  function loadTopMusicTable() {
    return withAuthRetry(function () {
      return ajaxGet(contextPath + '/api/admin/stats/top/music', buildQueryParams());
    }).done(function (resp) {
      var rows = unwrapData(resp) || [];
      var $tbody = $('#tbl-top-music').empty();
      if (rows && rows.length) {
        $.each(rows, function (i, row) {
          var $tr = $('<tr/>');
          $('<td/>').text(i + 1).appendTo($tr);
          $('<td/>').addClass('musicid').text(row.musicid).appendTo($tr);
          $('<td/>').addClass('text-end').text(formatNumber(row.soldcount)).appendTo($tr);
          $('<td/>').addClass('text-end').text(formatNumber(row.coinsum)).appendTo($tr);
          $tbody.append($tr);
        });
      } else {
        $tbody.append('<tr><td colspan="4" class="text-center text-muted">데이터 없음</td></tr>');
      }
    });
  }

  function loadNewMembersChart() {
    return withAuthRetry(function () {
      return ajaxGet(contextPath + '/api/admin/stats/members/new', buildQueryParams());
    }).done(function (resp) {
      var rows = unwrapData(resp) || [];
      var categories = [], values = [];
      if (rows && rows.length) {
        $.each(rows, function (_i, row) {
          categories.push(row.bucket);
          values.push(Number(row.value) || 0);
        });
      }
      drawNewMembersColumnChart(categories, values, buildSubtitle('명'));
    });
  }

  function loadActiveRatioChart() {
    return withAuthRetry(function () {
      return ajaxGet(contextPath + '/api/admin/stats/members/active-ratio', buildQueryParams());
    }).done(function (resp) {
      var data = unwrapData(resp) || {};
      var active   = data.active   ?? 0;
      var inactive = data.inactive ?? 0;
      drawActiveRatioDonut(active, inactive, buildSubtitle('명'));
    });
  }

  function loadTopFollowedTable() {
    return withAuthRetry(function () {
      return ajaxGet(contextPath + '/api/admin/stats/members/top-followed', buildQueryParams());
    }).done(function (resp) {
      var rows = unwrapData(resp) || [];
      var $tbody = $('#tbl-top-followed').empty();
      if (rows && rows.length) {
        $.each(rows, function (i, row) {
          var $tr = $('<tr/>');
          $('<td/>').text(i + 1).appendTo($tr);
          $('<td/>').addClass('nickname').text(row.nickname || '').appendTo($tr);
          $('<td/>').addClass('text-end').text(formatNumber(row.followers)).appendTo($tr);
          $tbody.append($tr);
        });
      } else {
        $tbody.append('<tr><td colspan="3" class="text-center text-muted">데이터 없음</td></tr>');
      }
    });
  }

  function loadHourlyVisitorsChart() {
    return withAuthRetry(function () {
      return ajaxGet(contextPath + '/api/admin/stats/visits/hourly', buildQueryParams());
    }).done(function (resp) {
      var rows = unwrapData(resp) || [];
      var categories = [], values = [];
      if (rows && rows.length) {
        $.each(rows, function (_i, row) {
          categories.push(row.bucket); // '00' ~ '23'
          values.push(Number(row.value) || 0);
        });
      }
      drawHourlyVisitorsLineChart(categories, values, buildSubtitle('명'));
    });
  }

  function loadDailyVisitorsChart() {
    return withAuthRetry(function () {
      return ajaxGet(contextPath + '/api/admin/stats/visits/daily', buildQueryParams());
    }).done(function (resp) {
      var rows = unwrapData(resp) || [];
      var categories = [], values = [];
      if (rows && rows.length) {
        $.each(rows, function (_i, row) {
          categories.push(row.bucket);
          values.push(Number(row.value) || 0);
        });
      }
      drawDailyVisitorsColumnChart(categories, values, buildSubtitle('명'));
    });
  }

  function loadTotalUsersCard() {
    return withAuthRetry(function () {
      return ajaxGet(contextPath + '/api/admin/stats/visits/summary', buildQueryParams());
    }).done(function (resp) {
      var data = unwrapData(resp) || {};
      var total = data.totalUsers ?? 0;
      $('#totalUsers').text(formatNumber(total));
    });
  }

  function loadTopGiftedMusicTable() {
    return withAuthRetry(function () {
      return ajaxGet(contextPath + '/api/admin/stats/music/top-gifted', buildQueryParams());
    }).done(function (resp) {
      var rows = unwrapData(resp) || [];
      var $tbody = $('#tbl-top-gifted').empty();
      if (rows && rows.length) {
        $.each(rows, function (i, row) {
          var $tr = $('<tr/>');
          $('<td/>').text(i + 1).appendTo($tr);
          $('<td/>').addClass('musicid').text(row.musicid).appendTo($tr);
          $('<td/>').addClass('text-end').text(formatNumber(row.giftcount)).appendTo($tr);
          $('<td/>').addClass('text-end').text(formatNumber(row.coinsum)).appendTo($tr);
          $tbody.append($tr);
        });
      } else {
        $tbody.append('<tr><td colspan="4" class="text-center text-muted">데이터 없음</td></tr>');
      }
    });
  }

  // ===== 일괄 로드 =====
  function loadAllWidgets() {
    var start = $('#startDate').val();
    var end   = $('#endDate').val();
    if (start && end && start > end) {
      alert('시작일이 종료일보다 큽니다.');
      return;
    }

    // [신규] 최근 7일 고정 차트 (Highcharts 이중 Y축 포맷)
    loadWeeklyUsedVsChargedChart();

    // 거래·매출
    loadSummaryCards();
    loadChargedSeriesChart();
    loadUsedSeriesChart();
    loadRevenueSeriesChart();
    loadTopChargersTable();
    loadTopSpendersTable();

    // 회원
    loadNewMembersChart();
    loadActiveRatioChart();
    loadTopFollowedTable();

    // 이용
    loadHourlyVisitorsChart();
    loadDailyVisitorsChart();
    loadTotalUsersCard();

    // 콘텐츠
    loadTopMusicTable();
    loadTopGiftedMusicTable();
  }

  // ===== 초기 바인딩 =====
  $(function () {
    // 빠른 날짜 범위 버튼
    $('#btnToday').click(function () { setTodayRange();       loadAllWidgets(); });
    $('#btnWeek').click(function ()  { setThisWeekRange();    loadAllWidgets(); });
    $('#btnMonth').click(function () { setThisMonthRange();   loadAllWidgets(); });
    $('#btn7').click(function ()     { setLastNDaysRange(7);  loadAllWidgets(); });
    $('#btn30').click(function ()    { setLastNDaysRange(30); loadAllWidgets(); });

    // 검색/리셋
    $('#btnSearch').click(loadAllWidgets);
    $('#btnReset').click(function () {
      $('#startDate').val('');
      $('#endDate').val('');
      loadAllWidgets();
    });

    // 기본값: 오늘(일간) — 주간차트는 별도로 최근 7일 고정
    setTodayRange();
    loadAllWidgets();
  });
})();
