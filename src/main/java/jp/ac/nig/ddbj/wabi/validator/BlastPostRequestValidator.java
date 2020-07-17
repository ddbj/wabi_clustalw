package jp.ac.nig.ddbj.wabi.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import jp.ac.nig.ddbj.wabi.request.BlastRequest;
import jp.ac.nig.ddbj.wabi.util.ConfBlast;

/**
 * POST リクエストされた BLAST入力データ の妥当性を検証します.
 */
public class BlastPostRequestValidator implements Validator {
	public boolean supports(Class clazz) {
		return BlastRequest.class.isAssignableFrom(clazz);
	}

	/**
	 * POST "/blast" リクエストの入力値を検証します。
	 */
	public void validate(Object target, Errors errors) {
		if (!(target instanceof BlastRequest)) {
			return;
		}
		BlastRequest request = (BlastRequest)target;
		BlastRequestValidationUtil.validateQuerySequence(request.getQuerySequence(), errors);
		BlastRequestValidationUtil.validateDatasets(request.getDatasets(), errors);
		BlastRequestValidationUtil.validateDatabase(request.getDatabase(), errors);
		BlastRequestValidationUtil.validateProgram(request.getProgram(), errors);
		BlastRequestValidationUtil.validateFormat(request.getFormat(), errors);
		BlastRequestValidationUtil.validateParameters(request.getParameters(), getAcceptOptions(request), errors);
		BlastRequestValidationUtil.validateResult(request.getResult(), errors);
		BlastRequestValidationUtil.validateAddress(request.getAddress(), request.getResult(), errors);
	}

	/**
	 * BLAST入力データ の parameters値 として受け付け可能なオプションです。
	 * 例: blastall の場合は "ABCDEFGIJKLMPQSTUVWXYZabdefglmnqrstvwyz" 等。
	 */
	private String getAcceptOptions(BlastRequest request) {
		if ("megablast".equals(request.getProgram())) {
			return ConfBlast.RequestValidationPattern.parameters_AcceptOptions_megablast;
		} else {
			// Note: blastall の場合 (例: "blastn" など。)
			return ConfBlast.RequestValidationPattern.parameters_AcceptOptions_blastall;
		}
	}
}
