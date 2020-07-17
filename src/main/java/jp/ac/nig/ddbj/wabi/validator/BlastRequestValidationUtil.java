package jp.ac.nig.ddbj.wabi.validator;

import java.io.File;
import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.ac.nig.ddbj.wabi.job.JobInfo;
import jp.ac.nig.ddbj.wabi.request.BlastRequest;
import jp.ac.nig.ddbj.wabi.util.ConfBlast;

import org.springframework.validation.Errors;

/**
 * リクエストされた BLAST入力データ の妥当性を検証します.
 */
public class BlastRequestValidationUtil {
	/*
	 * Note: patternParameters等 は、入力値が
	 * セキュリティ上の問題を起こす可能性があるか判定するための
	 * ホワイトリストです。
	 * 問題を起こし得る入力値の例として、
	 * 「foo; bar | baz」のような「;」、「|」等の文字を含むものの他に、
	 * 「%」や「\」などの HTTP上 のエンコーディングに関係する文字などが
	 * 考えられます。
	 */
	private static Pattern patternDatasets = Pattern.compile(ConfBlast.RequestValidationPattern.datasets);
	private static Pattern patternDatabase = Pattern.compile(ConfBlast.RequestValidationPattern.database);
	private static Pattern patternProgram = Pattern.compile(ConfBlast.RequestValidationPattern.program);
	private static Pattern patternFormat = Pattern.compile(ConfBlast.RequestValidationPattern.format);
	private static Pattern patternParameters = Pattern.compile(ConfBlast.RequestValidationPattern.parameters);
	private static Pattern patternParameters_each = Pattern.compile(ConfBlast.RequestValidationPattern.parameters_eachParameter);
	private static Pattern patternResult = Pattern.compile(ConfBlast.RequestValidationPattern.result);
	private static Pattern patternAddress = Pattern.compile(ConfBlast.RequestValidationPattern.address);
	private static Pattern patternAddress_localpart = Pattern.compile(ConfBlast.RequestValidationPattern.address_localpart);
	private static Pattern patternAddress_domainpart = Pattern.compile(ConfBlast.RequestValidationPattern.address_domainpart);
	private static Pattern patternRequestId = Pattern.compile(ConfBlast.RequestValidationPattern.requestId);
	private static Pattern patternImageId = Pattern.compile(ConfBlast.RequestValidationPattern.imageId);
	private static Pattern patternInfo = Pattern.compile(ConfBlast.RequestValidationPattern.info);
	private static Pattern patternInfoEnv = Pattern.compile(ConfBlast.RequestValidationPattern.infoEnv);

	/*
	 * Note: 無限ループ抑止のため、字句数に上限あり。
	 */
	private static final int RECURSIVE_LIMIT = 1000;


	static void validateQuerySequence(String querySequence, Errors errors) {
		if (null==querySequence || querySequence.isEmpty()) {
			errors.rejectValue("querySequence", "error.illegal_arguments.required");
		}
		/*
		 * Note: 実装なし。
		 * この querySequence値 はファイルに保存されて、
		 * そのファイル名を BLASTコマンド に与えているだけなので、
		 * OSコマンド・インジェクションのようなセキュリティ問題には
		 * 関係してこない。
		 * 値に不備がある場合は BLAST側 でエラーにする。
		 */
	}


	static void validateDatasets(String datasets, Errors errors) {
		if (null==datasets || datasets.isEmpty()) {
			return;
		} else if (!patternDatasets.matcher(datasets).matches()) {
			errors.rejectValue("datasets", "error.illegal_arguments.unknown_values");
		}
	}


	static void validateDatabase(String database, Errors errors) {
		if (null==database || database.isEmpty()) {
			errors.rejectValue("database", "error.illegal_arguments.required");
		} else if (!patternDatabase.matcher(database).matches()) {
			errors.rejectValue("database", "error.illegal_arguments.invalid_values");
		}
	}


	static void validateProgram(String program, Errors errors) {
		if (null==program || program.isEmpty()) {
			errors.rejectValue("program", "error.illegal_arguments.required");
		} else if (!patternProgram.matcher(program).matches()) {
			errors.rejectValue("program", "error.illegal_arguments.unknown_values");
		}
	}


	static void validateFormat(String format, Errors errors) {
		if (null==format || format.isEmpty()) {
			errors.rejectValue("format", "error.illegal_arguments.required");
		} else if (!patternFormat.matcher(format).matches()) {
			errors.rejectValue("format", "error.illegal_arguments.unknown_values");
		}
	}


	/**
	 * parameters値 を検証して、不備があった場合は errors に追加します。
	 *
	 * @param parameters parameters値
	 * @param acceptOptions BLAST入力データ の parameters値 として受け付け可能なオプションです。
	 *     (例: blastall の場合は "ABCDEFGIJKLMPQSTUVWXYZabdefglmnqrstvwyz" 等。)
	 * @param errors 検証で不備を見つけた場合に追加します。
	 */
	static void validateParameters(String parameters, String acceptOptions, Errors errors) {
		if (null==parameters || parameters.isEmpty()) {
			return;
		} else if (!patternParameters.matcher(parameters).matches()) {
			errors.rejectValue("parameters", "error.illegal_arguments.invalid_values");
		} else {
			Deque<String> parameterTokens = new LinkedList<String>();
			for (String token : parameters.trim().split(" ")) {
				if (!token.isEmpty()) {
					parameterTokens.add(token);
				}
			}
			if (!removeAcceptOptions(parameterTokens, acceptOptions, RECURSIVE_LIMIT)) {
				errors.rejectValue("parameters", "error.illegal_arguments.invalid_values");
			}
		}
	}

