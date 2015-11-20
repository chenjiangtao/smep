package com.aesirteam.smep.core.db.mapper;

import java.util.List;

import org.springframework.stereotype.Repository;
import com.aesirteam.smep.core.BaseMapper;
import com.aesirteam.smep.core.db.domain.SmepSysService;

@Repository
public interface SmepSysServiceMapper extends BaseMapper {
	
	List<SmepSysService> getAllData(SmepSysService record);

	int updateData(SmepSysService record);
}