package jp.ac.nig.ddbj.wabi.report;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

import jp.ac.nig.ddbj.wabi.request.WabiGetHelpRequest;
import jp.ac.nig.ddbj.wabi.util.ConfWabi;

public abstract class WabiGetHelpReport extends LinkedHashMap<String, Object> {
	public WabiGetHelpReport() {		
	}
	
	public WabiGetHelpReport(WabiGetHelpRequest request, boolean withResultOfQsub, boolean withGetenv) {
		try {
			if (null==request.getHelpCommand() || request.getHelpCommand().isEmpty()) {
				usage();
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
			"list_parameters",
			 "list_format",
			 "list_result",
			 "list_info"
	);
	protected static List<String> helpFormats = Arrays.asList("text", "json", "xml");

	protected void usage() {
		this.put("help_commands", helpCommands);
		this.put("format", helpFormats);
	}

	protected abstract void setListParameters(String program);

	protected void setListFormat() {
		this.put("format", listTokens(ConfWabi.RequestValidationPattern.format));
	}

	protected void setListResult() {
		this.put("result", listTokens(ConfWabi.RequestValidationPattern.result));
	}

	protected void setListInfo(boolean withResultOfQsub, boolean withGetenv) {
		List<String> list = listTokens(ConfWabi.RequestValidationPattern.info);
		if (withResultOfQsub) {
			list.add("result_stdout");
			list.add("result_stderr");
		}
		if (withGetenv) {
			list.add("env");
		}
		this.put("info", list);
	}

	protected List<String> listTokens(String conf) {
		List<String> list = new ArrayList<String>();
		for (String token : conf.substring(1, conf.length() - 1).split("\\|")) {
			list.add(token);
		}
		return list;
	}
}
