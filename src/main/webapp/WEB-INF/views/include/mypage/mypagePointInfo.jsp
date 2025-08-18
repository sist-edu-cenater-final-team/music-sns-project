<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"  %>

<div class="mypage-point">
    <div class="point-info">
        <p class="point-title">보유 음표</p>
        <p class="real-point">
            <span id="myCoinBalance">
                <fmt:formatNumber value="${empty myCoinBalance ? 0 : myCoinBalance}" pattern="#,##0"/>
            </span> 음표
        </p>
    </div>
    <div class="btn-form">
        <a href="javascript:;" class="btn">상세보기</a>
    </div>
</div>
