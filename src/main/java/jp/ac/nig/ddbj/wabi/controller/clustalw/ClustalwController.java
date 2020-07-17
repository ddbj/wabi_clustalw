package jp.ac.nig.ddbj.wabi.controller.clustalw;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import jp.ac.nig.ddbj.wabi.controller.BadRequestException;
import jp.ac.nig.ddbj.wabi.controller.InternalServerErrorException;
import jp.ac.nig.ddbj.wabi.controller.NotFoundException;
import jp.ac.nig.ddbj.wabi.controller.WabiController;
import jp.ac.nig.ddbj.wabi.job.clustalw.ClustalwJobInfo;
import jp.ac.nig.ddbj.wabi.job.JobIdNotInitializedException;
import jp.ac.nig.ddbj.wabi.job.WabiJobInfo;
import jp.ac.nig.ddbj.wabi.report.WabiGetHelpReport;
import jp.ac.nig.ddbj.wabi.report.clustalw.ClustalwErrorReport;
import jp.ac.nig.ddbj.wabi.report.clustalw.ClustalwGetErrorReport;
import jp.ac.nig.ddbj.wabi.report.clustalw.ClustalwGetHelpReport;
import jp.ac.nig.ddbj.wabi.report.clustalw.ClustalwGetReportOfStatus;
import jp.ac.nig.ddbj.wabi.report.clustalw.ClustalwIllegalRequestReport;
import jp.ac.nig.ddbj.wabi.report.clustalw.ClustalwPostReport;
import jp.ac.nig.ddbj.wabi.request.WabiGetHelpRequest;
import jp.ac.nig.ddbj.wabi.request.WabiGetRequest;
import jp.ac.nig.ddbj.wabi.request.WabiGetenvRequest;
import jp.ac.nig.ddbj.wabi.request.WabiRequest;
import jp.ac.nig.ddbj.wabi.request.clustalw.ClustalwRequest;
import jp.ac.nig.ddbj.wabi.util.Conf;
import jp.ac.nig.ddbj.wabi.util.ConfClustalw;
import jp.ac.nig.ddbj.wabi.validator.WabiGetenvRequestValidator;
import jp.ac.nig.ddbj.wabi.validator.clustalw.ClustalwGetRequestValidator;
import jp.ac.nig.ddbj.wabi.validator.clustalw.ClustalwPostRequestValidator;
import net.ogalab.util.linux.Bash;
import net.ogalab.util.linux.BashResult;
import net.ogalab.util.rand.RNG;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ClustalwController extends WabiController {

	public static final String WABI_OUT_TREE          = "query_sequence.ph";
	public static final String WABI_OUT_BTREE         = "query_sequence.phb";
	public static final String WABI_OUT_PIM           = "query_sequence.pim";
	public static final String WABI_PROFILE1          = "profile1";
	public static final String WABI_PROFILE2          = "profile2";
	public static final String WABI_GUIDETREE1        = "guidetree1";         //入力ファイル
	public static final String WABI_GUIDETREE2        = "guidetree2";         //入力ファイル
	public static final String WABI_OUT_GUIDETREE1    = "profile1.dnd";       //出力ファイル
	public static final String WABI_OUT_GUIDETREE2    = "profile2.dnd";       //出力ファイル
	public static final String WABI_PW_DNA_MATRIX     = "pw_dna_weight_matrix.txt";
	public static final String WABI_PW_AA_MATRIX      = "pw_aa_weight_matrix.txt";
	public static final String WABI_DNA_MATRIX        = "dna_weight_matrix.txt";
	public static final String WABI_AA_MATRIX         = "aa_weight_matrix.txt";
	public static final String WABI_LOG               = "clustalw.log";

	public static String outfilePrefix = "wabi_clustalw_";

	public static Pattern patternGetenvPermittedRemoteAddr
		= Pattern.compile(ConfClustalw.patternGetenvPermittedRemoteAddr);
	public static Pattern patternGetResultOfQsubPermittedRemoteAddr
		= Pattern.compile(ConfClustalw.patternGetResultOfQsubPermittedRemoteAddr);
	public static Pattern patternGetStatusOfQsubPermittedRemoteAddr
		= Pattern.compile(ConfClustalw.patternGetStatusOfQsubPermittedRemoteAddr);
	public static Pattern patternTestSecurityApplicationScanPagePermittedRemoteAddr
		= Pattern.compile(ConfClustalw.patternTestSecurityApplicationScanPagePermittedRemoteAddr);

	@Inject
	MessageSource messageSource;

	RNG engine = null;

	@Inject
	public ClustalwController(RNG engine) {
		super(engine);
	}

	@Override
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		/*
		 * Note: 脆弱性対策
		 * 不正なパラメータを指定された時に HTTP 500 エラーになっていたので、
		 * それを回避するために、有効なパラメータ名を列挙して指定しておく。
		 */
		binder.setAllowedFields("querySequence", "profile1", "profile2", "guidetree1", "guidetree2",
								"pwDnaMatrix", "pwAaMatrix", "dnaMatrix", "aaMatrix", "parameters", "format", "result", "address",
								"format", "info",
								"requestId", "format", "info",
								"helpCommand", "format");
	}
	
	/**
	 * HTTP POSTメソッドで呼ばれた時の処理を行う。Job(wabi)の投入を行い、当該Job(wabi)のID
	 * (request-idと呼ぶ）を作成して返す.
	 * 
	 * @param request
	 * @return
	 */
	@Override
	@RequestMapping(value = "/clustalw_dummy", method = RequestMethod.POST)
	public ModelAndView post(@ModelAttribute WabiRequest request,
			BindingResult errors) throws BadRequestException,
			InternalServerErrorException {
		ModelAndView result = null;
		return result;
	}
	
	
	@RequestMapping(value = "/clustalw", method = RequestMethod.POST)
	public ModelAndView post(@ModelAttribute ClustalwRequest request,
			BindingResult errors) throws BadRequestException,
			InternalServerErrorException {

		ModelAndView result = null;

		try {
			// 現在時刻を使って新規request-idを作成する。
			ClustalwJobInfo jobInfo = new ClustalwJobInfo(engine);
			jobInfo.generateRequestId();

			// 新規Job(wabi)を実行する際のワーキングディレクトリ作成
			jobInfo.makeWorkingDir();

			// Request(user), Query(fasta)をワーキングディレクトリ中にファイルとしてセーブ
			jobInfo.save(USER_REQUEST_FILE, request.toJsonStr());

			if (request.getQuerySequence() != null && !request.getQuerySequence().isEmpty())
				jobInfo.save(QUERY_SEQUENCE_FILE, request.getQuerySequence());

			if (request.getProfile1() != null && !request.getProfile1().isEmpty())
				jobInfo.save(WABI_PROFILE1, request.getProfile1());
			if (request.getProfile2() != null && !request.getProfile2().isEmpty())
				jobInfo.save(WABI_PROFILE2, request.getProfile2());
			if (request.getGuidetree1() != null && !request.getGuidetree1().isEmpty())
				jobInfo.save(WABI_GUIDETREE1, request.getGuidetree1());
			if (request.getGuidetree2() != null && !request.getGuidetree2().isEmpty())
				jobInfo.save(WABI_GUIDETREE2, request.getGuidetree2());
			if (request.getPwDnaMatrix() != null && !request.getPwDnaMatrix().isEmpty())
				jobInfo.save(WABI_PW_DNA_MATRIX, request.getPwDnaMatrix());
			if (request.getPwAaMatrix() != null && !request.getPwAaMatrix().isEmpty())
				jobInfo.save(WABI_PW_AA_MATRIX, request.getPwAaMatrix());
			if (request.getDnaMatrix() != null && !request.getDnaMatrix().isEmpty())
				jobInfo.save(WABI_DNA_MATRIX, request.getDnaMatrix());
			if (request.getAaMatrix() != null && !request.getAaMatrix().isEmpty())
				jobInfo.save(WABI_AA_MATRIX, request.getAaMatrix());

			// セキュリティのためにリクエスト値をチェックする
			Validator validator = new ClustalwPostRequestValidator();
			validator.validate(request, errors);
			if (errors.hasErrors()) {
				ClustalwIllegalRequestReport report = new ClustalwIllegalRequestReport(
						request);
				jobInfo.save(ILLEGAL_ARGUMENTS_FILE, errors.getAllErrors()
						.toString());
				throw new BadRequestException(report);
			}

			// clustalw実行用シェルスクリプトファイル作成
			jobInfo.save(SHELL_SCRIPT_FILE, makeShellScript(request, QUERY_SEQUENCE_FILE, WABI_OUT_FILE, jobInfo));

			// qsubコマンドを発行してUGEにjob(UGE)を投入
			String jobName = "clustalw2";
			String jobId = jobInfo.qsub(jobName, SHELL_SCRIPT_FILE);

			// jobId, jobInfoをファイルに保存
			jobInfo.save(UGE_JOB_ID_FILE, jobId + "\n"); // UGEのjob-id.
			jobInfo.save(JOB_INFO_FILE, jobInfo.getInfoAsJson(true));

			// 新規作成ジョブに関するレポートを返す
			ClustalwPostReport report = new ClustalwPostReport(jobInfo, request);
			result = new ModelAndView(request.getFormat(), "linked-hash-map",
					report);
		} catch (IOException e) {
			e.printStackTrace();
			LinkedHashMap<String, Object> report = null;
			try {
				report = new ClustalwErrorReport(request);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new InternalServerErrorException(report);
		}

		return result;
	}

	/**
	 * GETメソッドで呼ばれた時の処理を行う。request-idで表されるJobの現在の状態を返す.
	 * 
	 * @param requestId
	 *            jobを特定するためのID文字列。
	 * @param request
	 *            GET入力データ (パラメータは format, imageId, info の 3種)
	 * @param req
	 *            HTTPリクエスト
	 * @param errors
	 *            エラー情報 (パラメータからモデルへの変換処理でのエラー情報を格納済み)CLUSTALW
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/clustalw/{id}", method = RequestMethod.GET)
	public ModelAndView get(@PathVariable("id") String requestId,
			@ModelAttribute WabiGetRequest request, BindingResult errors,
			HttpServletRequest req) throws IOException, BadRequestException,
			NotFoundException {

		request.setRequestId(requestId);

		ModelAndView result = null;

		// セキュリティのためにリクエスト値をチェックする
		Validator validator = new ClustalwGetRequestValidator();
		validator.validate(request, errors);
		if (errors.hasErrors()) {
			ClustalwIllegalRequestReport report = new ClustalwIllegalRequestReport(
					request);
			System.out.println(report.get("current-time") + "[" + getClass()
					+ "#get] validation: (" + errors.getAllErrors() + ")");
			throw new BadRequestException(report);
		}

		if (request.getInfo().equals("status")) {
			try {
				ClustalwGetReportOfStatus report = new ClustalwGetReportOfStatus(
						requestId, isPermittedRemoteAddr(req,
								patternGetStatusOfQsubPermittedRemoteAddr));
				result = new ModelAndView(request.getFormat(),
						"linked-hash-map", report);
			} catch (JobIdNotInitializedException e) {
				LinkedHashMap<String, Object> report = new ClustalwGetErrorReport(
						request);
				report.put("Message", "Error (" + e + ")");
				System.out.println(report.get("current-time") + "["
						+ getClass() + "#get] status: (" + report + ")");
				throw new BadRequestException(report);
			}
		} else if (request.getInfo().equals("result")) {
			ClustalwJobInfo jobInfo = new ClustalwJobInfo(requestId);
			//multiple alignment
			if (jobInfo.existsOutFile()) {
				result = new ModelAndView("bigfile", "filename",
						jobInfo.getWorkingDir() + WABI_OUT_FILE);
			//tree
			} else if (jobInfo.existsOutTree()) {
				result = new ModelAndView("bigfile", "filename",
						jobInfo.getWorkingDir() + WABI_OUT_TREE);
			//bootstrap tree
			} else if (jobInfo.existsOutBTree()) {
				result = new ModelAndView("bigfile", "filename",
						jobInfo.getWorkingDir() + WABI_OUT_BTREE);
			} else {
				ClustalwGetErrorReport report = new ClustalwGetErrorReport(request);
				report.put("Message",
						"Error ( Results of your request id have been NOT FOUND, or still running.)");
				System.out.println(report.get("current-time") + "["
						+ getClass() + "#get] result: (" + report + ")");
				throw new NotFoundException(report);
			}
		} else if (request.getInfo().equals("result_guide1")) {
			ClustalwJobInfo jobInfo = new ClustalwJobInfo(requestId);
			if (jobInfo.existsOutGuideTree1()) {
				result = new ModelAndView("bigfile", "filename",
						jobInfo.getWorkingDir() + WABI_OUT_GUIDETREE1);
			} else {
				ClustalwGetErrorReport report = new ClustalwGetErrorReport(request);
				report.put("Message",
						"Error ( Results of your request id have been NOT FOUND, or still running.)");
				System.out.println(report.get("current-time") + "["
						+ getClass() + "#get] result: (" + report + ")");
				throw new NotFoundException(report);
			}
		} else if (request.getInfo().equals("result_guide2")) {
			ClustalwJobInfo jobInfo = new ClustalwJobInfo(requestId);
			if (jobInfo.existsOutGuideTree2()) {
				result = new ModelAndView("bigfile", "filename",
						jobInfo.getWorkingDir() + WABI_OUT_GUIDETREE2);
			} else {
				ClustalwGetErrorReport report = new ClustalwGetErrorReport(request);
				report.put("Message",
						"Error ( Results of your request id have been NOT FOUND, or still running.)");
				System.out.println(report.get("current-time") + "["
						+ getClass() + "#get] result: (" + report + ")");
				throw new NotFoundException(report);
			}
		} else if (request.getInfo().equals("result_pim")) {
			ClustalwJobInfo jobInfo = new ClustalwJobInfo(requestId);
			if (jobInfo.existsOutPim()) {
				result = new ModelAndView("bigfile", "filename",
						jobInfo.getWorkingDir() + WABI_OUT_PIM);
			} else {
				ClustalwGetErrorReport report = new ClustalwGetErrorReport(request);
				report.put("Message",
						"Error ( Results of your request id have been NOT FOUND, or still running.)");
				System.out.println(report.get("current-time") + "["
						+ getClass() + "#get] result: (" + report + ")");
				throw new NotFoundException(report);
			}
		} else if (request.getInfo().equals("result_log")) {
			ClustalwJobInfo jobInfo = new ClustalwJobInfo(requestId);
			if (jobInfo.existsLog()) {
				result = new ModelAndView("bigfile", "filename",
						jobInfo.getWorkingDir() + WABI_LOG);
			} else {
				ClustalwGetErrorReport report = new ClustalwGetErrorReport(request);
				report.put("Message",
						"Error ( Results of your request id have been NOT FOUND, or still running.)");
				System.out.println(report.get("current-time") + "["
						+ getClass() + "#get] result: (" + report + ")");
				throw new NotFoundException(report);
			}
		} else if (request.getInfo().equals("request")) {
			ClustalwJobInfo jobInfo = new ClustalwJobInfo(requestId);

			if (jobInfo.existsUserRequestFile()) {
				String userRequestFile = jobInfo.getWorkingDir()
						+ USER_REQUEST_FILE;
				result = new ModelAndView("requestfile", "filename",
						userRequestFile);
			} else {
				ClustalwGetErrorReport report = new ClustalwGetErrorReport(
						request);
				report.put("Message",
						"Unexpected error ( Results of your request id have been NOT FOUND.)");
				System.out.println(report.get("current-time") + "["
						+ getClass() + "#get] request: (" + report + ")");
				throw new NotFoundException(report);
			}
		} else if (request.getInfo().equals("result_stdout")) {
			result = getResultOfQsubOutput(requestId, request, req, true);
		} else if (request.getInfo().equals("result_stderr")) {
			result = getResultOfQsubOutput(requestId, request, req, false);
		}

		return result;

	}

	/**
	 * GETメソッドで呼ばれた時の処理を行う。request-idで表されるJobの現在の状態を返す.
	 * 
	 * @param request
	 *            GET入力データ (パラメータは format, info の 2種)
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/clustalw", method = RequestMethod.GET)
	public ModelAndView getenv(@ModelAttribute WabiGetenvRequest request,
			BindingResult errors, HttpServletRequest req) throws IOException,
			NotFoundException, BadRequestException {

		/*
		 * Note: セキュリティに関わるシステム情報を出力できるので、 DDBJ内部 の開発関係者だけが利用できるようにする。
		 */
		checkRemoteAddr(req, patternGetenvPermittedRemoteAddr);

		// セキュリティのためにリクエスト値をチェックする
		Validator validator = new WabiGetenvRequestValidator();
		validator.validate(request, errors);
		if (errors.hasErrors()) {
			ClustalwGetErrorReport report = new ClustalwGetErrorReport(request);
			System.out.println(report.get("current-time") + "[" + getClass()
					+ "#getenv] validation: (" + errors.getAllErrors() + ")");
			throw new BadRequestException(report);
		}

		ModelAndView result = null;

		if (request.getInfo().equals("env")) {
			LinkedHashMap<String, String> report = new LinkedHashMap<String, String>();
			Bash bash = new Bash();
			BashResult res = bash.system("env | sort");
			report.put("stdout", res.getStdout());
			report.put("stderr", res.getStderr());
			result = new ModelAndView(request.getFormat(), "linked-hash-map",
					report);
		} else {
			ClustalwGetErrorReport report = new ClustalwGetErrorReport(request);
			report.put("Message",
					"Illegal arguments (info = " + request.getInfo() + ")");
			System.out.println(report.get("current-time") + "[" + getClass()
					+ "#getenv] info: (" + report + ")");
			throw new BadRequestException(report);
		}

		return result;

	}

	/**
	 * Help 情報の使い方を返します。 /blast/help/help_command の「help_command」の選択肢を返します。
	 */
	@RequestMapping(value = "/clustalw/help", method = RequestMethod.GET)
	public ModelAndView help(@ModelAttribute WabiGetHelpRequest request,
			BindingResult errors, HttpServletRequest req) {
		return help(null, request, errors, req);
	}

	/**
	 * Help 情報を返します。 例: /blast/help/list_program
	 * 
	 * help_command が省略された場合は、その使い方を返します。 /blast/help/help_command
	 * の「help_command」の選択肢を返します。
	 */
	@RequestMapping(value = "/clustalw/help/{help_command}", method = RequestMethod.GET)
	public ModelAndView help(@PathVariable("help_command") String helpCommand,
			@ModelAttribute WabiGetHelpRequest request, BindingResult errors,
			HttpServletRequest req) {
		request.setHelpCommand(helpCommand);
		if (!"text".equals(request.getFormat())
				&& !"json".equals(request.getFormat())
				&& !"xml".equals(request.getFormat())) {
			request.setFormat("text");
		}

		boolean isPermittedRemoteAddrResultOfQsub = isPermittedRemoteAddr(req,
				patternGetResultOfQsubPermittedRemoteAddr);
		boolean isPermittedRemoteAddrGetenv = isPermittedRemoteAddr(req,
				patternGetenvPermittedRemoteAddr);
		WabiGetHelpReport report = new ClustalwGetHelpReport(request,
				isPermittedRemoteAddrResultOfQsub, isPermittedRemoteAddrGetenv);
		return new ModelAndView(request.getFormat(), "linked-hash-map", report);
	}

	@RequestMapping(value="/clustalw/test/security_application_scan", method=RequestMethod.GET)
	public String showSecurityApplicationScanPage(HttpServletRequest req) throws NotFoundException {
		if (!isPermittedRemoteAddr(req, patternTestSecurityApplicationScanPagePermittedRemoteAddr)) {
			throw new NotFoundException(null);
		}
		return "test.security_application_scan";
	}

	/**
	 * CLUSTALWの計算を行うbash scriptを作成する.
	 * 
	 * <code>
	 * clustalw2 -INFILE=test2.fasta -TYPE=DNA -OUTFILE=outfile [other parameters]
	 * </code>
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@Override
	public String makeShellScript(WabiRequest request, String infile,
			String outfile, WabiJobInfo jobInfo2) throws IOException {
	
		ClustalwJobInfo jobInfo = (ClustalwJobInfo) jobInfo2;

		StringBuffer buf = new StringBuffer();
		String requestId = jobInfo.generateRequestId();

//		buf.append("source /home/geadmin2/UGES/uges/common/settings.sh" + "\n");
		buf.append("WORKINGDIR=" + jobInfo.getWorkingDir() + "\n");
		buf.append("CLUSTALWURL=" + Conf.clustalwUrl + requestId + "\n");
		buf.append("MESSAGE=" + "\"The results can be viewed at:\"" + "\n");
		buf.append("SUBJECT=" + "\"[WABI] " + requestId + " is finished.\""
				+ "\n\n");

//		buf.append("/usr/bin/clustalw");

		//塩基のcustom weight matrixを使えるように修正したスパコン上のclustalw2
//		buf.append("/home/y-okuda/local/bin/clustalw2");

		buf.append("/home/w3wabi/wabi/bin/clustalw2");

		if (jobInfo.existsProfile1() && jobInfo.existsProfile2()) {
			//配列入力ファイルの指定
			buf.append(" -PROFILE1=" + WABI_PROFILE1);
			buf.append(" -PROFILE2=" + WABI_PROFILE2);

			//ガイドツリー入力ファイルの指定
			if (jobInfo.existsGuideTree2()) {
				buf.append(" -USETREE1=" + WABI_GUIDETREE1);
				buf.append(" -USETREE2=" + WABI_GUIDETREE2);

			} else if (jobInfo.existsGuideTree1()) {
				buf.append(" -USETREE=" + WABI_GUIDETREE1);

//			} else {
				//ガイドツリー出力ファイルの指定
//-SEQUENCESで-NEWTREE=を使うとガイドツリー出力で処理が終わるので指定できない。
//				if (request.getParameters().indexOf("-SEQUENCES") != -1) {
//					buf.append(" -NEWTREE=" + WABI_OUT_GUIDETREE1);

// newtree1=, newtree2= がコマンドラインでうまく動かない
//				} else {
//					buf.append(" -NEWTREE1=" + WABI_OUT_GUIDETREE1);
//					buf.append(" -NEWTREE2=" + WABI_OUT_GUIDETREE2);
//				}
			}

			//出力ファイルの指定
			buf.append(" -OUTFILE=" + outfile);

		} else {
			//配列入力ファイルの指定
			buf.append(" -INFILE=" + infile);

			//ガイドツリー入力ファイルの指定
			if (jobInfo.existsGuideTree1()) {
				buf.append(" -USETREE=" + WABI_GUIDETREE1);

				//出力ファイルの指定
				buf.append(" -OUTFILE=" + outfile);

			} else {
				//multiple alignmentの場合のみ、アライメント、ガイドツリー出力ファイルを指定
				if (!request.getParameters().matches(".*-(TREE|BOOTSTRAP|CONVERT).*")) {
					buf.append(" -NEWTREE=" + WABI_OUT_GUIDETREE1);
					buf.append(" -OUTFILE=" + outfile + " -ALIGN");					

				//-CONVERTの場合は出力ファイル名のみ指定
				} else if (request.getParameters().indexOf("-CONVERT") != -1) {
					buf.append(" -OUTFILE=" + outfile);
				}
			}
		}

		//Custom Weight Matrixファイルの指定
		if (jobInfo.existsPwDnaMatrix())
			buf.append(" -PWDNAMATRIX=" + WABI_PW_DNA_MATRIX);
		if (jobInfo.existsPwAaMatrix())
			buf.append(" -PWMATRIX=" + WABI_PW_AA_MATRIX);
		if (jobInfo.existsDnaMatrix())
			buf.append(" -DNAMATRIX=" + WABI_DNA_MATRIX);
		if (jobInfo.existsAaMatrix())
			buf.append(" -MATRIX=" + WABI_AA_MATRIX);

		//その他のパラメータ
		buf.append(" " + request.getParameters());
		
		buf.append(" -STATS=clustalw.log" + "\n");

		// メール送信処理追加
		
		if ("mail".equals(request.getResult())) {
		buf.append("echo -e \"Request ID   " + requestId + "\\n\\n");
		buf.append("\"${MESSAGE[*]}\"" + "\\n"); buf.append("${CLUSTALWURL}" + "\\n\\n");
		buf.append("Results\\n===========\\n");

		// 今のところ、-tree -pim でもWABI_OUT_TREEを返す。
		if (request.getParameters().matches(".*-(TREE).*")) {
			buf.append(" `cat " + WABI_OUT_TREE + "`\" | ");
		} else if (request.getParameters().matches(".*-(BOOTSTRAP).*")) {
			buf.append(" `cat " + WABI_OUT_BTREE + "`\" | ");
		} else {
			buf.append(" `cat " + outfile + "`\" | ");
		}

		buf.append("mail -v -s \"${SUBJECT[*]}\""); buf.append(" " +
		request.getAddress()); buf.append(" >/dev/null 2>&1\n"); }

		buf.append("touch " + FINISHED_FILE);

		return buf.toString() + "\n";
	}
	
	protected ModelAndView getResultOfQsubOutput(String requestId,
			WabiGetRequest request, HttpServletRequest req, boolean isStdout)
			throws IOException, NotFoundException {
		/*
		 * Note: qsub の標準出力にはシステム情報が含まれ得るので、 接続元IPアドレス で拒否します。 システム情報の例:
		 * パスやアカウント名など。
		 */
		checkRemoteAddr(req, patternGetResultOfQsubPermittedRemoteAddr);
		ClustalwJobInfo jobInfo = new ClustalwJobInfo(requestId);
		String jobName = makeJobName(jobInfo);
		String qsubOutFilename = isStdout ? jobInfo
				.getQsubStdoutFilename(jobName) : jobInfo
				.getQsubStderrFilename(jobName);
		if (null == jobName || jobInfo.existsFile(qsubOutFilename)) {
			ClustalwGetErrorReport report = new ClustalwGetErrorReport(request);
			report.put(
					"Message",
					"Error ( "
							+ (isStdout ? "Stdout" : "Stderr")
							+ " of your request id have been NOT FOUND, or still running.)");
			System.out.println(report.get("current-time") + "[" + getClass()
					+ "#get] " + (isStdout ? "result_stdout" : "result_stderr")
					+ ": (" + report + ")");
			throw new NotFoundException(report);
		}
		return new ModelAndView("bigfile", "filename", qsubOutFilename);
	}

}
