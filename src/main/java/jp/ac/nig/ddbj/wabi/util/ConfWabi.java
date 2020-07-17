package jp.ac.nig.ddbj.wabi.util;

import java.util.ResourceBundle;

/**
 * Web APIで実行するプログラムで共通する設定です。
 */
public abstract class ConfWabi {
	private static ResourceBundle bundle = ResourceBundle.getBundle("env_wabi");

	/**
	 * リクエストの入力値検証で、各値が満たすべき正規表現パターンです。
	 */
	public static class RequestValidationPattern {

		/** format値 */
		public static final String format = bundle.getString("Conf.Wabi.RequestValidatorPattern.format");

		/** result値 */
		public static final String result = bundle.getString("Conf.Wabi.RequestValidatorPattern.result");

		/** address値 が満たすべき正規表現パターンです。 */
		public static final String address = bundle.getString("Conf.Wabi.RequestValidatorPattern.address");
		/** address値 のローカル部が満たすべき正規表現パターンです。 */
		public static final String address_localpart = bundle.getString("Conf.Wabi.RequestValidatorPattern.address.localpart");
		/** address値 のドメイン部が満たすべき正規表現パターンです。 */
		public static final String address_domainpart = bundle.getString("Conf.Wabi.RequestValidatorPattern.address.domainpart");

		/** requestId値 */
		public static final String requestId = bundle.getString("Conf.Wabi.RequestValidatorPattern.requestId");

		/** info値 */
		public static final String info = bundle.getString("Conf.Wabi.RequestValidatorPattern.info");

		/** getenvリクエスト時 の info値 */
		public static final String infoEnv = bundle.getString("Conf.Wabi.RequestValidatorPattern.infoEnv");
	}

	/** リクエスト で GET getenv の実行を許可されている 接続元IPアドレス が満たすべき正規表現パターンです。 */
	public static final String patternGetenvPermittedRemoteAddr = bundle.getString("Conf.Wabi.patternGetenvPermittedRemoteAddr");
//	public static final String patternGetenvPermittedRemoteAddr = ".*";

	/** リクエスト で GET result_stdout, result_stderr の実行を許可されている 接続元IPアドレス が満たすべき正規表現パターンです。 */
	public static final String patternGetResultOfQsubPermittedRemoteAddr = bundle.getString("Conf.Wabi.patternGetResultOfQsubPermittedRemoteAddr");
//	public static final String patternGetResultOfQsubPermittedRemoteAddr = ".*";
	
	/** リクエスト で GET status で system-info の出力を許可されている 接続元IPアドレス が満たすべき正規表現パターンです。 */
	public static final String patternGetStatusOfQsubPermittedRemoteAddr = bundle.getString("Conf.Wabi.patternGetStatusOfQsubPermittedRemoteAddr");
//	public static final String patternGetStatusOfQsubPermittedRemoteAddr = ".*";
	
	public static final String patternTestSecurityApplicationScanPagePermittedRemoteAddr = bundle.getString("Conf.Wabi.patternTestSecurityApplicationScanPagePermittedRemoteAddr");
	
	/**
	 * BLAST の Help情報 に関する設定です。
	 */
//	public static class Help {
//		/** Helpアクションが返す database値 一覧です。 */
//		public static final String listDatabase = bundle.getString("Conf.Blast.Help.list.database");
//	}
}
