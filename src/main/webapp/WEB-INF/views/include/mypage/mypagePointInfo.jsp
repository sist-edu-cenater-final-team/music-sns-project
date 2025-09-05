<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"  %>

<%
  String ctxPath = request.getContextPath();
%>

<script type="text/javascript">
	$(function () {
	    const $coinBalanceEl = $('#myCoinBalance');
	
	    const cachedBalance = localStorage.getItem('coinBalance');
	    if (cachedBalance !== null && cachedBalance !== '') {
	        try {
	            const formatted = Number(cachedBalance).toLocaleString();
	            if ($coinBalanceEl.text().trim() !== formatted) {
	                $coinBalanceEl.text(formatted);
	            }
	        } catch (e) { }
	    }
	
	    function getAuthHeader() {
	        const accessToken = localStorage.getItem('accessToken');
	        const tokenType = localStorage.getItem('tokenType') || 'Bearer';
	        return accessToken ? { 'Authorization': tokenType + ' ' + accessToken } : {};
	    }
	
	    function updateBalanceIfChanged(nextBalance) {
	        try {
	            const formatted = Number(nextBalance).toLocaleString();
	            if ($coinBalanceEl.text().trim() !== formatted) {
	                $coinBalanceEl.text(formatted); // 값이 변한 경우에만 교체
	            }
	            localStorage.setItem('coinBalance', String(nextBalance)); // 캐시 갱신
	        } catch (e) { }
	    }
	
	    function fetchUserBalance() {
	        if (!localStorage.getItem('accessToken')) return; // 비로그인 시 요청 안 함
	
	        const contextPath =
	            window.ctxPath ||
	            document.querySelector('meta[name="ctxPath"]')?.content ||
	            '';
	
	        $.ajax({
	            url: contextPath + '/api/mypage/eumpyo/charge/balance',
	            method: 'GET',
	            headers: getAuthHeader(),
	            success: function (responseJson) {
	                const responseBody =
	                    (responseJson && responseJson.success && responseJson.success.responseData) ||
	                    responseJson.responseData ||
	                    responseJson;
	
	                const coinBalance = responseBody && (responseBody.coinBalance ?? responseBody.balance);
	
	                if (typeof coinBalance === 'number') {
	                    updateBalanceIfChanged(coinBalance);
	                }
	            },
	            error: function () {
	            }
	        });
	    }
	
	    if (window.requestIdleCallback) {
	        requestIdleCallback(fetchUserBalance);
	    } else {
	        setTimeout(fetchUserBalance, 0);
	    }
	});
</script>

<div class="mypage-point">
    <div class="point-info">
        <p class="point-title">보유 음표</p>
        <p class="real-point">
            <span
                id="myCoinBalance"
                data-initial="<c:out value='${empty myCoinBalance ? 0 : myCoinBalance}'/>">
                <fmt:formatNumber value="${empty myCoinBalance ? 0 : myCoinBalance}" pattern="#,##0"/>
            </span> 음표
        </p>
    </div>
    <div class="btn-form">
        <a href='<%= ctxPath%>/mypage/eumpyo/chargeHistory' class="btn">상세보기</a>
    </div>
</div>
