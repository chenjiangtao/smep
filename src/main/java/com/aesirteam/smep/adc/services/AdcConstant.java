package com.aesirteam.smep.adc.services;

public class AdcConstant {
	
	/**
	 * @see CORP_ORDER_SUCCESS EC订购产品成功
	 */
	public static final int HEADER_SERVICEID_ISNULL = -10;
	
	/**
	 * @see CORP_ORDER_SUCCESS EC订购产品成功
	 */
	public static final int CORP_ORDER_SUCCESS = 0;
	
	/**
	 * @see CORP_ORDER_NOTFOUND EC订购关系不存在
	 */
	public static final int CORP_ORDER_NOTFOUND = 100;
	
	/**
	 * @see CORP_ORDER_EXISTS EC订购关系已存在
	 */
	public static final int CORP_ORDER_EXISTS = 2;
	
	/**
	 * @see CORP_ORDER_FAIL EC订购产品失败
	 */
	public static final int CORP_ORDER_FAIL = 3;
	
	/**
	 * @see CORP_STATUS_CHANGE_SUCCESS EC状态变更成功
	 */
	public static final int CORP_STATUS_CHANGE_SUCCESS = 4;
	
	/**
	 * @see CORP_INFO_CHANGE_SUCCESS EC信息变更成功
	 */
	public static final int CORP_INFO_CHANGE_SUCCESS = 5;
	
	/**
	 * @see CORP_UNORDER_SUCCESS EC取消订购成功
	 */
	public static final int CORP_UNORDER_SUCCESS = 6;
	
	/**
	 * @see CORP_STATUS_UNORDER EC状态变更成功
	 */
	public static final int CORP_STATUS_UNORDER = 7;
	
	/**
	 * @see CORP_ACCOUNT_ISNULL EC编号为空
	 */
	public static final int CORP_ACCOUNT_ISNULL = -11;
	
	/**
	 * @see CORP_NAME_ISNULL EC名称为空
	 */
	public static final int CORP_NAME_ISNULL = 9;
	
	/**
	 * @see CORP_POINT_ISNULL 产品列表为空
	 */
	public static final int CORP_POINT_ISNULL = 10;
	
	/**
	 * @see CORP_OPTYPE_ISNULL 业务绑定指令为空
	 */
	public static final int CORP_OPTYPE_ISNULL = 11;
	
	/**
	 * @see CORP_OPTYPE_FAIL 业务绑定指令错误
	 */
	public static final int CORP_OPTYPE_FAIL = 12;
	
	/**
	 * @see CORP_PARAM_FAIL EC业务配置参数错误
	 */
	public static final int CORP_PARAM_FAIL = 401;
	
	/**
	 * @see DEPT_SYNC_SUCCESS 部门信息同步成功
	 */
	public static final int DEPT_SYNC_SUCCESS = 0;
	
	/**
	 * @see DEPT_SYNC_FAIL 部门信息同步失败
	 */
	public static final int DEPT_SYNC_FAIL = 1;
	
	/**
	 * @see STAFF_ORDER_SUCCESS 用户订购产品成功
	 */
	public static final int STAFF_ORDER_SUCCESS = 0;
	
	/**
	 * @see STAFF_ORDER_NOTFOUND 用户订购关系不存在
	 */
	public static final int STAFF_ORDER_NOTFOUND = 1;
	
	/**
	 * @see STAFF_ORDER_EXISTS 用户订购关系已存在
	 */
	public static final int STAFF_ORDER_EXISTS = 2;
	
	/**
	 * @see STAFF_ORDER_FAIL 用户订购产品失败
	 */
	public static final int STAFF_ORDER_FAIL = 3;
	
	/**
	 * @see STAFF_STATUS_CHANGE_SUCCESS 用户状态变更成功
	 */
	public static final int STAFF_STATUS_CHANGE_SUCCESS = 4;
	
	/**
	 * @see STAFF_INFO_CHANGE_SUCCESS 用户信息变更成功
	 */
	public static final int STAFF_INFO_CHANGE_SUCCESS = 5;
	
	/**
	 * @see STAFF_UNORDER_SUCCESS 取消用户订购成功
	 */
	public static final int STAFF_UNORDER_SUCCESS = 6;
	
	/**
	 * @see STAFF_STATUS_UNORDER 用户状态变更成功
	 */
	public static final int STAFF_STATUS_UNORDER = 7;
	
	/**
	 * @see STAFF_PARAM_FAIL EC业务配置参数错误
	 */
	public static final int STAFF_PARAM_FAIL = 8;
	
	/**
	 * @see STAFF_MOBILE_FAIL 用户手机号码错误
	 */
	public static final int STAFF_MOBILE_FAIL = 9;
	
	/**
	 * @see STAFF_UFID_ISNULL UFID为空
	 */
	public static final int STAFF_UFID_ISNULL = 10;
	
	/**
	 * @see STAFF_USERTYPE_ISNULL USERTYPE为空
	 */
	public static final int STAFF_USERTYPE_ISNULL = 11;
	
	/**
	 * @see STAFF_OPTYPE_ISNULL OPTYPE为空
	 */
	public static final int STAFF_OPTYPE_ISNULL = 12;
	
	/**
	 * @see STAFF_OPTYPE_FAIL OPTYPE错误
	 */
	public static final int STAFF_OPTYPE_FAIL = 13;
	
	/**
	 * @see STAFF_USERTYPE_FAIL USERTYPE错误
	 */
	public static final int STAFF_USERTYPE_FAIL = 14;
}
