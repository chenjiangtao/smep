package com.aesirteam.smep.adc.webservice;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface APServiceRemote extends Remote {
	/**
	 * ADC-SI 企业绑定接口
	 * 
	 * @param msg
	 * @return
	 */
	public String corpBinding(String msg) throws RemoteException;;

	/**
	 * ADC- SI 部门信息绑定接口
	 * 
	 * @param msg
	 * @return
	 */
	public String deptBinding(String msg) throws RemoteException;;

	/**
	 * ADC- SI 用户信息绑定接口
	 * 
	 * @param msg
	 * @return
	 */
	public String staffBinding(String msg) throws RemoteException;;
}
