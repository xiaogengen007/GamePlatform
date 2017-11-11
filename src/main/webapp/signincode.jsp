<%@ page language="java" contentType="text/html; charset=utf-8"  
    pageEncoding="utf-8"%>  
<%     
   String name = request.getParameter("user");  
   String pass = request.getParameter("password");  
   //boolean isLoginSucc = name.equals("abs") && pass.equals("123456");
   if(name != null)  
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