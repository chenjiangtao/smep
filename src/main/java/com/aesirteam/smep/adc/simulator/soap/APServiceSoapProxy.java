package com.aesirteam.smep.adc.simulator.soap;

public class APServiceSoapProxy implements APServiceSoap {
  private String _endpoint = null;
  private APServiceSoap aPServiceSoap = null;
  
  public APServiceSoapProxy() {
    _initAPServiceSoapProxy();
  }
  
 private void _initAPServiceSoapProxy() {
    try {
      aPServiceSoap = (new APServiceLocator()).getAPServiceSoap();
      if (aPServiceSoap != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)aPServiceSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)aPServiceSoap)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (aPServiceSoap != null)
      ((javax.xml.rpc.Stub)aPServiceSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public APServiceSoap getAPServiceSoap() {
    if (aPServiceSoap == null)
      _initAPServiceSoapProxy();
    return aPServiceSoap;
  }
  
  public java.lang.String corpBinding(java.lang.String msg) throws java.rmi.RemoteException{
    if (aPServiceSoap == null)
      _initAPServiceSoapProxy();
    return aPServiceSoap.corpBinding(msg);
  }
  
  public java.lang.String deptBinding(java.lang.String msg) throws java.rmi.RemoteException{
    if (aPServiceSoap == null)
      _initAPServiceSoapProxy();
    return aPServiceSoap.deptBinding(msg);
  }
  
  public java.lang.String staffBinding(java.lang.String msg) throws java.rmi.RemoteException{
    if (aPServiceSoap == null)
      _initAPServiceSoapProxy();
    return aPServiceSoap.staffBinding(msg);
  }
  
  
}