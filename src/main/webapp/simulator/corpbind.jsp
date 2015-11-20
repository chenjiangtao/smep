<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<HTML>
<HEAD>
<%@ page language="java" contentType="text/html; charset=GBK" pageEncoding="utf-8"%>
<jsp:useBean id="util" class="com.aesirteam.smep.adc.simulator.Util" scope="application"/>
<META name="GENERATOR" content="IBM Software Development Platform">
<META http-equiv="Content-Style-Type" content="text/css">
<LINK href="./theme/Master.css" rel="stylesheet" type="text/css" />
<SCRIPT src="./javascript/func.js"></SCRIPT>
<TITLE>CorpBind-企业绑定接口</TITLE>
</HEAD>
<BODY>
<p><font color="white">首页<a href="../">Back</a>&nbsp;
         企业绑定接口<a href="./corpbind.jsp">CorpBind</a>&nbsp;
         部门信息绑定接口<a href="./deptbind.jsp">DeptBind</a>&nbsp;
         员工帐号绑定接口<a href="./staffbind.jsp">StaffBind</a>
  </font></p>
<FORM method="post" action="../translet" onsubmit="corpbindonsubmit()">
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
			<TD bgcolor="silver"><INPUT type="text" name="tb_code"
				size="20" value="CorpBind" readonly></TD>
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
			<TD bgcolor="silver"><INPUT type="text" name="tb_serviceid" size="20"
				value="TGZ5719900"></TD>
		</TR>
		<TR>
			<TD rowspan="14" bgcolor="olive" width="10%" align="center">BODY</TD>
			<TD bgcolor="olive" width="141">CORPNAME</TD>
			<TD bgcolor="olive"><INPUT type="text" name="tb_corpname" size="20"
				maxlength="100" value="ADC联调集团2012011801"></TD>
		</TR>
		<TR>
			<TD bgcolor="olive" width="141">COPRACOUNT</TD>
			<TD bgcolor="olive"><INPUT type="text" name="tb_corpaccount" size="20" value="85153312861"></TD>
		</TR>
		<TR>
			<TD bgcolor="olive" width="141">LICENSE</TD>
			<TD bgcolor="olive"><INPUT type="text" name="tb_license" size="20"
				value="9999999"></TD>
		</TR>
		<TR>
			<TD bgcolor="olive" width="141">OPTYPE</TD>
			<TD bgcolor="olive" width="100%">
			<INPUT type="text" name="tb_optype" size="20" value="1">&nbsp;
			<label for="tb_optype" onclick="document.getElementById('tb_optype').innerText=1">1:订购</label>
			<label for="tb_optype" onclick="document.getElementById('tb_optype').innerText=2">2:暂停</label>
			<label for="tb_optype" onclick="document.getElementById('tb_optype').innerText=3">3:恢复</label>
			<label for="tb_optype" onclick="document.getElementById('tb_optype').innerText=4">4:变更</label>
			<label for="tb_optype" onclick="document.getElementById('tb_optype').innerText=5">5:退订</label>
			</TD>
		</TR>
		<TR>
			<TD bgcolor="olive" width="141">OPNOTE</TD>
			<TD bgcolor="olive"><INPUT type="text" name="tb_opnote" size="75" value="http://www.aesirteam.com"></TD>
		</TR>
		<TR>
			<TD bgcolor="olive" width="141">PARAMLIST</TD>
			<TD bgcolor="olive">
			<TABLE border="0">
				<TBODY>
					<TR>
						<TD><SELECT size="8" name="lb_paramlist" multiple style="width:260px;height:80px;overflow=auto">
							<OPTION value="PARAM_SUBSCRIBID:502020600000000513">PARAM_SUBSCRIBID:502020600000000513</OPTION>
							<OPTION value="AccessNumber:106573332236">AccessNumber:106573332236</OPTION>
						</SELECT></TD>
						<TD>
						<p>
						<INPUT type="button" name="b_up1" value="↑" onclick="fnUp('lb_paramlist')"> 
						K: <INPUT type="text" name="tb_paramname" size="20">
						<INPUT type="button" name="b_add1" value=" + " onclick="fnAdd('lb_paramlist',tb_paramname.value,tb_paramvalue.value)">
						<br/>
						<INPUT type="button" name="b_down1" value="↓" onclick="fnDown('lb_paramlist')"> 
						V: <INPUT type="text" name="tb_paramvalue" size="20">
						<INPUT type="button" name="b_del1" value=" - "onclick="fnRemoveItem('lb_paramlist')">
						</p>
						</TD>
					</TR>
				</TBODY>
			</TABLE>
			</TD>
		</TR>
		<TR>
			<TD bgcolor="olive" width="141">CORPINFOLIST</TD>
			<TD bgcolor="olive">
			<TABLE border="0">
				<TBODY>
					<TR>
						<TD><SELECT size="50" name="lb_corpinfolist" multiple style="width:260px;height:80px;overflow=auto">
							<OPTION value="CORP_DESC:">CORP_DESC:</OPTION>
							<OPTION value="CORP_CMSTAFFNO:">CORP_CMSTAFFNO:</OPTION>
							<OPTION value="CORP_AREAID:">CORP_AREAID:</OPTION>
							<OPTION value="CORP_LINKPHONE:15812430835">CORP_LINKPHONE:15812430835</OPTION>
							<OPTION value="CORP_PROVCODE:210">CORP_PROVCODE:210</OPTION>
							<OPTION value="CORP_RANK:3">CORP_RANK:3</OPTION>
							<OPTION value="CORP_TYPE:">CORP_TYPE:</OPTION>
							<OPTION value="CORP_LINKMOBILE:15812430835">CORP_LINKMOBILE:15812430835</OPTION>
							<OPTION value="CORP_STATE:0">CORP_STATE:0</OPTION>
							<OPTION value="CORP_ADDR:">CORP_ADDR:</OPTION>
							<OPTION value="CORP_BUREAUID:">CORP_BUREAUID:</OPTION>
							<OPTION value="CORP_AREACODE:">CORP_AREACODE:</OPTION>
							<OPTION value="CORP_SHORTNAME:ADC联调">CORP_SHORTNAME:ADC联调</OPTION>
							<OPTION value="CORP_FAX:">CORP_FAX:</OPTION>
							<OPTION value="CORP_HEADPHONE:15812430835">CORP_HEADPHONE:15812430835</OPTION>
							<OPTION value="CORP_LINKMAN:张总">CORP_LINKMAN:张总</OPTION></SELECT>
						</TD>
						<TD>
						<p>
						<INPUT type="button" name="b_up2" value="↑" onclick="fnUp('lb_corpinfolist')">
						K: <INPUT type="text" name="tb_corpinfoname" size="20">
						<INPUT type="button" name="b_add2" value=" + " onclick="fnAdd('lb_corpinfolist',tb_corpinfoname.value,tb_corpinfovalue.value)"> 
						<br>
						<INPUT type="button" name="b_down2" value="↓" onclick="fnDown('lb_corpinfolist')">
						V: <INPUT type="text" name="tb_corpinfovalue" size="20">
						<INPUT type="button" name="b_del2" value=" - " onclick="fnRemoveItem('lb_corpinfolist')">
						</p>
						</TD>
					</TR>
				</TBODY>
			</TABLE>
			</TD>
		</TR>
		<TR>
			<TD bgcolor="olive" width="141">POINTLIST</TD>
			<TD bgcolor="olive">
			<TABLE border="0">
				<TBODY>
					<TR>
						<TD><SELECT size="50" name="lb_pointlist" multiple style="width:260px;height:80px;overflow=auto">
							<OPTION value="MGZ6910100:贵州省地税网络发票">MGZ6910100:贵州省地税网络发票</OPTION></SELECT></TD>
						<TD>
						<p>
						<INPUT type="button" name="b_up3" value="↑" onclick="fnUp('lb_pointlist')">
						K: <INPUT type="text" name="tb_point" size="20">
						<INPUT type="button" name="b_add3" value=" + " onclick="fnAdd('lb_pointlist',tb_point.value,tb_point_value.value)">
						<br>
						<INPUT type="button" name="b_down3" value="↓" onclick="fnDown('lb_pointlist')">
						V: <INPUT type="text" name="tb_point_value" size="20">
						<INPUT type="button" name="b_del3" value=" - " onclick="fnRemoveItem('lb_pointlist')"> 	 
				 </p>
				 </TD>
				</TR>
				</TBODY>
			</TABLE></TD>
		</TR>
	</TBODY>
</TABLE>
</DIV>
</FORM>
</BODY>
</HTML>
