package jp.ac.nig.ddbj.wabi.report.vecscreen;

import jp.ac.nig.ddbj.wabi.report.WabiGetErrorReport;
import jp.ac.nig.ddbj.wabi.request.WabiGetRequest;
import jp.ac.nig.ddbj.wabi.request.WabiGetenvRequest;

public class VecscreenGetErrorReport extends WabiGetErrorReport {
	public VecscreenGetErrorReport(WabiGetRequest req) {
		super(req);
	}

	public VecscreenGetErrorReport(WabiGetenvRequest req) {
		super(req);
	}
}
