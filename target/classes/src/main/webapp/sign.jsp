<%@ page language="java" contentType="text/html; charset=utf-8"  
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%
	if(session.getAttribute("user") != null)
	{  
       out.println("<script>window.location.href='index.jsp'</script>");  
	}  
%>

<head>

<title>玩吧 登录和注册</title> 
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<script type="text/javascript" src="basicpages/2 sign/js/jquery-1.9.0.min.js"></script>
<script type="text/javascript" src="basicpages/2 sign/js/login.js"></script>
<link href="basicpages/2 sign/css/login.css" rel="stylesheet" type="text/css" />

</head>



<body>

<div class="login" style="margin-top:50px;">
    
    <div class="header">
        <div class="switch" id="switch"><a class="switch_btn_focus" id="switch_qlogin" href="javascript:void(0);" tabindex="7">快速登录</a>
			<a class="switch_btn" id="switch_login" href="javascript:void(0);" tabindex="8">快速注册</a><div class="switch_bottom" id="switch_bottom" style="position: absolute; width: 64px; left: 0px;"></div>
        </div>
    </div>    
  
    
    <!--登录-->
    <div class="web_qr_login" id="web_qr_login" style="display: block; height: 280px;">    
        <div class="web_login" id="web_login">
            <div class="login-box">
			    <div class="login_form">
				<form action="signincode.jsp" name="loginform" accept-charset="utf-8" id="login_form" class="loginForm" method="post"><input type="hidden" name="did" value="0"/>
                    <input type="hidden" name="to" value="log"/>
                    <div class="uinArea" id="uinArea">
                    <label class="input-tips" for="u">帐号：</label>
                    <div class="inputOuter" id="uArea">
                        <input type="text" id="u" name="user" class="inputstyle"/>
                    </div>
                    </div>

                    <br>
                    <div class="pwdArea" id="pwdArea">
                    <label class="input-tips" for="p">密码：</label> 
                    <div class="inputOuter" id="pArea">
                        <input type="password" id="p" name="password" class="inputstyle"/>
                    </div>
                    </div>
                    
                    <div style="padding-left:50px; margin-top:20px;">
                        <input value="登 录" style="width:150px;" class="button_blue" type="submit"/>
                    </div>
                </form>
                </div>
            </div> 
        </div>
    </div>
    <!--登录end-->
    
    <!--注册-->
    <div class="qlogin" id="qlogin" style="display: none; height: 250px; ">    
        <div class="web_login">
            <div class="login-box">
			    <div class="login_form">
				<form action="signupcode.jsp" name="loginform" accept-charset="utf-8" class="loginForm" method="post"><input type="hidden" name="did" value="0"/>
                    <input type="hidden" name="to" value="log"/>
                    <div class="uinArea">
                    <label class="input-tips" for="u">帐号：</label>
                    <div class="inputOuter">
                        <input type="text" name="user" class="inputstyle"/>
                    </div>
                    </div>

                    <br>
                    <div class="pwdArea" >
                    <label class="input-tips" for="p">密码：</label> 
                    <div class="inputOuter" >
                        <input type="password"  name="password" class="inputstyle"/>
                    </div>
                    </div>
                    
                    <div style="padding-left:50px; margin-top:20px;">
                        <input value="注 册" style="width:150px;" class="button_blue" type="submit"/>
                    </div>
                </form>
                </div>
            </div> 
        </div>
    </div>
    <!--注册end-->
    

    <!--注册-->
    <!-- 
    <div class="qlogin" id="qlogin" style="display: none; height: 250px; ">
        <div class="web_login"><form name="form2" id="regUser" accept-charset="utf-8"  action="signincode.jsp" method="post">
	        <input type="hidden" name="to" value="reg"/>
		    <input type="hidden" name="did" value="0"/>
            <ul class="reg_form" id="reg-ul">
                <br>
                <li>
                    <label for="user"  class="input-tips2">用户名&nbsp;：</label>
                    <div class="inputOuter2">
                        <input type="text" id="user" name="user" maxlength="16" class="inputstyle2"/>
                    </div>
                </li>
                <li>
                <label for="passwd" class="input-tips2">密&nbsp;&nbsp;码&nbsp;&nbsp;：</label>
                    <div class="inputOuter2">
                        <input type="password" id="passwd"  name="password" maxlength="16" class="inputstyle2"/>
                    </div>  
                </li>

                <li>
                <label for="passwd2" class="input-tips2">确认密码：</label>
                    <div class="inputOuter2">
                        <input type="password" id="passwd2" name="password2" maxlength="16" class="inputstyle2" />
                    </div> 
                </li>
                
                <li>
                    <div class="inputArea">
                        <div style="padding-left:80px; margin-top:20px;">
                            <input type="submit" id="reg" style=" width:150px;" class="button_blue" value="注册"/>
                        </div>
                    </div>
                </li>
                <div class="cl"></div>
            </ul>
        </form></div>
    </div>
    -->
    <!--注册end-->
</div>
    



<!--
    onclick="inMainWindow()"
    <script type="text/javascript">
    //转到游戏选择界面
    gotoGameSelection()
    function inMainWindow() {
      //window.location.href="index.jsp"+"?username="+document.getElementById('username').value;
      window.location.href="gamepage.html"+"?username="+document.getElementById('inputUsername').value;
    }
    </script>
-->


<!--<div class="jianyi">*推荐使用ie8或以上版本ie浏览器或Chrome内核浏览器访问本站</div>-->
</body>
</html>