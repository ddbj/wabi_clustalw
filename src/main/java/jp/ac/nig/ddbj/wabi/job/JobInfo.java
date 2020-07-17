package jp.ac.nig.ddbj.wabi.job;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.ac.nig.ddbj.wabi.controller.BlastController;
import jp.ac.nig.ddbj.wabi.util.Conf;

import net.arnx.jsonic.JSON;
import net.ogalab.util.container.ArrayUtil;
import net.ogalab.util.fundamental.StringUtil;
import net.ogalab.util.fundamental.Type;
import net.ogalab.util.linux.Bash;
import net.ogalab.util.linux.BashResult;
import net.ogalab.util.os.FileIO;
import net.ogalab.util.rand.RNG;
import cern.jet.random.Uniform;

public class JobInfo {
	
	String requestId    = null;
	
	private String prefix    = null;
	private String year      = null;
	private String month     = null;
	private String day       = null;
	private String hour      = null;
	private String min       = null;
	private String sec       = null;
	private String millisec  = null;
	private String randomSuffix  = null;

	Pattern pRequestId = Pattern.compile("(.+?)_([0-9]{4})-([0-9]{2})([0-9]{2})-([0-9]{2})([0-9]{2})-([0-9]{2})-([0-9]+)-([0-9]+)");
	
	Pattern p1 = Pattern.compile("^Following jobs do not exists", Pattern.MULTILINE);
	Pattern p2 = Pattern.compile("^usage\\s+", Pattern.MULTILINE);
	Pattern p3 = Pattern.compile("^job_number:\\s+", Pattern.MULTILINE);
	Pattern p4 = Pattern.compile("^scheduling info:\\s+There are no messages available", Pattern.MULTILINE);

	/** このディレクトリ以下にWebAPIによるプログラム実行結果などジョブの情報が置かれる */
	//public static String workingDirRoot = "/home/oogasawa/data1/wabi-test/";
	public static String workingDirRoot = Conf.workingDirBase;
	
	String jobId     = null;
	
	
	/** Random Number Generator Object (singleton) */
	RNG engine = null;


	/** ロックオブジェクト */
	private final static Object lock = new Object();
	

	public JobInfo(RNG e) {
		engine = e;
	}
	

