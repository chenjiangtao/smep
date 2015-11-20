package com.aesirteam.smep.resources;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;
import com.huawei.insa2.util.Args;

@Component("sysParams")
public class SysParams {
	
	protected Args args;
	
	public SysParams() {
		args = new Args();
	}
	
	public SysParams(Map<String, Object> sourceMap) {
		args = new Args(sourceMap);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getVal(String key, Class<T> type) {
		if (type.equals(String.class)) {
			return (T)args.get(key, null);
		} else if (type.equals(Integer.class)) {
			return (T)Integer.valueOf(args.get(key, null));
		} else if (type.equals(Long.class)) {
			return (T)Long.valueOf(args.get(key, null));
		} else if (type.equals(Float.class)) {
			return (T)Float.valueOf(args.get(key, null));
		} else if (type.equals(Boolean.class)) {
			return (T)Boolean.valueOf(args.get(key, null));
		}
		return null;
	}
	
	public void set(String key, Object val) {	
		if (val instanceof String) {
			args.set(key, val + "");
		} else if (val instanceof Integer) {
			args.set(key, Integer.parseInt(val + ""));
		} else if (val instanceof Long) {
			args.set(key, Long.parseLong(val + ""));
		} else if (val instanceof Float) {
			args.set(key, Float.parseFloat(val + ""));
		} else if (val instanceof Boolean) {
			args.set(key, Boolean.parseBoolean(val + ""));
		} else {
			args.set(key, val);
		}
	}
	
	public String getParams() {
		StringBuffer sb = new StringBuffer();
		Set<Map.Entry<String, Object>> set = args.getArgs().entrySet();
		for(Iterator<Map.Entry<String, Object>> it = set.iterator(); it.hasNext(); ) {
			Map.Entry<String, Object> entry = it.next();
			sb.append("\t").append(entry.getKey()).append("=").append(entry.getValue()).append("\r\n");
		}
		return sb.toString();
	}
}
