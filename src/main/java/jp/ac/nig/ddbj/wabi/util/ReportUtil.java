package jp.ac.nig.ddbj.wabi.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class ReportUtil {
	
	public static LinkedHashMap<String, String> makeSubset(Map<String, String> data, String[] keyList) {
		LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
		for (String k : keyList) {
			result.put(k, data.get(k));
		}
		return result;
	}
	
}
