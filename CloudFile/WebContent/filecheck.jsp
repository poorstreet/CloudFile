<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<form name = "filecheck" action = "${pageContext.request.contextPath}/filecheck">
请输入要查看的文件随机码<input name = "randompwd" type="text" /></br>
<input type="submit" value="查看文件">
</form>
</body>
</html>