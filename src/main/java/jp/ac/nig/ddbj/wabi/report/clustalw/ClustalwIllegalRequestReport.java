package jp.ac.nig.ddbj.wabi.report.clustalw;

import jp.ac.nig.ddbj.wabi.report.WabiIllegalRequestReport;
import jp.ac.nig.ddbj.wabi.request.clustalw.ClustalwRequest;
import jp.ac.nig.ddbj.wabi.request.WabiGetRequest;
import jp.ac.nig.ddbj.wabi.request.WabiRequest;

public class ClustalwIllegalRequestReport extends WabiIllegalRequestReport {
	public ClustalwIllegalRequestReport(WabiRequest req) {
		super(req);
	}

	public ClustalwIllegalRequestReport(ClustalwRequest req) {
		super(req);
	}

	public ClustalwIllegalRequestReport(WabiGetRequest request) {
		super(request);
	}

}
