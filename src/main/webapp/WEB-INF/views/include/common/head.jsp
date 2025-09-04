<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<% String ctxPath = request.getContextPath(); %>
<head>
    <link rel="favicon" href="<%=ctxPath%>/favicon.ico" type="image/x-icon">

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


    <script>
        const ctxPath = '<%=ctxPath%>';
    </script>
</head>
