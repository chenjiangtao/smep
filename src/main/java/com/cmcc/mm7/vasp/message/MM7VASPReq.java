/**File Name:MM7VASPReq.java
 * Company:  中国移动集团公司
 * Date  :   2004-2-2
 * */

package com.cmcc.mm7.vasp.message;

import com.aesirteam.smep.mms.db.domain.MmsMtLog;

public class MM7VASPReq extends MM7Message
{
	private static final long serialVersionUID = 1L;
	
    protected MmsMtLog mmsMtLog;

	public MmsMtLog getMmsMtLog() {
		return mmsMtLog;
	}
    
}