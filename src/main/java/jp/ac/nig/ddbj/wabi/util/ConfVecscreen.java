package jp.ac.nig.ddbj.wabi.util;

import java.util.ResourceBundle;

/**
 * CLUSTALW 関連の設定です。
 */
public class ConfVecscreen {
	private static ResourceBundle bundle = ResourceBundle.getBundle("env_vecscreen");

	/**
	 * Vecscreenリクエスト の入力値検証で、各値が満たすべ正規表現パターンです。
	 */
	public static class RequestValidationPattern {

		/** database値 */
		public static final String database = bundle.getString("Conf.Vecscreen.RequestValidatorPattern.database");

		/** format値 */
		public static final String format = bundle.getString("Conf.Vecscreen.RequestValidatorPattern.format");

		/** result値 */
		public static final String result = bundle.getString("Conf.Vecscreen.RequestValidatorPattern.result");

		/** address値 が満たすべき正規表現パターンです。 */
		public static final String address = bundle.getString("Conf.Vecscreen.RequestValidatorPattern.address");
		/** address値 のローカル部が満たすべき正規表現パターンです。 */
		public static final String address_localpart = bundle.getString("Conf.Vecscreen.RequestValidatorPattern.address.localpart");
		/** address値 のドメイン部が満たすべき正規表現パターンです。 */
		public static final String address_domainpart = bundle.getString("Conf.Vecscreen.RequestValidatorPattern.address.domainpart");

		/** requestId値 */
		public static final String requestId = bundle.getString("Conf.Vecscreen.RequestValidatorPattern.requestId");

		/** info値 */
		public static final String info = bundle.getString("Conf.Vecscreen.RequestValidatorPattern.info");

		/** getenvリクエスト時 の info値 */
		public static final String infoEnv = bundle.getString("Conf.Vecscreen.RequestValidatorPattern.infoEnv");
		/** parameters値 が満たすべき正規表現パターンです。 */
		public static final String parameters = bundle.getString("Conf.Vecscreen.RequestValidatorPattern.parameters");
		/** parameters値 に指定可能なオプションの正規表現パターンです。 */
		public static final String parameters_eachParameter = bundle.getString("Conf.Vecscreen.RequestValidatorPattern.parameters.eachParameter");

		public static final String parameters_AcceptOptions_vecscreen = bundle.getString("Conf.Vecscreen.RequestValidatorPattern.parameters.AcceptOptions.vecscreen");
	}

	/** リクエスト で GET getenv の実行を許可されている 接続元IPアドレス が満たすべき正規表現パターンです。 */
	public static final String patternGetenvPermittedRemoteAddr = bundle.getString("Conf.Vecscreen.patternGetenvPermittedRemoteAddr");

	/** リクエスト で GET result_stdout, result_stderr の実行を許可されている 接続元IPアドレス が満たすべき正規表現パターンです。 */
	public static final String patternGetResultOfQsubPermittedRemoteAddr = bundle.getString("Conf.Vecscreen.patternGetResultOfQsubPermittedRemoteAddr");

	/** リクエスト で GET status で system-info の出力を許可されている 接続元IPアドレス が満たすべき正規表現パターンです。 */
	public static final String patternGetStatusOfQsubPermittedRemoteAddr = bundle.getString("Conf.Vecscreen.patternGetStatusOfQsubPermittedRemoteAddr");

	/** 脆弱性診断ページの GET を許可されている 接続元IPアドレス が満たすべき正規表現パターンです。 */
	public static final String patternTestSecurityApplicationScanPagePermittedRemoteAddr = bundle.getString("Conf.Vecscreen.patternTestSecurityApplicationScanPagePermittedRemoteAddr");

	/**
	 * Help情報 に関する設定です。
	 */
	public static class Help {
		/** Helpアクションが返す database値 一覧です。 */
		public static final String listDatabase = bundle.getString("Conf.Vecscreen.Help.list.database");
	}
}
