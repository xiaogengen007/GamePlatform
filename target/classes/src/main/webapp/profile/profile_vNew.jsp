<%@ page language = "java" contentType = "text/html; charset = utf-8" pageEncoding = "utf-8" isELIgnored = "false" %>
<%@ page import = "db.*, java.sql.*, java.util.*" %>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html lang="en">
	<head>
		<meta charset="utf-8" />
		<title>玩吧 用户信息</title>
		<meta name="keywords" content="用户信息" />
		<meta name="description" content="显示用户名、头像、积分、胜率等信息" />
		<meta name="viewport" content="width=device-width, initial-scale=1.0" />

		<!-- basic styles -->

		<link href="assets/css/bootstrap.min.css" rel="stylesheet" />
		<link rel="stylesheet" href="assets/css/font-awesome.min.css" />

		<!--[if IE 7]>
		  <link rel="stylesheet" href="assets/css/font-awesome-ie7.min.css" />
		<![endif]-->

		<!-- page specific plugin styles -->

		<link rel="stylesheet" href="assets/css/jquery-ui-1.10.3.custom.min.css" />
		<link rel="stylesheet" href="assets/css/jquery.gritter.css" />
		<link rel="stylesheet" href="assets/css/select2.css" />
		<link rel="stylesheet" href="assets/css/bootstrap-editable.css" />
		<!-- link的rel属性指示被链接的文档是一个样式表 -->

		<!-- fonts -->

		<link rel="stylesheet" href="assets\css\cyrillic.css" />

		<!-- ace styles -->

		<link rel="stylesheet" href="assets/css/ace.min.css" />
		<link rel="stylesheet" href="assets/css/ace-rtl.min.css" />
		<link rel="stylesheet" href="assets/css/ace-skins.min.css" />

		<!--[if lte IE 8]>
		  <link rel="stylesheet" href="assets/css/ace-ie.min.css" />
		<![endif]-->

		<!-- inline styles related to this page -->

		<!-- ace settings handler -->

		<script src="assets/js/ace-extra.min.js"></script>

		<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->

		<!--[if lt IE 9]>
		<script src="assets/js/html5shiv.js"></script>
		<script src="assets/js/respond.min.js"></script>
		<![endif]-->
	</head>
		<!-- head部分结束 -->

	<body>
		<%
			//if(session.getAttribute("user") == null)  
		    //{  
		    //    out.println("<script>alert('请先登陆');window.location.href='sign.jsp'</script>");  
		    //    return;  
		    //}  
		  	//response.setHeader( "Cache-Control", "no-cache,no-store");//HTTP 1.1
		  	//response.setDateHeader( "Expires", 0 ); //prevent caching at the proxy server
		  	//response.setHeader( "Pragma", "no-cache" );  //HTTP 1.0  
		    //out.println("<script>function gotoMineSweeper(){window.location.href='game.html?game=1&username="+ session.getAttribute("user")  +"'};</script>");
		    //out.println("<script>function gotoWhoIsUnderCover(){window.location.href='game.html?game=2&username="+ session.getAttribute("user")  +"'};</script>");
		  	//out.println("<script>username = '" + session.getAttribute("user")+ "';</script>");
		  	String uname = (String)session.getAttribute("user");
		  	int outputID = PlayerManager.getId(uname);
   			int outputPoint = PlayerManager.getPoint(uname);
   			//Map<String, Integer> totalRanklist = PlayerManager.sortPoint();
   			//request.getSession().setAttribute("totalRanklist",totalRanklist);
		%> 


		<div class="navbar navbar-default" id="navbar">
			<script type="text/javascript">
				try{ace.settings.check('navbar' , 'fixed')}catch(e){}
			</script>

			<div class="navbar-container" id="navbar-container">
				<div class="navbar-header pull-left">
					<a href="#" class="navbar-brand">
						<small>
							<i class="icon-leaf"></i>
							玩吧
						</small>
					</a><!-- /.brand -->
				</div><!-- /.navbar-header -->

				<div class="navbar-header pull-right" role="navigation">
					<ul class="nav ace-nav">
						<!-- 右上角紫红色的“通知”板块 -->	
						<li class="purple">
							<a href="../gamepage.jsp">
								<i class="icon-bell-alt icon-animated-bell"></i>
									游戏大厅
							</a>
						</li>

						<!-- 右上角绿色的“消息”板块 -->	
						<li class="green">
							<a href="../profile/profile_vNew.jsp">
								<i class="icon-envelope icon-animated-vertical"></i>
									个人资料
							</a>
						</li>

						<!-- 右上角浅蓝色的“欢迎用户”板块 -->
						<li class="light-blue">
							<a data-toggle="dropdown" href="#" class="dropdown-toggle">

								<span class="user-info">
									<small>Welcome,</small>
									<%=uname%>
								</span>

								<i class="icon-caret-down"></i>
							</a>

							<ul class="user-menu pull-right dropdown-menu dropdown-yellow dropdown-caret dropdown-close">

								<li>
									<a href="../welcome.html">
										<i class="icon-off"></i>
										登出
									</a>
								</li>
							</ul>
						</li>
					</ul><!-- /.ace-nav -->
				</div><!-- /.navbar-header -->
			</div><!-- /.container -->
		</div>

		<div class="main-container" id="main-container">
			<script type="text/javascript">
				try{ace.settings.check('main-container' , 'fixed')}catch(e){}
			</script>

			<div class="main-container-inner">
				<a class="menu-toggler" id="menu-toggler" href="#">
					<span class="menu-text"></span>
				</a>

				<div class="sidebar" id="sidebar">
					<script type="text/javascript">
						try{ace.settings.check('sidebar' , 'fixed')}catch(e){}
					</script>

					<ul class="nav nav-list">
						<li class="active">
							<a href="../gamepage.jsp">
								<i class="icon-dashboard"></i>
								<span class="menu-text"> 游戏大厅 </span>
							</a>
						</li>

						<li class="active open">
							<a href="#" class="dropdown-toggle">
								<i class="icon-edit"></i>
								<span class="menu-text"> 个人资料 </span>
								<b class="arrow icon-angle-down"></b>
							</a>

							<ul class="submenu">
								<li>
									<a data-toggle="tab" href="#home">
										<i class="icon-double-angle-right"></i>
										基本信息
									</a>
								</li>

								<li>
									<a data-toggle="tab" href="#friends">
										<i class="icon-double-angle-right"></i>
										游戏好友
									</a>
								</li>

								<li>
									<a data-toggle="tab" href="#ranklist">
										<i class="icon-double-angle-right"></i>
										排行榜
									</a>
								</li>
							</ul>
						</li>
					</ul><!-- /.nav-list -->

					<div class="sidebar-collapse" id="sidebar-collapse">
						<i class="icon-double-angle-left" data-icon1="icon-double-angle-left" data-icon2="icon-double-angle-right"></i>
					</div>

					<script type="text/javascript">
						try{ace.settings.check('sidebar' , 'collapsed')}catch(e){}
					</script>
				</div>
				<!-- 至此左侧列表结束 -->

				<div class="main-content">
					<div class="page-content">
						

						<div class="row">
							<div class="col-xs-12">
								<!-- PAGE CONTENT BEGINS -->
								<div>
									<div id="user-profile-2" class="user-profile">
										<div class="tabbable">


											<div class="tab-content no-border padding-24">
												<div id="home" class="tab-pane in active">
													<div class="page-header">
														<h1>
															个人资料
															<small>
															<i class="icon-double-angle-right"></i>
															基本信息
															</small>
														</h1>
													</div><!-- /.page-header -->
													<br>
													<div class="row">
														<div class="col-xs-12 col-sm-3 center">
															<span class="profile-picture">
																<img class="editable img-responsive" alt="Alex's Avatar" id="avatar2" src="assets/avatars/profile-pic.jpg" />
															</span>

															<div class="space space-4"></div>

														</div><!-- /span -->

														<div class="col-xs-12 col-sm-9">
															<h4 class="blue">
																<span class="middle"><%=uname%></span>

																<span class="label label-purple arrowed-in-right">
																	<i class="icon-circle smaller-80 align-middle"></i>
																	online
																</span>
															</h4>

															<div class="profile-user-info">
																<div class="profile-info-row">
																	<div class="profile-info-name"> 用户ID </div>
																	<div class="profile-info-value">
																		<span><%=outputID%></span>
																	</div>
																</div>

																<div class="profile-info-row">

																	<div class="profile-info-name"> 积分 </div>
																	<div class="profile-info-value">
																		<span> <%=outputPoint%> </span>
																	</div>
																</div>

																<div class="profile-info-row">
																	<div class="profile-info-name"> 游戏局数 </div>

																	<div class="profile-info-value">
																		<span>N.A.</span>
																	</div>
																</div>
															</div>

														</div><!-- /span -->
													</div><!-- /row-fluid -->

													<div class="space-20"></div>

													<div class="row">
														<div class="col-xs-12 col-sm-6">
															<div class="widget-box transparent">
																<div class="widget-header widget-header-small">
																	<h4 class="smaller">
																		<i class="icon-check bigger-110"></i>
																		游戏参与分布
																	</h4>
																</div>
																<br>
																		<div class="profile-skills">
																			<div class="progress">
																				<div class="progress-bar" style="width:70%">
																					<span class="pull-left">扫雷大作战</span>
																					<span class="pull-right">50%</span>
																				</div>
																			</div>
																			<br>
																			<div class="progress">
																				<div class="progress-bar progress-bar-purple" style="width:40%">
																					<span class="pull-left">谁是卧底</span>

																					<span class="pull-right">20%</span>
																				</div>
																			</div>
																		</div>
															</div>
														</div>

														<div class="col-xs-12 col-sm-6">
															<div class="widget-box transparent">
																<div class="widget-header widget-header-small header-color-blue2">
																	<h4 class="smaller">
																		<i class="icon-lightbulb bigger-120"></i>
																		游戏胜率
																	</h4>
																</div>

																<div class="widget-body">
																	<div class="widget-main padding-16">
																		<div class="clearfix">
																			<div class="grid3 center">
																				<div class="easy-pie-chart percentage" data-percent="45" data-color="#CA5952">
																					<span class="percent">45</span>%
																				</div>

																				<div class="space-2"></div>
																				扫雷大作战
																			</div>
																			
																			<div class="grid3 center">
																				<div class="center easy-pie-chart percentage" data-percent="80" data-color="#9585BF">
																					<span class="percent">80</span>%
																				</div>

																				<div class="space-2"></div>
																				谁是卧底
																			</div>
																			
																			<div class="grid3 center">
																				<div class="center easy-pie-chart percentage" data-percent="0" data-color="#28BD9A">
																					<span class="percent">N.A.</span>
																				</div>

																				<div class="space-2"></div>
																				更多游戏
																			</div>
																		</div>

																		<div class="hr hr-16"></div>
																	</div>
																</div>
															</div>
														</div>
													</div>
												</div><!-- #home -->

												<div id="friends" class="tab-pane">
													<div class="page-header">
														<h1>
															个人资料
															<small>
															<i class="icon-double-angle-right"></i>
															游戏好友
															</small>
														</h1>
													</div><!-- /.page-header -->
													<br>
													<%
														ArrayList<String> fl = new ArrayList<String>();
														fl = FriendManager.getFriendList(uname);
														request.setAttribute("fl", fl);
													%>
													<div class="profile-users clearfix">
														<c:forEach items = "${fl}" var = "keyword">
														<div class="itemdiv memberdiv">
															<div class="inline position-relative">
																<div class="user">
																	<a href="#">
																		<img src="assets/avatars/avatar4.png" alt="Bob Doe's avatar" />
																	</a>
																</div>

																<div class="body">
																	<div class="name">
																		<a href="#">
																			<span class="user-status status-online"></span>
																			<c:out value = "${keyword}"> </c:out>
																		</a>
																	</div>
																</div>

																<div class="popover">
																	<div class="arrow"></div>

																	<div class="popover-content">
																		<div class="bolder"><c:out value = "${keyword}"> </c:out></div>

																		<div class="hr dotted hr-8"></div>

																		<div class="tools action-buttons">
																			<a href="#">
																				<i class="icon-facebook-sign blue bigger-150"></i>
																			</a>

																			<a href="#">
																				<i class="icon-twitter-sign light-blue bigger-150"></i>
																			</a>

																			<a href="#">
																				<i class="icon-google-plus-sign red bigger-150"></i>
																			</a>
																		</div>
																	</div>
																</div>
															</div>
														</div>
													</c:forEach>
													</div>

													<div class="hr hr10 hr-double"></div>

													<ul class="pager pull-right">
														<li class="previous disabled">
															<a href="#">&larr; Prev</a>
														</li>

														<li class="next">
															<a href="#">Next &rarr;</a>
														</li>
													</ul>
												</div><!-- /#friends -->

												<div id="ranklist" class="tab-pane">
													<div class="page-header">
														<h1>
															个人资料
															<small>
															<i class="icon-double-angle-right"></i>
															排行榜
															</small>
														</h1>
													</div><!-- /.page-header -->
												<div class = "tabbable">
												<ul class="nav nav-tabs padding-18">
													<li class="active">
														<a data-toggle="tab" href="#total">
															<i class="green icon-user bigger-120"></i>
															总排行榜
														</a>
													</li>

													<li>
														<a data-toggle="tab" href="#partial">
															<i class="blue icon-group bigger-120"></i>
															好友排行榜
														</a>
													</li>
												</ul>
												<div class="tab-content no-border padding-24">
												<div id = "total" class = "tab-pane in active">
													<div class="row">
														<div class="container-fluid">
															<div class="row-fluid">
																<div class="span12">
																	<% 
																	Map<String, Integer> totalRanklist = PlayerManager.sortPoint();
																	request.setAttribute("totalRanklist", totalRanklist);
																	%>
																	<table class="table">
																		<colgroup>
																			<col style = "width:25%">
																			<col style = "width:45%">
																			<col style = "width:30%">
																		</colgroup>
																		<thead>
																			<tr>
																				<th> 名次 </th>
																				<th> 用户名 </th>
																				<th> 积分 </th>
																			</tr>
																		</thead>
																		<tbody>
																			<c:forEach var = "item" items = "${totalRanklist}" varStatus = "status"> 
																				<tr>
																					<td style = 'vertical-align : middle'> 
																					${status.index + 1} 
																					</td>
																					<td> 
																						<div class="user">
																							<img src="assets/avatars/avatar5.png" alt="Jim Doe's avatar"/>
																							&#12288
																							<span class="user-status status-offline"></span>
																							<a href="#">
																							${item.key} 
																							</a>
																						</div>
																					</td>
																					<td style = 'vertical-align : middle'>
																					${item.value}
																					</td>
																				</tr>
																			</c:forEach>
																		</tbody>
																	</table>
																</div>
															</div>
														</div>
													</div><!-- /row -->
												</div>
												<div id = "partial" class = "tab-pane">
													<div class="row">
														<div class="container-fluid">
															<div class="row-fluid">
																<div class="span12">
																	<% 
																	Map<String, Integer> friendRanklist = FriendManager.sortFriendPoint(uname);
																	request.setAttribute("friendRanklist", friendRanklist);
																	%>
																	<table class="table">
																		<colgroup>
																			<col style = "width:25%">
																			<col style = "width:45%">
																			<col style = "width:30%">
																		</colgroup>
																		<thead>
																			<tr>
																				<th>
																					名次
																				</th>
																				<th>
																					用户名
																				</th>
																				<th>
																					积分
																				</th>
																			</tr>
																		</thead>
																		<tbody>
																			<tr>
																				<td style = 'vertical-align : middle'>
																					<span class="badge badge-yellow">1</span>
																				</td>
																				<td>
																					<div class="user">
																							<img src="assets/avatars/avatar5.png" alt="Jennifer Doe's avatar" />
																							&#12288
																							<span class="user-status status-offline"></span>
																							<a href="#">
																								${friendRanklist['key1']}
																							</a>
																					</div>
																				</td>
																				<td style = 'vertical-align : middle'>
																					${friendRanklist['value1']}
																				</td>
																			</tr>
																			<tr class="success">
																				<td style = 'vertical-align : middle'>
																					<span class="badge badge-success">2</span>
																				</td>
																				<td>
																					<div class="user">
																							<img src="assets/avatars/avatar.png" alt="Jim Doe's avatar"/>
																							&#12288
																							<span class="user-status status-offline"></span>
																							<a href="#">
																							${friendRanklist['key2']}
																							</a>
																					</div>
																				</td>
																				<td style = 'vertical-align : middle'>
																					${friendRanklist['value2']}
																				</td>
																			</tr>
																			<tr class="error">
																				<td style = 'vertical-align : middle'>
																					<span class="badge badge-purple">3</span>
																				</td>
																				<td>
																					<div class="user">
																							<img src="assets/avatars/avatar3.png" alt="Jim Doe's avatar"/>
																							&#12288
																							<span class="user-status status-offline"></span>
																							<a href="#">
																							${friendRanklist['key3']}
																						</a>
																					</div>
																				</td>
																				<td style = 'vertical-align : middle'>
																					${friendRanklist['value3']}
																				</td>
																			</tr>
																			<tr class="warning">
																				<td style = 'vertical-align : middle'>
																					<span class="badge badge-warning">4</span>
																				</td>
																				<td>
																					<div class="user">
																							<img src="assets/avatars/avatar1.png" alt="Jim Doe's avatar"/>
																							&#12288
																							<span class="user-status status-offline"></span>
																							<a href="#">
																							${friendRanklist['key4']}
																						</a>
																					</div>
																				</td>
																				<td style = 'vertical-align : middle'>
																					${friendRanklist['value4']}
																				</td>
																			</tr>
																			<tr class="info">
																				<td style = 'vertical-align : middle'>
																					<span class="badge badge-info">5</span>
																				</td>
																				<td>
																					<div class="user">
																							<img src="assets/avatars/avatar5.png" alt="Jim Doe's avatar"/>
																							&#12288
																							<span class="user-status status-offline"></span>
																							<a href="#">
																							${friendRanklist['key5']}
																						</a>
																					</div>
																				</td>
																				<td style = 'vertical-align : middle'>
																					${friendRanklist['value5']}
																				</td>
																			</tr>
																		</tbody>
																	</table>
																	<br>
																	<table class="table">
																		<colgroup>
																			<col style = "width:25%">
																			<col style = "width:45%">
																			<col style = "width:30%">
																		</colgroup>
																		<thead>
																			<tr>
																				<th> 名次 </th>
																				<th> 用户名 </th>
																				<th> 积分 </th>
																			</tr>
																		</thead>
																		<tbody>
																			<c:forEach var = "item" items = "${friendRanklist}" varStatus = "status"> 
																				<tr>
																					<td style = 'vertical-align : middle'>
																					${status.index + 1} 
																					</td>
																					<td> 
																						<div class="user">
																							<img src="assets/avatars/avatar5.png" alt="Jim Doe's avatar"/>
																							&#12288
																							<span class="user-status status-offline"></span>
																							<a href="#">
																							${item.key} 
																							</a>
																						</div>
																					</td>
																					<td style = 'vertical-align : middle'> 
																					${item.value} 
																					</td>
																				</tr>
																			</c:forEach>
																		</tbody>
																	</table>
																</div>
															</div>
														</div>
													</div><!-- /row -->
												</div>
												</div>
												</div>
												</div><!-- /#ranklist -->
											</div>
										</div>
									</div>
								</div>
							</div><!-- /.col -->
						</div><!-- /.row -->
					</div><!-- /.page-content -->
				</div><!-- /.main-content -->
				<div class="ace-settings-container" id="ace-settings-container">
					<div class="btn btn-app btn-xs btn-warning ace-settings-btn" id="ace-settings-btn">
						<i class="icon-cog bigger-150"></i>
					</div>

					<div class="ace-settings-box" id="ace-settings-box">
						<div>
							<div class="pull-left">
								<select id="skin-colorpicker" class="hide">
									<option data-skin="default" value="#438EB9">#438EB9</option>
									<option data-skin="skin-1" value="#222A2D">#222A2D</option>
									<option data-skin="skin-2" value="#C6487E">#C6487E</option>
									<option data-skin="skin-3" value="#D0D0D0">#D0D0D0</option>
								</select>
							</div>
							<span>&nbsp; Choose Skin</span>
						</div>

						<div>
							<input type="checkbox" class="ace ace-checkbox-2" id="ace-settings-navbar" />
							<label class="lbl" for="ace-settings-navbar"> Fixed Navbar</label>
						</div>

						<div>
							<input type="checkbox" class="ace ace-checkbox-2" id="ace-settings-sidebar" />
							<label class="lbl" for="ace-settings-sidebar"> Fixed Sidebar</label>
						</div>

						<div>
							<input type="checkbox" class="ace ace-checkbox-2" id="ace-settings-breadcrumbs" />
							<label class="lbl" for="ace-settings-breadcrumbs"> Fixed Breadcrumbs</label>
						</div>

						<div>
							<input type="checkbox" class="ace ace-checkbox-2" id="ace-settings-rtl" />
							<label class="lbl" for="ace-settings-rtl"> Right To Left (rtl)</label>
						</div>

						<div>
							<input type="checkbox" class="ace ace-checkbox-2" id="ace-settings-add-container" />
							<label class="lbl" for="ace-settings-add-container">
								Inside
								<b>.container</b>
							</label>
						</div>
					</div>
				</div><!-- /#ace-settings-container -->
			</div><!-- /.main-container-inner -->

			<a href="#" id="btn-scroll-up" class="btn-scroll-up btn btn-sm btn-inverse">
				<i class="icon-double-angle-up icon-only bigger-110"></i>
			</a>
		</div><!-- /.main-container -->

		<!-- basic scripts -->

		<!--[if !IE]> -->

		<script src="assets\js\jquery-2.0.3.min.js"></script>

		<!-- <![endif]-->

		<!--[if IE]>
