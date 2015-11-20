package com.aesirteam.smep.core;

public interface BaseMapper {

	public abstract int insertData(Object obj);

	public abstract int updateData(Object obj);

	public abstract Object getData(Object obj);
	
	public abstract Object getAllData(Object obj);
}
