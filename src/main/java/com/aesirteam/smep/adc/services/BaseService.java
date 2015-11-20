package com.aesirteam.smep.adc.services;

import com.aesirteam.smep.core.BaseMapper;

public abstract class BaseService {

	public BaseService() {}

	public BaseMapper getMapper() {
		return mapper;
	}

	public int insertData(Object obj) {
		return getMapper().insertData(obj);
	}

	public int updateData(Object obj) {
		return getMapper().updateData(obj);
	}

	public Object getData(Object obj) {
		return getMapper().getData(obj);
	}
		
	private BaseMapper mapper;
}
