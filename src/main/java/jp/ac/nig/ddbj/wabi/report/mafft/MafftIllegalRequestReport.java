package jp.ac.nig.ddbj.wabi.report.mafft;

import jp.ac.nig.ddbj.wabi.report.WabiIllegalRequestReport;
import jp.ac.nig.ddbj.wabi.request.mafft.MafftRequest;
import jp.ac.nig.ddbj.wabi.request.WabiGetRequest;
import jp.ac.nig.ddbj.wabi.request.WabiRequest;

public class MafftIllegalRequestReport extends WabiIllegalRequestReport {
	public MafftIllegalRequestReport(WabiRequest req) {
		super(req);
	}

	public MafftIllegalRequestReport(MafftRequest req) {
		super(req);
	}

	public MafftIllegalRequestReport(WabiGetRequest request) {
		super(request);
	}

}
