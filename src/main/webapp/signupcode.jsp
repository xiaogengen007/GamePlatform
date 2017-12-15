<%@ page language="java" contentType="text/html; charset=utf-8"  
    pageEncoding="utf-8"%> 
<%@ page import="db.*,java.sql.* "%> 
<%     
   String name = request.getParameter("user");  
   String pass = request.getParameter("password");  
   if (!SetupDatabase.hasSet) {
	    SetupDatabase.Setup();
   } 
   int logcode = PlayerManager.recordPlayer(name, pass);
   if (logcode == 0) {
	   out.println("<script>alert('注册成功，请登录！');window.location.href='sign.jsp'</script>"); 
   } else {
	   out.println("<script>alert('该用户名已被注册！');window.location.href='sign.jsp'</script>"); 
   }
%>  