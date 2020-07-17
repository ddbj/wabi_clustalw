package jp.ac.nig.ddbj.wabi.report.mafft;

import java.util.LinkedHashMap;

import jp.ac.nig.ddbj.wabi.report.WabiGetErrorReport;
import jp.ac.nig.ddbj.wabi.request.WabiGetRequest;
import jp.ac.nig.ddbj.wabi.request.WabiGetenvRequest;
import jp.ac.nig.ddbj.wabi.util.CalendarUtil;

public class MafftGetErrorReport extends WabiGetErrorReport {
	public MafftGetErrorReport(WabiGetRequest req) {
		super(req);
	}

	public MafftGetErrorReport(WabiGetenvRequest req) {
		super(req);
	}
}
