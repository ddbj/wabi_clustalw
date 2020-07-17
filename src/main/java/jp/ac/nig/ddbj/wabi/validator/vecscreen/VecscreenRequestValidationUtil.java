package jp.ac.nig.ddbj.wabi.validator.vecscreen;

import java.io.File;
import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.regex.Pattern;

import jp.ac.nig.ddbj.wabi.job.vecscreen.VecscreenJobInfo;
import jp.ac.nig.ddbj.wabi.util.ConfVecscreen;
import jp.ac.nig.ddbj.wabi.validator.WabiRequestValidationUtil;

import org.springframework.validation.Errors;

/**
 * リクエストされた BLAST入力データ の妥当性を検証します.
 */
public class VecscreenRequestValidationUtil extends WabiRequestValidationUtil {
	/*
	 * Note: patternParameters等 は、入力値が
	 * セキュリティ上の問題を起こす可能性があるか判定するための
	 * ホワイトリストです。
	 * 問題を起こし得る入力値の例として、
	 * 「foo; bar | baz」のような「;」、「|」等の文字を含むものの他に、
	 * 「%」や「\」などの HTTP上 のエンコーディングに関係する文字などが
	 * 考えられます。
	 */
	protected static Pattern patternDatabase = Pattern.compile(ConfVecscreen.RequestValidationPattern.database);
	protected static Pattern patternParameters = Pattern.compile(ConfVecscreen.RequestValidationPattern.parameters);
	protected static Pattern patternParameters_each = Pattern.compile(ConfVecscreen.RequestValidationPattern.parameters_eachParameter);

	static void validateDatabase(String database, Errors errors) {
		if (null==database || database.isEmpty()) {
			errors.rejectValue("database", "error.illegal_arguments.required");
		} else if (!patternDatabase.matcher(database).matches()) {
			errors.rejectValue("database", "error.illegal_arguments.invalid_values");
		}
	}

	/**
	 * parameters値 を検証して、不備があった場合は errors に追加します。
	 *
	 * @param parameters parameters値
	 * @param acceptOptions Vecscreen入力データ の parameters値 として受け付け可能なオプションです。
	 *     -f [0-3]のみ。
	 * @param errors 検証で不備を見つけた場合に追加します。
	 */
	static void validateParameters(String parameters, String acceptOptions, Errors errors) {
		if (null==parameters || parameters.isEmpty()) {
			return;
		} else if (!patternParameters.matcher(parameters).matches()) {
			errors.rejectValue("parameters", "error.illegal_arguments1" + " " + patternParameters);
		} else {
			Deque<String> parameterTokens = new LinkedList<String>();
			for (String token : parameters.trim().split(" ")) {
				if (!token.isEmpty()) {
					parameterTokens.add(token);
				}
			}
			if (!removeAcceptOptions(parameterTokens, acceptOptions, RECURSIVE_LIMIT)) {
				errors.rejectValue("parameters", "error.illegal_arguments2");
			}
		}
	}

	/**
	 * validateParameters(String parameters, String acceptOptions, Errors errors)のErrorsを使わないバージョン
	 * @param parameters
	 * @param acceptOptions
	 */
	static String validateParameters(String parameters, String acceptOptions) {
		if (null==parameters || parameters.isEmpty()) {
			return "no error";
		} else if (!patternParameters.matcher(parameters).matches()) {
			return "parameters check error: " + parameters + " : " + patternParameters;
		} else {
			Deque<String> parameterTokens = new LinkedList<String>();
			for (String token : parameters.trim().split(" ")) {
				if (!token.isEmpty()) {
					parameterTokens.add(token);
				}
			}
			if (!removeAcceptOptions(parameterTokens, acceptOptions, RECURSIVE_LIMIT)) {
				return "accept option check error: " + parameters + " : " + acceptOptions;
			}
		}
		return "no error";
	}

	/**
	 * vecscreen入力データ の parameters値 の先頭が受け付け可能なオプションの場合は、
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

	static void validateRequestId(String requestId, Errors errors) {
		if (null==requestId || requestId.isEmpty()) {
			errors.rejectValue("requestId", "error.illegal_arguments");
		} else if (0<requestId.indexOf(File.pathSeparator) || 0<requestId.indexOf(File.separator)) {
			// Note: パス区切り文字 ":" 、名前区切り文字 "/" 等は、意図しないディレクトリを参照する恐れがある。
			errors.rejectValue("requestId", "error.illegal_arguments");
		} else if (!patternRequestId.matcher(requestId).matches()) {
			errors.rejectValue("requestId", "error.illegal_arguments");
		} else {
			try {
				VecscreenJobInfo jobInfo = new VecscreenJobInfo(requestId);
				if (!jobInfo.existsWorkingDir()) {
					// Note: requestId 発行時に workingDir も作成済みだが、一定日数超過後は削除される。
					errors.rejectValue("requestId", "error.not_found");
				}
			} catch (IOException e) {
				errors.rejectValue("requestId", "error.illegal_arguments");
			}
		}
	}

}
