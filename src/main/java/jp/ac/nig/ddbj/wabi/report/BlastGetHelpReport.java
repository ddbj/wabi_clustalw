package jp.ac.nig.ddbj.wabi.report;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

import jp.ac.nig.ddbj.wabi.request.BlastGetHelpRequest;
import jp.ac.nig.ddbj.wabi.util.ConfBlast;

public class BlastGetHelpReport extends LinkedHashMap<String, Object> {
	public BlastGetHelpReport(BlastGetHelpRequest request, boolean withResultOfQsub, boolean withGetenv) {
		try {
			if (null==request.getHelpCommand() || request.getHelpCommand().isEmpty()) {
				usage();
			} else if ("list_datasets".equals(request.getHelpCommand())) {
				setListDatasets();
			} else if ("list_database".equals(request.getHelpCommand())) {
				setListDatabase();
			} else if ("list_program".equals(request.getHelpCommand())) {
				setListProgram();
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

	private static List<String> helpCommands = Arrays.asList("list_datasets",
															 "list_database",
															 "list_program",
															 "list_parameters",
															 "list_format",
															 "list_result",
															 "list_info");
	private static List<String> helpFormats = Arrays.asList("text", "json", "xml");
	private void usage() {
		this.put("help_commands", helpCommands);
		this.put("format", helpFormats);
	}

	private void setListDatasets() {
		this.put("datasets", listTokens(ConfBlast.RequestValidationPattern.datasets));
	}

	private void setListDatabase() {
		this.put("database", listTokens(ConfBlast.Help.listDatabase));
	}

	private void setListProgram() {
		this.put("datasets", listTokens(ConfBlast.RequestValidationPattern.program));
	}

	private void setListParameters(String program) {
		String conf = "megablast".equals(program)
			? ConfBlast.RequestValidationPattern.parameters_AcceptOptions_megablast
			: ConfBlast.RequestValidationPattern.parameters_AcceptOptions_blastall;
		char[] chars = conf.toCharArray();
		List<String> list = new ArrayList<String>();
		for (int i = 0; i<chars.length; ++i) {
			list.add(String.valueOf(chars[i]));
		}
		this.put("parameters", list);
	}

	private void setListFormat() {
		this.put("format", listTokens(ConfBlast.RequestValidationPattern.format));
	}

	private void setListResult() {
		this.put("result", listTokens(ConfBlast.RequestValidationPattern.result));
	}

	private void setListInfo(boolean withResultOfQsub, boolean withGetenv) {
		List<String> list = listTokens(ConfBlast.RequestValidationPattern.info);
		if (withResultOfQsub) {
			list.add("result_stdout");
			list.add("result_stderr");
		}
		if (withGetenv) {
			list.add("env");
		}
		this.put("info", list);
	}

	private List<String> listTokens(String conf) {
		List<String> list = new ArrayList<String>();
		for (String token : conf.substring(1, conf.length() - 1).split("\\|")) {
			list.add(token);
		}
		return list;
	}
}
