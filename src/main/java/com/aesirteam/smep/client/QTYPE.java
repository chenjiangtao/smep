package com.aesirteam.smep.client;

public enum QTYPE {
	
	   /**
	    * 短信MT全部队列(SUBMIT:*)
	    */
	   SMS_SUBMIT(1000),
	   
		/**
	    * 短信待发送总条数 
	    */
	   SMS_SUBMIT_TOTAL(1001),
		
	   /**
	    * 短信发送失败条数 
	    */
	   SMS_SUBMIT_FAIL(1002),
	   
	   /**
	    * 短信发送失败号码 
	    */
	   SMS_SUBMIT_FAIL_MOBILES(1003),
	   
	   /**
	    * 短信发送成功条数 
	    */
	   SMS_SUBMIT_OK(1004),
		
	   
	   /**
	    * 短信剩余发送条数 
	    */
	   SMS_SUBMIT_SURPLUS(1005),
	   
	   /**
	    * 短信状态报告全部队列 (MTSTAT:*)
	    */
	   SMS_MTSTAT(1010),
	   
	   /**
	    * 短信状态报告:成功
	    */
	   SMS_MTSTAT_DELIVED(1011),
	   
	   /**
	    * 短信状态报告:过期
	    */
	   SMS_MTSTAT_EXPIRED(1012),
	   
	   /**
	    * 短信状态报告:已删除 
	    */
	   SMS_MTSTAT_DELETED(1013),
	   
	   /**
	    * 短信状态报告:失败
	    */
	   SMS_MTSTAT_UNDELIV(1014),
	   
	   /**
	    * Message is in accepted state(i.e. has been manually read on behalf of the subscriber by customer service)
	    */
	   SMS_MTSTAT_ACCEPTD(1015),
	   
	   /**
	    * Message is in invalid state
	    */
	   SMS_MTSTAT_UNKNOWN(1016),
	   
	   /**
	    * Message is in a rejected state
	    */
	   SMS_MTSTAT_REJECTD(1017),

	   /**
	    * 彩信MT全部队列
	    */
	   MMS_SUBMIT(2000),
	   
	   /**
	    * 彩信待发送总条数 
	    */
	   MMS_SUBMIT_TOTAL(2001),
		
	   /**
	    * 彩信发送失败条数 
	    */
	   MMS_SUBMIT_FAIL(2002),
	   
	   /**
	    * 彩信发送失败号码 
	    */
	   MMS_SUBMIT_FAIL_MOBILES(2003),
	   
	   /**
	    * 彩信发送成功条数 
	    */
	   MMS_SUBMIT_OK(2004),
		
	   
	   /**
	    * 彩信剩余发送条数 
	    */
	   MMS_SUBMIT_SURPLUS(2005);
	   
	   private final int value;
	   
	   QTYPE(int value) {
		   this.value = value;
	   }

	   public int getValue() {
		return value;
	   }
}
