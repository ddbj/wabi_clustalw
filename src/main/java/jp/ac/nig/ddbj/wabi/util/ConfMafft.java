package jp.ac.nig.ddbj.wabi.util;

import java.util.ResourceBundle;

/**
 * CLUSTALW 関連の設定です。
 */
public class ConfMafft {
	private static ResourceBundle bundle = ResourceBundle.getBundle("env_mafft");

	/**
	 * Tリクエスト の入力値検証で、各値が満たすべ正規表現パターンです。
	 */
	public static class RequestValidationPattern {

		/** format値 */
		public static final String format = bundle.getString("Conf.Mafft.RequestValidatorPattern.format");

		/** result値 */
		public static final String result = bundle.getString("Conf.Mafft.RequestValidatorPattern.result");

		/** address値 が満たすべき正規表現パターンです。 */
		public static final String address = bundle.getString("Conf.Mafft.RequestValidatorPattern.address");
		/** address値 のローカル部が満たすべき正規表現パターンです。 */
		public static final String address_localpart = bundle.getString("Conf.Mafft.RequestValidatorPattern.address.localpart");
		/** address値 のドメイン部が満たすべき正規表現パターンです。 */
		public static final String address_domainpart = bundle.getString("Conf.Mafft.RequestValidatorPattern.address.domainpart");

		/** requestId値 */
		public static final String requestId = bundle.getString("Conf.Mafft.RequestValidatorPattern.requestId");

		/** info値 */
		public static final String info = bundle.getString("Conf.Mafft.RequestValidatorPattern.info");

		/** getenvリクエスト時 の info値 */
		public static final String infoEnv = bundle.getString("Conf.Mafft.RequestValidatorPattern.infoEnv");
		/** parameters値 が満たすべき正規表現パターンです。 */
		public static final String parameters = bundle.getString("Conf.Mafft.RequestValidatorPattern.parameters");
		/** parameters値 に指定可能なオプションの正規表現パターンです。 */
		public static final String parameters_eachParameter = bundle.getString("Conf.Mafft.RequestValidatorPattern.parameters.eachParameter");

		public static final String parameters_AcceptOptions_mafft_algorithm = bundle.getString("Conf.Mafft.RequestValidatorPattern.parameters.AcceptOptions.mafft.algorithm");
		public static final String parameters_AcceptOptions_mafft_parameter = bundle.getString("Conf.Mafft.RequestValidatorPattern.parameters.AcceptOptions.mafft.parameter");
		public static final String parameters_AcceptOptions_mafft_output = bundle.getString("Conf.Mafft.RequestValidatorPattern.parameters.AcceptOptions.mafft.output");
		public static final String parameters_AcceptOptions_mafft_input = bundle.getString("Conf.Mafft.RequestValidatorPattern.parameters.AcceptOptions.mafft.input");
	}

	/** リクエスト で GET getenv の実行を許可されている 接続元IPアドレス が満たすべき正規表現パターンです。 */
	public static final String patternGetenvPermittedRemoteAddr = bundle.getString("Conf.Mafft.patternGetenvPermittedRemoteAddr");

	/** リクエスト で GET result_stdout, result_stderr の実行を許可されている 接続元IPアドレス が満たすべき正規表現パターンです。 */
	public static final String patternGetResultOfQsubPermittedRemoteAddr = bundle.getString("Conf.Mafft.patternGetResultOfQsubPermittedRemoteAddr");

	/** リクエスト で GET status で system-info の出力を許可されている 接続元IPアドレス が満たすべき正規表現パターンです。 */
	public static final String patternGetStatusOfQsubPermittedRemoteAddr = bundle.getString("Conf.Mafft.patternGetStatusOfQsubPermittedRemoteAddr");

	/** 脆弱性診断ページの GET を許可されている 接続元IPアドレス が満たすべき正規表現パターンです。 */
	public static final String patternTestSecurityApplicationScanPagePermittedRemoteAddr = bundle.getString("Conf.Mafft.patternTestSecurityApplicationScanPagePermittedRemoteAddr");
}
