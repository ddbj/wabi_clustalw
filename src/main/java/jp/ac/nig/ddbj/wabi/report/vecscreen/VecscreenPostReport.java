package jp.ac.nig.ddbj.wabi.report.vecscreen;

import java.io.IOException;

import jp.ac.nig.ddbj.wabi.job.vecscreen.VecscreenJobInfo;
import jp.ac.nig.ddbj.wabi.job.WabiJobInfo;
import jp.ac.nig.ddbj.wabi.report.WabiPostReport;
import jp.ac.nig.ddbj.wabi.request.vecscreen.VecscreenRequest;
import jp.ac.nig.ddbj.wabi.request.WabiRequest;

public class VecscreenPostReport extends WabiPostReport {

	public VecscreenPostReport(WabiJobInfo info, WabiRequest req) throws IOException {
		super(info, req);
	}

	public VecscreenPostReport(VecscreenJobInfo info, VecscreenRequest req) throws IOException {
		super(info, req);
	}

}
