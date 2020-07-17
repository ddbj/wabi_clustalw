package jp.ac.nig.ddbj.wabi.validator.mafft;

import java.io.File;
import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.regex.Pattern;

import jp.ac.nig.ddbj.wabi.job.mafft.MafftJobInfo;
import jp.ac.nig.ddbj.wabi.util.ConfMafft;
import jp.ac.nig.ddbj.wabi.validator.WabiRequestValidationUtil;

import org.springframework.validation.Errors;

/**
 * リクエストされた BLAST入力データ の妥当性を検証します.
 */
public class MafftRequestValidationUtil extends WabiRequestValidationUtil {
	/*
	 * Note: patternParameters等 は、入力値が
	 * セキュリティ上の問題を起こす可能性があるか判定するための
	 * ホワイトリストです。
	 * 問題を起こし得る入力値の例として、
	 * 「foo; bar | baz」のような「;」、「|」等の文字を含むものの他に、
	 * 「%」や「\」などの HTTP上 のエンコーディングに関係する文字などが
	 * 考えられます。
	 */
	protected static Pattern patternParameters = Pattern.compile(ConfMafft.RequestValidationPattern.parameters);
	protected static Pattern patternParameters_each = Pattern.compile(ConfMafft.RequestValidationPattern.parameters_eachParameter);

	/**
	 * parameters値 を検証して、不備があった場合は errors に追加します。
	 *
	 * @param parameters parameters値
	 * @param acceptOptions MAFFT入力データ の parameters値 として受け付け可能なオプションです。
	 *     (例: blastall の場合は "ABCDEFGIJKLMPQSTUVWXYZabdefglmnqrstvwyz" 等。)
	 * @param errors 検証で不備を見つけた場合に追加します。
	 */
	static void validateParameters(String parameters, String acceptOptions, Errors errors) {
		if (null==parameters || parameters.isEmpty()) {
			return;
		} else if (!patternParameters.matcher(parameters).matches()) {
			errors.rejectValue("parameters", "error.illegal_arguments");
		} else {
			Deque<String> parameterTokens = new LinkedList<String>();
			for (String token : parameters.trim().split(" ")) {
				if (!token.isEmpty()) {
					parameterTokens.add(token);
				}
			}
			if (!removeAcceptOptions(parameterTokens, acceptOptions, RECURSIVE_LIMIT)) {
				errors.rejectValue("parameters", "error.illegal_arguments");
			}
		}
	}

	static void validateProfile(String profile, Errors errors) {
		/*
		 * Note: 実装なし。
		 * この profile値 はファイルに保存されて、
		 * そのファイル名を CLUSTALWコマンド に与えているだけなので、
		 * OSコマンド・インジェクションのようなセキュリティ問題には
		 * 関係してこない。
		 * 値に不備がある場合は CLUSTALW側 でエラーにする。
		 */
	}

	static void validateAaMatrix(String aaMatrix, Errors errors) {
		/*
		 * Note: 実装なし。
		 * この aaMatrix値 はファイルに保存されて、
		 * そのファイル名を CLUSTALWコマンド に与えているだけなので、
		 * OSコマンド・インジェクションのようなセキュリティ問題には
		 * 関係してこない。
		 * 値に不備がある場合は CLUSTALW側 でエラーにする。
		 */
	}

	static void validateAddSequence(String addSequence, Errors errors) {
		/*
		 * Note: 実装なし。
		 * この aaMatrix値 はファイルに保存されて、
		 * そのファイル名を CLUSTALWコマンド に与えているだけなので、
		 * OSコマンド・インジェクションのようなセキュリティ問題には
		 * 関係してこない。
		 * 値に不備がある場合は CLUSTALW側 でエラーにする。
		 */
	}

	static void validateAddfragmentsSequence(String addfragmentsSequence, Errors errors) {
		/*
		 * Note: 実装なし。
		 * この aaMatrix値 はファイルに保存されて、
		 * そのファイル名を CLUSTALWコマンド に与えているだけなので、
		 * OSコマンド・インジェクションのようなセキュリティ問題には
		 * 関係してこない。
		 * 値に不備がある場合は CLUSTALW側 でエラーにする。
		 */
	}

	static void validateAddprofileProfile(String addprofileProfile, Errors errors) {
		/*
		 * Note: 実装なし。
		 * この aaMatrix値 はファイルに保存されて、
		 * そのファイル名を CLUSTALWコマンド に与えているだけなので、
		 * OSコマンド・インジェクションのようなセキュリティ問題には
		 * 関係してこない。
		 * 値に不備がある場合は CLUSTALW側 でエラーにする。
		 */
	}

	static void validateAddfullSequence(String addfullSequence, Errors errors) {
		/*
		 * Note: 実装なし。
		 * この aaMatrix値 はファイルに保存されて、
		 * そのファイル名を CLUSTALWコマンド に与えているだけなので、
		 * OSコマンド・インジェクションのようなセキュリティ問題には
		 * 関係してこない。
		 * 値に不備がある場合は CLUSTALW側 でエラーにする。
		 */
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
	 * MAFFT入力データ の parameters値 の先頭が受け付け可能なオプションの場合は、
	 * それを除去して残りの部分を返します。再帰的に処理します。
	 * 先頭が受け付け可能なオプションの場合以外は null を返します。
	 * オプションの種類（verbs, general settings, fast pairwise alignments, 
	 * slow pairwise alignments, multiple-alignments, trees）の組み合わせが
	 * 正しいかどうかのチェックはしていません。
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
		} else if (!('-' == parameters.getFirst().charAt(0) && '-' == parameters.getFirst().charAt(1))) {
			// Note: "--auto" のように '--' で始まるはず。
			return false;
		} else if (acceptOptions.indexOf(parameters.getFirst().charAt(2))<0) {
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
				MafftJobInfo jobInfo = new MafftJobInfo(requestId);
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
