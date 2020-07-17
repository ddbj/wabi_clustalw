package jp.ac.nig.ddbj.wabi.controller;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import jp.ac.nig.ddbj.wabi.job.WabiJobInfo;
import jp.ac.nig.ddbj.wabi.request.WabiGetHelpRequest;
import jp.ac.nig.ddbj.wabi.request.WabiGetRequest;
import jp.ac.nig.ddbj.wabi.request.WabiGetenvRequest;
import jp.ac.nig.ddbj.wabi.request.WabiRequest;
import jp.ac.nig.ddbj.wabi.util.CalendarUtil;
import jp.ac.nig.ddbj.wabi.util.ConfWabi;
import net.ogalab.util.rand.RNG;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
public abstract class WabiController {
	
	public static final String USER_REQUEST_FILE      = "user_request.json";
	public static final String QUERY_SEQUENCE_FILE    = "query_sequence.txt";
	public static final String SHELL_SCRIPT_FILE      = "wabi_shell_script.sh";
	public static final String WABI_OUT_FILE          = "wabi_result.out";
	public static final String UGE_JOB_ID_FILE        = "uge_job_id.txt";
	public static final String JOB_INFO_FILE          = "job_info.json";
	public static final String FINISHED_FILE          = "finished.txt";
	public static final String ILLEGAL_ARGUMENTS_FILE = "illegal_arguments.txt";

	public static String outfilePrefix  = null;

	public static Pattern patternGetenvPermittedRemoteAddr = Pattern.compile(ConfWabi.patternGetenvPermittedRemoteAddr);
	public static Pattern patternGetResultOfQsubPermittedRemoteAddr = Pattern.compile(ConfWabi.patternGetResultOfQsubPermittedRemoteAddr);
	public static Pattern patternGetStatusOfQsubPermittedRemoteAddr = Pattern.compile(ConfWabi.patternGetStatusOfQsubPermittedRemoteAddr);
	public static Pattern patternTestSecurityApplicationScanPagePermittedRemoteAddr = Pattern.compile(ConfWabi.patternTestSecurityApplicationScanPagePermittedRemoteAddr);

	@Inject
	MessageSource messageSource;


	RNG engine = null;
	
	@Inject
	public WabiController(RNG engine) {
		this.engine = engine;
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		/*
		 * Note: 脆弱性対策
		 * 不正なパラメータを指定された時に HTTP 500 エラーになっていたので、
		 * それを回避するために、有効なパラメータ名を列挙して指定しておく。
		 */
		binder.setAllowedFields("querySequence", "parameters", "format", "result", "address",
								"format", "info",
								"requestId", "format", "info",
								"helpCommand", "format");
	}
	
	/** HTTP POSTメソッドで呼ばれた時の処理を行う。Job(wabi)の投入を行い、当該Job(wabi)のID (request-idと呼ぶ）を作成して返す.
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(method=RequestMethod.POST)
	public abstract ModelAndView post(@ModelAttribute WabiRequest request, BindingResult errors)
			throws BadRequestException, InternalServerErrorException;

	
	/** GETメソッドで呼ばれた時の処理を行う。request-idで表されるJobの現在の状態を返す.
	 * 
	 * @param requestId  jobを特定するためのID文字列。
	 * @param request GET入力データ (パラメータは format, imageId, info の 3種)
	 * @param req HTTPリクエスト
	 * @param errors エラー情報 (パラメータからモデルへの変換処理でのエラー情報を格納済み)
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(method=RequestMethod.GET)
	public abstract ModelAndView get(
			@PathVariable("id") String requestId,
			@ModelAttribute WabiGetRequest request,
			BindingResult errors,
			HttpServletRequest req
			) throws IOException, BadRequestException, NotFoundException;

	
	/** GETメソッドで呼ばれた時の処理を行う。request-idで表されるJobの現在の状態を返す.
	 * 
	 * @param request GET入力データ (パラメータは format, info の 2種)
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(method=RequestMethod.GET)
	public abstract ModelAndView getenv(
			@ModelAttribute WabiGetenvRequest request,
			BindingResult errors,
			HttpServletRequest req
			) throws IOException, NotFoundException, BadRequestException;
	

	/**
	 * Help 情報の使い方を返します。
	 * /blast/help/help_command の「help_command」の選択肢を返します。
	 */
	@RequestMapping(method=RequestMethod.GET)
	public abstract ModelAndView help(
			@ModelAttribute WabiGetHelpRequest request,
			BindingResult errors,
			HttpServletRequest req);

	/**
	 * Help 情報を返します。
	 * 例: /blast/help/list_program
	 *
	 * help_command が省略された場合は、その使い方を返します。
	 * /blast/help/help_command の「help_command」の選択肢を返します。
	 */
	@RequestMapping(method=RequestMethod.GET)
	public abstract ModelAndView help(
			@PathVariable("help_command") String helpCommand,
			@ModelAttribute WabiGetHelpRequest request,
			BindingResult errors,
			HttpServletRequest req);

	/** CLUSTALWの計算を行うbash scriptを作成する.
	 * 
	 * <code>
	 * clustalw2 -INFILE=test2.fasta -TYPE=DNA -OUTFILE=outfile [other parameters]
	 * </code>
	 * 
	 * @param request
	 * @return
	 * @throws IOException 
	 */
	public abstract String makeShellScript(
			WabiRequest request,
			String infile,
			String outfile,
			WabiJobInfo jobInfo
			) throws IOException;

		
	protected abstract ModelAndView getResultOfQsubOutput(
			String requestId,
			WabiGetRequest request,
			HttpServletRequest req,
			boolean isStdout
			) throws IOException, NotFoundException;

	
	@RequestMapping(method=RequestMethod.GET)
	public String showSecurityApplicationScanPage(HttpServletRequest req) throws NotFoundException {
		if (!isPermittedRemoteAddr(req, patternTestSecurityApplicationScanPagePermittedRemoteAddr)) {
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

	protected ModelAndView handlerException_Inner(AbstractReportException e, String message) {
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

	
	public String makeJobName(String prog) {
		return outfilePrefix + prog.trim() + "_w3wabi"; 
	}

	protected String makeJobName(WabiJobInfo jobInfo) {
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
	protected void checkRemoteAddr(HttpServletRequest req, Pattern pattern) throws NotFoundException {
		if (!isPermittedRemoteAddr(req, pattern)) {
			System.out.println(CalendarUtil.getTime() + "[" + getClass() + "] denied: remoteAddr = (" + req.getRemoteAddr() + ")");
			throw new NotFoundException(null);
		}
	}

	protected boolean isPermittedRemoteAddr(HttpServletRequest req, Pattern pattern) {
		return pattern.matcher(req.getRemoteAddr()).matches();
	}




}