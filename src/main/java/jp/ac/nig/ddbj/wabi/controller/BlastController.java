package jp.ac.nig.ddbj.wabi.controller;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Arrays;
import java.util.regex.Pattern;

import javax.inject.Inject;

import javax.servlet.http.HttpServletRequest;

import jp.ac.nig.ddbj.wabi.job.JobInfo;
import jp.ac.nig.ddbj.wabi.job.JobIdNotInitializedException;
import jp.ac.nig.ddbj.wabi.report.BlastErrorReport;
import jp.ac.nig.ddbj.wabi.report.BlastGetErrorReport;
import jp.ac.nig.ddbj.wabi.report.BlastGetReportOfStatus;
import jp.ac.nig.ddbj.wabi.report.BlastGetHelpReport;
import jp.ac.nig.ddbj.wabi.report.BlastPostReport;
import jp.ac.nig.ddbj.wabi.report.BlastIllegalRequestReport;
import jp.ac.nig.ddbj.wabi.request.BlastRequest;
import jp.ac.nig.ddbj.wabi.request.BlastGetRequest;
import jp.ac.nig.ddbj.wabi.request.BlastGetenvRequest;
import jp.ac.nig.ddbj.wabi.request.BlastGetHelpRequest;
import jp.ac.nig.ddbj.wabi.util.Conf;
import jp.ac.nig.ddbj.wabi.util.ConfBlast;
import jp.ac.nig.ddbj.wabi.util.CalendarUtil;
import jp.ac.nig.ddbj.wabi.validator.BlastPostRequestValidator;
import jp.ac.nig.ddbj.wabi.validator.BlastGetRequestValidator;
import jp.ac.nig.ddbj.wabi.validator.BlastGetenvRequestValidator;
import net.ogalab.util.linux.Bash;
import net.ogalab.util.linux.BashResult;
import net.ogalab.util.rand.RNG;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.servlet.ModelAndView;


/** blast2(普通のblast)のREST WebAPIインタフェイス
 * 
 * 
 * @author oogasawa
 *
 */
@Controller
public class BlastController {
	
	public static final String USER_REQUEST_FILE      = "user_request.json";
	public static final String QUERY_SEQUENCE_FILE    = "query_sequence.fasta";
	public static final String SHELL_SCRIPT_FILE      = "wabi_blast.sh";
	public static final String BLAST_RESULT_FILE      = "blast_result.txt";
	public static final String UGE_JOB_ID_FILE        = "uge_job_id.txt";
	public static final String JOB_INFO_FILE          = "job_info.json";
	public static final String FINISHED_FILE          = "finished.txt";
	public static final String BLASTPNG_SCRIPT_FILE   = "blastpng.pl";
	public static final String ILLEGAL_ARGUMENTS_FILE = "illegal_arguments.txt";
	//public static final String BLAST_MAIL_RESULT_FILE = "blast_mail_result.txt";
		
	public static String outfilePrefix  = "wabi_blast_";

	private static Pattern patternBlastGetenvPermittedRemoteAddr = Pattern.compile(ConfBlast.patternBlastGetenvPermittedRemoteAddr);
	private static Pattern patternBlastGetResultOfQsubPermittedRemoteAddr = Pattern.compile(ConfBlast.patternBlastGetResultOfQsubPermittedRemoteAddr);
	private static Pattern patternBlastGetStatusOfQsubPermittedRemoteAddr = Pattern.compile(ConfBlast.patternBlastGetStatusOfQsubPermittedRemoteAddr);
	private static Pattern patternBlastTestSecurityApplicationScanPagePermittedRemoteAddr = Pattern.compile(ConfBlast.patternBlastTestSecurityApplicationScanPagePermittedRemoteAddr);

	@Inject
	MessageSource messageSource;


	RNG engine = null;
	
	@Inject
	public BlastController(RNG engine) {
		this.engine = engine;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		/*
		 * Note: 脆弱性対策
		 * 不正なパラメータを指定された時に HTTP 500 エラーになっていたので、
		 * それを回避するために、有効なパラメータ名を列挙して指定しておく。
		 */
		binder.setAllowedFields("querySequence", "datasets", "database", "program", "parameters", "format", "result", "address",
								"format", "info",
								"requestId", "format", "imageId", "info",
								"helpCommand", "format", "program");
	}
	
