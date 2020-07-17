package jp.ac.nig.ddbj.wabi.report.mafft;

import java.util.List;
import java.util.ArrayList;

import jp.ac.nig.ddbj.wabi.report.WabiGetHelpReport;
import jp.ac.nig.ddbj.wabi.request.WabiGetHelpRequest;
import jp.ac.nig.ddbj.wabi.util.ConfMafft;

public class MafftGetHelpReport extends WabiGetHelpReport {
	public MafftGetHelpReport(WabiGetHelpRequest request, boolean withResultOfQsub, boolean withGetenv) {
		super(request, withResultOfQsub, withGetenv);
	}

	protected void setListParameters(String program) {
		String conf_a = ConfMafft.RequestValidationPattern.parameters_AcceptOptions_mafft_algorithm;
		String conf_p = ConfMafft.RequestValidationPattern.parameters_AcceptOptions_mafft_parameter;
		String conf_o = ConfMafft.RequestValidationPattern.parameters_AcceptOptions_mafft_output;
		String conf_i = ConfMafft.RequestValidationPattern.parameters_AcceptOptions_mafft_input;
		String conf = conf_a + "|" + conf_p + "|" + conf_o + "|" + conf_i;
		String[] parameters = conf.split("\\|");
		List<String> list = new ArrayList<String>();
		for (int i = 0; i<parameters.length; ++i) {
			list.add(parameters[i]);
		}
		this.put("parameters", list);
	}

}
