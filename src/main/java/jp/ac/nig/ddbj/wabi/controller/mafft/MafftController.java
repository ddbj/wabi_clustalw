package jp.ac.nig.ddbj.wabi.controller.mafft;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import jp.ac.nig.ddbj.wabi.controller.BadRequestException;
import jp.ac.nig.ddbj.wabi.controller.InternalServerErrorException;
import jp.ac.nig.ddbj.wabi.controller.NotFoundException;
import jp.ac.nig.ddbj.wabi.controller.WabiController;
import jp.ac.nig.ddbj.wabi.job.mafft.MafftJobInfo;
import jp.ac.nig.ddbj.wabi.job.JobIdNotInitializedException;
import jp.ac.nig.ddbj.wabi.job.WabiJobInfo;
import jp.ac.nig.ddbj.wabi.report.WabiGetHelpReport;
import jp.ac.nig.ddbj.wabi.report.mafft.MafftErrorReport;
import jp.ac.nig.ddbj.wabi.report.mafft.MafftGetErrorReport;
import jp.ac.nig.ddbj.wabi.report.mafft.MafftGetHelpReport;
import jp.ac.nig.ddbj.wabi.report.mafft.MafftGetReportOfStatus;
import jp.ac.nig.ddbj.wabi.report.mafft.MafftIllegalRequestReport;
import jp.ac.nig.ddbj.wabi.report.mafft.MafftPostReport;
import jp.ac.nig.ddbj.wabi.request.WabiGetHelpRequest;
import jp.ac.nig.ddbj.wabi.request.WabiGetRequest;
import jp.ac.nig.ddbj.wabi.request.WabiGetenvRequest;
import jp.ac.nig.ddbj.wabi.request.WabiRequest;
import jp.ac.nig.ddbj.wabi.request.mafft.MafftRequest;
import jp.ac.nig.ddbj.wabi.util.Conf;
import jp.ac.nig.ddbj.wabi.util.ConfMafft;
import jp.ac.nig.ddbj.wabi.validator.WabiGetenvRequestValidator;
import jp.ac.nig.ddbj.wabi.validator.mafft.MafftGetRequestValidator;
import jp.ac.nig.ddbj.wabi.validator.mafft.MafftPostRequestValidator;
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
public class MafftController extends WabiController {

	public static final String WABI_PROFILE1          = "profile1";
	public static final String WABI_PROFILE2          = "profile2";
	public static final String WABI_AA_MATRIX         = "aa_weight_matrix.txt";
	
	// --addオプションに対応（2016/07/01）
	public static final String WABI_ADD_SEQUENCE          = "add_sequences";
	public static final String WABI_ADDFRAGMENTS_SEQUENCE = "addfragment_sequences";
	public static final String WABI_ADDPROFILE_PROFILE    = "addprofile_profile";
	public static final String WABI_ADDFULL_SEQUENCE      = "addfull_sequences";

	public static String outfilePrefix = "wabi_mafft_";

	public static Pattern patternGetenvPermittedRemoteAddr = Pattern.compile(ConfMafft.patternGetenvPermittedRemoteAddr);
	public static Pattern patternGetResultOfQsubPermittedRemoteAddr = Pattern.compile(ConfMafft.patternGetResultOfQsubPermittedRemoteAddr);
	public static Pattern patternGetStatusOfQsubPermittedRemoteAddr = Pattern.compile(ConfMafft.patternGetStatusOfQsubPermittedRemoteAddr);
	public static Pattern patternTestSecurityApplicationScanPagePermittedRemoteAddr = Pattern.compile(ConfMafft.patternTestSecurityApplicationScanPagePermittedRemoteAddr);

	@Inject
	MessageSource messageSource;

	RNG engine = null;

