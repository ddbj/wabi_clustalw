package jp.ac.nig.ddbj.wabi.report.mafft;

import java.io.IOException;

import jp.ac.nig.ddbj.wabi.job.mafft.MafftJobInfo;
import jp.ac.nig.ddbj.wabi.job.WabiJobInfo;
import jp.ac.nig.ddbj.wabi.report.WabiPostReport;
import jp.ac.nig.ddbj.wabi.request.mafft.MafftRequest;
import jp.ac.nig.ddbj.wabi.request.WabiRequest;

public class MafftPostReport extends WabiPostReport {

	public MafftPostReport(WabiJobInfo info, WabiRequest req) throws IOException {
		super(info, req);
	}

	public MafftPostReport(MafftJobInfo info, MafftRequest req) throws IOException {
		super(info, req);
	}

}
