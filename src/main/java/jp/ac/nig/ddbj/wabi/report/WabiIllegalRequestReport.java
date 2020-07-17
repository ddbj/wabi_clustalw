package jp.ac.nig.ddbj.wabi.report;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import jp.ac.nig.ddbj.wabi.request.WabiGetRequest;
import jp.ac.nig.ddbj.wabi.request.WabiRequest;
import jp.ac.nig.ddbj.wabi.util.CalendarUtil;

public class WabiIllegalRequestReport extends LinkedHashMap<String, Object> {
	public WabiIllegalRequestReport(WabiRequest req) {
		this.put("status", "illegal-arguments");
		this.put("message", "Illegal arguments.");
		this.put("format", req.getFormat());
		this.put("parameters", req.getParameters());
		this.put("querySequence", req.getQuerySequence());
		this.put("result", req.getResult());
		this.put("address", req.getAddress());
		this.put("current-time", CalendarUtil.getTime());
	}

	public WabiIllegalRequestReport(WabiGetRequest request) {
		this.put("status", "illegal-arguments");
		this.put("message", "Illegal arguments.");
		this.put("request-ID", request.getRequestId());
		this.put("format", request.getFormat());
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
