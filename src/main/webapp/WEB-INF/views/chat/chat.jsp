<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>채팅 테스트</title>
    <meta charset="UTF-8">
    <!-- axios / sockjs / stomp -->
    <script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/sockjs-client/dist/sockjs.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/stompjs/lib/stomp.min.js"></script>
    <style>
        body { font-family: Arial, sans-serif; margin: 30px; }
        #chatBox { border: 1px solid #ddd; height: 300px; overflow-y: auto; padding: 10px; }
        .msg { margin: 5px 0; }
        .me { color: blue; }
        .other { color: green; }
    </style>
</head>
<jsp:include page="../include/common/head.jsp" />
<body>
<h2>채팅 테스트</h2>

<div>
    <label>채팅방 ID: </label>
    <input type="text" id="roomIdInput" placeholder="roomId 입력"/>
    <button onclick="connectRoom()">방 연결</button>
</div>

<div id="chatBox"></div>

<div style="margin-top:10px;">
    <input type="text" id="messageInput" placeholder="메시지 입력" style="width:250px"/>
    <button onclick="send()">전송</button>
</div>

<script src="${pageContext.request.contextPath}/js/chat/chat.js" defer></script>

</body>
</html>
