package jp.ac.nig.ddbj.wabi.util;

import java.util.ResourceBundle;

/**
 * CLUSTALW 関連の設定です。
 */
public class ConfClustalw {
	private static ResourceBundle bundle = ResourceBundle.getBundle("env_clustalw");

	/**
	 * BLASTリクエスト の入力値検証で、各値が満たすべ正規表現パターンです。
	 */
	public static class RequestValidationPattern {

		/** format値 */
		public static final String format = bundle.getString("Conf.Clustalw.RequestValidatorPattern.format");

		/** result値 */
		public static final String result = bundle.getString("Conf.Clustalw.RequestValidatorPattern.result");

		/** address値 が満たすべき正規表現パターンです。 */
		public static final String address = bundle.getString("Conf.Clustalw.RequestValidatorPattern.address");
		/** address値 のローカル部が満たすべき正規表現パターンです。 */
		public static final String address_localpart = bundle.getString("Conf.Clustalw.RequestValidatorPattern.address.localpart");
		/** address値 のドメイン部が満たすべき正規表現パターンです。 */
		public static final String address_domainpart = bundle.getString("Conf.Clustalw.RequestValidatorPattern.address.domainpart");

		/** requestId値 */
		public static final String requestId = bundle.getString("Conf.Clustalw.RequestValidatorPattern.requestId");

		/** info値 */
		public static final String info = bundle.getString("Conf.Clustalw.RequestValidatorPattern.info");

		/** getenvリクエスト時 の info値 */
		public static final String infoEnv = bundle.getString("Conf.Clustalw.RequestValidatorPattern.infoEnv");
		/** parameters値 が満たすべき正規表現パターンです。 */
		public static final String parameters = bundle.getString("Conf.Clustalw.RequestValidatorPattern.parameters");
		/** parameters値 に指定可能なオプションの正規表現パターンです。 */
		public static final String parameters_eachParameter = bundle.getString("Conf.Clustalw.RequestValidatorPattern.parameters.eachParameter");

		public static final String parameters_AcceptOptions_clustalw_verbs = bundle.getString("Conf.Clustalw.RequestValidatorPattern.parameters.AcceptOptions.clustalw.verbs");
		public static final String parameters_AcceptOptions_clustalw_general = bundle.getString("Conf.Clustalw.RequestValidatorPattern.parameters.AcceptOptions.clustalw.general");
		public static final String parameters_AcceptOptions_clustalw_fast = bundle.getString("Conf.Clustalw.RequestValidatorPattern.parameters.AcceptOptions.clustalw.fast");
		public static final String parameters_AcceptOptions_clustalw_slow = bundle.getString("Conf.Clustalw.RequestValidatorPattern.parameters.AcceptOptions.clustalw.slow");
		public static final String parameters_AcceptOptions_clustalw_multiple = bundle.getString("Conf.Clustalw.RequestValidatorPattern.parameters.AcceptOptions.clustalw.multiple");
		public static final String parameters_AcceptOptions_clustalw_profile = bundle.getString("Conf.Clustalw.RequestValidatorPattern.parameters.AcceptOptions.clustalw.profile");
		public static final String parameters_AcceptOptions_clustalw_sequence = bundle.getString("Conf.Clustalw.RequestValidatorPattern.parameters.AcceptOptions.clustalw.sequence");
		public static final String parameters_AcceptOptions_clustalw_structure = bundle.getString("Conf.Clustalw.RequestValidatorPattern.parameters.AcceptOptions.clustalw.structure");
		public static final String parameters_AcceptOptions_clustalw_tree = bundle.getString("Conf.Clustalw.RequestValidatorPattern.parameters.AcceptOptions.clustalw.tree");
	}

	/** リクエスト で GET getenv の実行を許可されている 接続元IPアドレス が満たすべき正規表現パターンです。 */
	public static final String patternGetenvPermittedRemoteAddr = bundle.getString("Conf.Clustalw.patternGetenvPermittedRemoteAddr");
//	public static final String patternGetenvPermittedRemoteAddr = ".*";

	/** リクエスト で GET result_stdout, result_stderr の実行を許可されている 接続元IPアドレス が満たすべき正規表現パターンです。 */
	public static final String patternGetResultOfQsubPermittedRemoteAddr = bundle.getString("Conf.Clustalw.patternGetResultOfQsubPermittedRemoteAddr");
//	public static final String patternGetResultOfQsubPermittedRemoteAddr = ".*";

	/** リクエスト で GET status で system-info の出力を許可されている 接続元IPアドレス が満たすべき正規表現パターンです。 */
	public static final String patternGetStatusOfQsubPermittedRemoteAddr = bundle.getString("Conf.Clustalw.patternGetStatusOfQsubPermittedRemoteAddr");
//	public static final String patternGetStatusOfQsubPermittedRemoteAddr = ".*";

	/** 脆弱性診断ページの GET を許可されている 接続元IPアドレス が満たすべき正規表現パターンです。 */
	public static final String patternTestSecurityApplicationScanPagePermittedRemoteAddr = bundle.getString("Conf.Clustalw.patternTestSecurityApplicationScanPagePermittedRemoteAddr");

}
