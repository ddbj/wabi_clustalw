package jp.ac.nig.ddbj.wabi.report;

import java.io.IOException;
import java.util.LinkedHashMap;

import jp.ac.nig.ddbj.wabi.job.JobInfo;
import jp.ac.nig.ddbj.wabi.request.BlastRequest;
import jp.ac.nig.ddbj.wabi.util.CalendarUtil;

public class BlastPostReport extends LinkedHashMap<String,String> {
	
	public BlastPostReport(JobInfo info, BlastRequest req) throws IOException {
		this.put("requestId", info.getRequestId());
		this.put("program", req.getProgram());
		this.put("datasets", req.getDatasets());
		this.put("database", req.getDatabase());
		this.put("parameters", req.getParameters());
		this.put("current-time", CalendarUtil.getTime());
		this.put("start-time", "");
		this.put("current-state", "");
	}
	

}
