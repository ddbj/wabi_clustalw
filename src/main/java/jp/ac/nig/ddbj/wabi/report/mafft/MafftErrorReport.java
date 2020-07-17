package jp.ac.nig.ddbj.wabi.report.mafft;

import java.io.IOException;

import jp.ac.nig.ddbj.wabi.report.WabiErrorReport;
import jp.ac.nig.ddbj.wabi.request.clustalw.ClustalwRequest;
import jp.ac.nig.ddbj.wabi.request.WabiRequest;

public class MafftErrorReport extends WabiErrorReport {

	public MafftErrorReport(WabiRequest req) throws IOException {
		super(req);
	}
	
	public MafftErrorReport(ClustalwRequest req) throws IOException {
		super(req);
	}
	

}
