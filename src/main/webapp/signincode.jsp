<%@ page language="java" contentType="text/html; charset=utf-8"  
    pageEncoding="utf-8"%> 
<%@   page   import= "db.*,java.sql.* "%> 
<%     
   String name = request.getParameter("user");  
   String pass = request.getParameter("password");  
   if (!SetupDatabase.hasSet) {
	    SetupDatabase.Setup(); //数据库还未建立时先建立数据库
   } 
   int signinCode = PlayerManager.checkLogin(name, pass); 
   //boolean isLoginSucc = name.equals("abs") && pass.equals("123456");
   if(signinCode == 0)  
   {  
       out.println("<script>alert('登陆成功！');window.location.href='index.jsp'</script>");  
       session.setAttribute("user", name);  
       session.setMaxInactiveInterval(60);  
   }  
   else  
   { 
       out.println("<script>alert('登陆失败！');window.location.href='signin.jsp'</script>");  
   }  
%>  