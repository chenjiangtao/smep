package com.aesirteam.smep.adc.message;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.Set;

import com.aesirteam.smep.adc.db.domain.SmepAdcStaffInfo;
import com.aesirteam.smep.adc.db.domain.SmepAdcStaffParam;

public class StaffInfo {
	
	private String ufId;
	private String userType;
	private String staffName;
	private String staffMobile;
	private String optype;
	private String opnote;
	private Map<String, String> userInfoMapList;
	
	public SmepAdcStaffInfo convertStaffInfo(String corpaccount){
		SmepAdcStaffInfo info = new SmepAdcStaffInfo();
		info.setUfid(ufId);
		info.setCorpaccount(corpaccount);
		info.setUsertype(Integer.valueOf(userType));
		info.setOptype(Integer.valueOf(optype));
		info.setOpnote(opnote);
		info.setStdate(new Date());
		info.setCreateor(0);
		info.setStaffName(staffName);
		info.setStaffMobile(staffMobile);
		info.setStaffSex("");
		info.setStaffDeptid("");
		return info;
	}
	
	public List<SmepAdcStaffParam> convertStaffParam(String ufid) {
		List<SmepAdcStaffParam> list = new ArrayList<SmepAdcStaffParam>();
		Set<Map.Entry<String, String>> set = userInfoMapList.entrySet();
		for(Iterator<Map.Entry<String, String>> it = set.iterator(); it.hasNext(); ) {
			Map.Entry<String, String> entry = it.next();
			SmepAdcStaffParam smepAdcStaffParam = new SmepAdcStaffParam();
			smepAdcStaffParam.setUfid(ufid);
			smepAdcStaffParam.setParamName(entry.getKey());
			smepAdcStaffParam.setParamValue(entry.getValue());
			list.add(smepAdcStaffParam);
		}
		return list;
	}
	
	/**
	 * @return 用户ID
	 */
	public String getUfId() {
		return ufId;
	}
	
	public void setUfId(String ufId) {
		this.ufId = ufId;
	}
	
	/**
	 * @return 用户类型  0：管理员；1：用户；2：企业通讯录；3：黑白名单
	 */
	public String getUserType() {
		return userType;
	}
	
	public void setUserType(String userType) {
		this.userType = userType;
	}
	
	/**
	 * @return 用户名称
	 */
	public String getStaffName() {
		return staffName;
	}
	
	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}
	
	/**
	 * @return 手机号码
	 */
	public String getStaffMobile() {
		return staffMobile;
	}
	
	public void setStaffMobile(String staffMobile) {
		this.staffMobile = staffMobile;
	}
	
	/**
	 * @return 绑定标志   1:订购（或加入名单）;2:暂停;3:恢复;4:变更;5:退订（或退出名单）
	 */
	public String getOptype() {
		return optype;
	}
	
	public void setOptype(String optype) {
		this.optype = optype;
	}
	
	/**
	 * @return 绑定说明
	 */
	public String getOpnote() {
		return opnote;
	}
	public void setOpnote(String opnote) {
		this.opnote = opnote;
	}
	
	/**
	 * @return 用户信息参数列表
	 */
	public Map<String, String> getUserInfoMapList() {
		return userInfoMapList;
	}
	public void setUserInfoMapList(Map<String, String> userInfoMapList) {
		this.userInfoMapList = userInfoMapList;
	}
}
