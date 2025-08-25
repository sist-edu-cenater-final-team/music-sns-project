<%--
  Created by IntelliJ IDEA.
  User: jks93
  Date: 25. 8. 19.
  Time: 오후 4:20
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<% String ctxPath= request.getContextPath(); %>

<link rel="stylesheet" href="<%=ctxPath%>/css/post/postView.css"/>
<script src="<%=ctxPath%>/js/post/postView.js"></script>



<div class="feed-container" id="feed"></div>

