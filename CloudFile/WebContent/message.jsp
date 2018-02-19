<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" isELIgnored="false"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>消息提示</title>
</head>
<body>
<body>
  ${message}
  <% 
  String message = request.getAttribute("message").toString();
  if(message.contains("文件上传成功")){
	  String randompwd = request.getAttribute("randompwd").toString();
	  out.print("<p>文件查看随机码为<span>"+randompwd+"</span></p>");
  }
	  
	  %>
</body>
</body>
</html>