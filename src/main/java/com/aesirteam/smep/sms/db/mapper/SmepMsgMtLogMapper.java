package com.aesirteam.smep.sms.db.mapper;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.aesirteam.smep.core.BaseMapper;
import com.aesirteam.smep.sms.db.domain.MsgMtLog;

@Repository
public interface SmepMsgMtLogMapper extends BaseMapper {

	public MsgMtLog getData(String msgid);

	//public int insertData(MsgMtLog msgMtLog);
	
	public int insertData(List<MsgMtLog> list);

	//public int updateData(MsgMtLog msgMtLog);
}