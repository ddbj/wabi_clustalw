package jp.ac.nig.ddbj.wabi.controller.vecscreen;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import jp.ac.nig.ddbj.wabi.controller.BadRequestException;
import jp.ac.nig.ddbj.wabi.controller.InternalServerErrorException;
import jp.ac.nig.ddbj.wabi.controller.NotFoundException;
import jp.ac.nig.ddbj.wabi.controller.WabiController;
import jp.ac.nig.ddbj.wabi.job.vecscreen.VecscreenJobInfo;
import jp.ac.nig.ddbj.wabi.job.JobIdNotInitializedException;
import jp.ac.nig.ddbj.wabi.job.WabiJobInfo;
import jp.ac.nig.ddbj.wabi.report.WabiGetErrorReport;
import jp.ac.nig.ddbj.wabi.report.WabiGetHelpReport;
import jp.ac.nig.ddbj.wabi.report.vecscreen.VecscreenErrorReport;
import jp.ac.nig.ddbj.wabi.report.vecscreen.VecscreenGetErrorReport;
import jp.ac.nig.ddbj.wabi.report.vecscreen.VecscreenGetHelpReport;
import jp.ac.nig.ddbj.wabi.report.vecscreen.VecscreenGetReportOfStatus;
import jp.ac.nig.ddbj.wabi.report.vecscreen.VecscreenIllegalRequestReport;
import jp.ac.nig.ddbj.wabi.report.vecscreen.VecscreenPostReport;
import jp.ac.nig.ddbj.wabi.request.WabiGetHelpRequest;
import jp.ac.nig.ddbj.wabi.request.WabiGetRequest;
import jp.ac.nig.ddbj.wabi.request.WabiGetenvRequest;
import jp.ac.nig.ddbj.wabi.request.WabiRequest;
import jp.ac.nig.ddbj.wabi.request.vecscreen.VecscreenRequest;
import jp.ac.nig.ddbj.wabi.util.Conf;
import jp.ac.nig.ddbj.wabi.util.ConfVecscreen;
import jp.ac.nig.ddbj.wabi.validator.WabiGetenvRequestValidator;
import jp.ac.nig.ddbj.wabi.validator.vecscreen.VecscreenGetRequestValidator;
import jp.ac.nig.ddbj.wabi.validator.vecscreen.VecscreenPostRequestValidator;
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
public class VecscreenController extends WabiController {

	public static String outfilePrefix = "wabi_vecscreen_";
	
	public static Pattern patternGetenvPermittedRemoteAddr = Pattern.compile(ConfVecscreen.patternGetenvPermittedRemoteAddr);
	public static Pattern patternGetResultOfQsubPermittedRemoteAddr = Pattern.compile(ConfVecscreen.patternGetResultOfQsubPermittedRemoteAddr);
	public static Pattern patternGetStatusOfQsubPermittedRemoteAddr = Pattern.compile(ConfVecscreen.patternGetStatusOfQsubPermittedRemoteAddr);
	public static Pattern patternTestSecurityApplicationScanPagePermittedRemoteAddr = Pattern.compile(ConfVecscreen.patternTestSecurityApplicationScanPagePermittedRemoteAddr);

	@Inject
	MessageSource messageSource;

	RNG engine = null;

	@Inject
	public VecscreenController(RNG engine) {
		super(engine);
	}

