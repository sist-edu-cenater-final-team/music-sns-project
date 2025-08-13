<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<% String ctxPath = request.getContextPath(); %>
<head>
    <title>무드으을</title>

    <script src="<%=ctxPath%>/lib/jquery-3.7.1.min.js"></script>

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


    <%--    부트스트랩 아이콘스--%>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
    <link rel="stylesheet" href="<%=ctxPath%>/css/reset.css" />
    <link rel="stylesheet" href="<%=ctxPath%>/css/common.css" />






    <script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>
    <script src="<%=ctxPath%>/js/common.js" defer></script>
</head>