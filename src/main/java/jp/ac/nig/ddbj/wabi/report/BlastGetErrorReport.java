package jp.ac.nig.ddbj.wabi.report;

import java.io.IOException;
import java.util.LinkedHashMap;

import jp.ac.nig.ddbj.wabi.request.BlastGetRequest;
import jp.ac.nig.ddbj.wabi.request.BlastGetenvRequest;
import jp.ac.nig.ddbj.wabi.util.CalendarUtil;

public class BlastGetErrorReport extends LinkedHashMap<String, Object> {
	public BlastGetErrorReport(BlastGetRequest req) {
		this.put("Message", "Runtime error.");
		this.put("requestId", req.getRequestId());
		this.put("format", req.getFormat());
		this.put("imageId", req.getImageId());
		this.put("info", req.getInfo());
		this.put("current-time", CalendarUtil.getTime());
	}

	public BlastGetErrorReport(BlastGetenvRequest req) {
		this.put("Message", "Runtime error.");
		this.put("format", req.getFormat());
		this.put("info", req.getInfo());
		this.put("current-time", CalendarUtil.getTime());
	}
}
