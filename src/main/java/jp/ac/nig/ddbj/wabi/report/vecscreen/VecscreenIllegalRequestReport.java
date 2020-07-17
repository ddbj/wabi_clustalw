package jp.ac.nig.ddbj.wabi.report.vecscreen;

import jp.ac.nig.ddbj.wabi.report.WabiIllegalRequestReport;
import jp.ac.nig.ddbj.wabi.request.vecscreen.VecscreenRequest;
import jp.ac.nig.ddbj.wabi.request.WabiGetRequest;
import jp.ac.nig.ddbj.wabi.request.WabiRequest;

public class VecscreenIllegalRequestReport extends WabiIllegalRequestReport {
	public VecscreenIllegalRequestReport(WabiRequest req) {
		super(req);
	}

	public VecscreenIllegalRequestReport(VecscreenRequest req) {
		super(req);
	}

	public VecscreenIllegalRequestReport(WabiGetRequest request) {
		super(request);
	}

}
