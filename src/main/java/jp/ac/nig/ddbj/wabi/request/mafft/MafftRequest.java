package jp.ac.nig.ddbj.wabi.request.mafft;

import jp.ac.nig.ddbj.wabi.request.WabiRequest;

/** POSTメソッドでCLUSTALWのWebAPIを呼び出す時に用いられる入力データを表す.
 * 
 * @author oogasawa
 *
 */
public class MafftRequest extends WabiRequest {
	String profile1       = null;
	String profile2       = null;
	String aaMatrix       = null;

	// --addオプションに対応（2016/07/01）
	String addSequence         = null;
	String addfragmentSequence = null;
	String addprofileProfile   = null;
	String addfullSequence     = null;

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
	public String getAaMatrix() {
		return aaMatrix;
	}
	public void setAaMatrix(String aaMatrix) {
		this.aaMatrix = aaMatrix;
	}
	public String getAddSequence() {
		return addSequence;
	}
	public void setAddSequence(String addSequence) {
		this.addSequence = addSequence;
	}
	public String getAddfragmentsSequence() {
		return addfragmentSequence;
	}
	public void setAddfragmentsSequence(String addfragmentSequence) {
		this.addfragmentSequence = addfragmentSequence;
	}
	public String getAddprofileProfile() {
		return addprofileProfile;
	}
	public void setAddprofileProfile(String addprofileProfile) {
		this.addprofileProfile = addprofileProfile;
	}
	public String getAddfullSequence() {
		return addfullSequence;
	}
	public void setAddfullSequence(String addfullSequence) {
		this.addfullSequence = addfullSequence;
	}
}
