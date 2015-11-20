/***********************************************************************
 * Module:  ResponeMessage.java
 * Author:  ShiHukui
 * Purpose: Defines the Class ResponeMessage
 ***********************************************************************/

package com.aesirteam.smep.client.message;

import java.io.Serializable;

public abstract class ResponeMessage implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 返回码 */
   private String rspCode;
   /** 返回消息 */
   private String rspMsg;
   /** 返回详情  */
   private String rspDetail;
   
   public String getRspCode() {
      return rspCode;
   }
   
   /** @param newRspCode */
   public void setRspCode(String newRspCode) {
      rspCode = newRspCode;
   }
   
   public String getRspMsg() {
      return rspMsg;
   }
   
   /** @param newRspMsg */
   public void setRspMsg(String newRspMsg) {
      rspMsg = newRspMsg;
   }
   
   public String getRspDetail() {
      return rspDetail;
   }
   
   /** @param newRspDetail */
   public void setRspDetail(String newRspDetail) {
      rspDetail = newRspDetail;
   }
}