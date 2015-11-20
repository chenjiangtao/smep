<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<HTML>
<HEAD>
<%@ page language="java" contentType="text/html; charset=GBK" pageEncoding="utf-8"%>
<jsp:useBean id="util" class="com.aesirteam.smep.adc.simulator.Util" scope="application"/>
<META name="GENERATOR" content="IBM Software Development Platform">
<META http-equiv="Content-Style-Type" content="text/css">
<LINK href="./theme/Master.css" rel="stylesheet" type="text/css" />
<SCRIPT src="./javascript/func.js"></SCRIPT>
<TITLE>StaffBind-员工帐号绑定接口</TITLE>
</HEAD>
<BODY>
<p><font color="white">首页<a href="../">Back</a>&nbsp;
企业绑定接口<a href="./corpbind.jsp">CorpBind</a>&nbsp;
         部门信息绑定接口<a href="./deptbind.jsp">DeptBind</a>&nbsp;
         员工帐号绑定接口<a href="./staffbind.jsp">StaffBind</a></font><br>
</p>
<FORM method="post" action="../translet" onsubmit="staffbindonsubmit()">
<DIV align="left">
<TABLE border="0" bgcolor="white">
	<TBODY>
		<TR>
			<TD bgcolor="fuchsia" width="10%" align="center">URL</TD>
			<TD colspan="2" height="29" bgcolor="fuchsia">
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
			<TD bgcolor="silver"><INPUT type="text" name="tb_code"
				size="20" value="StaffBind" readonly></TD>
		</TR>
		<TR>
			<TD bgcolor="silver" width="141">SID</TD>
			<TD bgcolor="silver"><INPUT type="text" name="tb_sid" size="20" value="0"></TD>
		</TR>
		<TR>
			<TD bgcolor="silver" width="141">TIMESTAMP</TD>
			<TD bgcolor="silver"><INPUT type="text" name="tb_timestamp" size="20" value=<%=util.getTimestamp()%> readonly>
			</TD>
		</TR>
		<TR>
			<TD bgcolor="silver" width="141">SERVICEID</TD>
			<TD bgcolor="silver"><INPUT type="text" name="tb_serviceid" size="20" value="TGZ5719900"></TD>
		</TR>
		<TR>
			<TD rowspan="4" bgcolor="olive" width="10%" align="center">BODY</TD>
			<TD bgcolor="olive" width="141">COPRACOUNT</TD>
			<TD bgcolor="olive"><INPUT type="text" name="tb_corpaccount" size="20" value="85153312861"></TD>
		</TR>
		<TR>
			<TD bgcolor="olive" width="141">STAFFINFO</TD>
			<TD bgcolor="olive" width="100%">
			<TABLE border="0">
				<TBODY>
					<TR>
						<TD align="right">UFID:</TD>
						<TD colspan="2"><INPUT type="text" name="tb_ufid" size="20" value="UFID000000061084" style="width:170px"></TD></TR>
					<TR>
						<TD align="right">USERTYPE:</TD>
						<TD colspan="2"><INPUT type="text" name="tb_usertype" size="20" value="0" style="width:170px">
						<label for="tb_usertype" onclick="document.getElementById('tb_usertype').innerText=0">0:管理员</label>
						<label for="tb_usertype" onclick="document.getElementById('tb_usertype').innerText=1">1:用户</label>
						<label for="tb_usertype" onclick="document.getElementById('tb_usertype').innerText=2">2:企业通讯录</label>
						<label for="tb_usertype" onclick="document.getElementById('tb_usertype').innerText=3">3:黑白名单</label>
						</TD>
					</TR>

					<TR>
						<TD align="right">STAFFNAME:</TD>
						<TD colspan="2"><INPUT type="text" name="tb_staffname" size="20" value="匿名" style="width:170px"></TD></TR>
					
					<TR>
						<TD align="right">STAFFMOBILE:</TD>
						<TD colspan="2"><INPUT type="text" name="tb_staffmobile" size="20" value="13984419358" style="width:170px">
						</TD>
					</TR>
					
					<TR>
						<TD align="right">OPTYPE:</TD>
						<TD colspan="2"><INPUT type="text" name="tb_optype" size="20" value="1" style="width:170px">
						<label for="tb_optype" onclick="document.getElementById('tb_optype').innerText=1">1:订购</label>
						<label for="tb_optype" onclick="document.getElementById('tb_optype').innerText=2">2:暂停</label>
						<label for="tb_optype" onclick="document.getElementById('tb_optype').innerText=3">3:恢复</label>
						<label for="tb_optype" onclick="document.getElementById('tb_optype').innerText=4">4:变更</label>
						<label for="tb_optype" onclick="document.getElementById('tb_optype').innerText=5">5:退订</label>
						</TD>
					</TR>
					
					<TR>
						<TD align="right">OPNOTE:</TD>
						<TD colspan="2"><INPUT type="text" name="tb_opnote" size="20" style="width:170px"></TD></TR>
					<TR>
						<TD align="right">USERINFOMAPLIST:</TD>
						<TD><SELECT size="50" name="lb_userinfomaplist" multiple style="width:170px;height:80px;overflow=auto">
							<OPTION value="STAFF_BIRTHDAY:">STAFF_BIRTHDAY:</OPTION>
							<OPTION value="STAFF_STAFFNO:">STAFF_STAFFNO:</OPTION>
							<OPTION value="STAFF_NAME:匿名">STAFF_NAME:匿名</OPTION>
							<OPTION value="STAFF_DEPTID:2406803">STAFF_DEPTID:2406802</OPTION>
							<OPTION value="STAFF_SEX:2">STAFF_SEX:2</OPTION>
							<OPTION value="STAFF_TITLE:">STAFF_TITLE:</OPTION>
							<OPTION value="STAFF_PHONE:13984419358">STAFF_PHONE:13984419358</OPTION>
							<OPTION value="STAFF_MOBILE:13984419358">STAFF_MOBILE:13984419358</OPTION>
							<OPTION value="STAFF_USERACCOUNT:13984419358">STAFF_USERACCOUNT:13984419358</OPTION>
							<OPTION value="STAFF_ENGNAME:">STAFF_ENGNAME:</OPTION>
							<OPTION value="STAFF_ADDR:">STAFF_ADDR:</OPTION>
							<OPTION value="STAFF_EMAIL:">STAFF_EMAIL:</OPTION>
							<OPTION value="STAFF_NICKNAME:匿名">STAFF_NICKNAME:匿名</OPTION></SELECT>
						</TD>
						<TD>
						<p>
						<INPUT type="button" name="b_up" value="↑" onclick="fnUp('lb_userinfomaplist')">
						K: <INPUT type="text" name="tb_userinfoname" size="20">
						<INPUT type="button" name="b_add" value=" + " onclick="fnAdd('lb_userinfomaplist',tb_userinfoname.value,tb_userinfovalue.value)">
						<br>
						<INPUT type="button" name="b_down" value="↓" onclick="fnDown('lb_userinfomaplist')">
						V: <INPUT type="text" name="tb_userinfovalue" size="20">
						<INPUT type="button" name="b_del" value=" - " onclick="fnRemoveItem('lb_userinfomaplist')">
						
			</p>
			</TD>
					</TR>
				</TBODY>
			</TABLE>
			</TD>
		</TR>
		<TR>
			<TD bgcolor="olive" width="141">STAFFINFO</TD>
			<TD bgcolor="olive">
			<TABLE border="0">
				<TBODY>
					<TR>
						<TD align="right">UFID:</TD>
						<TD colspan="2"><INPUT type="text" name="tb_ufid1" size="20" value="UFID000000000004" style="width:170px"></TD></TR>
					<TR>
						<TD align="right">USERTYPE:</TD>
						<TD colspan="2"><INPUT type="text" name="tb_usertype1" size="20" value="1" style="width:170px">
						<label for="tb_usertype1" onclick="document.getElementById('tb_usertype1').innerText=0">0:管理员</label>
						<label for="tb_usertype1" onclick="document.getElementById('tb_usertype1').innerText=1">1:用户</label>
						<label for="tb_usertype1" onclick="document.getElementById('tb_usertype1').innerText=2">2:企业通讯录</label>
						<label for="tb_usertype1" onclick="document.getElementById('tb_usertype1').innerText=3">3:黑白名单</label>
						</TD>
					</TR>
					<TR>
						<TD align="right">STAFFNAME:</TD>
						<TD colspan="2"><INPUT type="text" name="tb_staffname1" size="20" value="匿名" style="width:170px"></TD></TR>
					
					<TR>
						<TD align="right">STAFFMOBILE:</TD>
						<TD colspan="2"><INPUT type="text" name="tb_staffmobile1" size="20" value="13984800002" style="width:170px"></TD></TR>
					
					<TR>
						<TD align="right">OPTYPE:</TD>
						<TD colspan="2"><INPUT type="text" name="tb_optype1" size="20" value="1" style="width:170px">
						<label for="tb_optype1" onclick="document.getElementById('tb_optype1').innerText=1">1:订购</label>
						<label for="tb_optype1" onclick="document.getElementById('tb_optype1').innerText=2">2:暂停</label>
						<label for="tb_optype1" onclick="document.getElementById('tb_optype1').innerText=3">3:恢复</label>
						<label for="tb_optype1" onclick="document.getElementById('tb_optype1').innerText=4">4:变更</label>
						<label for="tb_optype1" onclick="document.getElementById('tb_optype1').innerText=5">5:退订</label>
						</TD>
					</TR>
					<TR>
						<TD align="right">OPNOTE:</TD>
						<TD colspan="2"><INPUT type="text" name="tb_opnote1" size="20" style="width:170px"></TD></TR>
					<TR>
						<TD align="right">USERINFOMAPLIST:</TD>
						<TD><SELECT size="50" name="lb_userinfomaplist1" multiple style="width:170px;height:80px;overflow=auto">
							<OPTION value="STAFF_BIRTHDAY:">STAFF_BIRTHDAY:</OPTION>
							<OPTION value="STAFF_STAFFNO:">STAFF_STAFFNO:</OPTION>
							<OPTION value="STAFF_NAME:匿名">STAFF_NAME:匿名</OPTION>
							<OPTION value="STAFF_DEPTID:2406803">STAFF_DEPTID:2406803</OPTION>
							<OPTION value="STAFF_SEX:2">STAFF_SEX:2</OPTION>
							<OPTION value="STAFF_TITLE:">STAFF_TITLE:</OPTION>
							<OPTION value="STAFF_PHONE:13984800002">STAFF_PHONE:13984800002</OPTION>
							<OPTION value="STAFF_MOBILE:13984800002">STAFF_MOBILE:13984800002</OPTION>
							<OPTION value="STAFF_USERACCOUNT:13984800002">STAFF_USERACCOUNT:13984800002</OPTION>
							<OPTION value="STAFF_ENGNAME:">STAFF_ENGNAME:</OPTION>
							<OPTION value="STAFF_ADDR:">STAFF_ADDR:</OPTION>
							<OPTION value="STAFF_EMAIL:">STAFF_EMAIL:</OPTION>
							<OPTION value="STAFF_NICKNAME:匿名">STAFF_NICKNAME:匿名</OPTION></SELECT></TD><TD><p>
						<INPUT type="button" name="b_up" value="↑" onclick="fnUp('lb_userinfomaplist1')">
						K: <INPUT type="text" name="tb_userinfoname1" size="20">
						<INPUT type="button" name="b_add" value=" + " onclick="fnAdd('lb_userinfomaplist1',tb_userinfoname1.value,tb_userinfovalue1.value)">
						<br/>
						<INPUT type="button" name="b_down" value="↓" onclick="fnDown('lb_userinfomaplist1')">
						V: <INPUT type="text" name="tb_userinfovalue1" size="20">
						<INPUT type="button" name="b_del" value=" - " onclick="fnRemoveItem('lb_userinfomaplist1')">
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
