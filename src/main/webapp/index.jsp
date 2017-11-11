<%@ page language="java" contentType="text/html; charset=utf-8"  pageEncoding="utf-8"%>  
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">  
<html>  
<head>  
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">  
<title>欢迎</title>  
</head>  
<body>  
  <%
  	response.setHeader( "Cache-Control", "no-cache,no-store");//HTTP 1.1
  	response.setDateHeader( "Expires", 0 ); //prevent caching at the proxy server
  	response.setHeader( "Pragma", "no-cache" );  //HTTP 1.0  
    if(session.getAttribute("user") == null)  
    {  
        out.println("<script>alert('请先登陆');window.location.href='signin.jsp'</script>");  
        return;  
    }  
    Object user = session.getAttribute("user");  
    out.println("欢迎"+user);
    System.out.printf("test");
    
    out.println("<script>window.location.href='game.html?username="+ session.getAttribute("user")  +"'</script>");
    //out.println("<script>window.location.href='minesweeper/mine.html?username="+ session.getAttribute("user")  +"'</script>");
  %>  
  <br/>  
  <form action="#" method="post">  
    <button type="submit" formaction="logout.jsp">登出</button>  
  </form>
  
  <script>
	window.location.href='game.html?';
  </script>  
    
</body>  
</html>  