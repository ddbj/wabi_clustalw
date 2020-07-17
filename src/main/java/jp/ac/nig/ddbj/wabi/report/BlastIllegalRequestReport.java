package jp.ac.nig.ddbj.wabi.report;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import jp.ac.nig.ddbj.wabi.request.BlastRequest;
import jp.ac.nig.ddbj.wabi.request.BlastGetRequest;
import jp.ac.nig.ddbj.wabi.util.CalendarUtil;

public class BlastIllegalRequestReport extends LinkedHashMap<String, Object> {
	public BlastIllegalRequestReport(BlastRequest req) {
		this.put("status", "illegal-arguments");
		this.put("message", "Illegal arguments.");
		this.put("format", req.getFormat());
		this.put("program", req.getProgram());
		this.put("datasets", req.getDatasets());
		this.put("database", req.getDatabase());
		this.put("parameters", req.getParameters());
		this.put("querySequence", req.getQuerySequence());
		this.put("result", req.getResult());
		this.put("address", req.getAddress());
		this.put("current-time", CalendarUtil.getTime());
	}

	public BlastIllegalRequestReport(BlastGetRequest request) {
		this.put("status", "illegal-arguments");
		this.put("message", "Illegal arguments.");
		this.put("request-ID", request.getRequestId());
		this.put("format", request.getFormat());
		this.put("imageId", request.getImageId());
		this.put("info", request.getInfo());
		this.put("current-time", CalendarUtil.getTime());
	}

	public void addErrorMessage(String message) {
		final String key = "error-messages";
		if (!this.containsKey(key)) {
			this.put(key, new ArrayList<String>());
		}
		((List<String>)this.get(key)).add(message);
	}

	public void addErrorCode(List<String> code) {
		final String key = "error-codes";
		if (!this.containsKey(key)) {
			this.put(key, new ArrayList<List<String>>());
		}
		((List<List<String>>)this.get(key)).add(code);
	}
}
