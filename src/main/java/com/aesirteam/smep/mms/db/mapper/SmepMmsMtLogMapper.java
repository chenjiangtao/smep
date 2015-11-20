package com.aesirteam.smep.mms.db.mapper;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.aesirteam.smep.core.BaseMapper;
import com.aesirteam.smep.mms.db.domain.MmsMtLog;

@Repository
public interface SmepMmsMtLogMapper extends BaseMapper {
	
	public MmsMtLog getData(String transactionid);

	public int insertData(List<MmsMtLog> list);

}
