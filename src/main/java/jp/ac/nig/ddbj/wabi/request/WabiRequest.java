package jp.ac.nig.ddbj.wabi.request;

import net.arnx.jsonic.JSON;

/** POSTメソッドでWABIを呼び出す時に用いられる入力データを表す.
 */
public class WabiRequest {

	String querySequence  = null;
	String parameters     = null;
	String format         = null;
	String result         = null;
	String address        = null;
	String database       = null;
	
	public String toJsonStr() {
		return JSON.encode(this, true);
	}
	public String getQuerySequence() {
		return querySequence;
	}
	public void setQuerySequence(String querySequence) {
		this.querySequence = querySequence;
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
	public String getDatabase() {
		return database;
	}
	public void setDatabase(String database) {
		this.database = database;
	}
	
}
