package com.aesirteam.smep.adc.db.mapper;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.aesirteam.smep.adc.db.domain.SmepAdcCorpParam;
import com.aesirteam.smep.core.BaseMapper;

@Repository
public interface SmepAdcCorpParamMapper extends BaseMapper {
	
	public SmepAdcCorpParam getData(SmepAdcCorpParam obj);
	
	public int insertData(List<SmepAdcCorpParam> list);
	
	public int updateData(SmepAdcCorpParam obj);
}