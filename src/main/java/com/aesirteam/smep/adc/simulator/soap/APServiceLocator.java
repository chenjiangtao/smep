/**
 * APServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package com.aesirteam.smep.adc.simulator.soap;

public class APServiceLocator extends org.apache.axis.client.Service implements APService {

	private static final long serialVersionUID = 1L;

	// Use to get a proxy class for APServiceSoap
   private java.lang.String APServiceSoap_address; // = "http://218.201.202.115:8788/services/IfAPService.asmx";
   
    public java.lang.String getAPServiceSoapAddress() {
        return APServiceSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String APServiceSoapWSDDServiceName = "APServiceSoap";

    public java.lang.String getAPServiceSoapWSDDServiceName() {
        return APServiceSoapWSDDServiceName;
    }

    public void setAPServiceSoapWSDDServiceName(java.lang.String name) {
        APServiceSoapWSDDServiceName = name;
    }

    public APServiceSoap getAPServiceSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(APServiceSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            return null; // unlikely as URL was validated in WSDL2Java
        }
        return getAPServiceSoap(endpoint);
    }

    public APServiceSoap getAPServiceSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            APServiceSoapStub _stub = new APServiceSoapStub(portAddress, this);
            _stub.setPortName(getAPServiceSoapWSDDServiceName());
            return (APServiceSoap) _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    /*
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (APServiceSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                APServiceSoapStub _stub = new APServiceSoapStub(new java.net.URL(APServiceSoap_address), this);
                _stub.setPortName(getAPServiceSoapWSDDServiceName());
                return (Remote) _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }
	*/
    
    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    /*
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        java.rmi.Remote _stub = getPort(serviceEndpointInterface);
        ((org.apache.axis.client.Stub) _stub).setPortName(portName);
        return _stub;
    }
	*/
    
    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://eaaapi.eaa.eidc.huawei.com", "APService");
    }

    private java.util.HashSet<javax.xml.namespace.QName> ports = null;

    public java.util.Iterator<javax.xml.namespace.QName> getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet<javax.xml.namespace.QName>();
            ports.add(new javax.xml.namespace.QName("APServiceSoap"));
        }
        return ports.iterator();
    }

}
