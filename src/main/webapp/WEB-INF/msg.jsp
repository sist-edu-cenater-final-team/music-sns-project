<%-- 20250605(Thu) --%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<script type="text/javascript">

	alert("${requestScope.message}");      // 메시지 출력해주기
	location.href = "${requestScope.loc}"; // 페이지 이동 

	if( ${not empty requestScope.popup_close && requestScope.popup_close == true} ) {
		 
		//	opener.location.reload(true); // 부모창 새로고침
			opener.history.go(0);         // 부모창 새로고침
			self.close(); // 팝업창 닫기 
	}
	
</script>    
