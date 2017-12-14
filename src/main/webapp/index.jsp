<%@ page language="java" contentType="text/html; charset=utf-8"  pageEncoding="utf-8"%>  
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">  
<html>  
<head>  
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<!-- Bootstrap core CSS -->
<link href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">  
<title>欢迎</title>  
</head>  
<body>  
  <%
  	response.setHeader( "Cache-Control", "no-cache,no-store");//HTTP 1.1
  	response.setDateHeader( "Expires", 0 ); //prevent caching at the proxy server
  	response.setHeader( "Pragma", "no-cache" );  //HTTP 1.0  
    if(session.getAttribute("user") == null)  
    {  
        out.println("<script>alert('请先登陆');window.location.href='sign.jsp'</script>");  
        return;  
    }  
    Object user = session.getAttribute("user");  
    out.println("欢迎"+user);
    //System.out.printf("test");
    out.println("<script>window.location.href='gamepage.jsp';</script>");
    //out.println("<script>function gotoGame(){window.location.href='game.html?username="+ session.getAttribute("user")  +"'};</script>");
  %>  
  <br/>  
  <!--
  <form action="#" method="post">  
    <button type="submit" formaction="logout.jsp">登出</button>  
  </form>
  -->
  
  <button onclick="gotoGame()">扫雷</button>
  <button class="btn btn-primary btn-lg" onclick="showModal()">开始演示模态框</button>
	<!-- 模态框（Modal） -->
  <div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	    <div class="modal-dialog">
	        <div class="modal-content">
	            <div class="modal-header">
	                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	                <h4 class="modal-title" id="myModalLabel">模态框（Modal）标题</h4>
	            </div>
	            <div class="modal-body">在这里添加一些文本</div>
	            <div class="modal-footer">
	                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
	                <button type="button" class="btn btn-primary">提交更改</button>
	            </div>
	        </div><!-- /.modal-content -->
	    </div><!-- /.modal -->
	</div>
  
  <script src="https://cdn.bootcss.com/jquery/1.12.4/jquery.min.js"></script>
  <script src="https://cdn.bootcss.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
  
  <script>
  	function showModal(){
  		$('#myModal').modal({backdrop: 'static', keyboard: false});
  	}
  	$(function(){$('#myModal').on('hide.bs.modal', function(){console.log('close modal');});});
  </script>
  
</body>  
</html>  