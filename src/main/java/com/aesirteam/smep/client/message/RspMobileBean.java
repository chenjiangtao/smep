package com.aesirteam.smep.client.message;

import com.aesirteam.smep.client.MasConstants;

/**
 * 验证手机号码的响应实体
 * 
 * @author yuzhongming
 *
 */
public class RspMobileBean extends ResponeMessage {

	private static final long serialVersionUID = 1L;

	//手机号码总数
	private int totalCount;
	
	//合法号码总数
	private int validCount;
	
	//非法号码列表
	private String invalidMobile;
		
	//非法号码总数
	private int invalidCount;
	
	//移动号码列表
	private String chinaMobile;
	
	private int chinaMobileCount;
	
	//电信号码列表
	private String chinaTelecom;
	
	private int chinaTelecomCount;
	
	//联通号码列表
	private String chinaUnicom;
	
	private int chinaUnicomCount;
	
	
	/**
	 * 获取彩信错误代码对应信息
	 * 
	 * @param errCode  错误代码
	 * @return RspMobileBean
	 */
	public static RspMobileBean getErrorMessage(int errCode) {
		RspMobileBean result = new RspMobileBean();
		switch (errCode) {
			case MasConstants.INIT_MOBILE_VALIDATE_RULE_FAIL :
				result.setRspCode(MasConstants.MOBILE_IS_NULL + "");
				result.setRspMsg("初始化手机号码验证规则失败");
				result.setRspDetail("初始化手机号码验证规则失败");
				break;
			
			case MasConstants.MOBILE_IS_NULL : 
				result.setRspCode(MasConstants.MOBILE_IS_NULL + "");
				result.setRspMsg("待验证手机号码为空");
				result.setRspDetail("待验证手机号码为空");
				break;
				
			default : 
				result.setRspCode(MasConstants.UNKNOWN_EXCEPTION + "");
				result.setRspMsg("");
				result.setRspDetail("");
				break;
		}
		
		return result;
	}


	public int getTotalCount() {
		return totalCount;
	}


	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}


	public int getValidCount() {
		return validCount;
	}


	public void setValidCount(int validCount) {
		this.validCount = validCount;
	}


	public String getInvalidMobile() {
		return invalidMobile;
	}


	public void setInvalidMobile(String invalidMobile) {
		this.invalidMobile = invalidMobile;
	}


	public int getInvalidCount() {
		return invalidCount;
	}


	public void setInvalidCount(int invalidCount) {
		this.invalidCount = invalidCount;
	}


	public String getChinaMobile() {
		return chinaMobile;
	}


	public void setChinaMobile(String chinaMobile) {
		this.chinaMobile = chinaMobile;
	}


	public int getChinaMobileCount() {
		return chinaMobileCount;
	}


	public void setChinaMobileCount(int chinaMobileCount) {
		this.chinaMobileCount = chinaMobileCount;
	}


	public String getChinaTelecom() {
		return chinaTelecom;
	}


	public void setChinaTelecom(String chinaTelecom) {
		this.chinaTelecom = chinaTelecom;
	}


	public int getChinaTelecomCount() {
		return chinaTelecomCount;
	}


	public void setChinaTelecomCount(int chinaTelecomCount) {
		this.chinaTelecomCount = chinaTelecomCount;
	}


	public String getChinaUnicom() {
		return chinaUnicom;
	}


	public void setChinaUnicom(String chinaUnicom) {
		this.chinaUnicom = chinaUnicom;
	}


	public int getChinaUnicomCount() {
		return chinaUnicomCount;
	}


	public void setChinaUnicomCount(int chinaUnicomCount) {
		this.chinaUnicomCount = chinaUnicomCount;
	}
	
}
