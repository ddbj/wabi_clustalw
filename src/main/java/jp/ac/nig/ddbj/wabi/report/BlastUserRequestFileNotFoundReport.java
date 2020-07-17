package jp.ac.nig.ddbj.wabi.report;

import java.util.LinkedHashMap;

import jp.ac.nig.ddbj.wabi.util.CalendarUtil;

public class BlastUserRequestFileNotFoundReport extends LinkedHashMap<String,String> {
	
	public BlastUserRequestFileNotFoundReport(String requestId) {
		this.put("request-ID", requestId);
		this.put("Message", "Unexpected error ( Results of your request id have been NOT FOUND.)");
		this.put("current-time", CalendarUtil.getTime());
	}

}
