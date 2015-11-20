package com.aesirteam.smep.adc.webservice;

import org.springframework.beans.factory.annotation.Autowired;

import com.aesirteam.smep.adc.message.CorpBindRsp;
import com.aesirteam.smep.adc.message.DeptBindRsp;
import com.aesirteam.smep.adc.message.StaffBindRsp;
import com.aesirteam.smep.adc.services.CorpBindService;
import com.aesirteam.smep.adc.services.DeptBindService;
import com.aesirteam.smep.adc.services.StaffBindService;
import com.aesirteam.smep.util.SecurityTool;

public class APServiceImpl implements APServiceRemote {
	
	@Autowired
	private CorpBindService corpBindService;
	
	@Autowired
	private DeptBindService deptBindService;
	
	@Autowired
	private StaffBindService staffBindService;
	
	public APServiceImpl(String secret) {
		SecurityTool.setDesSecret(secret);
	}
	
	@Override
	public String corpBinding(String msg) {
		// TODO Auto-generated method stub
		try {
			corpBindService.setXmlText(msg);
			int resultcode = corpBindService.execute();
			return new CorpBindRsp(corpBindService.getHeader(), resultcode).toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String deptBinding(String msg) {
		// TODO Auto-generated method stub
		try {
			deptBindService.setXmlText(msg);
			DeptBindRsp deptBindRsp = deptBindService.execute();
			return deptBindRsp.toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String staffBinding(String msg) {
		// TODO Auto-generated method stub
		try {
			staffBindService.setXmlText(msg);
			StaffBindRsp staffBindRsp = staffBindService.execute();
			return staffBindRsp.toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
