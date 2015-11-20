package com.aesirteam.smep.adc.webservice;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.springframework.remoting.jaxrpc.ServletEndpointSupport;

@SuppressWarnings("deprecation")
public class APService extends ServletEndpointSupport implements APServiceRemote{
	
	private APServiceRemote service;
	
	protected void onInit() throws ServiceException {
		service = (APServiceRemote)getApplicationContext().getBean("APService");
	}
	
	@Override
	public String corpBinding(String msg) throws RemoteException {
		// TODO Auto-generated method stub
		return service.corpBinding(msg);
	}

	@Override
	public String deptBinding(String msg) throws RemoteException {
		// TODO Auto-generated method stub
		return service.deptBinding(msg);
	}

	@Override
	public String staffBinding(String msg) throws RemoteException {
		// TODO Auto-generated method stub
		return service.staffBinding(msg);
	}

}
