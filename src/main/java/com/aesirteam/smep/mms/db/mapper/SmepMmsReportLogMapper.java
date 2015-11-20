package com.aesirteam.smep.mms.db.mapper;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.aesirteam.smep.core.BaseMapper;
import com.aesirteam.smep.mms.db.domain.MmsReportLog;

@Repository
public interface SmepMmsReportLogMapper extends BaseMapper {

	public MmsReportLog getData(String transactionid);

	public int insertData(List<MmsReportLog> list);
}