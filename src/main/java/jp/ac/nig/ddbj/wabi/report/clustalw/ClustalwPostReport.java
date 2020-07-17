package jp.ac.nig.ddbj.wabi.report.clustalw;

import java.io.IOException;

import jp.ac.nig.ddbj.wabi.job.clustalw.ClustalwJobInfo;
import jp.ac.nig.ddbj.wabi.job.WabiJobInfo;
import jp.ac.nig.ddbj.wabi.report.WabiPostReport;
import jp.ac.nig.ddbj.wabi.request.clustalw.ClustalwRequest;
import jp.ac.nig.ddbj.wabi.request.WabiRequest;

public class ClustalwPostReport extends WabiPostReport {

	public ClustalwPostReport(WabiJobInfo info, WabiRequest req) throws IOException {
		super(info, req);
	}

	public ClustalwPostReport(ClustalwJobInfo info, ClustalwRequest req) throws IOException {
		super(info, req);
	}

}
