package com.aesirteam.smep.adc.db.mapper;

import org.springframework.stereotype.Repository;

import com.aesirteam.smep.adc.db.domain.SmepAdcCorpInfo;
import com.aesirteam.smep.core.BaseMapper;

@Repository
public interface SmepAdcCorpInfoMapper extends BaseMapper {
	
	public SmepAdcCorpInfo getData(String corpAccount);
	
	public int insertData(SmepAdcCorpInfo corpInfo);
	
	public int updateData(SmepAdcCorpInfo corpInfo);
		
}