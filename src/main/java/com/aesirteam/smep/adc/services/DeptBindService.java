package com.aesirteam.smep.adc.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.aesirteam.smep.adc.message.DeptBindReq;
import com.aesirteam.smep.adc.message.DeptBindRsp;
import com.aesirteam.smep.adc.message.DeptInfo;
import com.aesirteam.smep.adc.message.DeptInfoResult;

@Service
public class DeptBindService {
	
	private String xmlText;
	
	public DeptBindRsp execute() throws Exception {
		//由于业务不涉及部门，因此可以忽略业务处理及数据库相关操作
		DeptBindReq deptBindReq = new DeptBindReq(xmlText);
		
		List<DeptInfoResult> result = new ArrayList<DeptInfoResult>();
		for(DeptInfo info : deptBindReq.getDeptList())
		{
			DeptInfoResult deptInfoResult = new DeptInfoResult();
			deptInfoResult.setDeptId(info.getDeptId());
			deptInfoResult.setResultcode(String.valueOf(AdcConstant.DEPT_SYNC_SUCCESS));
			deptInfoResult.setResultmsg("部门信息同步成功");
			result.add(deptInfoResult);
		}	
		return new DeptBindRsp(deptBindReq.getHeader(), deptBindReq.getCorpAccount(), result);
	}
	
	
	public String getXmlText() {
		return xmlText;
	}

	public void setXmlText(String xmlText) {
		this.xmlText = xmlText;
	}
}