	@Override
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		/*
		 * Note: ???????????????
		 * ???????????????????????????????????????????????? HTTP 500 ????????????????????????????????????
		 * ????????????????????????????????????????????????????????????????????????????????????????????????
		 */
		binder.setAllowedFields("querySequence", "database", "parameters", "format", "result", "address",
								"format", "info",
								"requestId", "format", "info",
								"helpCommand", "format");
	}
	
	/**
	 * HTTP POST???????????????????????????????????????????????????Job(wabi)???????????????????????????Job(wabi)???ID
	 * (request-id?????????????????????????????????.
	 * 
	 * @param request
	 * @return
	 */
	@Override
	@RequestMapping(value = "/vecscreen_dummy", method = RequestMethod.POST)
	public ModelAndView post(@ModelAttribute WabiRequest request,
			BindingResult errors) throws BadRequestException,
			InternalServerErrorException {
		ModelAndView result = null;
		return result;
	}
	
	
	@RequestMapping(value = "/vecscreen", method = RequestMethod.POST)
	public ModelAndView post(@ModelAttribute VecscreenRequest request,
			BindingResult errors) throws BadRequestException,
			InternalServerErrorException {

		ModelAndView result = null;

		try {
			// ??????????????????????????????request-id??????????????????
			VecscreenJobInfo jobInfo = new VecscreenJobInfo(engine);
			jobInfo.generateRequestId();

			// ??????Job(wabi)????????????????????????????????????????????????????????????
			jobInfo.makeWorkingDir();

			// Request(user), Query(fasta)????????????????????????????????????????????????????????????????????????
			jobInfo.save(USER_REQUEST_FILE, request.toJsonStr());
			jobInfo.save(QUERY_SEQUENCE_FILE, request.getQuerySequence());

			// ?????????????????????????????????????????????????????????????????????
			Validator validator = new VecscreenPostRequestValidator();
			validator.validate(request, errors);
			if (errors.hasErrors()) {
				VecscreenIllegalRequestReport report = new VecscreenIllegalRequestReport(
						request);
				jobInfo.save(ILLEGAL_ARGUMENTS_FILE, errors.getAllErrors()
						.toString());
				throw new BadRequestException(report);
			}

			// clustalw???????????????????????????????????????????????????
			jobInfo.save(
					SHELL_SCRIPT_FILE,
					makeShellScript(request, QUERY_SEQUENCE_FILE,
							WABI_OUT_FILE, jobInfo));

			// qsub???????????????????????????UGE???job(UGE)?????????
			String jobName = "vecscreen";
			String jobId = jobInfo.qsub(jobName, SHELL_SCRIPT_FILE);

			// jobId, jobInfo????????????????????????
			jobInfo.save(UGE_JOB_ID_FILE, jobId + "\n"); // UGE???job-id.
			jobInfo.save(JOB_INFO_FILE, jobInfo.getInfoAsJson(true));

			// ??????????????????????????????????????????????????????
			VecscreenPostReport report = new VecscreenPostReport(jobInfo, request);
			result = new ModelAndView(request.getFormat(), "linked-hash-map",
					report);
		} catch (IOException e) {
			e.printStackTrace();
			LinkedHashMap<String, Object> report = null;
			try {
				report = new VecscreenErrorReport(request);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new InternalServerErrorException(report);
		}

		return result;
		
	}
	
	/**
	 * GET???????????????????????????????????????????????????request-id???????????????Job???????????????????????????.
	 * 
	 * @param requestId
	 *            job????????????????????????ID????????????
	 * @param request
	 *            GET??????????????? (?????????????????? format, imageId, info ??? 3???)
	 * @param req
	 *            HTTP???????????????
	 * @param errors
	 *            ??????????????? (????????????????????????????????????????????????????????????????????????????????????)CLUSTALW
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/vecscreen/{id}", method = RequestMethod.GET)
	public ModelAndView get(@PathVariable("id") String requestId,
			@ModelAttribute WabiGetRequest request, BindingResult errors,
			HttpServletRequest req) throws IOException, BadRequestException,
			NotFoundException {

		request.setRequestId(requestId);

		ModelAndView result = null;

		// ?????????????????????????????????????????????????????????????????????
		Validator validator = new VecscreenGetRequestValidator();
		validator.validate(request, errors);
		if (errors.hasErrors()) {
			VecscreenIllegalRequestReport report = new VecscreenIllegalRequestReport(
					request);
			System.out.println(report.get("current-time") + "[" + getClass()
					+ "#get] validation: (" + errors.getAllErrors() + ")");
			throw new BadRequestException(report);
		}

		if (request.getInfo().equals("status")) {
			try {
				VecscreenGetReportOfStatus report = new VecscreenGetReportOfStatus(
						requestId, isPermittedRemoteAddr(req,
								patternGetStatusOfQsubPermittedRemoteAddr));
				result = new ModelAndView(request.getFormat(),
						"linked-hash-map", report);
			} catch (JobIdNotInitializedException e) {
				LinkedHashMap<String, Object> report = new VecscreenGetErrorReport(
						request);
				report.put("Message", "Error (" + e + ")");
				System.out.println(report.get("current-time") + "["
						+ getClass() + "#get] status: (" + report + ")");
				throw new BadRequestException(report);
			}
		} else if (request.getInfo().equals("result")) {
			VecscreenJobInfo jobInfo = new VecscreenJobInfo(requestId);
			//multiple alignment
			if (jobInfo.existsOutFile()) {
				result = new ModelAndView("bigfile", "filename",
						jobInfo.getWorkingDir() + WABI_OUT_FILE);
			} else {
				VecscreenGetErrorReport report = new VecscreenGetErrorReport(
						request);
				report.put("Message",
						"Error ( Results of your request id have been NOT FOUND, or still running.)");
				System.out.println(report.get("current-time") + "["
						+ getClass() + "#get] result: (" + report + ")");
				throw new NotFoundException(report);
			}
		} else if (request.getInfo().equals("request")) {
			VecscreenJobInfo jobInfo = new VecscreenJobInfo(requestId);

			if (jobInfo.existsUserRequestFile()) {
				String userRequestFile = jobInfo.getWorkingDir()
						+ USER_REQUEST_FILE;
				result = new ModelAndView("requestfile", "filename",
						userRequestFile);
			} else {
				VecscreenGetErrorReport report = new VecscreenGetErrorReport(
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
	 * GET???????????????????????????????????????????????????request-id???????????????Job???????????????????????????.
	 * 
	 * @param request
	 *            GET??????????????? (?????????????????? format, info ??? 2???)
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/vecscreen", method = RequestMethod.GET)
	public ModelAndView getenv(@ModelAttribute WabiGetenvRequest request,
			BindingResult errors, HttpServletRequest req) throws IOException,
			NotFoundException, BadRequestException {

		/*
		 * Note: ??????????????????????????????????????????????????????????????????????????? DDBJ?????? ????????????????????????????????????????????????????????????
		 */
		checkRemoteAddr(req, patternGetenvPermittedRemoteAddr);

		// ?????????????????????????????????????????????????????????????????????
		Validator validator = new WabiGetenvRequestValidator();
		validator.validate(request, errors);
		if (errors.hasErrors()) {
			VecscreenGetErrorReport report = new VecscreenGetErrorReport(request);
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
			VecscreenGetErrorReport report = new VecscreenGetErrorReport(request);
			report.put("Message",
					"Illegal arguments (info = " + request.getInfo() + ")");
			System.out.println(report.get("current-time") + "[" + getClass()
					+ "#getenv] info: (" + report + ")");
			throw new BadRequestException(report);
		}

		return result;

	}

	/**
	 * Help ???????????????????????????????????? /blast/help/help_command ??????help_command?????????????????????????????????
	 */
	@RequestMapping(value = "/vecscreen/help", method = RequestMethod.GET)
	public ModelAndView help(@ModelAttribute WabiGetHelpRequest request,
			BindingResult errors, HttpServletRequest req) {
		return help(null, request, errors, req);
	}

	/**
	 * Help ????????????????????????
     * ???: /blast/help/list_program
	 * 
	 * help_command ??????????????????????????????????????????????????????????????? /blast/help/help_command
	 * ??????help_command?????????????????????????????????
	 */
	@RequestMapping(value = "/vecscreen/help/{help_command}", method = RequestMethod.GET)
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
		WabiGetHelpReport report = new VecscreenGetHelpReport(request,
				isPermittedRemoteAddrResultOfQsub, isPermittedRemoteAddrGetenv);
		return new ModelAndView(request.getFormat(), "linked-hash-map", report);
	}

	/**
	 * CLUSTALW??????????????????bash script???????????????.
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
			String outfile, WabiJobInfo jobInfo) throws IOException {

		StringBuffer buf = new StringBuffer();
		String requestId = jobInfo.generateRequestId();

		buf.append("export BLASTDB=" + Conf.vecscreenDbPath + "\n");
		buf.append("WORKINGDIR=" + jobInfo.getWorkingDir() + "\n");
		buf.append("VECSCREENURL=" + Conf.vecscreenUrl + requestId + "\n");
		buf.append("MESSAGE=" + "\"The results can be viewed at:\"" + "\n");
		buf.append("SUBJECT=" + "\"[WABI] " + requestId + " is finished.\""
				+ "\n\n");

		buf.append("module load singularity" + "\n");
		buf.append("singularity exec /home/w3wabi/singularity_images/ncbi_cxx_toolkit/ncbi_cxx_toolkit.simg vecscreen");
//		buf.append("vecscreen");
		buf.append(" -db " + request.getDatabase());	//UniVec or UniVec_Core
//		buf.append(" -d " + request.getDatabase());	//UniVec or UniVec_Core
		buf.append(" -query " + infile);
//		buf.append(" -i " + infile);
		buf.append(" -out " + outfile);
//		buf.append(" -o " + outfile);
		String parameters = request.getParameters();
		if (parameters.equals("-f 0")) {
			buf.append(" -outfmt 0" + "\n");
		} else if (parameters.equals("-f 1")) {
			buf.append(" -outfmt 1" + "\n");
		} else if (parameters.equals("-f 2")) {
			buf.append(" -outfmt 0 -text_output" + "\n");
		} else if (parameters.equals("-f 3")) {
			buf.append(" -outfmt 1 -text_output" + "\n");
		} else {
			buf.append("\n");
		}
//		buf.append(" " + request.getParameters() + "\n");	//?????????????????????-f [0-3]??????

		buf.append("sed -i -e 's/<table border=\\\"0\\\" cellpadding=\\\"0\\\" cellspacing=\\\"0\\\" width=\\\"750\\\">/<table style=\"width:750px !important;border-width:0 !important;\">/g' wabi_result.out" + "\n");
		buf.append("sed -i -e 's/<td align=\\\"LEFT\\\" valign=\\\"CENTER\\\" width=\\\"150\\\">/<td style=\"width:150px !important;border-width:0 !important;padding:0 !important\">/g' wabi_result.out" + "\n");
		buf.append("sed -i -e 's/<td align=\\\"LEFT\\\" valign=\\\"CENTER\\\">/<td style=\"border-width:0 !important;padding:0 !important\">/g' wabi_result.out" + "\n");
		buf.append("sed -i -e 's/<img border=\\\"1\\\" height=\\\"20\\\" src=\\\".\\/red.gif\\\" width=\\\"20\\\" \\/>/<img src=\".\\/red.gif\" style=\"width:20px !important;height:20px !important;border:solid 1px black;\" \\/>/g' wabi_result.out" + "\n");
		buf.append("sed -i -e 's/<img border=\\\"1\\\" height=\\\"20\\\" src=\\\".\\/purple.gif\\\" width=\\\"20\\\" \\/>/<img src=\".\\/purple.gif\" style=\"width:20px !important;height:20px !important;border:solid 1px black !important;\" \\/>/g' wabi_result.out" + "\n");
		buf.append("sed -i -e 's/<img border=\\\"1\\\" height=\\\"20\\\" src=\\\".\\/green.gif\\\" width=\\\"20\\\" \\/>/<img src=\".\\/green.gif\" style=\"width:20px !important;height:20px !important;border:solid 1px black !important;\" \\/>/g' wabi_result.out" + "\n");
		buf.append("sed -i -e 's/<img border=\\\"1\\\" height=\\\"20\\\" src=\\\".\\/yellow.gif\\\" width=\\\"20\\\" \\/>/<img src=\".\\/yellow.gif\" style=\"width:20px !important;height:20px !important;border:solid 1px black !important;\" \\/>/g' wabi_result.out" + "\n");
		buf.append("sed -i -e 's/<img border=\"1\" height=\"20\" src=\"\\.\\/red.gif\" width=\"\\([0-9]\\+\\)\" \\/>/<img src=\".\\/red.gif\" style=\"width:\\1px !important;height:20px !important;border:solid 1px black !important;\" \\/>/g' wabi_result.out" + "\n");
		buf.append("sed -i -e 's/<img border=\"1\" height=\"20\" src=\"\\.\\/purple.gif\" width=\"\\([0-9]\\+\\)\" \\/>/<img src=\".\\/purple.gif\" style=\"width:\\1px !important;height:20px !important;border:solid 1px black !important;\" \\/>/g' wabi_result.out" + "\n");
		buf.append("sed -i -e 's/<img border=\"1\" height=\"20\" src=\"\\.\\/green.gif\" width=\"\\([0-9]\\+\\)\" \\/>/<img src=\".\\/green.gif\" style=\"width:\\1px !important;height:20px !important;border:solid 1px black !important;\" \\/>/g' wabi_result.out" + "\n");
		buf.append("sed -i -e 's/<img border=\"1\" height=\"20\" src=\"\\.\\/yellow.gif\" width=\"\\([0-9]\\+\\)\" \\/>/<img src=\".\\/yellow.gif\" style=\"width:\\1px !important;height:20px !important;border:solid 1px black !important;\" \\/>/g' wabi_result.out" + "\n");
		buf.append("sed -i -e 's/<img border=\"1\" height=\"20\" src=\"\\.\\/white.gif\" width=\"\\([0-9]\\+\\)\" \\/>/<img src=\".\\/white.gif\" style=\"width:\\1px !important;height:20px !important;border:solid 1px black !important;\" \\/>/g' wabi_result.out" + "\n");
		buf.append("sed -i -e 's/\\.\\/red\\.gif/resources\\/images\\/red.gif/g' wabi_result.out" + "\n");
		buf.append("sed -i -e 's/\\.\\/purple\\.gif/resources\\/images\\/purple.gif/g' wabi_result.out" + "\n");
		buf.append("sed -i -e 's/\\.\\/green\\.gif/resources\\/images\\/green.gif/g' wabi_result.out" + "\n");
		buf.append("sed -i -e 's/\\.\\/yellow\\.gif/resources\\/images\\/yellow.gif/g' wabi_result.out" + "\n");
		buf.append("sed -i -e 's/\\.\\/white\\.gif/resources\\/images\\/white.gif/g' wabi_result.out" + "\n");

		// ???????????????????????????
		if ("mail".equals(request.getResult())) {
		buf.append("echo -e \"Request ID   " + requestId + "\\n\\n");
		buf.append("\"${MESSAGE[*]}\"" + "\\n"); buf.append("${VECSCREENURL}" +
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
		 * Note: qsub ?????????????????????????????????????????????????????????????????? ?????????IP???????????? ????????????????????? ????????????????????????:
		 * ????????????????????????????????????
		 */
		checkRemoteAddr(req, patternGetResultOfQsubPermittedRemoteAddr);
		WabiJobInfo jobInfo = new VecscreenJobInfo(requestId);
		String jobName = makeJobName(jobInfo);
		String qsubOutFilename = isStdout ? jobInfo
				.getQsubStdoutFilename(jobName) : jobInfo
				.getQsubStderrFilename(jobName);
		if (null == jobName || jobInfo.existsFile(qsubOutFilename)) {
			WabiGetErrorReport report = new VecscreenGetErrorReport(request);
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
