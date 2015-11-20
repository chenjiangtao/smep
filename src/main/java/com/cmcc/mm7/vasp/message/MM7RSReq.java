/**File Name:MM7RSReq.java
 * Company:  中国移动集团公司
 * Date  :   2004-2-2
 * */

package com.cmcc.mm7.vasp.message;

import com.aesirteam.smep.mms.db.domain.MmsMoLog;
import com.aesirteam.smep.mms.db.domain.MmsReportLog;

public class MM7RSReq extends MM7Message {

	private static final long serialVersionUID = 1L;
	
	protected MmsReportLog mmsReportLog;
	
	protected MmsMoLog mmsMoLog;

	public MmsReportLog getMmsReportLog() {
		return mmsReportLog;
	}

	public MmsMoLog getMmsMoLog() {
		return mmsMoLog;
	}
}