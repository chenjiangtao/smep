package com.aesirteam.smep.adc.db.mapper;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.aesirteam.smep.adc.db.domain.SmepAdcStaffParam;
import com.aesirteam.smep.core.BaseMapper;

@Repository
public interface SmepAdcStaffParamMapper extends BaseMapper {
	
	public SmepAdcStaffParam getData(SmepAdcStaffParam obj);
	
	public int insertData(List<SmepAdcStaffParam> list);
	
	public int updateData(SmepAdcStaffParam obj);
}