package com.aesirteam.smep.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component("illegalContentFilter")
public class IllegalContentFilter {
	
	Map<String, String> rulesMap = new HashMap<String, String>();
	
	/**
	 * 文本内容检查
	 * @param content  内容字符串
	 * @return 违反规则的内容列表
	 */
	public List<String> check(String content) {
		List<String> result = new ArrayList<String>();
		content = content.replaceAll("\\s+", "");
		
		for(Iterator<Map.Entry<String, String>> it = rulesMap.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<String, String> entry = it.next();
			//System.out.println(entry.getValue() + ":" + content);
			Matcher matcher = Pattern.compile(entry.getValue()).matcher(content);
			if(matcher.find()) {
				//System.out.println(content + " : " + matcher.start() + " : " + matcher.end());
				result.add(entry.getKey());
			}
		}
		return result;
	}
	
	/**
	 * 添加内容过滤规则
	 * @param word  敏感字符串
	 * @param needSplit 是否需要分隔敏感字符串
	 */
	private void add(String word, boolean needSplit) {
		String value = "";
		if (needSplit) {
			char[] chars = word.toCharArray();
			StringBuffer sb = new StringBuffer();
			for(char c : chars) {
				sb.append(c).append("(.*?)");
			}
			value = sb.toString();
		} else {
			value = word.concat("(.*?)");
		}
		rulesMap.put(word, value);
	}

	public Map<String, String> getRulesMap() {
		return rulesMap;
	}

	public void setRulesMap(Map<String, String> rulesMap) {
		for(Iterator<Map.Entry<String, String>> it = rulesMap.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<String, String> entry = it.next();
			this.add(entry.getKey(), Boolean.valueOf(entry.getValue()));
		}
	}
}
