<%--
  Created by IntelliJ IDEA.
  User: sihu
  Date: 25. 8. 26.
  Time: 오후 3:16
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>
<script>
    const provider = '${provider.name().toLowerCase()}'
    const providerValue = '${provider.value}'
</script>
<script src="${pageContext.request.contextPath}/js/auth/oauth/callback.js"></script>
<html>
<head>
    <title>${provider.value} 소셜 로그인</title>
</head>
<body>

</body>
</html>
