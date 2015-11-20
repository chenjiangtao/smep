package com.huawei.insa2.comm;

public class PEventAdapter implements PEventListener {

  public void handle(PEvent e) {
    switch (e.getType()) {
      case PEvent.CHILD_CREATED:
        childCreated((PLayer)e.getData());
        break;
        
      case PEvent.CREATED:
        created();
        break;
        
      case PEvent.DELETED:
        deleted();
        break;
        
      case PEvent.MESSAGE_DISPATCH_FAIL:
        messageDispatchFail((PMessage)e.getData());
        break;
        
      case PEvent.MESSAGE_DISPATCH_SUCCESS:
        messageDispatchFail((PMessage)e.getData());
        break;
        
      case PEvent.MESSAGE_SEND_SUCCESS:
        messageDispatchFail((PMessage)e.getData());
        break;
        
      case PEvent.MESSAGE_SEND_FAIL:
        messageDispatchFail((PMessage)e.getData());
        break;
        
      default:  
    }
  }
  
  public void childCreated(PLayer child) {}
  
  public void messageSendError(PMessage msg) {}
  
  public void messageSendSuccess(PMessage msg) {}
  
  public void messageDispatchFail(PMessage msg) {}
  
  public void messageDispatchSuccess(PMessage msg) {}
  
  public void created(){}
  
  public void deleted(){}
}
