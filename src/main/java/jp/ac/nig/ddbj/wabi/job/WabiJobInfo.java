package jp.ac.nig.ddbj.wabi.job;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.ac.nig.ddbj.wabi.util.Conf;

import net.arnx.jsonic.JSON;
import net.ogalab.util.linux.Bash;
import net.ogalab.util.linux.BashResult;
import net.ogalab.util.os.FileIO;
import net.ogalab.util.rand.RNG;

public abstract class WabiJobInfo {
	
	public String requestId    = null;
	
	public String prefix    = null;
	public String year      = null;
	public String month     = null;
	public String day       = null;
	public String hour      = null;
	public String min       = null;
	public String sec       = null;
	public String millisec  = null;
	public String randomSuffix  = null;

	Pattern pRequestId = Pattern.compile("(.+?)_([0-9]{4})-([0-9]{2})([0-9]{2})-([0-9]{2})([0-9]{2})-([0-9]{2})-([0-9]+)-([0-9]+)");
	
	Pattern p1 = Pattern.compile("^Following jobs do not exists", Pattern.MULTILINE);
	Pattern p2 = Pattern.compile("^usage\\s+", Pattern.MULTILINE);
	Pattern p3 = Pattern.compile("^job_number:\\s+", Pattern.MULTILINE);
	Pattern p4 = Pattern.compile("^scheduling info:\\s+There are no messages available", Pattern.MULTILINE);

	/** このディレクトリ以下にWebAPIによるプログラム実行結果などジョブの情報が置かれる */
	//public static String workingDirRoot = "/home/oogasawa/data1/wabi-test/";
	public static String workingDirRoot = Conf.workingDirBase;
	
	public String jobId     = null;
		
	/** Random Number Generator Object (singleton) */
	RNG engine = null;

	/** ロックオブジェクト */
	protected final static Object lock = new Object();
	

	public WabiJobInfo(RNG e) {
		engine = e;
	}
	
	/** requestIdの文字列をパースすることによりjobInfoオブジェクトを作る。
	 * 
	 * @param requestId
	 * @throws IOException jobIDファイルがない。requestIdの文字列がおかしいかもしれません。
	 */
	public WabiJobInfo(String requestId) throws IOException {
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

	
	abstract public boolean existsFinishedFile();

	abstract public boolean existsOutFile();
	
	/** jobIDがかかれたファイルの中身を読んでjobIDを取得し、それを文字列として返す。
	 * /home/geadmin/UGEB/ugeb/common/settings.sh
	 * リクエストは既に実行されていてjobIDは既に発行されていると前提している。
	 * 
	 * @return jobID
	 * @throws IOException 
	 */
	abstract public String readJobId() throws IOException;

	/** requestIdが存在しなければ作成し、オブジェクトに登録する。既にオブジェクトに登録されているのであれば何もしない。
	 * 
	 * @return requestId
	 * @throws IOException 
	 */
	abstract public String generateRequestId() throws IOException;
	
	/** requestIdが既に発行されているかどうかに関わらずrequestIdを再発行し、オブジェクトに登録する。
	 *  対応するディレクトリも作成する。（同一IDが既にとられていないかを判定するため）.
	 * 
	 * @return requestId
	 * @throws IOException 
	 */
	abstract public String reGenerateRequestId() throws IOException;
	
	abstract public boolean existsUserRequestFile();

	/** Jobが動作するWorking Directoryのフルパス名をStringとして返す.
	 * 
	 * makeWorkingDir()メソッドを呼ぶまではディレクトリの実体は作成されていないかもしれない。
	 * 
	 * @return Working Directoryのフルパス名
	 */
	public abstract String getWorkingDir();
	
	
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


	public ArrayList<String> qstatStatus(Bash bash, boolean canPrintSystemInfo)  {
		ArrayList<String> result = new ArrayList<String>(2);
		result.add("");
		result.add("");
		
		Matcher m  = null;

		try {
			BashResult res = bash.system("source /home/geadmin2/UGES/uges/common/settings.sh; qstat -j " + jobId);
			
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
			BashResult res = bash.system("source /home/geadmin2/UGES/uges/common/settings.sh; qacct -d 7 -j " + jobId);
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
	
	
	public abstract LinkedHashMap<String, String> getInfo(boolean withWorkingDirRoot);
	
	public String getInfoAsJson(boolean withWorkingDirRoot) {
		return JSON.encode(getInfo(withWorkingDirRoot), true);
	}
	
	public String getRequestId() throws IOException {
		return generateRequestId();
	}
	
	public String getJobId() {
		return jobId;
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

	public boolean existsWorkingDir() {
		String path = getWorkingDir();
		return new File(path).exists();
	}

	public boolean existsFile(String filePath) {
		return new File(filePath).exists();
	}
}
