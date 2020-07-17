package jp.ac.nig.ddbj.wabi.report.vecscreen;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import jp.ac.nig.ddbj.wabi.report.WabiGetHelpReport;
import jp.ac.nig.ddbj.wabi.request.WabiGetHelpRequest;
import jp.ac.nig.ddbj.wabi.util.ConfVecscreen;

public class VecscreenGetHelpReport extends WabiGetHelpReport {
	public VecscreenGetHelpReport(WabiGetHelpRequest request, boolean withResultOfQsub, boolean withGetenv) {
		try {
			if (null==request.getHelpCommand() || request.getHelpCommand().isEmpty()) {
				usage();
			} else if ("list_database".equals(request.getHelpCommand())) {
				setListDatabase();
			} else if ("list_parameters".equals(request.getHelpCommand())) {
				setListParameters(request.getProgram());
			} else if ("list_format".equals(request.getHelpCommand())) {
				setListFormat();
			} else if ("list_result".equals(request.getHelpCommand())) {
				setListResult();
			} else if ("list_info".equals(request.getHelpCommand())) {
				setListInfo(withResultOfQsub, withGetenv);
			} else {
				usage();
			}
		} catch (Exception e) {
			usage();
		}
	}

	protected static List<String> helpCommands = Arrays.asList(
			"list_database",
			"list_parameters",
			 "list_format",
			 "list_result",
			 "list_info"
	);

	@Override
	protected void usage() {
		this.put("help_commands", helpCommands);
		this.put("format", helpFormats);
	}

	protected void setListParameters(String program) {
		String conf = ConfVecscreen.RequestValidationPattern.parameters_AcceptOptions_vecscreen;
		String[] parameters = conf.split("\\|");
		List<String> list = new ArrayList<String>();
		for (int i = 0; i<parameters.length; ++i) {
			list.add(parameters[i]);
		}
		this.put("parameters", list);
	}

	protected void setListDatabase() {
		this.put("database", listTokens(ConfVecscreen.Help.listDatabase));
	}
}
