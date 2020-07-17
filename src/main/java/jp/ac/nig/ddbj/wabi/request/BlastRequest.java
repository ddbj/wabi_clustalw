package jp.ac.nig.ddbj.wabi.request;

import net.arnx.jsonic.JSON;

/** POSTメソッドでBLASTのWebAPIを呼び出す時に用いられる入力データを表す.
 * 
 * @author oogasawa
 *
 */
public class BlastRequest {

	String querySequence  = null;
	String datasets       = null;
	String database       = null;
	String program        = null; 
	String parameters     = null;
	String format         = null;
	String result         = null;
	String address        = null;
	
	public String toJsonStr() {
		return JSON.encode(this, true);
	}
	
	
	public String getQuerySequence() {
		return querySequence;
	}
	public void setQuerySequence(String querySequence) {
		this.querySequence = querySequence;
	}
	public String getDatasets() {
		return datasets;
	}
	public void setDatasets(String datasets) {
		this.datasets = datasets;
	}
	public String getDatabase() {
		return database;
	}
	public void setDatabase(String database) {
		this.database = database;
	}
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}

	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public String getParameters() {
		return parameters;
	}
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	

	

}
