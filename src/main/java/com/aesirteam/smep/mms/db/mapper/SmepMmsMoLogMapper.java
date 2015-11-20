package com.aesirteam.smep.mms.db.mapper;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.aesirteam.smep.core.BaseMapper;
import com.aesirteam.smep.mms.db.domain.MmsMoLog;

@Repository
public interface SmepMmsMoLogMapper extends BaseMapper {

	public MmsMoLog getData(String transactionid);

	public int insertData(List<MmsMoLog> list);
}