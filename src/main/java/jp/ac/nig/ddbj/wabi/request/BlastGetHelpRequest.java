package jp.ac.nig.ddbj.wabi.request;

import net.arnx.jsonic.JSON;

/**
 * GETメソッド で BLASTの Help情報 を参照する時に用いられる入力データを表わします.
 */
public class BlastGetHelpRequest {
	String helpCommand;
	String format = "text";
	String program;

	public BlastGetHelpRequest() {
	}

	public BlastGetHelpRequest(String format) {
		this.format = format;
	}

	public String toJsonStr() {
		return JSON.encode(this, true);
	}


	public String getHelpCommand() {
		return helpCommand;
	}
	public void setHelpCommand(String helpCommand) {
		this.helpCommand = helpCommand;
	}

	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}

	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
}
