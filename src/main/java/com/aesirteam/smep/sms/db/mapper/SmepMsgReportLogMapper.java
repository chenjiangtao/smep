package com.aesirteam.smep.sms.db.mapper;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.aesirteam.smep.core.BaseMapper;
import com.aesirteam.smep.sms.db.domain.MsgMoLog;

@Repository
public interface SmepMsgReportLogMapper extends BaseMapper {

	public MsgMoLog getData(String msgid);

	public int insertData(List<MsgMoLog> list);

	//public int updateData(MsgMoLog msgMoLog);
}