package jp.ac.nig.ddbj.wabi.report.vecscreen;

import java.io.IOException;

import jp.ac.nig.ddbj.wabi.report.WabiErrorReport;
import jp.ac.nig.ddbj.wabi.request.vecscreen.VecscreenRequest;
import jp.ac.nig.ddbj.wabi.request.WabiRequest;

public class VecscreenErrorReport extends WabiErrorReport {

	public VecscreenErrorReport(WabiRequest req) throws IOException {
		super(req);
	}
	
	public VecscreenErrorReport(VecscreenRequest req) throws IOException {
		super(req);
	}
	

}
