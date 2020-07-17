package jp.ac.nig.ddbj.wabi.util;

import java.util.ResourceBundle;

/**
 * BLAST 関連の設定です。
 */
public class ConfBlast {
	private static ResourceBundle bundle = ResourceBundle.getBundle("env_blast");

	/**
	 * BLASTリクエスト の入力値検証で、各値が満たすべ正規表現パターンです。
	 */
	public static class RequestValidationPattern {
		/** datasets値 */
		public static final String datasets = bundle.getString("Conf.Blast.RequestValidatorPattern.datasets");

		/** database値 */
		public static final String database = bundle.getString("Conf.Blast.RequestValidatorPattern.database");

		/** program値 */
		public static final String program = bundle.getString("Conf.Blast.RequestValidatorPattern.program");

		/** format値 */
		public static final String format = bundle.getString("Conf.Blast.RequestValidatorPattern.format");

		/** parameters値 が満たすべき正規表現パターンです。 */
		public static final String parameters = bundle.getString("Conf.Blast.RequestValidatorPattern.parameters");
		/** parameters値 に指定可能なオプションの正規表現パターンです。 */
		public static final String parameters_eachParameter = bundle.getString("Conf.Blast.RequestValidatorPattern.parameters.eachParameter");
		/** parameters値 に指定可能なオプションです (megablast の場合) 。 */
		public static final String parameters_AcceptOptions_megablast = bundle.getString("Conf.Blast.RequestValidatorPattern.parameters.AcceptOptions.megablast");
		/** parameters値 に指定可能なオプションです (blastall の場合) 。 */
		public static final String parameters_AcceptOptions_blastall = bundle.getString("Conf.Blast.RequestValidatorPattern.parameters.AcceptOptions.blastall");

		/** result値 */
		public static final String result = bundle.getString("Conf.Blast.RequestValidatorPattern.result");

		/** address値 が満たすべき正規表現パターンです。 */
		public static final String address = bundle.getString("Conf.Blast.RequestValidatorPattern.address");
		/** address値 のローカル部が満たすべき正規表現パターンです。 */
		public static final String address_localpart = bundle.getString("Conf.Blast.RequestValidatorPattern.address.localpart");
		/** address値 のドメイン部が満たすべき正規表現パターンです。 */
		public static final String address_domainpart = bundle.getString("Conf.Blast.RequestValidatorPattern.address.domainpart");

		/** requestId値 */
		public static final String requestId = bundle.getString("Conf.Blast.RequestValidatorPattern.requestId");

		/** imageId値 */
		public static final String imageId = bundle.getString("Conf.Blast.RequestValidatorPattern.imageId");

		/** info値 */
		public static final String info = bundle.getString("Conf.Blast.RequestValidatorPattern.info");

		/** getenvリクエスト時 の info値 */
		public static final String infoEnv = bundle.getString("Conf.Blast.RequestValidatorPattern.infoEnv");
	}

	/** BLASTリクエスト で GET getenv の実行を許可されている 接続元IPアドレス が満たすべき正規表現パターンです。 */
	public static final String patternBlastGetenvPermittedRemoteAddr = bundle.getString("Conf.Blast.patternBlastGetenvPermittedRemoteAddr");

	/** BLASTリクエスト で GET result_stdout, result_stderr の実行を許可されている 接続元IPアドレス が満たすべき正規表現パターンです。 */
	public static final String patternBlastGetResultOfQsubPermittedRemoteAddr = bundle.getString("Conf.Blast.patternBlastGetResultOfQsubPermittedRemoteAddr");

	/** BLASTリクエスト で GET status で system-info の出力を許可されている 接続元IPアドレス が満たすべき正規表現パターンです。 */
	public static final String patternBlastGetStatusOfQsubPermittedRemoteAddr = bundle.getString("Conf.Blast.patternBlastGetStatusOfQsubPermittedRemoteAddr");

	/** 脆弱性診断ページの GET を許可されている 接続元IPアドレス が満たすべき正規表現パターンです。 */
	public static final String patternBlastTestSecurityApplicationScanPagePermittedRemoteAddr = bundle.getString("Conf.Blast.patternBlastTestSecurityApplicationScanPagePermittedRemoteAddr");

	/**
	 * BLAST の Help情報 に関する設定です。
	 */
	public static class Help {
		/** Helpアクションが返す database値 一覧です。 */
		public static final String listDatabase = bundle.getString("Conf.Blast.Help.list.database");
	}
}
