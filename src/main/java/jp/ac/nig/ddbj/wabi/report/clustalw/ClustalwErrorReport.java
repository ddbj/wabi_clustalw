package jp.ac.nig.ddbj.wabi.report.clustalw;

import java.io.IOException;

import jp.ac.nig.ddbj.wabi.report.WabiErrorReport;
import jp.ac.nig.ddbj.wabi.request.clustalw.ClustalwRequest;
import jp.ac.nig.ddbj.wabi.request.WabiRequest;

public class ClustalwErrorReport extends WabiErrorReport {

	public ClustalwErrorReport(WabiRequest req) throws IOException {
		super(req);
	}
	
	public ClustalwErrorReport(ClustalwRequest req) throws IOException {
		super(req);
	}
	

}
