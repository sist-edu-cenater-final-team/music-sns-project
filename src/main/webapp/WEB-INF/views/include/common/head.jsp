<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<% String ctxPath = request.getContextPath(); %>
<head>

    <title>muodle</title>

    <script src="<%=ctxPath%>/lib/jquery-3.7.1.min.js"></script>
    <script src="<%=ctxPath%>/js/auth/token.js"></script>
    <c:choose>
        <c:when test="${not empty boot}">
            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
            <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        </c:when>
        <c:otherwise>
            <script src="<%=ctxPath%>/lib/bootstrap-4.6.2-dist/js/bootstrap.bundle.min.js"></script>
            <link rel="stylesheet" href="<%=ctxPath%>/lib/bootstrap-4.6.2-dist/css/bootstrap.min.css" />
        </c:otherwise>
    </c:choose>

    <%-- 부트스트랩 아이콘스--%>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">


    <!-- TUI CSS (jsDelivr로 교체) -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/tui-color-picker@2.2.8/dist/tui-color-picker.min.css" />
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/tui-image-editor@3.15.3/dist/tui-image-editor.min.css" />

    <!-- JS (순서 중요, jsDelivr로 교체) -->
    <script src="https://cdn.jsdelivr.net/npm/fabric@3.6.3/dist/fabric.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/tui-code-snippet@2.3.2/dist/tui-code-snippet.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/tui-color-picker@2.2.8/dist/tui-color-picker.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/tui-image-editor@3.15.3/dist/tui-image-editor.min.js"></script>

    <link rel="stylesheet" href="<%=ctxPath%>/css/reset.css" />
    <link rel="stylesheet" href="<%=ctxPath%>/css/common.css" />

    <script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>
    <script src="<%=ctxPath%>/js/common.js" defer></script>

    <link rel="stylesheet" href="<%=ctxPath%>/css/indexPost.css" />
    <script src="<%=ctxPath%>/js/indexPost.js"></script>

	<%-- 추후 필요하면 확인 후 추가
	<script type="text/javascript">
		axios.interceptors.request.use(function(config){
        	try {
          		const t = localStorage.getItem('ACCESS_TOKEN');
          		if (t) config.headers['Authorization'] = 'Bearer ' + t;
        	} catch (e) { }
        	return config;
		});

      	axios.interceptors.response.use(
        	function(res){ return res; },
        	function(err){
          	const status = err && err.response ? err.response.status : 0;
          	if (status === 401 || status === 403) {
            	alert("로그인이 필요합니다.");
            	window.location.href = '<%=ctxPath%>/login';
          	}
          	return Promise.reject(err);
        	}
      	);

      	$(function(){
        	$(document).ajaxSend(function(e, xhr){
          		try {
            		const t = localStorage.getItem('ACCESS_TOKEN');
           		 	if (t) xhr.setRequestHeader('Authorization', 'Bearer ' + t);
          		} catch (e) { /* noop */ }
        	});

        	$(document).ajaxError(function(e, xhr){
          		if (xhr.status === 401 || xhr.status === 403) {
            		alert("로그인이 필요합니다.");
            		location.href = '<%=ctxPath%>/login';
          		}
        	});
      	});
    </script>
    --%>
    <script>
        const ctxPath = '<%=ctxPath%>';
    </script>
</head>