	@Inject
	public MafftController(RNG engine) {
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
		binder.setAllowedFields("querySequence", "profile1", "profile2", "aaMatrix", "parameters", "format", "result", "address",
				                "addSequence", "addfragmentsSequence", "addprofileProfile", "addfullSequence",
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
	@RequestMapping(value = "/mafft_dummy", method = RequestMethod.POST)
	public ModelAndView post(@ModelAttribute WabiRequest request,
			BindingResult errors) throws BadRequestException,
			InternalServerErrorException {
		ModelAndView result = null;
		return result;
	}
	
	
	@RequestMapping(value = "/mafft", method = RequestMethod.POST)
	public ModelAndView post(@ModelAttribute MafftRequest request,
			BindingResult errors) throws BadRequestException,
			InternalServerErrorException {

		ModelAndView result = null;

		try {
			// 現在時刻を使って新規request-idを作成する。
			MafftJobInfo jobInfo = new MafftJobInfo(engine);
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
			if (request.getAaMatrix() != null && !request.getAaMatrix().isEmpty())
				jobInfo.save(WABI_AA_MATRIX, request.getAaMatrix());

			// --addオプションに対応（2016/07/01）
			if (request.getAddSequence() != null && !request.getAddSequence().isEmpty())
				jobInfo.save(WABI_ADD_SEQUENCE, request.getAddSequence());
			if (request.getAddfragmentsSequence() != null && !request.getAddfragmentsSequence().isEmpty())
				jobInfo.save(WABI_ADDFRAGMENTS_SEQUENCE, request.getAddfragmentsSequence());
			if (request.getAddprofileProfile() != null && !request.getAddprofileProfile().isEmpty())
				jobInfo.save(WABI_ADDPROFILE_PROFILE, request.getAddprofileProfile());
			if (request.getAddfullSequence() != null && !request.getAddfullSequence().isEmpty())
				jobInfo.save(WABI_ADDFULL_SEQUENCE, request.getAddfullSequence());

			// セキュリティのためにリクエスト値をチェックする
			Validator validator = new MafftPostRequestValidator();
			validator.validate(request, errors);
			if (errors.hasErrors()) {
				MafftIllegalRequestReport report = new MafftIllegalRequestReport(
						request);
				jobInfo.save(ILLEGAL_ARGUMENTS_FILE, errors.getAllErrors()
						.toString());
				throw new BadRequestException(report);
			}

			// mafft実行用シェルスクリプトファイル作成
			jobInfo.save(
					SHELL_SCRIPT_FILE,
					makeShellScript(request, QUERY_SEQUENCE_FILE,
							WABI_OUT_FILE, jobInfo));

			// qsubコマンドを発行してUGEにjob(UGE)を投入
			String jobName = "mafft";
			String jobId = jobInfo.qsub(jobName, SHELL_SCRIPT_FILE);

			// jobId, jobInfoをファイルに保存
			jobInfo.save(UGE_JOB_ID_FILE, jobId + "\n"); // UGEのjob-id.
			jobInfo.save(JOB_INFO_FILE, jobInfo.getInfoAsJson(true));

			// 新規作成ジョブに関するレポートを返す
			MafftPostReport report = new MafftPostReport(jobInfo, request);
			result = new ModelAndView(request.getFormat(), "linked-hash-map",
					report);
		} catch (IOException e) {
			e.printStackTrace();
			LinkedHashMap<String, Object> report = null;
			try {
				report = new MafftErrorReport(request);
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
	@RequestMapping(value = "/mafft/{id}", method = RequestMethod.GET)
	public ModelAndView get(@PathVariable("id") String requestId,
			@ModelAttribute WabiGetRequest request, BindingResult errors,
			HttpServletRequest req) throws IOException, BadRequestException,
			NotFoundException {

		request.setRequestId(requestId);

		ModelAndView result = null;

		// セキュリティのためにリクエスト値をチェックする
		Validator validator = new MafftGetRequestValidator();
		validator.validate(request, errors);
		if (errors.hasErrors()) {
			MafftIllegalRequestReport report = new MafftIllegalRequestReport(
					request);
			System.out.println(report.get("current-time") + "[" + getClass()
					+ "#get] validation: (" + errors.getAllErrors() + ")");
			throw new BadRequestException(report);
		}

		if (request.getInfo().equals("status")) {
			try {
				MafftGetReportOfStatus report = new MafftGetReportOfStatus(
						requestId, isPermittedRemoteAddr(req,
								patternGetStatusOfQsubPermittedRemoteAddr));
				result = new ModelAndView(request.getFormat(),
						"linked-hash-map", report);
			} catch (JobIdNotInitializedException e) {
				LinkedHashMap<String, Object> report = new MafftGetErrorReport(
						request);
				report.put("Message", "Error (" + e + ")");
				System.out.println(report.get("current-time") + "["
						+ getClass() + "#get] status: (" + report + ")");
				throw new BadRequestException(report);
			}
		} else if (request.getInfo().equals("result")) {
			MafftJobInfo jobInfo = new MafftJobInfo(requestId);
			if (jobInfo.existsOutFile()) {
				result = new ModelAndView("bigfile", "filename",
						jobInfo.getWorkingDir() + WABI_OUT_FILE);
			} else {
				MafftGetErrorReport report = new MafftGetErrorReport(
						request);
				report.put("Message",
						"Error ( Results of your request id have been NOT FOUND, or still running.)");
				System.out.println(report.get("current-time") + "["
						+ getClass() + "#get] result: (" + report + ")");
				throw new NotFoundException(report);
			}
		} else if (request.getInfo().equals("request")) {
			MafftJobInfo jobInfo = new MafftJobInfo(requestId);

			if (jobInfo.existsUserRequestFile()) {
				String userRequestFile = jobInfo.getWorkingDir()
						+ USER_REQUEST_FILE;
				result = new ModelAndView("requestfile", "filename",
						userRequestFile);
			} else {
				MafftGetErrorReport report = new MafftGetErrorReport(
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
	@RequestMapping(value = "/mafft", method = RequestMethod.GET)
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
			MafftGetErrorReport report = new MafftGetErrorReport(request);
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
			MafftGetErrorReport report = new MafftGetErrorReport(request);
			report.put("Message",
					"Illegal arguments (info = " + request.getInfo() + ")");
			System.out.println(report.get("current-time") + "[" + getClass()
					+ "#getenv] info: (" + report + ")");
			throw new BadRequestException(report);
		}

		return result;

	}

	/**
	 * Help 情報の使い方を返します。 /mafft/help/help_command の「help_command」の選択肢を返します。
	 */
	@RequestMapping(value = "/mafft/help", method = RequestMethod.GET)
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
	@RequestMapping(value = "/mafft/help/{help_command}", method = RequestMethod.GET)
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
		WabiGetHelpReport report = new MafftGetHelpReport(request,
				isPermittedRemoteAddrResultOfQsub, isPermittedRemoteAddrGetenv);
		return new ModelAndView(request.getFormat(), "linked-hash-map", report);
	}

	@RequestMapping(value="/mafft/test/security_application_scan", method=RequestMethod.GET)
	public String showSecurityApplicationScanPage(HttpServletRequest req) throws NotFoundException {
		if (!isPermittedRemoteAddr(req, patternTestSecurityApplicationScanPagePermittedRemoteAddr)) {
			throw new NotFoundException(null);
		}
		return "test.security_application_scan";
	}

	/**
	 * MAFFTの計算を行うbash scriptを作成する.
	 * 
	 * <code>
	 * mafft [other parameters] query_sequence.txt > outfile
	 * </code>
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@Override
	public String makeShellScript(WabiRequest request, String infile,
			String outfile, WabiJobInfo jobInfo2) throws IOException {

		MafftJobInfo jobInfo = (MafftJobInfo) jobInfo2;

		StringBuffer buf = new StringBuffer();
		String requestId = jobInfo.generateRequestId();

		buf.append("WORKINGDIR=" + jobInfo.getWorkingDir() + "\n");
//		buf.append("MAFFTURL=" + Conf.clustalwUrl + requestId + "\n");
		buf.append("MESSAGE=" + "\"The results can be viewed at:\"" + "\n");
		buf.append("SUBJECT=" + "\"[WABI] " + requestId + " is finished.\""
				+ "\n\n");

		buf.append("module load singularity" + "\n");
		buf.append("singularity exec /lustre7/singularity/images/biotools_20190704/m/mafft:7.310--0 mafft");
//		buf.append("mafft");

		buf.append(" " + request.getParameters());

		if (jobInfo.existsProfile1() && jobInfo.existsProfile2()) {
			if (jobInfo.existsAaMatrix()) {
				buf.append(" --aamatrix " + WABI_AA_MATRIX);
			}
			
			//配列入力ファイルの指定
			buf.append(" --seed " + WABI_PROFILE1);
			buf.append(" --seed " + WABI_PROFILE2 + " /dev/null");

		// --addオプションに対応（2016/07/01）
		} else if (jobInfo.existsAddSequence()) {
			if (jobInfo.existsAaMatrix()) {
				buf.append(" --aamatrix " + WABI_AA_MATRIX);
			}
			buf.append(" --add " + WABI_ADD_SEQUENCE);
			buf.append(" " + infile);
		} else if (jobInfo.existsAddfragmentsSequence()) {
			if (jobInfo.existsAaMatrix()) {
				buf.append(" --aamatrix " + WABI_AA_MATRIX);
			}
			buf.append(" --addfragments " + WABI_ADDFRAGMENTS_SEQUENCE);
			buf.append(" " + infile);
		} else if (jobInfo.existsAddprofileProfile()) {
			if (jobInfo.existsAaMatrix()) {
				buf.append(" --aamatrix " + WABI_AA_MATRIX);
			}
			buf.append(" --addprofile " + WABI_ADDPROFILE_PROFILE);
			buf.append(" " + infile);
		} else if (jobInfo.existsAddfullSequence()) {
			if (jobInfo.existsAaMatrix()) {
				buf.append(" --aamatrix " + WABI_AA_MATRIX);
			}
			buf.append(" --addfull " + WABI_ADDFULL_SEQUENCE);
			buf.append(" " + infile);
		} else {
			if (jobInfo.existsAaMatrix()) {
				buf.append(" --aamatrix " + WABI_AA_MATRIX);
			}
			buf.append(" " + infile);
		}


		buf.append(" > " + outfile + "\n");

		// メール送信処理追加
		if ("mail".equals(request.getResult())) {
		buf.append("echo -e \"Request ID   " + requestId + "\\n\\n");
		buf.append("\"${MESSAGE[*]}\"" + "\\n"); buf.append("${MAFFTURL}" +
		"\\n\\n"); buf.append("Results\\n===========\\n");
		buf.append(" `cat " + outfile + "`\" | ");
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
		MafftJobInfo jobInfo = new MafftJobInfo(requestId);
		String jobName = makeJobName(jobInfo);
		String qsubOutFilename = isStdout ? jobInfo
				.getQsubStdoutFilename(jobName) : jobInfo
				.getQsubStderrFilename(jobName);
		if (null == jobName || jobInfo.existsFile(qsubOutFilename)) {
			MafftGetErrorReport report = new MafftGetErrorReport(request);
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
