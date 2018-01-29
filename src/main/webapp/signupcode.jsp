<%@ page language="java" contentType="text/html; charset=utf-8"  
    pageEncoding="utf-8"%> 
<%@ page import="db.*,java.sql.* "%> 
<%@ page import= "player.Encrypt "%> 
<%     
   String name = request.getParameter("user");  
   String pass = request.getParameter("password");  
   if (!SetupDatabase.hasSet) {
	    SetupDatabase.Setup();
   } 
   Encrypt en = new Encrypt();
   String[] strings = en.getPassword(pass);
   int logcode = PlayerManager.recordPlayer(name, strings[0], strings[1]);
   if (logcode == 0) {
	   out.println("<script>alert('注册成功，请登录！');window.location.href='sign.jsp'</script>"); 
   } else {
	   out.println("<script>alert('该用户名已被注册！');window.location.href='sign.jsp'</script>"); 
   }
%>  