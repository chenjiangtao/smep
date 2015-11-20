<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<%@ page language="java" contentType="text/html; charset=GBK" pageEncoding="utf-8"%>
<jsp:useBean id="util" class="com.aesirteam.smep.adc.simulator.Util" scope="application"/>
<META name="GENERATOR" content="IBM Software Development Platform">
<META http-equiv="Content-Style-Type" content="text/css">
</head>
<body>
<h2>Welcome form AesirTeam.com</h2>
<span style="BACKGROUND-COLOR=silver;height=100px;width=100%">
<br>
&nbsp;&nbsp;<b>ADC平台与SI应用系统接口 (V3.0)</b>
<br>&nbsp;&nbsp;
APService WSDL: <a href="./services/APService?wsdl"><%=request.getRequestURL()%>services/APService</a>
<br>&nbsp;&nbsp;
ADC Simulator: <a href="./simulator/corpbind.jsp">CorpBind</a>&nbsp;
<a href="./simulator/deptbind.jsp">DeptBind</a>&nbsp;
<a href="./simulator/staffbind.jsp">StaffBind</a>
</span>

</body>
</html>