	/**
	 * BLAST入力データ の parameters値 の先頭が受け付け可能なオプションの場合は、
	 * それを除去して残りの部分を返します。再帰的に処理します。
	 * 先頭が受け付け可能なオプションの場合以外は null を返します。
	 *
	 * @param parameters parameters値を空白で分割した配列
	 * @param recursiveCounter 再帰回数の上限に達した場合は処理を中止
	 * @return 先頭が受け付け可能なオプションの場合以外は false
	 */
	private static boolean removeAcceptOptions(Deque<String> parameters, String acceptOptions, int recursiveCounter) {
		if ((--recursiveCounter)<0) {
			return false;
		} else if (parameters.size()<1) {
			return true;
		} else if (parameters.getFirst().isEmpty()) {
			; // Note: 空文字列が除去されずに残っていたら、単に無視する。
		} else if (parameters.getFirst().length()!=2) {
			// Note: "-A" のように 2文字 のはず。
			return false;
		} else if ('-'!=parameters.getFirst().charAt(0)) {
			// Note: "-A" のように '-' で始まるはず。
			return false;
		} else if (acceptOptions.indexOf(parameters.getFirst().charAt(1))<0) {
			return false;
		}
		parameters.removeFirst();
		if (!removeOptionValues(parameters, recursiveCounter)) {
			return false;
		}
		return removeAcceptOptions(parameters, acceptOptions, recursiveCounter);
	}

	private static boolean removeOptionValues(Deque<String> parameters, int recursiveCounter) {
		if ((--recursiveCounter)<0) {
			return false;
		} else if (parameters.size()<1) {
			return true;
		}
		String firstToken = parameters.getFirst();
		if (firstToken.isEmpty()) {
			parameters.removeFirst();
		} else if (patternParameters_each.matcher(firstToken).matches()) {
			parameters.removeFirst();
		} else {
			return true;
		}
		return removeOptionValues(parameters, recursiveCounter);
	}


	static void validateResult(String result, Errors errors) {
		if (null==result || result.isEmpty()) {
			errors.rejectValue("result", "error.illegal_arguments.required");
		} else if (!patternResult.matcher(result).matches()) {
			errors.rejectValue("result", "error.illegal_arguments.unknown_values");
		}
	}


	/*
	 * address値 を検証して、不備があった場合は errors に追加します。
	 * result値 が "mail" だった場合は必須です。
	 *
	 * @param address address値
	 * @param result result値
	 * @param errors 検証で不備を見つけた場合に追加します。
	 */
	static void validateAddress(String address, String result, Errors errors) {
		if (null==address || address.isEmpty()) {
			if ("mail".equals(result)) {
				errors.rejectValue("address", "error.illegal_arguments.required");
			}
		} else {
			Matcher matcher = patternAddress.matcher(address);
			if (!matcher.matches()) {
				errors.rejectValue("address", "error.illegal_arguments.invalid_values");
			} else {
				String localpart = matcher.group(1);
				String domainpart = matcher.group(2);
				if (!patternAddress_localpart.matcher(localpart).matches()) {
					errors.rejectValue("address", "error.illegal_arguments.invalid_values");
				} else if (!patternAddress_domainpart.matcher(domainpart).matches()) {
					errors.rejectValue("address", "error.illegal_arguments.invalid_values");
				}
			}
		}
	}


	static void validateRequestId(String requestId, Errors errors) {
		if (null==requestId || requestId.isEmpty()) {
			errors.rejectValue("requestId", "error.illegal_arguments.required");
		} else if (0<requestId.indexOf(File.pathSeparator) || 0<requestId.indexOf(File.separator)) {
			// Note: パス区切り文字 ":" 、名前区切り文字 "/" 等は、意図しないディレクトリを参照する恐れがある。
			errors.rejectValue("requestId", "error.illegal_arguments.invalid_values");
		} else if (!patternRequestId.matcher(requestId).matches()) {
			errors.rejectValue("requestId", "error.illegal_arguments.invalid_values");
		} else {
			try {
				JobInfo jobInfo = new JobInfo(requestId);
				if (!jobInfo.existsWorkingDir()) {
					// Note: requestId 発行時に workingDir も作成済みだが、一定日数超過後は削除される。
					errors.rejectValue("requestId", "error.illegal_arguments.not_found");
				}
			} catch (IOException e) {
				errors.rejectValue("requestId", "error.illegal_arguments.not_found");
			}
		}
	}


	static void validateImageId(String imageId, Errors errors) {
		if (null==imageId || imageId.isEmpty()) {
			return;
		} else if (0<imageId.indexOf(File.pathSeparator) || 0<imageId.indexOf(File.separator)) {
			// Note: パス区切り文字 ":" 、名前区切り文字 "/" 等は、意図しないディレクトリを参照する恐れがある。
			errors.rejectValue("imageId", "error.illegal_arguments.invalid_values");
		} else if (!patternImageId.matcher(imageId).matches()) {
			errors.rejectValue("imageId", "error.illegal_arguments.invalid_values");
		}
	}


	static void validateInfo(String info, boolean isRequired, Errors errors) {
		if (null==info || info.isEmpty()) {
			if (isRequired) {
				errors.rejectValue("info", "error.illegal_arguments.required");
			} else {
				; // Note: imageId値 が指定されている場合には、 info値 は必須ではない。
			}
		} else if (!patternInfo.matcher(info).matches()) {
			errors.rejectValue("info", "error.illegal_arguments.unknown_values");
		}
	}


	static void validateInfoEnv(String info, Errors errors) {
		if (null==info || info.isEmpty()) {
			errors.rejectValue("info", "error.illegal_arguments.required");
		} else if (!patternInfoEnv.matcher(info).matches()) {
			errors.rejectValue("info", "error.illegal_arguments.unknown_values");
		}
	}
}
