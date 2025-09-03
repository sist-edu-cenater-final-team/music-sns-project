(function () {
	var ctxPath =
		window.ctxPath ||
		(document.querySelector('meta[name="ctxPath"]') &&
			document.querySelector('meta[name="ctxPath"]').content) ||
		'';
	var basePath = ctxPath;

	// Highcharts 공통
	if (window.Highcharts) {
		Highcharts.setOptions({
			colors: ['#6633FF', '#22C55E', '#F59E0B', '#3B82F6', '#EC4899', '#14B8A6'],
			chart: {
				backgroundColor: 'transparent',
				spacing: [16, 16, 16, 16], 
				style: { fontFamily: "'Segoe UI', Apple SD Gothic Neo, Pretendard, Roboto, sans-serif" }
			},
			title: { style: { color: '#0F172A', fontWeight: '800', fontSize: '16px' } },
			subtitle: { style: { color: '#475569', fontSize: '12px' } },
			xAxis: {
				lineColor: '#E5E7EB',
				tickColor: '#E5E7EB',
				labels: { style: { color: '#475569' } }
			},
			yAxis: {
				gridLineColor: '#EEF2F7',
				title: { style: { color: '#475569', fontWeight: 700 } },
				labels: { style: { color: '#475569' } }
			},
			legend: {
				itemStyle: { color: '#334155', fontWeight: 600 },
				itemHoverStyle: { color: '#111827' },
				margin: 12
			},
			tooltip: {
				borderColor: '#E5E7EB',
				backgroundColor: '#FFFFFF',
				style: { color: '#0F172A', fontWeight: 600 }
			},
			plotOptions: {
				series: {
					marker: { radius: 3, symbol: 'circle' },
					lineWidth: 3,
					softThreshold: true,
					states: { hover: { lineWidthPlus: 0 } },
					dataLabels: {
						enabled: true,
						allowOverlap: false,
						padding: 2,
						y: -6, 
						crop: true,
						overflow: 'justify',
						style: { textOutline: 'none', fontWeight: 700 },
						formatter: function () {
							if (!this.y) return '';
							return Highcharts.numberFormat(this.y, 0);
						}
					}
				},
				line: {
					enableMouseTracking: true
				},
				pie: {
					dataLabels: {
						style: { color: '#0F172A', textOutline: 'none', fontWeight: 700 }
					}
				}
			},
			credits: { enabled: false }
		});
	}

	// 로그인 체크 및 공통
	var isAuthAlertShown = false;
	var isLoginRedirecting = false;

	function alertOnce(msg) {
		if (!isAuthAlertShown) {
			isAuthAlertShown = true;
			alert(msg);
		}
	}
	function redirectLoginOnce() {
		if (!isLoginRedirecting) {
			isLoginRedirecting = true;
			location.replace(ctxPath + '/auth/login');
		}
	}

	// 미로그인시 로그인 화면으로 보내기
	try {
		var savedToken = localStorage.getItem('accessToken');
		if (savedToken == null || savedToken == '') {
			alertOnce('로그인이 필요합니다.');
			redirectLoginOnce();
			return;
		}
	} catch (e) {}

	// 인증 헤더 만들기
	function buildAuthHeaders() {
		try {
			var appHeaders =
				window.AuthFunc &&
				window.AuthFunc.getAuthHeader &&
				window.AuthFunc.getAuthHeader();
			if (appHeaders && typeof appHeaders == 'object' && Object.keys(appHeaders).length > 0) {
				if (!('X-Requested-With' in appHeaders)) appHeaders['X-Requested-With'] = 'XMLHttpRequest';
				return appHeaders;
			}
		} catch (e) {}
		var token = localStorage.getItem('accessToken');
		var type = localStorage.getItem('tokenType') || 'Bearer';
		var headers = { 'X-Requested-With': 'XMLHttpRequest' };
		if (token) headers.Authorization = type + ' ' + token;
		return headers;
	}

	// 토큰 갱신
	var refreshGatePromise = null;
	function refreshAuthIfNeeded() {
		if (refreshGatePromise != null) return refreshGatePromise;
		var dfd = $.Deferred();
		refreshGatePromise = dfd.promise();
		var refreshFn = window.refreshAuthToken;

		if (typeof refreshFn != 'function') {
			dfd.reject();
			refreshGatePromise = null;
			return dfd.promise();
		}

		$.when(refreshFn())
			.done(() => dfd.resolve())
			.fail(() => dfd.reject())
			.always(() => { refreshGatePromise = null; });

		return dfd.promise();
	}

	// HTML 응답 감지 (세션만료 페이지)
	function isHtmlResponse(xhr) {
		try {
			var ct = xhr && xhr.getResponseHeader && xhr.getResponseHeader('content-type');
			return !!(ct && ct.indexOf('text/html') != -1);
		} catch (e) { return false; }
	}

	// 인증 오류 처리
	function onUnauthorized() {
		try { localStorage.removeItem('accessToken'); localStorage.removeItem('tokenType'); } catch (e) {}
		alertOnce('세션이 만료되었습니다. 다시 로그인해주세요.');
		redirectLoginOnce();
	}
	function onForbidden() {
		alertOnce('관리자 권한이 필요합니다.');
		redirectLoginOnce();
	}

	// GET 요청 보내기
	function getJson(url, params) {
		return $.ajax({ url: url, type: 'GET', data: params || {}, dataType: 'json', headers: buildAuthHeaders() });
	}

	// 토큰 만료시, 한 번만 새 토큰 받고, 같은 요청 다시 시도
	function requestWithAuthRetry(runAjax) {
		var dfd = $.Deferred();
		var retried = false;

		(function exec() {
			var gate = refreshGatePromise != null ? refreshGatePromise : $.Deferred().resolve().promise();
			gate
				.done(function () {
					runAjax()
						.done(function (data, _text, xhr) {
							if (isHtmlResponse(xhr)) { onUnauthorized(); dfd.reject(xhr); return; }
							dfd.resolve(data);
						})
						.fail(function (xhr) {
							if (isHtmlResponse(xhr)) { onUnauthorized(); dfd.reject(xhr); return; }
							if (xhr && xhr.status == 401 && !retried) {
								retried = true;
								refreshAuthIfNeeded().done(exec).fail(function () { onUnauthorized(); dfd.reject(xhr); });
								return;
							}
							if (xhr && xhr.status == 403) { onForbidden(); dfd.reject(xhr); return; }
							console.warn('요청 실패', xhr && xhr.status);
							dfd.reject(xhr);
						});
				})
				.fail(function () { onUnauthorized(); dfd.reject(); });
		})();
		return dfd.promise();
	}

	function getBody(resp) {
		return (
			(resp && resp.success && resp.success.responseData) ||
			(resp && resp.responseData) ||
			resp
		);
	}

	// 숫자, 날짜 포맷
	function formatNumber(v) {
		try { return Number(v || 0).toLocaleString(); } catch (e) { return (v || 0) + ''; }
	}
	function formatDate(d) {
		var y = d.getFullYear();
		var m = ('0' + (d.getMonth() + 1)).slice(-2);
		var day = ('0' + d.getDate()).slice(-2);
		return y + '-' + m + '-' + day;
	}

	// 최근 7일 기본 범위
	function getLast7Days() {
		var today = new Date();
		var keys = [], labels = [];
		for (var i = 6; i >= 0; i--) {
			var d = new Date(today);
			d.setDate(today.getDate() - i);
			var y = d.getFullYear();
			var m = ('0' + (d.getMonth() + 1)).slice(-2);
			var dd = ('0' + d.getDate()).slice(-2);
			keys.push(y + '-' + m + '-' + dd);
			labels.push(m + '/' + dd);
		}
		return { keys: keys, labels: labels, start: keys[0], end: keys[6] };
	}

	// 입력된 범위가 있으면 그 범위를 사용, 없으면 최근 7일 사용
	function getActiveRange() {
		var s = ($('#startDate').val() || '').trim();
		var e = ($('#endDate').val() || '').trim();
		if (!s || !e) return getLast7Days(); // 기본 7일

		var start = new Date(s), end = new Date(e);
		// 일자 리스트 생성
		var keys = [], labels = [];
		var cur = new Date(start);
		while (cur.getTime() <= end.getTime()) {
			var y = cur.getFullYear();
			var m = ('0' + (cur.getMonth() + 1)).slice(-2);
			var dd = ('0' + cur.getDate()).slice(-2);
			keys.push(y + '-' + m + '-' + dd);
			labels.push(m + '/' + dd);
			cur.setDate(cur.getDate() + 1);
		}
		return { keys: keys, labels: labels, start: s, end: e };
	}

	function rowsToMap(rows) {
		var map = {};
		(rows || []).forEach(function (r) { map[r.bucket] = Number(r.value || 0) || 0; });
		return map;
	}

	// 차트 렌더링
	function renderLineChart(containerId, titleText, subtitleText, categories, data, yAxisTitle) {
		Highcharts.chart(containerId, {
			chart: { type: 'line' },
			title: { text: titleText },
			subtitle: { text: subtitleText || '' },
			xAxis: { categories: categories },
			yAxis: { title: { text: yAxisTitle }, min: 0 },
			tooltip: { shared: true, valueDecimals: 0 },
			series: [{ name: titleText, data: data }]
		});
	}

	// 오늘
	function setToday() {
		var t = new Date();
		$('#startDate').val(formatDate(t));
		$('#endDate').val(formatDate(t));
	}
	// 이번 주 (월~일)
	function setThisWeek() {
		var now = new Date();
		var dow = now.getDay();
		var move = dow == 0 ? -6 : 1 - dow;
		var monday = new Date(now); monday.setDate(now.getDate() + move);
		var sunday = new Date(monday); sunday.setDate(monday.getDate() + 6);
		$('#startDate').val(formatDate(monday));
		$('#endDate').val(formatDate(sunday));
	}
	// 이번 달 (1일~말일)
	function setThisMonth() {
		var now = new Date();
		var first = new Date(now.getFullYear(), now.getMonth(), 1);
		var last = new Date(now.getFullYear(), now.getMonth() + 1, 0);
		$('#startDate').val(formatDate(first));
		$('#endDate').val(formatDate(last));
	}
	// 최근 N일
	function setLastNDays(n) {
		var end = new Date();
		var start = new Date(); start.setDate(end.getDate() - (n - 1));
		$('#startDate').val(formatDate(start));
		$('#endDate').val(formatDate(end));
	}

	// 쿼리 파라미터 구성
	function getQueryParams() {
		var p = {};
		var s = $('#startDate').val();
		var e = $('#endDate').val();
		if (s) p.startDate = s;
		if (e) p.endDate = e;
		return p;
	}

	// 차트 부제목
	function buildSubtitleBy(range, unit) {
		return '기간: ' + range.start + ' ~ ' + range.end + (unit ? ' · 단위: ' + unit : '');
	}

	// 합계 카드
	function loadSummary() {
		return requestWithAuthRetry(function () {
			return getJson(ctxPath + '/api/admin/stats/summary', getQueryParams());
		}).done(function (resp) {
			var data = getBody(resp) || {};
			$('#sumChargedCoin').text(formatNumber(data.sumChargedCoin || 0));
			$('#sumRevenue').text(formatNumber(data.sumRevenue || 0));
			$('#sumUsedCoin').text(formatNumber(data.sumUsedCoin || 0));
		});
	}

	// 일자별 충전 음표
	function loadChargedDaily() {
		var range = getActiveRange();
		return requestWithAuthRetry(function () {
			return getJson(ctxPath + '/api/admin/stats/series/charged', { startDate: range.start, endDate: range.end });
		}).done(function (resp) {
			var map = rowsToMap(getBody(resp) || []);
			var values = range.keys.map(function (k) { return map[k] || 0; });
			renderLineChart('chart-charged', '충전 음표', buildSubtitleBy(range, '개'), range.labels, values, '개');
		});
	}

	// 일자별 사용 음표
	function loadUsedDaily() {
		var range = getActiveRange();
		return requestWithAuthRetry(function () {
			return getJson(ctxPath + '/api/admin/stats/series/used', { startDate: range.start, endDate: range.end });
		}).done(function (resp) {
			var map = rowsToMap(getBody(resp) || []);
			var values = range.keys.map(function (k) { return map[k] || 0; });
			renderLineChart('chart-used', '사용 음표', buildSubtitleBy(range, '개'), range.labels, values, '개');
		});
	}

	// 일자별 수익
	function loadRevenueDaily() {
		var el = document.getElementById('chart-revenue');
		if (el == null) return;
		var range = getActiveRange();
		return requestWithAuthRetry(function () {
			return getJson(ctxPath + '/api/admin/stats/series/revenue', { startDate: range.start, endDate: range.end });
		}).done(function (resp) {
			var map = rowsToMap(getBody(resp) || []);
			var values = range.keys.map(function (k) { return map[k] || 0; });
			renderLineChart('chart-revenue', '수익', buildSubtitleBy(range, '원'), range.labels, values, '원');
		});
	}

	// 음표 충전 Top 10
	function loadTopChargers() {
		return requestWithAuthRetry(function () {
			return getJson(basePath + '/api/admin/stats/top/chargers', getQueryParams());
		}).done(function (resp) {
			var rows = getBody(resp) || [];
			var $tbody = $('#tbl-top-chargers').empty();
			if (rows.length == 0) {
				$tbody.append('<tr><td colspan="3" class="text-center text-muted">데이터 없음</td></tr>');
				return;
			}
			rows.forEach(function (row, i) {
				var $tr = $('<tr/>');
				$('<td/>').text(i + 1).appendTo($tr);
				$('<td/>').addClass('nickname').text(row.nickname || '').appendTo($tr);
				var total = row.totalcoin != null ? row.totalcoin : row.totalCoin;
				$('<td/>').addClass('text-end').text(formatNumber(total)).appendTo($tr);
				$tbody.append($tr);
			});
		});
	}

	// 음표 사용 Top 10
	function loadTopSpenders() {
		return requestWithAuthRetry(function () {
			return getJson(basePath + '/api/admin/stats/top/spenders', getQueryParams());
		}).done(function (resp) {
			var rows = getBody(resp) || [];
			var $tbody = $('#tbl-top-spenders').empty();
			if (rows.length == 0) {
				$tbody.append('<tr><td colspan="3" class="text-center text-muted">데이터 없음</td></tr>');
				return;
			}
			rows.forEach(function (row, i) {
				var $tr = $('<tr/>');
				$('<td/>').text(i + 1).appendTo($tr);
				$('<td/>').addClass('nickname').text(row.nickname || '').appendTo($tr);
				var used = row.usedcoin != null ? row.usedcoin : row.usedCoin;
				$('<td/>').addClass('text-end').text(formatNumber(used)).appendTo($tr);
				$tbody.append($tr);
			});
		});
	}

	// 베스트셀러 음악 Top 10
	function loadTopMusic() {
		return requestWithAuthRetry(function () {
			return getJson(basePath + '/api/admin/stats/top/music', getQueryParams());
		}).done(function (resp) {
			var rows = getBody(resp) || [];
			var $tbody = $('#tbl-top-music').empty();
			if (rows.length == 0) {
				$tbody.append('<tr><td colspan="4" class="text-center text-muted">데이터 없음</td></tr>');
				return;
			}
			rows.forEach(function (row, i) {
				var $tr = $('<tr/>');
				$('<td/>').text(i + 1).appendTo($tr);
				var title = row.title || row.musicName || row.musicname || row.music_title || row.musicid;
				var artist = row.artist || row.artistName || row.artist_name || '';
				var display = artist ? title + ' - ' + artist : title;
				$('<td/>').addClass('musicid').text(display).appendTo($tr);
				$('<td/>').addClass('text-end').text(formatNumber(row.soldcount)).appendTo($tr);
				$('<td/>').addClass('text-end').text(formatNumber(row.coinsum)).appendTo($tr);
				$tbody.append($tr);
			});
		});
	}

	// 일자별 신규 가입
	function loadNewMembersDaily() {
		var el = document.getElementById('chart-new-members');
		if (el == null) return;
		var range = getActiveRange();
		return requestWithAuthRetry(function () {
			return getJson(ctxPath + '/api/admin/stats/series/new-members', { startDate: range.start, endDate: range.end });
		}).done(function (resp) {
			var map = rowsToMap(getBody(resp) || []);
			var values = range.keys.map(function (k) { return map[k] || 0; });
			renderLineChart('chart-new-members', '신규 가입', buildSubtitleBy(range, '명'), range.labels, values, '명');
		});
	}

	// 시간별 방문자 (선택 기간 합산)
	function loadHourlyVisitors() {
		var el = document.getElementById('chart-hourly-visitors');
		if (el == null) return;
		var categories = []; for (var i = 0; i < 24; i++) categories.push(('0' + i).slice(-2));
		var range = getActiveRange();
		return requestWithAuthRetry(function () {
			return getJson(ctxPath + '/api/admin/stats/visits/hourly', { startDate: range.start, endDate: range.end });
		}).done(function (resp) {
			var rows = getBody(resp) || [];
			var map = {}; rows.forEach(function (r) { map[r.bucket] = Number(r.value || 0); });
			var values = categories.map(function (h) { return map[h] || 0; });
			renderLineChart('chart-hourly-visitors', '시간별 방문자', buildSubtitleBy(range, '명'), categories, values, '명');
		});
	}

	// 일자별 방문자
	function loadDailyVisitors() {
		var el = document.getElementById('chart-daily-visitors');
		if (el == null) return;
		var range = getActiveRange();
		return requestWithAuthRetry(function () {
			return getJson(ctxPath + '/api/admin/stats/visits/daily', { startDate: range.start, endDate: range.end });
		}).done(function (resp) {
			var map = rowsToMap(getBody(resp) || []);
			var values = range.keys.map(function (k) { return map[k] || 0; });
			renderLineChart('chart-daily-visitors', '일자별 방문자', buildSubtitleBy(range, '명'), range.labels, values, '명');
		});
	}

	// 전체 이용자 수
	function loadTotalUsersCard() {
		var el = document.getElementById('totalUsers');
		if (el == null) return;
		return requestWithAuthRetry(function () {
			return getJson(ctxPath + '/api/admin/stats/visits/summary', {});
		}).done(function (resp) {
			var data = getBody(resp) || {};
			$('#totalUsers').text(formatNumber(data.totalUsers || 0));
		});
	}

	// 전체 데이터 로드
	function loadAll() {
		var s = $('#startDate').val();
		var e = $('#endDate').val();
		if (s && e && s > e) { alert('시작일이 종료일보다 큽니다.'); return; }

		loadSummary();
		loadChargedDaily();
		loadUsedDaily();
		loadRevenueDaily();
		loadTopChargers();
		loadTopSpenders();
		loadTopMusic();
		loadNewMembersDaily();
		loadHourlyVisitors();
		loadDailyVisitors();
		loadTotalUsersCard();
	}

	$(function () {
		$('#btnToday').click(function () { setToday(); loadAll(); });
		$('#btnWeek').click(function () { setThisWeek(); loadAll(); });
		$('#btnMonth').click(function () { setThisMonth(); loadAll(); });
		$('#btn7').click(function () { setLastNDays(7); loadAll(); });
		$('#btn30').click(function () { setLastNDays(30); loadAll(); });
		$('#btnSearch').click(loadAll);
		$('#btnReset').click(function () { $('#startDate').val(''); $('#endDate').val(''); loadAll(); });

		setToday();
		loadAll();
	});
})();