	/** requestIdの文字列をパースすることによりjobInfoオブジェクトを作る。
	 * 
	 * @param requestId
	 * @throws IOException jobIDファイルがない。requestIdの文字列がおかしいかもしれません。
	 */
	public JobInfo(String requestId) throws IOException {
		// TODO この関数のエラー処理（例外処理体系) : 特にrequestIdの文字列が一致しない場合
		this.requestId = requestId;
		
		Matcher m = pRequestId.matcher(requestId);
		if (m.matches()) {
			  prefix    = m.group(1);
			  year      = m.group(2);
			  month     = m.group(3);
			  day       = m.group(4);
			  hour      = m.group(5);
			  min       = m.group(6);
			  sec       = m.group(7);
			  millisec  = m.group(8);
			  randomSuffix  = m.group(9);
		}
		
		jobId = readJobId();
		
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
	public ArrayList<String> getStatus(boolean canPrintSystemInfo) throws IOException, JobIdNotInitializedException {
		ArrayList<String> status = null;
		
		Bash bash = new Bash();
		bash.setWorkingDirectory(new File(getWorkingDir()));

		// jobId が初期化できているか確認しておく
		if (null==jobId || jobId.isEmpty()) {
			/*
			 * Note: jobId は、 qsub実行結果から文字列として切り出して
			 * BlastController.UGE_JOB_ID_FILE ファイルに記載されている筈です。
			 * 稀に BlastController.UGE_JOB_ID_FILE ファイルが存在しなかったり、
			 * 又は中身に jobId が記述されていない、という異常系が発生し得ますが、
			 * 万が一その状態 (jobId が空文字列) で qstat, qacc コマンドを実行すると、
			 * 予期しない実行結果になります。
			 * 例: qacct コマンドは、全ての jobId の情報を出力してしまい、
			 * 他利用者の実行情報が漏れることになる。
			 */
			throw new JobIdNotInitializedException();
		}

		ArrayList<String> qstatStatus = qstatStatus(bash, canPrintSystemInfo);
		ArrayList<String> qacctStatus = qacctStatus(bash, canPrintSystemInfo);
		
		if (existsFinishedFile()) {
			status = new ArrayList<String>();
			status.add("finished");
			if (qacctStatus.get(0).equals("finished")) {
				status.add(qacctStatus.get(1));
			}
			else {
				status.add("");
			}
		}
		else if (qstatStatus.get(0).equals("waiting")) { 
			status = qstatStatus;
		}
		else if (qstatStatus.get(0).equals("running")) {
			status = qstatStatus;
		}
		else {
			status = new ArrayList<String>();
			status.add("not-found");
			status.add(qstatStatus.get(1) + "\n" + qacctStatus.get(1));
		}
		
		return status;
	}

	public boolean existsFinishedFile() {
		String path = getWorkingDir() + BlastController.FINISHED_FILE;
		return new File(path).exists();
	}

	public boolean existsBlastResultFile() {
		String path = getWorkingDir() + BlastController.BLAST_RESULT_FILE;
		return new File(path).exists();
	}

	public ArrayList<String> qstatStatus(Bash bash, boolean canPrintSystemInfo)  {
		ArrayList<String> result = new ArrayList<String>(2);
		result.add("");
		result.add("");
		
		Matcher m  = null;

		try {
			BashResult res = bash.system("qstat -j " + jobId);
			
			String stdout = res.getStdout();
			
			m = p1.matcher(stdout); // not exists in the queue.
			if (m.find()) {
				result.set(0, "");
				result.set(1, "\n" + (canPrintSystemInfo ? stdout : "stdout"));
				return result;
			}
			
			m = p2.matcher(stdout); 
			if (m.find()) {
				result.set(0, "running");
				result.set(1, "\n" + (canPrintSystemInfo ? stdout : "stdout"));
				return result;
			}
			
			m = p3.matcher(stdout);
			if (m.find()) {
				result.set(0, "waiting");
				result.set(1, "\n" + (canPrintSystemInfo ? stdout : "stdout"));
				return result;
			}
			
			m = p4.matcher(stdout);
			if (m.find()) {
				result.set(0, "");
				result.set(1, "\n" + (canPrintSystemInfo ? stdout : "stdout"));
				return result;
			}
			
		} catch (Exception e) {
			// nothing to do.
		}
		return result;
	}
	
	@SuppressWarnings("finally")
	public ArrayList<String> qacctStatus(Bash bash, boolean canPrintSystemInfo) {
		ArrayList<String> result = new ArrayList<String>(2);
		result.add("");
		result.add("");

		try {
			BashResult res = bash.system("qacct -d 7 -j " + jobId);
			if (!res.getStdout().equals("")) {
				result.set(0, "finished");
				result.set(1, "\n" + (canPrintSystemInfo ? res.getStdout() : "stdout"));
			}
		}
		catch (Exception e) {
			// nothing to do.
			// qacctはjobの番号が存在しないと以下の様にエラーを返す。これは無視してよい。
			// $ sudo -u tomcat7 qacct -j 800
			// error: job id 800 not found
		}
		finally {
			return result;
		}

		//return result;
	}
	
	

	

	/** jobIDがかかれたファイルの中身を読んでjobIDを取得し、それを文字列として返す。
	 * /home/geadmin/UGEB/ugeb/common/settings.sh
	 * リクエストは既に実行されていてjobIDは既に発行されていると前提している。
	 * 
	 * @return jobID
	 * @throws IOException 
	 */
	public String readJobId() throws IOException {
		String path = getWorkingDir() + BlastController.UGE_JOB_ID_FILE;
		//String id   = FileIO.readFile(path);
		String id = "";
		if (new File(path).exists())
			id = FileIO.readFile(path);
		
		return id.trim();
	}

	/**
	 * ファイルの内容をデコードした Map を返します。
	 * 作業ディレクトリ、ファイルが存在して、 JSON文字列 が保存されている前提です。
	 *
	 * @param filename 作業ディレクトリに存在するファイル名
	 * @return ファイルから読み取った JSONデータ
	 * @throws IOException 入出力エラー
	 */
	public LinkedHashMap<String, String> readJsonFrom(String filename) throws IOException {
		String path = getWorkingDir() + filename;
		return JSON.decode(new FileReader(path), LinkedHashMap.class);
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
	
	public String getInfoAsJson(boolean withWorkingDirRoot) {
		return JSON.encode(getInfo(withWorkingDirRoot), true);
	}
	
	public String getRequestId() throws IOException {
		return generateRequestId();
	}
	
	public String getJobId() {
		return jobId;
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
		
		requestId = BlastController.outfilePrefix + ArrayUtil.join("-", new String[]{year, month+day, hour+min, sec, millisec, randomSuffix}).trim();
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
	
	
	/** Jobがその上で動作するWorking Directoryを作成する。既にあれば何もしない.
	 * 
	 * @throws IOException
	 */
	public void makeWorkingDir() throws IOException {
		String dir = getWorkingDir();
		Bash bash = new Bash();
		bash.system("mkdir -p " + dir);
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
	
	/** Working Directory内に文字列を指定されたファイル名でセーブする。
	 * 
	 * @param filename
	 * @param str
	 * @throws IOException 
	 */
	public void save(String filename, String str) throws IOException {
		makeWorkingDir();
		String path = getWorkingDir() + filename;
		PrintWriter pw = FileIO.getPrintWriter(path, "UTF-8");
		pw.print(str);
		pw.close();
	}
	
	/** Univa Grid Engine(UGE)のqsubを実行してジョブをサブミットし、結果としてUGEのjobIDを返す。
	 * 
	 * @param jobName
	 * @param scriptName
	 * @return サブミットされたJobのjobID. 何か失敗するとnullが返る。
	 * @throws IOException
	 */
	public String qsub(String jobName, String scriptName) throws IOException {
		jobId = null;

		String qsub = null;
		BashResult res = null;

		makeWorkingDir(); // 念のため。
		Bash bash = new Bash();
		bash.setWorkingDirectory(new File(getWorkingDir()));

		//qsub = "qsub -cwd -N " + jobName + " -S /bin/bash " + scriptName;
		//memory, slotの指定追加
		qsub = "source /home/geadmin2/UGES/uges/common/settings.sh; " + "qsub " + Conf.qsubOptions + " " + jobName + " -S /bin/bash " + scriptName;
		// 例 (本番系): qsub = "qsub -l s_vmem=64G -l mem_req=64G -pe def_slot 1 -cwd -N " + jobName + " -S /bin/bash " + scriptName
		// Note: 開発環境の qsub コマンドは幾つかのオプションを指定できない。
		System.out.println(getWorkingDir() + "\n" + qsub);


		res = bash.system(qsub);
		String stdout = res.getStdout();
		String stderr = res.getStderr();

		System.out.println("qsub output: \n" + stdout + "\n" + stderr);

		Pattern pJobID = Pattern.compile("Your job\\s+([0-9]+)\\s");

		Matcher m = pJobID.matcher(stdout);
		if (m.find()) {
			jobId = m.group(1);
		}


		return jobId;
	}

	/**
	 * qsub の標準出力を保存するファイルのパスを返します。
	 */
	public String getQsubStdoutFilename(String jobName) {
		return getWorkingDir() + jobName + ".o" + jobId;
	}

	/**
	 * qsub の標準エラー出力を保存するファイルのパスを返します。
	 */
	public String getQsubStderrFilename(String jobName) {
		return getWorkingDir() + jobName + ".e" + jobId;
	}

	public boolean existsUserRequestFile() {
		String path = getWorkingDir() + BlastController.USER_REQUEST_FILE;
		return new File(path).exists();
	}

	public boolean existsWorkingDir() {
		String path = getWorkingDir();
		return new File(path).exists();
	}

	public boolean existsFile(String filePath) {
		return new File(filePath).exists();
	}
}
