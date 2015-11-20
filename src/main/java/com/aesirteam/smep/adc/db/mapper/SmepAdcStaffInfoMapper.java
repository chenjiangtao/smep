package com.aesirteam.smep.adc.db.mapper;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.aesirteam.smep.adc.db.domain.SmepAdcStaffInfo;
import com.aesirteam.smep.core.BaseMapper;

@Repository
public interface SmepAdcStaffInfoMapper extends BaseMapper {
	
	public SmepAdcStaffInfo getData(String ufid);
	
	public int insertData(SmepAdcStaffInfo staffInfo);
	
	public int updateData(SmepAdcStaffInfo staffInfo);
	
	public List<SmepAdcStaffInfo> getDataByCorpAccount(String corpAccount); 
}