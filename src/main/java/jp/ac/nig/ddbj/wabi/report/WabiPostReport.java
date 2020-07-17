package jp.ac.nig.ddbj.wabi.report;

import java.io.IOException;
import java.util.LinkedHashMap;

import jp.ac.nig.ddbj.wabi.job.WabiJobInfo;
import jp.ac.nig.ddbj.wabi.request.WabiRequest;
import jp.ac.nig.ddbj.wabi.util.CalendarUtil;

public abstract class WabiPostReport extends LinkedHashMap<String,String> {
	public WabiPostReport(WabiJobInfo info, WabiRequest req) throws IOException {
		this.put("requestId", info.getRequestId());
		this.put("parameters", req.getParameters());
		this.put("current-time", CalendarUtil.getTime());
		this.put("start-time", "");
		this.put("current-state", "");
	}
	

}
