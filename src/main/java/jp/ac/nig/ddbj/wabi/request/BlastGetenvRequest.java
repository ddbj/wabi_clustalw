package jp.ac.nig.ddbj.wabi.request;

import net.arnx.jsonic.JSON;

/**
 * GETメソッド で BLASTの WebAPIを 呼び出す時に用いられる入力データを表わします.
 */
public class BlastGetenvRequest {
	String format = "text";
	String info = "env";

	public BlastGetenvRequest() {
	}

	public BlastGetenvRequest(String format, String info) {
		this.format = format;
		this.info = info;
	}

	public String toJsonStr() {
		return JSON.encode(this, true);
	}


	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}

	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
}