<script src="assets\js\jquery-1.10.2.min.js"></script>
<![endif]-->

		<!--[if !IE]> -->

		<script type="text/javascript">
			window.jQuery || document.write("<script src='assets/js/jquery-2.0.3.min.js'>"+"<"+"/script>");
		</script>

		<!-- <![endif]-->

		<!--[if IE]>
<script type="text/javascript">
 window.jQuery || document.write("<script src='assets/js/jquery-1.10.2.min.js'>"+"<"+"/script>");
</script>
<![endif]-->

		<script type="text/javascript">
			if("ontouchend" in document) document.write("<script src='assets/js/jquery.mobile.custom.min.js'>"+"<"+"/script>");
		</script>
		<script src="assets/js/bootstrap.min.js"></script>
		<script src="assets/js/typeahead-bs2.min.js"></script>

		<!-- page specific plugin scripts -->

		<!--[if lte IE 8]>
		  <script src="assets/js/excanvas.min.js"></script>
		<![endif]-->

		<script src="assets/js/jquery-ui-1.10.3.custom.min.js"></script>
		<script src="assets/js/jquery.ui.touch-punch.min.js"></script>
		<script src="assets/js/jquery.gritter.min.js"></script>
		<script src="assets/js/bootbox.min.js"></script>
		<script src="assets/js/jquery.slimscroll.min.js"></script>
		<script src="assets/js/jquery.easy-pie-chart.min.js"></script>
		<script src="assets/js/jquery.hotkeys.min.js"></script>
		<script src="assets/js/bootstrap-wysiwyg.min.js"></script>
		<script src="assets/js/select2.min.js"></script>
		<script src="assets/js/date-time/bootstrap-datepicker.min.js"></script>
		<script src="assets/js/fuelux/fuelux.spinner.min.js"></script>
		<script src="assets/js/x-editable/bootstrap-editable.min.js"></script>
		<script src="assets/js/x-editable/ace-editable.min.js"></script>
		<script src="assets/js/jquery.maskedinput.min.js"></script>

		<!-- ace scripts -->

		<script src="assets/js/ace-elements.min.js"></script>
		<script src="assets/js/ace.min.js"></script>

		<!-- inline scripts related to this page -->

		<script type="text/javascript">
			jQuery(function($) {
			
				//editables on first profile page
				$.fn.editable.defaults.mode = 'inline';
				$.fn.editableform.loading = "<div class='editableform-loading'><i class='light-blue icon-2x icon-spinner icon-spin'></i></div>";
			    $.fn.editableform.buttons = '<button type="submit" class="btn btn-info editable-submit"><i class="icon-ok icon-white"></i></button>'+
			                                '<button type="button" class="btn editable-cancel"><i class="icon-remove"></i></button>';    
				
				//editables 
			    $('#username').editable({
					type: 'text',
					name: 'username'
			    });
			
			
				var countries = [];
			    $.each({ "CA": "Canada", "IN": "India", "NL": "Netherlands", "TR": "Turkey", "US": "United States"}, function(k, v) {
			        countries.push({id: k, text: v});
			    });
			
				var cities = [];
				cities["CA"] = [];
				$.each(["Toronto", "Ottawa", "Calgary", "Vancouver"] , function(k, v){
					cities["CA"].push({id: v, text: v});
				});
				cities["IN"] = [];
				$.each(["Delhi", "Mumbai", "Bangalore"] , function(k, v){
					cities["IN"].push({id: v, text: v});
				});
				cities["NL"] = [];
				$.each(["Amsterdam", "Rotterdam", "The Hague"] , function(k, v){
					cities["NL"].push({id: v, text: v});
				});
				cities["TR"] = [];
				$.each(["Ankara", "Istanbul", "Izmir"] , function(k, v){
					cities["TR"].push({id: v, text: v});
				});
				cities["US"] = [];
				$.each(["New York", "Miami", "Los Angeles", "Chicago", "Wysconsin"] , function(k, v){
					cities["US"].push({id: v, text: v});
				});
				
				var currentValue = "NL";
			    $('#country').editable({
					type: 'select2',
					value : 'NL',
			        source: countries,
					success: function(response, newValue) {
						if(currentValue == newValue) return;
						currentValue = newValue;
						
						var new_source = (!newValue || newValue == "") ? [] : cities[newValue];
						
						//the destroy method is causing errors in x-editable v1.4.6
						//it worked fine in v1.4.5
						/**			
						$('#city').editable('destroy').editable({
							type: 'select2',
							source: new_source
						}).editable('setValue', null);
						*/
						
						//so we remove it altogether and create a new element
						var city = $('#city').removeAttr('id').get(0);
						$(city).clone().attr('id', 'city').text('Select City').editable({
							type: 'select2',
							value : null,
							source: new_source
						}).insertAfter(city);//insert it after previous instance
						$(city).remove();//remove previous instance
						
					}
			    });
			
				$('#city').editable({
					type: 'select2',
					value : 'Amsterdam',
			        source: cities[currentValue]
			    });
			
			
			
				$('#signup').editable({
					type: 'date',
					format: 'yyyy-mm-dd',
					viewformat: 'dd/mm/yyyy',
					datepicker: {
						weekStart: 1
					}
				});
			
			    $('#age').editable({
			        type: 'spinner',
					name : 'age',
					spinner : {
						min : 16, max:99, step:1
					}
				});
				
				//var $range = document.createElement("INPUT");
				//$range.type = 'range';
			    $('#login').editable({
			        type: 'slider',//$range.type == 'range' ? 'range' : 'slider',
					name : 'login',
					slider : {
						min : 1, max:50, width:100
					},
					success: function(response, newValue) {
						if(parseInt(newValue) == 1)
							$(this).html(newValue + " hour ago");
						else $(this).html(newValue + " hours ago");
					}
				});
			
				$('#about').editable({
					mode: 'inline',
			        type: 'wysiwyg',
					name : 'about',
					wysiwyg : {
						//css : {'max-width':'300px'}
					},
					success: function(response, newValue) {
					}
				});
				
				
				
				// *** editable avatar *** //
				try {//ie8 throws some harmless exception, so let's catch it
			
					//it seems that editable plugin calls appendChild, and as Image doesn't have it, it causes errors on IE at unpredicted points
					//so let's have a fake appendChild for it!
					if( /msie\s*(8|7|6)/.test(navigator.userAgent.toLowerCase()) ) Image.prototype.appendChild = function(el){}
			
					var last_gritter
					$('#avatar').editable({
						type: 'image',
						name: 'avatar',
						value: null,
						image: {
							//specify ace file input plugin's options here
							btn_choose: 'Change Avatar',
							droppable: true,
							/**
							//this will override the default before_change that only accepts image files
							before_change: function(files, dropped) {
								return true;
							},
							*/
			
							//and a few extra ones here
							name: 'avatar',//put the field name here as well, will be used inside the custom plugin
							max_size: 110000,//~100Kb
							on_error : function(code) {//on_error function will be called when the selected file has a problem
								if(last_gritter) $.gritter.remove(last_gritter);
								if(code == 1) {//file format error
									last_gritter = $.gritter.add({
										title: 'File is not an image!',
										text: 'Please choose a jpg|gif|png image!',
										class_name: 'gritter-error gritter-center'
									});
								} else if(code == 2) {//file size rror
									last_gritter = $.gritter.add({
										title: 'File too big!',
										text: 'Image size should not exceed 100Kb!',
										class_name: 'gritter-error gritter-center'
									});
								}
								else {//other error
								}
							},
							on_success : function() {
								$.gritter.removeAll();
							}
						},
					    url: function(params) {
							// ***UPDATE AVATAR HERE*** //
							//You can replace the contents of this function with examples/profile-avatar-update.js for actual upload
			
			
							var deferred = new $.Deferred
			
							//if value is empty, means no valid files were selected
							//but it may still be submitted by the plugin, because "" (empty string) is different from previous non-empty value whatever it was
							//so we return just here to prevent problems
							var value = $('#avatar').next().find('input[type=hidden]:eq(0)').val();
							if(!value || value.length == 0) {
								deferred.resolve();
								return deferred.promise();
							}
			
			
							//dummy upload
							setTimeout(function(){
								if("FileReader" in window) {
									//for browsers that have a thumbnail of selected image
									var thumb = $('#avatar').next().find('img').data('thumb');
									if(thumb) $('#avatar').get(0).src = thumb;
								}
								
								deferred.resolve({'status':'OK'});
			
								if(last_gritter) $.gritter.remove(last_gritter);
								last_gritter = $.gritter.add({
									title: 'Avatar Updated!',
									text: 'Uploading to server can be easily implemented. A working example is included with the template.',
									class_name: 'gritter-info gritter-center'
								});
								
							 } , parseInt(Math.random() * 800 + 800))
			
							return deferred.promise();
						},
						
						success: function(response, newValue) {
						}
					})
				}catch(e) {}
				
				
			
				//another option is using modals
				$('#avatar2').on('click', function(){
					var modal = 
					'<div class="modal hide fade">\
						<div class="modal-header">\
							<button type="button" class="close" data-dismiss="modal">&times;</button>\
							<h4 class="blue">Change Avatar</h4>\
						</div>\
						\
						<form class="no-margin">\
						<div class="modal-body">\
							<div class="space-4"></div>\
							<div style="width:75%;margin-left:12%;"><input type="file" name="file-input" /></div>\
						</div>\
						\
						<div class="modal-footer center">\
							<button type="submit" class="btn btn-small btn-success"><i class="icon-ok"></i> Submit</button>\
							<button type="button" class="btn btn-small" data-dismiss="modal"><i class="icon-remove"></i> Cancel</button>\
						</div>\
						</form>\
					</div>';
					
					
					var modal = $(modal);
					modal.modal("show").on("hidden", function(){
						modal.remove();
					});
			
					var working = false;
			
					var form = modal.find('form:eq(0)');
					var file = form.find('input[type=file]').eq(0);
					file.ace_file_input({
						style:'well',
						btn_choose:'Click to choose new avatar',
						btn_change:null,
						no_icon:'icon-picture',
						thumbnail:'small',
						before_remove: function() {
							//don't remove/reset files while being uploaded
							return !working;
						},
						before_change: function(files, dropped) {
							var file = files[0];
							if(typeof file === "string") {
								//file is just a file name here (in browsers that don't support FileReader API)
								if(! (/\.(jpe?g|png|gif)$/i).test(file) ) return false;
							}
							else {//file is a File object
								var type = $.trim(file.type);
								if( ( type.length > 0 && ! (/^image\/(jpe?g|png|gif)$/i).test(type) )
										|| ( type.length == 0 && ! (/\.(jpe?g|png|gif)$/i).test(file.name) )//for android default browser!
									) return false;
			
								if( file.size > 110000 ) {//~100Kb
									return false;
								}
							}
			
							return true;
						}
					});
			
					form.on('submit', function(){
						if(!file.data('ace_input_files')) return false;
						
						file.ace_file_input('disable');
						form.find('button').attr('disabled', 'disabled');
						form.find('.modal-body').append("<div class='center'><i class='icon-spinner icon-spin bigger-150 orange'></i></div>");
						
						var deferred = new $.Deferred;
						working = true;
						deferred.done(function() {
							form.find('button').removeAttr('disabled');
							form.find('input[type=file]').ace_file_input('enable');
							form.find('.modal-body > :last-child').remove();
							
							modal.modal("hide");
			
							var thumb = file.next().find('img').data('thumb');
							if(thumb) $('#avatar2').get(0).src = thumb;
			
							working = false;
						});
						
						
						setTimeout(function(){
							deferred.resolve();
						} , parseInt(Math.random() * 800 + 800));
			
						return false;
					});
							
				});
			
				
			
				//////////////////////////////
				$('#profile-feed-1').slimScroll({
				height: '250px',
				alwaysVisible : true
				});
			
				$('.profile-social-links > a').tooltip();
			
				$('.easy-pie-chart.percentage').each(function(){
				var barColor = $(this).data('color') || '#555';
				var trackColor = '#E2E2E2';
				var size = parseInt($(this).data('size')) || 72;
				$(this).easyPieChart({
					barColor: barColor,
					trackColor: trackColor,
					scaleColor: false,
					lineCap: 'butt',
					lineWidth: parseInt(size/10),
					animate:false,
					size: size
				}).css('color', barColor);
				});
			  
				///////////////////////////////////////////
			
				//show the user info on right or left depending on its position
				$('#user-profile-2 .memberdiv').on('mouseenter', function(){
					var $this = $(this);
					var $parent = $this.closest('.tab-pane');
			
					var off1 = $parent.offset();
					var w1 = $parent.width();
			
					var off2 = $this.offset();
					var w2 = $this.width();
			
					var place = 'left';
					if( parseInt(off2.left) < parseInt(off1.left) + parseInt(w1 / 2) ) place = 'right';
					
					$this.find('.popover').removeClass('right left').addClass(place);
				}).on('click', function() {
					return false;
				});
			
			
				///////////////////////////////////////////
				$('#user-profile-3')
				.find('input[type=file]').ace_file_input({
					style:'well',
					btn_choose:'Change avatar',
					btn_change:null,
					no_icon:'icon-picture',
					thumbnail:'large',
					droppable:true,
					before_change: function(files, dropped) {
						var file = files[0];
						if(typeof file === "string") {//files is just a file name here (in browsers that don't support FileReader API)
							if(! (/\.(jpe?g|png|gif)$/i).test(file) ) return false;
						}
						else {//file is a File object
							var type = $.trim(file.type);
							if( ( type.length > 0 && ! (/^image\/(jpe?g|png|gif)$/i).test(type) )
									|| ( type.length == 0 && ! (/\.(jpe?g|png|gif)$/i).test(file.name) )//for android default browser!
								) return false;
			
							if( file.size > 110000 ) {//~100Kb
								return false;
							}
						}
			
						return true;
					}
				})
				.end().find('button[type=reset]').on(ace.click_event, function(){
					$('#user-profile-3 input[type=file]').ace_file_input('reset_input');
				})
				.end().find('.date-picker').datepicker().next().on(ace.click_event, function(){
					$(this).prev().focus();
				})
				$('.input-mask-phone').mask('(999) 999-9999');
				////////////////////
				//change profile
				$('[data-toggle="buttons"] .btn').on('click', function(e){
					var target = $(this).find('input[type=radio]');
					var which = parseInt(target.val());
					$('.user-profile').parent().addClass('hide');
					$('#user-profile-'+which).parent().removeClass('hide');
				});
			});
		</script>
</body>
</html>
<script>var _hmt = _hmt || [];(function(){var hm=document.createElement("script");hm.src = "//hm.baidu.com/hm.js?a43c39da34531f4da694d4499c5614d4";var s=document.getElementsByTagName("script")[0];s.parentNode.insertBefore(hm, s);})();</script>