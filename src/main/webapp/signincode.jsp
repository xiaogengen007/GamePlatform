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
	   switch (signinCode) {
	   case 1:
		   out.println("<script>alert('登陆失败！用户名与密码不匹配！');window.location.href='signin.jsp'</script>");
		   break;
	   case 2:
		   int logcode = PlayerManager.recordPlayer(name, pass);
		   if (logcode == 0) {
			   out.println("<script>alert('注册成功！');window.location.href='index.jsp'</script>"); 
		   } else {
			   out.println("<script>alert('注册失败！数据库异常！');window.location.href='signin.jsp'</script>"); 
		   }
		   //out.println("<script>alert('登陆失败！该用户不存在（未注册）！');window.location.href='signin.jsp'</script>");  
	   	   break;
	   case 3:
		   out.println("<script>alert('登陆失败！数据库异常！');window.location.href='signin.jsp'</script>");  
		   break;
	   default:
		   out.println("<script>alert('登陆失败！');window.location.href='signin.jsp'</script>");  
	   }
   }  
%>  