	/** HTTP POSTメソッドで呼ばれた時の処理を行う。Job(wabi)の投入を行い、当該Job(wabi)のID (request-idと呼ぶ）を作成して返す.
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/blast", method=RequestMethod.POST)
	public ModelAndView post(@ModelAttribute BlastRequest request, BindingResult errors) throws BadRequestException, InternalServerErrorException {
		
		ModelAndView result = null;
		
		try {
			// 現在時刻を使って新規request-idを作成する。
			JobInfo jobInfo      = new JobInfo(engine);
			jobInfo.generateRequestId();

			// 新規Job(wabi)を実行する際のワーキングディレクトリ作成
			jobInfo.makeWorkingDir();

			// Request(user), Query(fasta)をワーキングディレクトリ中にファイルとしてセーブ
			jobInfo.save(USER_REQUEST_FILE,   request.toJsonStr());
			jobInfo.save(QUERY_SEQUENCE_FILE, request.getQuerySequence());

			// セキュリティのためにリクエスト値をチェックする
			Validator validator = new BlastPostRequestValidator();
			validator.validate(request, errors);
			if (errors.hasErrors()) {
				BlastIllegalRequestReport report = new BlastIllegalRequestReport(request);
				jobInfo.save(ILLEGAL_ARGUMENTS_FILE, errors.getAllErrors().toString());
				for (ObjectError objectError : errors.getAllErrors()) {
					String code = objectError.getCode();
					String targetName = (objectError instanceof FieldError) ? ((FieldError)objectError).getField() : objectError.getObjectName();
					try {
						report.addErrorMessage(messageSource.getMessage(code, new String[] { targetName }, null));
					} catch (NoSuchMessageException ignore) {
						report.addErrorCode(Arrays.asList(code, targetName));
					}
				}
				throw new BadRequestException(report);
			}

			// blast実行用シェルスクリプトファイル作成
			//jobInfo.save(SHELL_SCRIPT_FILE, makeShellScript(request, QUERY_SEQUENCE_FILE, BLAST_RESULT_FILE));
			jobInfo.save(SHELL_SCRIPT_FILE, makeShellScript(request, QUERY_SEQUENCE_FILE, BLAST_RESULT_FILE, jobInfo));

			// qsubコマンドを発行してUGEにjob(UGE)を投入
			String jobName = makeJobName(request.getProgram().trim());
			String jobId   = jobInfo.qsub(jobName, SHELL_SCRIPT_FILE);
			
			// jobId, jobInfoをファイルに保存
			jobInfo.save(UGE_JOB_ID_FILE, jobId + "\n"); // UGEのjob-id.
			jobInfo.save(JOB_INFO_FILE, jobInfo.getInfoAsJson(true));

			// 新規作成ジョブに関するレポートを返す
			BlastPostReport report = new BlastPostReport(jobInfo, request);
			result = new ModelAndView(request.getFormat(), "linked-hash-map", report);
		}
		catch (IOException e) {
			e.printStackTrace();
			BlastErrorReport report = null;
			try {
				report = new BlastErrorReport(request);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new InternalServerErrorException(report);
		}
		
		return result;
	}
	
	
	/** GETメソッドで呼ばれた時の処理を行う。request-idで表されるJobの現在の状態を返す.
	 * 
	 * @param requestId  jobを特定するためのID文字列。
	 * @param request GET入力データ (パラメータは format, imageId, info の 3種)
	 * @param req HTTPリクエスト
	 * @param errors エラー情報 (パラメータからモデルへの変換処理でのエラー情報を格納済み)
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value="/blast/{id}", method=RequestMethod.GET)
	public ModelAndView get(@PathVariable("id") String requestId,
							@ModelAttribute BlastGetRequest request,
							BindingResult errors,
							HttpServletRequest req) throws IOException, BadRequestException, NotFoundException {
		request.setRequestId(requestId);
		
		
		ModelAndView result = null;
		
		// セキュリティのためにリクエスト値をチェックする
		Validator validator = new BlastGetRequestValidator();
		validator.validate(request, errors);
		if (errors.hasErrors()) {
			BlastIllegalRequestReport report = new BlastIllegalRequestReport(request);
			System.out.println(report.get("current-time") + "[" + getClass() + "#get] validation: (" + errors.getAllErrors() + ")");
			for (ObjectError objectError : errors.getAllErrors()) {
				String code = objectError.getCode();
				String targetName = (objectError instanceof FieldError) ? ((FieldError)objectError).getField() : objectError.getObjectName();
				try {
					report.addErrorMessage(messageSource.getMessage(code, new String[] { targetName }, null));
				} catch (NoSuchMessageException ignore) {
					report.addErrorCode(Arrays.asList(code, targetName));
				}
			}
			throw new BadRequestException(report);
		}
		
		if (request.getInfo().equals("status")) {
			try {
				BlastGetReportOfStatus report = new BlastGetReportOfStatus(requestId, isPermittedRemoteAddr(req, patternBlastGetStatusOfQsubPermittedRemoteAddr));
				result = new ModelAndView(request.getFormat(), "linked-hash-map", report);
			} catch (JobIdNotInitializedException e) {
				BlastGetErrorReport report = new BlastGetErrorReport(request);
				report.put("Message", "Error (" + e + ")");
				System.out.println(report.get("current-time") + "[" + getClass() + "#get] status: (" + report + ")");
				throw new BadRequestException(report);
			}
		}
		else if (request.getInfo().equals("result")) {
			JobInfo jobInfo = new JobInfo(requestId);
			if (jobInfo.existsBlastResultFile()) {
				result = new ModelAndView("bigfile", "filename", jobInfo.getWorkingDir() + BLAST_RESULT_FILE);			
			} else {
				BlastGetErrorReport report = new BlastGetErrorReport(request);
				report.put("Message", "Error ( Results of your request id have been NOT FOUND, or still running.)");
				System.out.println(report.get("current-time") + "[" + getClass() + "#get] result: (" + report + ")");
				throw new NotFoundException(report);
			}
		}
		else if (request.getInfo().equals("request")) {
			JobInfo jobInfo = new JobInfo(requestId);

			if (jobInfo.existsUserRequestFile()) {
				String userRequestFile = jobInfo.getWorkingDir() + USER_REQUEST_FILE;
				result = new ModelAndView("requestfile", "filename", userRequestFile);
			}
			else {
				BlastGetErrorReport report = new BlastGetErrorReport(request);
				report.put("Message", "Unexpected error ( Results of your request id have been NOT FOUND.)");
				System.out.println(report.get("current-time") + "[" + getClass() + "#get] request: (" + report + ")");
				throw new NotFoundException(report);
			}
		}
		else if (request.getInfo().equals("result_stdout")) {
			result = getResultOfQsubOutput(requestId, request, req, true);
		}
		else if (request.getInfo().equals("result_stderr")) {
			result = getResultOfQsubOutput(requestId, request, req, false);
		}
		
		if (!("".equals(request.getImageId()) || request.getImageId() == null)){
			JobInfo jobInfo = new JobInfo(requestId);
			String imageFile = jobInfo.getWorkingDir() + requestId + "_" + request.getImageId() + ".png";
			if (!jobInfo.existsFile(imageFile)) {
				BlastGetErrorReport report = new BlastGetErrorReport(request);
				report.put("Message", "Error ( Blast image file of your request id have been NOT FOUND, or still running.)");
				System.out.println(report.get("current-time") + "[" + getClass() + "#get] imageId: (" + report + ")");
				throw new NotFoundException(report);
			} else {
				result = new ModelAndView("imagefile", "filename", imageFile);
			}
		}
		
		return result;

	}

	/** GETメソッドで呼ばれた時の処理を行う。request-idで表されるJobの現在の状態を返す.
	 * 
	 * @param request GET入力データ (パラメータは format, info の 2種)
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value="/blast", method=RequestMethod.GET)
	public ModelAndView getenv(
			@ModelAttribute BlastGetenvRequest request,
			BindingResult errors,
			HttpServletRequest req) throws IOException, NotFoundException, BadRequestException {

		/*
		 * Note: セキュリティに関わるシステム情報を出力できるので、
		 * DDBJ内部 の開発関係者だけが利用できるようにする。
		 */
		checkRemoteAddr(req, patternBlastGetenvPermittedRemoteAddr);

