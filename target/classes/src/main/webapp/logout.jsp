<%@ page language="java" contentType="text/html; charset=utf-8"  
    pageEncoding="utf-8"%>  
<%  
  session.removeAttribute("user");  
  //session.invalidate()
  out.println("<script>window.location.href='sign.jsp'</script>");  
%>  