package jp.ac.nig.ddbj.wabi.job.mafft;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

import jp.ac.nig.ddbj.wabi.controller.clustalw.ClustalwController;
import jp.ac.nig.ddbj.wabi.controller.mafft.MafftController;
import jp.ac.nig.ddbj.wabi.job.WabiJobInfo;
import jp.ac.nig.ddbj.wabi.util.Conf;

import net.ogalab.util.container.ArrayUtil;
import net.ogalab.util.fundamental.Type;
import net.ogalab.util.os.FileIO;
import net.ogalab.util.rand.RNG;
import cern.jet.random.Uniform;

public class MafftJobInfo extends WabiJobInfo {
	
	Pattern pRequestId = Pattern.compile("(.+?)_([0-9]{4})-([0-9]{2})([0-9]{2})-([0-9]{2})([0-9]{2})-([0-9]{2})-([0-9]+)-([0-9]+)");
	
	Pattern p1 = Pattern.compile("^Following jobs do not exists", Pattern.MULTILINE);
	Pattern p2 = Pattern.compile("^usage\\s+", Pattern.MULTILINE);
	Pattern p3 = Pattern.compile("^job_number:\\s+", Pattern.MULTILINE);
	Pattern p4 = Pattern.compile("^scheduling info:\\s+There are no messages available", Pattern.MULTILINE);

	/** このディレクトリ以下にWebAPIによるプログラム実行結果などジョブの情報が置かれる */
	public static String workingDirRoot = Conf.workingDirBase;
	
	/** Random Number Generator Object (singleton) */
	RNG engine = null;

	/** ロックオブジェクト */
	private final static Object lock = new Object();
	

	public MafftJobInfo(RNG e) {
		super(e);
	}
	
	/** requestIdの文字列をパースすることによりjobInfoオブジェクトを作る。
	 * 
	 * @param requestId
	 * @throws IOException jobIDファイルがない。requestIdの文字列がおかしいかもしれません。
	 */
	public MafftJobInfo(String requestId) throws IOException {
		super(requestId);
	}
	
	/** UGEのqstat, qacctを呼び出し、jobIdで表されるジョブの現在の状況を返す.
	 * 
	 * ジョブの現在の状況は、以下のいずれかになる。 
	 * <ul>
	 * <li>"waiting"</li>
	 * <li>"running"</li>
	 * <li>"finished"</li>
	 * <li>"not-found"</li>
	 * </ul>
	 * @throws IOException 
	 * 
	 */

	public boolean existsFinishedFile() {
		String path = getWorkingDir() + MafftController.FINISHED_FILE;
		return new File(path).exists();
	}

	public boolean existsOutFile() {
		String path = getWorkingDir() + MafftController.WABI_OUT_FILE;
		return new File(path).exists();
	}

	public boolean existsProfile1() {
		String path = getWorkingDir() + ClustalwController.WABI_PROFILE1;
		return new File(path).exists();
	}

	public boolean existsProfile2() {
		String path = getWorkingDir() + ClustalwController.WABI_PROFILE2;
		return new File(path).exists();
	}

	public boolean existsAaMatrix() {
		String path = getWorkingDir() + ClustalwController.WABI_AA_MATRIX;
		return new File(path).exists();
	}
	
	// --addオプションに対応（2016/07/01）
	public boolean existsAddSequence() {
		String path = getWorkingDir() + MafftController.WABI_ADD_SEQUENCE;
		return new File(path).exists();
	}

	public boolean existsAddfragmentsSequence() {
		String path = getWorkingDir() + MafftController.WABI_ADDFRAGMENTS_SEQUENCE;
		return new File(path).exists();
	}

	public boolean existsAddprofileProfile() {
		String path = getWorkingDir() + MafftController.WABI_ADDPROFILE_PROFILE;
		return new File(path).exists();
	}

	public boolean existsAddfullSequence() {
		String path = getWorkingDir() + MafftController.WABI_ADDFULL_SEQUENCE;
		return new File(path).exists();
	}

	/** jobIDがかかれたファイルの中身を読んでjobIDを取得し、それを文字列として返す。
	 * /home/geadmin/UGEB/ugeb/common/settings.sh
	 * リクエストは既に実行されていてjobIDは既に発行されていると前提している。
	 * 
	 * @return jobID
	 * @throws IOException 
	 */
	public String readJobId() throws IOException {
		String path = getWorkingDir() + MafftController.UGE_JOB_ID_FILE;
		//String id   = FileIO.readFile(path);
		String id = "";
		if (new File(path).exists())
			id = FileIO.readFile(path);
		
		return id.trim();
	}

	/** requestIdが存在しなければ作成し、オブジェクトに登録する。既にオブジェクトに登録されているのであれば何もしない。
	 * 
	 * @return requestId
	 * @throws IOException 
	 */
	public String generateRequestId() throws IOException {
		String id = null;
		if (requestId == null)
			id = reGenerateRequestId();
		else
			id = requestId;
		
		return id;
	}
	
	/** requestIdが既に発行されているかどうかに関わらずrequestIdを再発行し、オブジェクトに登録する。
	 *  対応するディレクトリも作成する。（同一IDが既にとられていないかを判定するため）.
	 * 
	 * @return requestId
	 * @throws IOException 
	 */
	public String reGenerateRequestId() throws IOException {
		Calendar d = Calendar.getInstance();
		year      = String.format("%1$tY", d);
		month     = String.format("%1$tm", d);
		day       = String.format("%1$td", d);
		hour      = String.format("%1$tH", d);
		min       = String.format("%1$tM", d);
		sec       = String.format("%1$tS", d);
		millisec  = Type.toString(d.get(Calendar.MILLISECOND));
		

		// TODO 乱数発生要注意
		Uniform unif = new Uniform(engine.getEngine());
		randomSuffix  = Type.toString(unif.nextIntFromTo(0, 1000000), 6);
		
		requestId = MafftController.outfilePrefix + ArrayUtil.join("-", new String[]{year, month+day, hour+min, sec, millisec, randomSuffix}).trim();
		String dir = getWorkingDir(); // ディレクトリの名前だけが作られる。
		//ID重複防止のためロックする
		synchronized (lock) {
			// IDの重複がある場合はID発行やり直し
			if (new File(dir).exists()) // そのディレクトリが既にある
				requestId = reGenerateRequestId();
		
			makeWorkingDir();
		}
		return requestId;
	}
	
	public boolean existsUserRequestFile() {
		String path = getWorkingDir() + MafftController.USER_REQUEST_FILE;
		return new File(path).exists();
	}
	
	/** Jobが動作するWorking Directoryのフルパス名をStringとして返す.
	 * 
	 * makeWorkingDir()メソッドを呼ぶまではディレクトリの実体は作成されていないかもしれない。
	 * 
	 * @return Working Directoryのフルパス名
	 */
	public String getWorkingDir() {
		return workingDirRoot + ArrayUtil.join("/", new String[]{year, month+day, hour+min, sec, millisec, randomSuffix}).trim() + "/";
	}
	
	public LinkedHashMap<String, String> getInfo(boolean withWorkingDirRoot) {
		LinkedHashMap<String, String> info = new LinkedHashMap<String, String>();
		info.put("requestId", requestId);
		info.put("jobId", jobId);
		if (withWorkingDirRoot) {
			info.put("workingDirRoot", workingDirRoot);
		}
		info.put("year", year);
		info.put("month", month);
		info.put("day", day);
		info.put("hour", hour);
		info.put("min", min);
		info.put("sec", sec);
		info.put("millisec", millisec);
		info.put("randomId", randomSuffix);
		
		return info;
	}
	

}
