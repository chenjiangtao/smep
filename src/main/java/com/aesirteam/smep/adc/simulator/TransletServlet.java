package com.aesirteam.smep.adc.simulator;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class TransletServlet extends HttpServlet implements Servlet {

	private static final long serialVersionUID = -8689517466582758572L;

	public TransletServlet() {
		super();
	}

	private void printPage(HttpServletResponse response, String message) {
		PrintWriter pw = null;
		try {
			response.setContentType("text/xml;charset=utf-8");
			response.setCharacterEncoding("utf-8");
			response.setHeader("Cache-Control", "no-cache");
			pw = response.getWriter();
			pw.print(message);
			pw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pw != null)
				pw.close();
		}

	}
	
	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		String message = "";
		boolean body_flag = "on".equals(arg0.getParameter("body_flag")) ? true : false;
		arg0.setCharacterEncoding("GBK");
		
		String tb_endpoint = arg0.getParameter("tb_endpoint");
		String tb_secret = arg0.getParameter("teb_secret");
		String tb_code = arg0.getParameter("tb_code");
		
		Util util = new Util();
		util.setEndpoint(tb_endpoint);
		util.setSecret(tb_secret);
		
		if ("CorpBind".equals(tb_code)) {
			try {
				Document document = DocumentHelper.createDocument();
				Element root = document.addElement("CorpBindReq");
				//PUD��Ϣͷ
				Element head = root.addElement("HEAD");
				head.addElement("CODE").addText("CorpBind");
				head.addElement("SID").addText(arg0.getParameter("tb_sid"));
				head.addElement("TIMESTAMP").addText(arg0.getParameter("tb_timestamp"));
				head.addElement("SERVICEID").addText(arg0.getParameter("tb_serviceid"));

				//PDU��Ϣ��
				Document bodydocument = DocumentHelper.createDocument();
				Element body = bodydocument.addElement("BODY");				
				body.addElement("CORPNAME").addText(arg0.getParameter("tb_corpname"));
				body.addElement("CORPACCOUNT").addText(arg0.getParameter("tb_corpaccount"));
				body.addElement("LICENSE").addText(arg0.getParameter("tb_license"));
				body.addElement("OPTYPE").addText(arg0.getParameter("tb_optype"));
				body.addElement("OPNOTE").addText(arg0.getParameter("tb_opnote"));

				Element paramlist = body.addElement("PARAMLIST");
				String[] plist = arg0.getParameterValues("lb_paramlist");
				if (null != plist && 1 <= plist.length) {

					for (int i = 0; i < plist.length; i++) {
						//System.out.println(plist[i]);
						String[] ppv = plist[i].split(":");
						Element parammap = paramlist.addElement("PARAMMAP");
						parammap.addElement("PARAMNAME").addText(ppv[0]);
						if (1 < ppv.length)
							parammap.addElement("PARAMVALUE").addText(ppv[1]);
						else
							parammap.addElement("PARAMVALUE");
					}
				}

				Element corpinfolist = body.addElement("CORPINFOLIST");
				String[] clist = arg0.getParameterValues("lb_corpinfolist");
				if (null != clist && 1 <= clist.length) {
					for (int i = 0; i < clist.length; i++) {
						//System.out.println(clist[i]);
						String[] ppv = clist[i].split(":");
						Element corpinfomap = corpinfolist
								.addElement("CORPINFOMAP");
						corpinfomap.addElement("CORPINFONAME").addText(ppv[0]);
						if (1 < ppv.length)
							corpinfomap.addElement("CORPINFOVALUE").addText(
									ppv[1]);
						else
							corpinfomap.addElement("CORPINFOVALUE");
					}
				}

				Element pointlist = body.addElement("POINTLIST");
				String[] ilist = arg0.getParameterValues("lb_pointlist");
				if (null != ilist && 1 <= ilist.length) {
					for (int i = 0; i < ilist.length; i++) {
						String[] ppv = ilist[i].split(":");
						Element orderpointmap = pointlist
								.addElement("ORDERPOINTMAP");
						orderpointmap.addElement("POINTNAME").addText(ppv[0]);
						if (1 < ppv.length)
							orderpointmap.addElement("POINTVALUE").addText(
									ppv[1]);
						else
							orderpointmap.addElement("POINTVALUE");
					}
				}				
				if (!body_flag) {
					root.addElement("BODY").setText(util.encrypt(util.getXmlText(bodydocument)));
					String rsqstr = util.soapHttpClient("CorpBinding", util.getXmlText(document));
					message = util.decrypt(rsqstr);
				} else {
					message = util.getXmlText(bodydocument);
				}
				util = null;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else if ("DeptBind".equals(tb_code)) {
			try {
				Document document = DocumentHelper.createDocument();
				Element root = document.addElement("DeptBindReq");
				//PUD��Ϣͷ
				Element head = root.addElement("HEAD");
				head.addElement("CODE").addText("DeptBind");
				head.addElement("SID").addText(arg0.getParameter("tb_sid"));
				head.addElement("TIMESTAMP").addText(
						arg0.getParameter("tb_timestamp"));
				head.addElement("SERVICEID").addText(
						arg0.getParameter("tb_serviceid"));

				//PDU��Ϣ��
				Document bodydocument = DocumentHelper.createDocument();
				Element body = bodydocument.addElement("BODY");
				body.addElement("CORPACCOUNT").addText(
						arg0.getParameter("tb_corpaccount"));

				Element deptinfo = body.addElement("DEPS");
				deptinfo.addElement("DEPTID").addText(
						arg0.getParameter("tb_deptid"));			
				deptinfo.addElement("PARENTID").addText(
						arg0.getParameter("tb_parentid"));
				deptinfo.addElement("DEPNAME").addText(
						arg0.getParameter("tb_depname"));
				deptinfo.addElement("DEPDES").addText(
						arg0.getParameter("tb_depdes"));
				deptinfo.addElement("DEPADDRESS").addText(
						arg0.getParameter("tb_depaddress"));
				deptinfo.addElement("DEPTELNO").addText(
						arg0.getParameter("tb_deptelno"));
				deptinfo.addElement("DEPFAXNO").addText(
						arg0.getParameter("tb_depfaxno"));
				deptinfo.addElement("DEPMNGID").addText(
						arg0.getParameter("tb_depmngid"));
				deptinfo.addElement("BUILDTIME").addText(
						arg0.getParameter("tb_buildtime"));
				deptinfo.addElement("UPDATEDATE").addText(
						arg0.getParameter("tb_updatedate"));	
				deptinfo.addElement("OPTYPE").addText(
						arg0.getParameter("tb_optype"));

				Element deptinfomaplist = deptinfo.addElement("DEPTINFOMAPLIST");
				String[] diml = arg0.getParameterValues("lb_deptinfomaplist");				
				if (null != diml && 1 <= diml.length) {
					for (int i = 0; i < diml.length; i++) {
						//System.out.println(diml[i]);
						String[] ppv = diml[i].split(":");
						Element deptinfomap = deptinfomaplist
								.addElement("DEPTINFOMAP");
						deptinfomap.addElement("DEPTINFONAME").addText(ppv[0]);
						if (1 < ppv.length)
							deptinfomap.addElement("DEPTINFOVALUE").addText(
									ppv[1]);
						else
							deptinfomap.addElement("DEPTINFOVALUE");
					}
				}

				Element deptinfo1 = body.addElement("DEPS");
				deptinfo1.addElement("DEPTID").addText(
						arg0.getParameter("tb_deptid1"));
				deptinfo1.addElement("PARENTID").addText(
						arg0.getParameter("tb_parentid1"));
				deptinfo1.addElement("DEPNAME").addText(
						arg0.getParameter("tb_depname1"));
				deptinfo1.addElement("DEPDES").addText(
						arg0.getParameter("tb_depdes1"));
				deptinfo1.addElement("DEPADDRESS").addText(
						arg0.getParameter("tb_depaddress1"));
				deptinfo1.addElement("DEPTELNO").addText(
						arg0.getParameter("tb_deptelno1"));
				deptinfo1.addElement("DEPFAXNO").addText(
						arg0.getParameter("tb_depfaxno1"));
				deptinfo1.addElement("DEPMNGID").addText(
						arg0.getParameter("tb_depmngid1"));
				deptinfo1.addElement("BUILDTIME").addText(
						arg0.getParameter("tb_buildtime1"));
				deptinfo1.addElement("UPDATEDATE").addText(
						arg0.getParameter("tb_updatedate1"));	
				deptinfo1.addElement("OPTYPE").addText(
						arg0.getParameter("tb_optype1"));

				Element deptinfomaplist1 = deptinfo1
						.addElement("DEPTINFOMAPLIST");
				String[] diml1 = arg0.getParameterValues("lb_deptinfomaplist1");
				if (null != diml1 && 1 <= diml1.length) {
					for (int i = 0; i < diml1.length; i++) {
						//System.out.println(diml1[i]);
						String[] ppv = diml1[i].split(":");
						Element deptinfomap = deptinfomaplist1
								.addElement("DEPTINFOMAP");
						deptinfomap.addElement("DEPTINFONAME").addText(ppv[0]);
						if (1 < ppv.length)
							deptinfomap.addElement("DEPTINFOVALUE").addText(
									ppv[1]);
						else
							deptinfomap.addElement("DEPTINFOVALUE");
					}
				}

				if (!body_flag) {
					root.addElement("BODY").setText(
							util.encrypt(util.getXmlText(bodydocument)));
					String rsqstr = util.soapHttpClient("DeptBinding", util
							.getXmlText(document));
					message = util.decrypt(rsqstr);
				} else {
					message = util.getXmlText(bodydocument);
				}
				util = null;

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else if ("StaffBind".equals(tb_code)) {
			try {
				Document document = DocumentHelper.createDocument();
				Element root = document.addElement("StaffBindReq");
				//PUD��Ϣͷ
				Element head = root.addElement("HEAD");
				head.addElement("CODE").addText("StaffBind");
				head.addElement("SID").addText(arg0.getParameter("tb_sid"));
				head.addElement("TIMESTAMP").addText(
						arg0.getParameter("tb_timestamp"));
				head.addElement("SERVICEID").addText(
						arg0.getParameter("tb_serviceid"));

				//PDU��Ϣ��
				Document bodydocument = DocumentHelper.createDocument();
				Element body = bodydocument.addElement("BODY");
				body.addElement("CORPACCOUNT").addText(
						arg0.getParameter("tb_corpaccount"));

				Element stafflist = body.addElement("STAFFLIST");

				Element staffinfo = stafflist.addElement("STAFFINFO");
				staffinfo.addElement("UFID").addText(
						arg0.getParameter("tb_ufid"));
				staffinfo.addElement("USERTYPE").addText(
						arg0.getParameter("tb_usertype"));
				staffinfo.addElement("STAFFNAME").addText(
						arg0.getParameter("tb_staffname"));
				staffinfo.addElement("STAFFMOBILE").addText(
						arg0.getParameter("tb_staffmobile"));
				staffinfo.addElement("OPTYPE").addText(
						arg0.getParameter("tb_optype"));
				staffinfo.addElement("OPNOTE").addText(
						arg0.getParameter("tb_opnote"));

				Element userinfomaplist = staffinfo
						.addElement("USERINFOMAPLIST");
				String[] ufml = arg0.getParameterValues("lb_userinfomaplist");
				//System.out.println(diml.length);
				if (null != ufml && 1 <= ufml.length) {
					for (int i = 0; i < ufml.length; i++) {
						//System.out.println(diml[i]);
						String[] ppv = ufml[i].split(":");
						Element userinfomap = userinfomaplist
								.addElement("USERINFOMAP");
						userinfomap.addElement("USERINFONAME").addText(ppv[0]);
						if (1 < ppv.length)
							userinfomap.addElement("USERINFOVALUE").addText(
									ppv[1]);
						else
							userinfomap.addElement("USERINFOVALUE");
					}
				}

				Element staffinfo1 = stafflist.addElement("STAFFINFO");
				staffinfo1.addElement("UFID").addText(
						arg0.getParameter("tb_ufid1"));
				staffinfo1.addElement("USERTYPE").addText(
						arg0.getParameter("tb_usertype1"));
				staffinfo1.addElement("STAFFNAME").addText(
						arg0.getParameter("tb_staffname1"));
				staffinfo1.addElement("STAFFMOBILE").addText(
						arg0.getParameter("tb_staffmobile1"));
				staffinfo1.addElement("OPTYPE").addText(
						arg0.getParameter("tb_optype1"));
				staffinfo1.addElement("OPNOTE").addText(
						arg0.getParameter("tb_opnote1"));

				Element userinfomaplist1 = staffinfo1
						.addElement("USERINFOMAPLIST");
				String[] ufml1 = arg0.getParameterValues("lb_userinfomaplist1");
				if (null != ufml1 && 1 <= ufml1.length) {
					for (int i = 0; i < ufml1.length; i++) {
						//System.out.println(diml1[i]);
						String[] ppv = ufml1[i].split(":");
						Element userinfomap = userinfomaplist1
								.addElement("USERINFOMAP");
						userinfomap.addElement("USERINFONAME").addText(ppv[0]);
						if (1 < ppv.length)
							userinfomap.addElement("USERINFOVALUE").addText(
									ppv[1]);
						else
							userinfomap.addElement("USERINFOVALUE");
					}
				}

				if (!body_flag) {
					root.addElement("BODY").setText(
							util.encrypt(util.getXmlText(bodydocument)));
					String rsqstr = util.soapHttpClient("StaffBinding", util
							.getXmlText(document));
					message = util.decrypt(rsqstr);
				} else {
					message = util.getXmlText(bodydocument);
				}
				util = null;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		printPage(arg1, message);
	}

	protected void doPost(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		doGet(arg0, arg1);
	}
}