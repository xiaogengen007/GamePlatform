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
	   out.println("<script>alert('succeeded');window.location.href='signin.jsp'</script>"); 
   } else {
	   out.println("<script>alert('failed');window.location.href='signin.jsp'</script>"); 
   }
%>  