package jp.ac.nig.ddbj.wabi.report.clustalw;

import java.util.List;
import java.util.ArrayList;

import jp.ac.nig.ddbj.wabi.report.WabiGetHelpReport;
import jp.ac.nig.ddbj.wabi.request.WabiGetHelpRequest;
import jp.ac.nig.ddbj.wabi.util.ConfClustalw;

public class ClustalwGetHelpReport extends WabiGetHelpReport {
	public ClustalwGetHelpReport(WabiGetHelpRequest request, boolean withResultOfQsub, boolean withGetenv) {
		super(request, withResultOfQsub, withGetenv);
	}

	protected void setListParameters(String program) {
		String conf_v = ConfClustalw.RequestValidationPattern.parameters_AcceptOptions_clustalw_verbs;
		String conf_g = ConfClustalw.RequestValidationPattern.parameters_AcceptOptions_clustalw_general;
		String conf_f = ConfClustalw.RequestValidationPattern.parameters_AcceptOptions_clustalw_fast;
		String conf_s = ConfClustalw.RequestValidationPattern.parameters_AcceptOptions_clustalw_slow;
		String conf_m = ConfClustalw.RequestValidationPattern.parameters_AcceptOptions_clustalw_multiple;
		String conf_t = ConfClustalw.RequestValidationPattern.parameters_AcceptOptions_clustalw_tree;
		String conf = conf_v + "|" + conf_g + "|" + conf_f + "|" + conf_s + "|" + conf_m + "|" + conf_t;
		String[] parameters = conf.split("\\|");
		List<String> list = new ArrayList<String>();
		for (int i = 0; i<parameters.length; ++i) {
			list.add(parameters[i]);
		}
		this.put("parameters", list);
	}

}
