package com.huawei.insa2.comm;

import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.Field;

public class PEvent {

  public static final int CREATED = 1;

  public static final int CHILD_CREATED = 2;

  public static final int DELETED = 4;

  public static final int MESSAGE_SEND_SUCCESS = 8;

  public static final int MESSAGE_SEND_FAIL = 16;

  public static final int MESSAGE_DISPATCH_SUCCESS = 32;

  public static final int MESSAGE_DISPATCH_FAIL = 64;

  static final Map<Object, String> names = new HashMap<Object, String>();
  
  static {
    try {
      Field[] f = PEvent.class.getFields();
      for (int i=0;i<f.length;i++) {
        String name = f[i].getName();
        Object id = f[i].get(null);
        names.put(id,name);
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  protected PLayer source;

  protected int type;

  protected Object data;

  public PEvent(int type,PLayer source,Object data) {
    this.type = type;
    this.source = source;
    this.data = data;
  }

  public PLayer getSource() {
    return source;
  }

  public int getType() {
    return type;
  }

  public Object getData() {
    return data;
  }

  public String toString() {
    return "PEvent:source=" + source + ",type=" +
           names.get(new Integer(type)) + ",data=" + data;
  }
}