		// セキュリティのためにリクエスト値をチェックする
		Validator validator = new BlastGetenvRequestValidator();
		validator.validate(request, errors);
		if (errors.hasErrors()) {
			BlastGetErrorReport report = new BlastGetErrorReport(request);
			System.out.println(report.get("current-time") + "[" + getClass() + "#getenv] validation: (" + errors.getAllErrors() + ")");
			throw new BadRequestException(report);
		}

		ModelAndView result = null;
		
		if (request.getInfo().equals("env")) {
			LinkedHashMap<String, String> report = new LinkedHashMap<String, String>();
			Bash bash = new Bash();
			BashResult res = bash.system("env | sort");
			report.put("stdout", res.getStdout());
			report.put("stderr", res.getStderr());
			result = new ModelAndView(request.getFormat(), "linked-hash-map", report);			
		} else {
			BlastGetErrorReport report = new BlastGetErrorReport(request);
			report.put("Message", "Illegal arguments (info = " + request.getInfo() + ")");
			System.out.println(report.get("current-time") + "[" + getClass() + "#getenv] info: (" + report + ")");
			throw new BadRequestException(report);
		}
		
		return result;

	}

	/**
	 * Help 情報の使い方を返します。
	 * /blast/help/help_command の「help_command」の選択肢を返します。
	 */
	@RequestMapping(value="/blast/help", method=RequestMethod.GET)
	public ModelAndView help(@ModelAttribute BlastGetHelpRequest request,
							 BindingResult errors,
							 HttpServletRequest req) {
		return help(null, request, errors, req);
	}

	/**
	 * Help 情報を返します。
	 * 例: /blast/help/list_program
	 *
	 * help_command が省略された場合は、その使い方を返します。
	 * /blast/help/help_command の「help_command」の選択肢を返します。
	 */
	@RequestMapping(value="/blast/help/{help_command}", method=RequestMethod.GET)
	public ModelAndView help(@PathVariable("help_command") String helpCommand,
							 @ModelAttribute BlastGetHelpRequest request,
							 BindingResult errors,
							 HttpServletRequest req) {
		request.setHelpCommand(helpCommand);
		if (!"text".equals(request.getFormat()) &&
			!"json".equals(request.getFormat()) &&
			!"xml".equals(request.getFormat())) {
			request.setFormat("text");
		}

		boolean isPermittedRemoteAddrResultOfQsub = isPermittedRemoteAddr(req, patternBlastGetResultOfQsubPermittedRemoteAddr);
		boolean isPermittedRemoteAddrGetenv = isPermittedRemoteAddr(req, patternBlastGetenvPermittedRemoteAddr);
		BlastGetHelpReport report = new BlastGetHelpReport(request, isPermittedRemoteAddrResultOfQsub, isPermittedRemoteAddrGetenv);
		return new ModelAndView(request.getFormat(), "linked-hash-map", report);
	}

	@RequestMapping(value="/blast/test/security_application_scan", method=RequestMethod.GET)
	public String showSecurityApplicationScanPage(HttpServletRequest req) throws NotFoundException {
		if (!isPermittedRemoteAddr(req, patternBlastTestSecurityApplicationScanPagePermittedRemoteAddr)) {
			throw new NotFoundException(null);
		}
		return "test.security_application_scan";
	}


	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ModelAndView handlerException(BadRequestException e) {
		return handlerException_Inner(e, "BAD_REQUEST");
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ModelAndView handlerException(NotFoundException e) {
		return handlerException_Inner(e, "NOT_FOUND");
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView handlerException(InternalServerErrorException e) {
		return handlerException_Inner(e, "INTERNAL_SERVER_ERROR");
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView handlerException(Exception e) {
		e.printStackTrace();
		LinkedHashMap<String, String> report = new LinkedHashMap<String, String>();
		report.put("error-message", "INTERNAL_SERVER_ERROR (" + e.getMessage() + ")");
		report.put("current-time", CalendarUtil.getTime());
		return new ModelAndView("json", "linked-hash-map", report);
	}

	private ModelAndView handlerException_Inner(AbstractReportException e, String message) {
		LinkedHashMap<String, Object> report = e.getReport();
		if (null==report) {
			report = new LinkedHashMap<String, Object>();
		}
		if (!report.containsKey("error-message")) {
			report.put("error-message", message + " (" + e.getMessage() + ")");
		}
		if (!report.containsKey("current-time")) {
			report.put("current-time", CalendarUtil.getTime());
		}
		return new ModelAndView("json", "linked-hash-map", report);
	}

	
	//-----------------------------------------------------------------
	
	
	/** BLASTの計算を行うbash scriptを作成する.
	 * 
	 * <code>
	 * blastall -p blastn -d db_name -i fasta_file -o outfile -a 4 [other parameters]
	 * </code>
	 * 
	 * @param request
	 * @return
	 * @throws IOException 
	 */
	//public String makeShellScript(BlastRequest request, String fasta, String outfile) {
	public String makeShellScript(BlastRequest request, String fasta, String outfile, JobInfo jobInfo) throws IOException {
		StringBuffer buf = new StringBuffer();
		String requestId = jobInfo.generateRequestId();
		//buf.append("source /home/geadmin/UGEB/ugeb/common/settings.sh\n");
		buf.append("export BLASTDB=" + Conf.blastDbPath + "\n");
		buf.append("PNGPERLPATH=" + Conf.pngPerlPath + "\n");
		buf.append("WORKINGDIR=" + jobInfo.getWorkingDir() + "\n");
		buf.append("BLASTURL=" + Conf.blastUrl + requestId + "\n");
		buf.append("MESSAGE=" + "\"The results can be viewed at:\"" + "\n");
		buf.append("SUBJECT=" + "\"[WABI] " + requestId + " is finished.\"" + "\n\n");
		//megablast対応
		if("megablast".equals(request.getProgram())){
			buf.append("megablast");
		}
		else{
			buf.append("blastall");
			buf.append(" -p " + request.getProgram());
		}
		buf.append(" -d '" + request.getDatabase() + "' ");
		buf.append(" -i " + fasta);
		buf.append(" -o " + outfile);
		//CPU parameter 追加
		buf.append(" -a 16");
		buf.append(" " + request.getParameters() + "\n");
		//画像ファイル作成処理追加
		//buf.append("${PNGPERLPATH}" + BLASTPNG_SCRIPT_FILE);
		buf.append("perl ${PNGPERLPATH}" + BLASTPNG_SCRIPT_FILE);
		buf.append(" ${WORKINGDIR}" + outfile);
		buf.append(" " + jobInfo.generateRequestId()  + "\n");
		//メール送信処理追加
		if ("mail".equals(request.getResult())) {
			buf.append("echo -e \"Request ID   " + requestId + "\\n\\n");
			buf.append("\"${MESSAGE[*]}\"" + "\\n");
			buf.append("${BLASTURL}" + "\\n\\n");
			buf.append("Results\\n===========\\n");
			buf.append(" `cat " + outfile + "`\" | ");
			//buf.append("mail -s \"${SUBJECT[*]}\"");
			buf.append("mail -v -s \"${SUBJECT[*]}\"");
			buf.append(" " + request.getAddress());
			//buf.append(" > " + BLAST_MAIL_RESULT_FILE + " 2>&1\n");
			buf.append(" >/dev/null 2>&1\n");
		}
		buf.append("touch " + FINISHED_FILE);
		
		return buf.toString() + "\n";
	}
	
	public String makeJobName(String prog) {
		return outfilePrefix + prog.trim() + "_w3wabi"; 
	}

	private String makeJobName(JobInfo jobInfo) {
		try {
			LinkedHashMap<String, String> userRequest = jobInfo.readJsonFrom(USER_REQUEST_FILE);
			return makeJobName(userRequest.get("program"));
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * セキュリティに関わるシステム情報を出力できるアクション等の場合に
	 * DDBJ内部 の開発関係者だけが利用できるようにするために、
	 * 許可された 接続元IPアドレス か判別します。
	 *
	 * @param req HTTPリクエスト
	 * @param pattern 許可する 接続元IPアドレス の正規表現パターン
	 * @throws NotFoundException 許可された IPアドレス の場合以外
	 */
	private void checkRemoteAddr(HttpServletRequest req, Pattern pattern) throws NotFoundException {
		if (!isPermittedRemoteAddr(req, pattern)) {
			System.out.println(CalendarUtil.getTime() + "[" + getClass() + "] denied: remoteAddr = (" + req.getRemoteAddr() + ")");
			throw new NotFoundException(null);
		}
	}

	private boolean isPermittedRemoteAddr(HttpServletRequest req, Pattern pattern) {
		return pattern.matcher(req.getRemoteAddr()).matches();
	}

	private ModelAndView getResultOfQsubOutput(String requestId, BlastGetRequest request, HttpServletRequest req, boolean isStdout) throws IOException, NotFoundException {
		/*
		 * Note: qsub の標準出力にはシステム情報が含まれ得るので、 接続元IPアドレス で拒否します。
		 * システム情報の例: パスやアカウント名など。
		 */
		checkRemoteAddr(req, patternBlastGetResultOfQsubPermittedRemoteAddr);
		JobInfo jobInfo = new JobInfo(requestId);
		String jobName = makeJobName(jobInfo);
		String qsubOutFilename = isStdout ? jobInfo.getQsubStdoutFilename(jobName) : jobInfo.getQsubStderrFilename(jobName);
		if (null==jobName || jobInfo.existsFile(qsubOutFilename)) {
			BlastGetErrorReport report = new BlastGetErrorReport(request);
			report.put("Message", "Error ( " + (isStdout ? "Stdout" : "Stderr") + " of your request id have been NOT FOUND, or still running.)");
			System.out.println(report.get("current-time") + "[" + getClass() + "#get] " + (isStdout ? "result_stdout" : "result_stderr") + ": (" + report + ")");
			throw new NotFoundException(report);
		}
		return new ModelAndView("bigfile", "filename", qsubOutFilename);
	}

}
