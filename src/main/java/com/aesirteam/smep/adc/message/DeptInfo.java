package com.aesirteam.smep.adc.message;

import java.util.Map;

public class DeptInfo {
	
	private String deptId;
	private String parentId;
	private String depName;
	private String depDes;
	private String depAddress;
	private String depTelNo;
	private String depFaxNo;
	private String depMngId;
	private String buildTime;
	private String updateDate;
	private String optype;
	private Map<String, String> deptInfoMapList;
	
	/**
	 * @return 部门ID
	 */
	public String getDeptId() {
		return deptId;
	}
	
	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}
	
	/**
	 * @return 上级部门ID
	 */
	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	
	/**
	 * @return 部门名称
	 */
	public String getDepName() {
		return depName;
	}

	public void setDepName(String depName) {
		this.depName = depName;
	}
	
	/**
	 * @return 部门描述
	 */
	public String getDepDes() {
		return depDes;
	}

	public void setDepDes(String depDes) {
		this.depDes = depDes;
	}
	
	/**
	 * @return 部门地址
	 */
	public String getDepAddress() {
		return depAddress;
	}

	public void setDepAddress(String depAddress) {
		this.depAddress = depAddress;
	}
	
	/**
	 * @return 部门电话
	 */
	public String getDepTelNo() {
		return depTelNo;
	}

	public void setDepTelNo(String depTelNo) {
		this.depTelNo = depTelNo;
	}
	
	/**
	 * @return 传真号
	 */
	public String getDepFaxNo() {
		return depFaxNo;
	}

	public void setDepFaxNo(String depFaxNo) {
		this.depFaxNo = depFaxNo;
	}
	
	/**
	 * @return 部门负责人
	 */
	public String getDepMngId() {
		return depMngId;
	}

	public void setDepMngId(String depMngId) {
		this.depMngId = depMngId;
	}
	
	/**
	 * @return 部门创建时间
	 */
	public String getBuildTime() {
		return buildTime;
	}

	public void setBuildTime(String buildTime) {
		this.buildTime = buildTime;
	}
	
	/**
	 * @return 变更时间
	 */
	public String getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}

	/**
	 * @return 绑定标志   1: 新增; 2: 修改; 3: 删除
	 */
	public String getOptype() {
		return optype;
	}
	
	public void setOptype(String optype) {
		this.optype = optype;
	}
	
	/**
	 * @return 部门信息参数列表
	 */
	public Map<String, String> getDeptInfoMapList() {
		return deptInfoMapList;
	}

	public void setDeptInfoMapList(Map<String, String> deptInfoMapList) {
		this.deptInfoMapList = deptInfoMapList;
	}
}
