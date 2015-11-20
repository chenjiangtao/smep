package com.aesirteam.smep.core.db.mapper;

import java.util.List;

import org.springframework.stereotype.Repository;
import com.aesirteam.smep.core.BaseMapper;
import com.aesirteam.smep.core.db.domain.SmepSysParam;

@Repository
public interface SmepSysParamMapper extends BaseMapper {
    
	List<SmepSysParam> getData(String className);
	
    int insertData(SmepSysParam record);

    int updateData(SmepSysParam record);
}