package jp.ac.nig.ddbj.wabi.validator.mafft;

import org.springframework.validation.Errors;

import jp.ac.nig.ddbj.wabi.request.WabiRequest;
import jp.ac.nig.ddbj.wabi.request.mafft.MafftRequest;
import jp.ac.nig.ddbj.wabi.util.ConfMafft;
import jp.ac.nig.ddbj.wabi.validator.WabiPostRequestValidator;

/**
 * POST リクエストされた CLUSTALW入力データ の妥当性を検証します.
 */
public class MafftPostRequestValidator extends WabiPostRequestValidator {

	/**
	 * POST "/clustalw" リクエストの入力値を検証します。
	 */
	@Override
	public void validate(Object target, Errors errors) {
		super.validate(target, errors);
		MafftRequest request2 = (MafftRequest) request;

		//パラメータのチェックを追加
		MafftRequestValidationUtil.validateParameters(request2.getParameters(), getAcceptOptions(request), errors);

		MafftRequestValidationUtil.validateProfile(request2.getProfile1(), errors);
		MafftRequestValidationUtil.validateProfile(request2.getProfile2(), errors);
		MafftRequestValidationUtil.validateAaMatrix(request2.getAaMatrix(), errors);
}

	/**
	 * CLUSTALW入力データ の parameters値 として受け付け可能なオプションです。
	 * 例: blastall の場合は "ABCDEFGIJKLMPQSTUVWXYZabdefglmnqrstvwyz" 等。
	 */
	@Override
	protected String getAcceptOptions(WabiRequest request) {
		String a_o_m_algorithm = ConfMafft.RequestValidationPattern.parameters_AcceptOptions_mafft_algorithm;
		String a_o_m_parameter = ConfMafft.RequestValidationPattern.parameters_AcceptOptions_mafft_parameter;
		String a_o_m_output    = ConfMafft.RequestValidationPattern.parameters_AcceptOptions_mafft_output;
		String a_o_m_input     = ConfMafft.RequestValidationPattern.parameters_AcceptOptions_mafft_input;
		String acceptOptionsMafft = a_o_m_algorithm + "|" + a_o_m_parameter + "|" + a_o_m_input + "|" + a_o_m_output;

		return acceptOptionsMafft;
	}

}
