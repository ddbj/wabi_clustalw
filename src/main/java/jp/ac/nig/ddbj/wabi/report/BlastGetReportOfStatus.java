package jp.ac.nig.ddbj.wabi.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import jp.ac.nig.ddbj.wabi.job.JobInfo;
import jp.ac.nig.ddbj.wabi.job.JobIdNotInitializedException;
import jp.ac.nig.ddbj.wabi.request.BlastRequest;
import jp.ac.nig.ddbj.wabi.util.CalendarUtil;

/** WABI blastをGETメソッド、info="status"で呼んだときに返される情報を定義するクラス
 * 
 * ジョブの現在の状況は、以下のいずれかになる。 
 * <ul>
 * <li>"waiting"</li>
 * <li>"running"</li>
 * <li>"finished"</li>
 * <li>"not-found"</li>
 * </ul>
 * 
 * @author oogasawa
 *
 */
public class BlastGetReportOfStatus extends LinkedHashMap<String,String> {
	
	public BlastGetReportOfStatus(String requestId, boolean canPrintSystemInfo) throws IOException, JobIdNotInitializedException {
		
		JobInfo jobInfo = new JobInfo(requestId);
		
		ArrayList<String> status = jobInfo.getStatus(canPrintSystemInfo);
		if (status == null) {
			this.put("error-message", "Unexpected error (status == null)");
			this.putAll(jobInfo.getInfo(false));			
		}
		else if (status.size() < 2) {
			this.put("error-message", "Unexpected error (status.size() == " + status.size() + ")");
			this.putAll(jobInfo.getInfo(false));
		}
		else {
			this.put("request-ID", requestId);
			this.put("status", status.get(0));
			this.put("current-time", CalendarUtil.getTime());
			this.put("system-info",status.get(1));
		}
	}

}
