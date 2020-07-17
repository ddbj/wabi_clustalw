package jp.ac.nig.ddbj.wabi.report;

import java.io.IOException;
import java.util.LinkedHashMap;

import jp.ac.nig.ddbj.wabi.request.WabiRequest;
import jp.ac.nig.ddbj.wabi.util.CalendarUtil;

public abstract class WabiErrorReport extends LinkedHashMap<String,Object> {
	
	public WabiErrorReport(WabiRequest req) throws IOException {
		//this.put("requestId", info.getRequestId());
		this.put("Message: ", "Runtime error.");
		this.put("format", req.getFormat());
		this.put("parameters", req.getParameters());
		this.put("current-time", CalendarUtil.getTime());
	}
	

}
