package jp.ac.nig.ddbj.wabi.request.clustalw;

import net.arnx.jsonic.JSON;
import jp.ac.nig.ddbj.wabi.request.WabiRequest;

/** POSTメソッドでCLUSTALWのWebAPIを呼び出す時に用いられる入力データを表す.
 * 
 * @author oogasawa
 *
 */
public class ClustalwRequest extends WabiRequest {
	String profile1       = null;
	String profile2       = null;
	String guidetree1     = null;
	String guidetree2     = null;
	String pwDnaMatrix    = null;
	String pwAaMatrix     = null;
	String dnaMatrix      = null;
	String aaMatrix       = null;
	
	public String getProfile1() {
		return profile1;
	}
	public void setProfile1(String profile1) {
		this.profile1 = profile1;
	}
	public String getProfile2() {
		return profile2;
	}
	public void setProfile2(String profile2) {
		this.profile2 = profile2;
	}
	public String getGuidetree1() {
		return guidetree1;
	}
	public void setGuidetree1(String guidetree1) {
		this.guidetree1 = guidetree1;
	}
	public String getGuidetree2() {
		return guidetree2;
	}
	public void setGuidetree2(String guidetree2) {
		this.guidetree2 = guidetree2;
	}
	public String getPwDnaMatrix() {
		return pwDnaMatrix;
	}
	public void setPwDnaMatrix(String pwDnaMatrix) {
		this.pwDnaMatrix = pwDnaMatrix;
	}
	public String getPwAaMatrix() {
		return pwAaMatrix;
	}
	public void setPwAaMatrix(String pwAaMatrix) {
		this.pwAaMatrix = pwAaMatrix;
	}
	public String getDnaMatrix() {
		return dnaMatrix;
	}
	public void setDnaMatrix(String dnaMatrix) {
		this.dnaMatrix = dnaMatrix;
	}
	public String getAaMatrix() {
		return aaMatrix;
	}
	public void setAaMatrix(String aaMatrix) {
		this.aaMatrix = aaMatrix;
	}
}
