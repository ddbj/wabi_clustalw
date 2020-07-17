package jp.ac.nig.ddbj.wabi.report.clustalw;

import jp.ac.nig.ddbj.wabi.report.WabiGetErrorReport;
import jp.ac.nig.ddbj.wabi.request.WabiGetRequest;
import jp.ac.nig.ddbj.wabi.request.WabiGetenvRequest;

public class ClustalwGetErrorReport extends WabiGetErrorReport {
	public ClustalwGetErrorReport(WabiGetRequest req) {
		super(req);
	}

	public ClustalwGetErrorReport(WabiGetenvRequest req) {
		super(req);
	}
}
