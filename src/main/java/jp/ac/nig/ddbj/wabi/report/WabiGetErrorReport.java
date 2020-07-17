package jp.ac.nig.ddbj.wabi.report;

import java.util.LinkedHashMap;

import jp.ac.nig.ddbj.wabi.request.WabiGetRequest;
import jp.ac.nig.ddbj.wabi.request.WabiGetenvRequest;
import jp.ac.nig.ddbj.wabi.util.CalendarUtil;

public class WabiGetErrorReport extends LinkedHashMap<String, Object> {
	public WabiGetErrorReport(WabiGetRequest req) {
		this.put("Message", "Runtime error.");
		this.put("requestId", req.getRequestId());
		this.put("format", req.getFormat());
		this.put("info", req.getInfo());
		this.put("current-time", CalendarUtil.getTime());
	}

	public WabiGetErrorReport(WabiGetenvRequest req) {
		this.put("Message", "Runtime error.");
		this.put("format", req.getFormat());
		this.put("info", req.getInfo());
		this.put("current-time", CalendarUtil.getTime());
	}
}
