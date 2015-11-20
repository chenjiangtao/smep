<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<HTML>
<HEAD>
<%@ page language="java" contentType="text/html; charset=GBK" pageEncoding="utf-8"%>
<jsp:useBean id="util" class="com.aesirteam.smep.adc.simulator.Util" scope="application"/>
<META name="GENERATOR" content="IBM Software Development Platform">
<META http-equiv="Content-Style-Type" content="text/css">
<LINK href="./theme/Master.css" rel="stylesheet" type="text/css" />
<SCRIPT src="./javascript/func.js"></SCRIPT>
<TITLE>DeptBind-部门信息绑定接口</TITLE>
</HEAD>
<BODY>
<p><font color="white">首页<a href="../">Back</a>&nbsp;
企业绑定接口<a href="./corpbind.jsp">CorpBind</a>&nbsp;
         部门信息绑定接口<a href="./deptbind.jsp">DeptBind</a>&nbsp;
         员工帐号绑定接口<a href="./staffbind.jsp">StaffBind</a>&nbsp;</font><br>
</p>

<FORM method="post" action="../translet" onsubmit="deptbindonsubmit()">
<DIV align="left">
<TABLE border="0" bgcolor="white">
	<TBODY>
		<TR>
			<TD bgcolor="fuchsia" width="10%" align="center">URL</TD>
			<TD colspan="2" bgcolor="fuchsia">
			<INPUT type="text" name="tb_endpoint" maxlength=100 style="width:400px" value=<%=util.getEndpoint()%>>
			<INPUT type="submit" name="post_si" value="提交" style="width:60px">
			</TD>
		</TR>
		<TR>
			<TD bgcolor="fuchsia" width="10%" align="center">SECRET</TD>
			<TD colspan="2" bgcolor="fuchsia">
			<INPUT type="text" name="teb_secret" maxlength=16 style="width:400px" value=<%=util.getSecret()%>>
			<label><input type="checkbox" name="body_flag"/>查看BODY报文</label>
			</TD>
		</TR>
		<TR>
			<TD rowspan="4" bgcolor="silver" width="10%" align="center">HEAD</TD>
			<TD bgcolor="silver" width="141">CODE</TD>
			<TD bgcolor="silver">
			<INPUT type="text" name="tb_code" size="20" value="DeptBind" readonly>
			</TD>
		</TR>
		<TR>
			<TD bgcolor="silver" width="141">SID</TD>
			<TD bgcolor="silver">
			<INPUT type="text" name="tb_sid" size="20" value="0">
			</TD>
		</TR>
		<TR>
			<TD bgcolor="silver" width="141">TIMESTAMP</TD>
			<TD bgcolor="silver">
			<INPUT type="text" name="tb_timestamp" size="20" value=<%=util.getTimestamp()%> readonly>
			</TD>
		</TR>
		<TR>
			<TD bgcolor="silver" width="141">SERVICEID</TD>
			<TD bgcolor="silver">
			<INPUT type="text" name="tb_serviceid" size="20" value="TGZ5719900">
			</TD>
		</TR>
		<TR>
			<TD rowspan="4" bgcolor="olive" width="10%" align="center">BODY</TD>
			<TD bgcolor="olive" width="141">COPRACOUNT</TD>
			<TD bgcolor="olive">
			<INPUT type="text" name="tb_corpaccount" size="20" value="85153312861">
			</TD>
		</TR>
		<TR>
			<TD bgcolor="olive" width="141">DEPS</TD>
			<TD bgcolor="olive" width="100%">
			<TABLE border="0">
				<TBODY>
					<TR>
						<TD align="right">DEPTID:</TD>
						<TD colspan="2">
						<INPUT type="text" name="tb_deptid" size="20" value="2406802"></TD>
					</TR>
					
					<TR>
						<TD align="right">PARENTID:</TD>
						<TD colspan="2">
						<INPUT type="text" name="tb_parentid" size="20" value="-1">
						</TD>
					</TR>
					
					<TR>
						<TD align="right">DEPNAME:</TD>
						<TD colspan="2"><INPUT type="text" name="tb_depname" size="20" value="其他"></TD>
					</TR>
	
					<TR>
						<TD align="right">DEPDES:</TD>
						<TD colspan="2"><INPUT type="text" name="tb_depdes" size="20"></TD>
					</TR>
					
					<TR>
						<TD align="right">DEPADDRESS:</TD>
						<TD colspan="2"><INPUT type="text" name="tb_depaddress" size="20"></TD>
					</TR>
										
					<TR>
						<TD align="right">DEPTELNO:</TD>
						<TD colspan="2"><INPUT type="text" name="tb_deptelno" size="20"></TD>
					</TR>
					
					<TR>
						<TD align="right">DEPFAXNO:</TD>
						<TD colspan="2"><INPUT type="text" name="tb_depfaxno" size="20"></TD>
					</TR>					
					
					<TR>
						<TD align="right">DEPMNGID:</TD>
						<TD colspan="2"><INPUT type="text" name="tb_depmngid" size="20"></TD>
					</TR>
					
					<TR>
						<TD align="right">BUILDTIME:</TD>
						<TD colspan="2"><INPUT type="text" name="tb_buildtime" size="20"></TD>
					</TR>				
					
					<TR>
						<TD align="right">UPDATEDATE:</TD>
						<TD colspan="2"><INPUT type="text" name="tb_updatedate" size="20"></TD>
					</TR>
										
					<TR>
						<TD align="right">OPTYPE:</TD>
						<TD colspan="2"><INPUT type="text" name="tb_optype" size="20" value="1">
						<label for="tb_optype" onclick="document.getElementById('tb_optype').innerText=1">1:订购</label>
						<label for="tb_optype" onclick="document.getElementById('tb_optype').innerText=2">2:修改</label>
						<label for="tb_optype" onclick="document.getElementById('tb_optype').innerText=3">3:删除</label>
						</TD>
					</TR>

					<TR>
						<TD align="right">DEPTINFOMAPLIST:</TD>
						<TD><SELECT size="50" name="lb_deptinfomaplist" multiple style="width:133px;height:80px;overflow=auto">
							<OPTION value="DEPT_ID:2406802">DEPT_ID:2406802</OPTION>
							<OPTION value="DEPT_NAME:其他">DEPT_NAME:其他</OPTION>
							<OPTION value="DEPT_DESC:">DEPT_DESC:</OPTION>
							<OPTION value="DEPT_LEVEL:1">DEPT_LEVEL:1</OPTION>
							<OPTION value="DEPT_SUPPERID:0">DEPT_SUPPERID:0</OPTION></SELECT>
						</TD>
						<TD>
						<p>
						<INPUT type="button" name="b_up" value="↑" onclick="fnUp('lb_deptinfomaplist')">
						K: <INPUT type="text" name="tb_deptinfoname" size="20">
						<INPUT type="button" name="b_add" value=" + " onclick="fnAdd('lb_deptinfomaplist',tb_deptinfoname.value,tb_deptinfovalue.value)">
						<br>
						<INPUT type="button" name="b_down" value="↓" onclick="fnDown('lb_deptinfomaplist')">
						V: <INPUT type="text" name="tb_deptinfovalue" size="20">
						<INPUT type="button" name="b_del" value=" - " onclick="fnRemoveItem('lb_deptinfomaplist')"> 
						</p>
						</TD>
					</TR>
				</TBODY>
			</TABLE>
			</TD>
		</TR>
		<TR>
			<TD bgcolor="olive" width="141">DEPS</TD>
			<TD bgcolor="olive">
			<TABLE border="0">
				<TBODY>
					<TR>
						<TD align="right">DEPTID:</TD>
						<TD colspan="2"><INPUT type="text" name="tb_deptid1" size="20" value="2406803"></TD>
					</TR>
					
					<TR>
						<TD align="right">PARENTID:</TD>
						<TD colspan="2"><INPUT type="text" name="tb_parentid1" size="20" value="-1"></TD>
					</TR>
					
					<TR>
						<TD align="right">DEPNAME:</TD>
						<TD colspan="2"><INPUT type="text" name="tb_depname1" size="20" value="计费组"></TD>
					</TR>
	
					<TR>
						<TD align="right">DEPDES:</TD>
						<TD colspan="2"><INPUT type="text" name="tb_depdes1" size="20"></TD>
					</TR>
					
					<TR>
						<TD align="right">DEPADDRESS:</TD>
						<TD colspan="2"><INPUT type="text" name="tb_depaddress1" size="20"></TD>
					</TR>
										
					<TR>
						<TD align="right">DEPTELNO:</TD>
						<TD colspan="2"><INPUT type="text" name="tb_deptelno1" size="20"></TD>
					</TR>
					
					<TR>
						<TD align="right">DEPFAXNO:</TD>
						<TD colspan="2"><INPUT type="text" name="tb_depfaxno1" size="20"></TD>
					</TR>					
					
					<TR>
						<TD align="right">DEPMNGID:</TD>
						<TD colspan="2"><INPUT type="text" name="tb_depmngid1" size="20"></TD>
					</TR>
					
					<TR>
						<TD align="right">BUILDTIME:</TD>
						<TD colspan="2"><INPUT type="text" name="tb_buildtime1" size="20"></TD>
					</TR>				
					
					<TR>
						<TD align="right">UPDATEDATE:</TD>
						<TD colspan="2"><INPUT type="text" name="tb_updatedate1" size="20"></TD>
					</TR>
										
					<TR>
						<TD align="right">OPTYPE:</TD>
						<TD colspan="2">
						<INPUT type="text" name="tb_optype1" size="20" value="1">
						<label for="tb_optype1" onclick="document.getElementById('tb_optype1').innerText=1">1:订购</label>
						<label for="tb_optype1" onclick="document.getElementById('tb_optype1').innerText=2">2:修改</label>
						<label for="tb_optype1" onclick="document.getElementById('tb_optype1').innerText=3">3:删除</label>
						</TD>
					</TR>
					<TR>
						<TD align="right">DEPTINFOMAPLIST:</TD>
						<TD><SELECT size="50" name="lb_deptinfomaplist1" multiple style="width:133px;height:80px;overflow=auto">
						    <OPTION value="DEPT_ID:2406803">DEPT_ID:2406803</OPTION>
							<OPTION value="DEPT_NAME:计费组">DEPT_NAME:计费组</OPTION>
							<OPTION value="DEPT_DESC:">DEPT_DESC:</OPTION>
							<OPTION value="DEPT_LEVEL:1">DEPT_LEVEL:1</OPTION>
							<OPTION value="DEPT_SUPPERID:0">DEPT_SUPPERID:0</OPTION></SELECT>
						</TD>
						<TD>
						<p>
						<INPUT type="button" name="b_up" value="↑" onclick="fnUp('lb_deptinfomaplist1')">
						K: <INPUT type="text" name="tb_deptinfoname1" size="20">
						<INPUT type="button" name="b_add" value=" + " onclick="fnAdd('lb_deptinfomaplist1',tb_deptinfoname1.value,tb_deptinfovalue1.value)">
						<br>
						<INPUT type="button" name="b_down" value="↓" onclick="fnDown('lb_deptinfomaplist1')">
						V: <INPUT type="text" name="tb_deptinfovalue1" size="20">
						<INPUT type="button" name="b_del" value=" - " onclick="fnRemoveItem('lb_deptinfomaplist1')"> 
						</p>
						</TD>
					</TR>
				</TBODY>
			</TABLE>
			</TD>
		</TR>
	</TBODY>
</TABLE>
</DIV>
</FORM>
</BODY>
</HTML>
