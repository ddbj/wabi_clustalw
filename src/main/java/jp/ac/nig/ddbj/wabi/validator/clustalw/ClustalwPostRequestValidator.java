package jp.ac.nig.ddbj.wabi.validator.clustalw;

import org.springframework.validation.Errors;

import jp.ac.nig.ddbj.wabi.request.WabiRequest;
import jp.ac.nig.ddbj.wabi.request.clustalw.ClustalwRequest;
import jp.ac.nig.ddbj.wabi.util.ConfClustalw;
import jp.ac.nig.ddbj.wabi.validator.WabiPostRequestValidator;
import jp.ac.nig.ddbj.wabi.validator.WabiRequestValidationUtil;

/**
 * POST リクエストされた CLUSTALW入力データ の妥当性を検証します.
 */
public class ClustalwPostRequestValidator extends WabiPostRequestValidator {

	/**
	 * POST "/clustalw" リクエストの入力値を検証します。
	 */
	@Override
	public void validate(Object target, Errors errors) {
		super.validate(target, errors);
		ClustalwRequest request2 = (ClustalwRequest) request;

		//パラメータのチェックを追加
		ClustalwRequestValidationUtil.validateParameters(request2.getParameters(), getAcceptOptions(request2), errors);

		ClustalwRequestValidationUtil.validateProfile(request2.getProfile1(), errors);
		ClustalwRequestValidationUtil.validateProfile(request2.getProfile2(), errors);
		ClustalwRequestValidationUtil.validateGuidetree(request2.getGuidetree1(), errors);
		ClustalwRequestValidationUtil.validateGuidetree(request2.getGuidetree2(), errors);
		ClustalwRequestValidationUtil.validateAaMatrix(request2.getPwAaMatrix(), errors);
		ClustalwRequestValidationUtil.validateAaMatrix(request2.getAaMatrix(), errors);
		ClustalwRequestValidationUtil.validateDnaMatrix(request2.getPwDnaMatrix(), errors);
		ClustalwRequestValidationUtil.validateDnaMatrix(request2.getDnaMatrix(), errors);
	}

	/**
	 * CLUSTALW入力データ の parameters値 として受け付け可能なオプションです。
	 * 例: blastall の場合は "ABCDEFGIJKLMPQSTUVWXYZabdefglmnqrstvwyz" 等。
	 */
	@Override
	protected String getAcceptOptions(WabiRequest request) {
		String a_o_c_verbs = ConfClustalw.RequestValidationPattern.parameters_AcceptOptions_clustalw_verbs;
		String a_o_c_general = ConfClustalw.RequestValidationPattern.parameters_AcceptOptions_clustalw_general;
		String a_o_c_fast = ConfClustalw.RequestValidationPattern.parameters_AcceptOptions_clustalw_fast;
		String a_o_c_slow = ConfClustalw.RequestValidationPattern.parameters_AcceptOptions_clustalw_slow;
		String a_o_c_multiple = ConfClustalw.RequestValidationPattern.parameters_AcceptOptions_clustalw_multiple;
		String a_o_c_profile = ConfClustalw.RequestValidationPattern.parameters_AcceptOptions_clustalw_profile;
		String a_o_c_sequence = ConfClustalw.RequestValidationPattern.parameters_AcceptOptions_clustalw_sequence;
		String a_o_c_structure = ConfClustalw.RequestValidationPattern.parameters_AcceptOptions_clustalw_structure;
		String a_o_c_tree = ConfClustalw.RequestValidationPattern.parameters_AcceptOptions_clustalw_tree;
		String acceptOptionsClustalw = a_o_c_verbs + "|" + a_o_c_general + "|" + a_o_c_fast + "|" + a_o_c_slow + "|" + a_o_c_multiple + "|" + a_o_c_profile + "|" + a_o_c_sequence + "|" + a_o_c_structure + "|" + a_o_c_tree;
		return acceptOptionsClustalw;
	}

}
