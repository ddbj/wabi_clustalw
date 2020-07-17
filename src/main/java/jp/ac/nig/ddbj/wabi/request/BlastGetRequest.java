package jp.ac.nig.ddbj.wabi.request;

import net.arnx.jsonic.JSON;

/**
 * GETメソッド で BLASTの WebAPIを 呼び出す時に用いられる入力データを表わします.
 */
public class BlastGetRequest {
	String requestId;
	String format = "text";
	String imageId;
	String info = "status";

	public BlastGetRequest() {
	}

	public BlastGetRequest(String requestId, String format, String imageId, String info) {
		this.requestId = requestId;
		this.format = format;
		this.imageId = imageId;
		this.info = info;
	}

	public String toJsonStr() {
		return JSON.encode(this, true);
	}


	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}

	public String getImageId() {
		return imageId;
	}
	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
}
